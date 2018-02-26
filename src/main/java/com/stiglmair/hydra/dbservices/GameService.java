package com.stiglmair.hydra.dbservices;

import com.stiglmair.hydra.main.Main;
import com.stiglmair.hydra.objects.Game;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


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
     * @param title title of the game that is being played
     * @param id id of the user
     * @return the newly created Game
     * @throws java.sql.SQLException
     */
    public Game createGame(String title, String id) throws SQLException {
        String sql = "INSERT INTO " + TABLENAME
                + "(title, id, time_played, amount_played) values('" + title + "', '" + id + "', 0, 1)";
        Main.logger.info("Create Game: {}", sql);
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            DBService.execute(statement);
        }
        return new Game(title, 0, 0);
    }

    /**
     * @param title of the game that is requested
     * @param id id of the user
     * @return the requested Game; null if game wasnt found
     * @throws java.sql.SQLException
     */
    public Game getGame(String title, String id) throws SQLException {
        Game game = null;
        try (PreparedStatement statement = con.prepareStatement("Select time_played, amount_played FROM " + TABLENAME
                + " WHERE title=? "
                + "AND id=? ")) {
            statement.setString(1, title);
            statement.setString(2, id);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                game = new Game(title, result.getBigDecimal("time_played").longValue(), result.getInt("amount_played"));
            }
            statement.close();
        }
        return game;
    }

    /**
     * @param title the title of the game that will be updated
     * @param id id of the user
     * @param amountPlayed the amount of times the game has been played
     * @param timePlayed the overall time that was spent on the game by the user
     * @return the updated Game; null if game wasnt found
     * @throws java.sql.SQLException
     */
    public Game updateGame(String title, String id, int amountPlayed, long timePlayed) throws SQLException {

        try (PreparedStatement statement = con.prepareStatement("UPDATE " + TABLENAME
                + " SET amount_played=?, "
                + "time_played=? "
                + " WHERE title =? "
                + "AND id=?")) {
            statement.setInt(1, amountPlayed);
            statement.setLong(2, timePlayed);
            statement.setString(3, title);
            statement.setString(4, id);
            statement.execute();
            statement.close();
        }
        return new Game(title, timePlayed, amountPlayed);
    }

    /**
     * requests all games from the server for a given user
     *
     * @param id id of the user
     * @return a list of games that are associated with the user; null if there
     * are none
     * @throws SQLException
     */
    public List<Game> getGames(String id) throws SQLException {
        List<Game> games = new ArrayList<>();
        try (PreparedStatement statement = con.prepareStatement("SELECT amount_played, time_played, title FROM " + TABLENAME
                + " WHERE id=? "
                + "ORDER BY time_played DESC")) {
            statement.setString(1, id);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                int amount_played = result.getInt("amount_played");
                long time_played = result.getBigDecimal("time_played").longValue();
                String title = result.getString("title");
                games.add(new Game(title, time_played, amount_played));
            }
            statement.close();
        }
        return games;
    }

    /**
     * returns a list of games for all users
     *
     * @return a summarized list of games
     * @throws SQLException
     */
    public List<Game> getGamesAll() throws SQLException {
        List<Game> games = new ArrayList<>();
        try (PreparedStatement statement = con.prepareStatement("SELECT DISTINCT title, ua1.amount_played_all amount_played_all, ua2.time_played_all time_played_all FROM "
                + TABLENAME + ", "
                + "(SELECT SUM(amount_played) AS amount_played_all, title t FROM " + TABLENAME
                + " GROUP BY title) AS ua1, "
                + "(SELECT SUM(time_played) AS time_played_all, title t FROM " + TABLENAME
                + " GROUP BY title) as ua2 "
                + "WHERE title=ua1.t "
                + "AND title=ua2.t "
                + "ORDER BY ua2.time_played_all DESC")) {
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                int amount_played = result.getInt("amount_played_all");
                long time_played = result.getBigDecimal("time_played_all").longValue();
                String title = result.getString("title");
                Game temp = new Game(title, time_played, amount_played);
                games.add(temp);
            }
            statement.close();
        }
        return games;
    }
}
