package net.sourceforge.squirrel_sql.fw.sql;

/*
 * Copyright (C) 2006 Rob Manning
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
import static java.sql.Types.VARCHAR;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.sql.dbobj.BestRowIdentifier;

import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

/**
 * Test case for SQLDatabaseMetaData class.
 * 
 * @author manningr
 */
public class SQLDatabaseMetaDataTest extends BaseSQuirreLJUnit4TestCase {

	private static final String TEST_TABLE = "aTable";
	private static final String TEST_SCHEMA = "aSchema";
	private static final String TEST_CATALOG = "aCatalog";
	private SQLDatabaseMetaData classUnderTest = null;
	private EasyMockHelper mockHelper = new EasyMockHelper();

	/* Mock Objects */
	private Connection mockConnection = mockHelper.createMock(Connection.class);
	private ISQLConnection mockSqlConnection = mockHelper.createMock(ISQLConnection.class);
	private DatabaseMetaData mockDatabaseMetaData = mockHelper.createMock(DatabaseMetaData.class);

	@Before
	public void setUp() throws Exception {

		expect(mockDatabaseMetaData.getDatabaseProductName()).andStubReturn("PostgreSQL");
		expect(mockDatabaseMetaData.getDatabaseProductVersion()).andStubReturn("8.1.8");
		expect(mockDatabaseMetaData.supportsSchemasInIndexDefinitions()).andStubReturn(true);
		expect(mockDatabaseMetaData.supportsSchemasInDataManipulation()).andStubReturn(true);
		expect(mockDatabaseMetaData.supportsCatalogsInDataManipulation()).andStubReturn(true);
		expect(mockDatabaseMetaData.supportsSchemasInTableDefinitions()).andStubReturn(true);
		expect(mockDatabaseMetaData.getCatalogSeparator()).andStubReturn(".");
		expect(mockDatabaseMetaData.getIdentifierQuoteString()).andStubReturn("\"");
		
		expect(mockConnection.getMetaData()).andStubReturn(mockDatabaseMetaData);
		expect(mockSqlConnection.getConnection()).andStubReturn(mockConnection);

	}

	@Test
	public void testGetSchemas() throws SQLException {

		/* The first time that schemas are asked for, return just one */
		ResultSet schemaResultSet1 = buildVarcharResultSetAsRows("schemaResultSet1", new String[] { TEST_SCHEMA });	
		/* The second time that schemas are asked for, return two */
		ResultSet schemaResultSet2 = buildVarcharResultSetAsRows("schemaResultSet2", new String[] { TEST_SCHEMA, "aSchema2" });
		expect(mockDatabaseMetaData.getSchemas()).andReturn(schemaResultSet1);
		expect(mockDatabaseMetaData.getSchemas()).andReturn(schemaResultSet2);
		
		mockHelper.replayAll();
		classUnderTest = new SQLDatabaseMetaData(mockSqlConnection);

		// Check to be sure we get only one schema
		String[] currentSchemas = classUnderTest.getSchemas();
		assertEquals(1, currentSchemas.length);

		// Now, check to be sure we get both schemas.
		currentSchemas = classUnderTest.getSchemas();
		assertEquals(2, currentSchemas.length);

		mockHelper.verifyAll();
	}

	@Test
	public void testGetCatalogs() throws SQLException {

		/* The first time that catalogs are asked for, return just one */
		ResultSet catalogResultSet1 = buildVarcharResultSetAsRows(null, new String[] { TEST_CATALOG });
		/* The second time that catalogs are asked for, return two */
		ResultSet catalogResultSet2 = buildVarcharResultSetAsRows(null, new String[] { TEST_CATALOG, "aCatalog2" });
		expect(mockDatabaseMetaData.getCatalogs()).andReturn(catalogResultSet1);
		expect(mockDatabaseMetaData.getCatalogs()).andReturn(catalogResultSet2);

		mockHelper.replayAll();
		classUnderTest = new SQLDatabaseMetaData(mockSqlConnection);

		// Check to be sure we get only one schema
		String[] currentCatalogs = classUnderTest.getCatalogs();
		assertEquals(1, currentCatalogs.length);

		// Now, check to be sure we get both catalogs.
		currentCatalogs = classUnderTest.getCatalogs();
		assertEquals(2, currentCatalogs.length);

		mockHelper.verifyAll();
	}

