package thePowerpuffCards.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import thePowerpuffCards.persistence.dao.UsersDaoDb;
import thePowerpuffCards.core.models.User;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class UserController extends Controller {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final UsersDaoDb usersDao;

    public UserController(UsersDaoDb usersDao) {
        this.usersDao = usersDao;
    }

    public void handleRequest(String method, String path, Map<String, String> headers, String body, BufferedWriter out) throws IOException {
        System.out.println("Handling request: " + method + " " + path); // Debug-Ausgabe
        String[] pathParts = path.split("/");
        if (pathParts.length == 2 && "users".equals(pathParts[1])) {
            if ("POST".equalsIgnoreCase(method)) {
                registerUser(body, out);
            } else {
                sendNotFound(out);
            }
        } else if (pathParts.length == 3 && "users".equals(pathParts[1])) {
            String targetUsername = pathParts[2];
            System.out.println("Target username: " + targetUsername); // Debug-Ausgabe
            if ("GET".equalsIgnoreCase(method)) {
                getUserProfile(targetUsername, headers, out);
            } else if ("PUT".equalsIgnoreCase(method)) {
                updateUserProfile(targetUsername, headers, body, out);
            } else {
                sendMethodNotAllowed(out);
            }
        } else {
            sendNotFound(out);
        }
    }


    private void registerUser(String body, BufferedWriter out) throws IOException {
        User newUser = objectMapper.readValue(body, User.class);

        if (usersDao.userExists(newUser.getUsername())) {
            out.write("HTTP/1.1 409 Conflict\r\n");
            out.write("Content-Type: text/plain\r\n");
            out.write("\r\n");
            out.write("User already exists");
        } else {
            usersDao.save(newUser);
            out.write("HTTP/1.1 201 Created\r\n");
            out.write("\r\n");
        }
        out.flush();
    }

    private void getUserProfile(String targetUsername, Map<String, String> headers, BufferedWriter out) throws IOException {
        String authUsername = getUsernameFromHeaders(headers);
        if (authUsername == null || !authUsername.equals(targetUsername)) {
            sendUnauthorized(out, "You can only view your own profile.");
            return;
        }

        Optional<Map<String, String>> profile = usersDao.getUserProfile(targetUsername);
        if (profile.isPresent()) {
            String jsonResponse = objectMapper.writeValueAsString(profile.get());
            out.write("HTTP/1.1 200 OK\r\n");
            out.write("Content-Type: application/json\r\n");
            out.write("\r\n");
            out.write(jsonResponse);
        } else {
            // Fallback: leeres Profil zurückgeben
            out.write("HTTP/1.1 200 OK\r\n");
            out.write("Content-Type: application/json\r\n");
            out.write("\r\n");
            out.write("{\"Name\":\"\",\"Bio\":\"\",\"Image\":\"\"}");
        }
        out.flush();
    }


    private void updateUserProfile(String targetUsername, Map<String, String> headers, String body, BufferedWriter out) throws IOException {
        String authUsername = getUsernameFromHeaders(headers);
        if (authUsername == null || !authUsername.equals(targetUsername)) {
            sendUnauthorized(out, "You can only edit your own profile.");
            return;
        }

        try {
            Map<String, String> updatedProfile = objectMapper.readValue(body, new TypeReference<>() {});
            usersDao.updateUserProfile(
                    targetUsername,
                    updatedProfile.get("Name"),
                    updatedProfile.get("Bio"),
                    updatedProfile.get("Image")
            );
            out.write("HTTP/1.1 200 OK\r\n");
            out.write("\r\n");
            out.write("User profile updated successfully.");
        } catch (Exception e) {
            sendBadRequest(out, "Error updating user profile: " + e.getMessage());
        }
        out.flush();
    }

    private String getUsernameFromHeaders(Map<String, String> headers) {
        String authorization = headers.get("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring("Bearer ".length()).split("-")[0];
    }

    private void sendUnauthorized(BufferedWriter out, String message) throws IOException {
        out.write("HTTP/1.1 401 Unauthorized\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write(message);
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

    private void sendBadRequest(BufferedWriter out, String message) throws IOException {
        out.write("HTTP/1.1 400 Bad Request\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write(message);
        out.flush();
    }
}
