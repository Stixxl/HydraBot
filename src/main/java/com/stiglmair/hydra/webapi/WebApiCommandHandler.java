package com.stiglmair.hydra.webapi;

import com.stiglmair.hydra.events.CommandExecutionEvent;
import com.stiglmair.hydra.main.Main;
import com.stiglmair.hydra.objects.Command;
import com.stiglmair.hydra.objects.User;

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
     * Sends a http response.
     *
     * @param exchange The exchange to which the response belongs.
     * @param message The message encoded in UTF-8.
     * @param status the HTTP status code
     * @throws IOException
     */
    private void sendResponse(HttpExchange exchange, String message, int status) throws IOException {
        byte[] messageBytes = message.getBytes("UTF-8");
        exchange.sendResponseHeaders(status, messageBytes.length);
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
     * Otherwise the server responds with a 204 status code.
     * @param exchange The http exchange.
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) {
        String requestMethod = exchange.getRequestMethod();
        if (requestMethod.equalsIgnoreCase("GET")) {
            MultiMap<String> requestParams = new MultiMap<>();
            UrlEncoded.decodeTo(exchange.getRequestURI().getQuery(), requestParams, "UTF-8");

            // TODO: Exceptions in the method are not logged anywhere.
            try {
                List<String> commandStrings = requestParams.get("command");

            if (commandStrings == null || commandStrings.size() != 1) {
                String msg = "Wrong number of values for key command. Expected exactly one value.";
                sendResponse(exchange, msg, 400);
                return;
            }
            List<String> userIdStrings = requestParams.get("userId");
            if (userIdStrings == null || userIdStrings.size() != 1) {
                String msg = "Wrong number of values for key userId. Expected exactly one value.";
                sendResponse(exchange, msg, 400);
                return;
            }
            List<String> tokenStrings = requestParams.get("token");
            if (tokenStrings == null || tokenStrings.size() != 1) {
                String msg = "wrong number of values for key token. Expected exactly one value.";
                sendResponse(exchange, msg, 400);
                return;
            }

            IUser user;
            try {
                user = Main.client.getUserByID(Long.parseLong(userIdStrings.get(0)));
            } catch (NumberFormatException ex) {
                sendResponse(exchange, "Value of key userId ist not a long.", 400);
                return;
            }
            if (user == null) {
                String msg = "User with userId " + userIdStrings.get(0) + " is not known to the bot.";
                sendResponse(exchange, msg, 400);
                return;
            }

            User dbUser;
            try {
                dbUser = Main.userService.getUser(String.valueOf(user.getLongID()));
            } catch (java.sql.SQLException e) {
                Main.logger.error("error fetching user", e);
                sendResponse(exchange, "Internal server error.", 500);
                return;
            }
            if (dbUser == null) {
                sendResponse(exchange, "Sorry, do I know you?", 400);
                return;
            }
            if (dbUser.getApiToken() == null || !dbUser.getApiToken().equals(tokenStrings.get(0))) {
                sendResponse(exchange, "Bad token.", 403);
                return;
            }

            IMessage webMessage = new WebApiMessage(user);
            Command cmd = Command.parseCommand(commandStrings.get(0));
            Event cmdExecutionEvent =
                new CommandExecutionEvent(cmd, webMessage, user);
            Main.client.getDispatcher().dispatch(cmdExecutionEvent);

            sendResponse(exchange, "Ok.", 204);
        }catch(IOException e) {
                Main.logger.error("Failed to handle the request over rest for url " + exchange.getRequestURI().toString(), e);
                }
        } 
}
}
