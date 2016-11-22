package stiglmair.com.hydra.actions;

import stiglmair.com.hydra.main.Main;
import stiglmair.com.hydra.utilities.UtilityMethods;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.UnsupportedAudioFileException;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.audio.AudioPlayer.Track;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Stiglmair This class is designed to handle anything the bot wants to
 * say. Any Audiocommand should use this class.
 */
public class Audio {

    /**
     * retrieves path to soundfile
     *
     * @param args the arguments that were sent in addition to the command
     * @param textChannel the TextChannel, where a potential error message can
     * be printed
     * @param voiceChannels channel in which the user is, only the first will be
     * used
     * @param guild guild, in which the request was received
     * @return false, if the key does not exist or to many arguments were
     * received; true if the method could sucessfully execute the audio request
     */
    public static boolean handleSoundRequest(String[] args, IChannel textChannel, List<IVoiceChannel> voiceChannels, IGuild guild) {
        if ((args.length == 1 && !voiceChannels.isEmpty())) {
            String path = null;
            IVoiceChannel voiceChannel = null;
            for (IVoiceChannel channel : voiceChannels) {
                if (channel.getGuild().equals(guild))//true if channel is in the same guild as origin of request, false otherwise
                {
                    Logger.getGlobal().log(Level.FINER, "VoiceChannel on correct server found.");
                    voiceChannel = channel;
                }
            }
            try {
                path = Main.soundService.getPath(args[0]);//retrieves path for requested file and increments the overall call counter
                Main.soundService.incrementRequestAmount(args[0]);
            } catch (SQLException ex) {
                Logger.getGlobal().log(Level.SEVERE, "Sound path could not be retrieved.", ex);
            }
            
            if (path != null && voiceChannel != null) {//true, if requested sound exists in database AND voiceChannel of user could be detected, false otherwise
                path = UtilityMethods.generatePath(path);
                Logger.getGlobal().log(Level.FINER, "The generated audio path was: {0}", path);
                playSound(path, voiceChannel, guild);
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * plays a soundfile
     *
     * @param path absolute path to the audio file
     * @param voiceChannel channel, where the audio will be streamed
     */
    private static void playSound(String path, IVoiceChannel voiceChannel, IGuild guild) {
        AudioPlayer audioPlayer = AudioPlayer.getAudioPlayerForGuild(guild);
        File file = new File(path);
        try {
            voiceChannel.join();
        } catch (MissingPermissionsException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Could not join voice channel since the bot did not have the needed permissions.");
        }
        try {
            audioPlayer.setLoop(false);
            audioPlayer.setVolume(0.75f);
            Track currentTrack = audioPlayer.queue(file);
        } catch (IOException | UnsupportedAudioFileException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Error while trying to play audio.", ex);
            voiceChannel.leave();
        }
    }
}
