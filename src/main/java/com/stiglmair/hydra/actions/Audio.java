package com.stiglmair.hydra.actions;

import com.stiglmair.hydra.main.Main;
import com.stiglmair.hydra.objects.AudioObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.audio.AudioPlayer.Track;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * This class is designed to handle anything the bot wants to
 * say. Any Audiocommand should use this class.
 *w
 * @author Stiglmair
 */
public class Audio {

    /**
     * retrieves path to soundfile
     *
     * @param args the arguments that were sent in addition to the command
     * @param textChannel the TextChannel, where a potential error message can
     * be printed
     * @param voiceChannel Voice channel, in which user currently is
     * @param guild guild, in which the request was received
     * @return false, if the key does not exist or to many arguments were
     * received; true if the method could sucessfully execute the audio request
     */
    public static boolean handleSoundRequest(String[] args, IChannel textChannel, IVoiceChannel voiceChannel, IGuild guild) {
        if ((args.length == 1 && voiceChannel != null)) {
            String path = null;
            try {
                path = Main.soundService.getPath(args[0]);//retrieves path for requested file and increments the overall call counter
                Main.soundService.incrementRequestAmount(args[0]);
            } catch (SQLException ex) {
                Main.logger.error("Sound path could not be retrieved.", ex);
            }

            if (path != null) {//true, if requested sound exists in database, false otherwise
                Main.logger.info("The generated audio path was: {}", path);
                Main.audioListener.addAudio(path, voiceChannel, guild);
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * plays audio
     *
     * @param audioObject Object, which contains guild, voicechannel and path to
     * audiofile
     */
    public static void playSound(AudioObject audioObject) {
        AudioPlayer audioPlayer = AudioPlayer.getAudioPlayerForGuild(audioObject.getGuild());
        File file = new File(audioObject.getPath());
        try {
            audioObject.getVoiceChannel().join();
        } catch (MissingPermissionsException ex) {
            Main.logger.error("Could not join voice channel since the bot did not have the needed permissions.");
        }
        try {
            audioPlayer.setLoop(false);
            audioPlayer.setVolume(0.75f);
            Track currentTrack = audioPlayer.queue(file);
        } catch (IOException | UnsupportedAudioFileException ex) {
            Main.logger.error("Error while trying to play audio.", ex);
            audioObject.getVoiceChannel().leave();
            Main.audioListener.removeHead(); //removes current head of list to remove bad audioObject
        }
    }
}
