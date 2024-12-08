package thePowerpuffCards.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import thePowerpuffCards.persistence.dao.UsersDaoDb;
import thePowerpuffCards.core.models.User;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public class UserController extends Controller {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final UsersDaoDb usersDao; // Direkt UsersDaoDb verwenden

    public UserController(UsersDaoDb usersDao) {
        this.usersDao = usersDao; // Konstruktor erhält UsersDaoDb
    }

    public void handleRequest(String method, String path, Map<String, String> header, String body, BufferedWriter out) throws IOException {
        switch (method) {
            case "POST":
                if (path.equals("/users")) {
                    registerUser(body, out);
                } else {
                    sendNotFound(out);
                }
                break;
            default:
                sendMethodNotAllowed(out);
                break;
        }
    }

    private void registerUser(String body, BufferedWriter out) throws IOException {
        User newUser = objectMapper.readValue(body, User.class); // JSON-Body parsen

        if (usersDao.userExists(newUser.getUsername())) { //
            // Benutzer existiert bereits
            out.write("HTTP/1.1 409 - User already exists! \r\n");
            out.write("Content-Type: text/plain\r\n");
            out.write("\r\n");
            out.write("User already exists");
        } else {
            // Benutzer speichern
            usersDao.save(newUser);
            out.write("HTTP/1.1 201 Created\r\n");
            out.write("\r\n");
        }
        out.flush();
    }

    private void sendNotFound(BufferedWriter out) throws IOException {
        out.write("HTTP/1.1 404 Not Found\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write("404 - Not Found");
        out.flush();
    }

    private void sendMethodNotAllowed(BufferedWriter out) throws IOException {
        out.write("HTTP/1.1 405 Method Not Allowed\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write("405 - Method Not Allowed");
        out.flush();
    }
}
