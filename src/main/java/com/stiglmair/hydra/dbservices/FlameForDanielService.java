/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stiglmair.hydra.dbservices;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
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
        return DBService.selectRandom(TABLENAME, con).getString("content");
    }
}
