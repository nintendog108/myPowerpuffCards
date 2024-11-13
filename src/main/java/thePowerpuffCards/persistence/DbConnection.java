package thePowerpuffCards.persistence;

import java.io.Closeable;
import java.sql.*;

public class DbConnection implements Closeable {
    private static DbConnection instance;
    private Connection connection;

    public static void initDb() {
        // re-create the database
        try (Connection connection = getInstance().connect("postgres")) {
            // Datenbank löschen und neu erstellen
            executeSql(connection, "DROP DATABASE IF EXISTS monsterdb", true);
            executeSql(connection, "CREATE DATABASE monsterdb", true);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        // Verbindung zur neu erstellten "monsterdb" herstellen
        try (Connection connection = getInstance().connect("monsterdb")) {
            String sql = """
                    CREATE TABLE IF NOT EXISTS users (
                        uid serial PRIMARY KEY,
                        username VARCHAR (255) UNIQUE NOT NULL,
                        password VARCHAR (255) NOT NULL,
                        token VARCHAR (255) UNIQUE NOT NULL,
                        coins INT NOT NULL DEFAULT 20
                    );
                    """;
            executeSql(connection, sql);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public DbConnection() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC driver not found");
            e.printStackTrace();
        }
    }

    public Connection connect(String database) {
        try {
            return DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + database, "admin", "password");
        } catch (SQLException e) {
            System.err.println("Connection to the database failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Connection connect() throws SQLException {
        return connect("monsterdb");
    }

    public Connection getConnection() {
        if (connection == null) {
            try {
                connection = DbConnection.getInstance().connect();
                if (connection != null) {
                    System.out.println("Database connection established.");
                }
            } catch (SQLException e) {
                System.err.println("Error getting connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return connection;
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    public boolean executeSql(String sql) throws SQLException {
        return executeSql(getConnection(), sql, false);
    }

    public static boolean executeSql(Connection connection, String sql, boolean ignoreIfFails) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
            return true;
        } catch (SQLException e) {
            if (!ignoreIfFails) {
                throw e;
            }
            return false;
        }
    }

    public static boolean executeSql(Connection connection, String sql) throws SQLException {
        return executeSql(connection, sql, false);
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            connection = null;
        }
    }

    public static DbConnection getInstance() {
        if (instance == null) {
            instance = new DbConnection();
        }
        return instance;
    }
}
