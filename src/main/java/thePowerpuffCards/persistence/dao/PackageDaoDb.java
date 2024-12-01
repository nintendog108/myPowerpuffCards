package thePowerpuffCards.persistence.dao;

import thePowerpuffCards.persistence.DbConnection;
import thePowerpuffCards.services.models.cards.Card;
import thePowerpuffCards.services.models.cards.Package;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;
public class PackageDaoDb {

    private static final Logger logger = Logger.getLogger(PackageDaoDb.class.getName());

    public long savePackage(Package pckg) {
        String savePackageSQL = "INSERT INTO packages DEFAULT VALUES RETURNING pid";
        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(savePackageSQL, ResultSet.TYPE_FORWARD_ONLY)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long packageId = rs.getLong(1);
                saveCardsForPackage(pckg.getCards(), packageId);
                return packageId;
            }
        } catch (SQLException e) {
            logger.severe("Error saving package: " + e.getMessage());
        }
        return -1;
    }

    private void saveCardsForPackage(List<Card> cards, long packageId) {
        String saveCardSQL = "UPDATE card SET package_id = ? WHERE cid = ?";
        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(saveCardSQL, ResultSet.TYPE_FORWARD_ONLY)) {
            for (Card card : cards) {
                stmt.setLong(1, packageId);
                stmt.setLong(2, card.getId());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            logger.severe("Error linking cards to package: " + e.getMessage());
        }
    }
}
