/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.events;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.util.audio.events.AudioPlayerEvent;

/**
 *
 * @author PogChamp This class handles all audio events.
 */
public class AudioListener {

    @EventSubscriber
    public void onAudioPlayed(AudioPlayerEvent event) {
    }
}
