/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.corbi.robot.actions.DBServices;

import com.corbi.robot.objects.Game;
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
            //table for users
            PreparedStatement statement = con.prepareStatement(
                    "create table " + DBNAME + ".USERS "
                    + "(ID varchar(255) NOT NULL, "
                    + "GUILD_ID varchar(255) NOT NULL, "
                    + "UPTIME bigint NOT NULL, "
                    + "PRIMARY KEY (ID, GUILD_ID))");
            execute(statement);
        } catch (SQLException ex) {
            Logger.getLogger(DBService.class.getName()).log(Level.SEVERE, "table users already exists or could not be created.", ex);
        }
        //table for games
        PreparedStatement statement;
        try {
            statement = con.prepareStatement(
                    "CREATE TABLE " + DBNAME + ".GAMES "
                    + "(TITLE varchar(255) NOT NULL, "
                    + "ID varchar(255) not NULL, "
                    + "GUILD_ID varchar(255) NOT NULL, "
                    + "TIME_PLAYED bigint NOT NULL, "
                    + "AMOUNT_PLAYED int NOT NULL"
                    + "FOREIGN KEY (ID, GUILD_ID) references " + DBNAME + ".USERS ON DELETE CASCADE, "
                    + "PRIMARY KEY(ID, GUILD_ID, TITLE))");
            execute(statement);
        } catch (SQLException ex) {
            Logger.getLogger(DBService.class.getName()).log(Level.SEVERE, "table games already exists or could not be created.", ex);
        }

    }
public UserService getUserService(){
     return new UserService(DBNAME, con);
 }

public GameService getGameService(){
    return new GameService(DBNAME, con);
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
}
