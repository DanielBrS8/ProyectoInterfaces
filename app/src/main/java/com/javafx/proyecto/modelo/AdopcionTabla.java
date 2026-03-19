package com.javafx.proyecto.modelo;

import java.time.LocalDate;

public class AdopcionTabla {

    private Integer id;
    private String mascota;
    private String voluntario;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private Integer calificacion;
    private Integer idMascota;
    private Integer idVoluntario;

    public AdopcionTabla(Integer id, String mascota, String voluntario,
            LocalDate fechaInicio, LocalDate fechaFin,
            String estado, Integer calificacion,
            Integer idMascota, Integer idVoluntario) {
        this.id = id;
        this.mascota = mascota;
        this.voluntario = voluntario;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.calificacion = calificacion;
        this.idMascota = idMascota;
        this.idVoluntario = idVoluntario;
    }

    public Integer getId() {
        return id;
    }

    public String getMascota() {
        return mascota;
    }

    public String getVoluntario() {
        return voluntario;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public Integer getIdMascota() {
        return idMascota;
    }

    public Integer getIdVoluntario() {
        return idVoluntario;
    }
}
