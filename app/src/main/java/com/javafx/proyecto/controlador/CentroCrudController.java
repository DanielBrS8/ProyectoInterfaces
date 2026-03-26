package com.javafx.proyecto.controlador;

import com.javafx.proyecto.bbdd.PawLinkClient;
import com.javafx.proyecto.modelo.CentroVeterinario;
import com.javafx.proyecto.util.SesionUsuario;
import com.javafx.proyecto.util.UIUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CentroCrudController {

    private final TableView<CentroVeterinario> tablaCentros;
    private final ObservableList<CentroVeterinario> listaCentros;
    private final TableColumn<CentroVeterinario, Integer> colCentroId;
    private final TableColumn<CentroVeterinario, String> colCentroNombre;
    private final TableColumn<CentroVeterinario, String> colCentroCiudad;
    private final TableColumn<CentroVeterinario, String> colCentroDireccion;
    private final TableColumn<CentroVeterinario, String> colCentroTelefono;
    private final TableColumn<CentroVeterinario, String> colCentroEspecialidad;

    private final ComboBox<String> comboBuscarCentroNombre;
    private final ComboBox<String> comboBuscarCentroCiudad;
    private final Button btnLimpiarCentros;

    private final Label lblErrorConexionCentros;

    private final Runnable onDatosActualizados;

    public CentroCrudController(
            TableView<CentroVeterinario> tablaCentros,
            ObservableList<CentroVeterinario> listaCentros,
            TableColumn<CentroVeterinario, Integer> colCentroId,
            TableColumn<CentroVeterinario, String> colCentroNombre,
            TableColumn<CentroVeterinario, String> colCentroCiudad,
            TableColumn<CentroVeterinario, String> colCentroDireccion,
            TableColumn<CentroVeterinario, String> colCentroTelefono,
            TableColumn<CentroVeterinario, String> colCentroEspecialidad,
            ComboBox<String> comboBuscarCentroNombre,
            ComboBox<String> comboBuscarCentroCiudad,
            Button btnLimpiarCentros,
            Label lblErrorConexionCentros,
            Runnable onDatosActualizados) {

        this.tablaCentros = tablaCentros;
        this.listaCentros = listaCentros;
        this.colCentroId = colCentroId;
        this.colCentroNombre = colCentroNombre;
        this.colCentroCiudad = colCentroCiudad;
        this.colCentroDireccion = colCentroDireccion;
        this.colCentroTelefono = colCentroTelefono;
        this.colCentroEspecialidad = colCentroEspecialidad;
        this.comboBuscarCentroNombre = comboBuscarCentroNombre;
        this.comboBuscarCentroCiudad = comboBuscarCentroCiudad;
        this.btnLimpiarCentros = btnLimpiarCentros;
        this.lblErrorConexionCentros = lblErrorConexionCentros;
        this.onDatosActualizados = onDatosActualizados;
    }

    public void configurar() {
        configurarColumnas();
        configurarMenuContextual();
        configurarBuscadores();
    }

    private void configurarColumnas() {
        colCentroId.setCellValueFactory(new PropertyValueFactory<>("idCentro"));
        colCentroNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCentroCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
        colCentroDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colCentroTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCentroEspecialidad.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
    }

    private void configurarMenuContextual() {
        if (tablaCentros == null) return;

        ContextMenu menu = new ContextMenu();

        MenuItem itemEditar = new MenuItem("_Editar centro");
        itemEditar.setMnemonicParsing(true);
        itemEditar.setOnAction(e -> editar());

        MenuItem itemEliminar = new MenuItem("E_liminar centro");
        itemEliminar.setMnemonicParsing(true);
        itemEliminar.setOnAction(e -> eliminar());

        menu.getItems().addAll(itemEditar, itemEliminar);
        tablaCentros.setContextMenu(menu);

        tablaCentros.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                if (tablaCentros.getSelectionModel().getSelectedItem() != null) {
                    editar();
                }
            }
        });
    }

    public void cargarDatos() {
        listaCentros.clear();
        String token = SesionUsuario.getInstancia().getToken();

        try {
            List<Map<String, Object>> centros = PawLinkClient.getCentros(token);

            for (Map<String, Object> c : centros) {
                Integer id = (Integer) c.get("idCentro");
                String nombre = (String) c.get("nombre");
                String ciudad = (String) c.get("ciudad");
                String direccion = (String) c.get("direccion");
                String telefono = (String) c.get("telefono");
                String especialidad = (String) c.get("especialidad");

                listaCentros.add(new CentroVeterinario(id, nombre, ciudad, direccion, telefono, especialidad));
            }
            UIUtils.ocultarErrorConexion(lblErrorConexionCentros);

        } catch (Exception e) {
            e.printStackTrace();
            lblErrorConexionCentros.setText("Error: " + e.getMessage());
            lblErrorConexionCentros.setVisible(true);
            lblErrorConexionCentros.setManaged(true);
        }

        tablaCentros.setItems(listaCentros);
    }

    public void nuevo() {
        Dialog<ButtonType> dialog = crearDialogoCentro("Nuevo centro", "Introduce los datos del nuevo centro", null);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            Map<String, Object> body = extraerDatosDialogo(dialog);
            String token = SesionUsuario.getInstancia().getToken();

            try {
                PawLinkClient.crearCentro(body, token);
                cargarDatos();
                onDatosActualizados.run();
            } catch (Exception e) {
                UIUtils.mostrarInfo("Error", "No se pudo crear el centro:\n" + e.getMessage());
            }
        }
    }

    public void editar() {
        CentroVeterinario seleccionado = tablaCentros.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            UIUtils.mostrarInfo("Editar centro", "Selecciona primero un centro de la tabla.");
            return;
        }

        Dialog<ButtonType> dialog = crearDialogoCentro("Editar centro", "Edita los datos del centro", seleccionado);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            Map<String, Object> body = extraerDatosDialogo(dialog);
            String token = SesionUsuario.getInstancia().getToken();

            try {
                PawLinkClient.actualizarCentro(seleccionado.getIdCentro(), body, token);
                cargarDatos();
                onDatosActualizados.run();
            } catch (Exception e) {
                UIUtils.mostrarInfo("Error", "No se pudo actualizar el centro:\n" + e.getMessage());
            }
        }
    }

    public void eliminar() {
        CentroVeterinario seleccionado = tablaCentros.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            UIUtils.mostrarInfo("Eliminar centro", "Selecciona primero un centro de la tabla.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar centro");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que quieres eliminar el centro " + seleccionado.getNombre() + "?");
        UIUtils.añadirIconoADialogo(confirm);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String token = SesionUsuario.getInstancia().getToken();

            try {
                PawLinkClient.eliminarCentro(seleccionado.getIdCentro(), token);
                cargarDatos();
                onDatosActualizados.run();
            } catch (Exception e) {
                UIUtils.mostrarInfo("Error", "No se pudo eliminar el centro:\n" + e.getMessage());
            }
        }
    }

    private Dialog<ButtonType> crearDialogoCentro(String titulo, String header, CentroVeterinario datos) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText(header);

        ButtonType btnGuardar = new ButtonType("_Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("_Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

        GridPane grid = UIUtils.crearGridBasico();

        TextField txtNombre = new TextField(datos != null ? datos.getNombre() : "");
        txtNombre.setPromptText("Nombre del centro");
        txtNombre.setId("txtNombre");

        TextField txtCiudad = new TextField(datos != null ? datos.getCiudad() : "");
        txtCiudad.setPromptText("Ciudad");
        txtCiudad.setId("txtCiudad");

        TextField txtDireccion = new TextField(datos != null ? datos.getDireccion() : "");
        txtDireccion.setPromptText("Dirección completa");
        txtDireccion.setId("txtDireccion");

        TextField txtTelefono = new TextField(datos != null ? datos.getTelefono() : "");
        txtTelefono.setPromptText("123456789");
        txtTelefono.setId("txtTelefono");

        TextField txtEspecialidad = new TextField(datos != null ? datos.getEspecialidad() : "");
        txtEspecialidad.setPromptText("Ej: General, Exóticos...");
        txtEspecialidad.setId("txtEspecialidad");

        grid.addRow(0, new Label("Nombre:"), txtNombre);
        grid.addRow(1, new Label("Ciudad:"), txtCiudad);
        grid.addRow(2, new Label("Dirección:"), txtDireccion);
        grid.addRow(3, new Label("Teléfono:"), txtTelefono);
        grid.addRow(4, new Label("Especialidad:"), txtEspecialidad);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setMinWidth(500);
        UIUtils.añadirIconoADialogo(dialog);

        dialog.getDialogPane().lookupButton(btnGuardar).setDisable(datos == null);

        Runnable validar = () -> {
            boolean valido = !txtNombre.getText().trim().isEmpty()
                    && !txtCiudad.getText().trim().isEmpty();
            dialog.getDialogPane().lookupButton(btnGuardar).setDisable(!valido);
        };

        txtNombre.textProperty().addListener((obs, o, n) -> validar.run());
        txtCiudad.textProperty().addListener((obs, o, n) -> validar.run());

        javafx.application.Platform.runLater(txtNombre::requestFocus);

        return dialog;
    }

    private Map<String, Object> extraerDatosDialogo(Dialog<ButtonType> dialog) {
        GridPane grid = (GridPane) dialog.getDialogPane().getContent();
        Map<String, Object> body = new HashMap<>();

        for (javafx.scene.Node node : grid.getChildren()) {
            if (node instanceof TextField tf) {
                switch (tf.getId()) {
                    case "txtNombre" -> body.put("nombre", tf.getText().trim());
                    case "txtCiudad" -> body.put("ciudad", tf.getText().trim());
                    case "txtDireccion" -> body.put("direccion", tf.getText().trim());
                    case "txtTelefono" -> body.put("telefono", tf.getText().trim());
                    case "txtEspecialidad" -> body.put("especialidad", tf.getText().trim());
                }
            }
        }
        return body;
    }

    // --- Buscadores ---

    private void configurarBuscadores() {
        if (comboBuscarCentroNombre != null) {
            ObservableList<String> nombres = FXCollections.observableArrayList();
            UIUtils.configurarBuscadorConAutocompletado(comboBuscarCentroNombre, nombres,
                () -> listaCentros.stream().map(CentroVeterinario::getNombre).distinct().toList());
        }
        if (comboBuscarCentroCiudad != null) {
            ObservableList<String> ciudades = FXCollections.observableArrayList();
            UIUtils.configurarBuscadorConAutocompletado(comboBuscarCentroCiudad, ciudades,
                () -> listaCentros.stream().map(CentroVeterinario::getCiudad)
                        .filter(c -> c != null).distinct().toList());
        }
        if (btnLimpiarCentros != null) {
            btnLimpiarCentros.setOnAction(e -> buscar());
        }
    }

    public void recargarBuscadores() {
        recargarCombo(comboBuscarCentroNombre, () -> listaCentros.stream().map(CentroVeterinario::getNombre).distinct().toList());
        recargarCombo(comboBuscarCentroCiudad, () -> listaCentros.stream().map(CentroVeterinario::getCiudad)
                .filter(c -> c != null).distinct().toList());
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
        String nombre = getValor(comboBuscarCentroNombre);
        String ciudad = getValor(comboBuscarCentroCiudad);

        ObservableList<CentroVeterinario> filtrados = listaCentros.filtered(centro -> {
            boolean coincide = true;
            if (!nombre.isEmpty()) coincide = centro.getNombre().toLowerCase().contains(nombre);
            if (!ciudad.isEmpty()) coincide = coincide && centro.getCiudad() != null
                    && centro.getCiudad().toLowerCase().contains(ciudad);
            return coincide;
        });

        tablaCentros.setItems(filtrados);
    }

    private String getValor(ComboBox<String> combo) {
        return combo != null && combo.getValue() != null ? combo.getValue().trim().toLowerCase() : "";
    }
}
