package thePowerpuffCards.persistence.dao;

import thePowerpuffCards.core.models.User;
import thePowerpuffCards.core.models.cards.Card;
import thePowerpuffCards.persistence.DbConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class UsersDaoDb implements Dao<User> {

    private static final Logger logger = Logger.getLogger(UsersDaoDb.class.getName());

    public User findUserByUsernameAndPassword(String username, String password) {
        Collection<User> users = getAll();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

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
            """)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User(
                        resultSet.getString("username"),
                        resultSet.getString("password")
                );
                user.setToken(resultSet.getString("token"));
                user.setCoins(resultSet.getInt("coins"));
                user.setId(resultSet.getInt("uid"));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            logger.severe("Error fetching user by ID: " + e.getMessage());
        }
        return Optional.empty();
    }


    public Optional<User> getText(String username) {
        System.out.println("Fetching user from database: " + username); // Debug-Ausgabe
        String sql = """
        SELECT uid, username, password, token, coins
        FROM users
        WHERE username = ?;
    """;

        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                User user = new User(
                        resultSet.getString("username"),
                        resultSet.getString("password")
                );
                user.setId(resultSet.getInt("uid"));
                user.setToken(resultSet.getString("token"));
                user.setCoins(resultSet.getInt("coins"));
                System.out.println("User found: " + user.getUsername() + " with Token: " + user.getToken()); // Debug-Ausgabe
                return Optional.of(user);
            } else {
                System.out.println("No user found with username: " + username); // Debug-Ausgabe
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by username: " + e.getMessage()); // Debug-Ausgabe
        }
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

        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
        INSERT INTO users
        (username, password, token, coins)
        VALUES (?, ?, ?, ?)
        RETURNING uid;
        """)
        ) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, "");
            statement.setInt(4, user.getCoins());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            logger.severe("Error saving user: " + e.getMessage());
        }
    }

    public void updateUser(User user) {
        String sql = """
        UPDATE users
        SET coins = ?
        WHERE uid = ?
    """;

        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            stmt.setInt(1, user.getCoins());
            stmt.setInt(2, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Error updating user: " + e.getMessage());
        }
    }

    public void addCardToUser(User user, Card card) {
        String sql = """
        INSERT INTO user_cards (uid, cid)
        VALUES (?, ?);
    """;

        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            stmt.setInt(1, user.getId());
            stmt.setString(2, card.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Error adding card to user: " + e.getMessage());
        }
    }


    public User getUserByToken(String token) {
        String sql = """
        SELECT uid, username, password, token, coins
        FROM users
        WHERE token = ?;
    """;

        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, token);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                User user = new User(
                        resultSet.getString("username"),
                        resultSet.getString("password")
                );
                user.setId(resultSet.getInt("uid"));
                user.setToken(resultSet.getString("token"));
                user.setCoins(resultSet.getInt("coins"));
                return user;
            }
        } catch (SQLException e) {
            logger.severe("Error fetching user by token: " + e.getMessage());
        }
        return null;
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
        String sql = "UPDATE users SET token = ? WHERE username = ?";
        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, user.getToken());
            stmt.setString(2, user.getUsername());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Error adding session for user: " + e.getMessage());
        }
    }

}