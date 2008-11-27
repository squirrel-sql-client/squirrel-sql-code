/*
 * Copyright (C) CollabraSpace Inc. All rights reserved.
 */
package adhoc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;

public class MySQLDatabaseMetaDataTest {

    String jdbcDriver = "com.mysql.jdbc.Driver";
    
    String jdbcUrl = "jdbc:mysql://localhost:3306/test";
    
    String user = "test.user";
    
    String pass = "password";
    
    Connection con = null;
    
    String schema = "dbcopysrc";
    String table = "BIGINT_TYPE_TABLE";
    
    int[] bestRowConstants = new int[] {
            DatabaseMetaData.bestRowNotPseudo,
            DatabaseMetaData.bestRowPseudo,
            DatabaseMetaData.bestRowSession,
            DatabaseMetaData.bestRowTemporary,
            DatabaseMetaData.bestRowTransaction,
            DatabaseMetaData.bestRowUnknown,
    };
    
    public MySQLDatabaseMetaDataTest() throws Exception {
        init();
    }
    
    public void init() throws Exception {
        ApplicationArguments.initialize(new String[0]);
        Class.forName(jdbcDriver);
        con = DriverManager.getConnection(jdbcUrl,user, pass);
    }
    

    /**
     * This fails with a Connection failure - java.io.EOFException
     * @throws SQLException
     */
    public void doTest() throws SQLException {
        //ResultSet rs = con.getMetaData().getTables("dbcopysrc", null, "BIGINT_TYPE_TABLE", new String[]{ "TABLE" });
        ResultSet rs = con.getMetaData().getTables(null, null, "%", new String[]{ "TABLE" });
        while (rs.next()) {
            System.out.println("Table="+rs.getString("TABLE_NAME"));
        }
    }
    
    
    public void shutdown() throws SQLException {
        con.close();
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        MySQLDatabaseMetaDataTest test = new MySQLDatabaseMetaDataTest();
        
        test.doTest();
        
        test.shutdown();
    }

}
