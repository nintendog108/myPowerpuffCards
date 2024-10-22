package thePowerpuffCards.persistence;

import thePowerpuffCards.services.models.User;
import thePowerpuffCards.persistence.dao.UsersDaoDb;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class DaoDemo {
    private static thePowerpuffCards.persistence.dao.Dao<User> dao;

    public static void main(String[] args) {
        // Benutzer-DAO wird mit der Datenbank verbunden
        dao = new thePowerpuffCards.persistence.dao.Dao<User>() {
            @Override
            public Optional<User> get(int id) {
                return Optional.empty();
            }

            @Override
            public Collection<User> getAll() {
                return List.of();
            }

            @Override
            public void save(User user) {

            }

            @Override
            public void update(User user, String[] params) {

            }

            @Override
            public void delete(User user) {

            }
        };
        UsersDaoDb.initDb(); // Datenbank initialisieren

        // Ein neuer Benutzer wird gespeichert
        dao.save(new User("John Doe", "pw123"));

        // Benutzer abrufen
        User user1 = getUser(1);
        System.out.println(user1);

        // Benutzer aktualisieren
        dao.update(user1, new String[]{"1", "Max Musterfrau", "pw123"});
        System.out.println("Updated user: ");
        System.out.println(user1);

        // Benutzer löschen und neuen Benutzer speichern
        User user2 = getUser(2);
        dao.delete(user2);
        dao.save(new User("Jane Doe", "pw123"));

        // Alle Benutzer aus der Datenbank anzeigen
        dao.getAll().forEach(System.out::println);
    }

    // Benutzer anhand der ID abrufen
    private static User getUser(int id) {
        Optional<User> user = dao.get(id);

        // Falls Benutzer nicht existiert, wird ein neuer Benutzer zurückgegeben
        return user.orElseGet(
                () -> new User("Unknown", "unknownPassword")
        );
    }
}
