package com.stiglmair.hydra.dbservices;

import com.stiglmair.hydra.help.CommandHelp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * a class, that delivers path to soundfiles and takes care of the sound table
 *
 * @author Stiglmair
 */
public class SoundService {

    private final String TABLENAME;
    private final Connection con;

    protected SoundService(String DBNAME, Connection con) {
        TABLENAME = DBNAME + ".SOUNDS";
        this.con = con;
    }

    /**
     * returns a path to a requested soundfile
     *
     * @param name the argument that is given in order to play the sound
     * @return the path to the requested soundfile
     * @throws SQLException
     */
    public String getPath(String name) throws SQLException {
        String path;
        try (PreparedStatement statement = con.prepareStatement("SELECT path FROM " + TABLENAME
                + " WHERE name=?")) {
            statement.setString(1, name);
            ResultSet result = statement.executeQuery();
            //returns path, if it exists; otherwise throws SQLException
            result.next();
            path = result.getString("path");
        }
        return path;

    }

    /**
     * increments the total amount of requests for a soundfile
     *
     * @param name identifier for a soundfile
     * @throws SQLException
     */
    public void incrementRequestAmount(String name) throws SQLException {
        PreparedStatement statement = con.prepareStatement("UPDATE " + TABLENAME
                + " SET amount_requests = amount_requests + 1"
                + "WHERE name=?");
        statement.setString(1, name);
        DBService.execute(statement);
    }

    /**
     * Updates or creates a sound with the specified information.
     *
     * @param name The name of the sound.
     * @param path The path to the sound file.
     * @param description The sound description.
     * @throws SQLException
     */
    public void updateOrCreateSound(String name, String path, String description) throws SQLException {
        if (description == null) {
            description = "";
        }

        PreparedStatement statement = con.prepareStatement("UPDATE " + TABLENAME + " SET path=?, description=? WHERE name=?");
        statement.setString(1, path);
        statement.setString(2, description);
        statement.setString(3, name);
        DBService.execute(statement);

        statement = con.prepareStatement(
            "INSERT INTO " + TABLENAME + " (name, path, description, amount_requests) " +
            "SELECT ?, ?, ?, 0 " +
            "WHERE NOT EXISTS (SELECT 1 FROM " + TABLENAME + " WHERE name=?)");
        statement.setString(1, name);
        statement.setString(2, path);
        statement.setString(3, description);
        statement.setString(4, name);
        DBService.execute(statement);
    }

    /**
     * Retrieves name and description for sound commands from the database and
     * orders them alphabetically
     *
     * @return an Array containing CommandHelp objects(that do not contain
     * subcommands) for the command sounds
     * @throws SQLException
     */
    public CommandHelp[] getCommandHelp() throws SQLException {
        List<CommandHelp> results = new ArrayList<>();
        CommandHelp[] commandHelp;
        try (PreparedStatement statement = con.prepareStatement("SELECT name,description FROM " + TABLENAME + " ORDER BY name ASC")) {
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                results.add(new CommandHelp(result.getString("name"), result.getString("description")));
            }   commandHelp = results.toArray(new CommandHelp[results.size()]);
        }
        return commandHelp;
    }

}
