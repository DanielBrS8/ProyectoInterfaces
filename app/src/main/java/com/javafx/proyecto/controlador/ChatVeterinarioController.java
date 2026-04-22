package com.javafx.proyecto.controlador;

import com.javafx.proyecto.bbdd.PawLinkClient;
import com.javafx.proyecto.util.SesionUsuario;
import com.javafx.proyecto.util.UIUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ChatVeterinarioController {

    private static final String HOST = "localhost";
    private static final int PUERTO = 9090;

    @FXML private ListView<Map<String, Object>> listaConversaciones;
    @FXML private ListView<Map<String, Object>> listaMensajes;
    @FXML private Label lblCabeceraChat;
    @FXML private TextField txtMensaje;
    @FXML private Button btnEnviar;
    @FXML private Button btnNuevaConversacion;
    @FXML private Label lblEstado;

    private final ObservableList<Map<String, Object>> conversaciones = FXCollections.observableArrayList();
    private final ObservableList<Map<String, Object>> mensajes = FXCollections.observableArrayList();

    private Map<String, Object> conversacionActiva;

    private Socket socket;
    private PrintWriter salida;
    private BufferedReader entrada;
    private Thread hiloEscucha;
    private volatile boolean conectado = false;

    @FXML
    private void initialize() {
        listaConversaciones.setItems(conversaciones);
        listaMensajes.setItems(mensajes);

        listaConversaciones.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Map<String, Object> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String nombre = String.valueOf(item.getOrDefault("nombreOtroParticipante", "—"));
                    String ultimo = item.get("ultimoMensaje") != null ? String.valueOf(item.get("ultimoMensaje")) : "";
                    if (ultimo.length() > 40) ultimo = ultimo.substring(0, 37) + "…";
                    setText(nombre + (ultimo.isEmpty() ? "" : "\n" + ultimo));
                }
            }
        });

        listaMensajes.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Map<String, Object> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Integer idEmisor = toInt(item.get("idEmisor"));
                    Integer miId = SesionUsuario.getInstancia().getId();
                    boolean propio = miId != null && miId.equals(idEmisor);
                    String contenido = String.valueOf(item.getOrDefault("contenido", ""));
                    setText(contenido);
                    setStyle(propio
                            ? "-fx-background-color: #D1E7FF; -fx-alignment: CENTER-RIGHT; -fx-padding: 6 10 6 10;"
                            : "-fx-background-color: white; -fx-alignment: CENTER-LEFT; -fx-padding: 6 10 6 10;");
                }
            }
        });

        listaConversaciones.getSelectionModel().selectedItemProperty()
                .addListener((obs, o, n) -> abrirConversacion(n));

        btnEnviar.setOnAction(e -> enviarMensaje());
        txtMensaje.setOnAction(e -> enviarMensaje());
        if (btnNuevaConversacion != null) {
            btnNuevaConversacion.setOnAction(e -> abrirDialogoNuevaConversacion());
        }

        btnEnviar.setDisable(true);
        txtMensaje.setDisable(true);

        cargarConversaciones();
        Platform.runLater(this::conectar);
    }

    private void abrirDialogoNuevaConversacion() {
        SesionUsuario sesion = SesionUsuario.getInstancia();
        Integer miId = sesion.getId();
        String token = sesion.getToken();
        if (miId == null || token == null) {
            UIUtils.mostrarInfo("Chat", "No hay sesión activa.");
            return;
        }

        new Thread(() -> {
            try {
                List<Map<String, Object>> contactos = PawLinkClient.getContactosChat(miId, token);
                Platform.runLater(() -> mostrarSelectorUsuario(contactos));
            } catch (Exception e) {
                Platform.runLater(() -> UIUtils.mostrarInfo("Chat",
                        "No se pudieron cargar los usuarios:\n" + e.getMessage()));
            }
        }, "ChatContactosThread").start();
    }

    private void mostrarSelectorUsuario(List<Map<String, Object>> contactos) {
        if (contactos == null || contactos.isEmpty()) {
            UIUtils.mostrarInfo("Chat", "No hay usuarios disponibles para iniciar una conversación.");
            return;
        }

        java.util.Map<String, Map<String, Object>> indice = new java.util.LinkedHashMap<>();
        for (Map<String, Object> c : contactos) {
            String nombre = String.valueOf(c.getOrDefault("nombre", "—"));
            String email = String.valueOf(c.getOrDefault("email", ""));
            String etiqueta = email.isEmpty() ? nombre : nombre + " (" + email + ")";
            indice.put(etiqueta, c);
        }

        javafx.scene.control.ChoiceDialog<String> choice =
                new javafx.scene.control.ChoiceDialog<>(indice.keySet().iterator().next(), indice.keySet());
        choice.setTitle("Nueva conversación");
        choice.setHeaderText("Selecciona un usuario para iniciar la conversación");
        choice.setContentText("Usuario:");
        UIUtils.añadirIconoADialogo(choice);

        java.util.Optional<String> seleccion = choice.showAndWait();
        if (seleccion.isEmpty()) return;

        Map<String, Object> contacto = indice.get(seleccion.get());
        Integer idContacto = toInt(contacto.get("id"));
        if (idContacto == null) idContacto = toInt(contacto.get("idUsuario"));
        if (idContacto == null) {
            UIUtils.mostrarInfo("Chat", "El usuario seleccionado no tiene identificador válido.");
            return;
        }

        javafx.scene.control.TextInputDialog dlgAsunto = new javafx.scene.control.TextInputDialog();
        dlgAsunto.setTitle("Nueva conversación");
        dlgAsunto.setHeaderText("Asunto de la conversación");
        dlgAsunto.setContentText("Asunto:");
        UIUtils.añadirIconoADialogo(dlgAsunto);
        java.util.Optional<String> asuntoOpt = dlgAsunto.showAndWait();
        if (asuntoOpt.isEmpty()) return;
        String asunto = asuntoOpt.get().trim();
        if (asunto.isEmpty()) asunto = "Consulta";

        crearConversacion(idContacto, asunto);
    }

    private void crearConversacion(int idOtroUsuario, String asunto) {
        SesionUsuario sesion = SesionUsuario.getInstancia();
        Integer miId = sesion.getId();
        String token = sesion.getToken();
        if (miId == null || token == null) return;

        boolean soyVet = sesion.isVeterinario();
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("asunto", asunto);
        body.put("idUsuario", soyVet ? idOtroUsuario : miId);
        body.put("idVeterinario", soyVet ? miId : idOtroUsuario);

        new Thread(() -> {
            try {
                Map<String, Object> conv = PawLinkClient.crearConversacion(body, token);
                Platform.runLater(() -> {
                    cargarConversaciones();
                    Integer idNueva = toInt(conv.get("idConversacion"));
                    if (idNueva != null) {
                        conversaciones.stream()
                                .filter(c -> idNueva.equals(toInt(c.get("idConversacion"))))
                                .findFirst()
                                .ifPresent(c -> listaConversaciones.getSelectionModel().select(c));
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> UIUtils.mostrarInfo("Chat",
                        "No se pudo crear la conversación:\n" + e.getMessage()));
            }
        }, "ChatCrearConvThread").start();
    }

    private void cargarConversaciones() {
        SesionUsuario sesion = SesionUsuario.getInstancia();
        Integer idUsuario = sesion.getId();
        String token = sesion.getToken();
        if (idUsuario == null || token == null) return;

        new Thread(() -> {
            try {
                List<Map<String, Object>> resp = PawLinkClient.getConversaciones(idUsuario, token);
                Platform.runLater(() -> {
                    Integer idActiva = conversacionActiva != null ? toInt(conversacionActiva.get("idConversacion")) : null;
                    conversaciones.setAll(resp);
                    if (idActiva != null) {
                        conversaciones.stream()
                                .filter(c -> idActiva.equals(toInt(c.get("idConversacion"))))
                                .findFirst()
                                .ifPresent(c -> listaConversaciones.getSelectionModel().select(c));
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> lblEstado.setText("Error cargando conversaciones"));
            }
        }, "ChatConversacionesThread").start();
    }

    private void abrirConversacion(Map<String, Object> conv) {
        if (conv == null) {
            conversacionActiva = null;
            mensajes.clear();
            lblCabeceraChat.setText("Selecciona una conversación");
            btnEnviar.setDisable(true);
            txtMensaje.setDisable(true);
            return;
        }
        conversacionActiva = conv;
        lblCabeceraChat.setText(String.valueOf(conv.getOrDefault("nombreOtroParticipante", "Conversación")));
        btnEnviar.setDisable(!conectado);
        txtMensaje.setDisable(!conectado);
        cargarMensajes(toInt(conv.get("idConversacion")));
    }

    private void cargarMensajes(Integer idConversacion) {
        if (idConversacion == null) return;
        String token = SesionUsuario.getInstancia().getToken();

        new Thread(() -> {
            try {
                List<Map<String, Object>> resp = PawLinkClient.getMensajes(idConversacion, token);
                Platform.runLater(() -> {
                    mensajes.setAll(resp);
                    if (!mensajes.isEmpty()) listaMensajes.scrollTo(mensajes.size() - 1);
                });
            } catch (Exception e) {
                Platform.runLater(() -> lblEstado.setText("Error cargando mensajes"));
            }
        }, "ChatMensajesThread").start();
    }

    private void enviarMensaje() {
        if (conversacionActiva == null) return;
        String texto = txtMensaje.getText();
        if (texto == null || texto.trim().isEmpty()) return;

        Integer idConversacion = toInt(conversacionActiva.get("idConversacion"));
        Integer idEmisor = SesionUsuario.getInstancia().getId();
        String token = SesionUsuario.getInstancia().getToken();
        String contenido = texto.trim();
        txtMensaje.clear();

        new Thread(() -> {
            try {
                PawLinkClient.enviarMensaje(idConversacion, idEmisor, contenido, token);
                Platform.runLater(() -> {
                    cargarMensajes(idConversacion);
                    cargarConversaciones();
                });
            } catch (Exception e) {
                Platform.runLater(() -> UIUtils.mostrarInfo("Chat",
                        "No se pudo enviar el mensaje:\n" + e.getMessage()));
            }
        }, "ChatEnviarThread").start();
    }

    private void conectar() {
        SesionUsuario sesion = SesionUsuario.getInstancia();
        Integer idUsuario = sesion.getId();
        if (idUsuario == null) {
            lblEstado.setText("Sin sesión");
            return;
        }

        try {
            socket = new Socket(HOST, PUERTO);
            salida = new PrintWriter(
                    new java.io.OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                    true);
            entrada = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            salida.println(idUsuario.toString());
            conectado = true;

            lblEstado.setText("Conectado");
            if (conversacionActiva != null) {
                btnEnviar.setDisable(false);
                txtMensaje.setDisable(false);
            }

            hiloEscucha = new Thread(this::escucharServidor, "ChatListenerThread");
            hiloEscucha.setDaemon(true);
            hiloEscucha.start();

            Platform.runLater(() -> {
                Stage stage = (Stage) btnEnviar.getScene().getWindow();
                if (stage != null) {
                    stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::onCerrarVentana);
                }
            });

        } catch (IOException e) {
            conectado = false;
            lblEstado.setText("Sin notificaciones en tiempo real");
            mostrarAlertaSinConexion();
            cerrarConexion();
        }
    }

    private void escucharServidor() {
        try {
            String linea;
            while (conectado && (linea = entrada.readLine()) != null) {
                procesarNotificacion(linea);
            }
        } catch (IOException e) {
            if (conectado) {
                Platform.runLater(() -> {
                    lblEstado.setText("Desconectado");
                    btnEnviar.setDisable(true);
                    txtMensaje.setDisable(true);
                });
            }
        } finally {
            conectado = false;
        }
    }

    private void procesarNotificacion(String linea) {
        if (linea == null) return;
        if (linea.startsWith("NOTIFY:NUEVO_MENSAJE:")) {
            String resto = linea.substring("NOTIFY:NUEVO_MENSAJE:".length()).trim();
            try {
                int idConversacion = Integer.parseInt(resto);
                Platform.runLater(() -> {
                    cargarConversaciones();
                    Integer idActiva = conversacionActiva != null ? toInt(conversacionActiva.get("idConversacion")) : null;
                    if (idActiva != null && idActiva == idConversacion) {
                        cargarMensajes(idConversacion);
                    }
                });
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private void onCerrarVentana(WindowEvent event) {
        cerrarConexion();
    }

    public void cerrarConexion() {
        conectado = false;
        try { if (salida != null) salida.close(); } catch (Exception ignored) {}
        try { if (entrada != null) entrada.close(); } catch (Exception ignored) {}
        try { if (socket != null && !socket.isClosed()) socket.close(); } catch (Exception ignored) {}
        if (hiloEscucha != null && hiloEscucha.isAlive()) {
            hiloEscucha.interrupt();
        }
    }

    private void mostrarAlertaSinConexion() {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Chat PawLink");
        alerta.setHeaderText("No se pudo conectar al servidor de notificaciones");
        alerta.setContentText("Podrás enviar y leer mensajes, pero no recibirás avisos en tiempo real. "
                + "Verifica que el servidor esté en ejecución en " + HOST + ":" + PUERTO + ".");
        UIUtils.añadirIconoADialogo(alerta);
        alerta.showAndWait();
    }

    private Integer toInt(Object o) {
        if (o == null) return null;
        if (o instanceof Integer i) return i;
        if (o instanceof Number n) return n.intValue();
        try { return Integer.valueOf(o.toString()); } catch (NumberFormatException e) { return null; }
    }
}
