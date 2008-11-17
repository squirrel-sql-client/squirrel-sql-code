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
package test;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;

public class OracleSyntaxErrorOffsetTest {

    private static final String OFFSET_FUNCTION = 
        "create or replace function SQUIRREL_GET_ERROR_OFFSET (query IN varchar2) " +
        "return number authid current_user " +
        "is " +
        "     l_theCursor     integer default dbms_sql.open_cursor; " +
        "     l_status        integer; " +
        "begin " +
        "         begin " +
        "         dbms_sql.parse(  l_theCursor, query, dbms_sql.native ); " +
        "         exception " +
        "                 when others then l_status := dbms_sql.last_error_position; " +
        "         end; " +
        "         dbms_sql.close_cursor( l_theCursor ); " +
        "         return l_status; " +
        "end; ";
    
    
    private static void test(Connection con) throws Exception {
        CallableStatement cstmt = null;
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            System.out.println("Executing sql: "+OFFSET_FUNCTION);
            int offsetResult = stmt.executeUpdate(OFFSET_FUNCTION);
            System.out.println("Result: "+offsetResult);
            String sql = "{?=call get_error_offset(?)}";
            System.out.println("Executing sql: "+sql);
            cstmt = con.prepareCall(sql);
            cstmt.registerOutParameter(1, java.sql.Types.INTEGER);
            cstmt.setString(2, "select * from foobar");
            cstmt.execute();
            System.out.println("Offset="+cstmt.getInt(1));
                
        } catch (SQLException e ) {
            e.printStackTrace();
        } finally {
            SQLUtilities.closeStatement(cstmt);
            SQLUtilities.closeStatement(stmt);
        }
        
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        ApplicationArguments.initialize(new String[] {});
        Class.forName("oracle.jdbc.OracleDriver");
        String jdbcUrl = "jdbc:oracle:thin:@localhost:1521:CSUITE";
        Connection con = DriverManager.getConnection(jdbcUrl, "test", "password");
        test(con);
    }

}
