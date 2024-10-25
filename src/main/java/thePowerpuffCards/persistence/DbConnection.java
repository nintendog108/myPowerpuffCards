package thePowerpuffCards.persistence;

import java.io.Closeable;
import java.sql.*;

public class DbConnection implements Closeable {
    private static DbConnection instance;

    private Connection connection;

    /**
     * Loads the PostgreSql JDBC-driver
     * Don't forget to add the dependency in the pom.xml, like
     *         <dependency>
     *             <groupId>org.postgresql</groupId>
     *             <artifactId>postgresql</artifactId>
     *             <version>42.2.18.jre7</version>
     *         </dependency>
     */
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
        try ( Statement statement = connection.createStatement() ) {
            statement.execute(sql );
            return true;
        } catch (SQLException e) {
            if( !ignoreIfFails )
                throw e;
            return false;
        }
    }

    public static boolean executeSql(Connection connection, String sql) throws SQLException {
        return executeSql(connection, sql, false);
    }


    @Override
    public void close() {
        if( connection!=null ) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            connection = null;
        }
    }




    public static DbConnection getInstance() {
        if(instance==null)
            instance = new DbConnection();
        return instance;
    }
}
