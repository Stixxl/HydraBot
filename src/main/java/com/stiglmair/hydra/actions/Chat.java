package com.stiglmair.hydra.actions;

import com.stiglmair.hydra.help.Help;
import com.stiglmair.hydra.main.Main;
import com.stiglmair.hydra.objects.Game;
import com.stiglmair.hydra.objects.User;
import com.stiglmair.hydra.utilities.UtilityMethods;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.sql.SQLException;
import java.util.*;

/**
 * This class is designed to handle anything the bot wants to
 * write in a TextChannel. Any write to chat command should be done with this
 * class.
 *
 * @author Stiglmair
 */
public class Chat {

    /**
     * This method's only reason for existence is to make Daniel's Life just a
     * tiny bit harder.
     *
     * @param channel
     */
    public static void insultDaniel(IChannel channel) {
        String[] insults = {"Daniel ist sehr speziell in der Wahl der Musiklautstärke. Tätsächlich ist für ihn alles unangenehm laut.",
            "Daniel kauft keine neuen Spiele, da er zu sehr an seiner einzigen Liebe hängt, der Kunst des Feedens."};
        Random randInt = new Random(System.currentTimeMillis());

        int index = randInt.nextInt(insults.length);
        sendMessage(channel, insults[index]);
    }

    /**
     * Writes a message, that is specifically aimed at improving anbodys game,
     * be it in league or real life
     *
     * @param channel
     */
    public static void tellBinsenweisheit(IChannel channel) {
        String binsenweisheit = null;
        try {
            binsenweisheit = Main.dbService.getBinsenweisheitenService().selectSentenceRandom();
        } catch (SQLException ex) {
            Main.logger.error("Could not retrieve Binsenweisheit.", ex);
        }
        if (binsenweisheit != null) {
            sendMessage(channel, binsenweisheit);
        } else {
            sendErrorMessage(channel);
            Main.logger.warn("Error occured while trying to retrieve a Binsenweisheit.");
        }
    }


    /**
     * shows stats such as overall uptime on servers, time spent playing etc.
     *
     * @param channel id of the server from which the request was received
     * @param userID id of the user that sent request @link
     * #sendMessage(IChannel, String) channel
     * @param args the arguments received with the command
     * @return true if format of input was correct, false otherwise
     */
    public static boolean showStats(IChannel channel, String userID, String args[]) {
        if (args.length > 2 || args.length == 0) {
            return false;
        } else {
            User user = Main.userListener.getOnlineUser(userID);
            switch (args[0]) {

                case "me":
                    if (user != null) {
                        showStatsMe(channel, user);
                    } else if (userID != null) {
                        showStatsMe(channel, userID);
                    } else {
                        Main.logger.warn("User could not be found in onlineUsers.");
                        sendErrorMessage(channel);
                        return true;
                    }
                    break;

                case "all":
                    showStatsAll(channel);
                    break;
                case "name":
                    if (args.length == 2) {//showStatsByName needs a parameter itself
                        showStatsByName(channel, args[1]);
                        break;
                    } else {
                        return false;
                    }
                case "ranking":
                    if (args.length == 2 && UtilityMethods.isInteger(args[1])) {//showStatsRanking needs a parameter itself
                        showStatsRanking(channel, Integer.parseInt(args[1]));//selects the top n users by uptime
                        break;
                    } else {
                        return false;
                    }
                case "save":
                    if (user != null) {
                        user.save();
                        break;
                    } else {
                        sendErrorMessage(channel);
                        Main.logger.warn("User could not be found in onlineUsers. UserID: {}", userID);
                        return true;
                    }
                default:
                    return false;
            }
        }
        return true;
    }

    /**
     * show stats about the user, specifically his overall uptime
     *
     * @param channel @link #showStats(IChannel, IUser, String, String[])
     * channel
     * @param user user from which the request was received
     */
    private static void showStatsMe(IChannel channel, User user) {
        user.save();
        String personalStats = user.toString() + System.lineSeparator() + System.lineSeparator() + getGamesMessage(user.getUserID());
        sendMessage(channel, personalStats);
    }

