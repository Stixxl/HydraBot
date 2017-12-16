/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stiglmair.hydra.main;

import com.stiglmair.hydra.dbservices.DBService;
import com.stiglmair.hydra.dbservices.GameService;
import com.stiglmair.hydra.dbservices.SoundService;
import com.stiglmair.hydra.dbservices.UserService;
import com.stiglmair.hydra.listener.AudioListener;
import com.stiglmair.hydra.listener.CommandExecutionListener;
import com.stiglmair.hydra.listener.CommandListener;
import com.stiglmair.hydra.listener.UserListener;
import com.stiglmair.hydra.utilities.UtilityMethods;
import com.stiglmair.hydra.webapi.WebApiCommandHandler;
import com.stiglmair.hydra.webapi.WebApiServer;

import com.moandjiezana.toml.Toml;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.CommandLine;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Stiglmair
 */
public class Main {

    public static IDiscordClient client;
    private static String Token;
    public static DBService dbService;
    public static UserService userService;
    public static GameService gameService;
    public static SoundService soundService;
    private static FileHandler fh_severe = null;
    private static FileHandler fh_info = null;
    private static FileHandler fh_finer = null;
    private static final int LOGGING_FILE_SIZE = 1024 * 1024;//1MB
    public static UserListener userListener;
    public static AudioListener audioListener;
    private static String key;

    // Possibly overwritten from the command-line options.
    private static String CONFIGFILE = "config.toml";
    private static String LOGFOLDER = "logs/";

    public static CommandLine parseCommandline(String[] argv) {
        Options options = new Options();
        Option option;

        option = new Option(null, "config", true, "The TOML configuration file. Defaults to " + CONFIGFILE);
        options.addOption(option);

        option = new Option(null, "logfolder", true, "The folder where log files are stored. Defaults to " + LOGFOLDER);
        options.addOption(option);

        CommandLineParser parser = new org.apache.commons.cli.DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, argv);
        } catch (org.apache.commons.cli.ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("hydrabot", options);
            System.exit(1);
        }
        return cmd;
    }

    public static void main(String[] argv) throws Exception {
        CommandLine args = parseCommandline(argv);
        LOGFOLDER = UtilityMethods.firstNonNull(args.getOptionValue("logfolder", LOGFOLDER));
        CONFIGFILE = UtilityMethods.firstNonNull(args.getOptionValue("config", CONFIGFILE));

        readConfig();


        // Initialize logging handlers.
        UtilityMethods.ensureEmptyFolder(LOGFOLDER);
        Logger.getGlobal().setLevel(Level.FINER);
        fh_severe = new FileHandler(LOGFOLDER + "severe.log", LOGGING_FILE_SIZE, 1);
        fh_info = new FileHandler(LOGFOLDER + "info.log", LOGGING_FILE_SIZE, 1);
        fh_finer = new FileHandler(LOGFOLDER + "finer.log", LOGGING_FILE_SIZE, 1);
        FileHandler[] fileHandlers = {fh_severe, fh_info, fh_finer};
        SimpleFormatter formatter = new SimpleFormatter();
        for (FileHandler fh : fileHandlers) {
            Logger.getGlobal().addHandler(fh);
            fh.setFormatter(formatter);
        }
        Logger.getGlobal().setUseParentHandlers(false);
        //set respective level for filehandlers
        fh_severe.setLevel(Level.SEVERE);
        fh_info.setLevel(Level.INFO);
        fh_finer.setLevel(Level.FINER);
        //this filehandlers will only receive input for their respective level
        fh_severe.setFilter((LogRecord record) -> record.getLevel().equals(Level.SEVERE));
        fh_info.setFilter((LogRecord record) -> record.getLevel().equals(Level.INFO));
        fh_finer.setFilter((LogRecord record) -> record.getLevel().equals(Level.FINER));

        //DB Services
        userService = dbService.getUserService();
        gameService = dbService.getGameService();
        soundService = dbService.getSoundService();

        // Create the Discord client.
        client = new ClientBuilder().withToken(Token).login();
        //register event listener
        userListener = new UserListener();
        audioListener = new AudioListener();
        client.getDispatcher()
                .registerListener(new CommandExecutionListener());
        client.getDispatcher()
                .registerListener(new CommandListener());
        client.getDispatcher()
                .registerListener(audioListener);
        client.getDispatcher()
                .registerListener(userListener);
        Logger.getGlobal()
                .log(Level.FINER, "Server started.");

        int port = 1337;
        WebApiServer server = new WebApiServer(port);
        server.addHandler("/commands", new WebApiCommandHandler());
        server.start();
        Logger.getGlobal().log(Level.INFO,
            "Started the web server at port " + port + "."
        );
    }

    /**
     * a method for reading the config and mapping its values to suited
     * variables and such
     */
    public static void readConfig() throws IOException {
        //String securePath = "key.properties";
        String path = UtilityMethods.generatePath(CONFIGFILE);
        Toml config = new Toml().read(new File(path));

        // Initialize the database service.
        dbService = new DBService(config.getString("database.user"), config.getString("database.password"));

        // Initialize the Discord Token.
        Token = config.getString("discord.token");
    }

}
