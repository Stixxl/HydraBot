package com.stiglmair.hydra.webapi;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetAddress;
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
     * @param listenAddress The address on which the WebApiServer listens.
     * @param port The port on which the web server will listen.
     * @throws IOException
     */
    public WebApiServer(InetAddress listenAddress, int port) throws IOException {
        InetSocketAddress socketAddress = new InetSocketAddress(listenAddress, port);
        // Use the default parameters for the backlog by choosing 0.
        this.httpServer = HttpServer.create(socketAddress, 0);
    }

    /**
     * Creates a new WebApiServer which receives commands for the bot via http.
     *
     * @param listenAddress The address on which the WebApiServer listens.
     * @param port The port on which the web server will listen.
     * @throws IOException
     */
    public WebApiServer(String listenAddress, int port) throws IOException {
        this(InetAddress.getByName(listenAddress), port);
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

