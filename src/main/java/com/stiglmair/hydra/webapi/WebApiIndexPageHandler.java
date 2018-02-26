package com.stiglmair.hydra.webapi;

import com.stiglmair.hydra.main.Main;
import com.stiglmair.hydra.utilities.UtilityMethods;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WebApiIndexPageHandler implements HttpHandler {

    private static final String ADD_SERVER_URL = "https://discordapp.com/oauth2/authorize?client_id={{CLIENT_ID}}&scope=bot&permissions=3148800";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = UtilityMethods.generatePath("resources/index.html");
        String contents = new String(Files.readAllBytes(Paths.get(path)));
        String clientId = Main.client.getApplicationClientID();
        String url = ADD_SERVER_URL.replace("{{CLIENT_ID}}", clientId);
        contents = contents.replace("{{ADD_SERVER_URL}}", url);

        byte[] messageBytes = contents.getBytes("UTF-8");
        exchange.getResponseHeaders().add("Content-Type", "text/html;charset=UTF-8");
        exchange.sendResponseHeaders(200, messageBytes.length);
        exchange.getResponseBody().write(messageBytes);
        exchange.getResponseBody().close();
    }

}
