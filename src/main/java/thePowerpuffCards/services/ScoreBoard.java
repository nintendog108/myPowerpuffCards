package thePowerpuffCards.services;

import java.util.HashMap;
import java.util.Map;

public class ScoreBoard {
    private final Map<String, Integer> userScores;

    public ScoreBoard() {
        this.userScores = new HashMap<>();
    }

    public void addUser(String username) {
        if (!userScores.containsKey(username)) {
            userScores.put(username, 100);  // standardwert für neue useer now 100
        }
    }

    public void updateScore(String username, int score) {
        if (userScores.containsKey(username)) {
            userScores.put(username, score);
        }
    }

    public int getScore(String username) {
        return userScores.getOrDefault(username, 100);
    }

    public void showScoreBoard() {
        userScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> System.out.println("User: " + entry.getKey() + " - ELO: " + entry.getValue()));
    }
}
