package com.javafx.proyecto;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.application.Platform;
import javafx.scene.Node;

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

    @FXML
    private void initialize() {
        // Configurar botones del menú lateral
        btnInicio.setOnAction(e -> mostrarVista(vistaInicio, btnInicio, Seccion.INICIO));
        btnUsuarios.setOnAction(e -> mostrarVista(vistaUsuarios, btnUsuarios, Seccion.USUARIOS));
        btnMascotas.setOnAction(e -> mostrarVista(vistaMascotas, btnMascotas, Seccion.MASCOTAS));
        btnAdopciones.setOnAction(e -> mostrarVista(vistaAdopciones, btnAdopciones, Seccion.ADOPCIONES));
        btnInformes.setOnAction(e -> mostrarVista(vistaInformes, btnInformes, Seccion.INFORMES));

        // Botones CRUD
        btnNuevo.setOnAction(e -> accionCrud("Nuevo"));
        btnEditar.setOnAction(e -> accionCrud("Editar"));
        btnEliminar.setOnAction(e -> accionCrud("Eliminar"));

        // Configurar las tablas
        configurarColumnasUsuarios();
        configurarColumnasMascotas();
        configurarColumnasAdopciones();
        configurarTablaUltimos();

        // Cargar datos desde la BD
        cargarUsuariosDesdeBBDD();
        cargarMascotasDesdeBBDD();
        cargarAdopcionesDesdeBBDD();
        cargarDatosDashboard();
        rellenarGraficaEspeciesDesdeBBDD();
        cargarUltimosRegistros();

        // Mostrar la vista de inicio por defecto
        mostrarVista(vistaInicio, btnInicio, Seccion.INICIO);
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

        // Si estamos en la vista de inicio, ocultar los botones CRUD completamente
        if (seccionActual == Seccion.INICIO) {
            barraCrud.setVisible(false);
            barraCrud.setManaged(false);
            return;
        } else {
            // En el resto de vistas, mostrar la barra CRUD
            barraCrud.setVisible(true);
            barraCrud.setManaged(true);
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
        alerta.showAndWait();
    }

    // ===================== USUARIOS =====================
    private void mostrarDialogoNuevoUsuario() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nuevo usuario");
        dialog.setHeaderText("Introduce los datos del nuevo usuario");

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = crearGridBasico();

        TextField txtNombre = new TextField();
        TextField txtEmail = new TextField();
        TextField txtTelefono = new TextField();
        TextField txtDireccion = new TextField();
        CheckBox chkActivo = new CheckBox("Activo");
        chkActivo.setSelected(true);

        grid.addRow(0, new Label("Nombre:"), txtNombre);
        grid.addRow(1, new Label("Email:"), txtEmail);
        grid.addRow(2, new Label("Teléfono:"), txtTelefono);
        grid.addRow(3, new Label("Dirección:"), txtDireccion);
        grid.addRow(4, new Label(""), chkActivo);

        dialog.getDialogPane().setContent(grid);

        // VALIDACIÓN CON CONTROLSFX - Recopilar validadores
        java.util.List<ValidationSupport> validadores = new java.util.ArrayList<>();
        
        validadores.add(ValidadorForms.validarNombreUsuario(txtNombre));
        validadores.add(ValidadorForms.validarEmailUsuario(txtEmail));
        validadores.add(ValidadorForms.validarTelefonoUsuario(txtTelefono));
        validadores.add(ValidadorForms.validarCampoObligatorio(txtDireccion, "Dirección"));

        // Inicializar decoraciones gráficas
        javafx.application.Platform.runLater(() -> {
            for (ValidationSupport vs : validadores) {
                vs.initInitialDecoration();
            }
        });

        // Deshabilitar Guardar mientras haya errores
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

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = crearGridBasico();

        TextField txtNombre = new TextField(seleccionado.getNombre());
        TextField txtEmail = new TextField(seleccionado.getEmail());
        TextField txtTelefono = new TextField(seleccionado.getTelefono());
        TextField txtDireccion = new TextField(seleccionado.getDireccion());
        CheckBox chkActivo = new CheckBox("Activo");
        chkActivo.setSelected(true);

        grid.addRow(0, new Label("Nombre:"), txtNombre);
        grid.addRow(1, new Label("Email:"), txtEmail);
        grid.addRow(2, new Label("Teléfono:"), txtTelefono);
        grid.addRow(3, new Label("Dirección:"), txtDireccion);
        grid.addRow(4, new Label(""), chkActivo);

        dialog.getDialogPane().setContent(grid);

        // VALIDACIÓN CON CONTROLSFX - Recopilar validadores
        java.util.List<ValidationSupport> validadores = new java.util.ArrayList<>();
        
        validadores.add(ValidadorForms.validarNombreUsuario(txtNombre));
        validadores.add(ValidadorForms.validarEmailUsuario(txtEmail));
        validadores.add(ValidadorForms.validarTelefonoUsuario(txtTelefono));
        validadores.add(ValidadorForms.validarCampoObligatorio(txtDireccion, "Dirección"));

        // Inicializar decoraciones gráficas
        javafx.application.Platform.runLater(() -> {
            for (ValidationSupport vs : validadores) {
                vs.initInitialDecoration();
            }
        });

        // Deshabilitar Guardar mientras haya errores
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

    // ===================== MASCOTAS =====================
    private void mostrarDialogoNuevoMascota() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nueva mascota");
        dialog.setHeaderText("Introduce los datos de la mascota");

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = crearGridBasico();

        TextField txtNombre = new TextField();
        TextField txtEspecie = new TextField();
        TextField txtRaza = new TextField();
        DatePicker dpFechaNac = new DatePicker();
        TextField txtPeso = new TextField();
        TextField txtEstado = new TextField();
        CheckBox chkDisponible = new CheckBox("Disponible para alquiler");
        chkDisponible.setSelected(true);

        grid.addRow(0, new Label("Nombre:"), txtNombre);
        grid.addRow(1, new Label("Especie:"), txtEspecie);
        grid.addRow(2, new Label("Raza:"), txtRaza);
        grid.addRow(3, new Label("Fecha nac.:"), dpFechaNac);
        grid.addRow(4, new Label("Peso (kg):"), txtPeso);
        grid.addRow(5, new Label("Estado salud:"), txtEstado);
        grid.addRow(6, new Label(""), chkDisponible);

        dialog.getDialogPane().setContent(grid);

        // VALIDACIÓN CON CONTROLSFX - Recopilar validadores
        java.util.List<ValidationSupport> validadores = new java.util.ArrayList<>();
        
        validadores.add(ValidadorForms.validarNombreUsuario(txtNombre));
        validadores.add(ValidadorForms.validarEspecieMascota(txtEspecie));
        validadores.add(ValidadorForms.validarRazaMascota(txtRaza));
        validadores.add(ValidadorForms.validarPesoMascota(txtPeso));
        validadores.add(ValidadorForms.validarEstadoSaludMascota(txtEstado));

        // Inicializar decoraciones gráficas
        javafx.application.Platform.runLater(() -> {
            for (ValidationSupport vs : validadores) {
                vs.initInitialDecoration();
            }
        });

        // Deshabilitar Guardar mientras haya errores
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

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = crearGridBasico();

        TextField txtNombre = new TextField(seleccionada.getNombre());
        TextField txtEspecie = new TextField(seleccionada.getEspecie());
        TextField txtRaza = new TextField(seleccionada.getRaza());
        DatePicker dpFechaNac = new DatePicker(seleccionada.getFechaNacimiento());
        TextField txtPeso = new TextField(
                seleccionada.getPeso() != null ? seleccionada.getPeso().toString() : "");
        TextField txtEstado = new TextField(seleccionada.getEstadoSalud());
        CheckBox chkDisponible = new CheckBox("Disponible para alquiler");
        chkDisponible.setSelected(true);

        grid.addRow(0, new Label("Nombre:"), txtNombre);
        grid.addRow(1, new Label("Especie:"), txtEspecie);
        grid.addRow(2, new Label("Raza:"), txtRaza);
        grid.addRow(3, new Label("Fecha nac.:"), dpFechaNac);
        grid.addRow(4, new Label("Peso (kg):"), txtPeso);
        grid.addRow(5, new Label("Estado salud:"), txtEstado);
        grid.addRow(6, new Label(""), chkDisponible);

        dialog.getDialogPane().setContent(grid);

        // VALIDACIÓN CON CONTROLSFX - Recopilar validadores
        java.util.List<ValidationSupport> validadores = new java.util.ArrayList<>();
        
        validadores.add(ValidadorForms.validarNombreUsuario(txtNombre));
        validadores.add(ValidadorForms.validarEspecieMascota(txtEspecie));
        validadores.add(ValidadorForms.validarRazaMascota(txtRaza));
        validadores.add(ValidadorForms.validarPesoMascota(txtPeso));
        validadores.add(ValidadorForms.validarEstadoSaludMascota(txtEstado));

        // Inicializar decoraciones gráficas
        javafx.application.Platform.runLater(() -> {
            for (ValidationSupport vs : validadores) {
                vs.initInitialDecoration();
            }
        });

        // Deshabilitar Guardar mientras haya errores
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

    // ===================== ADOPCIONES =====================
    private void mostrarDialogoNuevoAdopcion() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nueva adopción");
        dialog.setHeaderText("Introduce los datos de la adopción");

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = crearGridBasico();

        ComboBox<Mascota> comboMascota = new ComboBox<>(listaMascotas);
        ComboBox<Usuario> comboVoluntario = new ComboBox<>(listaUsuarios);
        DatePicker dpInicio = new DatePicker(LocalDate.now());
        DatePicker dpFin = new DatePicker();
        TextField txtEstado = new TextField("activo");
        TextField txtCalif = new TextField();

        grid.addRow(0, new Label("Mascota:"), comboMascota);
        grid.addRow(1, new Label("Voluntario:"), comboVoluntario);
        grid.addRow(2, new Label("Fecha inicio:"), dpInicio);
        grid.addRow(3, new Label("Fecha fin:"), dpFin);
        grid.addRow(4, new Label("Estado:"), txtEstado);
        grid.addRow(5, new Label("Calificación (1-5):"), txtCalif);

        dialog.getDialogPane().setContent(grid);

        // VALIDACIÓN CON CONTROLSFX - Recopilar validadores
        java.util.List<ValidationSupport> validadores = new java.util.ArrayList<>();
        
        validadores.add(ValidadorForms.validarComboBoxObligatorio(comboMascota, "una mascota"));
        validadores.add(ValidadorForms.validarComboBoxObligatorio(comboVoluntario, "un voluntario"));
        validadores.add(ValidadorForms.validarEstadoAdopcion(txtEstado));
        validadores.add(ValidadorForms.validarCalificacionAdopcion(txtCalif));
        validadores.add(ValidadorForms.validarRangoFechasAdopcion(dpInicio, dpFin));

        // Inicializar decoraciones gráficas
        javafx.application.Platform.runLater(() -> {
            for (ValidationSupport vs : validadores) {
                vs.initInitialDecoration();
            }
        });

        // Deshabilitar Guardar mientras haya errores
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

    ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

    GridPane grid = crearGridBasico();

    TextField txtMascota = new TextField(seleccionada.getMascota());
    txtMascota.setEditable(false);
    TextField txtVoluntario = new TextField(seleccionada.getVoluntario());
    txtVoluntario.setEditable(false);
    DatePicker dpInicio = new DatePicker(seleccionada.getFechaInicio());
    DatePicker dpFin = new DatePicker(seleccionada.getFechaFin());
    TextField txtEstado = new TextField(seleccionada.getEstado());
    TextField txtCalif = new TextField(
            seleccionada.getCalificacion() != null ? seleccionada.getCalificacion().toString() : "");

    grid.addRow(0, new Label("Mascota:"), txtMascota);
    grid.addRow(1, new Label("Voluntario:"), txtVoluntario);
    grid.addRow(2, new Label("Fecha inicio:"), dpInicio);
    grid.addRow(3, new Label("Fecha fin:"), dpFin);
    grid.addRow(4, new Label("Estado:"), txtEstado);
    grid.addRow(5, new Label("Calificación:"), txtCalif);

    dialog.getDialogPane().setContent(grid);

    // VALIDACIÓN CON CONTROLSFX - Recopilar validadores
    java.util.List<ValidationSupport> validadores = new java.util.ArrayList<>();
    
    validadores.add(ValidadorForms.validarEstadoAdopcion(txtEstado));
    validadores.add(ValidadorForms.validarCalificacionAdopcion(txtCalif));
    validadores.add(ValidadorForms.validarRangoFechasAdopcion(dpInicio, dpFin));

    // Inicializar decoraciones gráficas
    javafx.application.Platform.runLater(() -> {
        for (ValidationSupport vs : validadores) {
            vs.initInitialDecoration();
        }
    });

    // Deshabilitar Guardar mientras haya errores
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

    // ===================== INFORMES (PLACEHOLDER) =====================
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

        confirm.showAndWait();
    }

    // ===================== UTILIDADES =====================
    private GridPane crearGridBasico() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        return grid;
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

        try (Connection conn = ConexionBBDD.getConexion(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

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

        } catch (SQLException e) {
            System.out.println("Error cargando usuarios: " + e.getMessage());
        }

        tablaUsuarios.setItems(listaUsuarios);
    }

    private void cargarMascotasDesdeBBDD() {
        listaMascotas.clear();

        String sql = "SELECT id_mascota, nombre, especie, raza, fecha_nacimiento, "
                + "peso, estado_salud, disponible_alquiler "
                + "FROM Mascotas";

        try (Connection conn = ConexionBBDD.getConexion(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

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

        } catch (SQLException e) {
            System.out.println("Error cargando mascotas: " + e.getMessage());
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

        try (Connection conn = ConexionBBDD.getConexion(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

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

        } catch (SQLException e) {
            System.out.println("Error cargando adopciones: " + e.getMessage());
        }

        tablaAdopciones.setItems(listaAdopciones);
    }

    private void cargarDatosDashboard() {
        try (Connection conn = ConexionBBDD.getConexion(); Statement st = conn.createStatement()) {

            ResultSet rsUsuarios = st.executeQuery("SELECT COUNT(*) FROM Usuarios WHERE activo = 1");
            if (rsUsuarios.next()) {
                lblUsuariosActivos.setText(String.valueOf(rsUsuarios.getInt(1)));
            }

            ResultSet rsMascotas = st.executeQuery("SELECT COUNT(*) FROM Mascotas");
            if (rsMascotas.next()) {
                lblMascotasRegistradas.setText(String.valueOf(rsMascotas.getInt(1)));
            }

            ResultSet rsAdop = st.executeQuery("SELECT COUNT(*) FROM Alquileres WHERE estado = 'activo'");
            if (rsAdop.next()) {
                lblAdopcionesActivas.setText(String.valueOf(rsAdop.getInt(1)));
            }

        } catch (SQLException e) {
            System.out.println("Error cargando datos del dashboard: " + e.getMessage());
        }
    }

    private void rellenarGraficaEspeciesDesdeBBDD() {
        graficaEspecies.getData().clear();

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Mascotas por especie");

        String sql = "SELECT especie, COUNT(*) AS total FROM Mascotas GROUP BY especie";

        try (Connection conn = ConexionBBDD.getConexion(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

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

        try (Connection conn = ConexionBBDD.getConexion(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

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
}
