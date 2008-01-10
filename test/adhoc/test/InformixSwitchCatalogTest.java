package test;

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


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;

/**
 * Test class to study the behavior of DatabaseMetaData.getUDTs in 
 * Informix.
 * 
 * @author manningr
 */
public class InformixSwitchCatalogTest {
   
	private static void changeDatabase(Connection con) throws Exception {
		Statement stmt = con.createStatement();
		stmt.execute("DATABASE dbcopydest");
	}
	
   private static void test(Connection con) 	{
   	try {
   	DatabaseMetaData md = con.getMetaData();
      String cat = "dbcopydest";
      String schemaPattern = null;
      String typeNamePattern = "%";

      con.setCatalog(cat);
      
      ResultSet rs = md.getUDTs(cat, schemaPattern, typeNamePattern, null);
      while (rs.next()) {
         /*
          * String catalog, String schema, String simpleName
          */
         String catalog = rs.getString(1);
         String schema = rs.getString(2);
         String simpleName = rs.getString(3);
         System.out.println("catalog: "+catalog);
         System.out.println("schema: "+schema);
         System.out.println("simpleName: "+simpleName);
      }
   	} catch (SQLException e) {
   		e.printStackTrace();
   		System.out.println("code="+e.getErrorCode());
   	}
   }  
    
    /**
     * @param args
     */
   public static void main(String[] args) throws Exception {
      ApplicationArguments.initialize(new String[] {});
      Class.forName("com.informix.jdbc.IfxDriver");
      String jdbcUrl = "jdbc:informix-sqli://192.168.1.135:9088:INFORMIXSERVER=sockets_srvr";
      Connection con = DriverManager.getConnection(jdbcUrl,
                                                   "informix",
                                                   "password");
      System.out.println("Running test before issuing DATABASE command:");
      test(con);
      System.out.println("Issuing DATABASE dbcopydest command:");
      changeDatabase(con);
      System.out.println("Running test after issuing DATABASE command:");
      test(con);
   }
}
