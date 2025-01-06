package thePowerpuffCards;

import thePowerpuffCards.persistence.DbConnection;
import thePowerpuffCards.server.HttpServer;

public class Main {
    public static void main(String[] args) {
        DbConnection.initDb();
        System.out.println("Hello World!");
        HttpServer server = new HttpServer();  // 1
        server.start(10001);
    }
}
