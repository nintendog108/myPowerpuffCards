package org.example;

import thePowerpuffCards.api.controller.SessionController;
import thePowerpuffCards.api.controller.UserController;
import thePowerpuffCards.database.Database;
import thePowerpuffCards.server.HttpServer;

import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) throws UnknownHostException {
        Database database = new Database();

        UserController userController = new UserController(database);
        SessionController sessionController = new SessionController(database);

        HttpServer server = new HttpServer(userController, sessionController);
        server.start(10001);

    }
}


