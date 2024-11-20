package org.example;

import thePowerpuffCards.api.controller.SessionController;
import thePowerpuffCards.api.controller.UserController;
import thePowerpuffCards.persistence.DbConnection;
import thePowerpuffCards.persistence.dao.UsersDaoDb;
import thePowerpuffCards.server.HttpServer;

public class Main {
    public static void main(String[] args) {
        DbConnection.initDb();
        UsersDaoDb usersDaoDb = new UsersDaoDb();

        UserController userController = new UserController(usersDaoDb);
        SessionController sessionController = new SessionController(usersDaoDb);

        HttpServer server = new HttpServer(userController, sessionController);
        server.start(10001);
    }
}
