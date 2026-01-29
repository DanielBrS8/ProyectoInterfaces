package com.javafx.proyecto;

import javafx.scene.Scene;
import javafx.scene.web.WebView;
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

    // Carpeta donde se guardan los informes automáticamente
    private static final String CARPETA_INFORMES = "INFORMES";

    /**
     * Genera el informe de mascotas y lo muestra en un WebView (incrustado)
     * También guarda el HTML en la carpeta INFORMES
     */
    public static void lanzarInformeMascotasIncrustado(WebView webView) {
        try {
            JasperPrint jasperPrint = generarInformeMascotas();
            if (jasperPrint != null) {
                File htmlFile = guardarHTMLEnCarpeta(jasperPrint, "informe_mascotas.html");
                webView.getEngine().load(htmlFile.toURI().toString());
            }
        } catch (Exception e) {
            mostrarError("Error al generar informe de mascotas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Genera el informe de mascotas y lo muestra en una ventana nueva
     * También guarda el HTML en la carpeta INFORMES
     */
    public static void lanzarInformeMascotasVentana() {
        try {
            JasperPrint jasperPrint = generarInformeMascotas();
            if (jasperPrint != null) {
                File htmlFile = guardarHTMLEnCarpeta(jasperPrint, "informe_mascotas.html");
                mostrarEnVentanaNueva(htmlFile, "Informe de Mascotas");
            }
        } catch (Exception e) {
            mostrarError("Error al generar informe de mascotas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Exporta el informe de mascotas a PDF en la carpeta INFORMES
     */
    public static void exportarInformeMascotasPDF() {
        try {
            JasperPrint jasperPrint = generarInformeMascotas();
            if (jasperPrint != null) {
                exportarAPDF(jasperPrint, "informe_mascotas.pdf");
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

    /**
     * Genera el informe de adopciones y lo muestra en un WebView (incrustado)
     * También guarda el HTML en la carpeta INFORMES
     */
    public static void lanzarInformeAdopcionesIncrustado(WebView webView, String estadoFiltro) {
        try {
            JasperPrint jasperPrint = generarInformeAdopciones(estadoFiltro);
            if (jasperPrint != null) {
                String nombreArchivo = "informe_adopciones.html";
                File htmlFile = guardarHTMLEnCarpeta(jasperPrint, nombreArchivo);
                webView.getEngine().load(htmlFile.toURI().toString());
            }
        } catch (Exception e) {
            mostrarError("Error al generar informe de adopciones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Genera el informe de adopciones y lo muestra en una ventana nueva
     * También guarda el HTML en la carpeta INFORMES
     */
    public static void lanzarInformeAdopcionesVentana(String estadoFiltro) {
        try {
            JasperPrint jasperPrint = generarInformeAdopciones(estadoFiltro);
            if (jasperPrint != null) {
                String nombreArchivo = "informe_adopciones.html";
                File htmlFile = guardarHTMLEnCarpeta(jasperPrint, nombreArchivo);
                String titulo = "Informe de Adopciones" +
                    (estadoFiltro != null && !estadoFiltro.equals("TODOS") ? " - " + estadoFiltro : "");
                mostrarEnVentanaNueva(htmlFile, titulo);
            }
        } catch (Exception e) {
            mostrarError("Error al generar informe de adopciones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Exporta el informe de adopciones a PDF en la carpeta INFORMES
     */
    public static void exportarInformeAdopcionesPDF(String estadoFiltro) {
        try {
            JasperPrint jasperPrint = generarInformeAdopciones(estadoFiltro);
            if (jasperPrint != null) {
                exportarAPDF(jasperPrint, "informe_adopciones.pdf");
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

    /**
     * Guarda el informe como HTML en la carpeta INFORMES del proyecto
     */
    private static File guardarHTMLEnCarpeta(JasperPrint jasperPrint, String nombreArchivo) throws Exception {
        // Crear carpeta INFORMES si no existe
        File carpeta = new File(CARPETA_INFORMES);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        File archivoHtml = new File(carpeta, nombreArchivo);
        JasperExportManager.exportReportToHtmlFile(jasperPrint, archivoHtml.getAbsolutePath());

        System.out.println("HTML guardado en: " + archivoHtml.getAbsolutePath());
        return archivoHtml;
    }

    /**
     * Muestra un archivo HTML en una ventana nueva
     */
    private static void mostrarEnVentanaNueva(File htmlFile, String titulo) {
        Stage ventana = new Stage();
        ventana.setTitle(titulo);

        WebView webView = new WebView();
        webView.getEngine().load(htmlFile.toURI().toString());

        Scene scene = new Scene(webView, 900, 700);
        ventana.setScene(scene);
        ventana.show();
    }

    /**
     * Exporta un JasperPrint a PDF directamente en la carpeta INFORMES
     */
    private static void exportarAPDF(JasperPrint jasperPrint, String nombreArchivo) throws Exception {
        File carpeta = new File(CARPETA_INFORMES);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        File pdfEnCarpeta = new File(carpeta, nombreArchivo);
        JasperExportManager.exportReportToPdfFile(jasperPrint, pdfEnCarpeta.getAbsolutePath());
        System.out.println("PDF guardado en: " + pdfEnCarpeta.getAbsolutePath());

        mostrarInfo("PDF guardado en:\n" + pdfEnCarpeta.getAbsolutePath());
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
