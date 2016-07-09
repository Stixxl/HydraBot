/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.main;

import com.corbi.robot.actions.DBServices.DBService;
import com.corbi.robot.actions.DBServices.GameService;
import com.corbi.robot.actions.DBServices.SoundService;
import com.corbi.robot.actions.DBServices.UserService;
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
    private static FileHandler fh = null;
    private static final int LOGGING_FILE_SIZE = 1024 * 1024;//1MB

    public static void main(String[] args) {

        try {
            fh = new FileHandler("temp.log", LOGGING_FILE_SIZE, 1);
        } catch (SecurityException | IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Failed to create FileHandler.");
        }
        Logger.getGlobal().addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        Logger.getGlobal().setUseParentHandlers(false);
        fh.setLevel(Level.INFO);
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
        client.getDispatcher().registerListener(new CommandExecutionListener());
        client.getDispatcher().registerListener(new CommandListener());
        client.getDispatcher().registerListener(new AudioListener());
        client.getDispatcher().registerListener(new UserListener());
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
}
