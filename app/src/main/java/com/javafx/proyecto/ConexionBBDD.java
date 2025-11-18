package com.javafx.ejercicio4;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBBDD {
    private static final String URL = "jdbc:mysql://localhost:3306/Veterinario?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "user";
    private static final String PASS = "test";

    private static Connection conexion;

    public static Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {

            conexion = DriverManager.getConnection(URL, USER, PASS);
            System.out.println(" Conexión a BBDD realizada correctamente");
        }
        return conexion;
    }

    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println(" Conexión cerrada");
            } catch (SQLException e) {
                System.out.println(" Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
}
