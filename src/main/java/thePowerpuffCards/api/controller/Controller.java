package thePowerpuffCards.api.controller;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public abstract class Controller {
    public abstract void handleRequest(String method, String path, Map<String, String> header, String body, BufferedWriter out) throws IOException;

    protected void sendBadRequest(BufferedWriter out, String message) throws IOException {
        out.write("HTTP/1.1 400 Bad Request\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write(message);
        out.flush();
    }

    protected void sendInternalError(BufferedWriter out, String message) throws IOException {
        out.write("HTTP/1.1 500 Internal Server Error\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write(message);
        out.flush();
    }

    protected void sendNotFound(BufferedWriter out, String message) throws IOException {
        out.write("HTTP/1.1 404 Not Found\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write(message);
        out.flush();
    }

    protected void sendMethodNotAllowed(BufferedWriter out) throws IOException {
        out.write("HTTP/1.1 405 Method Not Allowed\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write("405 - Method Not Allowed");
        out.flush();
    }

    protected void sendUnauthorized(BufferedWriter out, String message) throws IOException {
        out.write("HTTP/1.1 401 Unauthorized\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write(message);
        out.flush();
    }

    protected void sendOk(BufferedWriter out, String message) throws IOException {
        out.write("HTTP/1.1 200 OK\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write(message);
        out.flush();
    }
    protected void sendNotFound(BufferedWriter out) throws IOException {
        out.write("HTTP/1.1 404 Not Found\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write("404 - Not Found");
        out.flush();
    }
   protected void sendConflict(BufferedWriter out, String message) throws IOException {
        out.write("HTTP/1.1 409 Conflict\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write("Error: " + message + "\r\n");
        out.flush();
    }
    protected void sendForbidden(BufferedWriter out, String message) throws IOException {
        out.write("HTTP/1.1 403 Forbidden\r\n");
        out.write("Content-Type: application/json\r\n");
        out.write("\r\n");
        out.write("{\"error\": \"" + message + "\"}");
        out.flush();
    }


}
