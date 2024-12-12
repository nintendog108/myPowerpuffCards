package thePowerpuffCards.persistence.dao;

import thePowerpuffCards.core.models.cards.ElementType;
import thePowerpuffCards.core.models.cards.monster.MonsterType;
import thePowerpuffCards.core.models.cards.spell.SpellCard;
import thePowerpuffCards.persistence.DbConnection;
import thePowerpuffCards.core.models.cards.Card;
import thePowerpuffCards.core.models.cards.monster.MonsterCard;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CardDaoDb {
    private static final Logger logger = Logger.getLogger(CardDaoDb.class.getName());

    public void saveCard(Card card) {
        String sql = "INSERT INTO card (cid, name, damage, element_type, monster_type) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, card.getId());
            stmt.setString(2, card.getName());
            stmt.setDouble(3, card.getDamage());
            stmt.setString(4, card.getElementType().name());

            if (card instanceof MonsterCard) {
                MonsterType monsterType = MonsterType.getMonsterType(card.getName());
                if (monsterType != null) {
                    stmt.setString(5, monsterType.name());
                } else {
                    stmt.setNull(5, Types.VARCHAR);
                }
            } else {
                stmt.setNull(5, Types.VARCHAR); // Kein MonsterType für SpellCards
            }

            System.out.println("Saving card with ID: " + card.getId());
            stmt.executeUpdate();
            System.out.println("Card saved successfully!");

        } catch (SQLException e) {
            logger.severe("Fehler beim Speichern der Karte: " + e.getMessage());
        }
    }

    public void addCardsToStack(String username, List<Card> cards) {
        String sql = """
        INSERT INTO stack (username, cid)
        VALUES (?, ?)
    """;

        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            for (Card card : cards) {
                stmt.setString(1, username);
                stmt.setString(2, card.getId());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            logger.severe("Error adding cards to stack for user: " + e.getMessage());
        }
    }

    /*
    public List<Card> acquireCards() {
        List<Card> cards = new ArrayList<>();
        String sql = "SELECT * FROM card LIMIT 5";

        try (Statement stmt = DbConnection.getInstance().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = rs.getString("cid");
                String name = rs.getString("name");
                double damage = rs.getDouble("damage");
                ElementType elementType = ElementType.valueOf(rs.getString("element_type"));
                String monsterTypeStr = rs.getString("monster_type");

                Card card;
                if (monsterTypeStr != null) {
                    MonsterType monsterType = MonsterType.valueOf(monsterTypeStr);
                    card = new MonsterCard(id, name, damage, elementType, monsterType);
                } else {
                    card = new SpellCard(id, name, damage, elementType);
                }
                cards.add(card);
            }
        } catch (SQLException e) {
            logger.severe("Fehler beim Abrufen der Karten: " + e.getMessage());
        }
        return cards;
    }
}
*/
}
