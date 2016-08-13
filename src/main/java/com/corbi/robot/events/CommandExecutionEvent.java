/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.events;

import sx.blah.discord.api.events.Event;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * 
 * @author PogChamp
 * This Event holds a parsed input.
 * It should only be fired, whenever a new textcommand adressed at our bot is received
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
