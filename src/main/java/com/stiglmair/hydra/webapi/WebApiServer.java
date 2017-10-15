package com.stiglmair.hydra.webapi;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * This class implements the web API of the bot.
 * It uses http query strings to pass commands to the bot.
 */
public class WebApiServer {

    private final HttpServer httpServer;

    /**
     * Creates a new WebApiServer which receives commands for the bot via http.
     *
     * @param port The port of the web server.
     * @throws IOException
     */
    public WebApiServer(int port) throws IOException {
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
        this.httpServer.setExecutor(Executors.newSingleThreadExecutor());
        this.httpServer.start();
    }
}

