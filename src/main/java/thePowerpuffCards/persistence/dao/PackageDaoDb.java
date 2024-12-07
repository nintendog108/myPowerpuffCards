package thePowerpuffCards.persistence.dao;

import thePowerpuffCards.core.models.cards.Card;
import thePowerpuffCards.persistence.DbConnection;
import thePowerpuffCards.core.models.cards.Package;

import java.sql.*;
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




/*
    //  mit 1 karte pid erstellen, zurückbekommen und dann
    public long savePackage(Package pckg) {
        String firstCard = "INSERT INTO packages (cid) values (?)";
        String allCards = "INSERT INTO packages (pid, cid) values (?, ?)";
        try (PreparedStatement stmt = DbConnection.getInstance().prepareStatement(firstCard);
             PreparedStatement stmt2 = DbConnection.getInstance().prepareStatement(allCards)) {
            stmt.setString(1, pckg.getCards().getFirst().getId());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                pckg.setId(rs.getInt(1));
            } else {
                throw new SQLException("Failed to save package, no ID generated");
            }
            for (int i = 1; i < pckg.getCards().size(); i++) {
                stmt2.setInt(1, pckg.getId());
                stmt2.setString(2, pckg.getCards().get(i).getId());
                stmt2.addBatch();
            }
            stmt2.executeBatch();

        } catch (SQLException e) {
            logger.severe("Error saving package: " + e.getMessage());
        }
        return -1;
    } */

}
