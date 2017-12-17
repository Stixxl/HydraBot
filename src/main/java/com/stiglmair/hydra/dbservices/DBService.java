package com.stiglmair.hydra.dbservices;

import com.stiglmair.hydra.main.Main;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A class that handles any dbcalls
 *
 * @author PogChamp
 */
public class DBService {

    Connection con;
    private String schema;

    /**
     * Create a new {@link DbService} from the specified parameters.
     *
     * @param dbhost   The host name of the database server. Pass an empty
     *                 string to automatically fallback on "localhost".
     * @param dbport   The port of the database server. Pass zero to fallback
     *                 on the port configured with the `PGPORT` environment
     *                 variable or 5432 (postgres default port).
     * @param dbname   The name of the database to connect to (one server
     *                 can supply multiple databases with different names).
     *                 Pass an empty string to fallback on the value of the
     *                 `PGDATABASE` environment variable.
     * @param schema   The name of the database schema to connect to (the
     *                 default postgres schema is "public"). Pass an empty
     *                 string to fallback on the value of the `PGSCHEMA`
     *                 environment variable.
     * @param username The name of the user. Pass an empty string to fallback
     *                 on the `PGUSER` environment variable.
     * @param password The user's password.
     */
    public DBService(String dbhost, int dbport, String dbname,
                     String schema, String username, String password)
        throws SQLException
    {
        if (dbhost == null || dbhost.isEmpty()) {
            dbhost = System.getenv("PGHOST");
            if (dbhost == null || dbhost.isEmpty()) {
                dbhost = "localhost";
            }
        }
        if (dbport == 0) {
            String portString = System.getenv("PGPORT");
            if (portString != null && !portString.isEmpty()) {
                try {
                    dbport = Integer.parseInt(portString);
                } catch (NumberFormatException e) {
                    String msg = "Invalid value for PGPORT='" + portString + "'.";
                    Main.logger.error(msg, e);
                    throw new RuntimeException(msg);
                }
            }
            else {
                dbport = 5432;
            }
        }
        if (username == null || username.isEmpty()) {
            username = System.getenv("PGUSER");
            if (username == null || username.isEmpty()) {
                String msg = "Missing value for PGUSER.";
                Main.logger.error(msg);
                throw new RuntimeException(msg);
            }
        }
        if (dbname == null || dbname.isEmpty()) {
            dbname = System.getenv("PGDATABASE");
            if (dbname == null || dbname.isEmpty()) {
                dbname = username;
            }
        }

        // Check if the PostgreSQL driver is available.
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            Main.logger.error("PostgreSQL driver not found.", ex);
            throw new RuntimeException("PostgreSQL driver not found.");
        }

        // Construct the JDBC connection URL.
        String url = "jdbc:postgresql://" + dbhost + ":" + dbport + "/" + dbname;
        if (schema != null && !schema.isEmpty()) {
            url += "?currentSchema=" + schema;
        }

        Main.logger.info("Connecting to database (" + url + ") as '" + username + "'.");
        con = DriverManager.getConnection(url, username, password);
        con.setAutoCommit(true);
        this.schema = schema;
    }

    public UserService getUserService() {
        return new UserService(schema, con);
    }

    public GameService getGameService() {
        return new GameService(schema, con);
    }

    public SoundService getSoundService() {
        return new SoundService(schema, con);
    }

    public BinsenweisheitenService getBinsenweisheitenService() {
        return new BinsenweisheitenService(schema, con);
    }

    public FlameForDanielService getFlameForDanielService() {
        return new FlameForDanielService(schema, con);
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
