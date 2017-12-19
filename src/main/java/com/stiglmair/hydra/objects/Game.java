package com.stiglmair.hydra.objects;

import com.stiglmair.hydra.utilities.UtilityMethods;

/**
 * Represents a game that is being monitored by discord.
 *
 * @author PogChamp
 */
public class Game {

    private String title;
    private long startTime;
    private long time_played;
    private int amount_played;

    public Game(String name, long overallTime, int timesPlayed) {
        this.title = name;
        this.time_played = overallTime;
        this.amount_played = timesPlayed;
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

    public long getTime_played() {
        return time_played;
    }

    public void setTime_played(long time_played) {
        this.time_played = time_played;
    }

    public int getAmount_played() {
        return amount_played;
    }

    public void setAmount_played(int amount_played) {
        this.amount_played = amount_played;
    }

    @Override
    public String toString() {
        return "__" + title + "__ - Zeit gespielt: *" + UtilityMethods.formatTime(time_played) + "*; Anzahl Aufrufe: " + String.valueOf(amount_played);
    }

}
