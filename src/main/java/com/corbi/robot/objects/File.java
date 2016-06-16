/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.objects;

import sx.blah.discord.handle.obj.IChannel;

/**
 * supplies a file for playback
 * @author PogChamp
 */
public abstract class File {
private String key;
private String path;
private int amountPlayed;

public File(String key, String path, int amountPlayed)
{
    this.key = key;
    this.path = path;
    this.amountPlayed = amountPlayed;
}
/**
 * plays the file that is specified within the path
 * @param channel channel, where the audio is supposed to be played
 */
public void use(IChannel channel){
    
}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getAmountPlayed() {
        return amountPlayed;
    }

    public void setAmountPlayed(int amountPlayed) {
        this.amountPlayed = amountPlayed;
    }
}
