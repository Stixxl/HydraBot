/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.actions;

import com.corbi.robot.main.Main;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;

/**
 *
 * @author Stiglmair
 */
public class Chat {
 public static void insultDaniel(IChannel channel) throws HTTP429Exception, DiscordException, MissingPermissionsException
 {
     new MessageBuilder(Main.client).withChannel(channel).withContent("Fact: Daniel hat es lediglich bis Platin 2 gebracht.").build();
 }
 public static void showUnsupportedFormatMessage(String[] wrongMessage,int errorAt, IChannel channel)
 {
     String errorInfo;
     //wrongMessage[1] refers to the command, wrongMessage[i > 1] refers to any given parameters, wrongMessage[0] = !hydra
     if(errorAt > 1)
     {
     errorInfo = "The HydraBot does not support the parameter " + wrongMessage[errorAt]
             + " for the command " + wrongMessage[1] + ".";
     }
     else if(errorAt == 1)
     {
         errorInfo = "The HydraBot does not support the command " + wrongMessage[1] + ".";
     }
     else
     {
         errorInfo = "The HydraBot could not parse the input.";
     }
     new MessageBuilder(Main.client).withChannel(channel).withContent(errorInfo);
 }
}
