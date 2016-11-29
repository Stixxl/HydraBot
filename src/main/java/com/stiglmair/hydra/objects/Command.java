/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stiglmair.hydra.objects;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PogChamp
 */
public class Command {

    private String command;
    private String[] args;

    public Command(String command) {
        this.command = command;
    }

    public Command(String command, String[] args) {
        this.command = command;
        this.args = args;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    /**
     * Converts a String to a command object
     *
     * @param input input that will be parsed
     * @return new Command object; returns null if no input was given
     */
    public static Command parseCommand(String input) {
        String[] splitInput = input.split(" ");
        switch (splitInput.length) {
            case 0:
                Logger.getGlobal().log(Level.WARNING, "No Command was received.");
                return null;
            case 1:
                return new Command(splitInput[0]);
            default:
                String[] args = new String[splitInput.length - 1];
                for (int i = 0; i < args.length; i++) {
                    args[i] = splitInput[i + 1];
                }
                return new Command(splitInput[0], args);
        }
    }
}
