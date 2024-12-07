package thePowerpuffCards.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import thePowerpuffCards.core.models.cards.Card;
import thePowerpuffCards.core.models.cards.ElementType;
import thePowerpuffCards.core.models.cards.monster.MonsterCard;
import thePowerpuffCards.core.models.cards.monster.MonsterType;
import thePowerpuffCards.core.models.cards.spell.SpellCard;
import thePowerpuffCards.persistence.dao.CardDaoDb;
import thePowerpuffCards.persistence.dao.PackageDaoDb;
import thePowerpuffCards.core.models.cards.Package;

import java.io.BufferedWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PackageController extends Controller {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final PackageDaoDb packageDao;

    public PackageController(CardDaoDb cardDao, PackageDaoDb packageDao) {
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

    /*private void createPackage(String body, BufferedWriter out) throws IOException {
        try {

            List<Map<String, Object>> packages = objectMapper.readValue(body, new TypeReference<>(){});
            List<Card> cards = new ArrayList<>();
            for (Map<String, Object> packageMap : packages) { // Id, Name, Damage
                String Id = (String) packageMap.get("Id");
                String Name = (String) packageMap.get("Name");
                Double Damage  = (Double) packageMap.get("Damage");
                if(Name.contains("Spell")){
                    cards.add(new SpellCard(Id, Name, Damage, ElementType.getType(Name)));
                } else {
                    cards.add(new MonsterCard(Id, Name, Damage, ElementType.getType(Name), MonsterType.getMonsterType(Name)));
                }
            }
            Package newPackage = new Package(cards);
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
*/

    private void createPackage(String body, BufferedWriter out) throws IOException {
        try {
            List<Map<String, Object>> packages = objectMapper.readValue(body, new TypeReference<>() {});
            List<Card> cards = new ArrayList<>();
            CardDaoDb cardDao = new CardDaoDb(); // CardDaoDb-Instanz erstellen

            for (Map<String, Object> packageMap : packages) {
                String id = (String) packageMap.get("Id");
                String name = (String) packageMap.get("Name");
                Double damage = (Double) packageMap.get("Damage");

                Card card;
                if (name.contains("Spell")) {
                    card = new SpellCard(id, name, damage, ElementType.getType(name));
                } else {
                    MonsterType monsterType = MonsterType.getMonsterType(name);
                    card = new MonsterCard(id, name, damage, ElementType.getType(name), monsterType);
                }

                // Speichere die Karte
                cardDao.saveCard(card);

                cards.add(card);
            }

            Package newPackage = new Package(cards);

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
                sendInternalError(out, "Failed to create package in database.");
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
