package com.javafx.proyecto.controlador;

import com.javafx.proyecto.util.SesionUsuario;
import com.javafx.proyecto.util.UIUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

public class ChatVeterinarioController {

    private static final String HOST = "localhost";
    private static final int PUERTO = 9090;

    @FXML private ListView<String> listaMensajes;
    @FXML private TextField txtMensaje;
    @FXML private Button btnEnviar;
    @FXML private Label lblEstado;

    private final ObservableList<String> mensajes = FXCollections.observableArrayList();

    private Socket socket;
    private PrintWriter salida;
    private BufferedReader entrada;
    private Thread hiloEscucha;
    private volatile boolean conectado = false;

    @FXML
    private void initialize() {
        listaMensajes.setItems(mensajes);

        btnEnviar.setOnAction(e -> enviarMensaje());
        txtMensaje.setOnAction(e -> enviarMensaje());

        btnEnviar.setDisable(true);
        txtMensaje.setDisable(true);

        Platform.runLater(this::conectar);
    }

    private void conectar() {
        try {
            socket = new Socket(HOST, PUERTO);
            salida = new PrintWriter(
                    new java.io.OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                    true);
            entrada = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            conectado = true;

            SesionUsuario sesion = SesionUsuario.getInstancia();
            String nombreBase = sesion.getNombre() != null ? sesion.getNombre() : "Anonimo";
            String nombreUsuario = (sesion.isVeterinario() ? "Veterinario_" : "Admin_")
                    + nombreBase.replace(' ', '_');
            salida.println(nombreUsuario);

            lblEstado.setText("Conectado como " + nombreUsuario);
            btnEnviar.setDisable(false);
            txtMensaje.setDisable(false);
            txtMensaje.requestFocus();

            hiloEscucha = new Thread(this::escucharServidor, "ChatListenerThread");
            hiloEscucha.setDaemon(true);
            hiloEscucha.start();

            Stage stage = (Stage) btnEnviar.getScene().getWindow();
            if (stage != null) {
                stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::onCerrarVentana);
            }

        } catch (IOException e) {
            conectado = false;
            lblEstado.setText("Sin conexión con el servidor");
            mostrarAlertaSinConexion();
            cerrarConexion();
        }
    }

    private void escucharServidor() {
        try {
            String linea;
            while (conectado && (linea = entrada.readLine()) != null) {
                final String mensajeRecibido = linea;
                Platform.runLater(() -> mensajes.add(mensajeRecibido));
            }
        } catch (IOException e) {
            if (conectado) {
                Platform.runLater(() -> {
                    mensajes.add("[Se perdió la conexión con el servidor]");
                    lblEstado.setText("Desconectado");
                    btnEnviar.setDisable(true);
                    txtMensaje.setDisable(true);
                });
            }
        } finally {
            conectado = false;
        }
    }

    private void enviarMensaje() {
        if (!conectado || salida == null) return;

        String texto = txtMensaje.getText();
        if (texto == null || texto.trim().isEmpty()) return;

        salida.println(texto);
        txtMensaje.clear();

        if ("/salir".equalsIgnoreCase(texto.trim())) {
            Stage stage = (Stage) btnEnviar.getScene().getWindow();
            cerrarConexion();
            if (stage != null) stage.close();
        }
    }

    private void onCerrarVentana(WindowEvent event) {
        cerrarConexion();
    }

    public void cerrarConexion() {
        boolean estaba = conectado;
        conectado = false;

        try {
            if (estaba && salida != null) {
                salida.println("/salir");
                salida.flush();
            }
        } catch (Exception ignored) {
        }

        try { if (salida != null) salida.close(); } catch (Exception ignored) {}
        try { if (entrada != null) entrada.close(); } catch (Exception ignored) {}
        try { if (socket != null && !socket.isClosed()) socket.close(); } catch (Exception ignored) {}

        if (hiloEscucha != null && hiloEscucha.isAlive()) {
            hiloEscucha.interrupt();
        }
    }

    private void mostrarAlertaSinConexion() {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Chat Soporte");
        alerta.setHeaderText("No se pudo conectar al servidor de chat");
        alerta.setContentText("Verifica que el servidor esté en ejecución en "
                + HOST + ":" + PUERTO + " e inténtalo de nuevo.");
        UIUtils.añadirIconoADialogo(alerta);
        alerta.showAndWait();
    }
}
