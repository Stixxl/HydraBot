/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.main;

import com.corbi.robot.DBServices.DBService;
import com.corbi.robot.DBServices.GameService;
import com.corbi.robot.DBServices.SoundService;
import com.corbi.robot.DBServices.UserService;
import com.corbi.robot.events.AudioListener;
import com.corbi.robot.events.CommandListener;
import com.corbi.robot.events.CommandExecutionListener;
import com.corbi.robot.events.UserListener;
import com.corbi.robot.utilities.UtilityMethods;
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
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

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
    private static final String LOGFOLDER = "logs/";
    public static UserListener userListener;

    public static void main(String[] args) {
        init();
        Logger.getGlobal().setLevel(Level.FINER);
        try {
            //create filehandler
            fh_severe = new FileHandler(LOGFOLDER + "severe.log", LOGGING_FILE_SIZE, 1);
            fh_info = new FileHandler(LOGFOLDER + "info.log", LOGGING_FILE_SIZE, 1);
            fh_finer = new FileHandler(LOGFOLDER + "finer.log", LOGGING_FILE_SIZE, 1);

        } catch (SecurityException | IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Failed to create FileHandler.");
        }

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

        readConfig();
        userService = dbService.getUserService();
        gameService = dbService.getGameService();
        soundService = dbService.getSoundService();

        try {
            client = new ClientBuilder().withToken(Token).login();
        } catch (DiscordException ex) {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
        }
        //register event listener
        userListener = new UserListener();
        client.getDispatcher()
                .registerListener(new CommandExecutionListener());
        client.getDispatcher()
                .registerListener(new CommandListener());
        client.getDispatcher()
                .registerListener(new AudioListener());
        client.getDispatcher()
                .registerListener(userListener);
        Logger.getGlobal()
                .log(Level.FINER, "Server started.");
    }

    /**
     * a method for reading the config and mapping its values to suited
     * variables and such
     */
    public static void readConfig() {
        String path = "config.properties";
        path = UtilityMethods.generatePath(path);
        File f = new File(path);
        if (f.exists() && !f.isDirectory()) {//true if there is configurationdata to be read, false otherwise

            Properties properties = new Properties();
            FileInputStream inStream = null;
            try {
                inStream = new FileInputStream(path);
            } catch (FileNotFoundException e1) {
            }
            try {
                properties.load(inStream);
            } catch (IOException e) {
            }
            Token = properties.getProperty("token");
            dbService = new DBService(properties.getProperty("dbusername"), properties.getProperty("dbpassword"));
        }
    }

    /**
     * initializes the System; creates necessary folders;
     */
    public static void init() {
        File f = new File(LOGFOLDER);
        if (!(f.exists() && f.isDirectory())) {
            f.mkdir();
        } else {
            //deletes the logging folder and creates a new one, thus wiping its content
            try {
                UtilityMethods.deleteFileOrFolder(f.toPath());

                f.mkdir();
            } catch (IOException ex) {
                Logger.getGlobal().log(Level.SEVERE, "Error occured while trying to delete the logging folder.", ex);
            }
        }
    }
}
