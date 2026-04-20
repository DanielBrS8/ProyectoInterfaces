package com.javafx.proyecto.bbdd;

public class PawLinkHttpException extends RuntimeException {

    private final int statusCode;
    private final String body;

    public PawLinkHttpException(int statusCode, String body) {
        super("HTTP " + statusCode + ": " + body);
        this.statusCode = statusCode;
        this.body = body;
    }

    public int getStatusCode() { return statusCode; }

    public String getBody() { return body; }

    public boolean esBadRequest() { return statusCode == 400; }

    public boolean esConflicto() { return statusCode == 409; }
}
