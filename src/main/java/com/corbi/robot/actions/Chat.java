/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.actions;

import com.corbi.robot.main.Main;
import com.corbi.robot.objects.Game;
import com.corbi.robot.utilities.UtilityMethods;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
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
     * Writes a message, that is specifically aimed at improving noahs game be
     * it in league or real life
     *
     * @param channel @link #sendMessage(IChannel, String) channel
     * @throws HTTP429Exception
     * @throws DiscordException
     * @throws MissingPermissionsException
     */
    public static void tellBinsenweisheit(IChannel channel) throws HTTP429Exception, DiscordException, MissingPermissionsException {
        String[] binsenweisheiten = {"Ein Kampf, in dem die zahlenmäßige Unterlegenheit zwei oder mehr beträgt, ist kein Kampf, sondern eine Dummheit.",
            "Die ultimative Fähigkeit kann auch zum Fliehen eines aussichtlosen Kampfes genutzt werden.", "Sollte eine Person angerufen werden, so bite diese darum, dir ihren Gesprächspartner mitzuteilen. "
                + "Dies hat den Vorteil eines angenehmen Themas sollte es zu sogenanntem \"Smalltalk\" kommen."};
        Random randInt = new Random(System.currentTimeMillis());

        int index = randInt.nextInt(binsenweisheiten.length);
        String binsenweisheit = "Binsenweisheit " + String.valueOf(index + 1) + ": " + binsenweisheiten[index];
        sendMessage(channel, binsenweisheit);
    }

    /**
     * tells a random memorable quote
     *
     * @param channel @link #sendMessage(IChannel, String) channel
     */
    public static void tellQuotes(IChannel channel) {

    }

    /**
     * shows stats such as overall uptime on servers, time spent playing etc.
     *
     * @param channel @link #sendMessage(IChannel, String) channel 
     * @param args the arguments received with the command
     * @param user User who sent the command
     * @param guildID the id of the server
     * @return true if format of input was correct, false otherwise
     */
    public static boolean showStats(IChannel channel, IUser user, String guildID, String args[]) {
        if (args.length > 1 || args.length == 0) {
            return false;
        } else {
            switch (args[0]) {
                case "me":
                    showStatsUser(channel, user, guildID);
                    break;
                case "all":
                    showStatsAll(channel, guildID);
                    break;
                default:
                    return false;
            }
        }
        return true;
    }
    /**
     * show stats about the user, specifically his overall uptime
     * @param channel @link #showStats(IChannel, IUser, String, String[]) channel
     * @param user @link #showStats(IChannel, IUser, String, String[]) user
     * @param guildID @link #showStats(IChannel, IUser, String, String[]) guildID
     */
    private static void showStatsUser(IChannel channel, IUser user, String guildID) {
        long uptime = 0;
        List<Game> games = null;
        try {
            uptime = Main.userService.getUser(user.getID(), guildID).getUptime();
        } catch (SQLException ex) {
            Logger.getGlobal().log(Level.SEVERE, "user could not be retrieved.", ex);
        }
        try {
            games = Main.gameService.getGames(user.getID(), guildID);
        } catch (SQLException ex) {
            Logger.getGlobal().log(Level.SEVERE, "games could not be retrieved.", ex);
        }
        StringBuilder sb = new StringBuilder();
        if(games !=null)
        {
            sb.append("Deine verschwendete Zeit teilst du anscheinend wie folgt auf:");
        for(int i = 0; i < games.size(); i++)
        {
            sb.append(System.lineSeparator()).append(String.valueOf(i)).append(". ").append(games.get(i).toString());
        }
        }
        String personalStats = "Du hast insgesamt *" + UtilityMethods.formatTime(uptime) + "* auf diesem Server verschwendet."
                + System.lineSeparator() + sb.toString();
        sendMessage(channel, personalStats);
    }
    /**
     * 
     * @param channel @link #showStats(IChannel, IUser, String, String[]) channel
     * @param guildID @link #showStats(IChannel, IUser, String, String[]) guildID
     */
    private static void showStatsAll(IChannel channel, String guildID)
    {
       long uptime = 0;     
       List<Game> games = null;
        try {
            uptime = Main.userService.getUptimeAll(guildID);
        } catch (SQLException ex) {
            Logger.getGlobal().log(Level.SEVERE, "could not retrieve data for all users", ex);
        }
        try {
            games = Main.gameService.getGamesAll(guildID);
        } catch (SQLException ex) {
            Logger.getGlobal().log(Level.SEVERE, "could not retrieve game data for all users.", ex);
        }
                StringBuilder sb = new StringBuilder();
        if(games !=null)
        {
            sb.append("Eure verschwendete Zeit teilt ihr anscheinend wie folgt auf:");
        }
        for(int i = 0; i < games.size(); i++)
        {
            sb.append(System.lineSeparator()).append(String.valueOf(i)).append(". ").append(games.get(i).toString());
        }
        String statsAll = "Ihr habt insgesamt *" + UtilityMethods.formatTime(uptime) + "* auf diesem Server verschwendet.";
        sendMessage(channel, statsAll);
    }
    /**
     * Sends a message that informs the user of the use of a wrong command
     * @param wrongCommand a non-supported command
     * @param channel @link #sendMessage(IChannel, String) channel
     */
    public static void showUnsupportedFormatMessage(String wrongCommand, IChannel channel) {
        String errorInfo = "The HydraBot does not support the command *" + wrongCommand + "*.";

        sendMessage(channel, errorInfo);
    }
    /**
     * sends a message that informs the user of the non-supported arguments for a supported command
     * @param command command that does not support the used arguments
     * @param wrongArgs non-supported arguments
     * @param channel @link #sendMessage(IChannel, String) channel
     */
    public static void showUnsupportedFormatMessage(String command, String[] wrongArgs, IChannel channel) {

        String errorInfo = "The HydraBot does not support the arguments *" + Arrays.toString(wrongArgs)
                + "* for the command *" + command + "*.";
        sendMessage(channel, errorInfo);

    }

    /**
     * The bot will send the specified message in the specified channel
     *
     * @param channel channel where the message is to be sent
     * @param content the content of the intended message
     *
     */
    public static void sendMessage(IChannel channel, String content) {
        try {
            new MessageBuilder(Main.client).withChannel(channel).withContent(content).build();
        } catch (HTTP429Exception | DiscordException | MissingPermissionsException ex) {
            Logger.getGlobal().log(Level.SEVERE, "message could not be sent.", ex);
        }
    }
}
