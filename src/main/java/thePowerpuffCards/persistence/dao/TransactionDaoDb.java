package thePowerpuffCards.persistence.dao;

import thePowerpuffCards.core.models.cards.Card;
import thePowerpuffCards.core.models.cards.Package;
import thePowerpuffCards.persistence.DbConnection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


public class TransactionDaoDb implements Dao<Package> {

    @Override
    public Optional get(int id) {
        return Optional.empty();
    }

    @Override
    public Optional getText(String text) {
        return Optional.empty();
    }

    @Override
    public Collection getAll() {
        return List.of();
    }

    @Override
    public void save(Package aPackage) {

    }

    @Override
    public void update(Package aPackage, String[] params) {
       String insertSql = "INSERT INTO stack (username, cid) VALUES (?, ?) ";
       String deleteSql = "DELETE FROM package WHERE pid = ?";
    try (PreparedStatement statement = DbConnection.getInstance().prepareStatement(insertSql);
    PreparedStatement deleteStatement = DbConnection.getInstance().prepareStatement(deleteSql)) {
            for( Card card : aPackage.getCards()) {
                statement.setString(1, params[0]);
                statement.setString(2, card.getId());
                statement.addBatch();
            }
            statement.executeBatch();
            deleteStatement.setInt(1, aPackage.getId());
            deleteStatement.executeUpdate();
        } catch (SQLException e) {

        }
    }

    @Override
    public void delete(Package aPackage) {

    }

}