package thePowerpuffCards.server;

import thePowerpuffCards.api.controller.Controller;
import thePowerpuffCards.api.controller.SessionController;
import thePowerpuffCards.api.controller.UserController;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Router router;

    public ClientHandler(Socket clientSocket, Router router) {
        this.clientSocket = clientSocket;
        this.router = router;
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

                Map<String, String> headers = new HashMap<>();
                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    String[] headerParts = line.split(": ");
                    headers.put(headerParts[0], headerParts[1]);
                }


                StringBuilder body = new StringBuilder();
                while (in.ready()) {
                    body.append((char) in.read());
                }
                Controller controller;
                if ((controller = router.getController(path)) != null) {
                    controller.handleRequest(method, path, headers, body.toString(), out);
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

