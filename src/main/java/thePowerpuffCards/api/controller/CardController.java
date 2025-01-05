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
        // routes the request to the correct method based on HTTP method and path
        if ("GET".equalsIgnoreCase(method) && "/cards".equals(path)) {
            showAllCards(headers, out);
        } else if ("GET".equalsIgnoreCase(method) && path.startsWith("/deck")) {
            String query = null;
            if (path.contains("?")) {
                query = path.substring(path.indexOf("?") + 1);
            }
            showDeck(headers, out, query);
        } else if ("POST".equalsIgnoreCase(method) && "/deck".equals(path)) {
            defineDeck(headers, body, out);
        } else if ("PUT".equalsIgnoreCase(method) && "/deck".equals(path)) {
            configureDeck(headers, body, out);
        } else {
            sendBadRequest(out, "Bad request");
        }
    }

    public void configureDeck(Map<String, String> headers, String body, BufferedWriter out) throws IOException {
        String username = getUsernameFromHeaders(headers);
        if (username == null) {
            sendUnauthorized(out, "Invalid token.");
            return;
        }

        try {
            // parse JSON array containing card IDs
            List<String> cardIds = objectMapper.readValue(body, new TypeReference<>() {});

            if (cardIds.size() != 4) {
                throw new IllegalArgumentException("A deck must consist of exactly 4 cards.");
            }

            // get user from database
            User user = usersDao.getText(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

            // retrieve cards from the user's stack
            List<Card> stack = usersDao.getCardsFromStack(username);
            List<Card> selectedCards = new ArrayList<>();

            for (String cardId : cardIds) {
                // find card in the stack and add it to selected cards or throw an error if not found
                stack.stream()
                        .filter(card -> card.getId().equals(cardId))
                        .findFirst()
                        .ifPresentOrElse(selectedCards::add, () -> {
                            throw new IllegalArgumentException("Card " + cardId + " not found in stack.");
                        });
            }

            // ensure deck is not empty before saving
            if (selectedCards.isEmpty()) {
                sendBadRequest(out, "Cannot configure an empty deck.");
                return;
            }

            // clear and save new deck in database
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
            // parse JSON into a list of cards
            List<Card> selectedCards = objectMapper.readValue(body, new TypeReference<List<Card>>() {});

            // get user from database
            User user = usersDao.getText(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

            // define and save deck
            user.getDeck().defineDeck(selectedCards, username, usersDao);
            sendOk(out, "Deck defined successfully.");
        } catch (Exception e) {
            sendBadRequest(out, "Error defining deck: " + e.getMessage());
        }
    }

    private String getUsernameFromHeaders(Map<String, String> headers) {
        // extract username from the Authorization header (Bearer token format)
        String authorization = headers.get("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring("Bearer ".length()).split("-")[0];
    }

    private void showDeck(Map<String, String> headers, BufferedWriter out, String query) throws IOException {
        String username = getUsernameFromHeaders(headers);
        if (username == null) {
            sendUnauthorized(out, "Invalid token.");
            return;
        }

        List<Card> deck = usersDao.getDeck(username); // retrieve deck from database
        if (deck.isEmpty()) {
            sendOk(out, "[]");
            return;
        }

        // check if query param requests plain format
        if (query != null && query.contains("format=plain")) {
            StringBuilder plainDeck = new StringBuilder("Deck:\n");
            for (Card card : deck) {
                plainDeck.append("Name: ").append(card.getName())
                        .append(", Damage: ").append(card.getDamage())
                        .append(", Element: ").append(card.getElementType())
                        .append("\n");
            }
            sendOk(out, plainDeck.toString());
        } else {
            // default: return JSON format
            String jsonResponse = objectMapper.writeValueAsString(deck);
            sendOk(out, jsonResponse);
        }
    }

    private void showAllCards(Map<String, String> headers, BufferedWriter out) throws IOException {
        // check Authorization header
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

        // retrieve cards from the stack table
        List<Card> cards = usersDao.getCardsFromStack(username);
        if (cards.isEmpty()) {
            sendOk(out, "No cards available for this user.");
            return;
        }

        // serialize cards as JSON
        String jsonResponse = objectMapper.writeValueAsString(cards);
        sendOk(out, jsonResponse);
    }
}
