package thePowerpuffCards.persistence.dao;

import thePowerpuffCards.core.models.cards.monster.MonsterType;
import thePowerpuffCards.persistence.DbConnection;
import thePowerpuffCards.core.models.cards.Card;
import thePowerpuffCards.core.models.cards.monster.MonsterCard;

import java.sql.*;
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


}
