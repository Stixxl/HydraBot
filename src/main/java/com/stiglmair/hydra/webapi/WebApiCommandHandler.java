package com.stiglmair.hydra.webapi;

import com.stiglmair.hydra.events.CommandExecutionEvent;
import com.stiglmair.hydra.main.Main;
import com.stiglmair.hydra.objects.Command;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * This class is used for handling requests to the command web api of the bot.
 */
public class WebApiCommandHandler implements HttpHandler {

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
        if (requestMethod.equalsIgnoreCase("GET")) {
            MultiMap<String> requestParams = new MultiMap<>();
            UrlEncoded.decodeTo(exchange.getRequestURI().getQuery(), requestParams, "UTF-8");

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

            IUser user = null;
            try {
                user = Main.client.getUserByID(Long.parseLong(userIdStrings.get(0)));
            } catch (NumberFormatException ex) {
                sendBadRequestResponse(exchange, "Value of key userId ist not a long.");
            }
            if (user == null) {
                sendBadRequestResponse(exchange, "User with userId is not known to the bot.");
            }

            IMessage webMessage = new WebApiMessage(user);
            Command cmd = Command.parseCommand(commandStrings.get(0));
            Event cmdExecutionEvent =
                new CommandExecutionEvent(cmd, webMessage, user);
            Main.client.getDispatcher().dispatch(cmdExecutionEvent);
            exchange.sendResponseHeaders(202, 0);
        }
    }
}