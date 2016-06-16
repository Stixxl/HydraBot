/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.actions;

import com.corbi.robot.objects.User;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that handles anny dbcalls
 *
 * @author PogChamp
 */
public class DBService {

    Connection con;
    private static final String DBNAME = "HydraBotDB";

    public DBService(String username, String password) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBService.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            con = DriverManager.getConnection(
                    "jdbc:postgresql:" + DBNAME,
                    username,
                    password);
            con.setAutoCommit(true);
        } catch (SQLException ex) {
            Logger.getLogger(DBService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates all tables needed for the bot. if all tables exist does nothing
     * (except thrown a SQL-Exception), otherwise creates non existing tables
     */
    public void createTables() {
        try {
            //Table for Users
            PreparedStatement statement = con.prepareStatement(
                    "create table " + DBNAME + ".USERS "
                    + "(ID varchar(255) NOT NULL, "
                    + "GUILD_ID varchar(255) NOT NULL, "
                    + "UPTIME bigint NOT NULL, "
                    + "PRIMARY KEY (ID, GUILD_ID))");
            execute(statement);
        } catch (SQLException ex) {
            Logger.getLogger(DBService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Inserts a new user in the database
     *
     * @param id the id of the user
     * @param guild_id the server_id, in which the user was created
     * @throws java.sql.SQLException
     */
    public User createUser(String id, String guild_id) throws SQLException {
        PreparedStatement statement = con.prepareStatement(
                "insert into " + DBNAME + ".USERS "
                + "values('" + id + "', '" + guild_id + "', 0)");
        execute(statement);
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
        PreparedStatement statement = statement = con.prepareStatement(
                "SELECT * FROM " + DBNAME + ".USERS "//selects user, i will always be one user or none since (id, guild_id) is primary key
                + "WHERE ID=? "
                + "AND GUILD_ID=?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);//sets ResultSet to be able to update
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
     * @return a user object, that represents that saved in the database
     * @throws SQLException
     */
    public User getUser(String id, String guild_id) throws SQLException {
        PreparedStatement statement = con.prepareStatement(
                "SELECT * FROM " + DBNAME + ".USERS "
                + "WHERE ID=? "
                + "AND GUILD_ID=?");
        statement.setString(1, id);
        statement.setString(2, guild_id);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            BigDecimal uptime = result.getBigDecimal("UPTIME");
            return new User(uptime.longValue(), id, guild_id);

        } else {
            return null;
        }

    }

    public long getUptimeAll(String guildID) throws SQLException {
        long uptimeAll = 0;
        PreparedStatement statement = con.prepareStatement(
                "SELECT * FROM " + DBNAME + ".USERS "
                        + "WHERE GUILD_ID=?");
        statement.setString(1, guildID);
        ResultSet result = statement.executeQuery();
        while(result.next())
        {
            uptimeAll += result.getBigDecimal("UPTIME").longValue();
        }
       return uptimeAll; 
    }

    /**
     * This method sends a SQL-Statement to the database
     *
     * @param statement SQL-Statement that should be sent to the database
     * @return true, if statement was executed successfully, false otherwise;
     * @throws SQLException
     */
    private void execute(PreparedStatement statement) throws SQLException {
        statement.execute();
        statement.close();
    }
}
