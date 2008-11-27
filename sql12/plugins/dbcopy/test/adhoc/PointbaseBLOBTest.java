/*
 * Copyright (C) CollabraSpace Inc. All rights reserved.
 */
package adhoc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class PointbaseBLOBTest {

    String jdbcUrl = "jdbc:pointbase:server://localhost:9093/workshop";
    
    String user = "csuite";
    
    String pass = "csuite";
    
    Connection con = null;
    
    String insertSQL = 
        "insert into CSUITE.CS_BUILDING " +
        "(ID, CAMPUS_ID, NAME, DESCRIPTION, IMAGE_BYTES, MOTD, " +
        "ADMINISTRATORS_ID, CREATION_DATE, MODIFICATION_DATE) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    String insertSQLWithoutBlob = 
        "insert into CSUITE.CS_BUILDING " +
        "(ID, CAMPUS_ID, NAME, DESCRIPTION, MOTD, " +
        "ADMINISTRATORS_ID, CREATION_DATE, MODIFICATION_DATE) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    
    long id = 1;
    long campusId = 1127416427999l;
    long administratorsId = 7;
    long creationDate = 1127416677218l;
    long modificationDate = 1127416677218l;
    
    public PointbaseBLOBTest() throws Exception {
        init();
    }
    
    public void init() throws Exception {
        Class.forName("com.pointbase.jdbc.jdbcUniversalDriver");
        con = DriverManager.getConnection(jdbcUrl,user, pass);
    }
    

    /**
     * This fails with a Connection failure - java.io.EOFException
     * @throws SQLException
     */
    public void doTest() throws SQLException {
        PreparedStatement ps = con.prepareStatement(insertSQL);
        
        ps.setLong(1, id);
        ps.setLong(2, campusId);
        ps.setString(3, "c1b1");
        ps.setNull(4, Types.VARCHAR);
        ps.setNull(5, Types.LONGVARBINARY);
        ps.setNull(6, Types.VARCHAR);
        ps.setLong(7, administratorsId);
        ps.setLong(8, creationDate);
        ps.setLong(9, modificationDate);
        int rowCount = ps.executeUpdate();
        System.out.println("Inserted "+rowCount+" rows successfully");
        
        ps.close();
    }

    /**
     * This fails with a Connection failure - java.io.EOFException
     * @throws SQLException
     */
    public void doTestWithoutBlob() throws SQLException {
        PreparedStatement ps = con.prepareStatement(insertSQL);
        
        ps.setLong(1, id);
        ps.setLong(2, campusId);
        ps.setString(3, "c1b1");
        ps.setNull(4, Types.VARCHAR);
        ps.setNull(5, Types.VARCHAR);
        ps.setLong(6, administratorsId);
        ps.setLong(7, creationDate);
        ps.setLong(8, modificationDate);
        int rowCount = ps.executeUpdate();
        System.out.println("Inserted "+rowCount+" rows successfully");
        
        ps.close();
    }    
    
    public void shutdown() throws SQLException {
        con.close();
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        PointbaseBLOBTest test = new PointbaseBLOBTest();
        
        test.doTest();
        
        test.shutdown();
    }

}
