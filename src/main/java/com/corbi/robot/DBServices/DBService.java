/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.DBServices;

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
    private static final String TABLENAME = "HydraBotDB";

    public DBService(String username, String password) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Error while testing classname.", ex);
        }
        try {
            con = DriverManager.getConnection("jdbc:postgresql:" + TABLENAME,
                    username,
                    password);
            con.setAutoCommit(true);
        } catch (SQLException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Error during  connection to database.", ex);
        }
    }

    public UserService getUserService() {
        return new UserService(TABLENAME, con);
    }

    public GameService getGameService() {
        return new GameService(TABLENAME, con);
    }

    public SoundService getSoundService() {
        return new SoundService(TABLENAME, con);
    }

    public BinsenweisheitenService getBinsenweisheitenService() {
        return new BinsenweisheitenService(TABLENAME, con);
    }

    public FlameForDanielService getFlameForDanielService() {
        return new FlameForDanielService(TABLENAME, con);
    }

    /**
     * This method sends a SQL-Statement to the database
     *
     * @param statement SQL-Statement that should be sent to the database
     * @throws SQLException
     */
    protected static void execute(PreparedStatement statement) throws SQLException {
        statement.execute();
        statement.close();
    }

    protected static ResultSet selectRandom(String tablename, Connection con) throws SQLException {
        PreparedStatement statement = con.prepareStatement("SELECT * FROM " + tablename
                + " ORDER BY random()"
                + " LIMIT 1");
        return statement.executeQuery();
    }
}
