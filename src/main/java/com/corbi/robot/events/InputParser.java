/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.events;

import com.corbi.robot.actions.Audio;
import com.corbi.robot.actions.Chat;
import com.corbi.robot.main.Main;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MissingPermissionsException;

/**
 *
 * @author Stiglmair
 */
public class InputParser {

    static void parseInput(IMessage message) throws HTTP429Exception, MissingPermissionsException, DiscordException {
       String [] splitMessage = message.getContent().split(" ");
       if(splitMessage[0].equals("!hydra"))
       {
           switch(splitMessage[1]){
               case "daniel": Chat.insultDaniel(message.getChannel());
                               break;
               case "sounds": if(!(Audio.handleSoundRequest(splitMessage[2], message.getAuthor().getVoiceChannel(), message.getChannel())))
               {
                 Chat.showUnsupportedFormatMessage(splitMessage, 2, message.getChannel());
               }
                            break;
               default: Chat.showUnsupportedFormatMessage(splitMessage, 1, message.getChannel());                
           }
       }
       
    }
    
}
