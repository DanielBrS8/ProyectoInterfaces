package com.javafx.proyecto.controlador;

import com.javafx.proyecto.bbdd.PawLinkClient;
import com.javafx.proyecto.modelo.Mascota;
import com.javafx.proyecto.modelo.Vacuna;
import com.javafx.proyecto.util.SesionUsuario;
import com.javafx.proyecto.util.UIUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VacunacionController {

    private final Mascota mascota;
    private final ObservableList<Vacuna> listaVacunas = FXCollections.observableArrayList();
    private Label lblEstadoCarga;

    public VacunacionController(Mascota mascota) {
        this.mascota = mascota;
    }

    public VBox crearPanel() {
        VBox root = new VBox(14);
        root.setPadding(new Insets(15));

        // --- Historial ---
        Label lblHistorial = new Label("Historial de vacunas - " + mascota.getNombre());
        lblHistorial.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        TableView<Vacuna> tablaVacunas = new TableView<>(listaVacunas);
        tablaVacunas.setPrefHeight(200);
        tablaVacunas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tablaVacunas.setPlaceholder(new Label("Sin vacunas registradas"));

        TableColumn<Vacuna, String> colNombre = new TableColumn<>("Nombre vacuna");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Vacuna, LocalDate> colFechaAdmin = new TableColumn<>("Fecha admin.");
        colFechaAdmin.setCellValueFactory(new PropertyValueFactory<>("fechaAdministracion"));

        TableColumn<Vacuna, LocalDate> colProximaDosis = new TableColumn<>("Próxima dosis");
        colProximaDosis.setCellValueFactory(new PropertyValueFactory<>("fechaProximaDosis"));

        TableColumn<Vacuna, String> colVeterinario = new TableColumn<>("Veterinario");
        colVeterinario.setCellValueFactory(new PropertyValueFactory<>("veterinario"));

        TableColumn<Vacuna, String> colNotas = new TableColumn<>("Notas");
        colNotas.setCellValueFactory(new PropertyValueFactory<>("notas"));

        tablaVacunas.getColumns().add(colNombre);
        tablaVacunas.getColumns().add(colFechaAdmin);
        tablaVacunas.getColumns().add(colProximaDosis);
        tablaVacunas.getColumns().add(colVeterinario);
        tablaVacunas.getColumns().add(colNotas);

        lblEstadoCarga = new Label("⚠ Error al cargar el historial de vacunas");
        lblEstadoCarga.setStyle(
                "-fx-text-fill: #d32f2f; -fx-font-weight: bold; -fx-background-color: #ffebee;" +
                "-fx-padding: 6px; -fx-border-color: #d32f2f; -fx-border-width: 1px;");
        lblEstadoCarga.setVisible(false);
        lblEstadoCarga.setManaged(false);

        // --- Formulario ---
        Separator sep = new Separator();

        Label lblFormulario = new Label("Registrar nueva vacuna");
        lblFormulario.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        GridPane form = UIUtils.crearGridBasico();

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Ej: Rabia, Parvovirus, Moquillo...");

        DatePicker dpFechaAdmin = new DatePicker(LocalDate.now());

        DatePicker dpProximaDosis = new DatePicker();
        dpProximaDosis.setPromptText("Opcional");

        TextField txtVeterinario = new TextField();
        txtVeterinario.setPromptText("Nombre del veterinario (opcional)");

        TextField txtNotas = new TextField();
        txtNotas.setPromptText("Observaciones adicionales (opcional)");

        form.addRow(0, new Label("Vacuna *:"), txtNombre);
        form.addRow(1, new Label("Fecha admin. *:"), dpFechaAdmin);
        form.addRow(2, new Label("Próxima dosis:"), dpProximaDosis);
        form.addRow(3, new Label("Veterinario:"), txtVeterinario);
        form.addRow(4, new Label("Notas:"), txtNotas);

        Label lblMensaje = new Label();
        lblMensaje.setVisible(false);
        lblMensaje.setManaged(false);

        Button btnGuardar = new Button("_Guardar");
        btnGuardar.setMnemonicParsing(true);
        btnGuardar.setStyle(
                "-fx-background-color: #0078d4; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 16 6 16;");

        btnGuardar.setOnAction(e -> {
            if (txtNombre.getText().trim().isEmpty()) {
                mostrarMensaje(lblMensaje, "El nombre de la vacuna es obligatorio.", false);
                return;
            }
            if (dpFechaAdmin.getValue() == null) {
                mostrarMensaje(lblMensaje, "La fecha de administración es obligatoria.", false);
                return;
            }
            btnGuardar.setDisable(true);
            guardarVacuna(txtNombre, dpFechaAdmin, dpProximaDosis, txtVeterinario, txtNotas, lblMensaje, btnGuardar);
        });

        HBox barraGuardar = new HBox(12, btnGuardar, lblMensaje);
        barraGuardar.setAlignment(Pos.CENTER_LEFT);
        barraGuardar.setPadding(new Insets(0, 0, 0, 20));

        root.getChildren().addAll(
                lblHistorial, tablaVacunas, lblEstadoCarga,
                sep, lblFormulario, form, barraGuardar);

        cargarVacunas();

        return root;
    }

    private void cargarVacunas() {
        Task<List<Map<String, Object>>> task = new Task<>() {
            @Override
            protected List<Map<String, Object>> call() throws Exception {
                return PawLinkClient.getVacunas(mascota.getId(), SesionUsuario.getInstancia().getToken());
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            listaVacunas.clear();
            for (Map<String, Object> v : task.getValue()) {
                Integer id = v.get("idVacuna") instanceof Number n ? n.intValue() : null;
                String nombre = (String) v.get("nombreVacuna");
                String fechaAdminStr = (String) v.get("fechaAdministracion");
                LocalDate fechaAdmin = fechaAdminStr != null ? LocalDate.parse(fechaAdminStr) : null;
                String fechaProxStr = (String) v.get("fechaProximaDosis");
                LocalDate fechaProx = fechaProxStr != null ? LocalDate.parse(fechaProxStr) : null;
                String veterinario = (String) v.get("veterinario");
                String notas = (String) v.get("notas");
                listaVacunas.add(new Vacuna(id, nombre, fechaAdmin, fechaProx, veterinario, notas, mascota.getId()));
            }
            UIUtils.ocultarErrorConexion(lblEstadoCarga);
        }));

        task.setOnFailed(e -> Platform.runLater(() ->
                UIUtils.mostrarErrorConexion(lblEstadoCarga)));

        new Thread(task, "VacunasLoadThread").start();
    }

    private void guardarVacuna(TextField txtNombre, DatePicker dpFechaAdmin,
                               DatePicker dpProximaDosis, TextField txtVeterinario,
                               TextField txtNotas, Label lblMensaje, Button btnGuardar) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("idMascota", mascota.getId());
        body.put("nombreVacuna", txtNombre.getText().trim());
        body.put("fechaAdministracion", dpFechaAdmin.getValue().toString());
        body.put("fechaProximaDosis",
                dpProximaDosis.getValue() != null ? dpProximaDosis.getValue().toString() : null);
        String vet = txtVeterinario.getText().trim();
        body.put("veterinario", vet.isEmpty() ? null : vet);
        String notas = txtNotas.getText().trim();
        body.put("notas", notas.isEmpty() ? null : notas);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                PawLinkClient.registrarVacuna(body, SesionUsuario.getInstancia().getToken());
                return null;
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            txtNombre.clear();
            dpFechaAdmin.setValue(LocalDate.now());
            dpProximaDosis.setValue(null);
            txtVeterinario.clear();
            txtNotas.clear();
            btnGuardar.setDisable(false);
            mostrarMensaje(lblMensaje, "Vacuna registrada correctamente.", true);
            cargarVacunas();
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            btnGuardar.setDisable(false);
            mostrarMensaje(lblMensaje,
                    "Error al guardar: " + task.getException().getMessage(), false);
        }));

        new Thread(task, "VacunaSaveThread").start();
    }

    private void mostrarMensaje(Label lbl, String texto, boolean exito) {
        lbl.setText(texto);
        lbl.setStyle(exito
                ? "-fx-text-fill: #388e3c; -fx-font-weight: bold;"
                : "-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
        lbl.setVisible(true);
        lbl.setManaged(true);
    }
}