	@Test
	public void testPGGetTableTypes() throws SQLException {
		/* Build the table types returned by PostgreSQL */
		ResultSet mockTableTypeResultSet = buildVarcharResultSetAsRows("mockTableTypeResultSet", new String[] {
		      "SYSTEM INDEX", "SYSTEM VIEW", "SYSTEM TABLE", "SYSTEM TOAST INDEX", "SYSTEM TOAST TABLE",
		      "SYSTEM VIEW", "TABLE", "TEMPORARY INDEX", "TEMPORARY TABLE", "VIEW" });
		expect(mockDatabaseMetaData.getTableTypes()).andStubReturn(mockTableTypeResultSet);

		mockHelper.replayAll();
		classUnderTest = new SQLDatabaseMetaData(mockSqlConnection);

		String[] tableTypes = classUnderTest.getTableTypes();
		for (int i = 0; i < tableTypes.length; i++) {
			String type = tableTypes[i];
			assertFalse(
			   "'SYSTEM INDEX' is a type returned from " + "SQLDatabaseMetaData.getTableTypes for PostgreSQL - "
			         + "it should not be.", "SYSTEM INDEX".equals(type));
		}

		mockHelper.verifyAll();
	}

	/**
	 * Test for bug 1716859 (Can't see data in content tab or row count tab) SQLServer with a dash in the name
	 * needs to be quoted.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testGetIdentifierQuoteStringMSSQL() throws SQLException {
		Connection mockCon = mockHelper.createMock(Connection.class);
		DatabaseMetaData md = mockHelper.createMock(DatabaseMetaData.class);
		expect(md.getIdentifierQuoteString()).andStubReturn("foo");
		expect(mockCon.getMetaData()).andStubReturn(md);
		ISQLConnection sqlcon = mockHelper.createMock(ISQLConnection.class);
	   expect(sqlcon.getConnection()).andStubReturn(mockCon);
		SQLDatabaseMetaData sqlmd = new SQLDatabaseMetaData(sqlcon);

		mockHelper.replayAll();

		String quoteString = sqlmd.getIdentifierQuoteString();
		assertEquals("foo", quoteString);
		quoteString = sqlmd.getIdentifierQuoteString();
		assertEquals("foo", quoteString);
		quoteString = sqlmd.getIdentifierQuoteString();
		assertEquals("foo", quoteString);
		quoteString = sqlmd.getIdentifierQuoteString();
		assertEquals("foo", quoteString);

		mockHelper.verifyAll();
	}

	@Test
	public void testGetBestRowIdentifier() throws SQLException {
		
		ITableInfo mockTableInfo = mockHelper.createMock(ITableInfo.class);
		ResultSet mockBestRowIdResultSet = mockHelper.createMock(ResultSet.class);
		ResultSetMetaData mockResultSetMetaData = mockHelper.createMock(ResultSetMetaData.class);
		
		expect(mockTableInfo.getCatalogName()).andStubReturn(TEST_CATALOG);
		expect(mockTableInfo.getSchemaName()).andStubReturn(TEST_SCHEMA);
		expect(mockTableInfo.getSimpleName()).andStubReturn(TEST_TABLE);
		expect(mockDatabaseMetaData.getBestRowIdentifier(TEST_CATALOG, TEST_SCHEMA, TEST_TABLE, 
			DatabaseMetaData.bestRowTransaction, true));
		expectLastCall().andReturn(mockBestRowIdResultSet);
		expect(mockBestRowIdResultSet.getMetaData()).andReturn(mockResultSetMetaData);
		expect(mockBestRowIdResultSet.getObject(1)).andReturn(1); // scope
		expect(mockResultSetMetaData.getColumnType(1)).andReturn(Types.BIGINT);
		expect(mockBestRowIdResultSet.getString(2)).andReturn("aColumn"); // column name
		expect(mockBestRowIdResultSet.getObject(3)).andReturn(3); // sqlType
		expect(mockResultSetMetaData.getColumnType(3)).andReturn(Types.SMALLINT);
		expect(mockBestRowIdResultSet.getString(4)).andReturn("SMALLINT"); // typeName
		
		expect(mockBestRowIdResultSet.getObject(5)).andReturn(5); // int precision
		expect(mockResultSetMetaData.getColumnType(5)).andReturn(Types.INTEGER);
		
		expect(mockBestRowIdResultSet.getObject(7)).andReturn(7); // short scale
		expect(mockResultSetMetaData.getColumnType(7)).andReturn(Types.TINYINT);

		expect(mockBestRowIdResultSet.getObject(8)).andReturn(8); // short pseudocolumn
		expect(mockResultSetMetaData.getColumnType(8)).andReturn(Types.TINYINT);
		
		expect(mockBestRowIdResultSet.next()).andReturn(true);
		expect(mockBestRowIdResultSet.next()).andReturn(false);
		mockBestRowIdResultSet.close();
		
		
		
		mockHelper.replayAll();
		classUnderTest = new SQLDatabaseMetaData(mockSqlConnection);
		
		BestRowIdentifier[] result = classUnderTest.getBestRowIdentifier(mockTableInfo);
		
		assertEquals(1, result.length);
		BestRowIdentifier rid = result[0];
		assertEquals(1, rid.getScope());
		assertEquals("aColumn", rid.getColumnName());
		assertEquals(3, rid.getSQLDataType());
		assertEquals("SMALLINT", rid.getTypeName());
		assertEquals(5, rid.getPrecision());
		assertEquals(7, rid.getScale());
		assertEquals(8, rid.getPseudoColumn());
		
		mockHelper.verifyAll();
	}
	
	@Test
	public void testGetUDTs() throws SQLException {
		
		final String javaClassName = "java.lang.String";
		final String remarkValue = "remark";
		final String dataType = ""+VARCHAR;
		final String[] values = new String[] { TEST_CATALOG, TEST_SCHEMA, "JAVA_OBJECT", javaClassName, dataType, remarkValue};
		final ResultSet mockResultSet = buildVarcharResultSetAsColumns("UDTResultSet", values);
		mockDatabaseMetaData.getUDTs(TEST_CATALOG, TEST_SCHEMA, TEST_TABLE, null);
		expectLastCall().andStubReturn(mockResultSet);
		
		mockHelper.replayAll();
		classUnderTest = new SQLDatabaseMetaData(mockSqlConnection);
		final IUDTInfo[] result = classUnderTest.getUDTs(TEST_CATALOG, TEST_SCHEMA, TEST_TABLE, null);
		assertEquals(1, result.length);
		assertEquals(javaClassName, result[0].getJavaClassName());
		assertEquals(dataType, result[0].getDataType());
		assertEquals(remarkValue, result[0].getRemarks());
		mockHelper.verifyAll();
	}
	
	// Helper methods
	
	private ResultSet buildVarcharResultSetAsColumns(String mockName, String[] values) throws SQLException {
		ResultSetMetaData rsmd = mockHelper.createMock(mockName, ResultSetMetaData.class);
		expect(rsmd.getColumnCount()).andStubReturn(values.length);
		ResultSet rs = mockHelper.createMock(ResultSet.class);
		expect(rs.getMetaData()).andStubReturn(rsmd);
		
		// Setup values.length columns
		int count = 1;
		for (String value : values) {
			expect(rs.getString(count)).andStubReturn(value);
			expect(rs.wasNull()).andStubReturn(false);
			expect(rsmd.getColumnTypeName(count)).andStubReturn("varchar");
			expect(rsmd.getColumnType(count++)).andStubReturn(java.sql.Types.VARCHAR);
		}
		// Setup one row
		expect(rs.next()).andReturn(true);
		expect(rs.next()).andReturn(false);
		rs.close();
		return rs;
	}

	private ResultSet buildVarcharResultSetAsRows(String mockName, String[] values) throws SQLException {
		ResultSetMetaData rsmd = mockHelper.createMock(mockName, ResultSetMetaData.class);
		expect(rsmd.getColumnCount()).andStubReturn(values.length);
		ResultSet rs = mockHelper.createMock(ResultSet.class);
		expect(rs.getMetaData()).andStubReturn(rsmd);
		
		// Setup one column in multiple rows
		expect(rsmd.getColumnTypeName(1)).andStubReturn("varchar");
		expect(rsmd.getColumnType(1)).andStubReturn(java.sql.Types.VARCHAR);
		for (String value : values) {
			expect(rs.getString(1)).andStubReturn(value);
			expect(rs.next()).andReturn(true);
			expect(rs.wasNull()).andStubReturn(false);
		}
		expect(rs.next()).andReturn(false);
		rs.close();
		return rs;
	}

}
