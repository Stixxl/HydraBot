/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.main;

import com.corbi.robot.events.AudioListener;
import com.corbi.robot.events.CommandListener;
import com.corbi.robot.events.CommandExecutionListener;
import com.corbi.robot.utilities.UtilityMethods;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    public static void main(String[] args) {
        readConfig();
        try {
            client = new ClientBuilder().withToken(Token).login();
        } catch (DiscordException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        //register event listener
        client.getDispatcher().registerListener(new CommandExecutionListener());
        client.getDispatcher().registerListener(new CommandListener());
        client.getDispatcher().registerListener(new AudioListener());
    }
    /**
     * a method for reading the config and mapping its values to suited variables and such
     */
    public static void readConfig() {
        String path = "/src/main/resources/config.properties";
        path = UtilityMethods.generatePath(path);
        File f = new File(path);
        if (f.exists() && !f.isDirectory()) {//checks wheter there is configurationdata to be read
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
        }
    }
}
