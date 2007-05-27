/*
 * Copyright (C) 2005 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * 
 * A description of this class goes here...
 */
public class DBMetaDataConcurrencyTester {

    private static String jdbcUrl = "jdbc:oracle:thin:@cumberland:1521:csuite";
    private static String user = "BELAIR40";
    private static String pass = "password";
    private static String tableName = "CS_ACL";
    
    private static Connection con = null;
    private static DatabaseMetaData md = null;
    
    private static int iterations = 100;
    private static int sleepTime = 10;
    private static int threads = 1;
    private static boolean getProcedures = false;
    private static boolean getProductName = false;
    private static boolean getProductVersion = true;
    private static boolean getJDBCVersion = false;
    private static boolean getTables = false;
    private static boolean getColumns = true;
    
    private static boolean printStackTraceOnError = false;
    
    private static void init() throws Exception {
        Class.forName("oracle.jdbc.OracleDriver");
        con = DriverManager.getConnection(jdbcUrl, user, pass);
        md = con.getMetaData();        
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        init();
        Thread[] getProceduresThreads = new Thread[threads];
        Thread[] getProductNameThreads = new Thread[threads];
        Thread[] getProductVersionThreads = new Thread[threads];
        Thread[] getJDBCVersionThreads = new Thread[threads];
        Thread[] getTablesThreads = new Thread[threads];
        Thread[] getColumnsThreads = new Thread[threads];
        
        // create the threads
        for (int i = 0; i < threads; i++) {
            getProceduresThreads[i] = create(new GetProceduresRunnable(), i);
            getProductNameThreads[i] = create(new GetProductNameRunnable(), i);
            getProductVersionThreads[i] = create(new GetProductVersionRunnable(), i);
            getJDBCVersionThreads[i] = create(new GetJDBCVersionRunnable(), i);
            getTablesThreads[i] = create(new GetTablesRunnable(), i);
            getColumnsThreads[i] = create(new GetColumnsRunnable(), i);
        }
        
        // start the threads
        for (int i = 0; i < threads; i++) {
            getProceduresThreads[i].start();
            getProductNameThreads[i].start();
            getProductVersionThreads[i].start();
            getJDBCVersionThreads[i].start();
            getTablesThreads[i].start();
            getColumnsThreads[i].start();
        }

        // join with the threads
        for (int i = 0; i < threads; i++) {
            getProceduresThreads[i].join();
            getProductNameThreads[i].join();
            getProductVersionThreads[i].join();
            getJDBCVersionThreads[i].join();
            getTablesThreads[i].join();
            getColumnsThreads[i].join();
        }
    }
    
    private static Thread create(Runnable runnable, int index) {
        Thread result = new Thread(runnable);
        result.setName(runnable.getClass().getName()+index);
        return result;
    }
    
    private static void handleException(Exception e) {
        System.err.println(e.getMessage());
        if (printStackTraceOnError) {
            e.printStackTrace();
        }
    }
    
    private static class GetProceduresRunnable implements Runnable {
                
        public void run() {
            int count = 0;
            while (getProcedures && count++ < iterations) {
                ResultSet rs = null;
                try {
                    System.out.println("Thread "+Thread.currentThread().getName());
                    rs = md.getProcedures(null, user, null);
                    while (rs.next()) {
                        String catalog = rs.getString(1);
                        String schema = rs.getString(2);
                        String name = rs.getString(3);
                        String remarks = rs.getString(7);
                        String type = rs.getString(8);
                    }
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                } catch (Exception e) {
                    handleException(e);
                } finally {
                    if (rs != null) try { rs.close(); } catch (Exception e) {} 
                }
            }
        }
    }
    
    private static class GetProductNameRunnable implements Runnable {
                
        public void run() {
            int count = 0;
            while (getProductName && count++ < iterations) {
                try {
                    System.out.println("Thread "+Thread.currentThread().getName());
                    String productName = md.getDatabaseProductName();
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                } catch (Exception e) {
                    handleException(e);
                } 
            }
        }
    }

    private static class GetProductVersionRunnable implements Runnable {
        
        public void run() {
            int count = 0;
            while (getProductVersion && count++ < iterations) {
                try {
                    System.out.println("Thread "+Thread.currentThread().getName());
                    String productVersion = md.getDatabaseProductVersion();
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                } catch (Exception e) {
                    handleException(e);
                } 
            }
        }
    }    
    
    private static class GetJDBCVersionRunnable implements Runnable {
        
        public void run() {
            int count = 0;
            while (getJDBCVersion && count++ < iterations) {
                try {
                    System.out.println("Thread "+Thread.currentThread().getName());
                    int major = md.getJDBCMajorVersion();
                    int minor = md.getJDBCMinorVersion();
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                } catch (Exception e) {
                    handleException(e);
                } 
            }
        }
    }

    
    
    private static class GetTablesRunnable implements Runnable {
        
        public void run() {
            int count = 0;
            while (getTables && count++ < iterations) {
                ResultSet rs = null;
                try {
                    System.out.println("Thread "+Thread.currentThread().getName());
                    rs = md.getTables(null, user, null, null);
                    ArrayList list = new ArrayList();
                    while (rs.next()) {
                        String catalog = rs.getString(1);
                        String schema = rs.getString(2);
                        String name = rs.getString(3);
                        String type = rs.getString(4);
                        String remarks = rs.getString(5);
                        // Oracle yields "Invalid column index" for the following:
                        //String typeCat = rs.getString(6);
                        //String typeSchema = rs.getString(7);
                        //String typeName = rs.getString(8);
                        //String selfRefColName = rs.getString(9);
                        //String refGeneration = rs.getString(10);
                        list.add(name);
                    }
                    //System.out.println("Tables = "+list);
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                } catch (Exception e) {
                    handleException(e);
                } finally {
                    if (rs != null) try { rs.close(); } catch (Exception e) {} 
                }
            }
        }
    }
    
    private static class GetColumnsRunnable implements Runnable {
        
        public void run() {
            int count = 0;
            while (getColumns && count++ < iterations) {
                ResultSet rs = null;
                try {
                    System.out.println("Thread "+Thread.currentThread().getName());
                    rs = md.getColumns(null, user, tableName, null);
                    while (rs.next()) {
                        String catalog = rs.getString(1);
                        String schema = rs.getString(2);
                        String tableName = rs.getString(3);
                        String columnName = rs.getString(4);
                        int dataType = rs.getInt(5);
                        String typeName = rs.getString(6);
                        int columnSize = rs.getInt(7);
                        int decimalDigits = rs.getInt(9);
                        int numPrecRadiz = rs.getInt(10);
                        int nullable = rs.getInt(11);
                        String remarks = rs.getString(12);
                        String columnDef = rs.getString(13);
                        int sqlDataType = rs.getInt(14);
                        int sqlDateTimeSub = rs.getInt(15);
                        int charOctetLength = rs.getInt(16);
                        int ordPosition = rs.getInt(17);
                        String isNullable = rs.getString(18);
                        // Oracle yields "Invalid column index" for the following:
                        //String scopeCat = rs.getString(19);
                        //String scopeSchema = rs.getString(20);
                        //String scopeTable = rs.getString(21);
                        //short sourceDataType = rs.getShort(22);
                    }
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                } catch (Exception e) {
                    handleException(e);
                } finally {
                    if (rs != null) try { rs.close(); } catch (Exception e) {} 
                }
            }
        }
    }
    
}
