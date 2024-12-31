package thePowerpuffCards.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import thePowerpuffCards.persistence.dao.CardDaoDb;
import thePowerpuffCards.persistence.dao.TradeDaoDb;
import thePowerpuffCards.core.services.TradeService;

import java.io.BufferedWriter;
import java.io.IOException;

import java.util.List;
import java.util.Map;

public class TradeController extends Controller {
    private final TradeDaoDb tradeDao;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public TradeController(TradeDaoDb tradeDao) {
        this.tradeDao = tradeDao;
    }

    @Override
    public void handleRequest(String method, String path, Map<String, String> headers, String body, BufferedWriter out) throws IOException {
        System.out.println("Handling request: method=" + method + ", path=" + path);

        if ("POST".equalsIgnoreCase(method) && path.equals("/tradings")) {
            System.out.println("*************   POST /tradings recognized");
            try {
                createTrade(headers, body, out);
            } catch (IllegalArgumentException e) {
                sendConflict(out, e.getMessage()); // HTTP 409 für Konflikte
            }
        } else if ("POST".equalsIgnoreCase(method) && path.startsWith("/tradings/")) {
            System.out.println("*************   POST /tradings/{id} recognized");
            acceptTrade(headers, path, body, out);
        } else if ("GET".equalsIgnoreCase(method) && path.equals("/tradings")) {
            System.out.println("*************   GET /tradings recognized");
            listTrades(out);
        } else if ("DELETE".equalsIgnoreCase(method) && path.startsWith("/tradings/")) {
            System.out.println("*************   DELETE /tradings/{id} recognized");
            String tradeId = path.split("/")[2];
            deleteTrade(headers, tradeId, out);
        } else {
            System.out.println("*************   No matching route for method=" + method + ", path=" + path);
            sendNotFound(out, "Invalid endpoint.");
        }
    }

    public void deleteTrade(Map<String, String> headers, String tradeId, BufferedWriter out) throws IOException {
        System.out.println("*************   Deleting trade with ID: " + tradeId);
        try {
            // Benutzername aus dem Token extrahieren
            String token = headers.get("Authorization").split(" ")[1]; // "kienboec-mtcgToken"
            String username = token.split("-")[0]; // "kienboec"
            System.out.println("*************   Username from header: " + username);

            boolean isDeleted = tradeDao.deleteTrade(tradeId, username);
            if (isDeleted) {
                sendOk(out, "Trade deleted successfully.");
            } else {
                sendNotFound(out, "Trade not found or you are not authorized to delete it.");
            }
        } catch (Exception e) {
            System.err.println("Error deleting trade: " + e.getMessage());
            sendBadRequest(out, "Error deleting trade: " + e.getMessage());
        }
    }



    private void createTrade(Map<String, String> headers, String body, BufferedWriter out) throws IOException {
        System.out.println("*************   createTrade in controller called");
        String username = getUsernameFromHeaders(headers);
        if (username == null) {
            sendUnauthorized(out, "Invalid token.");
            System.out.println("Unauthorized: No username found in headers.");
            return;
        }

        try {
            TradeService trade = TradeService.fromJson(body, username);
            System.out.println("*************   TradeService created: " + trade.getTradeId());
            tradeDao.createTrade(trade);
            sendOk(out, "Trade created successfully.");
        } catch (Exception e) {
            sendBadRequest(out, "Error creating trade: " + e.getMessage());
        }
    }






    private void acceptTrade(Map<String, String> headers, String path, String body, BufferedWriter out) throws IOException {
        String buyerUsername = getUsernameFromHeaders(headers);
        if (buyerUsername == null) {
            sendUnauthorized(out, "Invalid token.");
            return;
        }

        try {
            String[] pathParts = path.split("/");
            if (pathParts.length < 3) {
                sendBadRequest(out, "Invalid path format.");
                return;
            }
            String tradeId = pathParts[2]; // Extrahiere die Trade-ID

            // Verarbeite die Käuferkarten-ID
            String buyerCardId = body.replace("\"", ""); // Entferne Anführungszeichen
            System.out.println("Trade ID: " + tradeId + ", Buyer Card ID: " + buyerCardId);

            tradeDao.acceptTrade(tradeId, buyerUsername, buyerCardId);
            sendOk(out, "Trade accepted successfully.");
        } catch (Exception e) {
            sendBadRequest(out, "Error accepting trade: " + e.getMessage());
        }
    }



    private void listTrades(BufferedWriter out) throws IOException {
        List<TradeService> trades = TradeService.fetchAllTrades(tradeDao);
        String response = objectMapper.writeValueAsString(trades);
        sendOk(out, response);
    }

    private String getUsernameFromHeaders(Map<String, String> headers) {
        String authorization = headers.get("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null; // Return null if the header is missing or invalid
        }

        // Extract the username (e.g., "kienboec" from "Bearer kienboec-mtcgToken")
        return authorization.substring("Bearer ".length()).split("-")[0];
    }


}
