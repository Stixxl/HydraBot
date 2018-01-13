package com.stiglmair.hydra.help;

import com.stiglmair.hydra.main.Main;
import com.stiglmair.hydra.utilities.UtilityMethods;
import java.sql.SQLException;


/**
 * @author PogChamp
 */
public class Help {

    //command stats
    CommandHelp stats = new CommandHelp("stats", "Zeigt wer den längsten hat und wie lang er eigentlich ist.", new CommandHelp[]{
        new CommandHelp("me", "Gibt deine eigenen Stats aus."),
        new CommandHelp("ranking", "Wählt die besten der besten aus. Gib eine Zahl an um die besten n Personen auszuwählen, etwa so: " + UtilityMethods.highlightItalic("!hydra stats ranking 3")),
        new CommandHelp("name", "Wählt eine Person nach Namen aus, etwa so: " + UtilityMethods.highlightItalic("!hydra stats name Mr. Poopy Butthole")),
        new CommandHelp("all", "FUSION! GENKIDAMA; Kombiniert eure Kraft. Gibt die kombinierten Stats von euch an."),
        new CommandHelp("save", "Speichert eure Daten, falls der Bot wieder mal abstürzt. FeelsBadMan")});
    CommandHelp sounds = new CommandHelp("sounds", "Spielt danke Memes ab.");
    CommandHelp binsenweissheiten = new CommandHelp("binsenweisheit", "Gibt Tipps und Tricks fürs Leben.");
    CommandHelp daniel = new CommandHelp("daniel", "Flamt Daniel.");
    CommandHelp pause = new CommandHelp("pause", "Unterbricht die Ausführung des Bots. Benötigt Adminrechte. Anwendungsfall: Sayan wird wieder gespammt.");
    CommandHelp unpause = new CommandHelp("unpause", "Setzt die Ausführung des Bots fort. Benötigt Adminrechte. Anwendungsfall: Sayan soll wieder gespammt werden können.");
    CommandHelp token = new CommandHelp("token", "Generiert einen Token, um Sayan noch effizienter per REST zu spammen. Dieser Befehl muss als Privatnachricht an den Bot gesendet werden. Die URL lautet " + UtilityMethods.highlightBold("https://hydra.stiglmair.com/commands?command=sounds%20sayan&youruserId=userid&token=generated_token") + ".", new CommandHelp[]{
	    new CommandHelp("new", "Generiert einen neuen Token."),
	    new CommandHelp("show", "Zeigt das bereits generierte Token an."),
	    new CommandHelp("revoke", "Löscht das Token.")});
    CommandHelp[] commandMans = {stats, sounds, token, binsenweissheiten, daniel, pause, unpause};

    public Help() {
        try {
            sounds.setSubcommands(Main.soundService.getCommandHelp());
        } catch (SQLException ex) {
            Main.logger.error("Error occured while trying to retrieve the help menu for sounds.", ex);
        }
    }

    /**
     *
     * @return a String containing all high level (meaning commands directly
     * after !hydra) commands
     */
    public String showHelp() {
        StringBuilder sb = new StringBuilder("Folgende Befehle existieren:");
        for (CommandHelp help : commandMans) {
            sb.append(System.lineSeparator()).append(formatHelp(help));
        }
        return sb.toString();
    }

    /**
     * returns a String for the queried command; calls showHelpRecursively,
     * basically just a utility method to call said function with i=0
     *
     * @param args contains parts of the queried command
     * @return a String with Information about the queried command, aswell as
     * its direct subcommands
     */
    public String showHelp(String[] args) {
        try {
            sounds.setSubcommands(Main.soundService.getCommandHelp());
        } catch (SQLException ex) {
            Main.logger.error("Error occured while trying to retrieve the help menu for sounds.", ex);
        }
        for (CommandHelp help : commandMans) {
            if (help.getName().equals(args[0])) {//true if the specified high level command by the user exists; false otherwise
                return showHelpRecursively(args, help, 1);
            }
        }
        return "Netter Versuch, aber den Befehl gibts nicht.";
    }

    /**
     * looks for a given command and returns a String if it exists
     *
     * @param args the command chain
     * @param help the command which is currently being processed
     * @param i points to the next argument
     * @return a String which gives information about the queried command; if
     * the command does not exist it will instead return an error message
     */
    private String showHelpRecursively(String[] args, CommandHelp help, int i) {
        CommandHelp[] subcommands = help.getSubcommands();
        if (args.length == i) {// we reached the end of the command chain and want to give information about the current command
            StringBuilder sb = new StringBuilder(formatHelp(help));
            if (help.hasSubcommands()) {
                sb.append(System.lineSeparator()).append("Der Befehl besitzt folgende Unterbefehle: ");
                for (CommandHelp subhelp : subcommands) {
                    sb.append(System.lineSeparator()).append(formatHelp(subhelp));
                }
            }
            return sb.toString();
        } else {
            for (CommandHelp subhelp : subcommands) { //calls itself if and only if a subcommand with the specified name exists
                if (subhelp.getName().equals(args[i])) {
                    return showHelpRecursively(args, subhelp, ++i); // we move on to the next command in the chain
                }
            }
            //no command with the name exists
            return "Ne, Ne , Ne, das sagt mir so garnix. Dir ist klar, dass du nur Befehle eingeben solltest, die auch existieren...";
        }
    }

    /**
     * formats a CommandHelp Object like this: <b>name</b> - description
     *
     * @param help the help for an command, which should be formatted
     * @return a formatted String for a given help of a command
     */
    private String formatHelp(CommandHelp help) {
        return UtilityMethods.highlightBold(help.getName()) + " - " + help.getDescription();
    }
}
