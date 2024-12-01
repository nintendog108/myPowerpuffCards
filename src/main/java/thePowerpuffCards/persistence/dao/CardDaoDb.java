package thePowerpuffCards.persistence.dao;

import thePowerpuffCards.persistence.DbConnection;
import thePowerpuffCards.services.models.cards.Card;
import thePowerpuffCards.services.models.cards.monster.MonsterCard;

import java.sql.*;
import java.util.logging.Logger;

public class CardDaoDb {
    private static final Logger logger = Logger.getLogger(CardDaoDb.class.getName());

    // Karte speichern
    public void saveCard(Card card) {
        String sql = "INSERT INTO card (name, damage, element_type, monster_type, package_id) VALUES (?, ?, ?, ?, NULL)";
        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY)) {
            stmt.setString(1, card.getName());
            stmt.setInt(2, card.getDamage());
            stmt.setString(3, card.getElementType().name());

            if (card instanceof MonsterCard) {
                stmt.setString(4, ((MonsterCard) card).getMonsterType().name());
            } else {
                stmt.setNull(4, java.sql.Types.VARCHAR); // Kein MonsterType für SpellCards
            }

            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                card.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            logger.severe("Fehler beim Speichern der Karte: " + e.getMessage());
        }
    }
}
