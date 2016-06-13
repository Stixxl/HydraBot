/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.events;

import com.corbi.robot.actions.Chat;
import sx.blah.discord.api.EventSubscriber;
import sx.blah.discord.handle.impl.events.AudioStopEvent;

/**
 *
 * @author PogChamp
 * This class handles all audio events.
 */
public class AudioListener {
    /**
     * 
     * @param event event, that is fired, when the audi played by the bot has ended.
     * The Bot will leave the VoiceChannel after streaming the file
     */
    @EventSubscriber
    public void onAudioStopped(AudioStopEvent event)
    {
        event.getClient().getOurUser().getVoiceChannel().get().leave();//gets the bot user and makes him leave the VoiceChannel
    }
}
