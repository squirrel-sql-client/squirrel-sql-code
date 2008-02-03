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

/**
 * A simple test that demonstrates the bug in the Ingres 2006 R3 JDBC driver where money columns are reported
 * to have zero length and zero decimal digits. I see the following when I run the test: 
 * Executing sql: DROP TABLE "moneylengthtest" 
 * Executing sql: CREATE TABLE moneylengthtest (amt money) 
 * tableName=moneylengthtest
 * columnName=amt 
 * columnSize=0 
 * decimalDigits=0
 * 
 * @author manningr
 */
public class IngresMoneyTest
{

	private static final String tableName = "moneylengthtest";

	private static final String dropTable = "DROP TABLE \"" + tableName + "\"";

	private static final String createTable = "CREATE TABLE " + tableName + " (amt money)";

	private static void execute(Connection con, String sql, boolean printError)
	{
		Statement stmt = null;
		try
		{
			stmt = con.createStatement();
			System.out.println("Executing sql: " + sql);
			stmt.execute(sql);
		} catch (SQLException e)
		{
			if (printError)
			{
				e.printStackTrace();
			}
		} finally
		{
			if (stmt != null)
				try
				{
					stmt.close();
				} catch (SQLException e)
				{
				}
		}
	}

	private static void test(Connection con) throws Exception
	{
		execute(con, dropTable, false);
		execute(con, createTable, true);

		ResultSet rs = null;
		DatabaseMetaData md = con.getMetaData();
		rs = md.getColumns(null, null, tableName, null);
		while (rs.next())
		{
			String tableName = rs.getString(3); // TABLE NAME
			String columnName = rs.getString(4); // COLUMN NAME
			int columnSize = rs.getInt(7); // COLUMN SIZE
			int decimalDigits = rs.getInt(9); // COLUMN SIZE

			System.out.println("tableName=" + tableName);
			System.out.println("columnName=" + columnName);
			System.out.println("columnSize=" + columnSize);
			System.out.println("decimalDigits=" + decimalDigits);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		Class.forName("com.ingres.jdbc.IngresDriver");
		String jdbcUrl = "jdbc:ingres://192.168.1.132:ii7/dbcopydest";
		Connection con = DriverManager.getConnection(jdbcUrl, "dbcopy", "password");
		test(con);
	}

}
