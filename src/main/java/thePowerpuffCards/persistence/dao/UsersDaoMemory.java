package thePowerpuffCards.persistence.dao;

import thePowerpuffCards.services.models.User;

import java.util.*;

public class UsersDaoMemory implements Dao<User> {
    private Map<Integer, User> users = new HashMap<>();

    @Override
    public Optional<User> get(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public void save(User user) {
        users.put(user.hashCode(), user);  // Benutzer wird mit seinem Hashcode als ID gespeichert
    }

    @Override
    public void update(User user, String[] params) {
        // Aktualisiere den Benutzer mit neuen Parametern
        user.setUsername(Objects.requireNonNull(params[1], "Username cannot be null"));
        user.setPassword(Objects.requireNonNull(params[2], "Password cannot be null"));
        user.setToken(Objects.requireNonNull(params[3], "Token cannot be null"));
        // Aktualisierter Benutzer wird persistiert
        users.put(user.hashCode(), user);
    }

    @Override
    public void delete(User user) {
        users.remove(user.hashCode());  // Benutzer wird anhand seines Hashcodes entfernt
    }
}
