package com.javafx.proyecto.controlador;

import com.javafx.proyecto.bbdd.ConexionBBDD;
import com.javafx.proyecto.modelo.AdopcionTabla;
import com.javafx.proyecto.modelo.CentroVeterinario;
import com.javafx.proyecto.modelo.Mascota;
import com.javafx.proyecto.modelo.UltimoRegistro;
import com.javafx.proyecto.modelo.Usuario;
import com.javafx.proyecto.util.SesionUsuario;
import com.javafx.proyecto.util.UIUtils;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PrincipalController {

    // --- Vistas y navegación ---
    @FXML private StackPane stackContenido;
    @FXML private AnchorPane vistaInicio;
    @FXML private AnchorPane vistaUsuarios;
    @FXML private AnchorPane vistaMascotas;
    @FXML private AnchorPane vistaAdopciones;
    @FXML private AnchorPane vistaInformes;
    @FXML private AnchorPane vistaCentros;
    @FXML private HBox barraCrud;

    // --- Botones de navegación ---
    @FXML private Button btnInicio;
    @FXML private Button btnUsuarios;
    @FXML private Button btnCentros;
    @FXML private Button btnMascotas;
    @FXML private Button btnAdopciones;
    @FXML private Button btnInformes;
    @FXML private Button btnChatSoporte;

    // --- Botones CRUD ---
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;

    // --- Dashboard ---
    @FXML private Label lblUsuariosActivos;
    @FXML private Label lblMascotasRegistradas;
    @FXML private Label lblAdopcionesActivas;
    @FXML private Label lblErrorConexionUsuarios;
    @FXML private Label lblErrorConexionMascotas;
    @FXML private Label lblErrorConexionAdopciones;

    @FXML private TableView<UltimoRegistro> tablaUltimos;
    @FXML private TableColumn<UltimoRegistro, String> colUltimoRegistro;
    @FXML private TableColumn<UltimoRegistro, String> colUltimoFecha;
    private final ObservableList<UltimoRegistro> listaUltimos = FXCollections.observableArrayList();

    @FXML private BarChart<String, Number> graficaEspecies;
    @FXML private CategoryAxis ejeXEspecies;
    @FXML private NumberAxis ejeYEspecies;

    // --- Tabla Usuarios (Veterinarios) ---
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Integer> colUsuarioId;
    @FXML private TableColumn<Usuario, String> colUsuarioNombre;
    @FXML private TableColumn<Usuario, String> colUsuarioEmail;
    @FXML private TableColumn<Usuario, String> colUsuarioCentro;
    @FXML private TableColumn<Usuario, Boolean> colUsuarioActivo;
    @FXML private ComboBox<String> comboBuscarUsuarioNombre;
    @FXML private ComboBox<String> comboBuscarUsuarioEmail;
    @FXML private ComboBox<String> comboBuscarUsuarioCentro;
    @FXML private Button btnLimpiarUsuarios;

    // --- Tabla Mascotas ---
    @FXML private TableView<Mascota> tablaMascotas;
    @FXML private TableColumn<Mascota, Integer> colMascotaId;
    @FXML private TableColumn<Mascota, String> colMascotaNombre;
    @FXML private TableColumn<Mascota, String> colMascotaEspecie;
    @FXML private TableColumn<Mascota, String> colMascotaRaza;
    @FXML private TableColumn<Mascota, LocalDate> colMascotaFechaNac;
    @FXML private TableColumn<Mascota, Double> colMascotaPeso;
    @FXML private TableColumn<Mascota, String> colMascotaEstadoSalud;
    @FXML private TableColumn<Mascota, Boolean> colMascotaDisponible;
    @FXML private ComboBox<String> comboBuscarMascotaNombre;
    @FXML private ComboBox<String> comboBuscarMascotaEspecie;
    @FXML private ComboBox<String> comboBuscarMascotaRaza;
    @FXML private ComboBox<String> comboBuscarMascotaEstadoSalud;
    @FXML private Button btnLimpiarMascotas;

    // --- Tabla Centros ---
    @FXML private TableView<CentroVeterinario> tablaCentros;
    @FXML private TableColumn<CentroVeterinario, Integer> colCentroId;
    @FXML private TableColumn<CentroVeterinario, String> colCentroNombre;
    @FXML private TableColumn<CentroVeterinario, String> colCentroCiudad;
    @FXML private TableColumn<CentroVeterinario, String> colCentroDireccion;
    @FXML private TableColumn<CentroVeterinario, String> colCentroTelefono;
    @FXML private TableColumn<CentroVeterinario, String> colCentroEspecialidad;
    @FXML private ComboBox<String> comboBuscarCentroNombre;
    @FXML private ComboBox<String> comboBuscarCentroCiudad;
    @FXML private Button btnLimpiarCentros;
    @FXML private Label lblErrorConexionCentros;

    // --- Tabla Adopciones ---
    @FXML private TableView<AdopcionTabla> tablaAdopciones;
    @FXML private TableColumn<AdopcionTabla, Integer> colAdopcionId;
    @FXML private TableColumn<AdopcionTabla, String> colAdopcionMascota;
    @FXML private TableColumn<AdopcionTabla, String> colAdopcionVoluntario;
    @FXML private TableColumn<AdopcionTabla, LocalDate> colAdopcionFechaInicio;
    @FXML private TableColumn<AdopcionTabla, LocalDate> colAdopcionFechaFin;
    @FXML private TableColumn<AdopcionTabla, String> colAdopcionEstado;
    @FXML private TableColumn<AdopcionTabla, Integer> colAdopcionCalificacion;
    @FXML private ComboBox<String> comboBuscarAdopcionMascota;
    @FXML private ComboBox<String> comboBuscarAdopcionVoluntario;
    @FXML private ComboBox<String> comboBuscarAdopcionEstado;
    @FXML private ComboBox<String> comboBuscarAdopcionCalificacion;
    @FXML private Button btnLimpiarAdopciones;

    // --- Informes ---
    @FXML private javafx.scene.web.WebView webViewInforme;
    @FXML private ComboBox<String> comboFiltroEstadoInforme;
    @FXML private Button btnMascotasIncrustado;
    @FXML private Button btnMascotasVentana;
    @FXML private Button btnMascotasPdf;
    @FXML private Button btnAdopcionesIncrustado;
    @FXML private Button btnAdopcionesVentana;
    @FXML private Button btnAdopcionesPdf;

    // --- Listas compartidas ---
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
    private final ObservableList<Mascota> listaMascotas = FXCollections.observableArrayList();
    private final ObservableList<AdopcionTabla> listaAdopciones = FXCollections.observableArrayList();
    private final ObservableList<CentroVeterinario> listaCentros = FXCollections.observableArrayList();

    // --- Sub-controladores ---
    private UsuarioCrudController usuarioCtrl;
    private MascotaCrudController mascotaCtrl;
    private AdopcionCrudController adopcionCtrl;
    private InformesController informesCtrl;
    private CentroCrudController centroCtrl;

    // --- Estado ---
    private AnchorPane vistaActual;

    private enum Seccion {
        INICIO, USUARIOS, MASCOTAS, ADOPCIONES, INFORMES, CENTROS
    }

    private Seccion seccionActual = Seccion.INICIO;

    @FXML
    private void initialize() {
        // Crear sub-controladores
        usuarioCtrl = new UsuarioCrudController(
                tablaUsuarios, listaUsuarios,
                colUsuarioId, colUsuarioNombre, colUsuarioEmail,
                colUsuarioCentro, colUsuarioActivo,
                comboBuscarUsuarioNombre, comboBuscarUsuarioEmail,
                comboBuscarUsuarioCentro,
                btnLimpiarUsuarios, lblErrorConexionUsuarios,
                this::recargarDashboard);

        mascotaCtrl = new MascotaCrudController(
                tablaMascotas, listaMascotas,
                colMascotaId, colMascotaNombre, colMascotaEspecie, colMascotaRaza,
                colMascotaFechaNac, colMascotaPeso, colMascotaEstadoSalud, colMascotaDisponible,
                graficaEspecies, ejeXEspecies, ejeYEspecies,
                comboBuscarMascotaNombre, comboBuscarMascotaEspecie,
                comboBuscarMascotaRaza, comboBuscarMascotaEstadoSalud,
                btnLimpiarMascotas, lblErrorConexionMascotas,
                this::recargarDashboard);

        adopcionCtrl = new AdopcionCrudController(
                tablaAdopciones, listaAdopciones,
                colAdopcionId, colAdopcionMascota, colAdopcionVoluntario,
                colAdopcionFechaInicio, colAdopcionFechaFin,
                colAdopcionEstado, colAdopcionCalificacion,
                listaMascotas, listaUsuarios,
                comboBuscarAdopcionMascota, comboBuscarAdopcionVoluntario,
                comboBuscarAdopcionEstado, comboBuscarAdopcionCalificacion,
                btnLimpiarAdopciones, lblErrorConexionAdopciones,
                () -> { mascotaCtrl.cargarDatos(); mascotaCtrl.rellenarGraficaEspecies(); recargarDashboard(); });

        informesCtrl = new InformesController(
                webViewInforme, comboFiltroEstadoInforme,
                btnMascotasIncrustado, btnMascotasVentana, btnMascotasPdf,
                btnAdopcionesIncrustado, btnAdopcionesVentana, btnAdopcionesPdf);

        centroCtrl = new CentroCrudController(
                tablaCentros, listaCentros,
                colCentroId, colCentroNombre, colCentroCiudad, colCentroDireccion,
                colCentroTelefono, colCentroEspecialidad,
                comboBuscarCentroNombre, comboBuscarCentroCiudad,
                btnLimpiarCentros, lblErrorConexionCentros,
                this::recargarDashboard);

        // Configurar navegación
        btnInicio.setOnAction(e -> mostrarVista(vistaInicio, btnInicio, Seccion.INICIO));
        btnUsuarios.setOnAction(e -> mostrarVista(vistaUsuarios, btnUsuarios, Seccion.USUARIOS));
        btnMascotas.setOnAction(e -> mostrarVista(vistaMascotas, btnMascotas, Seccion.MASCOTAS));
        btnAdopciones.setOnAction(e -> mostrarVista(vistaAdopciones, btnAdopciones, Seccion.ADOPCIONES));
        btnInformes.setOnAction(e -> mostrarVista(vistaInformes, btnInformes, Seccion.INFORMES));
        btnCentros.setOnAction(e -> mostrarVista(vistaCentros, btnCentros, Seccion.CENTROS));
        btnChatSoporte.setOnAction(e -> abrirChatSoporte());

        btnNuevo.setOnAction(e -> accionCrud("Nuevo"));
        btnEditar.setOnAction(e -> accionCrud("Editar"));
        btnEliminar.setOnAction(e -> accionCrud("Eliminar"));

        // Configurar sub-controladores
        informesCtrl.configurar();
        usuarioCtrl.configurar();
        mascotaCtrl.configurar();
        adopcionCtrl.configurar();
        centroCtrl.configurar();
        configurarTablaUltimos();

        configurarAnimaciones();

        // Cargar datos
        usuarioCtrl.cargarDatos();
        mascotaCtrl.cargarDatos();
        adopcionCtrl.cargarDatos();
        centroCtrl.cargarDatos();
        cargarDatosDashboard();
        mascotaCtrl.rellenarGraficaEspecies();
        cargarUltimosRegistros();

        // Configurar visibilidad según rol
        configurarPermisosPorRol();

        UIUtils.aplicarAnimacionEntrada(stackContenido);
    }

    private void configurarPermisosPorRol() {
        SesionUsuario sesion = SesionUsuario.getInstancia();

        if (sesion.isAdmin()) {
            // Admin gestiona usuarios y centros
            btnMascotas.setVisible(false);
            btnMascotas.setManaged(false);
            btnAdopciones.setVisible(false);
            btnAdopciones.setManaged(false);
            btnInformes.setVisible(false);
            btnInformes.setManaged(false);
            mostrarVista(vistaUsuarios, btnUsuarios, Seccion.USUARIOS);
        } else {
            // Veterinario ve mascotas, adopciones, informes — no usuarios ni centros
            btnUsuarios.setVisible(false);
            btnUsuarios.setManaged(false);
            btnCentros.setVisible(false);
            btnCentros.setManaged(false);
            mostrarVista(vistaInicio, btnInicio, Seccion.INICIO);
        }
    }

    private void mostrarVista(AnchorPane vista, Button botonMenu, Seccion seccion) {
        vistaInicio.setVisible(false);
        vistaUsuarios.setVisible(false);
        vistaMascotas.setVisible(false);
        vistaAdopciones.setVisible(false);
        vistaInformes.setVisible(false);
        vistaCentros.setVisible(false);

        vista.setVisible(true);
        vista.toFront();
        vistaActual = vista;
        seccionActual = seccion;

        recargarDatosBuscadores(seccion);
        marcarBotonActivo(botonMenu);
        registrarListenersTablas();
        actualizarEstadoBotonesCrud();
    }

    private void recargarDatosBuscadores(Seccion seccion) {
        switch (seccion) {
            case USUARIOS -> usuarioCtrl.recargarBuscadores();
            case MASCOTAS -> mascotaCtrl.recargarBuscadores();
            case ADOPCIONES -> adopcionCtrl.recargarBuscadores();
            case CENTROS -> centroCtrl.recargarBuscadores();
            default -> {}
        }
    }

    private void registrarListenersTablas() {
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
        if (tablaCentros != null) {
            tablaCentros.getSelectionModel().selectedItemProperty()
                    .addListener((obs, o, n) -> actualizarEstadoBotonesCrud());
        }
    }

    private void marcarBotonActivo(Button activo) {
        String normal = "";
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
        btnCentros.setStyle(normal);

        if (activo != null) {
            activo.setStyle(seleccionado);
        }
    }

    private void actualizarEstadoBotonesCrud() {
        if (barraCrud == null) return;

        barraCrud.setVisible(true);
        barraCrud.setManaged(true);

        if (seccionActual == Seccion.INICIO) {
            btnNuevo.setVisible(false);
            btnEditar.setVisible(false);
            btnEliminar.setVisible(false);
            return;
        } else {
            btnNuevo.setVisible(true);
            btnEditar.setVisible(true);
            btnEliminar.setVisible(true);
        }

        boolean haySeleccion;
        switch (seccionActual) {
            case USUARIOS -> haySeleccion = tablaUsuarios != null && tablaUsuarios.getSelectionModel().getSelectedItem() != null;
            case MASCOTAS -> haySeleccion = tablaMascotas != null && tablaMascotas.getSelectionModel().getSelectedItem() != null;
            case ADOPCIONES -> haySeleccion = tablaAdopciones != null && tablaAdopciones.getSelectionModel().getSelectedItem() != null;
            case CENTROS -> haySeleccion = tablaCentros != null && tablaCentros.getSelectionModel().getSelectedItem() != null;
            case INFORMES -> haySeleccion = false;
            default -> haySeleccion = false;
        }

        btnNuevo.setDisable(false);
        btnEditar.setDisable(!haySeleccion);
        btnEliminar.setDisable(!haySeleccion);
    }

    private void accionCrud(String tipo) {
        if (seccionActual == null) {
            UIUtils.mostrarInfo("Acción CRUD", "Selecciona primero una sección en el menú de la izquierda.");
            return;
        }

        switch (seccionActual) {
            case USUARIOS -> {
                switch (tipo) {
                    case "Nuevo" -> usuarioCtrl.nuevo();
                    case "Editar" -> usuarioCtrl.editar();
                    case "Eliminar" -> usuarioCtrl.eliminar();
                }
            }
            case MASCOTAS -> {
                switch (tipo) {
                    case "Nuevo" -> mascotaCtrl.nuevo();
                    case "Editar" -> mascotaCtrl.editar();
                    case "Eliminar" -> mascotaCtrl.eliminar();
                }
            }
            case ADOPCIONES -> {
                switch (tipo) {
                    case "Nuevo" -> adopcionCtrl.nuevo();
                    case "Editar" -> adopcionCtrl.editar();
                    case "Eliminar" -> adopcionCtrl.eliminar();
                }
            }
            case CENTROS -> {
                switch (tipo) {
                    case "Nuevo" -> centroCtrl.nuevo();
                    case "Editar" -> centroCtrl.editar();
                    case "Eliminar" -> centroCtrl.eliminar();
                }
            }
            case INFORMES -> {
                switch (tipo) {
                    case "Nuevo" -> informesCtrl.nuevo();
                    case "Editar" -> informesCtrl.editar();
                    case "Eliminar" -> informesCtrl.eliminar();
                }
            }
            default -> UIUtils.mostrarInfo("Acción CRUD", tipo + " en esta sección todavía no está implementado.");
        }
    }

    // --- Dashboard ---

    private void recargarDashboard() {
        cargarDatosDashboard();
        cargarUltimosRegistros();
    }

    private void cargarDatosDashboard() {
        // Mascotas: ya cargadas en la lista
        lblMascotasRegistradas.setText(String.valueOf(listaMascotas.size()));

        // Adopciones activas: filtrar la lista
        long activas = listaAdopciones.stream()
                .filter(a -> "activo".equals(a.getEstado()))
                .count();
        lblAdopcionesActivas.setText(String.valueOf(activas));

        // Usuarios activos: todavía vía JDBC (no hay endpoint en la API)
        try (Connection conn = ConexionBBDD.getConexion()) {
            String sqlUsuarios = "SELECT COUNT(*) FROM Usuarios WHERE activo = 1";
            try (Statement st = conn.createStatement();
                 ResultSet rsUsuarios = st.executeQuery(sqlUsuarios)) {
                if (rsUsuarios.next()) {
                    lblUsuariosActivos.setText(String.valueOf(rsUsuarios.getInt(1)));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error cargando dashboard usuarios: " + e.getMessage());
        }
    }

    private void configurarTablaUltimos() {
        if (tablaUltimos == null) return;

        if (tablaUltimos.getColumns().size() >= 2) {
            @SuppressWarnings("unchecked")
            TableColumn<UltimoRegistro, String> cDesc = (TableColumn<UltimoRegistro, String>) tablaUltimos.getColumns().get(0);
            @SuppressWarnings("unchecked")
            TableColumn<UltimoRegistro, String> cFecha = (TableColumn<UltimoRegistro, String>) tablaUltimos.getColumns().get(1);

            cDesc.setCellValueFactory(datos -> new SimpleStringProperty(datos.getValue().getDescripcion()));
            cFecha.setCellValueFactory(datos -> new SimpleStringProperty(datos.getValue().getFecha()));

            tablaUltimos.setItems(listaUltimos);
        }
    }

    private void cargarUltimosRegistros() {
        if (tablaUltimos == null) return;

        listaUltimos.clear();

        String sql = "SELECT descripcion, fecha FROM ("
                + " SELECT CONCAT('Mascota: ', nombre, ' - ', raza) AS descripcion, fecha_nacimiento AS fecha "
                + " FROM Mascotas "
                + " UNION ALL "
                + " SELECT CONCAT('Usuario: ', nombre, ' - ', email) AS descripcion, CURRENT_DATE AS fecha "
                + " FROM Usuarios "
                + " UNION ALL "
                + " SELECT CONCAT('Adopción de ', m.nombre, ' - ', u.nombre) AS descripcion, a.fecha_inicio AS fecha "
                + " FROM alquiler a "
                + " JOIN mascotas m ON a.id_mascota = m.id_mascota "
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

    // --- Animaciones ---

    private void configurarAnimaciones() {
        UIUtils.configurarHoverBoton(btnInicio);
        UIUtils.configurarHoverBoton(btnUsuarios);
        UIUtils.configurarHoverBoton(btnMascotas);
        UIUtils.configurarHoverBoton(btnAdopciones);
        UIUtils.configurarHoverBoton(btnInformes);
        UIUtils.configurarHoverBoton(btnCentros);
        UIUtils.configurarHoverBoton(btnChatSoporte);
        UIUtils.configurarHoverBoton(btnNuevo);
        UIUtils.configurarHoverBoton(btnEditar);
        UIUtils.configurarHoverBoton(btnEliminar);
    }

    private void abrirChatSoporte() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChatVeterinarioView.fxml"));
            Parent root = loader.load();
            ChatVeterinarioController chatCtrl = loader.getController();

            Stage chatStage = new Stage();
            chatStage.setTitle("Chat Soporte - PawLink");
            chatStage.getIcons().add(
                    new Image(getClass().getResourceAsStream("/miapp/icons/paw.png")));
            chatStage.setScene(new Scene(root));
            chatStage.initOwner(btnChatSoporte.getScene().getWindow());
            chatStage.initModality(Modality.NONE);
            chatStage.setResizable(true);

            chatStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST,
                    e -> chatCtrl.cerrarConexion());

            chatStage.show();
        } catch (IOException e) {
            UIUtils.mostrarInfo("Chat Soporte",
                    "No se pudo abrir la ventana de chat: " + e.getMessage());
        }
    }
}
