package test;

/*
 * Copyright (C) 2008 Rob Manning
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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;

/**
 * Test class to study the behavior of DatabaseMetaData.getTables in 
 * Sybase.
 * 
 * @author manningr
 */
public class ProgressGetTablesTest {
   
   private static void test(Connection con) throws Exception {
      DatabaseMetaData md = con.getMetaData();
      
      String cat = "DBCOPYDEST";
      String schemaPattern = "MANNINGR";
      String tableNamePattern = "TEST";

      ResultSet rs = md.getTables(cat,
                                  schemaPattern,
                                  tableNamePattern,
                                  new String[] { "TABLE" });
      while (rs.next()) {
         /*
          * String catalog, String schema, String simpleName,
                String tableType, String remarks,
          */
         String catalog = rs.getString(1);
         String schema = rs.getString(2);
         String simpleName = rs.getString(3);
         String tableType = rs.getString(4);
         String remarks = rs.getString(5);
         System.out.println("catalog: "+catalog);
         System.out.println("schema: "+schema);
         System.out.println("simpleName: "+simpleName);
         System.out.println("tableType: "+tableType);
         System.out.println("remarks: "+remarks);
      }
   }  
    
    
    /**
     * @param args
     */
   public static void main(String[] args) throws Exception {
      ApplicationArguments.initialize(new String[] {});
      Class.forName("com.ddtek.jdbc.openedge.OpenEdgeDriver");
      String jdbcUrl = "jdbc:datadirect:openedge://192.168.1.136:20935;DATABASENAME=dbcopydest";
      Connection con = DriverManager.getConnection(jdbcUrl,
                                                   "manningr",
                                                   "");
      test(con);
   }

    
}
