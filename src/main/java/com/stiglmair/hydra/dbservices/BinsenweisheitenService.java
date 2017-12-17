package com.stiglmair.hydra.dbservices;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A class that handles requests for the binsenweisheiten table
 * @author Stiglmair
 */
public class BinsenweisheitenService {
    private final String TABLENAME;
    private final Connection con;

    protected BinsenweisheitenService(String DBNAME, Connection con) {
        this.TABLENAME = DBNAME + ".binsenweisheiten";
        this.con = con;
    }

    /**
     * selects a random sentence from all binsenweisheiten
     * @return a random binsenweisheit
     * @throws SQLException
     */
    public String selectSentenceRandom() throws SQLException {
        ResultSet result =  DBService.selectRandom(TABLENAME, con);
        result.next();
        return result.getString("content");
    }
}
