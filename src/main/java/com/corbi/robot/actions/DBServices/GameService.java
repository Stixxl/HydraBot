/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.actions.DBServices;

import com.corbi.robot.objects.Game;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles db requests for the table GAMES
 * @author PogChamp
 */
public class GameService {

    private final String DBNAME;
    private final Connection con;

    protected GameService(String DBNAME, Connection con) {
        this.DBNAME = DBNAME + ".GAMES";
        this.con = con;
    }
    /**
     * 
     * @param title title of the game that is being played
     * @param id id of the user
     * @param guildID server that the user is online on
     * @return the newly created Game
     */
    public Game createGame(String title, String id, String guildID) throws SQLException {

            PreparedStatement statement = con.prepareStatement("INSERT INTO " + DBNAME
                    + " values('" + title + "', '" + id + "', '" + guildID + "', 0, 0");
            DBService.execute(statement);

        return new Game(title, 0, 0);
    }
    /**
     * 
     * @param title of the game that is requested
     * @param id id of the user
     * @param guildID server that the user is online on
     * @return the requested Game; null if game wasnt found
     */
    public Game getGame(String title, String id, String guildID) throws SQLException {
        PreparedStatement statement = con.prepareStatement("Select * FROM " + DBNAME
                    + " WHERE TITLE=? "
                    + "AND ID=? "
                    + "AND GUILD_ID=?");

            statement.setString(1, title);
            statement.setString(2, id);
            statement.setString(3, guildID);
            ResultSet result = statement.executeQuery();
            if (result.next()) {

                return new Game(title, result.getBigDecimal("TIME_PLAYED").longValue(), result.getInt("AMOUNT_PLAYED"));
            }

        return null;
    }
    /**
     * 
     * @param title the title of the game that will be updated
     * @param id id of the user
     * @param guildID server that the user is online on
     * @param amountPlayed the amount of times the game has been played
     * @param timePlayed the overall time that was spent on the game by the user
     * @return the updated Game; null if game wasnt found
     */
    public Game updateGame(String title, String id, String guildID, int amountPlayed, long timePlayed) throws SQLException {

            PreparedStatement statement = con.prepareStatement(
                    "SELECT * FROM" + DBNAME
                            + " WHERE TITLE =? "
                            + "AND ID=?"
                            + "AND GUILD_ID=?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, title);
            statement.setString(2, id);
            statement.setString(3, guildID);
            ResultSet result = statement.executeQuery();
            if(result.next()){
                result.updateInt("AMOUNT_PLAYED", amountPlayed);
                result.updateBigDecimal("TIME_PLAYED", BigDecimal.valueOf(timePlayed));
                result.updateRow();
                return new Game(title, timePlayed, amountPlayed);
            }

        return null;
    }
}
