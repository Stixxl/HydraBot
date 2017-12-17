package com.stiglmair.hydra.dbservices;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Stiglmair
 */
public class FlameForDanielService {

    private final String TABLENAME;
    private final Connection con;

    protected FlameForDanielService(String DBNAME, Connection con) {
        this.TABLENAME = DBNAME + ".flamefordaniel";
        this.con = con;
    }

    public String selectSentenceRandom() throws SQLException {
        ResultSet result = DBService.selectRandom(TABLENAME, con);
        result.next();
        return result.getString("content");
    }
}
