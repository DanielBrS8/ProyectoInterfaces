package com.javafx.ejercicio4;
public class UltimoRegistro {
    private final String descripcion;
    private final String fecha;

    public UltimoRegistro(String descripcion, String fecha) {
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFecha() {
        return fecha;
    }
}
