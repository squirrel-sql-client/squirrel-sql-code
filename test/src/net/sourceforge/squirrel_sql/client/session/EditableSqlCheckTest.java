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
package net.sourceforge.squirrel_sql.client.session;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class EditableSqlCheckTest
{
	EditableSqlCheck classUnderTest = null;	

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	/**
	 * Test for 1371587: Not possible to edit data after SQL query
	 */
	@Test
	public void testGetTableNameFromSQL_1371587()
	{
		String sql = "select `id`,`data` from `testing`";
		testGetTableNameFromSQL(sql, "testing");
	}

	/**
	 *  Test for 2049336: Make Editable disappear from SQL/results tab
	 */
	@Test
	public void testGetTableNameFromSQL()
	{
		String sql = "select * from table_name t1";
		testGetTableNameFromSQL(sql, "table_name");
	}

	@Test
	public void testGetTableNameFromSQL_OneNewLine() {
		String sql = "select col1, col2 \n" +
						 "from table_name";
		testGetTableNameFromSQL(sql, "table_name");
	}

	@Test
	public void testGetTableNameFromSQL_AllTokensOnNewLine() {
		String sql = "select \n" +
						 "col1, \n" +
						 "col2 \n" +
						 "from \n" +
						 "table_name";
		testGetTableNameFromSQL(sql, "table_name");
	}
	
	@Test
	public void testGetTableNameFromSQL_WhereClause() {
		String sql = "select col1, col2 \n" +
		 				 "from table_name \n" +
		 				 "where col3 = 'abc';";
		testGetTableNameFromSQL(sql, "table_name");
	}
	
	@Test
	public void testGetTableNameFromSQL_Union () {
		String sql = "select * from t1 union select * from t2 union select * from dups;";
		testGetTableNameFromSQL(sql, null);
	}
	
	// The following query produces a result that should be editable, but it currently isn't
	@Test @Ignore
	public void testGetTableNameFromSQL_derby_SubQuery() {
		String sql = "select (select (select (select i from s) from s) from s) from s; ";
		testGetTableNameFromSQL(sql, "s");
	}
	
	@Test
	public void testGetTableNameFromSQL_derived() {
		String sql = "select * from (select a from s) a, (select a from s) b where a.a = b.a;";
		testGetTableNameFromSQL(sql, null);
	}
	
	@Test
	public void testGetTableNameFromSQL_implicit_conversion() {
		String sql = "select si from all1 where cast(1 as smallint) > '2';";
		testGetTableNameFromSQL(sql, "all1");
	}
	
	/**
	 * Test for two tables theta-style join - we don't support this type of editing.
	 */
	@Test 
	public void testGetTableNameFromSQL_TwoTableJoin() {
		String sql = "select * from table_name1 t1, table_name2 t2 where t1.id = t2.id";
		testGetTableNameFromSQL(sql, null);
	}
	
	private void testGetTableNameFromSQL(String sql, String expectedTableName) {
		SQLExecutionInfo info = new SQLExecutionInfo(1, sql, 100);
		classUnderTest = new EditableSqlCheck(info);
		if (expectedTableName == null) {
			assertNull(classUnderTest.getTableNameFromSQL());
		} else {
			assertTrue(expectedTableName.equalsIgnoreCase(classUnderTest.getTableNameFromSQL()));
		}
	}
}
