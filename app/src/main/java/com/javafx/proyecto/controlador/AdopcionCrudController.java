package com.javafx.proyecto.controlador;

import com.javafx.proyecto.bbdd.PawLinkClient;
import com.javafx.proyecto.modelo.AdopcionTabla;
import com.javafx.proyecto.modelo.Mascota;
import com.javafx.proyecto.modelo.Usuario;
import com.javafx.proyecto.util.SesionUsuario;
import com.javafx.proyecto.util.UIUtils;
import com.javafx.proyecto.util.ValidadorForms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;

import org.controlsfx.validation.ValidationSupport;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AdopcionCrudController {

    private final TableView<AdopcionTabla> tablaAdopciones;
    private final ObservableList<AdopcionTabla> listaAdopciones;
    private final TableColumn<AdopcionTabla, Integer> colAdopcionId;
    private final TableColumn<AdopcionTabla, String> colAdopcionMascota;
    private final TableColumn<AdopcionTabla, String> colAdopcionVoluntario;
    private final TableColumn<AdopcionTabla, LocalDate> colAdopcionFechaInicio;
    private final TableColumn<AdopcionTabla, LocalDate> colAdopcionFechaFin;
    private final TableColumn<AdopcionTabla, String> colAdopcionEstado;
    private final TableColumn<AdopcionTabla, Integer> colAdopcionCalificacion;

    private final ObservableList<Mascota> listaMascotas;
    private final ObservableList<Usuario> listaUsuarios;

    private final ComboBox<String> comboBuscarAdopcionMascota;
    private final ComboBox<String> comboBuscarAdopcionVoluntario;
    private final ComboBox<String> comboBuscarAdopcionEstado;
    private final ComboBox<String> comboBuscarAdopcionCalificacion;
    private final Button btnLimpiarAdopciones;

    private final Label lblErrorConexionAdopciones;

    private final Runnable onDatosActualizados;

    public AdopcionCrudController(
            TableView<AdopcionTabla> tablaAdopciones,
            ObservableList<AdopcionTabla> listaAdopciones,
            TableColumn<AdopcionTabla, Integer> colAdopcionId,
            TableColumn<AdopcionTabla, String> colAdopcionMascota,
            TableColumn<AdopcionTabla, String> colAdopcionVoluntario,
            TableColumn<AdopcionTabla, LocalDate> colAdopcionFechaInicio,
            TableColumn<AdopcionTabla, LocalDate> colAdopcionFechaFin,
            TableColumn<AdopcionTabla, String> colAdopcionEstado,
            TableColumn<AdopcionTabla, Integer> colAdopcionCalificacion,
            ObservableList<Mascota> listaMascotas,
            ObservableList<Usuario> listaUsuarios,
            ComboBox<String> comboBuscarAdopcionMascota,
            ComboBox<String> comboBuscarAdopcionVoluntario,
            ComboBox<String> comboBuscarAdopcionEstado,
            ComboBox<String> comboBuscarAdopcionCalificacion,
            Button btnLimpiarAdopciones,
            Label lblErrorConexionAdopciones,
            Runnable onDatosActualizados) {

        this.tablaAdopciones = tablaAdopciones;
        this.listaAdopciones = listaAdopciones;
        this.colAdopcionId = colAdopcionId;
        this.colAdopcionMascota = colAdopcionMascota;
        this.colAdopcionVoluntario = colAdopcionVoluntario;
        this.colAdopcionFechaInicio = colAdopcionFechaInicio;
        this.colAdopcionFechaFin = colAdopcionFechaFin;
        this.colAdopcionEstado = colAdopcionEstado;
        this.colAdopcionCalificacion = colAdopcionCalificacion;
        this.listaMascotas = listaMascotas;
        this.listaUsuarios = listaUsuarios;
        this.comboBuscarAdopcionMascota = comboBuscarAdopcionMascota;
        this.comboBuscarAdopcionVoluntario = comboBuscarAdopcionVoluntario;
        this.comboBuscarAdopcionEstado = comboBuscarAdopcionEstado;
        this.comboBuscarAdopcionCalificacion = comboBuscarAdopcionCalificacion;
        this.btnLimpiarAdopciones = btnLimpiarAdopciones;
        this.lblErrorConexionAdopciones = lblErrorConexionAdopciones;
        this.onDatosActualizados = onDatosActualizados;
    }

    public void configurar() {
        configurarColumnas();
        configurarMenuContextual();
        configurarBuscadores();
    }

    private void configurarColumnas() {
        colAdopcionId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAdopcionMascota.setCellValueFactory(new PropertyValueFactory<>("mascota"));
        colAdopcionVoluntario.setCellValueFactory(new PropertyValueFactory<>("voluntario"));
        colAdopcionFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colAdopcionFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        colAdopcionEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colAdopcionCalificacion.setCellValueFactory(new PropertyValueFactory<>("calificacion"));
    }

    private void configurarMenuContextual() {
        if (tablaAdopciones == null) return;

        ContextMenu menuContextual = new ContextMenu();

        MenuItem itemEditar = new MenuItem("_Editar adopción");
        itemEditar.setMnemonicParsing(true);
        itemEditar.setGraphic(UIUtils.crearIcono("/miapp/icons/editar.png", 16));
        itemEditar.setOnAction(e -> editar());

        MenuItem itemEliminar = new MenuItem("E_liminar adopción");
        itemEliminar.setMnemonicParsing(true);
        itemEliminar.setGraphic(UIUtils.crearIcono("/miapp/icons/eliminar.png", 16));
        itemEliminar.setOnAction(e -> eliminar());

        MenuItem itemVerDetalles = new MenuItem("_Ver detalles");
        itemVerDetalles.setMnemonicParsing(true);
        itemVerDetalles.setGraphic(UIUtils.crearIcono("/miapp/icons/form.png", 16));
        itemVerDetalles.setOnAction(e -> {
            AdopcionTabla seleccionada = tablaAdopciones.getSelectionModel().getSelectedItem();
            if (seleccionada != null) {
                String calif = seleccionada.getCalificacion() != null ? seleccionada.getCalificacion().toString()
                        : "Sin calificar";
                UIUtils.mostrarInfo("Detalles de la Adopción",
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
        itemCambiarEstado.setGraphic(UIUtils.crearIcono("/miapp/icons/form.png", 16));
        itemCambiarEstado.setOnAction(e -> {
            AdopcionTabla seleccionada = tablaAdopciones.getSelectionModel().getSelectedItem();
            if (seleccionada != null) {
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("idMascota", seleccionada.getIdMascota());
                body.put("idVoluntario", seleccionada.getIdVoluntario());
                body.put("fechaInicio", seleccionada.getFechaInicio() != null ? seleccionada.getFechaInicio().toString() : null);
                body.put("fechaFin", seleccionada.getFechaFin() != null ? seleccionada.getFechaFin().toString() : null);
                body.put("estado", "finalizado");
                try {
                    PawLinkClient.actualizarAlquiler(seleccionada.getId(), body, SesionUsuario.getInstancia().getToken());
                    cargarDatos();
                    onDatosActualizados.run();
                    UIUtils.mostrarInfo("Éxito", "Estado actualizado a finalizado");
                } catch (Exception ex) {
                    UIUtils.mostrarInfo("Error", "No se pudo actualizar: " + ex.getMessage());
                }
            }
        });

        menuContextual.getItems().addAll(itemEditar, itemEliminar, new SeparatorMenuItem(),
                itemVerDetalles, itemCambiarEstado);

        tablaAdopciones.setContextMenu(menuContextual);

        tablaAdopciones.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                if (tablaAdopciones.getSelectionModel().getSelectedItem() != null) {
                    editar();
                }
            }
        });
    }

    public void cargarDatos() {
        listaAdopciones.clear();

        try {
            List<Map<String, Object>> alquileres = PawLinkClient.getAlquileres(SesionUsuario.getInstancia().getToken());
            for (Map<String, Object> a : alquileres) {
                int id = ((Number) a.get("idAlquiler")).intValue();
                String mascota = (String) a.get("nombreMascota");
                String voluntario = (String) a.get("nombreVoluntario");
                String fiStr = (String) a.get("fechaInicio");
                String ffStr = (String) a.get("fechaFin");
                LocalDate fechaInicio = fiStr != null ? LocalDate.parse(fiStr) : null;
                LocalDate fechaFin = ffStr != null ? LocalDate.parse(ffStr) : null;
                String estado = (String) a.get("estado");
                int idMascota = a.get("idMascota") instanceof Number ? ((Number) a.get("idMascota")).intValue() : 0;
                int idVoluntario = a.get("idVoluntario") instanceof Number ? ((Number) a.get("idVoluntario")).intValue() : 0;
                listaAdopciones.add(new AdopcionTabla(id, mascota, voluntario, fechaInicio, fechaFin, estado, null, idMascota, idVoluntario));
            }
            System.out.println("Adopciones cargadas: " + listaAdopciones.size());
            UIUtils.ocultarErrorConexion(lblErrorConexionAdopciones);
        } catch (Exception e) {
            System.out.println("Error cargando adopciones: " + e.getMessage());
            UIUtils.mostrarErrorConexion(lblErrorConexionAdopciones);
        }

        tablaAdopciones.setItems(listaAdopciones);
    }

    public void nuevo() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nueva adopción");
        dialog.setHeaderText("Introduce los datos de la adopción");

        ButtonType btnGuardar = new ButtonType("_Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("_Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

        GridPane grid = UIUtils.crearGridBasico();

        ComboBox<Mascota> comboMascota = new ComboBox<>();
        comboMascota.setPrefWidth(250);
        comboMascota.setTooltip(new Tooltip("Busca y selecciona una mascota (puedes escribir para filtrar)"));
        ObservableList<Mascota> mascotasDisponibles = listaMascotas.filtered(Mascota::getDisponible);
        UIUtils.configurarAutocompletado(comboMascota, mascotasDisponibles);

        ComboBox<Usuario> comboVoluntario = new ComboBox<>();
        comboVoluntario.setPrefWidth(250);
        comboVoluntario.setTooltip(new Tooltip("Busca y selecciona un voluntario (puedes escribir para filtrar)"));
        UIUtils.configurarAutocompletado(comboVoluntario, listaUsuarios);

        DatePicker dpInicio = new DatePicker(LocalDate.now());
        dpInicio.setTooltip(new Tooltip("Fecha de inicio de la adopción"));

        DatePicker dpFin = new DatePicker();
        dpFin.setTooltip(new Tooltip("Fecha de finalización de la adopción (opcional)"));

        TextField txtEstado = new TextField("activo");
        txtEstado.setTooltip(new Tooltip("Estado de la adopción (obligatorio)"));

        grid.addRow(0, new Label("Mascota:"), comboMascota);
        grid.addRow(1, new Label("Voluntario:"), comboVoluntario);
        grid.addRow(2, new Label("Fecha inicio:"), dpInicio);
        grid.addRow(3, new Label("Fecha fin:"), dpFin);
        grid.addRow(4, new Label("Estado:"), txtEstado);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setMinWidth(500);
        UIUtils.añadirIconoADialogo(dialog);

        List<ValidationSupport> validadores = new ArrayList<>();
        validadores.add(ValidadorForms.validarComboBoxObligatorio(comboMascota, "una mascota"));
        validadores.add(ValidadorForms.validarComboBoxObligatorio(comboVoluntario, "un voluntario"));
        validadores.add(ValidadorForms.validarEstadoAdopcion(txtEstado));
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
            Mascota m = comboMascota.getSelectionModel().getSelectedItem();
            Usuario u = comboVoluntario.getSelectionModel().getSelectedItem();

            if (m == null || u == null) {
                UIUtils.mostrarInfo("Datos incompletos", "Selecciona una mascota y un voluntario.");
                return;
            }

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("idMascota", m.getId());
            body.put("idVoluntario", u.getId());
            body.put("fechaInicio", dpInicio.getValue() != null ? dpInicio.getValue().toString() : null);
            body.put("fechaFin", dpFin.getValue() != null ? dpFin.getValue().toString() : null);
            body.put("estado", txtEstado.getText());
            try {
                PawLinkClient.crearAlquiler(body, SesionUsuario.getInstancia().getToken());
            } catch (Exception e) {
                UIUtils.mostrarInfo("Error API", "No se pudo crear el alquiler:\n" + e.getMessage());
                return;
            }
            // Marcar la mascota como no disponible
            Map<String, Object> bodyMascota = new LinkedHashMap<>();
            bodyMascota.put("idCentro", m.getIdCentro());
            bodyMascota.put("nombre", m.getNombre());
            bodyMascota.put("especie", m.getEspecie());
            bodyMascota.put("raza", m.getRaza());
            bodyMascota.put("fechaNacimiento", m.getFechaNacimiento() != null ? m.getFechaNacimiento().toString() : null);
            bodyMascota.put("peso", m.getPeso());
            bodyMascota.put("estadoSalud", m.getEstadoSalud());
            bodyMascota.put("disponibleAlquiler", 0);
            bodyMascota.put("foto", m.getFoto());
            bodyMascota.put("notas", m.getNotas());
            try {
                PawLinkClient.actualizarMascota(m.getId(), bodyMascota, SesionUsuario.getInstancia().getToken());
            } catch (Exception e) {
                UIUtils.mostrarInfo("Aviso", "Alquiler creado, pero no se pudo actualizar la disponibilidad:\n" + e.getMessage());
            }
            cargarDatos();
            onDatosActualizados.run();
        }
    }

    public void editar() {
        AdopcionTabla seleccionada = tablaAdopciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            UIUtils.mostrarInfo("Editar adopción", "Selecciona primero una adopción de la tabla.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar adopción");
        dialog.setHeaderText("Edita las fechas y el estado");

        ButtonType btnGuardar = new ButtonType("_Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("_Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

        GridPane grid = UIUtils.crearGridBasico();

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

        grid.addRow(0, new Label("Mascota:"), txtMascota);
        grid.addRow(1, new Label("Voluntario:"), txtVoluntario);
        grid.addRow(2, new Label("Fecha inicio:"), dpInicio);
        grid.addRow(3, new Label("Fecha fin:"), dpFin);
        grid.addRow(4, new Label("Estado:"), txtEstado);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setMinWidth(500);
        UIUtils.añadirIconoADialogo(dialog);

        List<ValidationSupport> validadores = new ArrayList<>();
        validadores.add(ValidadorForms.validarEstadoAdopcion(txtEstado));
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
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("idMascota", seleccionada.getIdMascota());
            body.put("idVoluntario", seleccionada.getIdVoluntario());
            body.put("fechaInicio", dpInicio.getValue() != null ? dpInicio.getValue().toString() : null);
            body.put("fechaFin", dpFin.getValue() != null ? dpFin.getValue().toString() : null);
            body.put("estado", txtEstado.getText());
            try {
                PawLinkClient.actualizarAlquiler(seleccionada.getId(), body, SesionUsuario.getInstancia().getToken());
                cargarDatos();
                onDatosActualizados.run();
            } catch (Exception e) {
                UIUtils.mostrarInfo("Error API", "No se pudo actualizar la adopción:\n" + e.getMessage());
            }
        }
    }

    public void eliminar() {
        AdopcionTabla seleccionada = tablaAdopciones.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            UIUtils.mostrarInfo("Eliminar adopción", "Selecciona primero una adopción de la tabla.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar adopción");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que quieres eliminar la adopción ID " + seleccionada.getId() + "?");
        UIUtils.añadirIconoADialogo(confirm);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                PawLinkClient.eliminarAlquiler(seleccionada.getId(), SesionUsuario.getInstancia().getToken());
                cargarDatos();
                onDatosActualizados.run();
            } catch (Exception e) {
                UIUtils.mostrarInfo("Error API", "No se pudo eliminar la adopción:\n" + e.getMessage());
            }
        }
    }

    // --- Buscadores ---

    private void configurarBuscadores() {
        if (comboBuscarAdopcionMascota != null) {
            ObservableList<String> mascotas = FXCollections.observableArrayList();
            UIUtils.configurarBuscadorConAutocompletado(comboBuscarAdopcionMascota, mascotas,
                () -> listaAdopciones.stream().map(AdopcionTabla::getMascota).distinct().toList());
        }
        if (comboBuscarAdopcionVoluntario != null) {
            ObservableList<String> voluntarios = FXCollections.observableArrayList();
            UIUtils.configurarBuscadorConAutocompletado(comboBuscarAdopcionVoluntario, voluntarios,
                () -> listaAdopciones.stream().map(AdopcionTabla::getVoluntario).distinct().toList());
        }
        if (comboBuscarAdopcionEstado != null) {
            ObservableList<String> estados = FXCollections.observableArrayList();
            UIUtils.configurarBuscadorConAutocompletado(comboBuscarAdopcionEstado, estados,
                () -> listaAdopciones.stream().map(AdopcionTabla::getEstado).distinct().toList());
        }
        if (comboBuscarAdopcionCalificacion != null) {
            ObservableList<String> calificaciones = FXCollections.observableArrayList();
            UIUtils.configurarBuscadorConAutocompletado(comboBuscarAdopcionCalificacion, calificaciones,
                () -> listaAdopciones.stream()
                    .map(a -> a.getCalificacion() != null ? a.getCalificacion().toString() : null)
                    .filter(c -> c != null)
                    .distinct()
                    .toList());
        }
        if (btnLimpiarAdopciones != null) {
            btnLimpiarAdopciones.setOnAction(e -> buscar());
        }
    }

    public void recargarBuscadores() {
        recargarCombo(comboBuscarAdopcionMascota, () -> listaAdopciones.stream().map(AdopcionTabla::getMascota).distinct().toList());
        recargarCombo(comboBuscarAdopcionVoluntario, () -> listaAdopciones.stream().map(AdopcionTabla::getVoluntario).distinct().toList());
        recargarCombo(comboBuscarAdopcionEstado, () -> listaAdopciones.stream().map(AdopcionTabla::getEstado).distinct().toList());
        recargarCombo(comboBuscarAdopcionCalificacion, () -> listaAdopciones.stream()
                .map(a -> a.getCalificacion() != null ? a.getCalificacion().toString() : null)
                .filter(c -> c != null)
                .distinct()
                .toList());
    }

    @SuppressWarnings("unchecked")
    private void recargarCombo(ComboBox<String> combo, java.util.function.Supplier<List<String>> supplier) {
        if (combo != null && combo.getItems() instanceof FilteredList) {
            FilteredList<String> filteredItems = (FilteredList<String>) combo.getItems();
            ObservableList<String> source = (ObservableList<String>) filteredItems.getSource();
            source.clear();
            source.addAll(supplier.get());
        }
    }

    private void buscar() {
        String mascota = getValor(comboBuscarAdopcionMascota);
        String voluntario = getValor(comboBuscarAdopcionVoluntario);
        String estado = getValor(comboBuscarAdopcionEstado);
        String calificacion = getValor(comboBuscarAdopcionCalificacion);

        ObservableList<AdopcionTabla> filtrados = listaAdopciones.filtered(adopcion -> {
            boolean coincide = true;
            if (!mascota.isEmpty()) coincide = coincide && adopcion.getMascota().toLowerCase().contains(mascota);
            if (!voluntario.isEmpty()) coincide = coincide && adopcion.getVoluntario().toLowerCase().contains(voluntario);
            if (!estado.isEmpty()) coincide = coincide && adopcion.getEstado().toLowerCase().contains(estado);
            if (!calificacion.isEmpty()) {
                String califAdopcion = adopcion.getCalificacion() != null
                    ? adopcion.getCalificacion().toString() : "";
                coincide = coincide && califAdopcion.contains(calificacion);
            }
            return coincide;
        });

        tablaAdopciones.setItems(filtrados);
    }

    private String getValor(ComboBox<String> combo) {
        return combo != null && combo.getValue() != null ? combo.getValue().trim().toLowerCase() : "";
    }
}
