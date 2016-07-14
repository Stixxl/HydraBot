/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.actions.DBServices;

import com.corbi.robot.objects.User;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
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
     * @param guild_id the server_id, in which the user was created
     * @return the newly created user
     * @throws java.sql.SQLException
     */
    public User createUser(String id, String guild_id, String name) throws SQLException {
        PreparedStatement statement = con.prepareStatement("insert into " + TABLENAME
                + "(id, guild_id, uptime, name) values('" + id + "', '" + guild_id + "', 0, '" + name + "')");
        DBService.execute(statement);
        return new User(0, id, guild_id, name);

    }

    /**
     *
     * @param id the userId in discord
     * @param guild_id the serverId in discord
     * @param uptime The overall time that was spent on the server
     * @throws SQLException
     */
    public void updateUser(String id, String guild_id, long uptime) throws SQLException {
        PreparedStatement statement = statement = con.prepareStatement("UPDATE " + TABLENAME//selects user, it will always be one user or none since (id, guild_id) is primary key
                + " SET uptime=?"
                + " WHERE id=?"
                + " AND guild_id=?");
        //sets parameter in above statement
        statement.setLong(1, uptime);
        statement.setString(2, id);
        statement.setString(3, guild_id);
        statement.execute();
        statement.close();
    }

    /**
     * retrieves a user from the database and returns a user object
     *
     * @param id part of primary key
     * @param guild_id part of primary key
     * @return a user object, that represents that saved in the database, null
     * if none exists
     * @throws SQLException
     */
    public User getUser(String id, String guild_id) throws SQLException {
        PreparedStatement statement = con.prepareStatement("SELECT * FROM " + TABLENAME
                + " WHERE id=? "
                + "AND guild_id=?");
        statement.setString(1, id);
        statement.setString(2, guild_id);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            Long uptime = result.getBigDecimal("uptime").longValue();
            String name = result.getString("name");
            statement.close();
            return new User(uptime, id, guild_id, name);

        } else {

            statement.close();
            return null;
        }

    }
    /**
     * gets all users for a given name on a given server
     * @param name name for which the table is to be queried
     * @param guild_id id of the server from which the request was received
     * @return a List of User Object which have the same name as the requested name
     * @throws SQLException 
     */
    public List<User> getUserByName(String name, String guild_id) throws SQLException {
        List<User> users = new ArrayList<>();
        PreparedStatement statement = con.prepareStatement("SELECT * FROM " + TABLENAME
                + " WHERE name=?"
                + " AND guild_id=?");
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            Long uptime = result.getBigDecimal("uptime").longValue();
            String id = result.getString("id");
            users.add(new User(uptime, id, guild_id, name));
        }
        statement.close();
        return users;
    }

    /**
     * gets the uptime of all users combined
     *
     * @param guildID the server, on which the overall uptime is requested
     * @return the overall uptime
     * @throws SQLException
     */
    public long getUptimeAll(String guildID) throws SQLException {
        long uptimeAll = 0;
        PreparedStatement statement = con.prepareStatement("SELECT * FROM " + TABLENAME
                + " WHERE guild_id=?");
        statement.setString(1, guildID);
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            uptimeAll += result.getBigDecimal("uptime").longValue();
        }
        statement.close();
        return uptimeAll;
    }
}
