package thePowerpuffCards.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import thePowerpuffCards.core.models.cards.Card;
import thePowerpuffCards.core.services.BattleService;
import thePowerpuffCards.persistence.dao.UsersDaoDb;
import thePowerpuffCards.core.models.User;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
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
        if ("POST".equalsIgnoreCase(method) && "/battles".equals(path)) {
            startBattle(headers, body, out);
        } else if (pathParts.length == 2 && "users".equals(pathParts[1])) {
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
        } else if(("GET".equalsIgnoreCase(method) && "/stats".equals(path))) {
            showStats(headers, out);
        } else  if ("GET".equalsIgnoreCase(method) && "/scoreboard".equals(path)) {
            showScoreboard(headers, out);
        } else {
            sendNotFound(out);
        }
    }
    private void startBattle(Map<String, String> headers, String body, BufferedWriter out) throws IOException {
        String player1 = getUsernameFromHeaders(headers);
        if (player1 == null) {
            sendUnauthorized(out, "Invalid token.");
            return;
        }

        String player2 = null;

        try {
            // Gegner aus dem Body lesen, falls vorhanden
            if (body != null && !body.trim().isEmpty()) {
                Map<String, String> requestBody = objectMapper.readValue(body, Map.class);
                player2 = requestBody.get("opponent");
            }

            // Zufälligen Gegner auswählen, wenn keiner angegeben wurde
            if (player2 == null) {
                player2 = usersDao.getRandomOpponent(player1);
                if (player2 == null) {
                    sendBadRequest(out, "No available opponent with a valid deck.");
                    return;
                }
            }

            // Decks der Spieler laden
            List<Card> player1Deck = usersDao.getDeck(player1);
            List<Card> player2Deck = usersDao.getDeck(player2);

            System.out.println("Starting battle: " + player1 + " vs " + player2);
            System.out.println("Player 1 deck size: " + player1Deck.size());
            System.out.println("Player 2 deck size: " + player2Deck.size());

            if (player1Deck.isEmpty() || player2Deck.isEmpty()) {
                sendBadRequest(out, "One or both players have no valid deck.");
                return;
            }

            // BattleService starten
            BattleService battleService = new BattleService(player1, player2, player1Deck, player2Deck, usersDao);
            String battleResult = battleService.startBattle();

            out.write("HTTP/1.1 200 OK\r\n");
            out.write("Content-Type: text/plain\r\n");
            out.write("\r\n");
            out.write(battleResult);
        } catch (Exception e) {
            sendInternalError(out, "Error during battle: " + e.getMessage());
        }
        out.flush();
    }




    private void showScoreboard(Map<String, String> headers, BufferedWriter out) throws IOException {
        String username = getUsernameFromHeaders(headers);
        if (username == null) {
            sendUnauthorized(out, "Invalid token.");
            return;
        }

        List<Map<String, Object>> scoreboard = usersDao.getScoreboard();
        if (!scoreboard.isEmpty()) {
            logScoreboard(scoreboard);
            String jsonResponse = objectMapper.writeValueAsString(scoreboard);
            out.write("HTTP/1.1 200 OK\r\n");
            out.write("Content-Type: application/json\r\n");
            out.write("\r\n");
            out.write(jsonResponse);
        } else {
            sendNotFound(out);
        }
        out.flush();
    }
    private void showStats(Map<String, String> headers, BufferedWriter out) throws IOException {
        String username = getUsernameFromHeaders(headers);
        if (username == null) {
            sendUnauthorized(out, "Invalid token.");
            return;
        }

        Optional<Map<String, Integer>> stats = usersDao.getUserStats(username);
        if (stats.isPresent()) {
            String jsonResponse = objectMapper.writeValueAsString(stats.get());
            out.write("HTTP/1.1 200 OK\r\n");
            out.write("Content-Type: application/json\r\n");
            out.write("\r\n");
            out.write(jsonResponse);
        } else {
            sendNotFound(out);
        }
        out.flush();
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
    private void sendInternalError(BufferedWriter out, String message) throws IOException {
        out.write("HTTP/1.1 500 Internal Server Error\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write(message);
        out.flush();
    }

    private void logScoreboard(List<Map<String, Object>> scoreboard) {
        System.out.println("=== SCOREBOARD ===");
        System.out.printf("%-5s %-15s %-15s %-10s %-10s %-5s%n",
                "Rank", "Username", "Games Played", "Games Won", "Games Lost", "ELO");
        System.out.println("------------------------------------------------------------");
        for (Map<String, Object> entry : scoreboard) {
            System.out.printf("%-5d %-15s %-15d %-10d %-10d %-5d%n",
                    entry.get("Rank"),
                    entry.get("Username"),
                    entry.get("GamesPlayed"),
                    entry.get("GamesWon"),
                    entry.get("GamesLost"),
                    entry.get("ELO")
            );
        }
        System.out.println("===================");
    }

}
