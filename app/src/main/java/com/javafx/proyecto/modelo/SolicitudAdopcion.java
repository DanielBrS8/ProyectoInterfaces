package com.javafx.proyecto.modelo;

import java.time.LocalDate;

public class SolicitudAdopcion {

    private final Integer id;
    private final Integer idMascota;
    private final String nombreMascota;
    private final Integer idSolicitante;
    private final String nombreSolicitante;
    private final LocalDate fechaPeticion;
    private final String estado;

    public SolicitudAdopcion(Integer id, Integer idMascota, String nombreMascota,
            Integer idSolicitante, String nombreSolicitante,
            LocalDate fechaPeticion, String estado) {
        this.id = id;
        this.idMascota = idMascota;
        this.nombreMascota = nombreMascota;
        this.idSolicitante = idSolicitante;
        this.nombreSolicitante = nombreSolicitante;
        this.fechaPeticion = fechaPeticion;
        this.estado = estado;
    }

    public Integer getId() { return id; }
    public Integer getIdMascota() { return idMascota; }
    public String getNombreMascota() { return nombreMascota; }
    public Integer getIdSolicitante() { return idSolicitante; }
    public String getNombreSolicitante() { return nombreSolicitante; }
    public LocalDate getFechaPeticion() { return fechaPeticion; }
    public String getEstado() { return estado; }
}
