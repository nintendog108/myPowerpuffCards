package thePowerpuffCards.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import thePowerpuffCards.core.models.User;
import thePowerpuffCards.core.models.cards.Card;
import thePowerpuffCards.persistence.dao.UsersDaoDb;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class CardController extends Controller {
    private final UsersDaoDb usersDao;
    private static final ObjectMapper objectMapper = new ObjectMapper();


    public CardController(UsersDaoDb usersDao) {
        this.usersDao = usersDao;
    }

    @Override
    public void handleRequest(String method, String path, Map<String, String> headers, String body, BufferedWriter out) throws IOException {
        if ("GET".equalsIgnoreCase(method) && "/cards".equals(path)) {
            showAllCards(headers, out);
        } else {
            sendBadRequest(out, "Bad request");
        }
    }

    private void showAllCards(Map<String, String> headers, BufferedWriter out) throws IOException {
        String authorization = headers.get("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            sendUnauthorized(out, "Missing or invalid token.");
            return;
        }

        String username = authorization.substring("Bearer ".length()).split("-")[0];
        Optional<User> userOptional = usersDao.getText(username);

        if (userOptional.isEmpty()) {
            sendBadRequest(out, "User not found.");
            return;
        }

        // Karten aus der Tabelle `stack` abrufen
        List<Card> cards = usersDao.getCardsFromStack(username);
        if (cards.isEmpty()) {
            sendOk(out, "No cards available for this user.");
            return;
        }

        // Karten als JSON serialisieren
        String jsonResponse = objectMapper.writeValueAsString(cards);

        sendOk(out, jsonResponse);
    }

    private void sendOk(BufferedWriter out, String message) throws IOException {
        out.write("HTTP/1.1 200 OK\r\n");
        out.write("Content-Type: application/json\r\n");
        out.write("\r\n");
        out.write(message);
        out.flush();
    }

    private void sendUnauthorized(BufferedWriter out, String message) throws IOException {
        out.write("HTTP/1.1 401 Unauthorized\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write(message);
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
