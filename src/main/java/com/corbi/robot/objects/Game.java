/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.objects;

/**
 * represents a game that is being monitored by discord
 * @author PogChamp
 */
public class Game {
     private String title;
    private long startTime;
    private long overallTime;
    private int timesPlayed;
    public Game(String name, long overallTime, int timesPlayed) {
        this.title = name;
        this.overallTime = overallTime;
        this.timesPlayed = timesPlayed;
        this.startTime = System.currentTimeMillis();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getOverallTime() {
        return overallTime;
    }

    public void setOverallTime(long overallTime) {
        this.overallTime = overallTime;
    }

    public int getTimesPlayed() {
        return timesPlayed;
    }

    public void setTimesPlayed(int timesPlayed) {
        this.timesPlayed = timesPlayed;
    }

}
