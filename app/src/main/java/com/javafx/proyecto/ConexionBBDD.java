package com.javafx.proyecto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBBDD {
    private static String URL;
    private static String USER;
    private static String PASS;

    private static Connection conexion;

    static {
        cargarConfiguracion();
    }

    private static void cargarConfiguracion() {
        Properties props = new Properties();

        // Primero intenta cargar desde archivo externo (junto al JAR)
        File archivoExterno = new File("ip.properties");
        if (archivoExterno.exists()) {
            try (FileInputStream fis = new FileInputStream(archivoExterno)) {
                props.load(fis);
                System.out.println("Configuracion cargada desde archivo externo: " + archivoExterno.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("Error al cargar archivo externo, usando configuracion interna");
                cargarDesdeRecursos(props);
            }
        } else {
            // Si no existe archivo externo, carga desde recursos internos
            cargarDesdeRecursos(props);
        }

        URL = props.getProperty("db.url");
        USER = props.getProperty("db.user");
        PASS = props.getProperty("db.password");
    }

    private static void cargarDesdeRecursos(Properties props) {
        try (InputStream is = ConexionBBDD.class.getResourceAsStream("/ip.properties")) {
            if (is != null) {
                props.load(is);
                System.out.println("Configuracion cargada desde recursos internos");
            } else {
                System.out.println("No se encontro el archivo ip.properties");
            }
        } catch (IOException e) {
            System.out.println("Error al cargar configuracion: " + e.getMessage());
        }
    }

    public static Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Conexion a BBDD realizada correctamente");
        }
        return conexion;
    }

    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("Conexión cerrada");
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
}
