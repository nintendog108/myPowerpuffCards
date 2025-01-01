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


    public PackageController(PackageDaoDb packageDao, UsersDaoDb usersDao, AuthService authService, TransactionDaoDb transDao) {
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
                    acquirePackageController(headers, out);
                } else {
                    sendNotFound(out, "Something went wrong");
                }
                break;
            default:
                sendMethodNotAllowed(out);
                break;
        }
    }

   public void acquirePackageController(Map<String, String> headers, BufferedWriter out) throws IOException {
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
            //   System.out.println("User Coins: " + userEntity.getCoins());
               sendBadRequest(out, "Not enough money.");
               return;
           }

           // Paket erwerben
           Package randomPackage = packageDao.acquirePackage();
           if (randomPackage == null) { // Falls keine Pakete mehr verfügbar sind
               sendNotFound(out, "No packages available.");
               return;
           }

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
           sendBadRequest(out, "Error acquiring package: " + e.getMessage()); //TODO: hier wird noch 500 Internal error angezeigt statt 4xx
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

}
