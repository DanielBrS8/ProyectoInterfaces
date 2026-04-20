package com.javafx.proyecto.modelo;

import java.time.LocalDate;

public class FiabilidadAdoptante {

    public enum Nivel {
        SIN_HISTORIAL("Sin historial previo", "#9e9e9e"),
        BAJA("Fiabilidad baja",               "#e53935"),
        MEDIA("Fiabilidad media",             "#fb8c00"),
        ALTA("Fiabilidad alta",               "#43a047");

        private final String etiqueta;
        private final String colorHex;

        Nivel(String etiqueta, String colorHex) {
            this.etiqueta = etiqueta;
            this.colorHex = colorHex;
        }

        public String getEtiqueta() { return etiqueta; }
        public String getColorHex() { return colorHex; }
    }

    private final int totalAdopciones;
    private final double duracionMediaDias;
    private final LocalDate ultimaAdopcion;
    private final Nivel nivel;
    private final String recomendacion;

    public FiabilidadAdoptante(int totalAdopciones, double duracionMediaDias,
            LocalDate ultimaAdopcion, Nivel nivel, String recomendacion) {
        this.totalAdopciones = totalAdopciones;
        this.duracionMediaDias = duracionMediaDias;
        this.ultimaAdopcion = ultimaAdopcion;
        this.nivel = nivel;
        this.recomendacion = recomendacion;
    }

    public int getTotalAdopciones() { return totalAdopciones; }
    public double getDuracionMediaDias() { return duracionMediaDias; }
    public LocalDate getUltimaAdopcion() { return ultimaAdopcion; }
    public Nivel getNivel() { return nivel; }
    public String getRecomendacion() { return recomendacion; }
}
