package com.stiglmair.hydra.listener;

import com.stiglmair.hydra.actions.Audio;
import com.stiglmair.hydra.objects.AudioObject;
import java.util.concurrent.ConcurrentLinkedQueue;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.audio.events.TrackFinishEvent;

/**
 * @author PogChamp This class handles all audio events.
 */
public class AudioListener {

    private final ConcurrentLinkedQueue<AudioObject> playlist = new ConcurrentLinkedQueue<>();

    /**
     * When track finishes playing, play another
     *
     * @param event event, that triggers, when audio finishes playing
     */
    @EventSubscriber
    public void onAudioPlayed(TrackFinishEvent event) {
        playlist.poll();
        playSound();
    }

    /**
     * adds audio to queue and plays it, if the queue was empty before
     *
     * @param path path to audiofile
     * @param voiceChannel voicechannel in which audio should be played
     * @param guild guild of voicechannel
     */
    public void addAudio(String path, IVoiceChannel voiceChannel, IGuild guild) {
        AudioObject playlistObject = new AudioObject(path, voiceChannel, guild);
        playlist.add(playlistObject);
        if (playlist.size() == 1) {
            playSound();
        }
    }

    /**
     * plays the next sound in the queue; does nothing if playlist is empty
     */
    public void playSound() {
        if (!playlist.isEmpty()) {
            Audio.playSound(playlist.peek());
        }
    }

    public void removeHead() {
        playlist.poll();
        playSound();
    }
}
