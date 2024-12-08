package thePowerpuffCards.api.controller;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public abstract class Controller {
    public abstract void handleRequest(String method, String path, Map<String, String> header, String body, BufferedWriter out) throws IOException;

}
