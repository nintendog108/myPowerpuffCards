package thePowerpuffCards.persistence;
import java.util.Optional;


import thePowerpuffCards.persistence.dao.UsersDaoDb;
import thePowerpuffCards.services.models.User;

public class DaoTestMain {
    public static void main(String[] args) {
        UsersDaoDb usersDaoDb = new UsersDaoDb();

        // Test: Neuen Benutzer speichern
        User user = new User("bubbles3", "bubbles3");
        usersDaoDb.save(user);
        System.out.println("User saved: " + user);

        // Test: Benutzer abrufen
        Optional<User> retrievedUser = usersDaoDb.get(user.getId());
        System.out.println("User retrieved: " + retrievedUser.orElse(null));

        // Test: Benutzer aktualisieren
       // user.setUsername("updatedBubbles");
      //  usersDaoDb.update(user, new String[]{"1", "updatedBubbles", "newBubbles"});
      //  System.out.println("User updated: " + user);

        // Test: Benutzer löschen
        //usersDaoDb.delete(user);
        //System.out.println("User deleted: " + user);
    }
}