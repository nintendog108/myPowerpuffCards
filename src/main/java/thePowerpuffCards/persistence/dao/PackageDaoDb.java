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

            // Erste Karte einfügen und `pid` abrufen
            try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(insertFirstCard)) {
                stmt.setString(1, pckg.getCards().get(0).getId());
                ResultSet rs = stmt.executeQuery(); // Abrufen des Ergebnisses
                if (rs.next()) {
                    packageId = rs.getInt("pid"); // Generierte `pid` abrufen
                    pckg.setId(packageId);       // Setze die Paket-ID im Paketobjekt
                } else {
                    throw new SQLException("Failed to retrieve package ID.");
                }
            }

            // Weitere Karten einfügen
            try (PreparedStatement stmt2 = DbConnection.getInstance().prepareStatement(insertAllCards)) {
                for (int i = 1; i < pckg.getCards().size(); i++) {
                    stmt2.setInt(1, pckg.getId()); // Verwende dieselbe Paket-ID
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
        // Erste Abfrage: Ein Paket löschen und die Karteninformationen abrufen
        String sql = """
        DELETE FROM packages
        WHERE pid = (SELECT MIN(pid) FROM packages)
        RETURNING pid, cid
    """;

        // Zweite Abfrage: Details zu den Karten aus der Tabelle `card` laden
        String cardDetailsSql = """
        SELECT cid, name, damage, element_type, monster_type
        FROM card
        WHERE cid = ?
    """;

        Map<Integer, List<Card>> packageMap = new HashMap<>();

        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            // Karteninformationen sammeln
            while (rs.next()) {
                int packageId = rs.getInt("pid");
                String cardId = rs.getString("cid");

                // Karte aus der Tabelle `card` abrufen
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

        if (packageMap.isEmpty()) {
            return null; // Kein Paket verfügbar
        }

        // Das erste (älteste) Paket zurückgeben
        int packageId = packageMap.keySet().iterator().next();
        return new Package(packageId, packageMap.get(packageId));
    }

    public Collection<Package> getAll() {
        ArrayList<Package> result = new ArrayList<>();
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
            SELECT packages.pid, packages.cid, card.name, card.damage, card.element_type, card.monster_type
            FROM packages
            JOIN card ON packages.cid = card.cid
            ORDER BY packages.pid
            """)
        ) {
            ResultSet resultSet = statement.executeQuery();
            int oldPid = -1;
            List<Card> cards = new ArrayList<>();
            Package pckg = null;
            while (resultSet.next()) {
                int packageId = resultSet.getInt("pid");
                if((oldPid != -1) && (packageId != oldPid)){
                    pckg = new Package(oldPid, cards);
                    result.add(pckg);
                    cards.clear();
                }
                oldPid = packageId;
                String id = resultSet.getString("cid");
                String name = resultSet.getString("name");
                double damage = resultSet.getDouble("damage");
                ElementType elementType = ElementType.valueOf(resultSet.getString("element_type"));
                MonsterType monsterType = MonsterType.valueOf(resultSet.getString("monster_type"));
                if(resultSet.getString("monster_type") != null){
                    cards.add(new MonsterCard(id, name, damage, elementType, monsterType));
                } else {
                    cards.add(new SpellCard(id, name, damage, elementType));
                }
            }
            pckg = new Package(oldPid, cards);
            result.add(pckg);
        } catch (SQLException e) {
            logger.severe("Error : " + e.getMessage());
        }
        return result;
    }




}
