package com.javafx.proyecto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBBDD {
    // Supabase PostgreSQL connection (Session Pooler)
    private static final String URL =
            "jdbc:postgresql://aws-1-eu-west-1.pooler.supabase.com:5432/postgres" +
            "?sslmode=require";

    private static final String USER = "postgres.ppkejrnppfuatarenrls";
    private static final String PASS = "ContraseñaProyectoInterfaces";

    private static Connection conexion;

    public static Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Conexión a BBDD realizada correctamente");
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
