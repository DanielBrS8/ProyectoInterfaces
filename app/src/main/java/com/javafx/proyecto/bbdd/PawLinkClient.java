package com.javafx.proyecto.bbdd;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class PawLinkClient {

    private static final String BASE_URL = "http://localhost:8080";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    // -------------------------------------------------------------------------
    // Métodos HTTP privados
    // -------------------------------------------------------------------------

    private static HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new RuntimeException("Error HTTP " + response.statusCode() + ": " + response.body());
        }
        return response;
    }

    private static HttpResponse<String> post(String path, Object body) throws Exception {
        String json = mapper.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new RuntimeException("Error HTTP " + response.statusCode() + ": " + response.body());
        }
        return response;
    }

    private static HttpResponse<String> put(String path, Object body) throws Exception {
        String json = mapper.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new RuntimeException("Error HTTP " + response.statusCode() + ": " + response.body());
        }
        return response;
    }

    private static HttpResponse<String> delete(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new RuntimeException("Error HTTP " + response.statusCode() + ": " + response.body());
        }
        return response;
    }

    // -------------------------------------------------------------------------
    // Mascotas
    // -------------------------------------------------------------------------

    public static List<Map<String, Object>> getMascotas() throws Exception {
        HttpResponse<String> response = get("/api/mascotas");
        return mapper.readValue(response.body(), new TypeReference<List<Map<String, Object>>>() {});
    }

    public static void crearMascota(Map<String, Object> body) throws Exception {
        post("/api/mascotas", body);
    }

    public static void actualizarMascota(int id, Map<String, Object> body) throws Exception {
        put("/api/mascotas/" + id, body);
    }

    public static void eliminarMascota(int id) throws Exception {
        delete("/api/mascotas/" + id);
    }

    // -------------------------------------------------------------------------
    // Alquileres
    // -------------------------------------------------------------------------

    public static List<Map<String, Object>> getAlquileres() throws Exception {
        HttpResponse<String> response = get("/api/alquileres");
        return mapper.readValue(response.body(), new TypeReference<List<Map<String, Object>>>() {});
    }

    public static void crearAlquiler(Map<String, Object> body) throws Exception {
        post("/api/alquileres", body);
    }

    public static void actualizarAlquiler(int id, Map<String, Object> body) throws Exception {
        put("/api/alquileres/" + id, body);
    }

    public static void eliminarAlquiler(int id) throws Exception {
        delete("/api/alquileres/" + id);
    }
}
