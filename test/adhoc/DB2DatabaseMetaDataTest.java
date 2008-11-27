/*
 * Copyright (C) CollabraSpace Inc. All rights reserved.
 */
package adhoc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DB2DatabaseMetaDataTest {

    String jdbcDriver = "com.ibm.db2.jcc.DB2Driver";
    
    String jdbcUrl = "jdbc:db2://localhost:50000/sample";
    
    String user = "dbcopy";
    
    String pass = "password";
    
    Connection con = null;
    
    String schema = "DBCOPYDEST";
    String table = "BIGINT_TYPE_TABLE";
    
    int[] bestRowConstants = new int[] {
            DatabaseMetaData.bestRowNotPseudo,
            DatabaseMetaData.bestRowPseudo,
            DatabaseMetaData.bestRowSession,
            DatabaseMetaData.bestRowTemporary,
            DatabaseMetaData.bestRowTransaction,
            DatabaseMetaData.bestRowUnknown,
    };
    
    public DB2DatabaseMetaDataTest() throws Exception {
        init();
    }
    
    public void init() throws Exception {
        Class.forName(jdbcDriver);
        con = DriverManager.getConnection(jdbcUrl,user, pass);
    }
    

    /**
     * This fails with a Connection failure - java.io.EOFException
     * @throws SQLException
     */
    public void doTest() throws SQLException {
        for (int i = 0; i < bestRowConstants.length; i++) {
            getBestRowIdentifier(null, i);
            getBestRowIdentifier("null", i);
            getBestRowIdentifier("%", i);
            getBestRowIdentifier(con.getCatalog(), i);
        }
    }
    
    private void getBestRowIdentifier(String catalog, int constant) 
        throws SQLException 
    {
        System.out.println("Testing catalog="+catalog+" constant="+constant);
        DatabaseMetaData dmd = con.getMetaData();
        ResultSet rs = 
            dmd.getBestRowIdentifier(catalog, schema, table, constant, true);      
        boolean results = false;
        while (rs.next()) {
            results = true;
            String columnName  = rs.getString("COLUMN_NAME");
            System.out.println("Column="+columnName);
        }
        if (!results) {
            System.out.println("Found no columns that identify row");
        }
        rs.close();
    }
    
    public void shutdown() throws SQLException {
        con.close();
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        DB2DatabaseMetaDataTest test = new DB2DatabaseMetaDataTest();
        
        test.doTest();
        
        test.shutdown();
    }

}
