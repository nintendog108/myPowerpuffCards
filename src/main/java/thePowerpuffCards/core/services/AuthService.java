package thePowerpuffCards.core.services;

import thePowerpuffCards.core.models.User;
import thePowerpuffCards.persistence.dao.UsersDaoDb;

import java.util.Map;
import java.util.Optional;

public class AuthService {
    private final UsersDaoDb usersDaoDb;

    public AuthService(UsersDaoDb usersDaoDb) {
        this.usersDaoDb = usersDaoDb;
    }

    public boolean authenticate(Map<String, String> headers) {
        String authorization = headers.get("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
        //    System.out.println("Authorization header missing or invalid"); // Debug-Ausgabe
            return false;
        }

        String token = extractToken(authorization);
        String username = token.split("-")[0];
      //  System.out.println("Extracted Username: " + username); // Debug-Ausgabe

        Optional<User> user = usersDaoDb.getText(username);
        if (user.isEmpty()) {
            System.out.println("User not found: " + username); // Debug-Ausgabe
            return false;
        }

       // System.out.println("Expected Token: " + user.get().getToken()); // Debug-Ausgabe
        if (!user.get().getToken().equals(token)) {
       //     System.out.println("Received Token: " + token); // Debug-Ausgabe
        //    System.out.println("Token mismatch for user: " + username); // Debug-Ausgabe
            return false;
        }

     //   System.out.println("User authenticated successfully: " + username); // Debug-Ausgabe
        return true;
    }


    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }
        String token = authorizationHeader.substring("Bearer ".length()).trim();
      //  System.out.println("Extracted Token: " + token);

        return token;
    }

}
