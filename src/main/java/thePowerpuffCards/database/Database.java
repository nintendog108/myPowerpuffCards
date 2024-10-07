package thePowerpuffCards.database;

import thePowerpuffCards.services.models.Session;
import thePowerpuffCards.services.models.User;

import java.util.ArrayList;
import java.util.List;

public class Database {
    private final List<User> users = new ArrayList<>();  // Liste zur Speicherung der Benutzer
    private final List<Session> sessions = new ArrayList<>();  // Liste zur Speicherung der aktiven Sessions

    // Konstruktor
    public Database() {}

    // Benutzer zur Datenbank hinzufügen
    public void addUser(User user) {
        if (!isUserExisting(user.getUsername())) {
            users.add(user);
        }
    }

    // Überprüfen, ob ein Benutzer mit dem gegebenen Benutzernamen existiert
    public boolean isUserExisting(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    // Sucht einen Benutzer basierend auf Benutzername und Passwort
    public User findUserByUsernameAndPassword(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    // Fügt eine neue Session zur Datenbank hinzu
    public void addSession(Session session) {
        sessions.add(session);
    }

    // Gibt alle aktiven Sessions zurück
    public List<Session> getAllSessions() {
        return sessions;
    }

    // Entfernt eine Session anhand der Session-ID
    public boolean removeSessionById(String sessionId) {
        return sessions.removeIf(session -> session.getSessionId().equals(sessionId));
    }

    // Rückgabe aller Benutzer
    public List<User> getAllUsers() {
        return users;
    }
}
