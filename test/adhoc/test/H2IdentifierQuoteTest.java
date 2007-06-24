package test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

public class H2IdentifierQuoteTest {

    private static final String tableName ="foo\"\"bar";
    
    private static final String dropTable = "drop table \"" + tableName + "\"";
    private static final String createTable = "CREATE TABLE "+tableName+" (someid int)";
    
    private static void execute(Connection con, String sql, boolean printError) {
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            System.out.println("Executing sql: "+sql);
            stmt.execute(sql);
        } catch (SQLException e ) {
            if (printError) {
                e.printStackTrace();
            }
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
        }
    }
    
    private static void test(Connection con) throws Exception{
        execute(con, dropTable, false);
        execute(con, createTable, true);
        
        ResultSet rs = null;
        DatabaseMetaData md = con.getMetaData();
        rs = md.getTables(null, null, "foo%", new String[] { "TABLE" });
        while (rs.next()) {
            String name = rs.getString(3);  //TABLENAME
            System.out.println("name="+name);
        }
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception{
        Class.forName("org.h2.Driver");
        String jdbcUrl = "jdbc:h2:tcp://localhost:9094/DBCOPYDEST";
        Connection con = DriverManager.getConnection(jdbcUrl, "dbcopy", "password");
        test(con);
    }

}
