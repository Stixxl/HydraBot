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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles db requests for the table GAMES
 *
 * @author PogChamp
 */
public class GameService {

    private final String TABLENAME;
    private final Connection con;

    protected GameService(String DBNAME, Connection con) {
        this.TABLENAME = DBNAME + ".GAMES";
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

        PreparedStatement statement = con.prepareStatement("INSERT INTO " + TABLENAME
                + " values('" + title + "', '" + id + "', '" + guildID + "', 0, 1)");
        DBService.execute(statement);
        statement.close();
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
        PreparedStatement statement = con.prepareStatement("Select * FROM " + TABLENAME
                + " WHERE title=? "
                + "AND id=? "
                + "AND guild_id=?");

        statement.setString(1, title);
        statement.setString(2, id);
        statement.setString(3, guildID);
        ResultSet result = statement.executeQuery();
        if (result.next()) {

            statement.close();
            return new Game(title, result.getBigDecimal("time_played").longValue(), result.getInt("amount_played"));
        }

        statement.close();
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

        PreparedStatement statement = con.prepareStatement("SELECT * FROM" + TABLENAME
                + " WHERE title =? "
                + "AND id=?"
                + "AND guild_id=?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        statement.setString(1, title);
        statement.setString(2, id);
        statement.setString(3, guildID);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            result.updateInt("amount_played", amountPlayed);
            result.updateBigDecimal("time_played", BigDecimal.valueOf(timePlayed));
            result.updateRow();

            statement.close();
            return new Game(title, timePlayed, amountPlayed);
        }

        statement.close();
        return null;
    }

    /**
     * requests all games from the server for a given user
     *
     * @param id id of the user
     * @param guildID server id from which the request is sent
     * @return a list of games that are associated with the user; null if there
     * are none
     * @throws SQLException
     */
    public List<Game> getGames(String id, String guildID) throws SQLException {
        List<Game> games = new ArrayList<>();
        PreparedStatement statement = con.prepareStatement("SELECT * FROM " + TABLENAME
                + " WHERE id=? "
                + "AND guild_id=? "
                + "ORDER BY time_played DESC");
        statement.setString(1, id);
        statement.setString(2, guildID);
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            int amount_played = result.getInt("amount_played");
            long time_played = result.getBigDecimal("time_played").longValue();
            String title = result.getString("title");
            games.add(new Game(title, time_played, amount_played));
        }
        statement.close();
        return games;
    }

    /**
     * returns a list of games for all users
     *
     * @param guildID server id from which the request is sent
     * @return a summarized list of games
     * @throws SQLException
     */
    public List<Game> getGamesAll(String guildID) throws SQLException {
        List<Game> games = new ArrayList<>();
        PreparedStatement statement = con.prepareStatement("SELECT DISTINCT title, ua1.amount_played_all, ua2.time_played_all FROM "
                + TABLENAME + ", "
                + "(SELECT SUM(amount_played) AS amount_played_all, title t FROM " + TABLENAME
                + " WHERE guild_id=? "
                + "GROUP BY title) AS ua1, "
                + "(SELECT SUM(time_played) AS time_played_all, title t FROM " + TABLENAME
                + " WHERE guild_id=? "
                + "GROUP BY title) as ua2 "
                + "WHERE title=ua1.t "
                + "AND title=ua2.t");
        statement.setString(1, guildID);
        statement.setString(2, guildID);
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            int amount_played = result.getInt("AMOUNT_PLAYED_ALL");
            long time_played = result.getBigDecimal("TIME_PLAYED_ALL").longValue();
            String title = result.getString("TITLE");
            games.add(new Game(title, time_played, amount_played));
        }
        statement.close();
        return games;
    }
}
