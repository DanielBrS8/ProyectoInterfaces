package com.javafx.proyecto.controlador;

import com.javafx.proyecto.bbdd.ConexionBBDD;
import com.javafx.proyecto.modelo.Mascota;
import com.javafx.proyecto.util.UIUtils;
import com.javafx.proyecto.util.ValidadorForms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;

import org.controlsfx.validation.ValidationSupport;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MascotaCrudController {

    private final TableView<Mascota> tablaMascotas;
    private final ObservableList<Mascota> listaMascotas;
    private final TableColumn<Mascota, Integer> colMascotaId;
    private final TableColumn<Mascota, String> colMascotaNombre;
    private final TableColumn<Mascota, String> colMascotaEspecie;
    private final TableColumn<Mascota, String> colMascotaRaza;
    private final TableColumn<Mascota, LocalDate> colMascotaFechaNac;
    private final TableColumn<Mascota, Double> colMascotaPeso;
    private final TableColumn<Mascota, String> colMascotaEstadoSalud;
    private final TableColumn<Mascota, Boolean> colMascotaDisponible;

    private final BarChart<String, Number> graficaEspecies;
    private final CategoryAxis ejeXEspecies;
    private final NumberAxis ejeYEspecies;

    private final ComboBox<String> comboBuscarMascotaNombre;
    private final ComboBox<String> comboBuscarMascotaEspecie;
    private final ComboBox<String> comboBuscarMascotaRaza;
    private final ComboBox<String> comboBuscarMascotaEstadoSalud;
    private final Button btnLimpiarMascotas;

    private final Label lblErrorConexionMascotas;

    private final Runnable onDatosActualizados;

    public MascotaCrudController(
            TableView<Mascota> tablaMascotas,
            ObservableList<Mascota> listaMascotas,
            TableColumn<Mascota, Integer> colMascotaId,
            TableColumn<Mascota, String> colMascotaNombre,
            TableColumn<Mascota, String> colMascotaEspecie,
            TableColumn<Mascota, String> colMascotaRaza,
            TableColumn<Mascota, LocalDate> colMascotaFechaNac,
            TableColumn<Mascota, Double> colMascotaPeso,
            TableColumn<Mascota, String> colMascotaEstadoSalud,
            TableColumn<Mascota, Boolean> colMascotaDisponible,
            BarChart<String, Number> graficaEspecies,
            CategoryAxis ejeXEspecies,
            NumberAxis ejeYEspecies,
            ComboBox<String> comboBuscarMascotaNombre,
            ComboBox<String> comboBuscarMascotaEspecie,
            ComboBox<String> comboBuscarMascotaRaza,
            ComboBox<String> comboBuscarMascotaEstadoSalud,
            Button btnLimpiarMascotas,
            Label lblErrorConexionMascotas,
            Runnable onDatosActualizados) {

        this.tablaMascotas = tablaMascotas;
        this.listaMascotas = listaMascotas;
        this.colMascotaId = colMascotaId;
        this.colMascotaNombre = colMascotaNombre;
        this.colMascotaEspecie = colMascotaEspecie;
        this.colMascotaRaza = colMascotaRaza;
        this.colMascotaFechaNac = colMascotaFechaNac;
        this.colMascotaPeso = colMascotaPeso;
        this.colMascotaEstadoSalud = colMascotaEstadoSalud;
        this.colMascotaDisponible = colMascotaDisponible;
        this.graficaEspecies = graficaEspecies;
        this.ejeXEspecies = ejeXEspecies;
        this.ejeYEspecies = ejeYEspecies;
        this.comboBuscarMascotaNombre = comboBuscarMascotaNombre;
        this.comboBuscarMascotaEspecie = comboBuscarMascotaEspecie;
        this.comboBuscarMascotaRaza = comboBuscarMascotaRaza;
        this.comboBuscarMascotaEstadoSalud = comboBuscarMascotaEstadoSalud;
        this.btnLimpiarMascotas = btnLimpiarMascotas;
        this.lblErrorConexionMascotas = lblErrorConexionMascotas;
        this.onDatosActualizados = onDatosActualizados;
    }

    public void configurar() {
        configurarColumnas();
        configurarMenuContextual();
        configurarBuscadores();
    }

    private void configurarColumnas() {
        colMascotaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMascotaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colMascotaEspecie.setCellValueFactory(new PropertyValueFactory<>("especie"));
        colMascotaRaza.setCellValueFactory(new PropertyValueFactory<>("raza"));
        colMascotaFechaNac.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colMascotaPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));
        colMascotaEstadoSalud.setCellValueFactory(new PropertyValueFactory<>("estadoSalud"));
        colMascotaDisponible.setCellValueFactory(new PropertyValueFactory<>("disponible"));
    }

    private void configurarMenuContextual() {
        if (tablaMascotas == null) return;

        ContextMenu menuContextual = new ContextMenu();

        MenuItem itemEditar = new MenuItem("_Editar mascota");
        itemEditar.setMnemonicParsing(true);
        itemEditar.setGraphic(UIUtils.crearIcono("/miapp/icons/editar.png", 16));
        itemEditar.setOnAction(e -> editar());

        MenuItem itemEliminar = new MenuItem("E_liminar mascota");
        itemEliminar.setMnemonicParsing(true);
        itemEliminar.setGraphic(UIUtils.crearIcono("/miapp/icons/eliminar.png", 16));
        itemEliminar.setOnAction(e -> eliminar());

        MenuItem itemVerDetalles = new MenuItem("_Ver detalles");
        itemVerDetalles.setMnemonicParsing(true);
        itemVerDetalles.setGraphic(UIUtils.crearIcono("/miapp/icons/form.png", 16));
        itemVerDetalles.setOnAction(e -> {
            Mascota seleccionada = tablaMascotas.getSelectionModel().getSelectedItem();
            if (seleccionada != null) {
                UIUtils.mostrarInfo("Detalles de la Mascota",
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
        itemCambiarDisponibilidad.setGraphic(UIUtils.crearIcono("/miapp/icons/paw.png", 16));
        itemCambiarDisponibilidad.setOnAction(e -> {
            Mascota seleccionada = tablaMascotas.getSelectionModel().getSelectedItem();
            if (seleccionada != null) {
                boolean nuevoEstado = !seleccionada.getDisponible();
                String sql = "UPDATE Mascotas SET disponible_alquiler = ? WHERE id_mascota = ?";

                try (Connection conn = ConexionBBDD.getConexion();
                        PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setInt(1, nuevoEstado ? 1 : 0);
                    pst.setInt(2, seleccionada.getId());
                    pst.executeUpdate();
                    cargarDatos();
                    UIUtils.mostrarInfo("Éxito", "Disponibilidad actualizada");
                } catch (SQLException ex) {
                    UIUtils.mostrarInfo("Error", "No se pudo actualizar: " + ex.getMessage());
                }
            }
        });

        menuContextual.getItems().addAll(itemEditar, itemEliminar, new SeparatorMenuItem(),
                itemVerDetalles, itemCambiarDisponibilidad);

        tablaMascotas.setContextMenu(menuContextual);

        tablaMascotas.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                if (tablaMascotas.getSelectionModel().getSelectedItem() != null) {
                    editar();
                }
            }
        });
    }

    public void cargarDatos() {
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
            UIUtils.ocultarErrorConexion(lblErrorConexionMascotas);

        } catch (SQLException e) {
            System.out.println("Error cargando mascotas: " + e.getMessage());
            UIUtils.mostrarErrorConexion(lblErrorConexionMascotas);
        }

        tablaMascotas.setItems(listaMascotas);
    }

    public void rellenarGraficaEspecies() {
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

        if (ejeXEspecies != null) ejeXEspecies.setLabel("Especie");
        if (ejeYEspecies != null) ejeYEspecies.setLabel("Número de mascotas");
    }

    public void nuevo() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nueva mascota");
        dialog.setHeaderText("Introduce los datos de la mascota");

        ButtonType btnGuardar = new ButtonType("_Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("_Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

        GridPane grid = UIUtils.crearGridBasico();

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
        UIUtils.añadirIconoADialogo(dialog);

        List<ValidationSupport> validadores = new ArrayList<>();
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
                UIUtils.mostrarInfo("Dato inválido", "El peso no es un número válido.");
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
                pst.setInt(7, chkDisponible.isSelected() ? 1 : 0);
                pst.executeUpdate();

                cargarDatos();
                rellenarGraficaEspecies();
                onDatosActualizados.run();

            } catch (SQLException e) {
                UIUtils.mostrarInfo("Error BBDD", "No se pudo insertar la mascota:\n" + e.getMessage());
            }
        }
    }

    public void editar() {
        Mascota seleccionada = tablaMascotas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            UIUtils.mostrarInfo("Editar mascota", "Selecciona primero una mascota de la tabla.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar mascota");
        dialog.setHeaderText("Edita los datos de la mascota");

        ButtonType btnGuardar = new ButtonType("_Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("_Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

        GridPane grid = UIUtils.crearGridBasico();

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
        UIUtils.añadirIconoADialogo(dialog);

        List<ValidationSupport> validadores = new ArrayList<>();
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
                UIUtils.mostrarInfo("Dato inválido", "El peso no es un número válido.");
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
                pst.setInt(7, chkDisponible.isSelected() ? 1 : 0);
                pst.setInt(8, seleccionada.getId());
                pst.executeUpdate();

                cargarDatos();
                rellenarGraficaEspecies();
                onDatosActualizados.run();

            } catch (SQLException e) {
                UIUtils.mostrarInfo("Error BBDD", "No se pudo actualizar la mascota:\n" + e.getMessage());
            }
        }
    }

    public void eliminar() {
        Mascota seleccionada = tablaMascotas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            UIUtils.mostrarInfo("Eliminar mascota", "Selecciona primero una mascota de la tabla.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar mascota");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que quieres eliminar la mascota " + seleccionada.getNombre() + "?");
        UIUtils.añadirIconoADialogo(confirm);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM Mascotas WHERE id_mascota = ?";

            try (Connection conn = ConexionBBDD.getConexion(); PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, seleccionada.getId());
                pst.executeUpdate();

                cargarDatos();
                rellenarGraficaEspecies();
                onDatosActualizados.run();

            } catch (SQLException e) {
                UIUtils.mostrarInfo("Error BBDD", "No se pudo eliminar la mascota:\n" + e.getMessage());
            }
        }
    }

    // --- Buscadores ---

    private void configurarBuscadores() {
        if (comboBuscarMascotaNombre != null) {
            ObservableList<String> nombres = FXCollections.observableArrayList();
            UIUtils.configurarBuscadorConAutocompletado(comboBuscarMascotaNombre, nombres,
                () -> listaMascotas.stream().map(Mascota::getNombre).distinct().toList());
        }
        if (comboBuscarMascotaEspecie != null) {
            ObservableList<String> especies = FXCollections.observableArrayList();
            UIUtils.configurarBuscadorConAutocompletado(comboBuscarMascotaEspecie, especies,
                () -> listaMascotas.stream().map(Mascota::getEspecie).distinct().toList());
        }
        if (comboBuscarMascotaRaza != null) {
            ObservableList<String> razas = FXCollections.observableArrayList();
            UIUtils.configurarBuscadorConAutocompletado(comboBuscarMascotaRaza, razas,
                () -> listaMascotas.stream().map(Mascota::getRaza).distinct().toList());
        }
        if (comboBuscarMascotaEstadoSalud != null) {
            ObservableList<String> estados = FXCollections.observableArrayList();
            UIUtils.configurarBuscadorConAutocompletado(comboBuscarMascotaEstadoSalud, estados,
                () -> listaMascotas.stream().map(Mascota::getEstadoSalud).distinct().toList());
        }
        if (btnLimpiarMascotas != null) {
            btnLimpiarMascotas.setOnAction(e -> buscar());
        }
    }

    public void recargarBuscadores() {
        recargarCombo(comboBuscarMascotaNombre, () -> listaMascotas.stream().map(Mascota::getNombre).distinct().toList());
        recargarCombo(comboBuscarMascotaEspecie, () -> listaMascotas.stream().map(Mascota::getEspecie).distinct().toList());
        recargarCombo(comboBuscarMascotaRaza, () -> listaMascotas.stream().map(Mascota::getRaza).distinct().toList());
        recargarCombo(comboBuscarMascotaEstadoSalud, () -> listaMascotas.stream().map(Mascota::getEstadoSalud).distinct().toList());
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
        String nombre = getValor(comboBuscarMascotaNombre);
        String especie = getValor(comboBuscarMascotaEspecie);
        String raza = getValor(comboBuscarMascotaRaza);
        String estadoSalud = getValor(comboBuscarMascotaEstadoSalud);

        ObservableList<Mascota> filtrados = listaMascotas.filtered(mascota -> {
            boolean coincide = true;
            if (!nombre.isEmpty()) coincide = coincide && mascota.getNombre().toLowerCase().contains(nombre);
            if (!especie.isEmpty()) coincide = coincide && mascota.getEspecie().toLowerCase().contains(especie);
            if (!raza.isEmpty()) coincide = coincide && mascota.getRaza().toLowerCase().contains(raza);
            if (!estadoSalud.isEmpty()) coincide = coincide && mascota.getEstadoSalud().toLowerCase().contains(estadoSalud);
            return coincide;
        });

        tablaMascotas.setItems(filtrados);
    }

    private String getValor(ComboBox<String> combo) {
        return combo != null && combo.getValue() != null ? combo.getValue().trim().toLowerCase() : "";
    }
}
