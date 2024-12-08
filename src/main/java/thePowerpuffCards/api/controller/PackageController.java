package thePowerpuffCards.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import thePowerpuffCards.core.models.User;
import thePowerpuffCards.core.models.cards.Card;
import thePowerpuffCards.core.models.cards.ElementType;
import thePowerpuffCards.core.models.cards.monster.MonsterCard;
import thePowerpuffCards.core.models.cards.monster.MonsterType;
import thePowerpuffCards.core.models.cards.spell.SpellCard;
import thePowerpuffCards.core.services.AuthService;
import thePowerpuffCards.persistence.dao.CardDaoDb;
import thePowerpuffCards.persistence.dao.PackageDaoDb;
import thePowerpuffCards.core.models.cards.Package;
import thePowerpuffCards.persistence.dao.UsersDaoDb;
import thePowerpuffCards.persistence.dao.TransactionDaoDb;
import java.io.BufferedWriter;
import java.io.IOException;

import java.util.*;

public class PackageController extends Controller {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final PackageDaoDb packageDao;
    private final UsersDaoDb usersDao;
    private final AuthService authService;
    private final TransactionDaoDb transDao;


    public PackageController(CardDaoDb cardDao, PackageDaoDb packageDao, UsersDaoDb usersDao, AuthService authService, TransactionDaoDb transDao) {
        this.packageDao = packageDao;
        this.usersDao = usersDao;
        this.authService = authService;
        this.transDao = transDao;
    }

    public void handleRequest(String method, String path, Map<String, String> headers, String body, BufferedWriter out) throws IOException {
        switch (method) {
            case "POST":
                if (path.equals("/packages")) {
                    createPackage(body, out);
                }else if (path.equals("/transactions/packages")){
                    acquirePackage(headers, out);
                } else {
                    sendNotFound(out);
                }
                break;
            default:
                sendMethodNotAllowed(out);
                break;
        }
    }
   /* public void acquirePackage(Map<String, String> headers, BufferedWriter out) throws IOException {
        try {
            if (!authService.authenticate(headers)){
                sendMethodNotAllowed(out);
            }
            String username = headers.get("Authorization").split(" ")[1].split("-")[0];
            Optional<User> optionalUser = usersDao.get(username);

            if (optionalUser.isEmpty()) {
                sendBadRequest(out, "User not found.");
                return;
            }
            User user = optionalUser.get();
            // Überprüfen, ob der Benutzer genug Coins hat
            if (user.getCoins() < 5) {
                sendBadRequest(out, "Not enough money.");
                return;
            }

            // Paket erwerben
            Collection<Package> packages = packageDao.getAll();
            if (packages.isEmpty()) {
                sendInternalError(out, "No packages available.");
                return;
            }
            Package randomPackage = packages.iterator().next();
            //transaction durchführen
            transDao.update(randomPackage, new String[]{username});
            // Benutzer aktualisieren und Erfolgsmeldung senden
            user.setCoins(user.getCoins() - 5);

            String[] params = {
                    user.getUsername(),
                    user.getPassword(),
                    user.getToken()
            };
            usersDao.update(user, params);

            out.write("Content-Type: application/json\r\n");
            out.write("\r\n");
            out.write("{\"message\":\"Package acquired successfully\", \"packageId\": " + randomPackage.getId() + "}");
        } catch (IllegalArgumentException e) {
            sendBadRequest(out, e.getMessage());
        } catch (Exception e) {
            sendInternalError(out, "Error acquiring package: " + e.getMessage());
        }
        out.flush();
    }
*/
   public void acquirePackage(Map<String, String> headers, BufferedWriter out) throws IOException {
       try {
           // Überprüfe Authentifizierung
           if (!authService.authenticate(headers)) {
               sendUnauthorized(out, "Unauthorized request.");
               return;
           }

           // Extrahiere Benutzername aus dem Header
           String authorization = headers.get("Authorization");
           if (authorization == null || !authorization.startsWith("Bearer ")) {
               sendBadRequest(out, "Invalid Authorization header.");
               return;
           }
           String username = authorization.substring("Bearer ".length()).split("-")[0];

           Optional<User> user = usersDao.getText(username);

           if (user.isEmpty()) {
               sendBadRequest(out, "User not found.");
               return;
           }

           // Überprüfen, ob der Benutzer genug Coins hat
           User userEntity = user.get();
           if (userEntity.getCoins() < 5) {
               sendBadRequest(out, "Not enough money.");
               return;
           }

           // Paket erwerben
           Collection<Package> packages = packageDao.getAll();
           if (packages.isEmpty()) {
               sendInternalError(out, "No packages available.");
               return;
           }

           Package randomPackage = packages.iterator().next();

           // Update User und Package
           userEntity.setCoins(userEntity.getCoins() - 5);
           usersDao.update(userEntity, new String[]{
                   userEntity.getUsername(),
                   userEntity.getPassword(),
                   userEntity.getToken()
           });
           transDao.update(randomPackage, new String[]{username});

           // Erfolgsmeldung senden
           out.write("HTTP/1.1 201 Created\r\n");
           out.write("Content-Type: application/json\r\n");
           out.write("\r\n");
           out.write("{\"message\":\"Package acquired successfully\", \"packageId\": " + randomPackage.getId() + "}");
       } catch (IllegalArgumentException e) {
           sendBadRequest(out, e.getMessage());
       } catch (Exception e) {
           sendInternalError(out, "Error acquiring package: " + e.getMessage());
       }
       out.flush();
   }


    private void createPackage(String body, BufferedWriter out) throws IOException {
        try {
            List<Map<String, Object>> packages = objectMapper.readValue(body, new TypeReference<>() {});
            List<Card> cards = new ArrayList<>();
            CardDaoDb cardDao = new CardDaoDb(); // CardDaoDb-Instanz erstellen

            for (Map<String, Object> packageMap : packages) {
                String id = (String) packageMap.get("Id");
                String name = (String) packageMap.get("Name");
                if (name == null) {
                    name = (String) packageMap.get("name");
                }
                if (name == null || name.isEmpty()) {
                    throw new IllegalArgumentException("Card name is null or empty");
                }
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
    private void sendUnauthorized(BufferedWriter out, String message) throws IOException {
        out.write("HTTP/1.1 401 Unauthorized\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write(message);
        out.flush();
    }

}
