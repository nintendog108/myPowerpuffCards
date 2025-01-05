package thePowerpuffCards.persistence.dao;

import thePowerpuffCards.core.models.User;
import thePowerpuffCards.core.models.cards.Card;
import thePowerpuffCards.core.models.cards.ElementType;
import thePowerpuffCards.core.models.cards.monster.MonsterCard;
import thePowerpuffCards.core.models.cards.monster.MonsterType;
import thePowerpuffCards.core.models.cards.spell.SpellCard;
import thePowerpuffCards.persistence.DbConnection;
import thePowerpuffCards.core.models.cards.Package;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class PackageDaoDb {

    private static final Logger logger = Logger.getLogger(PackageDaoDb.class.getName());

    // saves a package to the database
    public long savePackage(Package pckg) {
        String insertFirstCard = "INSERT INTO packages (cid) VALUES (?) RETURNING pid";
        String insertAllCards = "INSERT INTO packages (pid, cid) VALUES (?, ?)";

        try {
            DbConnection.getInstance().setAutoCommit(false);
            int packageId;

            // insert the first card and get the package ID
            try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(insertFirstCard)) {
                stmt.setString(1, pckg.getCards().get(0).getId());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    packageId = rs.getInt("pid");
                    pckg.setId(packageId);
                } else {
                    throw new SQLException("Failed to retrieve package ID.");
                }
            }

            // insert the rest of the cards into the package
            try (PreparedStatement stmt2 = DbConnection.getInstance().prepareStatement(insertAllCards)) {
                for (int i = 1; i < pckg.getCards().size(); i++) {
                    stmt2.setInt(1, pckg.getId());
                    stmt2.setString(2, pckg.getCards().get(i).getId());
                    stmt2.addBatch();
                }
                stmt2.executeBatch();
            }

            DbConnection.getInstance().commit();
            return pckg.getId();

        } catch (SQLException e) {
            try {
                DbConnection.getInstance().rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
            System.err.println("Error saving package: " + e.getMessage());
        } finally {
            try {
                DbConnection.getInstance().setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Failed to reset auto-commit: " + ex.getMessage());
            }
        }
        return -1;
    }

    // retrieves and removes the oldest available package from the database
    public Package acquirePackage() throws SQLException {
        String sql = """
        DELETE FROM packages
        WHERE pid = (SELECT MIN(pid) FROM packages)
        RETURNING pid, cid
        """;

        String cardDetailsSql = """
        SELECT cid, name, damage, element_type, monster_type
        FROM card
        WHERE cid = ?
        """;

        Map<Integer, List<Card>> packageMap = new HashMap<>();

        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int packageId = rs.getInt("pid");
                String cardId = rs.getString("cid");

                try (PreparedStatement cardStmt = DbConnection.getInstance().prepareStatement(cardDetailsSql)) {
                    cardStmt.setString(1, cardId);
                    ResultSet cardRs = cardStmt.executeQuery();

                    if (cardRs.next()) {
                        String name = cardRs.getString("name");
                        double damage = cardRs.getDouble("damage");
                        ElementType elementType = ElementType.valueOf(cardRs.getString("element_type"));
                        MonsterType monsterType = cardRs.getString("monster_type") != null
                                ? MonsterType.valueOf(cardRs.getString("monster_type"))
                                : null;

                        Card card = monsterType != null
                                ? new MonsterCard(cardId, name, damage, elementType, monsterType)
                                : new SpellCard(cardId, name, damage, elementType);

                        packageMap.computeIfAbsent(packageId, k -> new ArrayList<>()).add(card);
                    } else {
                        throw new SQLException("Card not found for ID: " + cardId);
                    }
                }
            }
        }

        // if no packages are left, generate new ones
        if (packageMap.isEmpty()) {
            logger.info("❌ No packages available. Generating default packages.");
            generateDefaultPackages();
            return acquirePackage();
        }

        int packageId = packageMap.keySet().iterator().next();
        return new Package(packageId, packageMap.get(packageId));
    }

    // generates default card packages if none are available
    private void generateDefaultPackages() throws SQLException {
        String insertPackageSql = "INSERT INTO packages (cid) VALUES (?) RETURNING pid";
        String insertCardSql = "INSERT INTO card (cid, name, damage, element_type, monster_type) VALUES (?, ?, ?, ?, ?)";

        List<Card> defaultCards = List.of(
                new MonsterCard("new-card-1", "FireGoblin", 10.0, ElementType.FIRE, MonsterType.GOBLIN),
                new MonsterCard("new-card-2", "Dragon", 50.0, ElementType.NORMAL, MonsterType.DRAGON),
                new SpellCard("new-card-3", "WaterSpell", 20.0, ElementType.WATER),
                new MonsterCard("new-card-4", "Ork", 40.0, ElementType.NORMAL, MonsterType.ORK),
                new SpellCard("new-card-5", "FireSpell", 25.0, ElementType.FIRE)
        );

        try {
            DbConnection.getInstance().setAutoCommit(false);

            for (Card card : defaultCards) {
                try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(insertCardSql)) {
                    stmt.setString(1, card.getId());
                    stmt.setString(2, card.getName());
                    stmt.setDouble(3, card.getDamage());
                    stmt.setString(4, card.getElementType().name());
                    MonsterType monsterType = card instanceof MonsterCard ? MonsterType.getMonsterType(card.getName()) : null;
                    stmt.setString(5, monsterType != null ? monsterType.name() : null);
                    stmt.executeUpdate();
                }
            }

            DbConnection.getInstance().commit();
            logger.info("✅ Default packages generated successfully.");
        } catch (SQLException e) {
            DbConnection.getInstance().rollback();
            logger.severe("❌ Error generating default packages: " + e.getMessage());
        } finally {
            DbConnection.getInstance().setAutoCommit(true);
        }
    }
}
