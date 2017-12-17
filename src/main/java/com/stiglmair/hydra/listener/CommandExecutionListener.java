package com.stiglmair.hydra.listener;

import com.stiglmair.hydra.actions.Audio;
import com.stiglmair.hydra.actions.Chat;
import com.stiglmair.hydra.main.Main;
import com.stiglmair.hydra.objects.User;
import com.stiglmair.hydra.security.Role;
import com.stiglmair.hydra.utilities.UtilityMethods;

import com.stiglmair.hydra.events.CommandExecutionEvent;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.obj.IChannel;

/**
 * @author PogChamp
 */
public class CommandExecutionListener {

    private boolean isPaused = false;

    /**
     * @param event event thatis thrown when a new command is received
     */
    @EventSubscriber
    public void handle(CommandExecutionEvent event) {
        String command = event.getCommand();
        String args[] = event.getArgs();
        IChannel textChannel = event.getMessage().getChannel();
        String userId = String.valueOf(event.getBy().getLongID());
        if (!isPaused) {
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
                    if (!(Audio.handleSoundRequest(args, textChannel, event.getBy().getVoiceStateForGuild(event.getMessage().getGuild()).getChannel(), event.getMessage().getGuild()))) {
                        Chat.showUnsupportedFormatMessage(command, args, textChannel);
                    }
                    break;
                //statistics
                case "stats":
                    if (!(Chat.showStats(textChannel, String.valueOf(event.getBy().getLongID()), args))) {
                        Chat.showUnsupportedFormatMessage(command, args, textChannel);
                    }
                    break;
                //help menu
                case "help":
                    Chat.showHelp(textChannel, args);
                    break;
                // pause the bot, so he does not react to events
                case "pause":
                    if (Role.authorize(event.getBy(), textChannel.getGuild(), Role.ROLE.ADMIN)) {
                        isPaused = true;
                        Chat.sendMessage(textChannel, "Gute Nacht, Kinder. Wecke mich wieder auf mit einem liebevollen " + UtilityMethods.highlightItalic("!hydra unpause") + ".");
                    } else {
                        Chat.showUnauthorizedMessage(textChannel);
                    }
                    break;
                case "token":
                    handleTokenCommand(userId, command, args, textChannel);
                    break;
                default:
                    Chat.showUnsupportedFormatMessage(command, event.getMessage().getChannel());// no suitable command found
            }
            //let bot resume his business
        } else if (command.equals("unpause") && Role.authorize(event.getBy(), textChannel.getGuild(), Role.ROLE.ADMIN)) {
            isPaused = false;
            Chat.sendMessage(textChannel, "READY TO RUMBLE!");
        }
    }

    private void handleTokenCommand(String userId, String command, String[] args, IChannel textChannel) {
        User user;
        try {
            user = Main.userService.getUser(userId);
            if (user == null) {
                throw new NullPointerException("No user returned from UserService.getUser()");
            }
        } catch (java.sql.SQLException | NullPointerException e) {
            Main.logger.error("Error while retrieving user with ID " + userId, e);
            Chat.sendMessage(textChannel, "Sorry, do I know you? " + userId);
            return;
        }

        if (args.length != 1) {
            Chat.showUnsupportedFormatMessage(command, args, textChannel);
            return;
        }

        if (!textChannel.isPrivate()) {
            Chat.sendMessage(textChannel, "Send me a private message, senpai.");
            return;
        }

        if (args[0].equals("show")) {
            String token = user.getApiToken();
            if (token == null) {
                Chat.sendMessage(textChannel, "No token.");
            }
            else {
                Chat.sendMessage(textChannel, "Your token is " + token);
            }
        }
        else if (args[0].equals("new")) {
            String token = user.newApiToken();
            if (token == null) {
                Chat.sendMessage(textChannel, "Token could not be created.");
            }
            else {
                user.save();
                Chat.sendMessage(textChannel, "New token is " + token);
            }
        }
        else if (args[0].equals("revoke")) {
            if (user.getApiToken() == null) {
                Chat.sendMessage(textChannel, "No token.");
            }
            else {
                user.revokeApiToken();
                user.save();
                Chat.sendMessage(textChannel, "Token revoked.");
            }
        }
        else {
            Chat.sendMessage(textChannel, "Unsupported command: " + args[0]);
        }
    }

}
