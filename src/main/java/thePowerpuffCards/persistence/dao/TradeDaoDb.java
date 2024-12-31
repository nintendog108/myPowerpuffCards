package thePowerpuffCards.persistence.dao;

import thePowerpuffCards.core.models.cards.Card;
import thePowerpuffCards.core.services.TradeService;
import thePowerpuffCards.persistence.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TradeDaoDb {
    private final CardDaoDb cardDao = new CardDaoDb();


    public void createTrade(TradeService trade) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM trades WHERE trade_id = ?";
        try (PreparedStatement checkStmt = DbConnection.getInstance().prepareStatement(checkSql)) {
            checkStmt.setString(1, trade.getTradeId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new IllegalArgumentException("Trade with ID " + trade.getTradeId() + " already exists.");
                }
            }
        }
        System.out.println("*************   createTrade called for trade ID: " + trade.getTradeId());
        String sql = "INSERT INTO trades (trade_id, offered_card_id, required_type, min_damage, offered_by) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, trade.getTradeId());
            stmt.setString(2, trade.getOfferedCard().getId());
            stmt.setString(3, trade.getRequiredCardType());
            stmt.setInt(4, trade.getMinDamage());
            stmt.setString(5, trade.getOfferedByUser());
            int rowsInserted = stmt.executeUpdate();
            System.out.println("*************   Rows inserted: " + rowsInserted);
        } catch (SQLException e) {
            System.err.println("Error creating trade: " + e.getMessage());
            throw e;
        }
    }
    public List<TradeService> getAllTrades() {
        String sql = "SELECT trade_id, offered_card_id, required_type, min_damage, offered_by FROM trades";
        List<TradeService> trades = new ArrayList<>();
        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                CardDaoDb cardDao = new CardDaoDb();
                Card offeredCard = cardDao.getCardById(rs.getString("offered_card_id"));
                trades.add(new TradeService(
                        rs.getString("trade_id"),
                        offeredCard,
                        rs.getString("required_type"),
                        rs.getInt("min_damage"),
                        rs.getString("offered_by")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching trades: " + e.getMessage());
        }
        return trades;
    }

    public void updateTradeStatus(String tradeId, String status) throws SQLException {
        String sql = "UPDATE trades SET status = ? WHERE trade_id = ?";
        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, tradeId);
            int rowsUpdated = stmt.executeUpdate();
            System.out.println("*************   Rows updated: " + rowsUpdated);
        } catch (SQLException e) {
            System.err.println("Error updating trade status: " + e.getMessage());
            throw e;
        }
    }

    public boolean deleteTrade(String tradeId, String username) throws SQLException {
        String sql = "DELETE FROM trades WHERE trade_id = ? AND offered_by = ?";
        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, tradeId);
            stmt.setString(2, username);
            int rowsDeleted = stmt.executeUpdate();
            System.out.println("*************   Rows deleted: " + rowsDeleted);
            System.out.println("SQL Query: " + sql);
            System.out.println("Parameters: tradeId=" + tradeId + ", username=" + username);

            return rowsDeleted > 0;
        }
    }

    public void acceptTrade(String tradeId, String buyerUsername, String buyerCardId) throws SQLException {
        System.out.println("Processing acceptTrade: tradeId=" + tradeId + ", buyerUsername=" + buyerUsername + ", buyerCardId=" + buyerCardId);
        String sql = "SELECT offered_by, offered_card_id, required_type, min_damage FROM trades WHERE trade_id = ?";
        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, tradeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {

                String sellerUsername = rs.getString("offered_by");
                String offeredCardId = rs.getString("offered_card_id");
                String requiredType = rs.getString("required_type");
                int minDamage = rs.getInt("min_damage");
                System.out.println("Trade details: seller=" + sellerUsername + ", offeredCardId=" + offeredCardId + ", requiredType=" + requiredType + ", minDamage=" + minDamage);
                if (buyerUsername.equals(sellerUsername)) {
                    throw new SQLException("Cannot trade with yourself.");
                }

                Card buyerCard = cardDao.getCardById(buyerCardId); // Use CardDaoDb here
                System.out.println("Buyer card details: " + buyerCard);
                if (!TradeService.validateCardForTrade(buyerCard, requiredType, minDamage)) {
                    throw new SQLException("Card does not meet trade requirements.");
                }

                // Perform the card transfer
                cardDao.transferCard(buyerCardId, buyerUsername, sellerUsername);
                cardDao.transferCard(offeredCardId, sellerUsername, buyerUsername);

                // Remove the trade
                updateTradeStatus(tradeId, "completed");

            } else {
                throw new SQLException("Trade not found.");
            }
        } catch (SQLException e) {
        System.err.println("Error processing trade: " + e.getMessage());
        throw e;
    }
    }

}
