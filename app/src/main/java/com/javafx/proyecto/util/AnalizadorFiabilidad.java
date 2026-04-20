package com.javafx.proyecto.util;

import com.javafx.proyecto.bbdd.PawLinkClient;
import com.javafx.proyecto.modelo.FiabilidadAdoptante;
import com.javafx.proyecto.modelo.FiabilidadAdoptante.Nivel;

import javafx.concurrent.Task;

import java.time.LocalDate;
import java.util.Map;

public final class AnalizadorFiabilidad {

    private AnalizadorFiabilidad() {}

    public static Task<FiabilidadAdoptante> crearTarea(int idSolicitante, String token) {
        return new Task<>() {
            @Override
            protected FiabilidadAdoptante call() throws Exception {
                Map<String, Object> historial = PawLinkClient.getHistorialAdopciones(idSolicitante, token);
                return desdeRespuestaBackend(historial);
            }
        };
    }

    private static FiabilidadAdoptante desdeRespuestaBackend(Map<String, Object> historial) {
        int total = entero(historial.get("totalMascotas"));
        double mediaDias = decimal(historial.get("duracionMediaDias"));
        LocalDate ultima = fecha(historial.get("ultimaAdopcion"));

        Nivel nivel = calcularNivel(total, mediaDias);
        String recomendacion = construirRecomendacion(nivel, total, mediaDias);
        return new FiabilidadAdoptante(total, mediaDias, ultima, nivel, recomendacion);
    }

    private static Nivel calcularNivel(int total, double mediaDias) {
        if (total == 0) return Nivel.SIN_HISTORIAL;
        if (total >= 3 && mediaDias >= 60) return Nivel.ALTA;
        if (total >= 1 && mediaDias >= 30) return Nivel.MEDIA;
        return Nivel.BAJA;
    }

    private static String construirRecomendacion(Nivel nivel, int total, double mediaDias) {
        return switch (nivel) {
            case SIN_HISTORIAL -> "Primer solicitante. Requiere entrevista previa y verificación de vivienda.";
            case BAJA          -> "Historial breve (media " + String.format("%.0f", mediaDias)
                                  + " días). Se recomienda seguimiento durante los primeros 15 días.";
            case MEDIA         -> "Historial aceptable (" + total + " adopciones, media "
                                  + String.format("%.0f", mediaDias) + " días). Aprobación estándar.";
            case ALTA          -> "Adoptante recurrente y consolidado (" + total
                                  + " adopciones, media " + String.format("%.0f", mediaDias)
                                  + " días). Aprobación directa sugerida.";
        };
    }

    private static int entero(Object o) {
        return o instanceof Number n ? n.intValue() : 0;
    }

    private static double decimal(Object o) {
        return o instanceof Number n ? n.doubleValue() : 0.0;
    }

    private static LocalDate fecha(Object o) {
        if (o == null) return null;
        try {
            return LocalDate.parse(o.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
