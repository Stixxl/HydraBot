/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.events;

import com.corbi.robot.actions.Chat;
import com.corbi.robot.main.Main;
import com.corbi.robot.objects.User;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sx.blah.discord.api.EventSubscriber;
import sx.blah.discord.handle.impl.events.GameChangeEvent;
import sx.blah.discord.handle.impl.events.PresenceUpdateEvent;
import sx.blah.discord.handle.impl.events.UserJoinEvent;
import sx.blah.discord.handle.impl.events.UserLeaveEvent;
import sx.blah.discord.handle.obj.Presences;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MissingPermissionsException;

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
        if (event.getOldPresence().equals(Presences.OFFLINE) && event.getNewPresence().equals(Presences.ONLINE)) //user goes online
        {
            onOfflineToOnline(event);
        } else if (event.getNewPresence().equals(Presences.OFFLINE)) //user goes offline
        {
            onOnlineToOffline(event);
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
        String userID = event.getUser().getID();
        String guildID = event.getGuild().getID();
        User user = null;
        try {
            user = Main.userService.getUser(userID, guildID);//looks if user exists
        } catch (SQLException ex) {
            Logger.getLogger(UserListener.class.getName()).log(Level.SEVERE, "user could not be retrieved.", ex);
        }
        try {
            if (user == null) {
                user = Main.userService.createUser(userID, guildID);//creates user if none exists
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserListener.class.getName()).log(Level.SEVERE, "user could not be created.", ex);
        }
        onlineUsers.add(user);
    }

    /**
     * The user's uptime will be updated on the database and he will be removed
     * from the list. onlineUsers
     *
     * @param event event that is fired when a user goes offline on a server
     */
    @EventSubscriber
    public void onOnlineToOffline(PresenceUpdateEvent event) {
        long time = System.currentTimeMillis();
        for (User user : onlineUsers) {
            if (user.getId().equals(event.getUser().getID()) && user.getGuildID().equals(event.getGuild().getID()))//user on same server and same user as specified in event
            {
                user.setUptime(time - user.getLoginTime() + user.getUptime());//current time - time of login + overall time spent online
                try {
                    Main.userService.updateUser(user.getId(), user.getGuildID(), user.getUptime());
                } catch (SQLException ex) {
                    Logger.getLogger(UserListener.class.getName()).log(Level.SEVERE, "User could not be updated. ID: " + user.getId()
                            + ", Guild ID: " + user.getGuildID() + ", uptime: " + String.valueOf(user.getUptime()), ex);
                }
                break;
            }
        }
    }

    @EventSubscriber
    public void onGameChanged(GameChangeEvent event) {
        long time = System.currentTimeMillis();
        String game = event.getNewGame().orElse("None");
        for (User user : onlineUsers) {
            if (user.getId().equals(event.getUser().getID()) && user.getGuildID().equals(event.getGuild().getID()))//user on same server and same user as specified in event
            {
                
            }
        }
    }
}
