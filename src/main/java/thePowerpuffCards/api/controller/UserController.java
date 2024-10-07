package thePowerpuffCards.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import thePowerpuffCards.database.Database;
import thePowerpuffCards.services.models.User;

import java.io.BufferedWriter;
import java.io.IOException;

public class UserController {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static Database database;

    public UserController(Database db) {
        database = db;
    }

    public static void handleRequest(String method, String path, String body, BufferedWriter out) throws IOException {
        switch (method) {
            case "POST":
                if (path.equals("/users")) {
                    registerUser(body, out);  // Registrierung (POST /users)
                } else {
                    sendNotFound(out);
                }
                break;
            default:
                sendMethodNotAllowed(out);
                break;
        }
    }

    private static void registerUser(String body, BufferedWriter out) throws IOException {
        User newUser = objectMapper.readValue(body, User.class);

        if (database.isUserExisting(newUser.getUsername())) {
            out.write("HTTP/1.1 409 Conflict\r\n");
            out.write("Content-Type: text/plain\r\n");
            out.write("\r\n");
            out.write("User already exists");
        } else {
            database.addUser(newUser);
            out.write("HTTP/1.1 201 Created\r\n");
            out.write("\r\n");
        }
        out.flush();
    }

    private static void sendNotFound(BufferedWriter out) throws IOException {
        out.write("HTTP/1.1 404 Not Found\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write("404 - Not Found");
        out.flush();
    }

    private static void sendMethodNotAllowed(BufferedWriter out) throws IOException {
        out.write("HTTP/1.1 405 Method Not Allowed\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write("405 - Method Not Allowed");
        out.flush();
    }
}
