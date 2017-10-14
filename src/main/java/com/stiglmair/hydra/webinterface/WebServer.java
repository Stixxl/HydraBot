package com.stiglmair.hydra.webinterface;

import com.stiglmair.hydra.events.CommandExecutionEvent;
import com.stiglmair.hydra.main.Main;
import com.stiglmair.hydra.objects.Command;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * This class implements the web API of the bot.
 * It uses http query strings to pass commands to the bot.
 */
public class WebServer {

    private final HttpServer httpServer;

    /**
     * Creates a new WebServer which receives commands for the bot via http.
     *
     * @param port The port of the web server.
     * @throws IOException
     */
    public WebServer(int port) throws IOException {
        InetSocketAddress address = new InetSocketAddress(port);
        // Use the default parameters for the backlog by choosing 0.
        this.httpServer = HttpServer.create(address, 0);
    }

    /**
     * Adds a new Handler to the web server.
     *
     * @param location The location which the handler handles. Example: "/commands".
     * @param handler  The handler.
     */
    public void addHandler(String location, HttpHandler handler) {
        this.httpServer.createContext(location, handler);
    }

    /**
     * Starts the server. Call this method after adding all handlers.
     */
    public void start() {
        this.httpServer.setExecutor(Executors.newCachedThreadPool());
        this.httpServer.start();
    }
}

/**
 * This class is used for handling requests to the command web api of the bot.
 */
class CommandHandler implements HttpHandler {

    /**
     * Sends a http response with the bad request status code.
     * @param exchange The exchange to which the response belongs.
     * @param message The message encoded in UTF-8.
     * @throws IOException
     */
    private void sendBadRequestResponse(HttpExchange exchange, String message) throws IOException {
        byte[] messageBytes = message.getBytes("UTF-8");
        exchange.sendResponseHeaders(400, messageBytes.length);
        exchange.getResponseHeaders().add("Content-Type", "text/plain;charset=UTF-8");
        OutputStream os = exchange.getResponseBody();
        os.write(messageBytes);
        os.close();
    }

    /**
     * Handles requests to the web api of the bot. The web api uses http query parameters.
     * It accepts the keys command and userId.
     * Example: example.com/exampleLocation?command=sounds%20faker&userId=1337.
     * If the request is malformed a 400 response is issued with an according message.
     * Otherwise the server responds with a 202 status code.
     * @param exchange The http exchange.
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        if (requestMethod.equalsIgnoreCase("POST")) {
            URL requestUrl = exchange.getRequestURI().toURL();
            MultiMap<String> requestParams = new MultiMap<>();
            UrlEncoded.decodeTo(requestUrl.getQuery(), requestParams, "UTF-8");

            List<String> commandStrings = requestParams.get("command");
            if (commandStrings.size() != 1) {
                sendBadRequestResponse(exchange,
                    "Wrong number of values for key command. Expected exactly one value.");
                return;
            }
            List<String> userIdStrings = requestParams.get("userId");
            if (userIdStrings.size() != 1) {
                sendBadRequestResponse(exchange,
                    "Wrong number of values for key userId. Expected exactly one value.");
                return;
            }

            try {
                IUser user = Main.client.getUserByID(Long.parseLong(userIdStrings.get(0)));
                IMessage webMessage = new UserWebMessage(user);
                Command cmd = Command.parseCommand(commandStrings.get(0));
                Event cmdExecutionEvent =
                    new CommandExecutionEvent(cmd, webMessage, user);
                Main.client.getDispatcher().dispatch(cmdExecutionEvent);
                exchange.sendResponseHeaders(202, 0);
            } catch (NumberFormatException ex) {
                sendBadRequestResponse(exchange, "Value of key userId ist not a long.");
            }
        }
    }
}
