/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.actions.DBServices;

import static com.corbi.robot.actions.DBServices.DBService.execute;
import com.corbi.robot.objects.User;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    public User createUser(String id, String guild_id) throws SQLException {
        PreparedStatement statement = con.prepareStatement("insert into " + TABLENAME
                + " values('" + id + "', '" + guild_id + "', 0)");
        DBService.execute(statement);
        return new User(0, id, guild_id);

    }

    /**
     *
     * @param id the userId in discord
     * @param guild_id the serverId in discord
     * @param uptime The overall time that was spent on the server
     * @throws SQLException
     */
    public void updateUser(String id, String guild_id, long uptime) throws SQLException {
        PreparedStatement statement = statement = con.prepareStatement("SELECT * FROM " + TABLENAME//selects user, i will always be one user or none since (id, guild_id) is primary key
                + " WHERE id=? "
                + "AND guild_id=?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);//sets ResultSet to be able to update
        //sets parameter in above statement
        statement.setString(1, id);
        statement.setString(2, guild_id);
        //gets user in result otherwise throws SQLException
        ResultSet result = statement.executeQuery();
        result.next();
        //updates value of the user if he exists
        result.updateBigDecimal("uptime", BigDecimal.valueOf(uptime));
        result.updateRow();
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
            BigDecimal uptime = result.getBigDecimal("uptime");
            return new User(uptime.longValue(), id, guild_id);

        } else {
            return null;
        }

    }

    /**
     * gets the uptime of all users combined
     *
     * @param guildID the server on which the overall uptime is requested
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
        return uptimeAll;
    }
}
