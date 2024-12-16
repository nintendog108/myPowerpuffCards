package thePowerpuffCards.server;


import thePowerpuffCards.api.controller.*;
import thePowerpuffCards.core.services.AuthService;
import thePowerpuffCards.persistence.dao.CardDaoDb;
import thePowerpuffCards.persistence.dao.PackageDaoDb;
import thePowerpuffCards.persistence.dao.TransactionDaoDb;
import thePowerpuffCards.persistence.dao.UsersDaoDb;

import java.util.HashMap;
import java.util.Map;

public class Router {
    Map<String, Controller> routes = new HashMap<>();
    public Router(){

        // create babies of daos
        UsersDaoDb usersDao = new UsersDaoDb();
        CardDaoDb cardDao = new CardDaoDb();
        PackageDaoDb packageDao = new PackageDaoDb();
        TransactionDaoDb transDao = new TransactionDaoDb();
        //services
        AuthService authService = new AuthService(usersDao);
        // Controller section
        UserController userController = new UserController(usersDao);
        SessionController sessionController = new SessionController(usersDao);
        PackageController packageController = new PackageController(cardDao, packageDao, usersDao, authService, transDao);
        CardController cardController = new CardController(usersDao);
        // routes
        routes.put("/users", userController);
        routes.put("/sessions", sessionController);
        routes.put("/packages", packageController);
        routes.put("/transactions", packageController);
        routes.put("/cards", cardController);
        routes.put("/deck", cardController);
    }
    public Controller getController(String route) {
        for (Map.Entry<String, Controller> entry : routes.entrySet()) {
            if (route.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }


}
