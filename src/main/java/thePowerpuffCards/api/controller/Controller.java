package thePowerpuffCards.api.controller;

import java.io.BufferedWriter;
import java.io.IOException;

public abstract class Controller {
    public abstract void handleRequest(String method, String path, String body, BufferedWriter out) throws IOException;
}
