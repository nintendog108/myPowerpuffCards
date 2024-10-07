package thePowerpuffCards.services.models;

public class Session {
    private String sessionId;
    private String username;

    public Session(String sessionId, String username) {
        this.sessionId = sessionId;
        this.username = username;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUsername() {
        return username;
    }
    public String toString() {
        return "{ \"sessionId\": \"" + sessionId + "\", \"username\": \"" + username + "\" }";
    }
}
