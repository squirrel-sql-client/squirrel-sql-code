package test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import oracle.sql.OPAQUE;
import oracle.xdb.XMLType;

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

public class OracleSqlXmlTypeTest {

    private static void test(Connection con) throws Exception {
        OPAQUE opaque = getOpaque(con);
        readOpaqueAsBytes(opaque);
        opaque = getOpaque(con);
        readOpaqueAsCharacterStream(opaque);
        opaque = getOpaque(con);
        readOpaqueAsJdbc(opaque);
        opaque = getOpaque(con);
        readOpaqueAsAsciiStreamValue(opaque);
        opaque = getOpaque(con);
        readObjectUsingXmlType(opaque);
        setNullXmlValue(con);
    }
    
    private static OPAQUE getOpaque(Connection con) throws Exception{
        OPAQUE result = null;
        ResultSet rs = null;
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            //System.out.println("Selecting xml_data");
            rs = stmt.executeQuery("select xml_data from xmltable");
            if (rs.next()) {
                result = (oracle.xdb.XMLType)rs.getObject(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLUtilities.closeResultSet(rs);
        }
        return result;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception{
        ApplicationArguments.initialize(new String[] {});
        Class.forName("oracle.jdbc.OracleDriver");
        String jdbcUrl = "jdbc:oracle:thin:@192.168.1.100:1521:XE";
        Connection con = DriverManager.getConnection(jdbcUrl, "testdrop", "password");
        test(con);
    }

    
    private static void readOpaqueAsCharacterStream(OPAQUE opaque)  
    {
        try {
            // The following gives
            // java.sql.SQLException: Conversion to character stream failed
            System.out.println("\nreadOpaqueAsCharacterStream: ");
            Reader reader = opaque.characterStreamValue();
            char[] buffer = new char[32];
            
            while (reader.read(buffer) != -1) {
                System.out.println("buffer: "+buffer.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void readOpaqueAsBytes(OPAQUE opaque) 
        throws SQLException 
    {
        byte[] bytes = (byte[])opaque.getValue();
        System.out.println("\nreadOpaqueAsBytes: "+new String(bytes));
    }

    private static void readOpaqueAsJdbc(OPAQUE opaque) 
    throws SQLException 
    {
        Object o = opaque.toJdbc();
        System.out.println("\readOpaqueAsJdbc: "+o.toString());
    }
    
    private static void readOpaqueAsAsciiStreamValue(OPAQUE opaque)  
    {
        try {
        InputStream is = opaque.asciiStreamValue();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println("\readOpaqueAsAsciiStreamValue: "+line);    
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private static void readObjectUsingXmlType(OPAQUE opaque) throws Exception {
        XMLType xml = XMLType.createXML(opaque);
        System.out.println("readObjectUsingXmlType: "+xml.getStringVal());
    }
    
    private static void setNullXmlValue(Connection con) {
        PreparedStatement pstmt = null;
        try {
            String sql = "update xmltable set XML_DATA = ? where DOC_ID = 3";
            pstmt = con.prepareStatement(sql);
            pstmt.setObject(1, null);
            if (pstmt.executeUpdate() == 1) {
                System.out.println("Successully set XML_DATA to null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SQLUtilities.closeStatement(pstmt);
        }
    }
}
