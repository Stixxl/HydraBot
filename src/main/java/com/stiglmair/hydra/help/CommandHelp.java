package com.stiglmair.hydra.help;

/**
 * @author PogChamp
 */
public class CommandHelp {

    private final String name;
    private final String description;
    private CommandHelp[] subcommands = {}; // in command !hydra stats me; me is a subcommand

    public CommandHelp(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public CommandHelp(String name, String description, CommandHelp[] subcommands) {
        this.name = name;
        this.description = description;
        this.subcommands = subcommands;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        if (!(hasSubcommands())) {
            return description;
        }

        return description + "; besitzt weitere Unterbefehle.";
    }

    public CommandHelp[] getSubcommands() {
        return subcommands;
    }

    public void setSubcommands(CommandHelp[] subcommands) {
        this.subcommands = subcommands;
    }

    public boolean hasSubcommands() {
        return subcommands.length != 0;
    }

}
