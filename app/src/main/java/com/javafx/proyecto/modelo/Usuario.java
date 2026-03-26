package com.javafx.proyecto.modelo;

public class Usuario {

    private Integer id;
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
    private Boolean activo;
    private String rol;
    private String nombreCentro;

    public Usuario(Integer id, String nombre, String email,
            String telefono, String direccion, Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.activo = activo;
    }

    public Usuario(Integer id, String nombre, String email,
            String telefono, String direccion, Boolean activo, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.activo = activo;
        this.rol = rol;
    }

    public Usuario(Integer id, String nombre, String email,
            Boolean activo, String rol, String nombreCentro) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.activo = activo;
        this.rol = rol;
        this.nombreCentro = nombreCentro;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public String getRol() {
        return rol;
    }

    public String getNombreCentro() {
        return nombreCentro;
    }

    @Override
    public String toString() {
        return nombre + " - " + email;
    }
}
