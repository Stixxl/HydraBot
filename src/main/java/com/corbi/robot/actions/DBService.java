/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.actions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
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

        } catch (SQLException ex) {
            Logger.getLogger(DBService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Creates all tables needed for the bot.
     * if all tables exist does nothing (except thrown a SQL-Exception), otherwise creates non existing tables
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
     * @param id the id of the user
     * @param guild_id the server_id, in which the user was created
     * @return true if a new user was successfully inserted, false otherwise(also if the user already exists)
     */
    public boolean createUser(String id, String guild_id)
    {
        boolean isSuccess = false;
        try {
            PreparedStatement statement = con.prepareStatement(
                    "insert into " + DBNAME + ".USERS "
                            + "values('" + id + "', '" + guild_id + "', 0)");
            isSuccess = execute(statement);
        } catch (SQLException ex) {
            Logger.getLogger(DBService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return isSuccess;
    }
    /**
     * This method sends a SQL-Statement to the database
     * @param statement SQL-Statement that should be sent to the database
     * @return true, if statement was executed successfully, false otherwise;
     * @throws SQLException 
     */
    private boolean execute(PreparedStatement statement) throws SQLException {
        boolean isSuccessStatement = false;
        try {
            isSuccessStatement = statement.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DBService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }

        return isSuccessStatement;
    }
}
