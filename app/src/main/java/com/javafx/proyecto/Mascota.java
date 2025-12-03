package com.javafx.proyecto;

import java.time.LocalDate;

public class Mascota {

    private Integer id;
    private String nombre;
    private String especie;
    private String raza;
    private LocalDate fechaNacimiento;
    private Double peso;
    private String estadoSalud;
    private Boolean disponible;

    public Mascota(Integer id, String nombre, String especie, String raza,
            LocalDate fechaNacimiento, Double peso,
            String estadoSalud, Boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.peso = peso;
        this.estadoSalud = estadoSalud;
        this.disponible = disponible;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public String getRaza() {
        return raza;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public Double getPeso() {
        return peso;
    }

    public String getEstadoSalud() {
        return estadoSalud;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    @Override
    public String toString() {
        
        return nombre + " (" + especie + ")";
        
    }
}
