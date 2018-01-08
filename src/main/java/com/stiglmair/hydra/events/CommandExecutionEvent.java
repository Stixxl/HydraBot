package com.stiglmair.hydra.events;

import com.stiglmair.hydra.objects.Command;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * This Event holds a parsed input. It should only be fired,
 * whenever a new textcommand adressed at our bot is received.
 *
 * @author PogChamp
 */
public class CommandExecutionEvent extends Event {

    private final IMessage message;
    private final String command;
    private final IUser by;
    private final String[] args;

    public CommandExecutionEvent(IMessage message, String command, IUser by, String[] args) {
        this.message = message;
        this.command = command;
        this.by = by;
        this.args = args;
    }

    public CommandExecutionEvent(Command command, IMessage message, IUser by) {
        this.message = message;
        this.by = by;
        this.command = command.getCommand();
        this.args = command.getArgs();
    }

    public String[] getArgs() {
        return args;
    }

    public IMessage getMessage() {
        return message;
    }

    public String getCommand() {
        return command;
    }

    public IUser getBy() {
        return by;
    }
}
