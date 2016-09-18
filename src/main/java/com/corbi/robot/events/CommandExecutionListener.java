/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.events;

import com.corbi.robot.actions.Audio;
import com.corbi.robot.actions.Chat;
import com.corbi.robot.main.Main;
import com.corbi.robot.objects.User;
import java.util.logging.Level;
import java.util.logging.Logger;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

/**
 *
 * @author Stiglmair
 */
public class CommandExecutionListener {

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        try {
            Main.client.changeUsername("Süßwasserpolyp");
        } catch (DiscordException | RateLimitException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Error while setting bot's username.", ex);
        }
        event.getClient().changeStatus(Status.game("mit der Mumu deiner Mama")); //sets the game of the bot

        for (IGuild guild : event.getClient().getGuilds()) {
            Logger.getGlobal().log(Level.FINER, "bot is online on guild{0}", guild.toString());
            for (IUser user : User.getOnlineUsers(guild.getUsers())) {
                Main.userListener.addOnlineUser(user.getID(), user.getName());//adds every user that is online, when the bot started, to the onlineUser list
            }
        }
    }

    /**
     * @param event event thatis thrown when a new command is received
     */
    @EventSubscriber
    public void handle(CommandExecutionEvent event) {
        String command = event.getCommand();
        String args[] = event.getArgs();
        IChannel textChannel = event.getMessage().getChannel();

        switch (command) {
            //chat
            case "daniel":
                Chat.insultDaniel(textChannel);
                break;
            case "binsenweisheit":
                Chat.tellBinsenweisheit(textChannel);
                break;
            //sounds
            case "sounds":
                if (!(Audio.handleSoundRequest(args, textChannel, event.getBy().getConnectedVoiceChannels(), event.getMessage().getGuild()))) {
                    Chat.showUnsupportedFormatMessage(command, args, textChannel);
                }
                break;
            //statistics
            case "stats":
                if (!(Chat.showStats(textChannel, event.getBy().getID(), event.getMessage().getGuild().getID(), args))) {
                    Chat.showUnsupportedFormatMessage(command, args, textChannel);
                }
                break;
            //help menu
            case "help":
                Chat.showHelp(textChannel, args);
                break;
            default:
                Chat.showUnsupportedFormatMessage(command, event.getMessage().getChannel());// no suitable command found
        }
    }
}
