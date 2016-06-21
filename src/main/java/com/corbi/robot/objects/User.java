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
    private String tier;
    private final long loginTime;
    private Game game;

    public User(long uptime, String id, String guildID) {
        this.uptime = uptime;
        this.id = id;
        this.loginTime = System.currentTimeMillis();
        this.guildID = guildID;
        this.tier = calculateTier();
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

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
    private String calculateTier()
    {
        String[] tiers = {"McShitsen", "Fan Grill", "BabyRageBoy", "Beach Boy","Kazoo Kid", "Average Joe",
            "Quality Shit Poster", "Major Block Hustler", "Top Notch Memer", "Navy Seal", "Undercover agent working for bagool", "Bobby Ryan", "Person mit zuviel Zeit und zu wenig Privatleben"};
        long uptime_hours = uptime / 1000 / 60 / 60; //millseconds / 1000 = seconds / 60 = minutes / 60 = hours
        return tiers[0];
        }
    }
