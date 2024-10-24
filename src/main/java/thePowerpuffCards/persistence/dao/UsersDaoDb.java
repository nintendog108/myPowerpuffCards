package thePowerpuffCards.persistence.dao;

import thePowerpuffCards.services.models.User;
import thePowerpuffCards.persistence.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class UsersDaoDb implements Dao<User> {

    private static final Logger logger = Logger.getLogger(UsersDaoDb.class.getName());

    public static void initDb() {
        try (Connection connection = DbConnection.getInstance().connect("")) {
            DbConnection.executeSql(connection, "DROP DATABASE IF EXISTS swen", false);
            DbConnection.executeSql(connection, "CREATE DATABASE swen", false);
        } catch (SQLException e) {
            logger.severe("Error initializing the database: " + e.getMessage());
        }

        try {
            DbConnection.getInstance().executeSql("""
            CREATE TABLE IF NOT EXISTS user (
                uid serial PRIMARY KEY,
                username VARCHAR (255) NOT NULL,
                password VARCHAR (255) NOT NULL,
                token VARCHAR (255),
                coins INT DEFAULT 20,
                last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """);
        } catch (SQLException e) {
            logger.severe("Error creating the user table: " + e.getMessage());
        }
    }


    @Override
    public Optional<User> get(int id) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT uid, username, password, token, coins
                FROM user
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
    public Collection<User> getAll() {
        ArrayList<User> result = new ArrayList<>();
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT uid, username, password, token, coins
                FROM user
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

    @Override
    public void save(User user) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
            INSERT INTO user
            (username, password, token, coins)
            VALUES (?, ?, ?, ?)
            RETURNING uid;
            """)
        ) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getToken());
            statement.setInt(4, user.getCoins());  // Füge die Coins des Benutzers hinzu
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));  // Setze die ID des neu eingefügten Benutzers
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

        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                UPDATE user
                SET username = ?, password = ?, token = ?, coins = ?
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
                DELETE FROM user
                WHERE uid = ?;
                """)
        ) {
            statement.setInt(1, user.getId());
            statement.execute();
        } catch (SQLException e) {
            logger.severe("Error deleting user: " + e.getMessage());
        }
    }
}
