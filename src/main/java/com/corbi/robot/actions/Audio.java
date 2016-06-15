package com.corbi.robot.actions;

import com.corbi.robot.utilities.UtilityMethods;
import java.util.HashMap;
import java.util.Optional;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MissingPermissionsException;

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
    private static HashMap<String, String> Sounds = new HashMap<>();
    private static final String AUDIO_PATH = "/src/main/resources/AudioFiles/";

    /**
     *
     * @param args the arguments that were sent in addition to the command
     * @param optionalChannel the VoiceChannel, where the audio will be streamed
     * @param textChannel the TextChannel, where a potential error message can
     * be printed
     * @return false, if the key does not exist or to many arguments were
     * received; true if the method could sucessfully execute the audio
     * @throws DiscordException
     * @throws HTTP429Exception
     * @throws MissingPermissionsException
     */
    public static boolean handleSoundRequest(String[] args, Optional<IVoiceChannel> optionalChannel, IChannel textChannel) throws DiscordException, HTTP429Exception, MissingPermissionsException {
        Sounds.put("rko", AUDIO_PATH + "RKO.mp3");//TODO find good way to map keys to paths; find out how to do relative paths
        Sounds.put("cena", AUDIO_PATH + "John_Cena.mp3");
        Sounds.put("faker", AUDIO_PATH + "Faker_what_was_that.mp3");
        if (args.length == 1) {

            if (optionalChannel.isPresent()) { //true if user is in VoiceChannel, false otherwise
                IVoiceChannel voiceChannel = optionalChannel.get();
                String path = Sounds.get(args[0]);

                if (path != null) {//true, if requested sound exists in database, false otherwise

                    playSound(UtilityMethods.generatePath(path), voiceChannel);
                } else {
                    return false;
                }
            } else {
                Chat.sendMessage(textChannel, "You dumbo.... You might wanna join a voice channel first.");
            }
            return true;
        }
        return false;

    }

    /**
     *
     * @param path absolute path to the audio file
     * @param voiceChannel channel, where the audio will be streamed
     * @throws DiscordException
     */
    private static void playSound(String path, IVoiceChannel voiceChannel) throws DiscordException {
        voiceChannel.join();
        voiceChannel.getAudioChannel().queueFile(path);
    }
}
