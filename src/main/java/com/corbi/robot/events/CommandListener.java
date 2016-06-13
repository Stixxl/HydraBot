/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.events;

import com.corbi.robot.main.Main;
import sx.blah.discord.api.EventSubscriber;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

/**
 *
 * @author PogChamp
 */
public class CommandListener {

    // This is the executor that we'll look for
    final static String KEY = "!hydra";
    /**
     * 
     * @param event Event that is fired whenever a new message is sent
     */
    @EventSubscriber
    public void watchForCommands(MessageReceivedEvent event) {
        try {

            IMessage _message = event.getMessage();
            String _content = _message.getContent().toLowerCase();

            if (!_content.startsWith(KEY)){ //this means the message was not meant for us 
                return;
            }
            // a standard command looks like this: !hydra command param param...
            String[] _args = null;
            String _command = _content.split(" ")[1];
            if (_content.contains(" ")) {
                String temp = _content.substring(_content.indexOf(' ') + 1); //temp = command param param...
                _args = temp.substring(temp.indexOf(' ') + 1).split(" ");//args = param param
            }

            CommandExecutionEvent _event = new CommandExecutionEvent(_message, _command, _message.getAuthor(), _args);
            Main.client.getDispatcher().dispatch(_event);

        } catch (Exception ex) {
            // Handle how ever you please
        }
    }

}
