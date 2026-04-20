package com.javafx.proyecto.controlador;

import com.javafx.proyecto.bbdd.PawLinkClient;
import com.javafx.proyecto.bbdd.PawLinkHttpException;
import com.javafx.proyecto.modelo.FiabilidadAdoptante;
import com.javafx.proyecto.modelo.Mascota;
import com.javafx.proyecto.modelo.SolicitudAdopcion;
import com.javafx.proyecto.modelo.Usuario;
import com.javafx.proyecto.util.AnalizadorFiabilidad;
import com.javafx.proyecto.util.SesionUsuario;
import com.javafx.proyecto.util.UIUtils;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class SolicitudesAdopcionController {

    private static final String ESTADO_PENDIENTE  = "pendiente";
    private static final String ESTADO_APROBADO   = "aprobado";
    private static final String ESTADO_DENEGADO   = "rechazado";
    private static final DateTimeFormatter FMT    = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Stage stage;
    private final ObservableList<Mascota> listaMascotas;
    private final ObservableList<Usuario> listaUsuarios;
    private final Runnable onCambios;

    private final ObservableList<SolicitudAdopcion> solicitudes = FXCollections.observableArrayList();
    private final TableView<SolicitudAdopcion> tabla = new TableView<>();

    private final VBox panelDetalles = new VBox(14);
    private final StackPane contenedorFiabilidad = new StackPane();
    private final ProgressIndicator spinnerFiabilidad = new ProgressIndicator();
    private final VBox panelFiabilidad = new VBox(6);

    private final Button btnAprobar = new Button("✓ Aprobar");
    private final Button btnDenegar = new Button("✗ Denegar");
    private final Label lblEstadoVacio = new Label("Selecciona una solicitud para ver sus detalles.");

    private Task<FiabilidadAdoptante> tareaFiabilidadActual;

    private SolicitudesAdopcionController(Stage owner,
            ObservableList<Mascota> listaMascotas,
            ObservableList<Usuario> listaUsuarios,
            Runnable onCambios) {
        this.listaMascotas = listaMascotas;
        this.listaUsuarios = listaUsuarios;
        this.onCambios = onCambios;

        this.stage = new Stage();
        this.stage.initOwner(owner);
        this.stage.initModality(Modality.WINDOW_MODAL);
        this.stage.setTitle("PawLink - Gestión de Solicitudes de Adopción");

        try {
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/miapp/icons/paw.png")));
        } catch (Exception ignored) {}
    }

    public static void abrir(Stage owner,
            ObservableList<Mascota> listaMascotas,
            ObservableList<Usuario> listaUsuarios,
            Runnable onCambios) {
        SolicitudesAdopcionController ctrl = new SolicitudesAdopcionController(owner, listaMascotas, listaUsuarios, onCambios);
        ctrl.construirUI();
        ctrl.cargarSolicitudes();
        ctrl.stage.show();
    }

    // ------------------------------------------------------------------
    // Construcción de la UI (todo programático)
    // ------------------------------------------------------------------

    private void construirUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f6fa;");
        root.setPadding(new Insets(12));

        root.setTop(construirCabecera());
        root.setLeft(construirTabla());
        root.setCenter(construirDetalles());
        root.setBottom(construirBotonera());

        Scene scene = new Scene(root, 1100, 680);
        stage.setScene(scene);
        stage.setMinWidth(980);
        stage.setMinHeight(600);
    }

    private Node construirCabecera() {
        Label titulo = new Label("Solicitudes de Adopción Pendientes");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 20));
        titulo.setTextFill(Color.web("#2c3e50"));

        Button btnRefrescar = new Button("↻ Actualizar");
        btnRefrescar.setStyle(estiloBotonSecundario());
        btnRefrescar.setOnAction(e -> cargarSolicitudes());

        Region separador = new Region();
        HBox.setHgrow(separador, Priority.ALWAYS);

        HBox cabecera = new HBox(12, titulo, separador, btnRefrescar);
        cabecera.setAlignment(Pos.CENTER_LEFT);
        cabecera.setPadding(new Insets(0, 0, 12, 0));
        return cabecera;
    }

    private Node construirTabla() {
        TableColumn<SolicitudAdopcion, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colId.setPrefWidth(55);

        TableColumn<SolicitudAdopcion, String> colMascota = new TableColumn<>("Mascota");
        colMascota.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreMascota()));
        colMascota.setPrefWidth(130);

        TableColumn<SolicitudAdopcion, String> colSolicitante = new TableColumn<>("Solicitante");
        colSolicitante.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreSolicitante()));
        colSolicitante.setPrefWidth(150);

        TableColumn<SolicitudAdopcion, String> colFecha = new TableColumn<>("F. petición");
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaPeticion() != null ? c.getValue().getFechaPeticion().format(FMT) : "—"));
        colFecha.setPrefWidth(100);

        tabla.getColumns().setAll(List.of(colId, colMascota, colSolicitante, colFecha));
        tabla.setItems(solicitudes);
        tabla.setPlaceholder(new Label("No hay solicitudes pendientes."));
        tabla.setPrefWidth(470);

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> mostrarDetalles(n));

        VBox wrapper = new VBox(tabla);
        wrapper.setPadding(new Insets(0, 12, 0, 0));
        return wrapper;
    }

    private Node construirDetalles() {
        lblEstadoVacio.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        lblEstadoVacio.setAlignment(Pos.CENTER);
        lblEstadoVacio.setMaxWidth(Double.MAX_VALUE);

        panelDetalles.setPadding(new Insets(10, 16, 10, 16));
        panelDetalles.getChildren().add(lblEstadoVacio);

        spinnerFiabilidad.setPrefSize(36, 36);
        spinnerFiabilidad.setVisible(false);
        contenedorFiabilidad.getChildren().addAll(panelFiabilidad, spinnerFiabilidad);
        StackPane.setAlignment(spinnerFiabilidad, Pos.CENTER);

        ScrollPane scroll = new ScrollPane(panelDetalles);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: white; -fx-border-color: #dcdde1; -fx-border-radius: 6; -fx-background-radius: 6;");
        return scroll;
    }

    private Node construirBotonera() {
        btnAprobar.setStyle(estiloBotonAccion("#27ae60"));
        btnDenegar.setStyle(estiloBotonAccion("#c0392b"));
        btnAprobar.setPrefWidth(140);
        btnDenegar.setPrefWidth(140);
        btnAprobar.setDisable(true);
        btnDenegar.setDisable(true);

        btnAprobar.setOnAction(e -> decidir(true));
        btnDenegar.setOnAction(e -> decidir(false));

        Region glue = new Region();
        HBox.setHgrow(glue, Priority.ALWAYS);

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setStyle(estiloBotonSecundario());
        btnCerrar.setOnAction(e -> stage.close());

        HBox botonera = new HBox(10, glue, btnDenegar, btnAprobar, btnCerrar);
        botonera.setAlignment(Pos.CENTER_RIGHT);
        botonera.setPadding(new Insets(12, 0, 0, 0));
        return botonera;
    }

    // ------------------------------------------------------------------
    // Carga de datos
    // ------------------------------------------------------------------

    private void cargarSolicitudes() {
        Task<List<SolicitudAdopcion>> tarea = new Task<>() {
            @Override
            protected List<SolicitudAdopcion> call() throws Exception {
                List<Map<String, Object>> alquileres = PawLinkClient.getAlquileres(
                        SesionUsuario.getInstancia().getToken());
                return alquileres.stream()
                        .filter(a -> ESTADO_PENDIENTE.equalsIgnoreCase((String) a.get("estado")))
                        .map(SolicitudesAdopcionController::mapear)
                        .toList();
            }
        };

        tarea.setOnSucceeded(e -> {
            solicitudes.setAll(tarea.getValue());
            limpiarPanelDetalles();
        });
        tarea.setOnFailed(e -> UIUtils.mostrarInfo("Error",
                "No se pudieron cargar las solicitudes: " + tarea.getException().getMessage()));

        ejecutarEnHiloDemonio(tarea);
    }

    private static SolicitudAdopcion mapear(Map<String, Object> a) {
        Integer id          = ((Number) a.get("idAlquiler")).intValue();
        Integer idMascota   = a.get("idMascota") instanceof Number n ? n.intValue() : null;
        Integer idVol       = a.get("idVoluntario") instanceof Number n ? n.intValue() : null;
        String nombreM      = (String) a.get("nombreMascota");
        String nombreV      = (String) a.get("nombreVoluntario");
        String fiStr        = (String) a.get("fechaInicio");
        LocalDate fecha     = fiStr != null ? LocalDate.parse(fiStr) : null;
        String estado       = (String) a.get("estado");
        return new SolicitudAdopcion(id, idMascota, nombreM, idVol, nombreV, fecha, estado);
    }

    // ------------------------------------------------------------------
    // Panel de detalles
    // ------------------------------------------------------------------

    private void limpiarPanelDetalles() {
        panelDetalles.getChildren().setAll(lblEstadoVacio);
        btnAprobar.setDisable(true);
        btnDenegar.setDisable(true);
    }

    private void mostrarDetalles(SolicitudAdopcion sel) {
        if (sel == null) {
            limpiarPanelDetalles();
            return;
        }

        if (tareaFiabilidadActual != null && tareaFiabilidadActual.isRunning()) {
            tareaFiabilidadActual.cancel();
        }

        Mascota m = buscarPorId(listaMascotas, sel.getIdMascota(), Mascota::getId);
        Usuario u = buscarPorId(listaUsuarios, sel.getIdSolicitante(), Usuario::getId);

        panelDetalles.getChildren().setAll(
                construirTarjetaMascota(m, sel),
                construirTarjetaUsuario(u, sel),
                construirTarjetaFiabilidad()
        );

        btnAprobar.setDisable(false);
        btnDenegar.setDisable(false);

        lanzarAnalisisFiabilidad(sel.getIdSolicitante());
    }

    private Node construirTarjetaMascota(Mascota m, SolicitudAdopcion sel) {
        VBox tarjeta = tarjetaBase("Mascota solicitada");

        HBox contenido = new HBox(16);
        contenido.setAlignment(Pos.CENTER_LEFT);

        ImageView foto = new ImageView();
        foto.setFitWidth(110);
        foto.setFitHeight(110);
        foto.setPreserveRatio(true);
        foto.setSmooth(true);
        cargarFoto(foto, m != null ? m.getFoto() : null);

        StackPane marco = new StackPane(foto);
        marco.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 8;");
        marco.setPrefSize(120, 120);

        GridPane ficha = new GridPane();
        ficha.setHgap(10);
        ficha.setVgap(4);
        if (m == null) {
            ficha.add(new Label("Datos no disponibles (ID " + sel.getIdMascota() + ")"), 0, 0);
        } else {
            int r = 0;
            filaFicha(ficha, r++, "Nombre:",  m.getNombre());
            filaFicha(ficha, r++, "Especie:", m.getEspecie());
            filaFicha(ficha, r++, "Raza:",    m.getRaza());
            filaFicha(ficha, r++, "Edad:",    edadDesde(m.getFechaNacimiento()));
            filaFicha(ficha, r++, "Peso:",    m.getPeso() != null ? m.getPeso() + " kg" : "—");
            filaFicha(ficha, r++, "Salud:",   m.getEstadoSalud());
        }

        contenido.getChildren().addAll(marco, ficha);
        tarjeta.getChildren().add(contenido);
        return tarjeta;
    }

    private Node construirTarjetaUsuario(Usuario u, SolicitudAdopcion sel) {
        VBox tarjeta = tarjetaBase("Perfil del solicitante");
        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(4);
        if (u == null) {
            g.add(new Label("Datos no disponibles (ID " + sel.getIdSolicitante() + ")"), 0, 0);
        } else {
            int r = 0;
            filaFicha(g, r++, "Nombre:",    u.getNombre());
            filaFicha(g, r++, "Email:",     u.getEmail());
            filaFicha(g, r++, "Teléfono:",  u.getTelefono());
            filaFicha(g, r++, "Dirección:", u.getDireccion());
            filaFicha(g, r++, "Estado:",    Boolean.TRUE.equals(u.getActivo()) ? "Activo" : "Inactivo");
        }
        tarjeta.getChildren().add(g);
        return tarjeta;
    }

    private Node construirTarjetaFiabilidad() {
        VBox tarjeta = tarjetaBase("Análisis de fiabilidad");
        panelFiabilidad.getChildren().clear();
        panelFiabilidad.getChildren().add(new Label("Analizando historial del adoptante..."));
        tarjeta.getChildren().add(contenedorFiabilidad);
        return tarjeta;
    }

    private void lanzarAnalisisFiabilidad(Integer idSolicitante) {
        if (idSolicitante == null) {
            panelFiabilidad.getChildren().setAll(new Label("No se puede analizar (sin ID de solicitante)."));
            return;
        }

        spinnerFiabilidad.setVisible(true);
        panelFiabilidad.setOpacity(0.35);

        Task<FiabilidadAdoptante> tarea = AnalizadorFiabilidad.crearTarea(
                idSolicitante, SesionUsuario.getInstancia().getToken());

        tarea.setOnSucceeded(e -> {
            spinnerFiabilidad.setVisible(false);
            panelFiabilidad.setOpacity(1.0);
            renderizarFiabilidad(tarea.getValue());
        });
        tarea.setOnFailed(e -> {
            spinnerFiabilidad.setVisible(false);
            panelFiabilidad.setOpacity(1.0);
            panelFiabilidad.getChildren().setAll(new Label(
                    "Error al analizar: " + tarea.getException().getMessage()));
        });
        tarea.setOnCancelled(e -> spinnerFiabilidad.setVisible(false));

        this.tareaFiabilidadActual = tarea;
        ejecutarEnHiloDemonio(tarea);
    }

    private void renderizarFiabilidad(FiabilidadAdoptante f) {
        Circle badge = new Circle(8, Color.web(f.getNivel().getColorHex()));
        Label nivel = new Label(f.getNivel().getEtiqueta());
        nivel.setFont(Font.font("System", FontWeight.BOLD, 14));
        nivel.setTextFill(Color.web(f.getNivel().getColorHex()));
        HBox cabecera = new HBox(8, badge, nivel);
        cabecera.setAlignment(Pos.CENTER_LEFT);

        GridPane m = new GridPane();
        m.setHgap(10);
        m.setVgap(4);
        int r = 0;
        filaFicha(m, r++, "Adopciones previas:",  String.valueOf(f.getTotalAdopciones()));
        filaFicha(m, r++, "Duración media:",      f.getTotalAdopciones() == 0
                ? "—" : String.format("%.1f días", f.getDuracionMediaDias()));
        filaFicha(m, r++, "Última adopción:",     f.getUltimaAdopcion() != null
                ? f.getUltimaAdopcion().format(FMT) : "—");

        Label recomendacion = new Label(f.getRecomendacion());
        recomendacion.setWrapText(true);
        recomendacion.setStyle("-fx-text-fill: #34495e; -fx-padding: 6 0 0 0;");

        panelFiabilidad.getChildren().setAll(cabecera, m, recomendacion);
    }

    // ------------------------------------------------------------------
    // Aprobar / Denegar
    // ------------------------------------------------------------------

    private void decidir(boolean aprobar) {
        SolicitudAdopcion sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        String nuevoEstado = aprobar ? ESTADO_APROBADO : ESTADO_DENEGADO;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(aprobar ? "Aprobar solicitud" : "Denegar solicitud");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Confirmas " + (aprobar ? "aprobar" : "denegar")
                + " la solicitud de " + sel.getNombreSolicitante()
                + " para " + sel.getNombreMascota() + "?");
        UIUtils.añadirIconoADialogo(confirm);
        if (confirm.showAndWait().filter(b -> b == ButtonType.OK).isEmpty()) return;

        btnAprobar.setDisable(true);
        btnDenegar.setDisable(true);

        Task<Void> tarea = new Task<>() {
            @Override
            protected Void call() throws Exception {
                PawLinkClient.actualizarEstadoAlquiler(
                        sel.getId(), nuevoEstado, SesionUsuario.getInstancia().getToken());
                return null;
            }
        };

        tarea.setOnSucceeded(e -> {
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setTitle("Operación completada");
            ok.setHeaderText(aprobar ? "Solicitud aprobada" : "Solicitud denegada");
            ok.setContentText(aprobar
                    ? "La adopción pasa a estado activo y la mascota ha sido marcada como no disponible."
                    : "La solicitud queda rechazada. La mascota sigue disponible para otras peticiones.");
            UIUtils.añadirIconoADialogo(ok);
            ok.showAndWait();

            if (onCambios != null) onCambios.run();
            cargarSolicitudes();
        });
        tarea.setOnFailed(e -> {
            btnAprobar.setDisable(false);
            btnDenegar.setDisable(false);
            mostrarErrorDecision(aprobar, tarea.getException());
        });

        ejecutarEnHiloDemonio(tarea);
    }

    private void mostrarErrorDecision(boolean aprobar, Throwable ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(aprobar ? "No se pudo aprobar" : "No se pudo denegar");

        if (ex instanceof PawLinkHttpException http) {
            switch (http.getStatusCode()) {
                case 400 -> {
                    alert.setHeaderText("Petición rechazada por el servidor (400)");
                    alert.setContentText("Los datos enviados no son válidos.\n\n"
                            + extraerMensajeServidor(http));
                }
                case 409 -> {
                    alert.setHeaderText("Conflicto de estado (409)");
                    alert.setContentText("La solicitud ya fue procesada o la mascota no está disponible.\n\n"
                            + extraerMensajeServidor(http));
                }
                case 401, 403 -> {
                    alert.setHeaderText("Sin autorización (" + http.getStatusCode() + ")");
                    alert.setContentText("Tu sesión no tiene permisos para esta operación.");
                }
                default -> {
                    alert.setHeaderText("Error HTTP " + http.getStatusCode());
                    alert.setContentText(extraerMensajeServidor(http));
                }
            }
        } else {
            alert.setHeaderText("Error de comunicación");
            alert.setContentText(ex != null ? ex.getMessage() : "Error desconocido.");
        }

        UIUtils.añadirIconoADialogo(alert);
        alert.showAndWait();
    }

    private static String extraerMensajeServidor(PawLinkHttpException http) {
        String body = http.getBody();
        return body != null && !body.isBlank() ? body : "(sin detalle)";
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private static <T> T buscarPorId(List<T> lista, Integer id,
            java.util.function.Function<T, Integer> idGetter) {
        if (lista == null || id == null) return null;
        return lista.stream()
                .filter(x -> id.equals(idGetter.apply(x)))
                .findFirst()
                .orElse(null);
    }

    private static String edadDesde(LocalDate nacimiento) {
        if (nacimiento == null) return "—";
        Period p = Period.between(nacimiento, LocalDate.now());
        if (p.getYears() > 0) return p.getYears() + " año" + (p.getYears() == 1 ? "" : "s");
        if (p.getMonths() > 0) return p.getMonths() + " mes" + (p.getMonths() == 1 ? "" : "es");
        return p.getDays() + " día" + (p.getDays() == 1 ? "" : "s");
    }

    private void cargarFoto(ImageView iv, String ruta) {
        Image img = null;
        if (ruta != null && !ruta.isBlank()) {
            try {
                img = new Image(ruta, true);
            } catch (Exception ignored) {}
        }
        if (img == null || img.isError()) {
            try {
                img = new Image(getClass().getResourceAsStream("/miapp/icons/paw.png"));
            } catch (Exception ignored) {}
        }
        iv.setImage(img);
    }

    private static VBox tarjetaBase(String titulo) {
        Label t = new Label(titulo);
        t.setFont(Font.font("System", FontWeight.BOLD, 14));
        t.setTextFill(Color.web("#2c3e50"));

        VBox v = new VBox(8, t);
        v.setPadding(new Insets(12));
        v.setStyle("""
                -fx-background-color: white;
                -fx-border-color: #dfe4ea;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                """);
        return v;
    }

    private static void filaFicha(GridPane g, int fila, String clave, String valor) {
        Label k = new Label(clave);
        k.setStyle("-fx-text-fill: #7f8c8d;");
        Label v = new Label(valor != null && !valor.isBlank() ? valor : "—");
        v.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        g.add(k, 0, fila);
        g.add(v, 1, fila);
    }

    private static String estiloBotonAccion(String colorHex) {
        return "-fx-background-color: " + colorHex + ";"
             + "-fx-text-fill: white;"
             + "-fx-font-weight: bold;"
             + "-fx-padding: 8 16 8 16;"
             + "-fx-background-radius: 4;";
    }

    private static String estiloBotonSecundario() {
        return "-fx-background-color: #ecf0f1;"
             + "-fx-text-fill: #2c3e50;"
             + "-fx-padding: 8 14 8 14;"
             + "-fx-background-radius: 4;";
    }

    private static void ejecutarEnHiloDemonio(Task<?> tarea) {
        Thread t = new Thread(tarea, "PawLink-Solicitudes");
        t.setDaemon(true);
        Platform.runLater(() -> {}); // asegurar FX disponible
        t.start();
    }
}
