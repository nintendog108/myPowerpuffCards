package thePowerpuffCards.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import thePowerpuffCards.persistence.dao.CardDaoDb;
import thePowerpuffCards.persistence.dao.PackageDaoDb;
import thePowerpuffCards.persistence.dao.UsersDaoDb;
import thePowerpuffCards.services.models.User;
import thePowerpuffCards.services.models.cards.Card;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class PackageController {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final CardDaoDb cardDao;
    private final PackageDaoDb packageDao;

    public PackageController(UsersDaoDb usersDao, CardDaoDb cardDao, PackageDaoDb packageDao) {
        this.cardDao = cardDao;
        this.packageDao = packageDao;
    } //TODO:: hier übergeben

    public void handleRequest(String method, String path, String body, BufferedWriter out) throws IOException {
        switch (method) {
            case "POST":
                if (path.equals("/packages")) {
                   // createPackage(body, out);
                } else {
                    sendNotFound(out);
                }
                break;
            default:
                sendMethodNotAllowed(out);
                break;
        }

    }
 /*   private void createPackage(String body, BufferedWriter out) throws IOException {
        // Deserialize the package from the request body
        Package newPackage = objectMapper.readValue(body, Package.class);
        List<Card> cards = newPackage.getCards();

        if (cards.size() != 5) {
            out.write("HTTP/1.1 400 Bad Request\r\n");
            out.write("Content-Type: text/plain\r\n");
            out.write("\r\n");
            out.write("A package must contain exactly 5 cards.");
            out.flush();
            return;
        }

        // Save each card and the package to the database
        long packageId = packageDao.save(newPackage);
        for (Card card : cards) {
            card.setPackageId(packageId);
            cardDao.save(card);
        }

        out.write("HTTP/1.1 201 Created\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write("Package and cards successfully created.");
        out.flush();
    } */

    private void sendNotFound(BufferedWriter out) throws IOException {
        out.write("HTTP/1.1 404 Not Found\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write("404 - Not Found");
        out.flush();
    }

    private void sendMethodNotAllowed(BufferedWriter out) throws IOException {
        out.write("HTTP/1.1 405 Method Not Allowed\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write("405 - Method Not Allowed");
        out.flush();
    }
}