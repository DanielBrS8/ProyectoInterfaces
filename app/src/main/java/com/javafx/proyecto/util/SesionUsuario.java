package com.javafx.proyecto.util;

import java.util.Map;

public class SesionUsuario {

    private static SesionUsuario instancia;

    private String token;
    private Integer id;
    private String nombre;
    private String rol;
    private Integer idCentro;

    private SesionUsuario() {
    }

    public static SesionUsuario getInstancia() {
        if (instancia == null) {
            instancia = new SesionUsuario();
        }
        return instancia;
    }

    public void iniciarSesion(Map<String, Object> respuesta) {
        this.token = (String) respuesta.get("token");
        this.id = (Integer) respuesta.get("id");
        this.nombre = (String) respuesta.get("nombre");
        this.rol = (String) respuesta.get("rol");
        this.idCentro = (Integer) respuesta.get("idCentro");
    }

    public void cerrarSesion() {
        this.token = null;
        this.id = null;
        this.nombre = null;
        this.rol = null;
        this.idCentro = null;
    }

    public boolean isLogueado() {
        return token != null;
    }

    public String getToken() {
        return token;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRol() {
        return rol;
    }

    public Integer getIdCentro() {
        return idCentro;
    }

    public boolean isAdmin() {
        return "admin".equals(rol);
    }

    public boolean isVeterinario() {
        return "veterinario".equals(rol);
    }
}
