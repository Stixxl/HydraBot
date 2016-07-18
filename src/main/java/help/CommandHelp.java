/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package help;

import com.corbi.robot.utilities.UtilityMethods;

/**
 *
 * @author PogChamp
 */
public class CommandHelp {

    private String name;
    private String description;
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

    public boolean hasSubcommands() {
        return subcommands.length != 0;
    }

}
