package com.stiglmair.hydra.dbservices;

import com.stiglmair.hydra.main.Main;
import com.stiglmair.hydra.objects.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author PogChamp
 */
public class UserService {

    private final String TABLENAME;
    private final Connection con;

    protected UserService(String DBNAME, Connection con) {
        this.TABLENAME = DBNAME + ".USERS";
        this.con = con;
    }

    /**
     * Inserts a new user in the database
     *
     * @param id the id of the user
     * @param name name of user as visible for users
     * @return the newly created user
     * @throws java.sql.SQLException
     */
    public User createUser(String id, String name) throws SQLException {
        PreparedStatement statement = con.prepareStatement("insert into " + TABLENAME
                + "(id, uptime, name, apitoken) values(?, 0, ?, null)");
        statement.setString(1, id);
        statement.setString(2, name);
        DBService.execute(statement);
        return new User(0, id, name, null);
    }

    /**
     *
     * @param id the userId in discord
     * @param name name of the user
     * @param uptime The overall time that was spent on the server
     * @throws SQLException
     */
    public void updateUser(String id, String name, String apiToken, long uptime) throws SQLException {
        PreparedStatement statement = con.prepareStatement(
                "UPDATE " + TABLENAME
                + " SET uptime=?, name=?, apitoken=?"
                + " WHERE id=?");
        //sets parameter in above statement
        statement.setLong(1, uptime);
        statement.setString(2, name);
        statement.setString(3, apiToken);
        statement.setString(4, id);
        DBService.execute(statement);
    }

    /**
     * retrieves a user from the database and returns a user object
     *
     * @param id part of primary key
     * @return a user object, that represents that saved in the database, null
     * if none exists
     * @throws SQLException
     */
    public User getUser(String id) throws SQLException {
        User user = null;
        PreparedStatement statement = con.prepareStatement(
                "SELECT uptime, name, apitoken FROM " + TABLENAME + " WHERE id=?");
        statement.setString(1, id);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            Long uptime = result.getBigDecimal("uptime").longValue();
            String name = result.getString("name");
            String apiToken = result.getString("apitoken");
            user = new User(uptime, id, name, apiToken);
            statement.close();
        }
        return user;
    }

    /**
     * gets all users for a given name on a given server
     *
     * @param name name for which the table is to be queried
     * @return a List of User Object which have the same name as the requested
     * name
     * @throws SQLException
     */
    public List<User> getUserByName(String name) throws SQLException {
        List<User> users = new ArrayList<>();
        try (PreparedStatement statement = con.prepareStatement(
                "SELECT uptime, id, apitoken FROM " + TABLENAME + " WHERE name=?")) {
            statement.setString(1, name);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Long uptime = result.getBigDecimal("uptime").longValue();
                String id = result.getString("id");
                String apiToken = result.getString("apitoken");
                users.add(new User(uptime, id, name, apiToken));
            }
            statement.close();
        }
        return users;
    }

    public User getUserByToken(String apiToken) throws SQLException {
        User user = null;
        PreparedStatement statement = con.prepareStatement(
                "SELECT uptime, name, id FROM " + TABLENAME + " WHERE apitoken=?");
        statement.setString(1, apiToken);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            Long uptime = result.getBigDecimal("uptime").longValue();
            String name = result.getString("name");
            String id = result.getString("id");
            user = new User(uptime, id, name, apiToken);
            statement.close();
        }
        return user;
    }

    /**
     * gets the uptime of all users combined
     *
     * @return the overall uptime
     * @throws SQLException
     */
    public long getUptimeAll() throws SQLException {
        long uptimeAll = 0;
        try (PreparedStatement statement = con.prepareStatement("SELECT SUM(uptime) as uptime_all FROM " + TABLENAME)) {
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                uptimeAll = result.getBigDecimal("uptime_all").longValue();
            }
            statement.close();
        }
        return uptimeAll;
    }

    /**
     * Selects the top n users by uptime
     *
     * @param limit limits the output to the top n users
     * @return a list of the top n users
     * @throws SQLException
     */
    public List<User> getRankingByUptime(int limit) throws SQLException {
        List<User> users = new ArrayList();
        try (PreparedStatement statement = con.prepareStatement(
                "SELECT uptime, id, name, apitoken FROM " + TABLENAME
                + " ORDER BY uptime DESC"
                + " LIMIT ?::integer")) {
            statement.setInt(1, limit);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                User user = new User(
                        result.getBigDecimal("uptime").longValue(),
                        result.getString("id"),
                        result.getString("name"),
                        result.getString("apitoken"));
                Main.logger.info("Following user was retrieved by ranking: {}", user.toString());
                users.add(user);
            }
            statement.close();
        }
        return users;
    }
}
