package com.javafx.proyecto.modelo;

public class CentroVeterinario {

    private Integer idCentro;
    private String nombre;
    private String ciudad;
    private String direccion;
    private String telefono;
    private String especialidad;
    private String foto;
    private String horario;
    private Double latitud;
    private Double longitud;

    public CentroVeterinario(Integer idCentro, String nombre, String ciudad,
            String direccion, String telefono, String especialidad,
            String foto, String horario, Double latitud, Double longitud) {
        this.idCentro = idCentro;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.telefono = telefono;
        this.especialidad = especialidad;
        this.foto = foto;
        this.horario = horario;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Integer getIdCentro() { return idCentro; }
    public String getNombre() { return nombre; }
    public String getCiudad() { return ciudad; }
    public String getDireccion() { return direccion; }
    public String getTelefono() { return telefono; }
    public String getEspecialidad() { return especialidad; }
    public String getFoto() { return foto; }
    public String getHorario() { return horario; }
    public Double getLatitud() { return latitud; }
    public Double getLongitud() { return longitud; }

    @Override
    public String toString() {
        return nombre + " - " + ciudad;
    }
}
