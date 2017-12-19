package com.stiglmair.hydra.listener;

import com.stiglmair.hydra.events.CommandExecutionEvent;
import com.stiglmair.hydra.main.Main;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

/**
 * @author PogChamp
 */
public class CommandListener {

    public CommandListener() {

    }

    public CommandListener(String key) {
        this.key = key;
    }
    // This is the executor that we'll look for
    String key = "!hydra";

    /**
     * dispatches a CommandExecutionEvent whenever a message with member
     * variable key is received, otherwise does nothing
     *
     * @param event Event that is fired whenever a new message is sent
     */
    @EventSubscriber
    public void watchForCommands(MessageReceivedEvent event) {
        IMessage _message = event.getMessage();
        String _content = _message.getContent();

        if (!_content.startsWith(key)) { //this means the message was not meant for us
            return;
        }
        // a standard command looks like this: !hydra command param param...
        String[] _args = {}; // equals no arguments
        String _command = _content.split(" ")[1];
        if (_content.contains(" ")) {
            String temp = _content.substring(_content.indexOf(' ') + 1); //temp = command param param...
            if (temp.contains(" "))// true if there are any arguments, false otherwise
            {
                _args = temp.substring(temp.indexOf(' ') + 1).split(" ");//args = param param
            }
        }

        CommandExecutionEvent _event = new CommandExecutionEvent(_message, _command, _message.getAuthor(), _args);
        Main.client.getDispatcher().dispatch(_event);

    }

}
