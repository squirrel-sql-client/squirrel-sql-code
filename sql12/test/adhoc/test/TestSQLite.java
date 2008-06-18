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
package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class TestSQLite
{

	public static void main(String[] args) throws Exception
	{
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:/tmp/test.dbf");
		Statement stat = conn.createStatement();
		stat.executeUpdate("drop table if exists test");
		stat.executeUpdate("create table test (myid integer)");
		stat.close();
		
		
		
		System.out.println("\t *** Before insert (empty table) *** \n");
		
		getDatabaseMetaDataType(conn, "test", "myid");
		printColumnTypeAndName(conn);

		PreparedStatement prep = conn.prepareStatement("insert into test values (?)");
		prep.setInt(1, 1);
		prep.executeUpdate();

		System.out.println("\t *** After insert (one record table) *** \n");
		
		getDatabaseMetaDataType(conn, "test", "myid");
		printColumnTypeAndName(conn);

		conn.close();
	}

	private static void printColumnTypeAndName(Connection conn) throws Exception
	{
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery("select * from test");
		ResultSetMetaData md = rs.getMetaData();
		System.out.println("Column type from ResultSetMetaData: " + md.getColumnType(1));
		System.out.println("Column type name from ResultSetMetaData: " + md.getColumnTypeName(1) + "\n");
		rs.close();
	}
	
	private static int getDatabaseMetaDataType(Connection conn, String tableName, String columnName) throws Exception {
		int result = -1;
		ResultSet rs = conn.getMetaData().getColumns(null, null, tableName, columnName);
		while (rs.next()) {
			int columnType = rs.getInt(5);
			String columnTypeName = rs.getString(6);
			System.out.println("Column type from DatabaseMetaData: "+columnType);
			System.out.println("Column type name from DatabaseMetaData: "+columnTypeName+"\n");
			
		}
		rs.close();
		return result;
	}
}
