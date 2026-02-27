package com.javafx.proyecto.controlador;

import com.javafx.proyecto.bbdd.ConexionBBDD;
import com.javafx.proyecto.modelo.Usuario;
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

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioCrudController {

    private final TableView<Usuario> tablaUsuarios;
    private final ObservableList<Usuario> listaUsuarios;
    private final TableColumn<Usuario, Integer> colUsuarioId;
    private final TableColumn<Usuario, String> colUsuarioNombre;
    private final TableColumn<Usuario, String> colUsuarioEmail;
    private final TableColumn<Usuario, String> colUsuarioTelefono;
    private final TableColumn<Usuario, String> colUsuarioDireccion;
    private final TableColumn<Usuario, Boolean> colUsuarioActivo;

    private final ComboBox<String> comboBuscarUsuarioNombre;
    private final ComboBox<String> comboBuscarUsuarioEmail;
    private final ComboBox<String> comboBuscarUsuarioTelefono;
    private final ComboBox<String> comboBuscarUsuarioDireccion;
    private final Button btnLimpiarUsuarios;

    private final Label lblErrorConexionUsuarios;

    private final Runnable onDatosActualizados;

    public UsuarioCrudController(
            TableView<Usuario> tablaUsuarios,
            ObservableList<Usuario> listaUsuarios,
            TableColumn<Usuario, Integer> colUsuarioId,
            TableColumn<Usuario, String> colUsuarioNombre,
            TableColumn<Usuario, String> colUsuarioEmail,
            TableColumn<Usuario, String> colUsuarioTelefono,
            TableColumn<Usuario, String> colUsuarioDireccion,
            TableColumn<Usuario, Boolean> colUsuarioActivo,
            ComboBox<String> comboBuscarUsuarioNombre,
            ComboBox<String> comboBuscarUsuarioEmail,
            ComboBox<String> comboBuscarUsuarioTelefono,
            ComboBox<String> comboBuscarUsuarioDireccion,
            Button btnLimpiarUsuarios,
            Label lblErrorConexionUsuarios,
            Runnable onDatosActualizados) {

        this.tablaUsuarios = tablaUsuarios;
        this.listaUsuarios = listaUsuarios;
        this.colUsuarioId = colUsuarioId;
        this.colUsuarioNombre = colUsuarioNombre;
        this.colUsuarioEmail = colUsuarioEmail;
        this.colUsuarioTelefono = colUsuarioTelefono;
        this.colUsuarioDireccion = colUsuarioDireccion;
        this.colUsuarioActivo = colUsuarioActivo;
        this.comboBuscarUsuarioNombre = comboBuscarUsuarioNombre;
        this.comboBuscarUsuarioEmail = comboBuscarUsuarioEmail;
        this.comboBuscarUsuarioTelefono = comboBuscarUsuarioTelefono;
        this.comboBuscarUsuarioDireccion = comboBuscarUsuarioDireccion;
        this.btnLimpiarUsuarios = btnLimpiarUsuarios;
        this.lblErrorConexionUsuarios = lblErrorConexionUsuarios;
        this.onDatosActualizados = onDatosActualizados;
    }

    public void configurar() {
        configurarColumnas();
        configurarMenuContextual();
        configurarBuscadores();
    }

    private void configurarColumnas() {
        colUsuarioId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsuarioNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colUsuarioEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colUsuarioTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colUsuarioDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colUsuarioActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
    }

    private void configurarMenuContextual() {
        if (tablaUsuarios == null) return;

        ContextMenu menuContextual = new ContextMenu();

        MenuItem itemEditar = new MenuItem("_Editar usuario");
        itemEditar.setMnemonicParsing(true);
        itemEditar.setGraphic(UIUtils.crearIcono("/miapp/icons/editar.png", 16));
        itemEditar.setOnAction(e -> editar());

        MenuItem itemEliminar = new MenuItem("E_liminar usuario");
        itemEliminar.setMnemonicParsing(true);
        itemEliminar.setGraphic(UIUtils.crearIcono("/miapp/icons/eliminar.png", 16));
        itemEliminar.setOnAction(e -> eliminar());

        MenuItem itemVerDetalles = new MenuItem("_Ver detalles");
        itemVerDetalles.setMnemonicParsing(true);
        itemVerDetalles.setGraphic(UIUtils.crearIcono("/miapp/icons/form.png", 16));
        itemVerDetalles.setOnAction(e -> {
            Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                UIUtils.mostrarInfo("Detalles del Usuario",
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
                    editar();
                }
            }
        });
    }

    public void cargarDatos() {
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
            UIUtils.ocultarErrorConexion(lblErrorConexionUsuarios);

        } catch (SQLException e) {
            System.out.println("Error cargando usuarios: " + e.getMessage());
            UIUtils.mostrarErrorConexion(lblErrorConexionUsuarios);
        }

        tablaUsuarios.setItems(listaUsuarios);
    }

    public void nuevo() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nuevo usuario");
        dialog.setHeaderText("Introduce los datos del nuevo usuario");

        ButtonType btnGuardar = new ButtonType("_Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("_Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

        GridPane grid = UIUtils.crearGridBasico();

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
        UIUtils.añadirIconoADialogo(dialog);

        List<ValidationSupport> validadores = new ArrayList<>();
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

                cargarDatos();
                onDatosActualizados.run();

            } catch (SQLException e) {
                UIUtils.mostrarInfo("Error BBDD", "No se pudo insertar el usuario:\n" + e.getMessage());
            }
        }
    }

    public void editar() {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            UIUtils.mostrarInfo("Editar usuario", "Selecciona primero un usuario de la tabla.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar usuario");
        dialog.setHeaderText("Edita los datos del usuario");

        ButtonType btnGuardar = new ButtonType("_Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("_Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

        GridPane grid = UIUtils.crearGridBasico();

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
        UIUtils.añadirIconoADialogo(dialog);

        List<ValidationSupport> validadores = new ArrayList<>();
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

                cargarDatos();
                onDatosActualizados.run();

            } catch (SQLException e) {
                UIUtils.mostrarInfo("Error BBDD", "No se pudo actualizar el usuario:\n" + e.getMessage());
            }
        }
    }

    public void eliminar() {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            UIUtils.mostrarInfo("Eliminar usuario", "Selecciona primero un usuario de la tabla.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar usuario");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que quieres eliminar al usuario " + seleccionado.getNombre() + "?");
        UIUtils.añadirIconoADialogo(confirm);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM Usuarios WHERE id_usuario = ?";

            try (Connection conn = ConexionBBDD.getConexion(); PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, seleccionado.getId());
                pst.executeUpdate();

                cargarDatos();
                onDatosActualizados.run();

            } catch (SQLException e) {
                UIUtils.mostrarInfo("Error BBDD", "No se pudo eliminar el usuario:\n" + e.getMessage());
            }
        }
    }

    // --- Buscadores ---

    private void configurarBuscadores() {
        if (comboBuscarUsuarioNombre != null) {
            ObservableList<String> nombresUsuarios = FXCollections.observableArrayList();
            UIUtils.configurarBuscadorConAutocompletado(comboBuscarUsuarioNombre, nombresUsuarios,
                () -> listaUsuarios.stream().map(Usuario::getNombre).distinct().toList());
        }
        if (comboBuscarUsuarioEmail != null) {
            ObservableList<String> emailsUsuarios = FXCollections.observableArrayList();
            UIUtils.configurarBuscadorConAutocompletado(comboBuscarUsuarioEmail, emailsUsuarios,
                () -> listaUsuarios.stream().map(Usuario::getEmail).distinct().toList());
        }
        if (comboBuscarUsuarioTelefono != null) {
            ObservableList<String> telefonosUsuarios = FXCollections.observableArrayList();
            UIUtils.configurarBuscadorConAutocompletado(comboBuscarUsuarioTelefono, telefonosUsuarios,
                () -> listaUsuarios.stream().map(Usuario::getTelefono).distinct().toList());
        }
        if (comboBuscarUsuarioDireccion != null) {
            ObservableList<String> direccionesUsuarios = FXCollections.observableArrayList();
            UIUtils.configurarBuscadorConAutocompletado(comboBuscarUsuarioDireccion, direccionesUsuarios,
                () -> listaUsuarios.stream().map(Usuario::getDireccion).distinct().toList());
        }
        if (btnLimpiarUsuarios != null) {
            btnLimpiarUsuarios.setOnAction(e -> buscar());
        }
    }

    public void recargarBuscadores() {
        recargarCombo(comboBuscarUsuarioNombre, () -> listaUsuarios.stream().map(Usuario::getNombre).distinct().toList());
        recargarCombo(comboBuscarUsuarioEmail, () -> listaUsuarios.stream().map(Usuario::getEmail).distinct().toList());
        recargarCombo(comboBuscarUsuarioTelefono, () -> listaUsuarios.stream().map(Usuario::getTelefono).distinct().toList());
        recargarCombo(comboBuscarUsuarioDireccion, () -> listaUsuarios.stream().map(Usuario::getDireccion).distinct().toList());
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
        String nombre = getValor(comboBuscarUsuarioNombre);
        String email = getValor(comboBuscarUsuarioEmail);
        String telefono = getValor(comboBuscarUsuarioTelefono);
        String direccion = getValor(comboBuscarUsuarioDireccion);

        ObservableList<Usuario> filtrados = listaUsuarios.filtered(usuario -> {
            boolean coincide = true;
            if (!nombre.isEmpty()) coincide = coincide && usuario.getNombre().toLowerCase().contains(nombre);
            if (!email.isEmpty()) coincide = coincide && usuario.getEmail().toLowerCase().contains(email);
            if (!telefono.isEmpty()) coincide = coincide && usuario.getTelefono().toLowerCase().contains(telefono);
            if (!direccion.isEmpty()) coincide = coincide && usuario.getDireccion().toLowerCase().contains(direccion);
            return coincide;
        });

        tablaUsuarios.setItems(filtrados);
    }

    private String getValor(ComboBox<String> combo) {
        return combo != null && combo.getValue() != null ? combo.getValue().trim().toLowerCase() : "";
    }
}
