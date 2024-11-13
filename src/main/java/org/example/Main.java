package org.example;

import thePowerpuffCards.api.controller.SessionController;
import thePowerpuffCards.api.controller.UserController;
import thePowerpuffCards.database.Database;
import thePowerpuffCards.persistence.DbConnection;
import thePowerpuffCards.persistence.dao.Dao;
import thePowerpuffCards.persistence.dao.UsersDaoDb;
import thePowerpuffCards.server.HttpServer;
import thePowerpuffCards.services.models.User;

import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) throws UnknownHostException {
        Database database = new Database();
        Dao<User> usersDaoDb = new UsersDaoDb();

        UserController userController = new UserController(usersDaoDb);
        SessionController sessionController = new SessionController(database);

        DbConnection.initDb();

        HttpServer server = new HttpServer(userController, sessionController);
        server.start(10001);

    }
}


