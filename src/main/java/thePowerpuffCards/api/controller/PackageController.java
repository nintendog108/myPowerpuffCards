package thePowerpuffCards.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import thePowerpuffCards.persistence.dao.CardDaoDb;
import thePowerpuffCards.persistence.dao.PackageDaoDb;
import thePowerpuffCards.persistence.dao.UsersDaoDb;
import thePowerpuffCards.services.models.User;
import thePowerpuffCards.services.models.cards.Card;
import thePowerpuffCards.services.models.cards.Package;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class PackageController {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final CardDaoDb cardDao;
    private final PackageDaoDb packageDao;

    public PackageController(CardDaoDb cardDao, PackageDaoDb packageDao) {
        this.cardDao = cardDao;
        this.packageDao = packageDao;
    }

    public void handleRequest(String method, String path, String body, BufferedWriter out) throws IOException {
        switch (method) {
            case "POST":
                if (path.equals("/packages")) {
                    createPackage(body, out);
                } else {
                    sendNotFound(out);
                }
                break;
            default:
                sendMethodNotAllowed(out);
                break;
        }
    }

    private void createPackage(String body, BufferedWriter out) throws IOException {
        try {
            Package newPackage = objectMapper.readValue(body, Package.class);

            // Hier greifst du jetzt auf die Karten im Paket zu
            if (newPackage.getCards().size() != 5) {
                sendBadRequest(out, "A package must contain exactly 5 cards.");
                return;
            }

            long packageId = packageDao.savePackage(newPackage);
            if (packageId > 0) {
                out.write("HTTP/1.1 201 Created\r\n");
                out.write("Content-Type: text/plain\r\n");
                out.write("\r\n");
                out.write("Package created with ID: " + packageId);
            } else {
                sendInternalError(out, "Failed to create package.");
            }
        } catch (Exception e) {
            sendInternalError(out, "Error creating package: " + e.getMessage());
        }
        out.flush();
    }


    private void sendBadRequest(BufferedWriter out, String message) throws IOException {
        out.write("HTTP/1.1 400 Bad Request\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write(message);
    }

    private void sendInternalError(BufferedWriter out, String message) throws IOException {
        out.write("HTTP/1.1 500 Internal Server Error\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write(message);
    }

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
