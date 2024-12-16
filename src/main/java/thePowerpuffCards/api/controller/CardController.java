package thePowerpuffCards.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import thePowerpuffCards.core.models.User;
import thePowerpuffCards.core.models.cards.Card;
import thePowerpuffCards.persistence.dao.UsersDaoDb;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class CardController extends Controller {
    private final UsersDaoDb usersDao;
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);


    public CardController(UsersDaoDb usersDao) {
        this.usersDao = usersDao;
    }

    @Override
    public void handleRequest(String method, String path, Map<String, String> headers, String body, BufferedWriter out) throws IOException {
        if ("GET".equalsIgnoreCase(method) && "/cards".equals(path)) {
            showAllCards(headers, out);
        } else if ("GET".equalsIgnoreCase(method) && "/deck".equals(path)) {
            showDeck(headers, out); // Neuer Endpunkt: Deck anzeigen
        } else if ("POST".equalsIgnoreCase(method) && "/deck".equals(path)) {
            defineDeck(headers, body, out); // Neuer Endpunkt: Deck definieren
        } else if ("PUT".equalsIgnoreCase(method) && "/deck".equals(path)) {
            configureDeck(headers, body, out);
        } else {
            sendBadRequest(out, "Bad request");
        }
    }
    private void configureDeck(Map<String, String> headers, String body, BufferedWriter out) throws IOException {
        String username = getUsernameFromHeaders(headers);
        if (username == null) {
            sendUnauthorized(out, "Invalid token.");
            return;
        }

        try {
            // JSON-Array der Karten-IDs parsen
            List<String> cardIds = objectMapper.readValue(body, new TypeReference<List<String>>() {});

            if (cardIds.size() != 4) {
                throw new IllegalArgumentException("A deck must consist of exactly 4 cards.");
            }

            // Benutzer aus der Datenbank abrufen
            User user = usersDao.getText(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Karten aus dem Stack abrufen
            List<Card> stack = usersDao.getCardsFromStack(username);
            List<Card> selectedCards = new ArrayList<>();
            for (String cardId : cardIds) {
                stack.stream()
                        .filter(card -> card.getId().equals(cardId))
                        .findFirst()
                        .ifPresentOrElse(selectedCards::add, () -> {
                            throw new IllegalArgumentException("Card " + cardId + " not found in stack.");
                        });
            }

            // Deck speichern
            user.getDeck().defineDeck(selectedCards, username, usersDao);
            usersDao.clearDeck(username);
            usersDao.saveDeck(username, selectedCards);

            sendOk(out, "Deck configured successfully.");
        } catch (Exception e) {
            sendBadRequest(out, "Error configuring deck: " + e.getMessage());
        }
    }

    private void defineDeck(Map<String, String> headers, String body, BufferedWriter out) throws IOException {
        String username = getUsernameFromHeaders(headers);
        if (username == null) {
            sendUnauthorized(out, "Invalid token.");
            return;
        }

        try {
            // JSON in Card-Liste konvertieren
            List<Card> selectedCards = objectMapper.readValue(body, new TypeReference<List<Card>>() {});

            // Benutzer aus der Datenbank abrufen
            User user = usersDao.getText(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Deck definieren und speichern
            user.getDeck().defineDeck(selectedCards, username, usersDao);
            sendOk(out, "Deck defined successfully.");
        } catch (Exception e) {
            sendBadRequest(out, "Error defining deck: " + e.getMessage());
        }
    }
    private String getUsernameFromHeaders(Map<String, String> headers) {
        String authorization = headers.get("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring("Bearer ".length()).split("-")[0];
    }


    private void showDeck(Map<String, String> headers, BufferedWriter out) throws IOException {
        String username = getUsernameFromHeaders(headers);
        if (username == null) {
            sendUnauthorized(out, "Invalid token.");
            return;
        }

        List<Card> deck = usersDao.getDeck(username); // Deck aus der Datenbank abrufen
        if (deck.isEmpty()) {
            sendOk(out, "[]"); // Leeres Deck zurückgeben
        } else {
            String jsonResponse = objectMapper.writeValueAsString(deck);
            sendOk(out, jsonResponse);
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
