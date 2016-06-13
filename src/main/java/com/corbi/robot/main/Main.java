/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.main;

import com.corbi.robot.events.AudioListener;
import com.corbi.robot.events.CommandListener;
import com.corbi.robot.events.CommandExecutionListener;
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
    public static void main(String[] args) {
    try {
        client = new ClientBuilder().withToken("MTkxMTYwOTIzNTk4MDk0MzM3.Cj2P-w.mqBlzmwL3fnhK1CTwUI2AIEiWVs").login();
    } catch (DiscordException ex) {
        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
    //register event listener
    client.getDispatcher().registerListener(new CommandExecutionListener());
    client.getDispatcher().registerListener(new CommandListener());
    client.getDispatcher().registerListener(new AudioListener());
    }
}
