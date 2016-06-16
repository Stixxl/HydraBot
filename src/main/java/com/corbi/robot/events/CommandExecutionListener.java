/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.events;

import com.corbi.robot.actions.Audio;
import com.corbi.robot.actions.Chat;
import java.util.Optional;
import sx.blah.discord.api.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MissingPermissionsException;

/**
 *
 * @author Stiglmair
 */
public class CommandExecutionListener {

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        Optional<String> game = Optional.of("mit der Mumu deiner Mama");
        event.getClient().updatePresence(false, game);
    }

    /**
     * @param event event thatis thrown when a new command is received
     * @throws HTTP429Exception
     * @throws DiscordException
     * @throws MissingPermissionsException This method maps a command received
     * through the chat to a suited method
     */
    @EventSubscriber
    public void handle(CommandExecutionEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException {
        String command = event.getCommand();
        String args[] = event.getArgs();
        IChannel textChannel = event.getMessage().getChannel();
        switch (command) {
            //chat
            case "daniel":
                Chat.insultDaniel(textChannel);
                break;
            case "noah":
                Chat.tellBinsenweisheit(textChannel);
                break;
            //sounds
            case "sounds":
                if (!(Audio.handleSoundRequest(args, event.getBy().getVoiceChannel(), textChannel))) {
                    Chat.showUnsupportedFormatMessage(command, args, textChannel);
                }
            case "stats":
                if (!(Chat.showStats(textChannel, event.getBy(), event.getMessage().getGuild().getID(), args))) {
                    Chat.showUnsupportedFormatMessage(command, args, textChannel);
                }
                break;

            default:
                Chat.showUnsupportedFormatMessage(command, event.getMessage().getChannel());// no suitable command found
        }
    }
}
