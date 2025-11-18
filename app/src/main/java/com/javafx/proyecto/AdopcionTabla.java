package com.javafx.proyecto;

import java.time.LocalDate;

public class AdopcionTabla {

    private Integer id;
    private String mascota;
    private String voluntario;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private Integer calificacion;

    public AdopcionTabla(Integer id, String mascota, String voluntario,
            LocalDate fechaInicio, LocalDate fechaFin,
            String estado, Integer calificacion) {
        this.id = id;
        this.mascota = mascota;
        this.voluntario = voluntario;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.calificacion = calificacion;
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
}
