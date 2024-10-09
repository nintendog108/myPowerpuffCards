package thePowerpuffCards.database;

import thePowerpuffCards.services.models.Session;
import thePowerpuffCards.services.models.User;

import java.util.ArrayList;
import java.util.List;
//TODO: es wird noch databaseeisiert
public class Database {
    private final List<User> users = new ArrayList<>();
    private final List<Session> sessions = new ArrayList<>();

    public Database() {}

    public void addUser(User user) {
        if (!isUserExisting(user.getUsername())) {
            users.add(user);
        }
    }


    public boolean isUserExisting(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public User findUserByUsernameAndPassword(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public void addSession(Session session) {
        sessions.add(session);
    }

    public List<Session> getAllSessions() {
        return sessions;
    }


    public boolean removeSessionById(String sessionId) {
        return sessions.removeIf(session -> session.getSessionId().equals(sessionId));
    }

    public List<User> getAllUsers() {
        return users;
    }
}
