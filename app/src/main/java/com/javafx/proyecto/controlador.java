package com.javafx.proyecto;

import javafx.animation.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.Severity;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class controlador {

    @FXML
    private StackPane stackContenido;

    @FXML
    private AnchorPane vistaInicio;
    @FXML
    private AnchorPane vistaUsuarios;
    @FXML
    private AnchorPane vistaMascotas;
    @FXML
    private AnchorPane vistaAdopciones;
    @FXML
    private AnchorPane vistaInformes;

    @FXML
    private HBox barraCrud;

    @FXML
    private Button btnInicio;
    @FXML
    private Button btnUsuarios;
    @FXML
    private Button btnMascotas;
    @FXML
    private Button btnAdopciones;
    @FXML
    private Button btnInformes;

    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;

    @FXML
    private Label lblUsuariosActivos;
    @FXML
    private Label lblMascotasRegistradas;
    @FXML
    private Label lblAdopcionesActivas;
    @FXML
    private Label lblErrorConexionUsuarios;
    @FXML
    private Label lblErrorConexionMascotas;
    @FXML
    private Label lblErrorConexionAdopciones;

    @FXML
    private TableView<UltimoRegistro> tablaUltimos;
    @FXML
    private TableColumn<UltimoRegistro, String> colUltimoRegistro;
    @FXML
    private TableColumn<UltimoRegistro, String> colUltimoFecha;

    private final ObservableList<UltimoRegistro> listaUltimos = FXCollections.observableArrayList();

    @FXML
    private BarChart<String, Number> graficaEspecies;
    @FXML
    private CategoryAxis ejeXEspecies;
    @FXML
    private NumberAxis ejeYEspecies;

    @FXML
    private TableView<Usuario> tablaUsuarios;
    @FXML
    private TableColumn<Usuario, Integer> colUsuarioId;
    @FXML
    private TableColumn<Usuario, String> colUsuarioNombre;
    @FXML
    private TableColumn<Usuario, String> colUsuarioEmail;
    @FXML
    private TableColumn<Usuario, String> colUsuarioTelefono;
    @FXML
    private TableColumn<Usuario, String> colUsuarioDireccion;
    @FXML
    private TableColumn<Usuario, Boolean> colUsuarioActivo;

    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();

    // Campos de búsqueda de usuarios
    @FXML
    private ComboBox<String> comboBuscarUsuarioNombre;
    @FXML
    private ComboBox<String> comboBuscarUsuarioEmail;
    @FXML
    private ComboBox<String> comboBuscarUsuarioTelefono;
    @FXML
    private ComboBox<String> comboBuscarUsuarioDireccion;
    @FXML
    private Button btnLimpiarUsuarios;

    @FXML
    private TableView<Mascota> tablaMascotas;
    @FXML
    private TableColumn<Mascota, Integer> colMascotaId;
    @FXML
    private TableColumn<Mascota, String> colMascotaNombre;
    @FXML
    private TableColumn<Mascota, String> colMascotaEspecie;
    @FXML
    private TableColumn<Mascota, String> colMascotaRaza;
    @FXML
    private TableColumn<Mascota, LocalDate> colMascotaFechaNac;
    @FXML
    private TableColumn<Mascota, Double> colMascotaPeso;
    @FXML
    private TableColumn<Mascota, String> colMascotaEstadoSalud;
    @FXML
    private TableColumn<Mascota, Boolean> colMascotaDisponible;

    private final ObservableList<Mascota> listaMascotas = FXCollections.observableArrayList();

    // Campos de búsqueda de mascotas
    @FXML
    private ComboBox<String> comboBuscarMascotaNombre;
    @FXML
    private ComboBox<String> comboBuscarMascotaEspecie;
    @FXML
    private ComboBox<String> comboBuscarMascotaRaza;
    @FXML
    private ComboBox<String> comboBuscarMascotaEstadoSalud;
    @FXML
    private Button btnLimpiarMascotas;

    @FXML
    private TableView<AdopcionTabla> tablaAdopciones;
    @FXML
    private TableColumn<AdopcionTabla, Integer> colAdopcionId;
    @FXML
    private TableColumn<AdopcionTabla, String> colAdopcionMascota;
    @FXML
    private TableColumn<AdopcionTabla, String> colAdopcionVoluntario;
    @FXML
    private TableColumn<AdopcionTabla, LocalDate> colAdopcionFechaInicio;
    @FXML
    private TableColumn<AdopcionTabla, LocalDate> colAdopcionFechaFin;
    @FXML
    private TableColumn<AdopcionTabla, String> colAdopcionEstado;
    @FXML
    private TableColumn<AdopcionTabla, Integer> colAdopcionCalificacion;

    private final ObservableList<AdopcionTabla> listaAdopciones = FXCollections.observableArrayList();

    // Campos de búsqueda de adopciones
    @FXML
    private ComboBox<String> comboBuscarAdopcionMascota;
    @FXML
    private ComboBox<String> comboBuscarAdopcionVoluntario;
    @FXML
    private ComboBox<String> comboBuscarAdopcionEstado;
    @FXML
    private ComboBox<String> comboBuscarAdopcionCalificacion;
    @FXML
    private Button btnLimpiarAdopciones;

    @FXML
    private DatePicker dpInformeDesde;
    @FXML
    private DatePicker dpInformeHasta;
    @FXML
    private ComboBox<String> comboInformeMascota;
    @FXML
    private ComboBox<String> comboInformeUsuario;
    @FXML
    private TableView<?> tablaInformes;
    @FXML
    private Button btnExportarPdf;
    @FXML
    private Button btnExportarExcel;
    @FXML
    private Button btnBuscarContratos;

    private AnchorPane vistaActual;

    private enum Seccion {
        INICIO, USUARIOS, MASCOTAS, ADOPCIONES, INFORMES
    }

    private Seccion seccionActual = Seccion.INICIO;

    // Variables para prevenir recursión en ComboBox con autocompletado
    private boolean cambiandoProgramaticamente = false;

    private void añadirIconoADialogo(Dialog<?> dialogo) {
        Stage stage = (Stage) dialogo.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/miapp/icons/paw.png")));
    }

    @FXML
    private void initialize() {
        btnInicio.setOnAction(e -> mostrarVista(vistaInicio, btnInicio, Seccion.INICIO));
        btnUsuarios.setOnAction(e -> mostrarVista(vistaUsuarios, btnUsuarios, Seccion.USUARIOS));
        btnMascotas.setOnAction(e -> mostrarVista(vistaMascotas, btnMascotas, Seccion.MASCOTAS));
        btnAdopciones.setOnAction(e -> mostrarVista(vistaAdopciones, btnAdopciones, Seccion.ADOPCIONES));
        btnInformes.setOnAction(e -> mostrarVista(vistaInformes, btnInformes, Seccion.INFORMES));

        btnNuevo.setOnAction(e -> accionCrud("Nuevo"));
        btnEditar.setOnAction(e -> accionCrud("Editar"));
        btnEliminar.setOnAction(e -> accionCrud("Eliminar"));

        configurarColumnasUsuarios();
        configurarColumnasMascotas();
        configurarColumnasAdopciones();
        configurarTablaUltimos();

        configurarMenuContextualUsuarios();
        configurarMenuContextualMascotas();
        configurarMenuContextualAdopciones();

        configurarAnimacionesBotones();

        configurarBuscadores();

        cargarUsuariosDesdeBBDD();
        cargarMascotasDesdeBBDD();
        cargarAdopcionesDesdeBBDD();
        cargarDatosDashboard();
        rellenarGraficaEspeciesDesdeBBDD();
        cargarUltimosRegistros();

        // Mostrar la vista de inicio por defecto
        mostrarVista(vistaInicio, btnInicio, Seccion.INICIO);

        // Animación de entrada
        aplicarAnimacionEntrada();
    }

    private void mostrarVista(AnchorPane vista, Button botonMenu, Seccion seccion) {
        // Ocultar todas las vistas
        vistaInicio.setVisible(false);
        vistaUsuarios.setVisible(false);
        vistaMascotas.setVisible(false);
        vistaAdopciones.setVisible(false);
        vistaInformes.setVisible(false);

        // Mostrar solo la vista seleccionada
        vista.setVisible(true);
        vista.toFront();
        vistaActual = vista;
        seccionActual = seccion;

        // Recargar datos de los buscadores según la vista
        recargarDatosBuscadores(seccion);

        // Resaltar el botón activo y actualizar estado
        marcarBotonActivo(botonMenu);
        registrarListenersTablas();
        actualizarEstadoBotonesCrud();
    }

    private void registrarListenersTablas() {
        // Escuchar cambios en la selección de cada tabla
        if (tablaUsuarios != null) {
            tablaUsuarios.getSelectionModel().selectedItemProperty()
                    .addListener((obs, o, n) -> actualizarEstadoBotonesCrud());
        }
        if (tablaMascotas != null) {
            tablaMascotas.getSelectionModel().selectedItemProperty()
                    .addListener((obs, o, n) -> actualizarEstadoBotonesCrud());
        }
        if (tablaAdopciones != null) {
            tablaAdopciones.getSelectionModel().selectedItemProperty()
                    .addListener((obs, o, n) -> actualizarEstadoBotonesCrud());
        }
        if (tablaInformes != null) {
            tablaInformes.getSelectionModel().selectedItemProperty()
                    .addListener((obs, o, n) -> actualizarEstadoBotonesCrud());
        }
    }

    private void marcarBotonActivo(Button activo) {
        // Limpiar estilos de todos los botones del menú
        String normal = "";

        // Estilo para el botón activo: fondo gris, texto en negritas, línea azul
        // inferior
        String seleccionado = """
                -fx-background-color: #d0d0d0;
                -fx-font-weight: bold;
                -fx-border-color: #0078d4;
                -fx-border-width: 0 0 3 0;
                """;

        btnInicio.setStyle(normal);
        btnUsuarios.setStyle(normal);
        btnMascotas.setStyle(normal);
        btnAdopciones.setStyle(normal);
        btnInformes.setStyle(normal);

        // Aplicar el estilo seleccionado al botón activo
        if (activo != null) {
            activo.setStyle(seleccionado);
        }
    }

    private void actualizarEstadoBotonesCrud() {
        if (barraCrud == null) {
            return;
        }

        // La barra siempre está visible, pero los botones se ocultan en Inicio
        barraCrud.setVisible(true);
        barraCrud.setManaged(true);

        // Si estamos en la vista de inicio, ocultar solo los botones
        if (seccionActual == Seccion.INICIO) {
            btnNuevo.setVisible(false);
            btnEditar.setVisible(false);
            btnEliminar.setVisible(false);
            return;
        } else {
            // En el resto de vistas, mostrar los botones
            btnNuevo.setVisible(true);
            btnEditar.setVisible(true);
            btnEliminar.setVisible(true);
        }

        // Verificar si hay alguna fila seleccionada en la tabla actual
        boolean haySeleccion;

        switch (seccionActual) {
            case USUARIOS ->
                haySeleccion = tablaUsuarios != null && tablaUsuarios.getSelectionModel().getSelectedItem() != null;
            case MASCOTAS ->
                haySeleccion = tablaMascotas != null && tablaMascotas.getSelectionModel().getSelectedItem() != null;
            case ADOPCIONES ->
                haySeleccion = tablaAdopciones != null && tablaAdopciones.getSelectionModel().getSelectedItem() != null;
            case INFORMES ->
                haySeleccion = tablaInformes != null && tablaInformes.getSelectionModel().getSelectedItem() != null;
            default ->
                haySeleccion = false;
        }

        // El botón nuevo siempre está activo, editar y eliminar solo si hay selección
        btnNuevo.setDisable(false);
        btnEditar.setDisable(!haySeleccion);
        btnEliminar.setDisable(!haySeleccion);
    }

    private void accionCrud(String tipo) {
        if (seccionActual == null) {
            mostrarInfo("Acción CRUD", "Selecciona primero una sección en el menú de la izquierda.");
            return;
        }

        // Ejecutar la acción CRUD correspondiente según la sección actual
        if (seccionActual == Seccion.USUARIOS) {
            switch (tipo) {
                case "Nuevo" ->
                    mostrarDialogoNuevoUsuario();
                case "Editar" ->
                    mostrarDialogoEditarUsuario();
                case "Eliminar" ->
                    mostrarDialogoEliminarUsuario();
            }
        } else if (seccionActual == Seccion.MASCOTAS) {
            switch (tipo) {
                case "Nuevo" ->
                    mostrarDialogoNuevoMascota();
                case "Editar" ->
                    mostrarDialogoEditarMascota();
                case "Eliminar" ->
                    mostrarDialogoEliminarMascota();
            }
        } else if (seccionActual == Seccion.ADOPCIONES) {
            switch (tipo) {
                case "Nuevo" ->
                    mostrarDialogoNuevoAdopcion();
                case "Editar" ->
                    mostrarDialogoEditarAdopcion();
                case "Eliminar" ->
                    mostrarDialogoEliminarAdopcion();
            }
        } else if (seccionActual == Seccion.INFORMES) {
            switch (tipo) {
                case "Nuevo" ->
                    mostrarDialogoNuevoInforme();
                case "Editar" ->
                    mostrarDialogoEditarInforme();
                case "Eliminar" ->
                    mostrarDialogoEliminarInforme();
            }
        } else {
            mostrarInfo("Acción CRUD", tipo + " en esta sección todavía no está implementado.");
        }
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        añadirIconoADialogo(alerta);
        alerta.showAndWait();
    }

    private void mostrarDialogoNuevoUsuario() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nuevo usuario");
        dialog.setHeaderText("Introduce los datos del nuevo usuario");

        ButtonType btnGuardar = new ButtonType("_Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("_Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

        GridPane grid = crearGridBasico();

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Ej: Juan Pérez");
        txtNombre.setTooltip(new Tooltip("Introduce el nombre completo del usuario (entre 3 y 50 caracteres)"));

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("usuario@ejemplo.com");
        txtEmail.setTooltip(new Tooltip("Introduce un email válido en formato: usuario@dominio.com"));

        TextField txtTelefono = new TextField();
        txtTelefono.setPromptText("123456789");
        txtTelefono.setTooltip(new Tooltip("Introduce 9 dígitos numéricos sin espacios"));

        TextField txtDireccion = new TextField();
        txtDireccion.setPromptText("Ej: Calle Principal 123");
        txtDireccion.setTooltip(new Tooltip("Introduce la dirección completa del usuario"));

        CheckBox chkActivo = new CheckBox("_Activo");
        chkActivo.setSelected(true);

        grid.addRow(0, new Label("Nombre:"), txtNombre);
        grid.addRow(1, new Label("Email:"), txtEmail);
        grid.addRow(2, new Label("Teléfono:"), txtTelefono);
        grid.addRow(3, new Label("Dirección:"), txtDireccion);
        grid.addRow(4, new Label(""), chkActivo);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setMinWidth(500);
        añadirIconoADialogo(dialog);

        java.util.List<ValidationSupport> validadores = new java.util.ArrayList<>();
        validadores.add(ValidadorForms.validarNombreUsuario(txtNombre));
        validadores.add(ValidadorForms.validarEmailUsuario(txtEmail));
        validadores.add(ValidadorForms.validarTelefonoUsuario(txtTelefono));
        validadores.add(ValidadorForms.validarCampoObligatorio(txtDireccion, "Dirección"));

        javafx.application.Platform.runLater(() -> {
            for (ValidationSupport vs : validadores) {
                vs.initInitialDecoration();
            }
            txtNombre.requestFocus();
        });

        validadores.forEach(vs -> vs.invalidProperty().addListener((obs, o, n) -> {
            boolean todoOK = validadores.stream()
                    .allMatch(v -> v.getValidationResult().getErrors().isEmpty());
            dialog.getDialogPane().lookupButton(btnGuardar).setDisable(!todoOK);
        }));

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == btnGuardar) {

            String sql = "INSERT INTO Usuarios (nombre, email, telefono, direccion, activo) VALUES (?,?,?,?,?)";

            try (Connection conn = ConexionBBDD.getConexion(); PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setString(1, txtNombre.getText());
                pst.setString(2, txtEmail.getText());
                pst.setString(3, txtTelefono.getText());
                pst.setString(4, txtDireccion.getText());
                pst.setBoolean(5, chkActivo.isSelected());

                pst.executeUpdate();

                // Recargar tablas/dashboard
                cargarUsuariosDesdeBBDD();
                cargarDatosDashboard();
                cargarUltimosRegistros();

            } catch (SQLException e) {
                mostrarInfo("Error BBDD", "No se pudo insertar el usuario:\n" + e.getMessage());
            }
        }
    }

    private void mostrarDialogoEditarUsuario() {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarInfo("Editar usuario", "Selecciona primero un usuario de la tabla.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar usuario");
        dialog.setHeaderText("Edita los datos del usuario");

        ButtonType btnGuardar = new ButtonType("_Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("_Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

        GridPane grid = crearGridBasico();

        TextField txtNombre = new TextField(seleccionado.getNombre());
        txtNombre.setTooltip(new Tooltip("Introduce el nombre completo del usuario (entre 3 y 50 caracteres)"));

        TextField txtEmail = new TextField(seleccionado.getEmail());
        txtEmail.setTooltip(new Tooltip("Introduce un email válido en formato: usuario@dominio.com"));

        TextField txtTelefono = new TextField(seleccionado.getTelefono());
        txtTelefono.setTooltip(new Tooltip("Introduce 9 dígitos numéricos sin espacios"));

        TextField txtDireccion = new TextField(seleccionado.getDireccion());
        txtDireccion.setTooltip(new Tooltip("Introduce la dirección completa del usuario"));

        CheckBox chkActivo = new CheckBox("_Activo");
        chkActivo.setSelected(seleccionado.getActivo());

        grid.addRow(0, new Label("Nombre:"), txtNombre);
        grid.addRow(1, new Label("Email:"), txtEmail);
        grid.addRow(2, new Label("Teléfono:"), txtTelefono);
        grid.addRow(3, new Label("Dirección:"), txtDireccion);
        grid.addRow(4, new Label(""), chkActivo);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setMinWidth(500);
        añadirIconoADialogo(dialog);

        java.util.List<ValidationSupport> validadores = new java.util.ArrayList<>();
        validadores.add(ValidadorForms.validarNombreUsuario(txtNombre));
        validadores.add(ValidadorForms.validarEmailUsuario(txtEmail));
        validadores.add(ValidadorForms.validarTelefonoUsuario(txtTelefono));
        validadores.add(ValidadorForms.validarCampoObligatorio(txtDireccion, "Dirección"));

        javafx.application.Platform.runLater(() -> {
            for (ValidationSupport vs : validadores) {
                vs.initInitialDecoration();
            }
        });

        validadores.forEach(vs -> vs.invalidProperty().addListener((obs, o, n) -> {
            boolean todoOK = validadores.stream()
                    .allMatch(v -> v.getValidationResult().getErrors().isEmpty());
            dialog.getDialogPane().lookupButton(btnGuardar).setDisable(!todoOK);
        }));

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == btnGuardar) {

            String sql = "UPDATE Usuarios SET nombre = ?, email = ?, telefono = ?, direccion = ?, activo = ? "
                    + "WHERE id_usuario = ?";

            try (Connection conn = ConexionBBDD.getConexion(); PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setString(1, txtNombre.getText());
                pst.setString(2, txtEmail.getText());
                pst.setString(3, txtTelefono.getText());
                pst.setString(4, txtDireccion.getText());
                pst.setBoolean(5, chkActivo.isSelected());
                pst.setInt(6, seleccionado.getId());

                pst.executeUpdate();

                cargarUsuariosDesdeBBDD();
                cargarDatosDashboard();
                cargarUltimosRegistros();

            } catch (SQLException e) {
                mostrarInfo("Error BBDD", "No se pudo actualizar el usuario:\n" + e.getMessage());
            }
        }
    }

    private void mostrarDialogoEliminarUsuario() {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarInfo("Eliminar usuario", "Selecciona primero un usuario de la tabla.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar usuario");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que quieres eliminar al usuario " + seleccionado.getNombre() + "?");
        añadirIconoADialogo(confirm);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            String sql = "DELETE FROM Usuarios WHERE id_usuario = ?";

            try (Connection conn = ConexionBBDD.getConexion(); PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setInt(1, seleccionado.getId());
                pst.executeUpdate();

                cargarUsuariosDesdeBBDD();
                cargarDatosDashboard();
                cargarUltimosRegistros();

            } catch (SQLException e) {
                mostrarInfo("Error BBDD", "No se pudo eliminar el usuario:\n" + e.getMessage());
            }
        }
    }

    private void mostrarDialogoNuevoMascota() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nueva mascota");
        dialog.setHeaderText("Introduce los datos de la mascota");

        ButtonType btnGuardar = new ButtonType("_Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("_Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

        GridPane grid = crearGridBasico();

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Ej: Max");
        txtNombre.setTooltip(new Tooltip("Introduce el nombre de la mascota"));

        TextField txtEspecie = new TextField();
        txtEspecie.setPromptText("Ej: Perro, Gato");
        txtEspecie.setTooltip(new Tooltip("Introduce la especie de la mascota (obligatorio)"));

        TextField txtRaza = new TextField();
        txtRaza.setPromptText("Ej: Labrador");
        txtRaza.setTooltip(new Tooltip("Introduce la raza de la mascota (obligatorio)"));

        DatePicker dpFechaNac = new DatePicker();
        dpFechaNac.setPromptText("dd/mm/aaaa");
        dpFechaNac.setTooltip(new Tooltip("Selecciona la fecha de nacimiento"));

        TextField txtPeso = new TextField();
        txtPeso.setPromptText("Ej: 15.5");
        txtPeso.setTooltip(new Tooltip("Introduce el peso en kilogramos (usa punto como separador decimal)"));

        TextField txtEstado = new TextField();
        txtEstado.setPromptText("Ej: Saludable");
        txtEstado.setTooltip(new Tooltip("Introduce el estado de salud de la mascota (obligatorio)"));

        CheckBox chkDisponible = new CheckBox("_Disponible para alquiler");
        chkDisponible.setSelected(true);

        grid.addRow(0, new Label("Nombre:"), txtNombre);
        grid.addRow(1, new Label("Especie:"), txtEspecie);
        grid.addRow(2, new Label("Raza:"), txtRaza);
        grid.addRow(3, new Label("Fecha nac.:"), dpFechaNac);
        grid.addRow(4, new Label("Peso (kg):"), txtPeso);
        grid.addRow(5, new Label("Estado salud:"), txtEstado);
        grid.addRow(6, new Label(""), chkDisponible);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setMinWidth(500);
        añadirIconoADialogo(dialog);

        java.util.List<ValidationSupport> validadores = new java.util.ArrayList<>();
        validadores.add(ValidadorForms.validarNombreUsuario(txtNombre));
        validadores.add(ValidadorForms.validarEspecieMascota(txtEspecie));
        validadores.add(ValidadorForms.validarRazaMascota(txtRaza));
        validadores.add(ValidadorForms.validarPesoMascota(txtPeso));
        validadores.add(ValidadorForms.validarEstadoSaludMascota(txtEstado));

        javafx.application.Platform.runLater(() -> {
            for (ValidationSupport vs : validadores) {
                vs.initInitialDecoration();
            }
            txtNombre.requestFocus();
        });

        validadores.forEach(vs -> vs.invalidProperty().addListener((obs, o, n) -> {
            boolean todoOK = validadores.stream()
                    .allMatch(v -> v.getValidationResult().getErrors().isEmpty());
            dialog.getDialogPane().lookupButton(btnGuardar).setDisable(!todoOK);
        }));

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == btnGuardar) {

            double peso = 0;
            try {
                if (!txtPeso.getText().isBlank()) {
                    peso = Double.parseDouble(txtPeso.getText().replace(",", "."));
                }
            } catch (NumberFormatException e) {
                mostrarInfo("Dato inválido", "El peso no es un número válido.");
                return;
            }

            String sql = "INSERT INTO Mascotas "
                    + "(nombre, especie, raza, fecha_nacimiento, peso, estado_salud, disponible_alquiler) "
                    + "VALUES (?,?,?,?,?,?,?)";

            try (Connection conn = ConexionBBDD.getConexion(); PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setString(1, txtNombre.getText());
                pst.setString(2, txtEspecie.getText());
                pst.setString(3, txtRaza.getText());

                LocalDate fechaNac = dpFechaNac.getValue();
                if (fechaNac != null) {
                    pst.setDate(4, java.sql.Date.valueOf(fechaNac));
                } else {
                    pst.setNull(4, java.sql.Types.DATE);
                }

                pst.setDouble(5, peso);
                pst.setString(6, txtEstado.getText());
                pst.setBoolean(7, chkDisponible.isSelected());

                pst.executeUpdate();

                cargarMascotasDesdeBBDD();
                rellenarGraficaEspeciesDesdeBBDD();
                cargarDatosDashboard();
                cargarUltimosRegistros();

            } catch (SQLException e) {
                mostrarInfo("Error BBDD", "No se pudo insertar la mascota:\n" + e.getMessage());
            }
        }
    }

    private void mostrarDialogoEditarMascota() {
        Mascota seleccionada = tablaMascotas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarInfo("Editar mascota", "Selecciona primero una mascota de la tabla.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar mascota");
        dialog.setHeaderText("Edita los datos de la mascota");

        ButtonType btnGuardar = new ButtonType("_Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("_Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

        GridPane grid = crearGridBasico();

        TextField txtNombre = new TextField(seleccionada.getNombre());
        txtNombre.setTooltip(new Tooltip("Introduce el nombre de la mascota"));

        TextField txtEspecie = new TextField(seleccionada.getEspecie());
        txtEspecie.setTooltip(new Tooltip("Introduce la especie de la mascota (obligatorio)"));

        TextField txtRaza = new TextField(seleccionada.getRaza());
        txtRaza.setTooltip(new Tooltip("Introduce la raza de la mascota (obligatorio)"));

        DatePicker dpFechaNac = new DatePicker(seleccionada.getFechaNacimiento());
        dpFechaNac.setTooltip(new Tooltip("Selecciona la fecha de nacimiento"));

        TextField txtPeso = new TextField(
                seleccionada.getPeso() != null ? seleccionada.getPeso().toString() : "");
        txtPeso.setTooltip(new Tooltip("Introduce el peso en kilogramos (usa punto como separador decimal)"));

        TextField txtEstado = new TextField(seleccionada.getEstadoSalud());
        txtEstado.setTooltip(new Tooltip("Introduce el estado de salud de la mascota (obligatorio)"));

        CheckBox chkDisponible = new CheckBox("_Disponible para alquiler");
        chkDisponible.setSelected(seleccionada.getDisponible());

        grid.addRow(0, new Label("Nombre:"), txtNombre);
        grid.addRow(1, new Label("Especie:"), txtEspecie);
        grid.addRow(2, new Label("Raza:"), txtRaza);
        grid.addRow(3, new Label("Fecha nac.:"), dpFechaNac);
        grid.addRow(4, new Label("Peso (kg):"), txtPeso);
        grid.addRow(5, new Label("Estado salud:"), txtEstado);
        grid.addRow(6, new Label(""), chkDisponible);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setMinWidth(500);
        añadirIconoADialogo(dialog);

        java.util.List<ValidationSupport> validadores = new java.util.ArrayList<>();
        validadores.add(ValidadorForms.validarNombreUsuario(txtNombre));
        validadores.add(ValidadorForms.validarEspecieMascota(txtEspecie));
        validadores.add(ValidadorForms.validarRazaMascota(txtRaza));
        validadores.add(ValidadorForms.validarPesoMascota(txtPeso));
        validadores.add(ValidadorForms.validarEstadoSaludMascota(txtEstado));

        javafx.application.Platform.runLater(() -> {
            for (ValidationSupport vs : validadores) {
                vs.initInitialDecoration();
            }
        });

        validadores.forEach(vs -> vs.invalidProperty().addListener((obs, o, n) -> {
            boolean todoOK = validadores.stream()
                    .allMatch(v -> v.getValidationResult().getErrors().isEmpty());
            dialog.getDialogPane().lookupButton(btnGuardar).setDisable(!todoOK);
        }));

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == btnGuardar) {

            double peso = 0;
            try {
                if (!txtPeso.getText().isBlank()) {
                    peso = Double.parseDouble(txtPeso.getText().replace(",", "."));
                }
            } catch (NumberFormatException e) {
                mostrarInfo("Dato inválido", "El peso no es un número válido.");
                return;
            }

            String sql = "UPDATE Mascotas SET nombre = ?, especie = ?, raza = ?, "
                    + "fecha_nacimiento = ?, peso = ?, estado_salud = ?, disponible_alquiler = ? "
                    + "WHERE id_mascota = ?";

            try (Connection conn = ConexionBBDD.getConexion(); PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setString(1, txtNombre.getText());
                pst.setString(2, txtEspecie.getText());
                pst.setString(3, txtRaza.getText());

                LocalDate fechaNac = dpFechaNac.getValue();
                if (fechaNac != null) {
                    pst.setDate(4, java.sql.Date.valueOf(fechaNac));
                } else {
                    pst.setNull(4, java.sql.Types.DATE);
                }

                pst.setDouble(5, peso);
                pst.setString(6, txtEstado.getText());
                pst.setBoolean(7, chkDisponible.isSelected());
                pst.setInt(8, seleccionada.getId());

                pst.executeUpdate();

                cargarMascotasDesdeBBDD();
                rellenarGraficaEspeciesDesdeBBDD();
                cargarDatosDashboard();
                cargarUltimosRegistros();

            } catch (SQLException e) {
                mostrarInfo("Error BBDD", "No se pudo actualizar la mascota:\n" + e.getMessage());
            }
        }
    }

    private void mostrarDialogoEliminarMascota() {
        Mascota seleccionada = tablaMascotas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarInfo("Eliminar mascota", "Selecciona primero una mascota de la tabla.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar mascota");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que quieres eliminar la mascota " + seleccionada.getNombre() + "?");
        añadirIconoADialogo(confirm);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            String sql = "DELETE FROM Mascotas WHERE id_mascota = ?";

            try (Connection conn = ConexionBBDD.getConexion(); PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setInt(1, seleccionada.getId());
                pst.executeUpdate();

                cargarMascotasDesdeBBDD();
                rellenarGraficaEspeciesDesdeBBDD();
                cargarDatosDashboard();
                cargarUltimosRegistros();

            } catch (SQLException e) {
                mostrarInfo("Error BBDD", "No se pudo eliminar la mascota:\n" + e.getMessage());
            }
        }
    }

    private void mostrarDialogoNuevoAdopcion() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nueva adopción");
        dialog.setHeaderText("Introduce los datos de la adopción");

        ButtonType btnGuardar = new ButtonType("_Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("_Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

        GridPane grid = crearGridBasico();

        // ComboBox con autocompletado para mascotas
        ComboBox<Mascota> comboMascota = new ComboBox<>();
        comboMascota.setPrefWidth(250);
        comboMascota.setTooltip(new Tooltip("Busca y selecciona una mascota (puedes escribir para filtrar)"));
        configurarAutocompletado(comboMascota, listaMascotas);

        // ComboBox con autocompletado para usuarios
        ComboBox<Usuario> comboVoluntario = new ComboBox<>();
        comboVoluntario.setPrefWidth(250);
        comboVoluntario.setTooltip(new Tooltip("Busca y selecciona un voluntario (puedes escribir para filtrar)"));
        configurarAutocompletado(comboVoluntario, listaUsuarios);

        DatePicker dpInicio = new DatePicker(LocalDate.now());
        dpInicio.setTooltip(new Tooltip("Fecha de inicio de la adopción"));

        DatePicker dpFin = new DatePicker();
        dpFin.setTooltip(new Tooltip("Fecha de finalización de la adopción (opcional)"));

        TextField txtEstado = new TextField("activo");
        txtEstado.setTooltip(new Tooltip("Estado de la adopción (obligatorio)"));

        TextField txtCalif = new TextField();
        txtCalif.setPromptText("1-5");
        txtCalif.setTooltip(new Tooltip("Calificación de 1 a 5 (opcional)"));

        grid.addRow(0, new Label("Mascota:"), comboMascota);
        grid.addRow(1, new Label("Voluntario:"), comboVoluntario);
        grid.addRow(2, new Label("Fecha inicio:"), dpInicio);
        grid.addRow(3, new Label("Fecha fin:"), dpFin);
        grid.addRow(4, new Label("Estado:"), txtEstado);
        grid.addRow(5, new Label("Calificación (1-5):"), txtCalif);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setMinWidth(500);
        añadirIconoADialogo(dialog);

        // Validadores
        java.util.List<ValidationSupport> validadores = new java.util.ArrayList<>();

        validadores.add(ValidadorForms.validarComboBoxObligatorio(comboMascota, "una mascota"));
        validadores.add(ValidadorForms.validarComboBoxObligatorio(comboVoluntario, "un voluntario"));
        validadores.add(ValidadorForms.validarEstadoAdopcion(txtEstado));
        validadores.add(ValidadorForms.validarCalificacionAdopcion(txtCalif));
        validadores.add(ValidadorForms.validarRangoFechasAdopcion(dpInicio, dpFin));

        javafx.application.Platform.runLater(() -> {
            for (ValidationSupport vs : validadores) {
                vs.initInitialDecoration();
            }
        });

        validadores.forEach(vs -> vs.invalidProperty().addListener((obs, o, n) -> {
            boolean todoOK = validadores.stream()
                    .allMatch(v -> v.getValidationResult().getErrors().isEmpty());
            dialog.getDialogPane().lookupButton(btnGuardar).setDisable(!todoOK);
        }));

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == btnGuardar) {

            Mascota m = comboMascota.getValue();
            Usuario u = comboVoluntario.getValue();

            if (m == null || u == null) {
                mostrarInfo("Datos incompletos", "Selecciona una mascota y un voluntario.");
                return;
            }

            Integer calif = null;
            if (!txtCalif.getText().isBlank()) {
                try {
                    calif = Integer.parseInt(txtCalif.getText());
                } catch (NumberFormatException e) {
                    mostrarInfo("Dato inválido", "La calificación debe ser un número entero.");
                    return;
                }
            }

            String sql = "INSERT INTO Alquileres "
                    + "(id_mascota, id_voluntario, fecha_inicio, fecha_fin, estado, calificacion) "
                    + "VALUES (?,?,?,?,?,?)";

            try (Connection conn = ConexionBBDD.getConexion(); PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setInt(1, m.getId());
                pst.setInt(2, u.getId());

                LocalDate fi = dpInicio.getValue();
                LocalDate ff = dpFin.getValue();

                if (fi != null) {
                    pst.setDate(3, java.sql.Date.valueOf(fi));
                } else {
                    pst.setNull(3, java.sql.Types.DATE);
                }

                if (ff != null) {
                    pst.setDate(4, java.sql.Date.valueOf(ff));
                } else {
                    pst.setNull(4, java.sql.Types.DATE);
                }

                pst.setString(5, txtEstado.getText());

                if (calif != null) {
                    pst.setInt(6, calif);
                } else {
                    pst.setNull(6, java.sql.Types.INTEGER);
                }

                pst.executeUpdate();

                cargarAdopcionesDesdeBBDD();
                cargarDatosDashboard();
                cargarUltimosRegistros();

            } catch (SQLException e) {
                mostrarInfo("Error BBDD", "No se pudo insertar la adopción:\n" + e.getMessage());
            }
        }
    }

    private void mostrarDialogoEditarAdopcion() {
        AdopcionTabla seleccionada = tablaAdopciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarInfo("Editar adopción", "Selecciona primero una adopción de la tabla.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar adopción");
        dialog.setHeaderText("Edita las fechas, el estado y la calificación");

        ButtonType btnGuardar = new ButtonType("_Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("_Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

        GridPane grid = crearGridBasico();

        TextField txtMascota = new TextField(seleccionada.getMascota());
        txtMascota.setEditable(false);
        txtMascota.setStyle("-fx-background-color: #f0f0f0;");

        TextField txtVoluntario = new TextField(seleccionada.getVoluntario());
        txtVoluntario.setEditable(false);
        txtVoluntario.setStyle("-fx-background-color: #f0f0f0;");

        DatePicker dpInicio = new DatePicker(seleccionada.getFechaInicio());
        dpInicio.setTooltip(new Tooltip("Fecha de inicio de la adopción"));

        DatePicker dpFin = new DatePicker(seleccionada.getFechaFin());
        dpFin.setTooltip(new Tooltip("Fecha de finalización de la adopción (opcional)"));

        TextField txtEstado = new TextField(seleccionada.getEstado());
        txtEstado.setTooltip(new Tooltip("Estado de la adopción (obligatorio)"));

        TextField txtCalif = new TextField(
                seleccionada.getCalificacion() != null ? seleccionada.getCalificacion().toString() : "");
        txtCalif.setTooltip(new Tooltip("Calificación de 1 a 5 (opcional)"));

        grid.addRow(0, new Label("Mascota:"), txtMascota);
        grid.addRow(1, new Label("Voluntario:"), txtVoluntario);
        grid.addRow(2, new Label("Fecha inicio:"), dpInicio);
        grid.addRow(3, new Label("Fecha fin:"), dpFin);
        grid.addRow(4, new Label("Estado:"), txtEstado);
        grid.addRow(5, new Label("Calificación:"), txtCalif);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setMinWidth(500);
        añadirIconoADialogo(dialog);

        // Validadores
        java.util.List<ValidationSupport> validadores = new java.util.ArrayList<>();

        validadores.add(ValidadorForms.validarEstadoAdopcion(txtEstado));
        validadores.add(ValidadorForms.validarCalificacionAdopcion(txtCalif));
        validadores.add(ValidadorForms.validarRangoFechasAdopcion(dpInicio, dpFin));

        javafx.application.Platform.runLater(() -> {
            for (ValidationSupport vs : validadores) {
                vs.initInitialDecoration();
            }
        });

        validadores.forEach(vs -> vs.invalidProperty().addListener((obs, o, n) -> {
            boolean todoOK = validadores.stream()
                    .allMatch(v -> v.getValidationResult().getErrors().isEmpty());
            dialog.getDialogPane().lookupButton(btnGuardar).setDisable(!todoOK);
        }));

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == btnGuardar) {

            Integer calif = null;
            if (!txtCalif.getText().isBlank()) {
                try {
                    calif = Integer.parseInt(txtCalif.getText());
                } catch (NumberFormatException e) {
                    mostrarInfo("Dato inválido", "La calificación debe ser un número entero.");
                    return;
                }
            }

            String sql = "UPDATE Alquileres SET fecha_inicio = ?, fecha_fin = ?, estado = ?, calificacion = ? "
                    + "WHERE id_alquiler = ?";

            try (Connection conn = ConexionBBDD.getConexion(); PreparedStatement pst = conn.prepareStatement(sql)) {

                LocalDate fi = dpInicio.getValue();
                LocalDate ff = dpFin.getValue();
                if (fi != null) {
                    pst.setDate(1, java.sql.Date.valueOf(fi));
                } else {
                    pst.setNull(1, java.sql.Types.DATE);
                }

                if (ff != null) {
                    pst.setDate(2, java.sql.Date.valueOf(ff));
                } else {
                    pst.setNull(2, java.sql.Types.DATE);
                }

                pst.setString(3, txtEstado.getText());

                if (calif != null) {
                    pst.setInt(4, calif);
                } else {
                    pst.setNull(4, java.sql.Types.INTEGER);
                }

                pst.setInt(5, seleccionada.getId());
                pst.executeUpdate();

                cargarAdopcionesDesdeBBDD();
                cargarDatosDashboard();
                cargarUltimosRegistros();

            } catch (SQLException e) {
                mostrarInfo("Error BBDD", "No se pudo actualizar la adopción:\n" + e.getMessage());
            }
        }
    }

    private void mostrarDialogoEliminarAdopcion() {
        AdopcionTabla seleccionada = tablaAdopciones.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarInfo("Eliminar adopción", "Selecciona primero una adopción de la tabla.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar adopción");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que quieres eliminar la adopción ID " + seleccionada.getId() + "?");
        añadirIconoADialogo(confirm);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            String sql = "DELETE FROM Alquileres WHERE id_alquiler = ?";

            try (Connection conn = ConexionBBDD.getConexion(); PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setInt(1, seleccionada.getId());
                pst.executeUpdate();

                cargarAdopcionesDesdeBBDD();
                cargarDatosDashboard();
                cargarUltimosRegistros();

            } catch (SQLException e) {
                mostrarInfo("Error BBDD", "No se pudo eliminar la adopción:\n" + e.getMessage());
            }
        }
    }

    private void mostrarDialogoNuevoInforme() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Nuevo informe");
        dialog.setHeaderText("Introduce los datos del informe");

        ButtonType btnAceptar = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnAceptar, btnCancelar);

        GridPane grid = crearGridBasico();

        TextField txtId = new TextField();
        TextField txtUsuario = new TextField();
        TextField txtMascota = new TextField();
        DatePicker dpFecha = new DatePicker();
        TextField txtEstado = new TextField();

        txtId.setText("aqui se haria un buscado avanzado");
        txtUsuario.setText("aqui se haria un buscado avanzado");
        txtMascota.setText("aqui se haria un buscado avanzado");
        txtEstado.setText("aqui se haria un buscado avanzado");

        grid.addRow(0, new Label("ID:"), txtId);
        grid.addRow(1, new Label("Usuario:"), txtUsuario);
        grid.addRow(2, new Label("Mascota:"), txtMascota);
        grid.addRow(3, new Label("Fecha:"), dpFecha);
        grid.addRow(4, new Label("Estado:"), txtEstado);

        dialog.getDialogPane().setContent(grid);
        añadirIconoADialogo(dialog);
        dialog.showAndWait();
    }

    private void mostrarDialogoEditarInforme() {
        Object seleccionado = (tablaInformes != null)
                ? tablaInformes.getSelectionModel().getSelectedItem()
                : null;

        if (seleccionado == null) {
            mostrarInfo("Editar informe", "Selecciona primero un informe de la tabla.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Editar informe");
        dialog.setHeaderText("Edita los datos del informe");

        ButtonType btnAceptar = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnAceptar, btnCancelar);

        GridPane grid = crearGridBasico();

        TextField txtId = new TextField();
        TextField txtUsuario = new TextField();
        TextField txtMascota = new TextField();
        DatePicker dpFecha = new DatePicker();
        TextField txtEstado = new TextField();

        grid.addRow(0, new Label("ID:"), txtId);
        grid.addRow(1, new Label("Usuario:"), txtUsuario);
        grid.addRow(2, new Label("Mascota:"), txtMascota);
        grid.addRow(3, new Label("Fecha:"), dpFecha);
        grid.addRow(4, new Label("Estado:"), txtEstado);

        dialog.getDialogPane().setContent(grid);
        añadirIconoADialogo(dialog);
        dialog.showAndWait();
    }

    private void mostrarDialogoEliminarInforme() {
        Object seleccionado = (tablaInformes != null)
                ? tablaInformes.getSelectionModel().getSelectedItem()
                : null;

        if (seleccionado == null) {
            mostrarInfo("Eliminar informe", "Selecciona primero un informe de la tabla.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar informe");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que quieres eliminar el informe seleccionado?");
        añadirIconoADialogo(confirm);

        confirm.showAndWait();
    }

    private GridPane crearGridBasico() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setStyle("-fx-background-color: #f9f9f9; -fx-alignment: center;");

        // Configurar columnas para que los campos tengan buen tamaño
        javafx.scene.layout.ColumnConstraints col1 = new javafx.scene.layout.ColumnConstraints();
        col1.setMinWidth(120);
        col1.setPrefWidth(120);

        javafx.scene.layout.ColumnConstraints col2 = new javafx.scene.layout.ColumnConstraints();
        col2.setMinWidth(250);
        col2.setPrefWidth(300);
        col2.setHgrow(javafx.scene.layout.Priority.ALWAYS);

        grid.getColumnConstraints().addAll(col1, col2);

        return grid;
    }

    // Configurar autocompletado en ComboBox
    private <T> void configurarAutocompletado(ComboBox<T> combo, ObservableList<T> items) {
        FilteredList<T> filteredItems = new FilteredList<>(items, p -> true);
        combo.setItems(filteredItems);
        combo.setEditable(true);

        TextField editor = combo.getEditor();

        // Bloquear TODOS los espacios
        editor.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (event.getCharacter().equals(" ")) {
                event.consume();
            }
        });

        // Listener para el autocompletado
        editor.textProperty().addListener((obs, oldValue, newValue) -> {
            // Prevenir recursión: si ya estamos cambiando programáticamente, salir
            if (cambiandoProgramaticamente) {
                return;
            }

            final T selected = combo.getSelectionModel().getSelectedItem();

            // Si el campo está vacío, oculta y restaura el filtro
            if (newValue == null || newValue.trim().isEmpty()) {
                try {
                    cambiandoProgramaticamente = true;
                    filteredItems.setPredicate(p -> true);
                    combo.hide();
                } finally {
                    cambiandoProgramaticamente = false;
                }
                return;
            }

            // Si no hay nada seleccionado o he escrito algo cambiando lo seleccionado
            if (selected == null || !selected.toString().equals(newValue)) {
                try {
                    cambiandoProgramaticamente = true;
                    filteredItems.setPredicate(p ->
                        p.toString().toLowerCase().startsWith(newValue.toLowerCase().trim()));
                    combo.setVisibleRowCount(5);
                    combo.show();
                } finally {
                    cambiandoProgramaticamente = false;
                }
            } else {
                // Ocurre si he seleccionado algo de la lista, quita el filtro
                try {
                    cambiandoProgramaticamente = true;
                    filteredItems.setPredicate(p -> true);
                } finally {
                    cambiandoProgramaticamente = false;
                }
            }
        });

        // Posicionar cursor al final al seleccionar
        combo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Prevenir recursión
            if (cambiandoProgramaticamente) {
                return;
            }

            if (newValue != null) {
                Platform.runLater(() -> {
                    try {
                        cambiandoProgramaticamente = true;
                        editor.positionCaret(editor.getText().length());
                    } finally {
                        cambiandoProgramaticamente = false;
                    }
                });
            }
        });
    }

    private void configurarColumnasUsuarios() {
        // Mapear las propiedades del objeto Usuario a las columnas de la tabla
        colUsuarioId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsuarioNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colUsuarioEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colUsuarioTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colUsuarioDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colUsuarioActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
    }

    private void configurarColumnasMascotas() {
        // Mapear las propiedades del objeto Mascota a las columnas de la tabla
        colMascotaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMascotaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colMascotaEspecie.setCellValueFactory(new PropertyValueFactory<>("especie"));
        colMascotaRaza.setCellValueFactory(new PropertyValueFactory<>("raza"));
        colMascotaFechaNac.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colMascotaPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));
        colMascotaEstadoSalud.setCellValueFactory(new PropertyValueFactory<>("estadoSalud"));
        colMascotaDisponible.setCellValueFactory(new PropertyValueFactory<>("disponible"));
    }

    private void configurarColumnasAdopciones() {
        // Mapear las propiedades del objeto AdopcionTabla a las columnas de la tabla
        colAdopcionId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAdopcionMascota.setCellValueFactory(new PropertyValueFactory<>("mascota"));
        colAdopcionVoluntario.setCellValueFactory(new PropertyValueFactory<>("voluntario"));
        colAdopcionFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colAdopcionFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        colAdopcionEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colAdopcionCalificacion.setCellValueFactory(new PropertyValueFactory<>("calificacion"));
    }

    private void configurarTablaUltimos() {
        if (tablaUltimos == null) {
            return;
        }

        if (tablaUltimos.getColumns().size() >= 2) {
            @SuppressWarnings("unchecked")
            TableColumn<UltimoRegistro, String> cDesc = (TableColumn<UltimoRegistro, String>) tablaUltimos.getColumns()
                    .get(0);
            @SuppressWarnings("unchecked")
            TableColumn<UltimoRegistro, String> cFecha = (TableColumn<UltimoRegistro, String>) tablaUltimos.getColumns()
                    .get(1);

            cDesc.setCellValueFactory(
                    datos -> new SimpleStringProperty(datos.getValue().getDescripcion()));
            cFecha.setCellValueFactory(
                    datos -> new SimpleStringProperty(datos.getValue().getFecha()));

            tablaUltimos.setItems(listaUltimos);
        }
    }

    private void cargarUsuariosDesdeBBDD() {
        listaUsuarios.clear();

        String sql = "SELECT id_usuario, nombre, email, telefono, direccion, activo FROM Usuarios";

        try (Connection conn = ConexionBBDD.getConexion();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            int contador = 0;

            while (rs.next()) {
                Usuario u = new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("telefono"),
                        rs.getString("direccion"),
                        rs.getBoolean("activo"));
                listaUsuarios.add(u);
                contador++;
            }
            System.out.println("Usuarios cargados: " + contador);
            ocultarErrorConexion();

        } catch (SQLException e) {
            System.out.println("Error cargando usuarios: " + e.getMessage());
            mostrarErrorConexion();
        }

        tablaUsuarios.setItems(listaUsuarios);
    }

    private void cargarMascotasDesdeBBDD() {
        listaMascotas.clear();

        String sql = "SELECT id_mascota, nombre, especie, raza, fecha_nacimiento, "
                + "peso, estado_salud, disponible_alquiler "
                + "FROM Mascotas";

        try (Connection conn = ConexionBBDD.getConexion();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            int contador = 0;

            while (rs.next()) {
                Date fechaSql = rs.getDate("fecha_nacimiento");
                LocalDate fechaNac = fechaSql != null ? fechaSql.toLocalDate() : null;

                Mascota m = new Mascota(
                        rs.getInt("id_mascota"),
                        rs.getString("nombre"),
                        rs.getString("especie"),
                        rs.getString("raza"),
                        fechaNac,
                        rs.getDouble("peso"),
                        rs.getString("estado_salud"),
                        rs.getBoolean("disponible_alquiler"));
                listaMascotas.add(m);
                contador++;
            }
            System.out.println("Mascotas cargadas: " + contador);
            ocultarErrorConexion();

        } catch (SQLException e) {
            System.out.println("Error cargando mascotas: " + e.getMessage());
            mostrarErrorConexion();
        }

        tablaMascotas.setItems(listaMascotas);
    }

    private void cargarAdopcionesDesdeBBDD() {
        listaAdopciones.clear();

        String sql = "SELECT a.id_alquiler, m.nombre AS mascota, u.nombre AS voluntario, "
                + "a.fecha_inicio, a.fecha_fin, a.estado, a.calificacion "
                + "FROM Alquileres a "
                + "JOIN Mascotas m ON a.id_mascota = m.id_mascota "
                + "JOIN Usuarios u ON a.id_voluntario = u.id_usuario";

        try (Connection conn = ConexionBBDD.getConexion();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            int contador = 0;

            while (rs.next()) {
                Date fiSql = rs.getDate("fecha_inicio");
                Date ffSql = rs.getDate("fecha_fin");

                LocalDate fechaInicio = fiSql != null ? fiSql.toLocalDate() : null;
                LocalDate fechaFin = ffSql != null ? ffSql.toLocalDate() : null;

                Integer calif = rs.getObject("calificacion") != null ? rs.getInt("calificacion") : null;

                AdopcionTabla a = new AdopcionTabla(
                        rs.getInt("id_alquiler"),
                        rs.getString("mascota"),
                        rs.getString("voluntario"),
                        fechaInicio,
                        fechaFin,
                        rs.getString("estado"),
                        calif);
                listaAdopciones.add(a);
                contador++;
            }
            System.out.println("Adopciones cargadas: " + contador);
            ocultarErrorConexion();

        } catch (SQLException e) {
            System.out.println("Error cargando adopciones: " + e.getMessage());
            mostrarErrorConexion();
        }

        tablaAdopciones.setItems(listaAdopciones);
    }

    private void cargarDatosDashboard() {
        try (Connection conn = ConexionBBDD.getConexion()) {

            // Usuarios activos (con activo = true)
            String sqlUsuarios = "SELECT COUNT(*) FROM Usuarios WHERE activo = true";
            try (Statement st = conn.createStatement();
                 ResultSet rsUsuarios = st.executeQuery(sqlUsuarios)) {
                if (rsUsuarios.next()) {
                    lblUsuariosActivos.setText(String.valueOf(rsUsuarios.getInt(1)));
                }
            }

            // Mascotas registradas (total de mascotas)
            String sqlMascotas = "SELECT COUNT(*) FROM Mascotas";
            try (Statement st = conn.createStatement();
                 ResultSet rsMascotas = st.executeQuery(sqlMascotas)) {
                if (rsMascotas.next()) {
                    lblMascotasRegistradas.setText(String.valueOf(rsMascotas.getInt(1)));
                }
            }

            // Adopciones activas (estado = 'activo')
            String sqlAdopciones = "SELECT COUNT(*) FROM Alquileres WHERE estado = 'activo'";
            try (Statement st = conn.createStatement();
                 ResultSet rsAdop = st.executeQuery(sqlAdopciones)) {
                if (rsAdop.next()) {
                    lblAdopcionesActivas.setText(String.valueOf(rsAdop.getInt(1)));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error cargando datos del dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void rellenarGraficaEspeciesDesdeBBDD() {
        graficaEspecies.getData().clear();

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Mascotas por especie");

        String sql = "SELECT especie, COUNT(*) AS total FROM Mascotas GROUP BY especie";

        try (Connection conn = ConexionBBDD.getConexion();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            int contador = 0;
            while (rs.next()) {
                String especie = rs.getString("especie");
                int total = rs.getInt("total");
                serie.getData().add(new XYChart.Data<>(especie, total));
                contador++;
            }
            System.out.println("Datos cargados para gráfica especies: " + contador);

        } catch (SQLException e) {
            System.out.println("Error cargando datos de la gráfica: " + e.getMessage());
        }

        graficaEspecies.getData().add(serie);

        if (ejeXEspecies != null) {
            ejeXEspecies.setLabel("Especie");
        }
        if (ejeYEspecies != null) {
            ejeYEspecies.setLabel("Número de mascotas");
        }
    }

    private void cargarUltimosRegistros() {
        if (tablaUltimos == null) {
            return;
        }

        listaUltimos.clear();

        String sql = "SELECT descripcion, fecha FROM ("
                + " SELECT CONCAT('Mascota: ', nombre, ' - ', raza) AS descripcion, fecha_nacimiento AS fecha "
                + " FROM Mascotas "
                + " UNION ALL "
                + " SELECT CONCAT('Usuario: ', nombre, ' - ', email) AS descripcion, CURDATE() AS fecha "
                + " FROM Usuarios "
                + " UNION ALL "
                + " SELECT CONCAT('Adopción de ', m.nombre, ' - ', u.nombre) AS descripcion, a.fecha_inicio AS fecha "
                + " FROM Alquileres a "
                + " JOIN Mascotas m ON a.id_mascota = m.id_mascota "
                + " JOIN Usuarios u ON a.id_voluntario = u.id_usuario "
                + ") t "
                + "ORDER BY fecha DESC "
                + "LIMIT 5";

        try (Connection conn = ConexionBBDD.getConexion();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            int contador = 0;

            while (rs.next()) {
                java.sql.Date fechaSql = rs.getDate("fecha");
                String fechaStr = "";
                if (fechaSql != null) {
                    fechaStr = fechaSql.toLocalDate().format(fmt);
                }

                String desc = rs.getString("descripcion");
                listaUltimos.add(new UltimoRegistro(desc, fechaStr));
                contador++;
            }
            System.out.println("Últimos registros cargados: " + contador);

        } catch (SQLException e) {
            System.out.println("Error cargando últimos registros: " + e.getMessage());
        }

        tablaUltimos.setItems(listaUltimos);
    }

    private void aplicarAnimacionEntrada() {
        if (stackContenido != null) {
            stackContenido.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(600), stackContenido);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        }
    }

    private void configurarAnimacionesBotones() {
        configurarHoverBoton(btnInicio);
        configurarHoverBoton(btnUsuarios);
        configurarHoverBoton(btnMascotas);
        configurarHoverBoton(btnAdopciones);
        configurarHoverBoton(btnInformes);
        configurarHoverBoton(btnNuevo);
        configurarHoverBoton(btnEditar);
        configurarHoverBoton(btnEliminar);
    }

    private void configurarHoverBoton(Button btn) {
        if (btn == null)
            return;

        btn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });

        btn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    private void animarCambioVista(AnchorPane vista) {
        if (vista == null)
            return;

        vista.setOpacity(0);
        vista.setTranslateX(20);

        FadeTransition fade = new FadeTransition(Duration.millis(400), vista);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(400), vista);
        slide.setFromX(20);
        slide.setToX(0);

        ParallelTransition pt = new ParallelTransition(fade, slide);
        pt.play();
    }

    private void configurarMenuContextualUsuarios() {
        if (tablaUsuarios == null)
            return;

        ContextMenu menuContextual = new ContextMenu();

        MenuItem itemEditar = new MenuItem("_Editar usuario");
        itemEditar.setMnemonicParsing(true);
        itemEditar.setGraphic(crearIcono("/miapp/icons/editar.png", 16));
        itemEditar.setOnAction(e -> mostrarDialogoEditarUsuario());

        MenuItem itemEliminar = new MenuItem("E_liminar usuario");
        itemEliminar.setMnemonicParsing(true);
        itemEliminar.setGraphic(crearIcono("/miapp/icons/eliminar.png", 16));
        itemEliminar.setOnAction(e -> mostrarDialogoEliminarUsuario());

        MenuItem itemVerDetalles = new MenuItem("_Ver detalles");
        itemVerDetalles.setMnemonicParsing(true);
        itemVerDetalles.setGraphic(crearIcono("/miapp/icons/form.png", 16));
        itemVerDetalles.setOnAction(e -> {
            Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                mostrarInfo("Detalles del Usuario",
                        "ID: " + seleccionado.getId() + "\n" +
                                "Nombre: " + seleccionado.getNombre() + "\n" +
                                "Email: " + seleccionado.getEmail() + "\n" +
                                "Teléfono: " + seleccionado.getTelefono() + "\n" +
                                "Dirección: " + seleccionado.getDireccion() + "\n" +
                                "Activo: " + (seleccionado.getActivo() ? "Sí" : "No"));
            }
        });

        menuContextual.getItems().addAll(itemEditar, itemEliminar, new SeparatorMenuItem(), itemVerDetalles);

        tablaUsuarios.setContextMenu(menuContextual);

        tablaUsuarios.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                if (tablaUsuarios.getSelectionModel().getSelectedItem() != null) {
                    mostrarDialogoEditarUsuario();
                }
            }
        });
    }

    private void configurarMenuContextualMascotas() {
        if (tablaMascotas == null)
            return;

        ContextMenu menuContextual = new ContextMenu();

        MenuItem itemEditar = new MenuItem("_Editar mascota");
        itemEditar.setMnemonicParsing(true);
        itemEditar.setGraphic(crearIcono("/miapp/icons/editar.png", 16));
        itemEditar.setOnAction(e -> mostrarDialogoEditarMascota());

        MenuItem itemEliminar = new MenuItem("E_liminar mascota");
        itemEliminar.setMnemonicParsing(true);
        itemEliminar.setGraphic(crearIcono("/miapp/icons/eliminar.png", 16));
        itemEliminar.setOnAction(e -> mostrarDialogoEliminarMascota());

        MenuItem itemVerDetalles = new MenuItem("_Ver detalles");
        itemVerDetalles.setMnemonicParsing(true);
        itemVerDetalles.setGraphic(crearIcono("/miapp/icons/form.png", 16));
        itemVerDetalles.setOnAction(e -> {
            Mascota seleccionada = tablaMascotas.getSelectionModel().getSelectedItem();
            if (seleccionada != null) {
                mostrarInfo("Detalles de la Mascota",
                        "ID: " + seleccionada.getId() + "\n" +
                                "Nombre: " + seleccionada.getNombre() + "\n" +
                                "Especie: " + seleccionada.getEspecie() + "\n" +
                                "Raza: " + seleccionada.getRaza() + "\n" +
                                "Peso: " + seleccionada.getPeso() + " kg\n" +
                                "Estado: " + seleccionada.getEstadoSalud() + "\n" +
                                "Disponible: " + (seleccionada.getDisponible() ? "Sí" : "No"));
            }
        });

        MenuItem itemCambiarDisponibilidad = new MenuItem("_Cambiar disponibilidad");
        itemCambiarDisponibilidad.setMnemonicParsing(true);
        itemCambiarDisponibilidad.setGraphic(crearIcono("/miapp/icons/paw.png", 16));
        itemCambiarDisponibilidad.setOnAction(e -> {
            Mascota seleccionada = tablaMascotas.getSelectionModel().getSelectedItem();
            if (seleccionada != null) {
                boolean nuevoEstado = !seleccionada.getDisponible();
                String sql = "UPDATE Mascotas SET disponible_alquiler = ? WHERE id_mascota = ?";

                try (Connection conn = ConexionBBDD.getConexion();
                        PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setBoolean(1, nuevoEstado);
                    pst.setInt(2, seleccionada.getId());
                    pst.executeUpdate();
                    cargarMascotasDesdeBBDD();
                    mostrarInfo("Éxito", "Disponibilidad actualizada");
                } catch (SQLException ex) {
                    mostrarInfo("Error", "No se pudo actualizar: " + ex.getMessage());
                }
            }
        });

        menuContextual.getItems().addAll(itemEditar, itemEliminar, new SeparatorMenuItem(),
                itemVerDetalles, itemCambiarDisponibilidad);

        tablaMascotas.setContextMenu(menuContextual);

        tablaMascotas.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                if (tablaMascotas.getSelectionModel().getSelectedItem() != null) {
                    mostrarDialogoEditarMascota();
                }
            }
        });
    }

    private void configurarMenuContextualAdopciones() {
        if (tablaAdopciones == null)
            return;

        ContextMenu menuContextual = new ContextMenu();

        MenuItem itemEditar = new MenuItem("_Editar adopción");
        itemEditar.setMnemonicParsing(true);
        itemEditar.setGraphic(crearIcono("/miapp/icons/editar.png", 16));
        itemEditar.setOnAction(e -> mostrarDialogoEditarAdopcion());

        MenuItem itemEliminar = new MenuItem("E_liminar adopción");
        itemEliminar.setMnemonicParsing(true);
        itemEliminar.setGraphic(crearIcono("/miapp/icons/eliminar.png", 16));
        itemEliminar.setOnAction(e -> mostrarDialogoEliminarAdopcion());

        MenuItem itemVerDetalles = new MenuItem("_Ver detalles");
        itemVerDetalles.setMnemonicParsing(true);
        itemVerDetalles.setGraphic(crearIcono("/miapp/icons/form.png", 16));
        itemVerDetalles.setOnAction(e -> {
            AdopcionTabla seleccionada = tablaAdopciones.getSelectionModel().getSelectedItem();
            if (seleccionada != null) {
                String calif = seleccionada.getCalificacion() != null ? seleccionada.getCalificacion().toString()
                        : "Sin calificar";
                mostrarInfo("Detalles de la Adopción",
                        "ID: " + seleccionada.getId() + "\n" +
                                "Mascota: " + seleccionada.getMascota() + "\n" +
                                "Voluntario: " + seleccionada.getVoluntario() + "\n" +
                                "Fecha inicio: " + seleccionada.getFechaInicio() + "\n" +
                                "Fecha fin: " + seleccionada.getFechaFin() + "\n" +
                                "Estado: " + seleccionada.getEstado() + "\n" +
                                "Calificación: " + calif);
            }
        });

        MenuItem itemCambiarEstado = new MenuItem("_Cambiar a finalizado");
        itemCambiarEstado.setMnemonicParsing(true);
        itemCambiarEstado.setGraphic(crearIcono("/miapp/icons/form.png", 16));
        itemCambiarEstado.setOnAction(e -> {
            AdopcionTabla seleccionada = tablaAdopciones.getSelectionModel().getSelectedItem();
            if (seleccionada != null) {
                String sql = "UPDATE Alquileres SET estado = 'finalizado' WHERE id_alquiler = ?";

                try (Connection conn = ConexionBBDD.getConexion();
                        PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setInt(1, seleccionada.getId());
                    pst.executeUpdate();
                    cargarAdopcionesDesdeBBDD();
                    cargarDatosDashboard();
                    mostrarInfo("Éxito", "Estado actualizado a finalizado");
                } catch (SQLException ex) {
                    mostrarInfo("Error", "No se pudo actualizar: " + ex.getMessage());
                }
            }
        });

        menuContextual.getItems().addAll(itemEditar, itemEliminar, new SeparatorMenuItem(),
                itemVerDetalles, itemCambiarEstado);

        tablaAdopciones.setContextMenu(menuContextual);

        tablaAdopciones.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                if (tablaAdopciones.getSelectionModel().getSelectedItem() != null) {
                    mostrarDialogoEditarAdopcion();
                }
            }
        });
    }

    private javafx.scene.image.ImageView crearIcono(String ruta, int tamaño) {
        try {
            javafx.scene.image.ImageView iv = new javafx.scene.image.ImageView(
                new Image(getClass().getResourceAsStream(ruta)));
            iv.setFitWidth(tamaño);
            iv.setFitHeight(tamaño);
            iv.setPreserveRatio(true);
            return iv;
        } catch (Exception e) {
            return null;
        }
    }

    private void configurarBuscadores() {
        // Configurar autocompletado y búsqueda para usuarios
        if (comboBuscarUsuarioNombre != null) {
            ObservableList<String> nombresUsuarios = FXCollections.observableArrayList();
            configurarBuscadorConAutocompletado(comboBuscarUsuarioNombre, nombresUsuarios,
                () -> listaUsuarios.stream().map(Usuario::getNombre).distinct().toList());
        }
        if (comboBuscarUsuarioEmail != null) {
            ObservableList<String> emailsUsuarios = FXCollections.observableArrayList();
            configurarBuscadorConAutocompletado(comboBuscarUsuarioEmail, emailsUsuarios,
                () -> listaUsuarios.stream().map(Usuario::getEmail).distinct().toList());
        }
        if (comboBuscarUsuarioTelefono != null) {
            ObservableList<String> telefonosUsuarios = FXCollections.observableArrayList();
            configurarBuscadorConAutocompletado(comboBuscarUsuarioTelefono, telefonosUsuarios,
                () -> listaUsuarios.stream().map(Usuario::getTelefono).distinct().toList());
        }
        if (comboBuscarUsuarioDireccion != null) {
            ObservableList<String> direccionesUsuarios = FXCollections.observableArrayList();
            configurarBuscadorConAutocompletado(comboBuscarUsuarioDireccion, direccionesUsuarios,
                () -> listaUsuarios.stream().map(Usuario::getDireccion).distinct().toList());
        }
        if (btnLimpiarUsuarios != null) {
            btnLimpiarUsuarios.setOnAction(e -> buscarUsuarios());
        }

        // Configurar autocompletado y búsqueda para mascotas
        if (comboBuscarMascotaNombre != null) {
            ObservableList<String> nombresMascotas = FXCollections.observableArrayList();
            configurarBuscadorConAutocompletado(comboBuscarMascotaNombre, nombresMascotas,
                () -> listaMascotas.stream().map(Mascota::getNombre).distinct().toList());
        }
        if (comboBuscarMascotaEspecie != null) {
            ObservableList<String> especiesMascotas = FXCollections.observableArrayList();
            configurarBuscadorConAutocompletado(comboBuscarMascotaEspecie, especiesMascotas,
                () -> listaMascotas.stream().map(Mascota::getEspecie).distinct().toList());
        }
        if (comboBuscarMascotaRaza != null) {
            ObservableList<String> razasMascotas = FXCollections.observableArrayList();
            configurarBuscadorConAutocompletado(comboBuscarMascotaRaza, razasMascotas,
                () -> listaMascotas.stream().map(Mascota::getRaza).distinct().toList());
        }
        if (comboBuscarMascotaEstadoSalud != null) {
            ObservableList<String> estadosSaludMascotas = FXCollections.observableArrayList();
            configurarBuscadorConAutocompletado(comboBuscarMascotaEstadoSalud, estadosSaludMascotas,
                () -> listaMascotas.stream().map(Mascota::getEstadoSalud).distinct().toList());
        }
        if (btnLimpiarMascotas != null) {
            btnLimpiarMascotas.setOnAction(e -> buscarMascotas());
        }

        // Configurar autocompletado y búsqueda para adopciones
        if (comboBuscarAdopcionMascota != null) {
            ObservableList<String> mascotasAdopciones = FXCollections.observableArrayList();
            configurarBuscadorConAutocompletado(comboBuscarAdopcionMascota, mascotasAdopciones,
                () -> listaAdopciones.stream().map(AdopcionTabla::getMascota).distinct().toList());
        }
        if (comboBuscarAdopcionVoluntario != null) {
            ObservableList<String> voluntariosAdopciones = FXCollections.observableArrayList();
            configurarBuscadorConAutocompletado(comboBuscarAdopcionVoluntario, voluntariosAdopciones,
                () -> listaAdopciones.stream().map(AdopcionTabla::getVoluntario).distinct().toList());
        }
        if (comboBuscarAdopcionEstado != null) {
            ObservableList<String> estadosAdopciones = FXCollections.observableArrayList();
            configurarBuscadorConAutocompletado(comboBuscarAdopcionEstado, estadosAdopciones,
                () -> listaAdopciones.stream().map(AdopcionTabla::getEstado).distinct().toList());
        }
        if (comboBuscarAdopcionCalificacion != null) {
            ObservableList<String> calificacionesAdopciones = FXCollections.observableArrayList();
            configurarBuscadorConAutocompletado(comboBuscarAdopcionCalificacion, calificacionesAdopciones,
                () -> listaAdopciones.stream()
                    .map(a -> a.getCalificacion() != null ? a.getCalificacion().toString() : null)
                    .filter(c -> c != null)
                    .distinct()
                    .toList());
        }
        if (btnLimpiarAdopciones != null) {
            btnLimpiarAdopciones.setOnAction(e -> buscarAdopciones());
        }
    }

    private void recargarDatosBuscadores(Seccion seccion) {
        switch (seccion) {
            case USUARIOS:
                recargarBuscadoresUsuarios();
                break;
            case MASCOTAS:
                recargarBuscadoresMascotas();
                break;
            case ADOPCIONES:
                recargarBuscadoresAdopciones();
                break;
            default:
                break;
        }
    }

    private void recargarBuscadoresUsuarios() {
        if (comboBuscarUsuarioNombre != null && comboBuscarUsuarioNombre.getItems() instanceof FilteredList) {
            @SuppressWarnings("unchecked")
            FilteredList<String> filteredItems = (FilteredList<String>) comboBuscarUsuarioNombre.getItems();
            @SuppressWarnings("unchecked")
            ObservableList<String> source = (ObservableList<String>) filteredItems.getSource();
            source.clear();
            source.addAll(listaUsuarios.stream().map(Usuario::getNombre).distinct().toList());
        }
        if (comboBuscarUsuarioEmail != null && comboBuscarUsuarioEmail.getItems() instanceof FilteredList) {
            @SuppressWarnings("unchecked")
            FilteredList<String> filteredItems = (FilteredList<String>) comboBuscarUsuarioEmail.getItems();
            @SuppressWarnings("unchecked")
            ObservableList<String> source = (ObservableList<String>) filteredItems.getSource();
            source.clear();
            source.addAll(listaUsuarios.stream().map(Usuario::getEmail).distinct().toList());
        }
        if (comboBuscarUsuarioTelefono != null && comboBuscarUsuarioTelefono.getItems() instanceof FilteredList) {
            @SuppressWarnings("unchecked")
            FilteredList<String> filteredItems = (FilteredList<String>) comboBuscarUsuarioTelefono.getItems();
            @SuppressWarnings("unchecked")
            ObservableList<String> source = (ObservableList<String>) filteredItems.getSource();
            source.clear();
            source.addAll(listaUsuarios.stream().map(Usuario::getTelefono).distinct().toList());
        }
        if (comboBuscarUsuarioDireccion != null && comboBuscarUsuarioDireccion.getItems() instanceof FilteredList) {
            @SuppressWarnings("unchecked")
            FilteredList<String> filteredItems = (FilteredList<String>) comboBuscarUsuarioDireccion.getItems();
            @SuppressWarnings("unchecked")
            ObservableList<String> source = (ObservableList<String>) filteredItems.getSource();
            source.clear();
            source.addAll(listaUsuarios.stream().map(Usuario::getDireccion).distinct().toList());
        }
    }

    private void recargarBuscadoresMascotas() {
        if (comboBuscarMascotaNombre != null && comboBuscarMascotaNombre.getItems() instanceof FilteredList) {
            @SuppressWarnings("unchecked")
            FilteredList<String> filteredItems = (FilteredList<String>) comboBuscarMascotaNombre.getItems();
            @SuppressWarnings("unchecked")
            ObservableList<String> source = (ObservableList<String>) filteredItems.getSource();
            source.clear();
            source.addAll(listaMascotas.stream().map(Mascota::getNombre).distinct().toList());
        }
        if (comboBuscarMascotaEspecie != null && comboBuscarMascotaEspecie.getItems() instanceof FilteredList) {
            @SuppressWarnings("unchecked")
            FilteredList<String> filteredItems = (FilteredList<String>) comboBuscarMascotaEspecie.getItems();
            @SuppressWarnings("unchecked")
            ObservableList<String> source = (ObservableList<String>) filteredItems.getSource();
            source.clear();
            source.addAll(listaMascotas.stream().map(Mascota::getEspecie).distinct().toList());
        }
        if (comboBuscarMascotaRaza != null && comboBuscarMascotaRaza.getItems() instanceof FilteredList) {
            @SuppressWarnings("unchecked")
            FilteredList<String> filteredItems = (FilteredList<String>) comboBuscarMascotaRaza.getItems();
            @SuppressWarnings("unchecked")
            ObservableList<String> source = (ObservableList<String>) filteredItems.getSource();
            source.clear();
            source.addAll(listaMascotas.stream().map(Mascota::getRaza).distinct().toList());
        }
        if (comboBuscarMascotaEstadoSalud != null && comboBuscarMascotaEstadoSalud.getItems() instanceof FilteredList) {
            @SuppressWarnings("unchecked")
            FilteredList<String> filteredItems = (FilteredList<String>) comboBuscarMascotaEstadoSalud.getItems();
            @SuppressWarnings("unchecked")
            ObservableList<String> source = (ObservableList<String>) filteredItems.getSource();
            source.clear();
            source.addAll(listaMascotas.stream().map(Mascota::getEstadoSalud).distinct().toList());
        }
    }

    private void recargarBuscadoresAdopciones() {
        if (comboBuscarAdopcionMascota != null && comboBuscarAdopcionMascota.getItems() instanceof FilteredList) {
            @SuppressWarnings("unchecked")
            FilteredList<String> filteredItems = (FilteredList<String>) comboBuscarAdopcionMascota.getItems();
            @SuppressWarnings("unchecked")
            ObservableList<String> source = (ObservableList<String>) filteredItems.getSource();
            source.clear();
            source.addAll(listaAdopciones.stream().map(AdopcionTabla::getMascota).distinct().toList());
        }
        if (comboBuscarAdopcionVoluntario != null && comboBuscarAdopcionVoluntario.getItems() instanceof FilteredList) {
            @SuppressWarnings("unchecked")
            FilteredList<String> filteredItems = (FilteredList<String>) comboBuscarAdopcionVoluntario.getItems();
            @SuppressWarnings("unchecked")
            ObservableList<String> source = (ObservableList<String>) filteredItems.getSource();
            source.clear();
            source.addAll(listaAdopciones.stream().map(AdopcionTabla::getVoluntario).distinct().toList());
        }
        if (comboBuscarAdopcionEstado != null && comboBuscarAdopcionEstado.getItems() instanceof FilteredList) {
            @SuppressWarnings("unchecked")
            FilteredList<String> filteredItems = (FilteredList<String>) comboBuscarAdopcionEstado.getItems();
            @SuppressWarnings("unchecked")
            ObservableList<String> source = (ObservableList<String>) filteredItems.getSource();
            source.clear();
            source.addAll(listaAdopciones.stream().map(AdopcionTabla::getEstado).distinct().toList());
        }
        if (comboBuscarAdopcionCalificacion != null && comboBuscarAdopcionCalificacion.getItems() instanceof FilteredList) {
            @SuppressWarnings("unchecked")
            FilteredList<String> filteredItems = (FilteredList<String>) comboBuscarAdopcionCalificacion.getItems();
            @SuppressWarnings("unchecked")
            ObservableList<String> source = (ObservableList<String>) filteredItems.getSource();
            source.clear();
            source.addAll(listaAdopciones.stream()
                .map(a -> a.getCalificacion() != null ? a.getCalificacion().toString() : null)
                .filter(c -> c != null)
                .distinct()
                .toList());
        }
    }

    private void configurarBuscadorConAutocompletado(ComboBox<String> combo,
            ObservableList<String> items,
            java.util.function.Supplier<java.util.List<String>> dataSupplier) {

        // Cargar datos iniciales
        items.setAll(dataSupplier.get());

        FilteredList<String> filteredItems = new FilteredList<>(items, p -> true);
        combo.setItems(filteredItems);
        combo.setEditable(true);

        TextField editor = combo.getEditor();

        // Bloquear espacios
        editor.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (event.getCharacter().equals(" ")) {
                event.consume();
            }
        });

        // Listener para el autocompletado
        editor.textProperty().addListener((obs, oldValue, newValue) -> {
            // Prevenir recursión: si ya estamos cambiando programáticamente, salir
            if (cambiandoProgramaticamente) {
                return;
            }

            final String selected = combo.getSelectionModel().getSelectedItem();

            // Si el campo está vacío, oculta y restaura el filtro
            if (newValue == null || newValue.trim().isEmpty()) {
                try {
                    cambiandoProgramaticamente = true;
                    filteredItems.setPredicate(p -> true);
                    combo.hide();
                } finally {
                    cambiandoProgramaticamente = false;
                }
                return;
            }

            // Si no hay nada seleccionado o he escrito algo cambiando lo seleccionado
            if (selected == null || !selected.equals(newValue)) {
                try {
                    cambiandoProgramaticamente = true;
                    filteredItems.setPredicate(p ->
                        p.toLowerCase().startsWith(newValue.toLowerCase().trim()));
                    combo.setVisibleRowCount(5);
                    combo.show();
                } finally {
                    cambiandoProgramaticamente = false;
                }
            } else {
                // Ocurre si he seleccionado algo de la lista, quita el filtro
                try {
                    cambiandoProgramaticamente = true;
                    filteredItems.setPredicate(p -> true);
                } finally {
                    cambiandoProgramaticamente = false;
                }
            }
        });

        // Posicionar cursor al final al seleccionar
        combo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Prevenir recursión
            if (cambiandoProgramaticamente) {
                return;
            }

            if (newValue != null) {
                Platform.runLater(() -> {
                    try {
                        cambiandoProgramaticamente = true;
                        editor.positionCaret(editor.getText().length());
                    } finally {
                        cambiandoProgramaticamente = false;
                    }
                });
            }
        });
    }

    private void buscarUsuarios() {
        String nombre = comboBuscarUsuarioNombre != null && comboBuscarUsuarioNombre.getValue() != null
            ? comboBuscarUsuarioNombre.getValue().trim().toLowerCase() : "";
        String email = comboBuscarUsuarioEmail != null && comboBuscarUsuarioEmail.getValue() != null
            ? comboBuscarUsuarioEmail.getValue().trim().toLowerCase() : "";
        String telefono = comboBuscarUsuarioTelefono != null && comboBuscarUsuarioTelefono.getValue() != null
            ? comboBuscarUsuarioTelefono.getValue().trim().toLowerCase() : "";
        String direccion = comboBuscarUsuarioDireccion != null && comboBuscarUsuarioDireccion.getValue() != null
            ? comboBuscarUsuarioDireccion.getValue().trim().toLowerCase() : "";

        ObservableList<Usuario> filtrados = listaUsuarios.filtered(usuario -> {
            boolean coincide = true;

            if (!nombre.isEmpty()) {
                coincide = coincide && usuario.getNombre().toLowerCase().contains(nombre);
            }
            if (!email.isEmpty()) {
                coincide = coincide && usuario.getEmail().toLowerCase().contains(email);
            }
            if (!telefono.isEmpty()) {
                coincide = coincide && usuario.getTelefono().toLowerCase().contains(telefono);
            }
            if (!direccion.isEmpty()) {
                coincide = coincide && usuario.getDireccion().toLowerCase().contains(direccion);
            }

            return coincide;
        });

        tablaUsuarios.setItems(filtrados);
    }

    private void limpiarBusquedaUsuarios() {
        if (comboBuscarUsuarioNombre != null) {
            comboBuscarUsuarioNombre.setValue(null);
            comboBuscarUsuarioNombre.getEditor().clear();
        }
        if (comboBuscarUsuarioEmail != null) {
            comboBuscarUsuarioEmail.setValue(null);
            comboBuscarUsuarioEmail.getEditor().clear();
        }
        if (comboBuscarUsuarioTelefono != null) {
            comboBuscarUsuarioTelefono.setValue(null);
            comboBuscarUsuarioTelefono.getEditor().clear();
        }
        if (comboBuscarUsuarioDireccion != null) {
            comboBuscarUsuarioDireccion.setValue(null);
            comboBuscarUsuarioDireccion.getEditor().clear();
        }
        tablaUsuarios.setItems(listaUsuarios);
    }

    private void buscarMascotas() {
        String nombre = comboBuscarMascotaNombre != null && comboBuscarMascotaNombre.getValue() != null
            ? comboBuscarMascotaNombre.getValue().trim().toLowerCase() : "";
        String especie = comboBuscarMascotaEspecie != null && comboBuscarMascotaEspecie.getValue() != null
            ? comboBuscarMascotaEspecie.getValue().trim().toLowerCase() : "";
        String raza = comboBuscarMascotaRaza != null && comboBuscarMascotaRaza.getValue() != null
            ? comboBuscarMascotaRaza.getValue().trim().toLowerCase() : "";
        String estadoSalud = comboBuscarMascotaEstadoSalud != null && comboBuscarMascotaEstadoSalud.getValue() != null
            ? comboBuscarMascotaEstadoSalud.getValue().trim().toLowerCase() : "";

        ObservableList<Mascota> filtrados = listaMascotas.filtered(mascota -> {
            boolean coincide = true;

            if (!nombre.isEmpty()) {
                coincide = coincide && mascota.getNombre().toLowerCase().contains(nombre);
            }
            if (!especie.isEmpty()) {
                coincide = coincide && mascota.getEspecie().toLowerCase().contains(especie);
            }
            if (!raza.isEmpty()) {
                coincide = coincide && mascota.getRaza().toLowerCase().contains(raza);
            }
            if (!estadoSalud.isEmpty()) {
                coincide = coincide && mascota.getEstadoSalud().toLowerCase().contains(estadoSalud);
            }

            return coincide;
        });

        tablaMascotas.setItems(filtrados);
    }

    private void limpiarBusquedaMascotas() {
        if (comboBuscarMascotaNombre != null) {
            comboBuscarMascotaNombre.setValue(null);
            comboBuscarMascotaNombre.getEditor().clear();
        }
        if (comboBuscarMascotaEspecie != null) {
            comboBuscarMascotaEspecie.setValue(null);
            comboBuscarMascotaEspecie.getEditor().clear();
        }
        if (comboBuscarMascotaRaza != null) {
            comboBuscarMascotaRaza.setValue(null);
            comboBuscarMascotaRaza.getEditor().clear();
        }
        if (comboBuscarMascotaEstadoSalud != null) {
            comboBuscarMascotaEstadoSalud.setValue(null);
            comboBuscarMascotaEstadoSalud.getEditor().clear();
        }
        tablaMascotas.setItems(listaMascotas);
    }

    private void buscarAdopciones() {
        String mascota = comboBuscarAdopcionMascota != null && comboBuscarAdopcionMascota.getValue() != null
            ? comboBuscarAdopcionMascota.getValue().trim().toLowerCase() : "";
        String voluntario = comboBuscarAdopcionVoluntario != null && comboBuscarAdopcionVoluntario.getValue() != null
            ? comboBuscarAdopcionVoluntario.getValue().trim().toLowerCase() : "";
        String estado = comboBuscarAdopcionEstado != null && comboBuscarAdopcionEstado.getValue() != null
            ? comboBuscarAdopcionEstado.getValue().trim().toLowerCase() : "";
        String calificacion = comboBuscarAdopcionCalificacion != null && comboBuscarAdopcionCalificacion.getValue() != null
            ? comboBuscarAdopcionCalificacion.getValue().trim().toLowerCase() : "";

        ObservableList<AdopcionTabla> filtrados = listaAdopciones.filtered(adopcion -> {
            boolean coincide = true;

            if (!mascota.isEmpty()) {
                coincide = coincide && adopcion.getMascota().toLowerCase().contains(mascota);
            }
            if (!voluntario.isEmpty()) {
                coincide = coincide && adopcion.getVoluntario().toLowerCase().contains(voluntario);
            }
            if (!estado.isEmpty()) {
                coincide = coincide && adopcion.getEstado().toLowerCase().contains(estado);
            }
            if (!calificacion.isEmpty()) {
                String califAdopcion = adopcion.getCalificacion() != null
                    ? adopcion.getCalificacion().toString()
                    : "";
                coincide = coincide && califAdopcion.contains(calificacion);
            }

            return coincide;
        });

        tablaAdopciones.setItems(filtrados);
    }

    private void limpiarBusquedaAdopciones() {
        if (comboBuscarAdopcionMascota != null) {
            comboBuscarAdopcionMascota.setValue(null);
            comboBuscarAdopcionMascota.getEditor().clear();
        }
        if (comboBuscarAdopcionVoluntario != null) {
            comboBuscarAdopcionVoluntario.setValue(null);
            comboBuscarAdopcionVoluntario.getEditor().clear();
        }
        if (comboBuscarAdopcionEstado != null) {
            comboBuscarAdopcionEstado.setValue(null);
            comboBuscarAdopcionEstado.getEditor().clear();
        }
        if (comboBuscarAdopcionCalificacion != null) {
            comboBuscarAdopcionCalificacion.setValue(null);
            comboBuscarAdopcionCalificacion.getEditor().clear();
        }
        tablaAdopciones.setItems(listaAdopciones);
    }

    private void mostrarErrorConexion() {
        if (lblErrorConexionUsuarios != null) {
            lblErrorConexionUsuarios.setVisible(true);
            lblErrorConexionUsuarios.setManaged(true);
        }
        if (lblErrorConexionMascotas != null) {
            lblErrorConexionMascotas.setVisible(true);
            lblErrorConexionMascotas.setManaged(true);
        }
        if (lblErrorConexionAdopciones != null) {
            lblErrorConexionAdopciones.setVisible(true);
            lblErrorConexionAdopciones.setManaged(true);
        }
    }

    private void ocultarErrorConexion() {
        if (lblErrorConexionUsuarios != null) {
            lblErrorConexionUsuarios.setVisible(false);
            lblErrorConexionUsuarios.setManaged(false);
        }
        if (lblErrorConexionMascotas != null) {
            lblErrorConexionMascotas.setVisible(false);
            lblErrorConexionMascotas.setManaged(false);
        }
        if (lblErrorConexionAdopciones != null) {
            lblErrorConexionAdopciones.setVisible(false);
            lblErrorConexionAdopciones.setManaged(false);
        }
    }
}
