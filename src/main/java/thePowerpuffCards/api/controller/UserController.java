package thePowerpuffCards.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import thePowerpuffCards.persistence.dao.Dao;
import thePowerpuffCards.persistence.dao.UsersDaoDb;
import thePowerpuffCards.services.models.User;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Optional;

public class UserController {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Dao<User> userDao;

    public UserController(Dao<User> userDao) {
        this.userDao = userDao;
    }

    public void handleRequest(String method, String path, String body, BufferedWriter out) throws IOException {
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
        User newUser = objectMapper.readValue(body, User.class);
        Optional<User> dbUser = userDao.get(newUser.getUsername());
        if (dbUser.isPresent()) {
            out.write("HTTP/1.1 409 Conflict\r\n");
            out.write("Content-Type: text/plain\r\n");
            out.write("\r\n");
            out.write("User already exists");
        } else {
            userDao.save(newUser);
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
