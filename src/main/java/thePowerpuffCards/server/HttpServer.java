package thePowerpuffCards.server;

import thePowerpuffCards.api.controller.SessionController;
import thePowerpuffCards.api.controller.UserController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private final UserController userController;
    private final SessionController sessionController;

    public HttpServer(UserController userController, SessionController sessionController) {
        this.userController = userController;
        this.sessionController = sessionController;
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(new ClientHandler(clientSocket, userController, sessionController));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
