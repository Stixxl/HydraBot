/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.events;

import com.corbi.robot.main.Main;
import com.corbi.robot.objects.Game;
import com.corbi.robot.objects.User;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.PresenceUpdateEvent;
import sx.blah.discord.handle.impl.events.StatusChangeEvent;
import sx.blah.discord.handle.obj.Presences;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.handle.obj.Status.StatusType;

/**
 * This class listens to events concerning users
 *
 * @author PogChamp
 */
public class UserListener {
    
    public List<User> onlineUsers = new ArrayList<>();

    /**
     *
     * This method receives events that include the change of a satus (such as
     * offline to online) and calls a suited method.
     *
     * @param event event that is fired when a user changes his presence
     */
    @EventSubscriber
    public void onPresenceUpdated(PresenceUpdateEvent event) {
        if (!(event.getUser().isBot())) {
            if (event.getOldPresence().equals(Presences.OFFLINE)
                    || (event.getOldPresence().equals(Presences.IDLE)
                    && (event.getNewPresence().equals(Presences.ONLINE)
                    || event.getNewPresence().equals(Presences.STREAMING)))) //user goes online or stops being idle
            {
                onOfflineToOnline(event);
            } else if (event.getNewPresence().equals(Presences.OFFLINE) || event.getNewPresence().equals(Presences.IDLE)) //user goes offline or is idle
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
        addOnlineUser(event.getUser().getID(), event.getUser().getName());
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
            if (user.getUserID().equals(event.getUser().getID()))//user on same server and same user as specified in event
            {
                user.save();
                Logger.getGlobal().log(Level.FINER, "Following user went offline: {0}", user.toString());
                onlineUsers.remove(user);
                break;
            }
        }
    }

    /**
     *
     * @param event event that is fired whenever a game changes
     */
    @EventSubscriber
    public void onGameChanged(StatusChangeEvent event) {
        long time = System.currentTimeMillis();
        Game game = null;
        Status status = event.getNewStatus();
        for (User user : onlineUsers) {
            if (user.getUserID().equals(event.getUser().getID()) && status.getType().compareTo(StatusType.GAME) == 0) { //true if the user started playing a game
                String title = status.getStatusMessage();
                if (user.getGame() != null) { //true if the user was playing a game
                    try {
                        //will update the game; increments the AmountPlayed and calculates new overall time as follows: current time - time of login + overall time spent online overall
                        Main.gameService.updateGame(user.getGame().getTitle(), user.getUserID(), user.getGame().getAmount_played() + 1, time - user.getGame().getStartTime() + user.getGame().getTime_played());
                        Logger.getGlobal().log(Level.INFO, "New game created. {0}", user.getGame().toString());
                    } catch (SQLException ex) {
                        Logger.getGlobal().log(Level.SEVERE, "could not update game.", ex);
                    }
                } else {
                    // true if the new game is a game, false if user is now idle
                    try {
                        game = Main.gameService.getGame(title, user.getUserID()); //retrieves game data, throws exception if none is retrieved
                        Logger.getGlobal().log(Level.INFO, "Game retrieved.");
                    } catch (SQLException ex) {
                        Logger.getGlobal().log(Level.SEVERE, "game could not be retrieved.", ex);
                    }
                    if (game == null) {
                        try {
                            game = Main.gameService.createGame(title, user.getUserID()); // creates game, throws excepton if none could be created; either getGame or createGame should always work
                            Logger.getGlobal().log(Level.INFO, "New game created. {0}", game.toString());
                        } catch (SQLException ex) {
                            Logger.getGlobal().log(Level.SEVERE, "game could not be created.", ex);
                        }
                    }
                    if (game != null) {
                        user.setGame(game); //informs the user object of new game
                    }
                }
                break;
            }
            //user on same server and same user as specified in event
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
                Logger.getGlobal().log(Level.WARNING, "User is already online; ID: {0}", user.getUserID());
                return null;
            }
        }
        User user = null;
        try {
            user = Main.userService.getUser(userID);//looks if user exists
        } catch (SQLException ex) {
            Logger.getGlobal().log(Level.SEVERE, "User could not be retrieved.", ex);
        }
        
        try {
            if (user == null) {
                user = Main.userService.createUser(userID, name);//creates user if none exists
            }
        } catch (SQLException ex) {
            Logger.getGlobal().log(Level.SEVERE, "User could not be created.", ex);
        }
        
        if (user != null) {
            onlineUsers.add(user);
            Logger.getGlobal().log(Level.FINER, "Following user was added to online users: {0}", user.toString());
        } else {
            Logger.getGlobal().log(Level.SEVERE, "User could not be added to onlineUsers since he was not created nor retrieved.");
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
