package com.javafx.proyecto.controlador;

import com.javafx.proyecto.bbdd.PawLinkClient;
import com.javafx.proyecto.modelo.Usuario;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    private final TableColumn<Usuario, String> colUsuarioFechaRegistro;

    private final ComboBox<String> comboBuscarUsuarioNombre;
    private final ComboBox<String> comboBuscarUsuarioEmail;
    private final ComboBox<String> comboBuscarUsuarioTelefono;
    private final Button btnLimpiarUsuarios;

    private final Label lblErrorConexionUsuarios;

    private final Runnable onDatosActualizados;

    // Cache de centros: nombre -> idCentro
    private final Map<String, Integer> mapaCentros = new LinkedHashMap<>();

    public UsuarioCrudController(
            TableView<Usuario> tablaUsuarios,
            ObservableList<Usuario> listaUsuarios,
            TableColumn<Usuario, Integer> colUsuarioId,
            TableColumn<Usuario, String> colUsuarioNombre,
            TableColumn<Usuario, String> colUsuarioEmail,
            TableColumn<Usuario, String> colUsuarioTelefono,
            TableColumn<Usuario, String> colUsuarioDireccion,
            TableColumn<Usuario, Boolean> colUsuarioActivo,
            TableColumn<Usuario, String> colUsuarioFechaRegistro,
            ComboBox<String> comboBuscarUsuarioNombre,
            ComboBox<String> comboBuscarUsuarioEmail,
            ComboBox<String> comboBuscarUsuarioTelefono,
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
        this.colUsuarioFechaRegistro = colUsuarioFechaRegistro;
        this.comboBuscarUsuarioNombre = comboBuscarUsuarioNombre;
        this.comboBuscarUsuarioEmail = comboBuscarUsuarioEmail;
        this.comboBuscarUsuarioTelefono = comboBuscarUsuarioTelefono;
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
        colUsuarioFechaRegistro.setCellValueFactory(cd -> {
            String raw = cd.getValue().getFechaRegistro();
            String formateada = raw == null ? "" : raw.replace("T", " ");
            if (formateada.length() >= 16) formateada = formateada.substring(0, 16);
            return new javafx.beans.property.SimpleStringProperty(formateada);
        });
    }

    private void configurarMenuContextual() {
        if (tablaUsuarios == null) return;

        ContextMenu menuContextual = new ContextMenu();

        MenuItem itemVerDetalles = new MenuItem("_Ver detalles");
        itemVerDetalles.setMnemonicParsing(true);
        itemVerDetalles.setGraphic(UIUtils.crearIcono("/miapp/icons/form.png", 16));
        itemVerDetalles.setOnAction(e -> {
            Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                mostrarDetalles(seleccionado);
            }
        });

        menuContextual.getItems().add(itemVerDetalles);
        tablaUsuarios.setContextMenu(menuContextual);

        tablaUsuarios.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Usuario sel = tablaUsuarios.getSelectionModel().getSelectedItem();
                if (sel != null) {
                    mostrarDetalles(sel);
                }
            }
        });
    }

    private void mostrarDetalles(Usuario u) {
        String titulo = "Detalles del " + capitalizar(etiquetaEntidad());
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(u.getId()).append("\n");
        sb.append("Nombre: ").append(u.getNombre()).append("\n");
        sb.append("Email: ").append(u.getEmail()).append("\n");
        sb.append("Teléfono: ").append(u.getTelefono() != null ? u.getTelefono() : "—").append("\n");
        sb.append("Dirección: ").append(u.getDireccion() != null ? u.getDireccion() : "—").append("\n");
        if (SesionUsuario.getInstancia().isAdmin()) {
            sb.append("Centro: ").append(u.getNombreCentro() != null ? u.getNombreCentro() : "Sin asignar").append("\n");
        }
        if (u.getMonedas() != null) {
            sb.append("Monedas: ").append(u.getMonedas()).append("\n");
        }
        if (u.getFechaRegistro() != null) {
            sb.append("Fecha registro: ").append(u.getFechaRegistro().replace("T", " ")).append("\n");
        }
        sb.append("Activo: ").append(Boolean.TRUE.equals(u.getActivo()) ? "Sí" : "No");
        UIUtils.mostrarInfo(titulo, sb.toString());
    }

    private String capitalizar(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private String rolGestionado() {
        return SesionUsuario.getInstancia().isAdmin() ? "veterinario" : "user";
    }

    private String etiquetaEntidad() {
        return SesionUsuario.getInstancia().isAdmin() ? "veterinario" : "usuario";
    }

    public void cargarDatos() {
        listaUsuarios.clear();
        String token = SesionUsuario.getInstancia().getToken();
        String rolFiltro = rolGestionado();
        System.out.println("[DEBUG] Token para getUsuarios: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "NULL"));

        try {
            List<Map<String, Object>> usuarios = PawLinkClient.getUsuarios(token);

            for (Map<String, Object> u : usuarios) {
                String rol = (String) u.get("rol");
                if (!rolFiltro.equals(rol)) continue;

                Integer id = (Integer) u.get("id");
                String nombre = (String) u.get("nombre");
                String email = (String) u.get("email");
                Object activoRaw = u.get("activo");
                Boolean activo = activoRaw instanceof Boolean ? (Boolean) activoRaw : activoRaw != null && ((Number) activoRaw).intValue() == 1;
                String nombreCentro = (String) u.get("nombreCentro");

                Usuario usuario = new Usuario(id, nombre, email, activo, rol, nombreCentro);
                usuario.setTelefono((String) u.get("telefono"));
                usuario.setDireccion((String) u.get("direccion"));
                Object monedasRaw = u.get("monedas");
                if (monedasRaw instanceof Number) usuario.setMonedas(((Number) monedasRaw).intValue());
                usuario.setFechaRegistro((String) u.get("fechaRegistro"));
                listaUsuarios.add(usuario);
            }
            UIUtils.ocultarErrorConexion(lblErrorConexionUsuarios);

        } catch (Exception e) {
            e.printStackTrace();
            lblErrorConexionUsuarios.setText("Error: " + e.getMessage());
            lblErrorConexionUsuarios.setVisible(true);
            lblErrorConexionUsuarios.setManaged(true);
        }

        tablaUsuarios.setItems(listaUsuarios);
    }

    private void cargarCentros() {
        mapaCentros.clear();
        String token = SesionUsuario.getInstancia().getToken();

        try {
            List<Map<String, Object>> centros = PawLinkClient.getCentros(token);
            for (Map<String, Object> c : centros) {
                Integer idCentro = (Integer) c.get("idCentro");
                String nombre = (String) c.get("nombre");
                if (idCentro != null && nombre != null) {
                    mapaCentros.put(nombre, idCentro);
                }
            }
        } catch (Exception e) {
            System.out.println("Error cargando centros: " + e.getMessage());
        }
    }

    public void nuevo() {
        boolean esAdmin = SesionUsuario.getInstancia().isAdmin();
        String etiqueta = etiquetaEntidad();
        String rol = rolGestionado();

        if (esAdmin) {
            cargarCentros();
            if (mapaCentros.isEmpty()) {
                UIUtils.mostrarInfo("Sin centros", "No hay centros veterinarios registrados. Crea uno primero.");
                return;
            }
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nuevo " + etiqueta);
        dialog.setHeaderText("Introduce los datos del nuevo " + etiqueta);

        ButtonType btnGuardar = new ButtonType("_Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("_Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

        GridPane grid = UIUtils.crearGridBasico();

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Ej: Juan Pérez");

        TextField txtEmail = new TextField();
        txtEmail.setPromptText(etiqueta + "@ejemplo.com");

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Contraseña");

        ComboBox<String> comboCentro = new ComboBox<>();
        if (esAdmin) {
            comboCentro.getItems().addAll(mapaCentros.keySet());
            comboCentro.setPromptText("Selecciona un centro");
            comboCentro.setPrefWidth(220);
        }

        grid.addRow(0, new Label("Nombre:"), txtNombre);
        grid.addRow(1, new Label("Email:"), txtEmail);
        grid.addRow(2, new Label("Contraseña:"), txtPassword);
        if (esAdmin) {
            grid.addRow(3, new Label("Centro:"), comboCentro);
        }

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setMinWidth(500);
        UIUtils.añadirIconoADialogo(dialog);

        dialog.getDialogPane().lookupButton(btnGuardar).setDisable(true);

        Runnable validar = () -> {
            boolean valido = !txtNombre.getText().trim().isEmpty()
                    && !txtEmail.getText().trim().isEmpty()
                    && !txtPassword.getText().trim().isEmpty()
                    && (!esAdmin || comboCentro.getValue() != null);
            dialog.getDialogPane().lookupButton(btnGuardar).setDisable(!valido);
        };

        txtNombre.textProperty().addListener((obs, o, n) -> validar.run());
        txtEmail.textProperty().addListener((obs, o, n) -> validar.run());
        txtPassword.textProperty().addListener((obs, o, n) -> validar.run());
        if (esAdmin) {
            comboCentro.valueProperty().addListener((obs, o, n) -> validar.run());
        }

        javafx.application.Platform.runLater(txtNombre::requestFocus);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == btnGuardar) {
            String token = SesionUsuario.getInstancia().getToken();

            Map<String, Object> body = new HashMap<>();
            body.put("nombre", txtNombre.getText().trim());
            body.put("email", txtEmail.getText().trim());
            body.put("password", txtPassword.getText());
            body.put("rol", rol);
            if (esAdmin) {
                body.put("idCentro", mapaCentros.get(comboCentro.getValue()));
            }

            try {
                PawLinkClient.crearUsuario(body, token);
                cargarDatos();
                onDatosActualizados.run();
                UIUtils.mostrarInfo(capitalizar(etiqueta) + " creado",
                        "Se ha creado el " + etiqueta + " " + txtNombre.getText().trim() + " correctamente.");
            } catch (RuntimeException e) {
                String msg = e.getMessage();
                if (msg != null && msg.contains("409")) {
                    UIUtils.mostrarInfo("Error", "Ya existe un usuario con ese email.");
                } else {
                    UIUtils.mostrarInfo("Error", "No se pudo crear el " + etiqueta + ":\n" + msg);
                }
            } catch (Exception e) {
                UIUtils.mostrarInfo("Error", "Error de conexión con el servidor.");
            }
        }
    }

    public void editar() {
        boolean esAdmin = SesionUsuario.getInstancia().isAdmin();
        String etiqueta = etiquetaEntidad();
        String rol = rolGestionado();

        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            UIUtils.mostrarInfo("Editar " + etiqueta, "Selecciona primero un " + etiqueta + " de la tabla.");
            return;
        }

        if (esAdmin) {
            cargarCentros();
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar " + etiqueta);
        dialog.setHeaderText("Edita los datos del " + etiqueta);

        ButtonType btnGuardar = new ButtonType("_Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("_Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

        GridPane grid = UIUtils.crearGridBasico();

        TextField txtNombre = new TextField(seleccionado.getNombre());
        TextField txtEmail = new TextField(seleccionado.getEmail());

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Dejar vacío para no cambiar");

        ComboBox<String> comboCentro = new ComboBox<>();
        if (esAdmin) {
            comboCentro.getItems().addAll(mapaCentros.keySet());
            comboCentro.setPrefWidth(220);
            if (seleccionado.getNombreCentro() != null) {
                comboCentro.setValue(seleccionado.getNombreCentro());
            }
        }

        grid.addRow(0, new Label("Nombre:"), txtNombre);
        grid.addRow(1, new Label("Email:"), txtEmail);
        grid.addRow(2, new Label("Contraseña:"), txtPassword);
        if (esAdmin) {
            grid.addRow(3, new Label("Centro:"), comboCentro);
        }

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setMinWidth(500);
        UIUtils.añadirIconoADialogo(dialog);

        Runnable validar = () -> {
            boolean valido = !txtNombre.getText().trim().isEmpty()
                    && !txtEmail.getText().trim().isEmpty()
                    && (!esAdmin || comboCentro.getValue() != null);
            dialog.getDialogPane().lookupButton(btnGuardar).setDisable(!valido);
        };

        txtNombre.textProperty().addListener((obs, o, n) -> validar.run());
        txtEmail.textProperty().addListener((obs, o, n) -> validar.run());
        if (esAdmin) {
            comboCentro.valueProperty().addListener((obs, o, n) -> validar.run());
        }

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == btnGuardar) {
            String token = SesionUsuario.getInstancia().getToken();

            Map<String, Object> body = new HashMap<>();
            body.put("nombre", txtNombre.getText().trim());
            body.put("email", txtEmail.getText().trim());
            body.put("rol", rol);
            if (esAdmin) {
                body.put("idCentro", mapaCentros.get(comboCentro.getValue()));
            }
            if (!txtPassword.getText().isEmpty()) {
                body.put("password", txtPassword.getText());
            }

            try {
                PawLinkClient.actualizarUsuario(seleccionado.getId(), body, token);
                cargarDatos();
                onDatosActualizados.run();
            } catch (RuntimeException e) {
                String msg = e.getMessage();
                if (msg != null && msg.contains("409")) {
                    UIUtils.mostrarInfo("Error", "Ya existe un usuario con ese email.");
                } else {
                    UIUtils.mostrarInfo("Error", "No se pudo actualizar el " + etiqueta + ":\n" + msg);
                }
            } catch (Exception e) {
                UIUtils.mostrarInfo("Error", "Error de conexión con el servidor.");
            }
        }
    }

    public void eliminar() {
        String etiqueta = etiquetaEntidad();
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            UIUtils.mostrarInfo("Eliminar " + etiqueta, "Selecciona primero un " + etiqueta + " de la tabla.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar " + etiqueta);
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que quieres eliminar al " + etiqueta + " " + seleccionado.getNombre() + "?");
        UIUtils.añadirIconoADialogo(confirm);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String token = SesionUsuario.getInstancia().getToken();

            try {
                PawLinkClient.eliminarUsuario(seleccionado.getId(), token);
                cargarDatos();
                onDatosActualizados.run();
            } catch (Exception e) {
                UIUtils.mostrarInfo("Error", "No se pudo eliminar el " + etiqueta + ":\n" + e.getMessage());
            }
        }
    }

    // --- Buscadores ---

    private void configurarBuscadores() {
        if (comboBuscarUsuarioNombre != null) {
            ObservableList<String> nombres = FXCollections.observableArrayList();
            UIUtils.configurarBuscadorConAutocompletado(comboBuscarUsuarioNombre, nombres,
                () -> listaUsuarios.stream().map(Usuario::getNombre).distinct().toList());
        }
        if (comboBuscarUsuarioEmail != null) {
            ObservableList<String> emails = FXCollections.observableArrayList();
            UIUtils.configurarBuscadorConAutocompletado(comboBuscarUsuarioEmail, emails,
                () -> listaUsuarios.stream().map(Usuario::getEmail).distinct().toList());
        }
        if (comboBuscarUsuarioTelefono != null) {
            ObservableList<String> telefonos = FXCollections.observableArrayList();
            UIUtils.configurarBuscadorConAutocompletado(comboBuscarUsuarioTelefono, telefonos,
                () -> listaUsuarios.stream()
                        .map(Usuario::getTelefono)
                        .filter(t -> t != null)
                        .distinct().toList());
        }
        if (btnLimpiarUsuarios != null) {
            btnLimpiarUsuarios.setOnAction(e -> buscar());
        }
    }

    public void recargarBuscadores() {
        recargarCombo(comboBuscarUsuarioNombre, () -> listaUsuarios.stream().map(Usuario::getNombre).distinct().toList());
        recargarCombo(comboBuscarUsuarioEmail, () -> listaUsuarios.stream().map(Usuario::getEmail).distinct().toList());
        recargarCombo(comboBuscarUsuarioTelefono, () -> listaUsuarios.stream()
                .map(Usuario::getTelefono).filter(t -> t != null).distinct().toList());
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

        ObservableList<Usuario> filtrados = listaUsuarios.filtered(usuario -> {
            boolean coincide = true;
            if (!nombre.isEmpty()) coincide = usuario.getNombre().toLowerCase().contains(nombre);
            if (!email.isEmpty()) coincide = coincide && usuario.getEmail().toLowerCase().contains(email);
            if (!telefono.isEmpty()) coincide = coincide && usuario.getTelefono() != null
                    && usuario.getTelefono().toLowerCase().contains(telefono);
            return coincide;
        });

        tablaUsuarios.setItems(filtrados);
    }

    private String getValor(ComboBox<String> combo) {
        return combo != null && combo.getValue() != null ? combo.getValue().trim().toLowerCase() : "";
    }
}
