package com.javafx.proyecto.controlador;

import com.javafx.proyecto.util.InformesUtil;
import com.javafx.proyecto.util.UIUtils;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;

public class InformesController {

    private final WebView webViewInforme;
    private final ComboBox<String> comboFiltroEstadoInforme;
    private final Button btnMascotasIncrustado;
    private final Button btnMascotasVentana;
    private final Button btnMascotasPdf;
    private final Button btnAdopcionesIncrustado;
    private final Button btnAdopcionesVentana;
    private final Button btnAdopcionesPdf;

    public InformesController(
            WebView webViewInforme,
            ComboBox<String> comboFiltroEstadoInforme,
            Button btnMascotasIncrustado,
            Button btnMascotasVentana,
            Button btnMascotasPdf,
            Button btnAdopcionesIncrustado,
            Button btnAdopcionesVentana,
            Button btnAdopcionesPdf) {

        this.webViewInforme = webViewInforme;
        this.comboFiltroEstadoInforme = comboFiltroEstadoInforme;
        this.btnMascotasIncrustado = btnMascotasIncrustado;
        this.btnMascotasVentana = btnMascotasVentana;
        this.btnMascotasPdf = btnMascotasPdf;
        this.btnAdopcionesIncrustado = btnAdopcionesIncrustado;
        this.btnAdopcionesVentana = btnAdopcionesVentana;
        this.btnAdopcionesPdf = btnAdopcionesPdf;
    }

    public void configurar() {
        if (comboFiltroEstadoInforme != null) {
            comboFiltroEstadoInforme.getItems().addAll("TODOS", "activo", "pendiente", "finalizado");
            comboFiltroEstadoInforme.setValue("TODOS");
        }

        // INFORME DE MASCOTAS
        if (btnMascotasIncrustado != null) {
            btnMascotasIncrustado.setOnAction(e -> {
                if (webViewInforme != null) {
                    InformesUtil.lanzarInformeMascotasIncrustado(webViewInforme);
                }
            });
        }

        if (btnMascotasVentana != null) {
            btnMascotasVentana.setOnAction(e -> InformesUtil.lanzarInformeMascotasVentana());
        }

        if (btnMascotasPdf != null) {
            btnMascotasPdf.setOnAction(e -> InformesUtil.exportarInformeMascotasPDF());
        }

        // INFORME DE ADOPCIONES
        if (btnAdopcionesIncrustado != null) {
            btnAdopcionesIncrustado.setOnAction(e -> {
                if (webViewInforme != null) {
                    String filtro = comboFiltroEstadoInforme != null ?
                        comboFiltroEstadoInforme.getValue() : "TODOS";
                    InformesUtil.lanzarInformeAdopcionesIncrustado(webViewInforme, filtro);
                }
            });
        }

        if (btnAdopcionesVentana != null) {
            btnAdopcionesVentana.setOnAction(e -> {
                String filtro = comboFiltroEstadoInforme != null ?
                    comboFiltroEstadoInforme.getValue() : "TODOS";
                InformesUtil.lanzarInformeAdopcionesVentana(filtro);
            });
        }

        if (btnAdopcionesPdf != null) {
            btnAdopcionesPdf.setOnAction(e -> {
                String filtro = comboFiltroEstadoInforme != null ?
                    comboFiltroEstadoInforme.getValue() : "TODOS";
                InformesUtil.exportarInformeAdopcionesPDF(filtro);
            });
        }
    }

    public void nuevo() {
        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Nuevo informe");
        dialog.setHeaderText("Introduce los datos del informe");

        javafx.scene.control.ButtonType btnAceptar = new javafx.scene.control.ButtonType("Aceptar",
                javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        javafx.scene.control.ButtonType btnCancelar = new javafx.scene.control.ButtonType("Cancelar",
                javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnAceptar, btnCancelar);

        GridPane grid = UIUtils.crearGridBasico();

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
        UIUtils.añadirIconoADialogo(dialog);
        dialog.showAndWait();
    }

    public void editar() {
        UIUtils.mostrarInfo("Informes", "Los informes se generan automáticamente desde la base de datos.");
    }

    public void eliminar() {
        UIUtils.mostrarInfo("Informes", "Los informes se generan automáticamente desde la base de datos.");
    }
}
