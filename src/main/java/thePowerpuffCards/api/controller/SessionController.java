package thePowerpuffCards.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import thePowerpuffCards.persistence.dao.UsersDaoDb;
import thePowerpuffCards.core.models.User;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public class SessionController extends Controller {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final UsersDaoDb usersDao;

    public SessionController(UsersDaoDb usersDao) {

        this.usersDao = usersDao;
    }

    public void handleRequest(String method, String path, Map<String, String> header, String body, BufferedWriter out) throws IOException {
        switch (method) {
            case "POST":
                if (path.equals("/sessions")) {
                    createSession(body, out);
                } else {
                    sendNotFound(out);
                }
                break;
            default:
                sendMethodNotAllowed(out);
                break;
        }
    }

    private void createSession(String body, BufferedWriter out) throws IOException {
        User user = objectMapper.readValue(body, User.class);
        User foundUser = usersDao.findUserByUsernameAndPassword(user.getUsername(), user.getPassword());

        if (foundUser != null) {
            foundUser.setToken(foundUser.getUsername() + "-mtcgToken");
            usersDao.addSession(foundUser);
            out.write("HTTP/1.1 200 OK\r\n");
            out.write("Content-Type: text/plain\r\n");
            out.write("\r\n");
            out.write(foundUser.getToken());
        } else {
            out.write("HTTP/1.1 401 Unauthorized\r\n");
            out.write("Content-Type: text/plain\r\n");
            out.write("\r\n");
            out.write("Invalid credentials");
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
