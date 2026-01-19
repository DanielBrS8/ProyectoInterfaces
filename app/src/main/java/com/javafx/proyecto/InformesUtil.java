package com.javafx.proyecto;

import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class InformesUtil {

    // ==================== INFORME DE MASCOTAS ====================

    /**
     * Genera el informe de mascotas y lo muestra en un WebView (incrustado)
     */
    public static void lanzarInformeMascotasIncrustado(WebView webView) {
        try {
            JasperPrint jasperPrint = generarInformeMascotas();
            if (jasperPrint != null) {
                mostrarEnWebView(jasperPrint, webView);
            }
        } catch (Exception e) {
            mostrarError("Error al generar informe de mascotas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Genera el informe de mascotas y lo muestra en una ventana nueva
     */
    public static void lanzarInformeMascotasVentana() {
        try {
            JasperPrint jasperPrint = generarInformeMascotas();
            if (jasperPrint != null) {
                mostrarEnVentanaNueva(jasperPrint, "Informe de Mascotas");
            }
        } catch (Exception e) {
            mostrarError("Error al generar informe de mascotas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Exporta el informe de mascotas a PDF
     */
    public static void exportarInformeMascotasPDF() {
        try {
            JasperPrint jasperPrint = generarInformeMascotas();
            if (jasperPrint != null) {
                exportarAPDF(jasperPrint, "Guardar Informe de Mascotas", "informe_mascotas.pdf");
            }
        } catch (Exception e) {
            mostrarError("Error al exportar PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static JasperPrint generarInformeMascotas() throws Exception {
        InputStream jasperStream = InformesUtil.class.getResourceAsStream("/informes/informe_mascotas.jasper");
        if (jasperStream == null) {
            mostrarError("No se encontró el archivo informe_mascotas.jasper");
            return null;
        }

        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
        Connection conexion = ConexionBBDD.getConexion();
        return JasperFillManager.fillReport(jasperReport, new HashMap<>(), conexion);
    }

    // ==================== INFORME DE ADOPCIONES/ALQUILERES ====================

    /**
     * Genera el informe de adopciones y lo muestra en un WebView (incrustado)
     */
    public static void lanzarInformeAdopcionesIncrustado(WebView webView, String estadoFiltro) {
        try {
            JasperPrint jasperPrint = generarInformeAdopciones(estadoFiltro);
            if (jasperPrint != null) {
                mostrarEnWebView(jasperPrint, webView);
            }
        } catch (Exception e) {
            mostrarError("Error al generar informe de adopciones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Genera el informe de adopciones y lo muestra en una ventana nueva
     */
    public static void lanzarInformeAdopcionesVentana(String estadoFiltro) {
        try {
            JasperPrint jasperPrint = generarInformeAdopciones(estadoFiltro);
            if (jasperPrint != null) {
                String titulo = "Informe de Adopciones" +
                    (estadoFiltro != null && !estadoFiltro.equals("TODOS") ? " - " + estadoFiltro : "");
                mostrarEnVentanaNueva(jasperPrint, titulo);
            }
        } catch (Exception e) {
            mostrarError("Error al generar informe de adopciones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Exporta el informe de adopciones a PDF
     */
    public static void exportarInformeAdopcionesPDF(String estadoFiltro) {
        try {
            JasperPrint jasperPrint = generarInformeAdopciones(estadoFiltro);
            if (jasperPrint != null) {
                exportarAPDF(jasperPrint, "Guardar Informe de Adopciones", "informe_adopciones.pdf");
            }
        } catch (Exception e) {
            mostrarError("Error al exportar PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static JasperPrint generarInformeAdopciones(String estadoFiltro) throws Exception {
        InputStream jasperStream = InformesUtil.class.getResourceAsStream("/informes/informe_alquileres.jasper");
        if (jasperStream == null) {
            mostrarError("No se encontró el archivo informe_alquileres.jasper");
            return null;
        }

        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
        Connection conexion = ConexionBBDD.getConexion();

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("ESTADO_FILTRO", estadoFiltro != null ? estadoFiltro : "TODOS");

        return JasperFillManager.fillReport(jasperReport, parametros, conexion);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Muestra un JasperPrint en un WebView exportándolo a HTML temporal
     */
    private static void mostrarEnWebView(JasperPrint jasperPrint, WebView webView) throws Exception {
        File tempHtml = File.createTempFile("informe_", ".html");
        tempHtml.deleteOnExit();

        JasperExportManager.exportReportToHtmlFile(jasperPrint, tempHtml.getAbsolutePath());
        webView.getEngine().load(tempHtml.toURI().toString());
    }

    /**
     * Muestra un JasperPrint en una ventana nueva
     */
    private static void mostrarEnVentanaNueva(JasperPrint jasperPrint, String titulo) throws Exception {
        File tempHtml = File.createTempFile("informe_", ".html");
        tempHtml.deleteOnExit();

        JasperExportManager.exportReportToHtmlFile(jasperPrint, tempHtml.getAbsolutePath());

        Stage ventana = new Stage();
        ventana.setTitle(titulo);

        WebView webView = new WebView();
        webView.getEngine().load(tempHtml.toURI().toString());

        Scene scene = new Scene(webView, 900, 700);
        ventana.setScene(scene);
        ventana.show();
    }

    /**
     * Exporta un JasperPrint a PDF con diálogo de guardado
     */
    private static void exportarAPDF(JasperPrint jasperPrint, String tituloDialogo, String nombreArchivo) throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(tituloDialogo);
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf")
        );
        fileChooser.setInitialFileName(nombreArchivo);

        File archivoDestino = fileChooser.showSaveDialog(null);
        if (archivoDestino != null) {
            JasperExportManager.exportReportToPdfFile(jasperPrint, archivoDestino.getAbsolutePath());
            mostrarInfo("PDF exportado correctamente en:\n" + archivoDestino.getAbsolutePath());
        }
    }

    private static void mostrarError(String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private static void mostrarInfo(String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION
        );
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
