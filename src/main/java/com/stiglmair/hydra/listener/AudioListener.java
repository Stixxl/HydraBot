/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stiglmair.hydra.listener;

import com.stiglmair.hydra.actions.Audio;
import com.stiglmair.hydra.objects.AudioObject;
import java.util.concurrent.ConcurrentLinkedQueue;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.audio.events.TrackFinishEvent;
import sx.blah.discord.util.audio.events.TrackStartEvent;

/**
 *
 * @author PogChamp This class handles all audio events.
 */
public class AudioListener {

    /**
     *
     */
    private static final ConcurrentLinkedQueue<AudioObject> PLAYLIST = new ConcurrentLinkedQueue<>();

    /**
     * When track finishes playing, play another
     *
     * @param event event, that triggers, when audio finishes playing
     */
    @EventSubscriber
    public static void onAudioPlayed(TrackFinishEvent event) {
        PLAYLIST.poll();
        playSound();
    }
    @EventSubscriber
    public static void onAudioStarted(TrackStartEvent event)
    {
        System.out.println("Audio playback started.");
    }

    /**
     * adds audio to queue and plays it, if the queue was empty before
     * @param path path to audiofile
     * @param voiceChannel voicechannel in which audio should be played
     * @param guild guild of voicechannel
     */
    public static void addAudio(String path, IVoiceChannel voiceChannel, IGuild guild) {
        AudioObject playlistObject = new AudioObject(path, voiceChannel, guild);
        PLAYLIST.add(playlistObject);
        if (PLAYLIST.size() == 1) {
            playSound();
        }
    }

    /**
     * plays the next sound in the queue; does nothing if playlist is empty
     */
    public static void playSound() {
        if (!PLAYLIST.isEmpty()) {
            Audio.playSound(PLAYLIST.peek());
        }
    }

    public static void removeHead() {
        PLAYLIST.poll();
        playSound();
    }
}
