/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.events;

import com.corbi.robot.main.Main;
import com.corbi.robot.objects.User;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sx.blah.discord.api.EventSubscriber;
import sx.blah.discord.handle.impl.events.UserJoinEvent;
import sx.blah.discord.handle.impl.events.UserLeaveEvent;

/**
 * This class listens to events concerning users
 * @author PogChamp
 */
public class UserListener {
    
    public List<User> onlineUsers = new ArrayList<>();
    /**
     * A user will be created and added to the list onlineUsers
     * @param event event that is fired when a user joins a server
     */
    @EventSubscriber
    public void onUserJoin(UserJoinEvent event)
    {
        String userID = event.getUser().getID();
        String guildID = event.getGuild().getID();
        User user = new User(0, userID, guildID);
        Main.dbService.createUser(userID, guildID);
        onlineUsers.add(user);
    }
    /**
     * The user's uptime will be updated and he will be removed from the list onlineUsers
     * @param event event that is fired when a user leaves a server
     */
    @EventSubscriber
    public void onUserLeave(UserLeaveEvent event)
    {
        long time = System.currentTimeMillis();
        for (User user : onlineUsers) {
            if(user.getIUser().equals(event.getUser()) && user.getGuildID().equals(event.getGuild()))//user on same server and same user as specified in event
            {
                user.setUptime(time - user.getLoginTime() + user.getUptime());//current time - time of login + overall time spent online
                onlineUsers.remove(user);
                break;
            } else {
            }
                }
    }
    
}
