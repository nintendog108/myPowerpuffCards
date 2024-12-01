package thePowerpuffCards.persistence.dao;

import thePowerpuffCards.persistence.DbConnection;
import thePowerpuffCards.services.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class CardDaoDb implements Dao<User> {

    private static final Logger logger = Logger.getLogger(CardDaoDb.class.getName());

//TODO: Dao vorgegebene sachen drinnen lassen, user sachen löschen

    public boolean userExists(String username) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
            SELECT COUNT(*) FROM users WHERE username = ?
            """)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.severe("Error checking user existence: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Optional<User> get(int id) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT uid, username, password, token, coins
                FROM users
                WHERE uid = ?
                """)
        ) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User(
                        resultSet.getString(2),  // username
                        resultSet.getString(3)   // password
                );
                user.setToken(resultSet.getString(4));  // token
                user.setId(resultSet.getInt(1));        // Set the ID in User object
                return Optional.of(user);
            }
        } catch (SQLException e) {
            logger.severe("Error fetching user: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> get(String text) {
        return Optional.empty();
    }

    @Override
    public Collection<User> getAll() {
        ArrayList<User> result = new ArrayList<>();
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT uid, username, password, token, coins
                FROM users
                """)
        ) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User user = new User(
                        resultSet.getString(2), // username
                        resultSet.getString(3)  // password
                );
                user.setToken(resultSet.getString(4)); // token
                user.setCoins(resultSet.getInt(5));     // Coins setzen
                user.setId(resultSet.getInt(1));       // Set the ID in User object
                result.add(user);
            }
        } catch (SQLException e) {
            logger.severe("Error fetching users: " + e.getMessage());
        }
        return result;
    }

    public void save(User user) {
        if (userExists(user.getUsername())) {
            logger.severe("User with username " + user.getUsername() + " already exists.");
            return; // Bricht die Speicherung ab
        }
//TODO: spell karten -> monster leer lassen

        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
        INSERT INTO users
        (username, password, token, coins)
        VALUES (?, ?, ?, ?)
        RETURNING uid;
        """)
        ) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, (user.getToken() != null) ? user.getToken() : "");
            statement.setInt(4, user.getCoins());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            logger.severe("Error saving user: " + e.getMessage());
        }
    }



    @Override
    public void update(User user, String[] params) {
        user.setUsername(Objects.requireNonNull(params[0], "Username cannot be null"));
        user.setPassword(Objects.requireNonNull(params[1], "Password cannot be null"));
        user.setToken(Objects.requireNonNull(params[2], "Token cannot be null"));
        //user.setCoins(Integer.parseInt(Objects.requireNonNull(params[3], "Coins cannot be null")));

        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                UPDATE users
                SET username = ?, password = ?, token = ?, coins = 15
                WHERE uid = ?;
                """)
        ) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getToken());
            statement.setInt(4, user.getId()); // Use the user's stored ID
            statement.execute();
        } catch (SQLException e) {
            logger.severe("Error updating user: " + e.getMessage());
        }
    }

    @Override
    public void delete(User user) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                DELETE FROM users
                WHERE uid = ?;
                """)
        ) {
            statement.setInt(1, user.getId());
            statement.execute();
        } catch (SQLException e) {
            logger.severe("Error deleting user: " + e.getMessage());
        }
    }

    public void addSession(User user) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                    UPDATE users SET token = ? WHERE username = ?;
                    """)) {
            statement.setString(1, user.getToken());
            statement.setString(2, user.getUsername());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Error adding session: " + e.getMessage());
        }
    }
}