package org.mailosz.githubviewer;

public class ClientException extends RuntimeException {
    private final int statusCode;
    private final String message;
    public ClientException(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
