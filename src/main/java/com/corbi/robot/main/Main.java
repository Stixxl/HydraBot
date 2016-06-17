/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.main;

import com.corbi.robot.actions.DBServices.DBService;
import com.corbi.robot.actions.DBServices.GameService;
import com.corbi.robot.actions.DBServices.UserService;
import com.corbi.robot.events.AudioListener;
import com.corbi.robot.events.CommandListener;
import com.corbi.robot.events.CommandExecutionListener;
import com.corbi.robot.events.UserListener;
import com.corbi.robot.objects.User;
import com.corbi.robot.utilities.UtilityMethods;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public static void main(String[] args) {
        readConfig();
        userService = dbService.getUserService();
        gameService = dbService.getGameService();
        
        try {
            client = new ClientBuilder().withToken(Token).login();
        } catch (DiscordException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
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
        String path = "/src/main/resources/config.properties";
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
