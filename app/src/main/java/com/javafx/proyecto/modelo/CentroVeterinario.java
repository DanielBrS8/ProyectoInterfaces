package com.javafx.proyecto.modelo;

public class CentroVeterinario {

    private Integer idCentro;
    private String nombre;
    private String ciudad;
    private String direccion;
    private String telefono;
    private String especialidad;

    public CentroVeterinario(Integer idCentro, String nombre, String ciudad,
            String direccion, String telefono, String especialidad) {
        this.idCentro = idCentro;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.telefono = telefono;
        this.especialidad = especialidad;
    }

    public Integer getIdCentro() {
        return idCentro;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    @Override
    public String toString() {
        return nombre + " - " + ciudad;
    }
}
