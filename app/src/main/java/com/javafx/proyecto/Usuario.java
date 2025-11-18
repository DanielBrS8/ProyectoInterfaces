package com.javafx.proyecto;

public class Usuario {

    private Integer id;
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
    private Boolean activo;

    public Usuario(Integer id, String nombre, String email,
            String telefono, String direccion, Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.activo = activo;
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
}
