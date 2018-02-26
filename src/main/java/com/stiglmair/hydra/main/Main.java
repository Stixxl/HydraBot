package com.stiglmair.hydra.main;

import com.moandjiezana.toml.Toml;
import com.stiglmair.hydra.dbservices.DBService;
import com.stiglmair.hydra.dbservices.GameService;
import com.stiglmair.hydra.dbservices.SoundService;
import com.stiglmair.hydra.dbservices.UserService;
import com.stiglmair.hydra.listener.AudioListener;
import com.stiglmair.hydra.listener.CommandExecutionListener;
import com.stiglmair.hydra.listener.CommandListener;
import com.stiglmair.hydra.listener.UserListener;
import com.stiglmair.hydra.objects.User;
import com.stiglmair.hydra.utilities.UtilityMethods;
import com.stiglmair.hydra.webapi.WebApiCommandHandler;
import com.stiglmair.hydra.webapi.WebApiIndexPageHandler;
import com.stiglmair.hydra.webapi.WebApiServer;
import org.apache.commons.cli.*;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.*;

/**
 *
 * @author Stiglmair
 */
public class Main {

    public static org.slf4j.Logger logger;
    public static IDiscordClient client;
    public static Config config = new Config();
    public static DBService dbService;
    public static UserService userService;
    public static GameService gameService;
    public static SoundService soundService;
    public static AudioListener audioListener = new AudioListener();
    public static UserListener userListener = new UserListener();

    private static final int LOGGING_FILE_SIZE = 1024 * 1024;  // 1MB

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
        config = new Toml().read(new File(UtilityMethods.generatePath(configFile))).to(Config.class);

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

        FileHandler handler;
        java.util.ArrayList<FileHandler> handlers = new java.util.ArrayList<>();

        handler = new FileHandler(folder + "severe.log", LOGGING_FILE_SIZE, 1);
        handler.setLevel(Level.SEVERE);
        handlers.add(handler);

        handler = new FileHandler(folder + "info.log", LOGGING_FILE_SIZE, 1);
        handler.setLevel(Level.INFO);
        handlers.add(handler);

        handler = new FileHandler(folder + "debug.log", LOGGING_FILE_SIZE, 1);
        handler.setLevel(Level.FINER);
        handlers.add(handler);

        SimpleFormatter formatter = new SimpleFormatter();
        for (FileHandler fh: handlers) {
            fh.setFormatter(formatter);
            fh.setFilter((LogRecord record) -> record.getLevel().equals(fh.getLevel()));
            Logger.getLogger("").addHandler(fh);
        }

        Logger.getGlobal().setLevel(Level.INFO);

        // TODO: Disable logging method entries.
        logger = org.slf4j.LoggerFactory.getLogger(Main.class);
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
                logger.error("Sound " + path + " does not exist!");
                continue;
            }
            try {
                soundService.updateOrCreateSound((String) e.getKey(), path, entry.getString("description"));
            } catch (SQLException exc) {
                logger.error(exc.getMessage());
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

        logger.info("Discord Bot Client started.");
    }

    public static void initDiscordClientWhenReady() {
        // Change the name and playing message of the bot.
        try {
            client.changeUsername("Süßwasserpolyp");
        } catch (DiscordException | RateLimitException ex) {
            logger.error("Error while setting bot's username.", ex);
        }
        client.changePlayingText("with your emotions"); //sets the game of the bot

        // Initialize users that are currently online/
        for (IGuild guild : client.getGuilds()) {
            logger.info("bot is online on guild {}", guild.toString());
            for (IUser user : User.getOnlineUsers(guild.getUsers())) {
                userListener.addOnlineUser(String.valueOf(user.getLongID()), user.getName());//adds every user that is online, when the bot started, to the onlineUser list
            }
        }

        logger.info("Discord Bot Client ready.");
    }

    public static void initWebApi() throws IOException {
        WebApiServer server = new WebApiServer(config.webapi.listenAddress, config.webapi.port);
        server.addHandler("/", new WebApiIndexPageHandler());
        server.addHandler("/commands", new WebApiCommandHandler());
        server.start();
        logger.info("Started the web server at " + config.webapi.listenAddress +
                ":" + config.webapi.port + ".");
    }

}
