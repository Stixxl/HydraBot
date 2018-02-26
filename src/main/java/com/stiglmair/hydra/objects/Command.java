package com.stiglmair.hydra.objects;

import com.stiglmair.hydra.main.Main;

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
                Main.logger.warn("No Command was received.");
                return null;
            case 1:
                return new Command(splitInput[0]);
            default:
                String[] args = new String[splitInput.length - 1];
                System.arraycopy(splitInput, 1, args, 0, args.length);
                return new Command(splitInput[0], args);
        }
    }
}
