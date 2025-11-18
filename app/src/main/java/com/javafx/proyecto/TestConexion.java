package com.javafx.ejercicio4;

public class TestConexion {
    public static void main(String[] args) {
        try {
            java.sql.Connection c = ConexionBBDD.getConexion();
            System.out.println("OK: " + c.getCatalog());
            ConexionBBDD.cerrarConexion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