    private static void showStatsMe(IChannel channel, String userID) {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(Main.userService.getUser(userID).toString()).append(System.lineSeparator()).append(System.lineSeparator()).append(getGamesMessage(userID));
        } catch (SQLException ex) {
            Main.logger.error("ShowStats failed for user with ID (not in onlineUsers): " + userID, ex);
        }
        sendMessage(channel, sb.toString());
    }

    /**
     * returns Users which equal the specified name
     *
     * @param channel @link #showStats(IChannel, IUser, String, String[])
     * channel
     * @param name name, of which the stats are to be shown
     * @param guildID @link #showStats(IChannel, IUser, String, String[])
     * guildID
     */
    private static void showStatsByName(IChannel channel, String name) {
        List<User> users = new ArrayList<>();
        User.saveUsers(Main.userListener.onlineUsers);
        try {
            users = Main.userService.getUserByName(name);
        } catch (SQLException ex) {
            Main.logger.error("User could not be retrieved by name.", ex);
        }
        if (!(users.isEmpty())) {
            StringBuilder sb = new StringBuilder();
            for (User user : users) {
                sb.append(user.toString()).append(System.lineSeparator()).append(System.lineSeparator()).append(getGamesMessage(user.getUserID()));
            }
            sendMessage(channel, sb.toString());
        } else {
            sendMessage(channel, "Die Person mit dem Namen " + UtilityMethods.highlightItalic(name) + " existiert genau so wenig wie deine Freundin.");
        }
    }

    /**
     * shows the stats of the top n users
     *
     * @param channel @link #showStats(IChannel, IUser, String, String[])
     * channel
     * @param limit
     * {@link com.corbi.robot.actions.DBServices.UserService#getRankingByUptime(String, int}
     * limit
     * @param guildID @link #showStats(IChannel, IUser, String, String[])
     * guildID
     */
    private static void showStatsRanking(IChannel channel, int limit) {
        List<User> users = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        User.saveUsers(Main.userListener.onlineUsers);
        try {
            users = Main.userService.getRankingByUptime(limit);
        } catch (SQLException ex) {
            Main.logger.error("Users could not be retrieved by ranking.", ex);
        }
        if (!(users.isEmpty())) {
            for (int i = 0; i < users.size(); i++) {
                sb.append(UtilityMethods.highlightBold(String.valueOf(i + 1))).append(": ").append(users.get(i).toString()).append(System.lineSeparator());// bold rank number: user.toString() + linebreak
            }
            sendMessage(channel, sb.toString());
        } else {
            sendErrorMessage(channel);
        }
    }

    /**
     * shows the combined stats of all users
     *
     * @param channel @link #showStats(IChannel, IUser, String, String[])
     * channel
     * @param guildID @link #showStats(IChannel, IUser, String, String[])
     * guildID
     */
    private static void showStatsAll(IChannel channel) {
        long uptime = 0;
        List<Game> games = null;
        User.saveUsers(Main.userListener.onlineUsers);
        try {
            uptime = Main.userService.getUptimeAll();
        } catch (SQLException ex) {
            Main.logger.error("could not retrieve data for all users", ex);
        }
        try {
            games = Main.gameService.getGamesAll();
        } catch (SQLException ex) {
            Main.logger.error("could not retrieve game data for all users.", ex);
        }
        StringBuilder sb = new StringBuilder();
        if (games != null) {
            sb.append(System.lineSeparator()).append(System.lineSeparator());
            sb.append("Eure verschwendete Zeit teilt ihr anscheinend wie folgt auf:");
            for (int i = 0; i < games.size(); i++) {
                sb.append(System.lineSeparator()).append(String.valueOf(i)).append(". ").append(games.get(i).toString());
            }
        }
        String statsAll = "Ihr habt insgesamt " + UtilityMethods.highlightItalic(UtilityMethods.formatTime(uptime)) + " auf diesem Server verschwendet." + sb.toString();
        sendMessage(channel, statsAll);
    }

    /**
     * Sends a message that informs the user of the use of a wrong command
     *
     * @param wrongCommand a non-supported command
     * @param channel @link #sendMessage(IChannel, String) channel
     */
    public static void showUnsupportedFormatMessage(String wrongCommand, IChannel channel) {
        String errorInfo = "The HydraBot does not support the command " + UtilityMethods.highlightItalic(wrongCommand) + "."
                + " Gib " + UtilityMethods.highlightItalic("!hydra help") + " ein um mögliche Befehle einzusehen.";

        sendMessage(channel, errorInfo);
    }

    /**
     * sends a message that informs the user of the non-supported arguments for
     * a supported command
     *
     * @param command command that does not support the used arguments
     * @param wrongArgs non-supported arguments
     * @param channel @link #sendMessage(IChannel, String) channel
     */
    public static void showUnsupportedFormatMessage(String command, String[] wrongArgs, IChannel channel) {

        String errorInfo = "The HydraBot does not support the arguments " + UtilityMethods.highlightItalic(Arrays.toString(wrongArgs))
                + " for the command " + UtilityMethods.highlightItalic(command) + "."
                + " Gib " + UtilityMethods.highlightItalic("!hydra help " + command) + " für mögliche Unterbefehle ein.";
        sendMessage(channel, errorInfo);

    }

    /**
     * sends a message that will indicate to the user that he is no authorized
     *
     * @param textChannel @see
     * com.corbi.robot.actions.Chat#sendMessage(sx.blah.discord.handle.obj.IChannel,
     * java.lang.String)
     */
    public static void showUnauthorizedMessage(IChannel textChannel) {
        Chat.sendMessage(textChannel, "?");
        new Timer().schedule(
                new java.util.TimerTask() {
            @Override
            public void run() {
                Chat.sendMessage(textChannel, "Diese Funktion ist nicht für Plebs gedacht.");
            }
        },
                1000
        );
    }

    /**
     * sends Information regarding a given command; if args is empty (=!hydra
     * help) only top level commands will be shown
     *
     * @param channel channel, where message is sent
     * @param args args, containing possible command chain
     */
    public static void showHelp(IChannel channel, String[] args) {
        Help help = new Help();
        if (args == null || args.length == 0) {
            sendMessage(channel, help.showHelp());
        } else {
            sendMessage(channel, help.showHelp(args));
        }
    }

    /**
     * The bot will send the specified message in the specified channel splits
     * the message if needed so it does not exceed 200 chars as required by the
     * discord API
     *
     * @param channel channel where the message is to be sent
     * @param content the content of the intended message
     *
     */
    public static void sendMessage(IChannel channel, String content) {
        int MessageParts = content.length() / 2000 + 1; // Ammount of parts that have to be sent; +1 because the result will be rounded off
        for (int i = 0; i < MessageParts; i++) {
            String splitMessage = content.substring(i * 2000, Math.min((i + 1) * 2000, content.length()));

            try {
                new MessageBuilder(Main.client).withChannel(channel).withContent(splitMessage).build();
            } catch (RateLimitException | DiscordException | MissingPermissionsException ex) {
                Main.logger.error("message could not be sent.", ex);
            }
        }
    }

    /**
     * A utility method that will retrieve and format the games for a given user
     *
     * @param userID unique id for an user
     * @param guildID Server from which the request was sent
     * @return a formatted String that contains all information for games from a
     * single user
     */
    private static String getGamesMessage(String userID) {
        List<Game> games = null;
        try {
            games = Main.gameService.getGames(userID);
        } catch (SQLException ex) {
            Main.logger.error("games could not be retrieved.", ex);
        }
        StringBuilder sb = new StringBuilder();
        if (games != null) {
            sb.append("Die verschwendete Zeit wird wie folgt aufgeteilt:");
            for (int i = 0; i < games.size(); i++) {
                sb.append(System.lineSeparator()).append(String.valueOf(i + 1)).append(". ").append(games.get(i).toString());
            }
        }
        return sb.toString();
    }

    /**
     * sends a general message that an error occured
     *
     * @param channel Text channel in which to send the message
     */
    private static void sendErrorMessage(IChannel channel) {
        sendMessage(channel, "Ein Fehler ist aufgetreten. Bitte kontaktiere die nächstbeste Bedienung im Penny.");
    }

}
