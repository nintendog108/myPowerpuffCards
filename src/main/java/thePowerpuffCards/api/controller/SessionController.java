package thePowerpuffCards.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import thePowerpuffCards.database.Database;
import thePowerpuffCards.services.models.Session;
import thePowerpuffCards.services.models.User;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.UUID;

public class SessionController {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static Database database;


    public SessionController(Database db) {
        database = db;
    }

    public static void handleRequest(String method, String path, String body, BufferedWriter out) throws IOException {
        switch (method) {
            case "POST":
                if (path.equals("/sessions")) {
                    createSession(body, out);
                } else {
                    sendNotFound(out);
                }
                break;
            case "GET":
                if (path.equals("/sessions")) {
                    listSessions(out);
                } else {
                    sendNotFound(out);
                }
                break;
            case "DELETE":
                if (path.matches("/sessions/\\w+")) {
                    String sessionId = path.split("/")[2];
                    deleteSession(out, sessionId);
                } else {
                    sendNotFound(out);
                }
                break;
            default:
                sendMethodNotAllowed(out);
                break;
        }
    }

    private static void createSession(String body, BufferedWriter out) throws IOException {
        User user = objectMapper.readValue(body, User.class);
        User foundUser = database.findUserByUsernameAndPassword(user.getUsername(), user.getPassword());

        if (foundUser != null) {

            String sessionId = UUID.randomUUID().toString();
            Session session = new Session(sessionId, foundUser.getUsername());

            database.addSession(session);

            //token als plain text ( content-type
            out.write("HTTP/1.1 200 OK\r\n");
            out.write("Content-Type: text/plain\r\n");
            out.write("\r\n");
            out.write(foundUser.getToken());  // token as plain text
        } else {
            out.write("HTTP/1.1 401 Unauthorized\r\n");
            out.write("Content-Type: text/plain\r\n");
            out.write("\r\n");
            out.write("Invalid credentials");
        }
        out.flush();
    }

    // get all sessions ausgeben, important
    private static void listSessions(BufferedWriter out) throws IOException {
        String sessionsJson = objectMapper.writeValueAsString(database.getAllSessions());

        // Rückgabe als JSON
        out.write("HTTP/1.1 200 OK\r\n");
        out.write("Content-Type: application/json\r\n");
        out.write("\r\n");
        out.write(sessionsJson);
        out.flush();
    }

    private static void deleteSession(BufferedWriter out, String sessionId) throws IOException {
        boolean result = database.removeSessionById(sessionId);

        if (result) {
            out.write("HTTP/1.1 200 OK\r\n");
            out.write("Content-Type: text/plain\r\n");
            out.write("\r\n");
            out.write("Session deleted successfully");
        } else {
            out.write("HTTP/1.1 404 Not Found\r\n");
            out.write("Content-Type: text/plain\r\n");
            out.write("\r\n");
            out.write("Session not found");
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
