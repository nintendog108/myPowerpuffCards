package thePowerpuffCards.database;

import thePowerpuffCards.services.models.Session;
import thePowerpuffCards.services.models.User;

import java.util.HashMap;
import java.util.Map;

public class Database {
    private final Map<String, User> userStorage;

    private final Map<String, Session> sessionStorage;


    public Database() {
        this.userStorage = new HashMap<>();
        this.sessionStorage = new HashMap<>();
    }


    public void addUser(User user) {
        if (!isUserExisting(user.getUsername())) {
            userStorage.put(user.getUsername(), user);
        } else {
            System.out.println("User already exists: " + user.getUsername());
        }
    }


    public void addSession(Session session) {
        if (!sessionStorage.containsKey(session.getSessionId())) {
            sessionStorage.put(session.getSessionId(), session);
        } else {
            System.out.println("Session already exists: " + session.getSessionId());
        }
    }


    public boolean isUserExisting(String username) {
        return userStorage.containsKey(username);
    }


    public User findUser(String username) {
        return userStorage.get(username);
    }


    public Session findSessionById(String sessionId) {
        return sessionStorage.get(sessionId);
    }


    public Map<String, User> getAllUsers() {
        return userStorage;
    }


    public Map<String, Session> getAllSessions() {
        return sessionStorage;
    }

    // Methode, um einen Benutzer anhand seines Tokens zu finden
    public User findUserByToken(String token) {
        for (User user : userStorage.values()) {
            if (user.getToken() != null && user.getToken().equals(token)) {
                return user;
            }
        }
        return null;
    }


    public User findUserByUsernameAndPassword(String username, String password) {
        User user = userStorage.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public boolean removeSessionById(String sessionId) {
        if (sessionStorage.containsKey(sessionId)) {
            sessionStorage.remove(sessionId);
            return true;
        }
        return false;
    }
}
