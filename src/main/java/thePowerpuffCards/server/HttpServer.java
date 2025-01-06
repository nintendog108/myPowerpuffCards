package thePowerpuffCards.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* 1. Ein request kommt hier an und
*
* */

public class HttpServer {
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private final Router router;

    public HttpServer() {
        this.router = new Router(); // 2
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started successfully on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
         //       System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                threadPool.submit(new ClientHandler(clientSocket, router));
            }
        } catch (IOException e) {
            System.err.println("Error starting server on port " + port);
            e.printStackTrace();
        }
    }
}
