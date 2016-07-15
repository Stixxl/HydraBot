/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.objects;

import com.corbi.robot.utilities.UtilityMethods;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * identifies a user that ever was or is currently on the server
 *
 * @author PogChamp
 */
public class User {
    private String name;
    private long uptime;
    private String id;
    private String guildID;
    private String tier;
    private final long loginTime;
    private Game game;

    public User(long uptime, String id, String guildID, String name) {
        this.uptime = uptime;
        this.id = id;
        this.loginTime = System.currentTimeMillis();
        this.guildID = guildID;
        this.tier = calculateTier();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        String[] tiers = {"McShitsen", "DansGame", "Dödelbär", "MrPoppyButthole", "Fan Grill", "BabyRageBoy", "Beach Boy","Kazoo Kid", "Average Joe",
            "Quality Shit Poster", "Big City Kid", "Top Notch Memer", "Navy Seal", "Undercover agent working for bagool", "Bobby Ryan", "Korean", "PogChamp", "Person mit zuviel Zeit und zu wenig Privatleben", "Genji OTP"};
        long uptime_hours = uptime / 1000 / 60 / 60; //millseconds / 1000 = seconds / 60 = minutes / 60 = hours
        long linear_scaling_factor = 365 * 6 / tiers.length; // 365 * 6 hours is the estimate of the uptime of a power user in a year (6 hours a day online)
        return tiers[(int)Math.min(uptime /linear_scaling_factor, tiers.length - 1)]; //selects an according tier; if uptime_hours > 365 * 6 the highest availabe tier will be selected --> no ArrayOutOfBounds
        }
    @Override
    public String toString()
    {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
        Date loginDate = new Date(loginTime);
        return "Name: " + name + ", Uptime: *" + UtilityMethods.formatTime(uptime) + "*, Tier: **" + tier + "**";
    }
    }
