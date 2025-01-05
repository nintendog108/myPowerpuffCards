package thePowerpuffCards.persistence.dao;

import thePowerpuffCards.core.models.User;
import thePowerpuffCards.core.models.cards.Card;
import thePowerpuffCards.core.models.cards.ElementType;
import thePowerpuffCards.core.models.cards.monster.MonsterCard;
import thePowerpuffCards.core.models.cards.monster.MonsterType;
import thePowerpuffCards.core.models.cards.spell.SpellCard;
import thePowerpuffCards.persistence.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class UsersDaoDb implements Dao<User> {

    public static final Logger logger = Logger.getLogger(UsersDaoDb.class.getName());

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
     //   System.out.println("Fetching user from database: " + username); // Debug-Ausgabe
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
              //  System.out.println("User found: " + user.getUsername() + " with Token: " + user.getToken()); // Debug-Ausgabe
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
                user.setId(resultSet.getInt(1));
                user.setToken(resultSet.getString(4));
                user.setCoins(resultSet.getInt(5));

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
            return;
        }

        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
        INSERT INTO users (username, password, token, coins)
        VALUES (?, ?, ?, ?) RETURNING uid;
        """);
             PreparedStatement statsStatement = DbConnection.getInstance().prepareStatement("""
        INSERT INTO stats (username, games_played, games_won, games_lost, elo)
        VALUES (?, 0, 0, 0, 100);
        """)) {
            // Benutzer speichern
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, "");
            statement.setInt(4, user.getCoins());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
            }

            // Statistiken speichern
            statsStatement.setString(1, user.getUsername());
            statsStatement.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Error saving user: " + e.getMessage());
        }
    }


    public void update(User user, String[] params) {
        user.setUsername(Objects.requireNonNull(params[0], "Username cannot be null"));
        user.setPassword(Objects.requireNonNull(params[1], "Password cannot be null"));
        user.setToken(Objects.requireNonNull(params[2], "Token cannot be null"));

        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
            UPDATE users
            SET username = ?, password = ?, token = ?, coins = ?
            WHERE uid = ?;
            """)
        ) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getToken());
            statement.setInt(4, user.getCoins());
            statement.setInt(5, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Error updating user: " + e.getMessage());
        }
    }

    public void updateUserProfile(String username, String name, String bio, String image) {
        String sql = """
        INSERT INTO userprofile (username, name, bio, image)
        VALUES (?, ?, ?, ?)
        ON CONFLICT (username) DO UPDATE
        SET name = EXCLUDED.name, bio = EXCLUDED.bio, image = EXCLUDED.image;
    """;

        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, name);
            stmt.setString(3, bio);
            stmt.setString(4, image);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Error updating user profile: " + e.getMessage());
        }
    }

    public Optional<Map<String, String>> getUserProfile(String username) {
        String sql = "SELECT name, bio, image FROM userprofile WHERE username = ?";
        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Map<String, String> profile = new HashMap<>();
                profile.put("Name", rs.getString("name"));
                profile.put("Bio", rs.getString("bio"));
                profile.put("Image", rs.getString("image"));
                return Optional.of(profile);
            }
        } catch (SQLException e) {
            logger.severe("Error fetching user profile: " + e.getMessage());
        }
        return Optional.empty();
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

    public List<Card> getCardsFromStack(String username) {
        String sql = """
        SELECT card.cid, card.name, card.damage, card.element_type, card.monster_type
        FROM stack
        JOIN card ON stack.cid = card.cid
        WHERE stack.username = ?
    """;

        List<Card> cards = new ArrayList<>();

        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String id = rs.getString("cid");
                String name = rs.getString("name");
                double damage = rs.getDouble("damage");
                ElementType elementType = ElementType.valueOf(rs.getString("element_type"));
                MonsterType monsterType = null;
                if (rs.getString("monster_type") != null) {
                    monsterType = MonsterType.valueOf(rs.getString("monster_type"));
                }

                Card card;
                if (monsterType != null) {
                    card = new MonsterCard(id, name, damage, elementType, monsterType);
                } else {
                    card = new SpellCard(id, name, damage, elementType);
                }


                cards.add(card);
            }
        } catch (SQLException e) {
            logger.severe("Error fetching cards from stack for user: " + e.getMessage());
        }

        return cards;
    }

    public void saveDeck(String username, List<Card> deck) {
        if (deck.isEmpty()) {
            logger.warning("⚠️ WARNUNG: `saveDeck()` wurde aufgerufen, aber das Deck von " + username + " ist leer.");
            return;
        }

        String checkExistingDeckSql = "SELECT COUNT(*) FROM deck WHERE username = ?";
        String deleteSql = "DELETE FROM deck WHERE username = ?";
        String insertSql = "INSERT INTO deck (username, cid, deck_slot) VALUES (?, ?, ?)";

        try (Connection conn = DbConnection.getInstance().connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement checkStmt = conn.prepareStatement(checkExistingDeckSql)) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    logger.warning("⚠️ WARNUNG: `saveDeck()` wurde aufgerufen, aber " + username + " hat bereits ein Deck. Es wird vorher gelöscht.");
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                        deleteStmt.setString(1, username);
                        deleteStmt.executeUpdate();
                    }
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                for (int i = 0; i < deck.size(); i++) {
                    insertStmt.setString(1, username);
                    insertStmt.setString(2, deck.get(i).getId());
                    insertStmt.setInt(3, i);
                    insertStmt.addBatch();
                }

                insertStmt.executeBatch();
                conn.commit();
                logger.info("✅ Deck für " + username + " erfolgreich gespeichert!");

            } catch (SQLException e) {
                conn.rollback();
                logger.severe("❌ Fehler beim Speichern des Decks: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.severe("❌ Fehler beim Verbinden mit der Datenbank: " + e.getMessage());
        }
    }

    public synchronized List<Card> getDeck(String username) {
        String sql = """
            SELECT card.cid, card.name, card.damage, card.element_type, card.monster_type
            FROM deck
            JOIN card ON deck.cid = card.cid
            WHERE deck.username = ?
            ORDER BY deck.deck_slot
            """;

        List<Card> deck = new ArrayList<>();
        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String id = rs.getString("cid");
                String name = rs.getString("name");
                double damage = rs.getDouble("damage");
                String rawElementType = rs.getString("element_type");

                ElementType elementType = ElementType.valueOf(rawElementType);
                String monsterTypeStr = rs.getString("monster_type");
                MonsterType monsterType = monsterTypeStr != null ? MonsterType.valueOf(monsterTypeStr) : null;

                Card card;
                if (monsterType != null) {
                    card = new MonsterCard(id, name, damage, elementType, monsterType);
                } else {
                    card = new SpellCard(id, name, damage, elementType);
                }

                deck.add(card);
            }
        } catch (SQLException e) {
            logger.severe("Error fetching deck: " + e.getMessage());
        }
     //   System.out.println("Deck fetched for user: + username + ", size: " + deck.size());
        return deck;
    }


    public void clearDeck(String username) {
        if (getDeck(username).isEmpty()) {
            logger.info("Deck is empty!");
         //  logger.warning("⚠️ WARNUNG: `clearDeck()` wurde aufgerufen, aber " + username + " hat bereits kein Deck.");
            return;
        }

        String sql = "DELETE FROM deck WHERE username = ?";
        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, username);
            int rowsDeleted = stmt.executeUpdate();
            logger.info("✅ Deck für " + username + " gelöscht: " + rowsDeleted + " Karten entfernt.");
        } catch (SQLException e) {
            logger.severe("❌ Fehler beim Löschen des Decks für " + username + ": " + e.getMessage());
        }
    }




    public Optional<Map<String, Integer>> getUserStats(String username) {
        String sql = """
        SELECT games_played, games_won, games_lost, elo
        FROM stats
        WHERE username = ?
    """;

        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Map<String, Integer> stats = new HashMap<>();
                stats.put("GamesPlayed", rs.getInt("games_played"));
                stats.put("GamesWon", rs.getInt("games_won"));
                stats.put("GamesLost", rs.getInt("games_lost"));
                stats.put("ELO", rs.getInt("elo"));
                return Optional.of(stats);
            }
        } catch (SQLException e) {
            logger.severe("Error fetching stats for user " + username + ": " + e.getMessage());
        }
        return Optional.empty();
    }
    public List<Map<String, Object>> getScoreboard() {
        String sql = """
        SELECT username, games_played, games_won, games_lost, elo
        FROM stats
        ORDER BY elo DESC
    """;

        List<Map<String, Object>> scoreboard = new ArrayList<>();
        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            int rank = 1;
            while (rs.next()) {
                Map<String, Object> entry = new HashMap<>();
                entry.put("Rank", rank++);
                entry.put("Username", rs.getString("username"));
                entry.put("GamesPlayed", rs.getInt("games_played"));
                entry.put("GamesWon", rs.getInt("games_won"));
                entry.put("GamesLost", rs.getInt("games_lost"));
                entry.put("ELO", rs.getInt("elo"));
                scoreboard.add(entry);
            }
        } catch (SQLException e) {
            logger.severe("Error fetching scoreboard: " + e.getMessage());
        }
        return scoreboard;
    }
    public void incrementGamesPlayed(String username) {
        String sql = """
        UPDATE stats
        SET games_played = games_played + 1
        WHERE username = ?
    """;

        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Error incrementing games played for user: " + username + " - " + e.getMessage());
        }
    }
    public void incrementGamesWon(String username) {
        String sql = """
        UPDATE stats
        SET games_won = games_won + 1
        WHERE username = ?
    """;

        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Error incrementing games won for user: " + username + " - " + e.getMessage());
        }
    }
    public void incrementGamesLost(String username) {
        String sql = """
        UPDATE stats
        SET games_lost = games_lost + 1
        WHERE username = ?
    """;

        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Error incrementing games lost for user: " + username + " - " + e.getMessage());
        }
    }
    public synchronized void updateElo(String username, int eloChange) {
        String sql = """
        UPDATE stats
        SET elo = elo + ?
        WHERE username = ?
        """;

        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            stmt.setInt(1, eloChange);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Error updating ELO for user: " + username + " - " + e.getMessage());
        }
    }


    public String getRandomOpponent(String currentUser) {
        String sql = """
        SELECT username
        FROM deck
        WHERE username != ?
        GROUP BY username
        HAVING COUNT(*) = 4
        ORDER BY RANDOM()
        LIMIT 1;
    """;

        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, currentUser);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            logger.severe("Error fetching random opponent: " + e.getMessage());
        }
        return null;
    }




}