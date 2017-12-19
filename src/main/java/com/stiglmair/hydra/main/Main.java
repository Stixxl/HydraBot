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
import com.stiglmair.hydra.webapi.WebApiIndexPageHandler;
import com.stiglmair.hydra.webapi.WebApiServer;

import com.moandjiezana.toml.Toml;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.CommandLine;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.Image;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.Map;
import java.sql.SQLException;

/**
 *
 * @author Stiglmair
 */
public class Main {

    public static IDiscordClient client;
    public static Config config = new Config();
    public static DBService dbService;
    public static UserService userService;
    public static GameService gameService;
    public static SoundService soundService;
    public static AudioListener audioListener = new AudioListener();
    public static UserListener userListener = new UserListener();

    private static final int LOGGING_FILE_SIZE = 1024 * 1024;  // 1MB
    private static String key;

    public static CommandLine parseCommandline(String[] argv) {
        Options options = new Options();
        Option option;

        option = new Option(null, "help", false, "Print this text and exit.");
        options.addOption(option);

        option = new Option(null, "config", true, "The TOML configuration file. Defaults to config.toml");
        options.addOption(option);

        option = new Option(null, "logfolder", true, "The folder where log files are stored. Defaults to logs/");
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
        if (cmd.hasOption("help")) {
            formatter.printHelp("hydrabot", options);
            System.exit(0);
        }
        return cmd;
    }

    public static void main(String[] argv) throws Exception {
        CommandLine args = parseCommandline(argv);

        // Read the configuration file.
        String configFile = UtilityMethods.firstNonNull(args.getOptionValue("config"), "config.toml");
        config.read(configFile);

        // Determine the log folder.
        config.logging.folder = UtilityMethods.firstNonNull(args.getOptionValue("logfolder"), config.logging.folder, "logs/");

        initLogging();
        initDbService();
        initSounds();
        initDiscordClient();
        initWebApi();
    }

    public static void initLogging() throws IOException {
        String folder = config.logging.folder;
        UtilityMethods.ensureEmptyFolder(folder);
        Logger.getGlobal().setLevel(Level.FINER);
        FileHandler fh_severe = new FileHandler(folder + "severe.log", LOGGING_FILE_SIZE, 1);
        FileHandler fh_info = new FileHandler(folder + "info.log", LOGGING_FILE_SIZE, 1);
        FileHandler fh_finer = new FileHandler(folder + "finer.log", LOGGING_FILE_SIZE, 1);
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
    }

    public static void initDbService() throws SQLException {
        dbService = new DBService(
            config.database.host,
            config.database.port,
            config.database.name,
            config.database.schema,
            config.database.user,
            config.database.password
        );
        userService = dbService.getUserService();
        gameService = dbService.getGameService();
        soundService = dbService.getSoundService();
    }

    public static void initSounds() {
        Toml toml = new Toml().read(new File(UtilityMethods.generatePath("sounds.toml")));
        for (Map.Entry e: toml.entrySet()) {
            Toml entry = (Toml) e.getValue();
            String path = UtilityMethods.generatePath(entry.getString("path"));
            File file = new File(path);
            if (!file.isFile()) {
                Logger.getGlobal().log(Level.SEVERE, "Sound " + path + " does not exist!");
                continue;
            }
            try {
                soundService.updateOrCreateSound((String) e.getKey(), path, entry.getString("description"));
            } catch (SQLException exc) {
                Logger.getGlobal().log(Level.SEVERE, exc.getMessage());
            }
        }
    }

    public static void initDiscordClient() {
        client = new ClientBuilder().withToken(config.discord.token).login();
        EventDispatcher dispatcher = client.getDispatcher();

        dispatcher.registerListener(new CommandExecutionListener());
        dispatcher.registerListener(new CommandListener());
        dispatcher.registerListener(userListener);
        dispatcher.registerListener(audioListener);
        dispatcher.registerListener(new IListener<ReadyEvent>() {
            @Override
            public void handle(ReadyEvent event) {
                initDiscordClientWhenReady();
            }
        });

        Logger.getGlobal().log(Level.FINER, "Discord Bot Client started.");
    }

    public static void initDiscordClientWhenReady() {
        Logger.getGlobal().log(Level.FINER, "Discord Bot Client ready.");
    }

    public static void initWebApi() throws IOException {
        WebApiServer server = new WebApiServer(config.webapi.port);
        server.addHandler("/", new WebApiIndexPageHandler());
        server.addHandler("/commands", new WebApiCommandHandler());
        server.start();
        Logger.getGlobal().log(Level.INFO,
            "Started the web server at port " + config.webapi.port + "."
        );
    }

}
