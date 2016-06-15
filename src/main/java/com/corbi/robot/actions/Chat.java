/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.actions;

import com.corbi.robot.main.Main;
import java.util.Arrays;
import java.util.Random;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;

/**
 *
 * @author Stiglmair This class is designed to handle anything the bot wants to
 * write in a TextChannel. Any write to chat command should be done with this
 * class.
 */
public class Chat {

    /**
     *
     * @param channel @link #sendMessage(IChannel, String) channel
     * @throws HTTP429Exception
     * @throws DiscordException
     * @throws MissingPermissionsException This method's only reason for
     * existence is to make Daniel's Life just a tiny bit harder.
     */
    public static void insultDaniel(IChannel channel) throws HTTP429Exception, DiscordException, MissingPermissionsException {
        String[] insults = {"Daniel ist sehr speziell in der Wahl der Musiklautstärke. Tätsächlich ist für ihn alles unangenehm laut.",
            "Daniel kauft keine neuen Spiele, da er zu sehr an seiner einzigen Liebe hängt, der Kunst des Feedens."};
        Random randInt = new Random(System.currentTimeMillis());

        int index = randInt.nextInt(insults.length);
        sendMessage(channel, insults[index]);
    }
    /**
     * Writes a message, that is specifically aimed at improving noahs game be it in league or real life
     * @param channel @link #sendMessage(IChannel, String) channel
     * @throws HTTP429Exception
     * @throws DiscordException
     * @throws MissingPermissionsException 
     */
    public static void tellBinsenweisheit(IChannel channel) throws HTTP429Exception, DiscordException, MissingPermissionsException {
        String[] binsenweisheiten = {"Kämpfe nie 1 gegen 3.", "Die ultimative Fähigkeit kann auch zum Fliehen eines aussichtlosen Kampfes genutzt werden."};
        Random randInt = new Random(System.currentTimeMillis());

        int index = randInt.nextInt(binsenweisheiten.length);
        String binsenweisheit = "Binsenweisheit " + String.valueOf(index + 1) + ": " +  binsenweisheiten[index];
        sendMessage(channel, binsenweisheit);
    }
    /**
     * tells a random memorable quote
     * @param channel @link #sendMessage(IChannel, String) channel
     */
    public static void tellQuotes (IChannel channel){
    {
        
    }
}
    public static void showUnsupportedFormatMessage(String wrongCommand, IChannel channel) throws HTTP429Exception, MissingPermissionsException, DiscordException {
        String errorInfo = "The HydraBot does not support the command *" + wrongCommand + "*.";

        sendMessage(channel, errorInfo);
    }

    public static void showUnsupportedFormatMessage(String command, String[] wrongArgs, IChannel channel) throws HTTP429Exception, MissingPermissionsException, DiscordException {

        String errorInfo = "The HydraBot does not support the arguments *" + Arrays.toString(wrongArgs)
                + "* for the command *" + command + "*.";
        sendMessage(channel, errorInfo);

    }

    /**
     * The bot will send the specified message in the specified channel
     *
     * @param channel channel where the message is to be sent
     * @param content the content of the intended message
     * @throws HTTP429Exception
     * @throws DiscordException
     * @throws MissingPermissionsException
     */
    public static void sendMessage(IChannel channel, String content) throws HTTP429Exception, DiscordException, MissingPermissionsException {
        new MessageBuilder(Main.client).withChannel(channel).withContent(content).build();
    }
}
