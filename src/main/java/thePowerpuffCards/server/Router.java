package thePowerpuffCards.server;


import thePowerpuffCards.api.controller.*;
import thePowerpuffCards.core.services.AuthService;
import thePowerpuffCards.persistence.dao.*;

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
        TradeDaoDb tradeDao = new TradeDaoDb();
        //services
        AuthService authService = new AuthService(usersDao);
        // Controller section
        UserController userController = new UserController(usersDao);
        SessionController sessionController = new SessionController(usersDao);
        PackageController packageController = new PackageController(packageDao, usersDao, authService, transDao);
        CardController cardController = new CardController(usersDao);
        TradeController tradeController = new TradeController(new TradeDaoDb(), new CardDaoDb());

        // routes
        routes.put("/users", userController);
        routes.put("/sessions", sessionController);
        routes.put("/packages", packageController);
        routes.put("/transactions", packageController);
        routes.put("/cards", cardController);
        routes.put("/deck", cardController);
        routes.put("/stats", userController);
        routes.put("/scoreboard", userController);
        routes.put("/battles", userController);
        routes.put("/tradings", tradeController);
    }
    public Controller getController(String route) {
        for (Map.Entry<String, Controller> entry : routes.entrySet()) {
            if (route.startsWith(entry.getKey())) {
                System.out.println("Routing to controller: " + entry.getKey());
                return entry.getValue();
            }
        }
        System.out.println("No controller found for route: " + route);
        return null;
    }





}
