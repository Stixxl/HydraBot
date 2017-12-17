package com.stiglmair.hydra.objects;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;

/**
 * @author Danielus Creates objects for playlist
 */
public class AudioObject {

    private final String path;
    private final IVoiceChannel voiceChannel;
    private final IGuild guild;

    public AudioObject(String path, IVoiceChannel voiceChannel, IGuild guild) {
        this.path = path;
        this.voiceChannel = voiceChannel;
        this.guild = guild;
    }

    public String getPath() {
        return this.path;
    }

    public IVoiceChannel getVoiceChannel() {
        return this.voiceChannel;
    }

    public IGuild getGuild() {
        return this.guild;
    }
}