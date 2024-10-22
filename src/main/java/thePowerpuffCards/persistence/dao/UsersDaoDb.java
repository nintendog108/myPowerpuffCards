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

public class UsersDaoDb implements Dao<User> {

    /**
     * initializes the database with its tables
     */
    // PostgreSQL documentation: https://www.postgresqltutorial.com/postgresql-create-table/
    public static void initDb() {
        // Re-create the database
        try (Connection connection = DbConnection.getInstance().connect("")) {
            DbConnection.executeSql(connection, "DROP DATABASE swen", false);
            DbConnection.executeSql(connection, "CREATE DATABASE swen", false);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // Create the users table
        try {
            DbConnection.getInstance().executeSql("""
                CREATE TABLE IF NOT EXISTS users (
                    id serial PRIMARY KEY,
                    username VARCHAR (255) NOT NULL,
                    password VARCHAR (255) NOT NULL,
                    token VARCHAR (255),
                    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public Optional<User> get(int id) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT id, username, password, token 
                FROM users 
                WHERE id = ?
                """)
        ) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new User(
                        resultSet.getString(2),  // username
                        resultSet.getString(3)   // password
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Collection<User> getAll() {
        ArrayList<User> result = new ArrayList<>();
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT id, username, password, token 
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
                result.add(user);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    @Override
    public void save(User user) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                INSERT INTO users 
                (username, password, token) 
                VALUES (?, ?, ?);
                """)
        ) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getToken());
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void update(User user, String[] params) {
        // Update the user object with new parameters
        user.setUsername(Objects.requireNonNull(params[1], "Username cannot be null"));
        user.setPassword(Objects.requireNonNull(params[2], "Password cannot be null"));
        user.setToken(Objects.requireNonNull(params[3], "Token cannot be null"));

        // Persist the updated item
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                UPDATE users 
                SET username = ?, password = ?, token = ?
                WHERE id = ?;
                """)
        ) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getToken());
            statement.setInt(4, user.hashCode());  // Use hashcode to represent the user's unique ID
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void delete(User user) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                DELETE FROM users 
                WHERE id = ?;
                """)
        ) {
            statement.setInt(1, user.hashCode());
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
