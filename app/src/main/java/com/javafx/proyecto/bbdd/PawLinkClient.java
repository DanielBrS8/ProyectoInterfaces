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
    private static final String PATH_ALQUILERES = "/api/alquileres/";
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
            throw new PawLinkHttpException(response.statusCode(), response.body());
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
            throw new PawLinkHttpException(response.statusCode(), response.body());
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
            throw new PawLinkHttpException(response.statusCode(), response.body());
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
            throw new PawLinkHttpException(response.statusCode(), response.body());
        }
        return response;
    }

    // -------------------------------------------------------------------------
    // Métodos HTTP autenticados (con token JWT)
    // -------------------------------------------------------------------------

    private static HttpResponse<String> getAuth(String path, String token) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new PawLinkHttpException(response.statusCode(), response.body());
        }
        return response;
    }

    private static HttpResponse<String> postAuth(String path, Object body, String token) throws Exception {
        String json = mapper.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new PawLinkHttpException(response.statusCode(), response.body());
        }
        return response;
    }

    private static HttpResponse<String> putAuth(String path, Object body, String token) throws Exception {
        String json = mapper.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new PawLinkHttpException(response.statusCode(), response.body());
        }
        return response;
    }

    private static HttpResponse<String> deleteAuth(String path, String token) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new PawLinkHttpException(response.statusCode(), response.body());
        }
        return response;
    }

    // -------------------------------------------------------------------------
    // Autenticación
    // -------------------------------------------------------------------------

    public static Map<String, Object> login(String email, String password) throws Exception {
        Map<String, String> body = Map.of("email", email, "password", password);
        HttpResponse<String> response = post("/api/auth/login", body);
        return mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
    }

    public static Map<String, Object> loginGoogle(String code) throws Exception {
        Map<String, String> body = Map.of("code", code);
        HttpResponse<String> response = post("/api/auth/google", body);
        return mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
    }

    // -------------------------------------------------------------------------
    // Mascotas
    // -------------------------------------------------------------------------

    public static List<Map<String, Object>> getMascotas(String token) throws Exception {
        HttpResponse<String> response = getAuth("/api/mascotas", token);
        return mapper.readValue(response.body(), new TypeReference<List<Map<String, Object>>>() {});
    }

    public static void crearMascota(Map<String, Object> body, String token) throws Exception {
        postAuth("/api/mascotas", body, token);
    }

    public static void actualizarMascota(int id, Map<String, Object> body, String token) throws Exception {
        putAuth("/api/mascotas/" + id, body, token);
    }

    public static void eliminarMascota(int id, String token) throws Exception {
        deleteAuth("/api/mascotas/" + id, token);
    }

    // -------------------------------------------------------------------------
    // Alquileres (Adopciones)
    // -------------------------------------------------------------------------

    public static List<Map<String, Object>> getAlquileres(String token) throws Exception {
        HttpResponse<String> response = getAuth("/api/alquileres", token);
        return mapper.readValue(response.body(), new TypeReference<List<Map<String, Object>>>() {});
    }

    public static void crearAlquiler(Map<String, Object> body, String token) throws Exception {
        postAuth("/api/alquileres", body, token);
    }

    public static void actualizarAlquiler(int id, Map<String, Object> body, String token) throws Exception {
        putAuth(PATH_ALQUILERES + id, body, token);
    }

    public static void eliminarAlquiler(int id, String token) throws Exception {
        deleteAuth(PATH_ALQUILERES + id, token);
    }

    public static void actualizarEstadoAlquiler(int id, String estado, String token) throws Exception {
        putAuth(PATH_ALQUILERES + id + "/estado", Map.of("estado", estado), token);
    }

    // -------------------------------------------------------------------------
    // Historial / Fiabilidad del adoptante
    // -------------------------------------------------------------------------

    public static Map<String, Object> getHistorialAdopciones(int idUsuario, String token) throws Exception {
        HttpResponse<String> response = getAuth("/api/usuarios/" + idUsuario + "/historial-adopciones", token);
        return mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
    }

    // -------------------------------------------------------------------------
    // Admin — Gestión de usuarios
    // -------------------------------------------------------------------------

    public static List<Map<String, Object>> getUsuarios(String token) throws Exception {
        HttpResponse<String> response = getAuth("/api/admin/usuarios", token);
        return mapper.readValue(response.body(), new TypeReference<List<Map<String, Object>>>() {});
    }

    public static Map<String, Object> crearUsuario(Map<String, Object> body, String token) throws Exception {
        HttpResponse<String> response = postAuth("/api/admin/usuarios", body, token);
        return mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
    }

    public static void actualizarUsuario(int id, Map<String, Object> body, String token) throws Exception {
        putAuth("/api/admin/usuarios/" + id, body, token);
    }

    public static void eliminarUsuario(int id, String token) throws Exception {
        deleteAuth("/api/admin/usuarios/" + id, token);
    }

    // -------------------------------------------------------------------------
    // Centros Veterinarios
    // -------------------------------------------------------------------------

    public static List<Map<String, Object>> getCentros(String token) throws Exception {
        HttpResponse<String> response = getAuth("/api/centros", token);
        return mapper.readValue(response.body(), new TypeReference<List<Map<String, Object>>>() {});
    }

    public static void crearCentro(Map<String, Object> body, String token) throws Exception {
        postAuth("/api/centros", body, token);
    }

    public static void actualizarCentro(int id, Map<String, Object> body, String token) throws Exception {
        putAuth("/api/centros/" + id, body, token);
    }

    public static void eliminarCentro(int id, String token) throws Exception {
        deleteAuth("/api/centros/" + id, token);
    }
}
