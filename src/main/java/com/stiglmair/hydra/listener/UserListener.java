package com.stiglmair.hydra.listener;

import com.stiglmair.hydra.main.Main;
import com.stiglmair.hydra.objects.Game;
import com.stiglmair.hydra.objects.User;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.user.PresenceUpdateEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;

/**
 * This class listens to events concerning users
 *
 * @author PogChamp
 */
public class UserListener {

    public List<User> onlineUsers = new ArrayList<>();

    /**
     * This method receives events that include the change of a satus (such as
     * offline to online) and calls a suited method.
     *
     * @param event event that is fired when a user changes his presence
     */
    @EventSubscriber
    public void onPresenceUpdated(PresenceUpdateEvent event) {
        IUser user = event.getUser();
        //user started a game
        if (!(event.getUser().isBot())) {
            //user game changed
            if (!event.getOldPresence().getPlayingText().isPresent() && event.getNewPresence().getPlayingText().isPresent())//User started playing game
            {
                onGameStarted(user.getLongID(), event.getNewPresence().getPlayingText().get());
            } else if (event.getOldPresence().getPlayingText().isPresent() && !event.getNewPresence().getPlayingText().isPresent())//User stopped playing game
            {
                onGameStopped(user.getLongID());
            }
            //user came changed online-status
            if ((event.getOldPresence().getStatus().equals(StatusType.OFFLINE)
                    || event.getOldPresence().getStatus().equals(StatusType.IDLE))
                    && event.getNewPresence().getStatus().equals(StatusType.ONLINE)) //user goes online or stops being idle
            {
                onOfflineToOnline(event);
            } else if (event.getNewPresence().getStatus().equals(StatusType.OFFLINE) || event.getNewPresence().getStatus().equals(StatusType.IDLE)) //user goes offline or is idle
            {
                onOnlineToOffline(event);
            }
        }
    }

    /**
     * if the user was never logged on to the server before an instance will be
     * created on the database. Also an user object is created and added to the
     * List onlineUsers.
     *
     * @param event event that is fired when a user status changes from offline
     * to online
     */
    public void onOfflineToOnline(PresenceUpdateEvent event) {
        addOnlineUser(String.valueOf(event.getUser().getLongID()), event.getUser().getName());
    }

    /**
     * The user's uptime will be updated on the database and he will be removed
     * from the list. onlineUsers
     *
     * @param event event that is fired when a user goes offline on a server
     */
    @EventSubscriber
    public void onOnlineToOffline(PresenceUpdateEvent event) {
        for (User user : onlineUsers) {
            if (user.getUserID().equals(String.valueOf(event.getUser().getLongID())))//user on same server and same user as specified in event
            {
                user.save();
                Main.logger.info("Following user went offline: {}", user.toString());
                onlineUsers.remove(user);
                break;
            }
        }
    }
    /**
     * Gets called whenever a new game is started
     *
     * @param userID ID of user
     * @param gameName name of newly started game
     */
    private void onGameStarted(Long userID, String gameName) {
        Game game = null;
        for (User user : onlineUsers) {
            if (userID.equals(Long.parseUnsignedLong(user.getUserID()))) {
                try {
                    game = Main.gameService.getGame(gameName, user.getUserID()); //retrieves game data, throws exception if none is retrieved
                    Main.logger.info("Game retrieved.");
                } catch (SQLException ex) {
                    Main.logger.error("game could not be retrieved.", ex);
                }
                if (game == null) {
                    try {
                        game = Main.gameService.createGame(gameName, user.getUserID()); // creates game, throws excepton if none could be created; either getGame or createGame should always work
                        Main.logger.info("New game created. {}", game.toString());
                    } catch (SQLException ex) {
                        Main.logger.error("game could not be created.", ex);
                    }
                }
                if (game != null) {
                    user.setGame(game); //informs the user object of new game
                }
                break;
            }
        }
    }
    /**
     * Gets called when a game is ended
     *
     * @param userID ID of User
     */
    private void onGameStopped(Long userID) {
        long time = System.currentTimeMillis();
        for (User user : onlineUsers) {
            if (userID.equals(Long.parseUnsignedLong(user.getUserID()))) {
                try {
                    //will update the game; increments the AmountPlayed and calculates new overall time as follows: current time - time of login + overall time spent online overall
                    Main.gameService.updateGame(user.getGame().getTitle(), user.getUserID(), user.getGame().getAmount_played() + 1, time - user.getGame().getStartTime() + user.getGame().getTime_played());
                    Main.logger.info("New game created. {}", user.getGame().toString());
                } catch (SQLException ex) {
                    Main.logger.error("could not update game.", ex);
                }
            }
        }
    }

    /**
     * creates a user object and adds it to the onlineUsers
     *
     * @param userID id of user
     * @param name name of user
     * @return the newly created user object; returns null if the user already
     * exists (is in onlineUsers)
     */
    public User addOnlineUser(String userID, String name) {
        for (User user : onlineUsers) {
            if (user.getUserID().equals(userID)) {
                Main.logger.warn("User is already online; ID: {}", user.getUserID());
                return null;
            }
        }
        User user = null;
        try {
            user = Main.userService.getUser(userID);//looks if user exists
        } catch (SQLException ex) {
            Main.logger.error("User could not be retrieved.", ex);
        }

        try {
            if (user == null) {
                user = Main.userService.createUser(userID, name);//creates user if none exists
            }
        } catch (SQLException ex) {
            Main.logger.error("User could not be created.", ex);
        }

        if (user != null) {
            onlineUsers.add(user);
            Main.logger.info("Following user was added to online users: {}", user.toString());
        } else {
            Main.logger.error("User could not be added to onlineUsers since he was not created nor retrieved.");
        }
        return user;
    }

    /**
     * Finds an user, if they are online
     *
     * @param userID id of user to be found
     * @return an user object, which is online; null if none was found
     */
    public User getOnlineUser(String userID) {
        for (User user : onlineUsers) {
            if (user.getUserID().equals(userID)) {
                return user;
            }
        }
        return null;
    }
}
