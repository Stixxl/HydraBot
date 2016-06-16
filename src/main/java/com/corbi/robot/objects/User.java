/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.objects;

import com.corbi.robot.main.Main;
import sx.blah.discord.handle.obj.IUser;

/**
 * identifies a user that ever was or is currently on the server
 *
 * @author PogChamp
 */
public class User {

    private long uptime;
    private String id;
    private String guildID;
    private final long loginTime;

    public User(long uptime, String id, String guildID) {
        this.uptime = uptime;
        this.id = id;
        this.loginTime = System.currentTimeMillis();
        this.guildID = guildID;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public String getGuildID() {
        return guildID;
    }

    public void setGuildID(String guildID) {
        this.guildID = guildID;
    }

    public long getUptime() {
        return uptime;
    }

    public void setUptime(long uptime) {
        this.uptime = uptime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
