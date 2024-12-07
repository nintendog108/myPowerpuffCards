package thePowerpuffCards.server;


import thePowerpuffCards.api.controller.Controller;
import thePowerpuffCards.api.controller.PackageController;
import thePowerpuffCards.api.controller.SessionController;
import thePowerpuffCards.api.controller.UserController;
import thePowerpuffCards.persistence.dao.CardDaoDb;
import thePowerpuffCards.persistence.dao.PackageDaoDb;
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
        // Controller section
        UserController userController = new UserController(usersDao);
        SessionController sessionController = new SessionController(usersDao);
        PackageController packageController = new PackageController(cardDao, packageDao);
        // routes
        routes.put("/users", userController);
        routes.put("/sessions", sessionController);
        routes.put("/packages", packageController);
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
