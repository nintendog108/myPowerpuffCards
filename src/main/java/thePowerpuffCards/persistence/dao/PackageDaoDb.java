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
    public long savePackage(Package pckg) {
        String insertFirstCard = "INSERT INTO packages (cid) VALUES (?) RETURNING pid";
        String insertAllCards = "INSERT INTO packages (pid, cid) VALUES (?, ?)";

        try {
            DbConnection.getInstance().setAutoCommit(false);

            int packageId;

            // first card einfügen und `pid` abrufen
            try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(insertFirstCard)) {
                stmt.setString(1, pckg.getCards().get(0).getId());
                ResultSet rs = stmt.executeQuery(); // ergebnis rufen
                if (rs.next()) {
                    packageId = rs.getInt("pid"); //pid abrufen
                    pckg.setId(packageId);       // setze die Paket-ID im Paketobjekt
                } else {
                    throw new SQLException("Failed to retrieve package ID.");
                }
            }

            // weitere Karten einfügen
            try (PreparedStatement stmt2 = DbConnection.getInstance().prepareStatement(insertAllCards)) {
                for (int i = 1; i < pckg.getCards().size(); i++) {
                    stmt2.setInt(1, pckg.getId()); // verwende dieselbe Paket-ID
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

    public Package acquirePackage() throws SQLException {
        // ein Paket löschen und Karten abrufen
        String sql = """
        DELETE FROM packages
        WHERE pid = (SELECT MIN(pid) FROM packages)
        RETURNING pid, cid
    """;

        // SQL-Abfrage: Kartendetails abrufen
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

        // falls keine Pakete mehr verfügbar sind, erstelle neue
        if (packageMap.isEmpty()) {
            logger.warning("❌ Keine Pakete mehr verfügbar. Erstelle automatisch neue Pakete.");
            generateDefaultPackages(); // erstellt neue Pakete
            return acquirePackage();   // versucht erneut, ein Paket abzurufen
        }

        // das erste (älteste) Paket zurückgeben
        int packageId = packageMap.keySet().iterator().next();
        return new Package(packageId, packageMap.get(packageId));
    }
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

            // Neue Karten hinzufügen
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

            // neues Paket mit den Karten erstellen
            int packageId;
            try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(insertPackageSql)) {
                stmt.setString(1, defaultCards.get(0).getId());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    packageId = rs.getInt("pid");
                } else {
                    throw new SQLException("Fehler beim Erstellen eines neuen Pakets.");
                }
            }

            for (int i = 1; i < defaultCards.size(); i++) {
                try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement("INSERT INTO packages (pid, cid) VALUES (?, ?)")) {
                    stmt.setInt(1, packageId);
                    stmt.setString(2, defaultCards.get(i).getId());
                    stmt.executeUpdate();
                }
            }

            DbConnection.getInstance().commit();
            logger.info("✅ Neue Standard-Pakete wurden erfolgreich erstellt.");
        } catch (SQLException e) {
            DbConnection.getInstance().rollback();
            logger.severe("❌ Fehler beim Erstellen neuer Pakete: " + e.getMessage());
        } finally {
            DbConnection.getInstance().setAutoCommit(true);
        }
    }




}
