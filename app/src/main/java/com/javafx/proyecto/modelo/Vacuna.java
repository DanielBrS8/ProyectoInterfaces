package com.javafx.proyecto.modelo;

import java.time.LocalDate;

public class Vacuna {

    private Integer id;
    private String nombre;
    private LocalDate fechaAdministracion;
    private LocalDate fechaProximaDosis;
    private String veterinario;
    private String notas;
    private Integer idMascota;

    public Vacuna(Integer id, String nombre, LocalDate fechaAdministracion,
                  LocalDate fechaProximaDosis, String veterinario, String notas, Integer idMascota) {
        this.id = id;
        this.nombre = nombre;
        this.fechaAdministracion = fechaAdministracion;
        this.fechaProximaDosis = fechaProximaDosis;
        this.veterinario = veterinario;
        this.notas = notas;
        this.idMascota = idMascota;
    }

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public LocalDate getFechaAdministracion() { return fechaAdministracion; }
    public LocalDate getFechaProximaDosis() { return fechaProximaDosis; }
    public String getVeterinario() { return veterinario; }
    public String getNotas() { return notas; }
    public Integer getIdMascota() { return idMascota; }
}
