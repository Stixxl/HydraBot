/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.objects;

import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;

/**
 *
 * @author PogChamp
 */
public class Sound extends File {

    public Sound(String key, String path, int amountPlayed) {
        super(key, path, amountPlayed);
    }
    public void play(IVoiceChannel voiceChannel) throws DiscordException   
    {
        voiceChannel.join();
        voiceChannel.getAudioChannel().queueFile(getPath());
        setAmountPlayed(getAmountPlayed()+1);
    }
}
