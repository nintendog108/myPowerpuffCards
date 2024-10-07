package thePowerpuffCards.server;

import thePowerpuffCards.api.controller.SessionController;
import thePowerpuffCards.api.controller.UserController;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final UserController userController;
    private final SessionController sessionController;

    public ClientHandler(Socket clientSocket, UserController userController, SessionController sessionController) {
        this.clientSocket = clientSocket;
        this.userController = userController;
        this.sessionController = sessionController;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

            String line = in.readLine();
            if (line != null) {
                String[] requestParts = line.split(" ");
                String method = requestParts[0];
                String path = requestParts[1];


                while ((line = in.readLine()) != null && !line.isEmpty()) {

                    System.out.println("Header: " + line);
                }


                StringBuilder body = new StringBuilder();
                while (in.ready()) {
                    body.append((char) in.read());
                }

                if (path.startsWith("/users")) {
                    userController.handleRequest(method, path, body.toString(), out);
                } else if (path.startsWith("/sessions")) {
                    sessionController.handleRequest(method, path, body.toString(), out);
                } else {
                    sendNotFound(out);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendNotFound(BufferedWriter out) throws IOException {
        out.write("HTTP/1.1 404 Not Found\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("\r\n");
        out.write("404 - Not Found");
        out.flush();
    }
}

