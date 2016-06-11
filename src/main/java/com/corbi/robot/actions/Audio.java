package com.corbi.robot.actions;

import com.corbi.robot.main.Main;
import java.io.File;
import java.util.HashMap;
import java.util.Optional;
import sx.blah.discord.handle.AudioChannel;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Stiglmair
 */
public class Audio {
    private static HashMap<String, String> Sounds = new HashMap<>();
    
    public static boolean handleSoundRequest(String key, Optional<IVoiceChannel> optionalChannel, IChannel textChannel) throws DiscordException, HTTP429Exception, MissingPermissionsException
    {
     Sounds.put("rko", "AudioFiles/RKO.mp3");//TODO find good way to map keys to paths
     if(optionalChannel.isPresent())
     {
         IVoiceChannel voiceChannel = optionalChannel.get();
         String path = Sounds.get(key);
         if(path != null)
         {
         playSound(path, voiceChannel);
         }
         else
         {
            return false;
         }
     }
     else
     {
         new MessageBuilder(Main.client).withChannel(textChannel).withContent("You dumbo.... You might wanna join a voice channel first.").build();
     }
     return true;
    }
    private static void playSound(String path, IVoiceChannel voiceChannel) throws DiscordException
    {
         voiceChannel.join();
         AudioChannel audioChannel = voiceChannel.getAudioChannel();
         audioChannel.queueFile(new File(path));
    }
}
