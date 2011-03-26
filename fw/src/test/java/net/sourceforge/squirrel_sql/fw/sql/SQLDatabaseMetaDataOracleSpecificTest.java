package net.sourceforge.squirrel_sql.fw.sql;

/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

/**
 * Some Oracle specific tests for {@link SQLDatabaseMetaData}.
 * @author Stefan Willinger
 *
 */
public class SQLDatabaseMetaDataOracleSpecificTest extends BaseSQuirreLJUnit4TestCase {

	private SQLDatabaseMetaData classUnderTest = null;
	private EasyMockHelper mockHelper = new EasyMockHelper();

	/* Mock Objects */
	private Connection mockConnection = mockHelper.createMock(Connection.class);
	private ISQLConnection mockSqlConnection = mockHelper.createMock(ISQLConnection.class);
	private DatabaseMetaData mockDatabaseMetaData = mockHelper.createMock(DatabaseMetaData.class);

	@Before
	public void setUp() throws Exception {

		expect(mockDatabaseMetaData.getDatabaseProductName()).andStubReturn("Oracle");
		expect(mockDatabaseMetaData.getDatabaseProductVersion()).andStubReturn("10.2.0.2.0");
		expect(mockDatabaseMetaData.supportsSchemasInIndexDefinitions()).andStubReturn(true);
		expect(mockDatabaseMetaData.supportsSchemasInDataManipulation()).andStubReturn(true);
		expect(mockDatabaseMetaData.supportsCatalogsInDataManipulation()).andStubReturn(true);
		expect(mockDatabaseMetaData.supportsSchemasInTableDefinitions()).andStubReturn(true);
		expect(mockDatabaseMetaData.getCatalogSeparator()).andStubReturn(".");
		expect(mockDatabaseMetaData.getIdentifierQuoteString()).andStubReturn("\"");
		
		expect(mockConnection.getMetaData()).andStubReturn(mockDatabaseMetaData);
		expect(mockSqlConnection.getConnection()).andStubReturn(mockConnection);

	}

	/**
	 * Ensures, that table names of recycle bins are escaped before calling {@link DatabaseMetaData#getColumns(String, String, String, String)}
	 * For some names of BIN$ objects, the jdbc driver could not catch the columns.
	 * Calling getColumns for a table like BIN$nPl/2NHWRNXgQKjAQgFYEQ==$0 will end in a exception without escaping 
	 * ORA-01424: missing or illegal character following the escape character.
	 * 
	 * This test ensures, that the table name is escaped for a Oracle Database
	 *  
	 */
	@Test
	public void testGetColumnsForOracleRecycleBin() throws SQLException {

		final String tableNameExpected = "BIN$nPl//2NHWRNXgQKjAQgFYEQ==$0";
		final String tableName = "BIN$nPl/2NHWRNXgQKjAQgFYEQ==$0";
		
		// Build an empty ResultSet
		ResultSet columnResultSet = buildTableMetaDataResultSet();
		
		// Ensure, that getColumns() was called with the escaped table name.
		expect(mockDatabaseMetaData.getColumns(null, "mySchema", tableNameExpected, "%")).andReturn(columnResultSet);
		mockHelper.replayAll();
		classUnderTest = new SQLDatabaseMetaData(mockSqlConnection);

		// Check to be sure we get only one schema
		TableColumnInfo[] columnInfo = classUnderTest.getColumnInfo(null, "mySchema", tableName);
		// we expect an empty table info because this test ensures escaping the table name.
		assertEquals(0, columnInfo.length);

		mockHelper.verifyAll();
	}
	

	/**
	 * The ResultSet is empty.
	 */
	private ResultSet buildTableMetaDataResultSet() throws SQLException {
		ResultSetMetaData rsmd = mockHelper.createMock(ResultSetMetaData.class);

		ResultSet rs = mockHelper.createMock(ResultSet.class);
		expect(rs.getMetaData()).andStubReturn(rsmd);
		
		expect(rs.next()).andReturn(false);
		rs.close();
		return rs;
		
	}

}
