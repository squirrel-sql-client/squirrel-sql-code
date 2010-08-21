package net.sourceforge.squirrel_sql.client.session.properties;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.sourceforge.squirrel_sql.AbstractSerializableTest;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties.IDataSetDestinations;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *   Test class for SessionProperties
 */
public class SessionPropertiesTest extends AbstractSerializableTest {

	SessionProperties classUnderTest = new SessionProperties();

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SessionProperties();
		super.serializableToTest = new SessionProperties();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
		super.serializableToTest = null;
	}

	
	@Test
	public void testGetReadOnlyTableOutputClassName() throws Exception
	{
		assertEquals(IDataSetDestinations.READ_ONLY_TABLE, classUnderTest.getReadOnlyTableOutputClassName());
	}

	@Test
	public void testGetEditableTableOutputClassName() throws Exception
	{
		assertEquals(IDataSetDestinations.EDITABLE_TABLE, classUnderTest.getEditableTableOutputClassName());
	}

	@Test
	public void testGetReadOnlySQLResultsOutputClassName() throws Exception
	{
		assertEquals(IDataSetDestinations.READ_ONLY_TABLE, classUnderTest.getReadOnlySQLResultsOutputClassName());
	}

	@Test
	public void testaddPropertyChangeListener() throws Exception
	{
	}

	@Test
	public void testremovePropertyChangeListener() throws Exception
	{
	}

	@Test
	public void testGetMetaDataOutputClassName() throws Exception
	{
		classUnderTest.setMetaDataOutputClassName("aTestString");
		assertEquals("aTestString", classUnderTest.getMetaDataOutputClassName());
	}

	@Test
	public void testGetTableContentsOutputClassName() throws Exception
	{
		classUnderTest.setTableContentsOutputClassName("aTestString");
		assertEquals("aTestString", classUnderTest.getTableContentsOutputClassName());
	}

	@Test
	public void testGetSQLResultsOutputClassName() throws Exception
	{
		classUnderTest.setSQLResultsOutputClassName("aTestString");
		assertEquals("aTestString", classUnderTest.getSQLResultsOutputClassName());
	}

	@Test
	public void testGetAutoCommit() throws Exception
	{
		classUnderTest.setAutoCommit(true);
		assertEquals(true, classUnderTest.getAutoCommit());
	}

	@Test
	public void testGetAbortOnError() throws Exception
	{
		classUnderTest.setAbortOnError(true);
		assertEquals(true, classUnderTest.getAbortOnError());
	}

	@Test
	public void testGetWriteSQLErrorsToLog() throws Exception
	{
		classUnderTest.setWriteSQLErrorsToLog(true);
		assertEquals(true, classUnderTest.getWriteSQLErrorsToLog());
	}

	@Test
	public void testGetLoadColumnsInBackground() throws Exception
	{
		classUnderTest.setLoadColumnsInBackground(true);
		assertEquals(true, classUnderTest.getLoadColumnsInBackground());
	}

	@Test
	public void testGetLimitSQLResultTabs() throws Exception
	{
		classUnderTest.setLimitSQLResultTabs(true);
		assertEquals(true, classUnderTest.getLimitSQLResultTabs());
	}

	@Test
	public void testGetSqlResultTabLimit() throws Exception
	{
		classUnderTest.setSqlResultTabLimit(10);
		assertEquals(10, classUnderTest.getSqlResultTabLimit());
	}

	@Test
	public void testGetShowToolBar() throws Exception
	{
		classUnderTest.setShowToolBar(true);
		assertEquals(true, classUnderTest.getShowToolBar());
	}

	@Test
	public void testGetContentsNbrRowsToShow() throws Exception
	{
		classUnderTest.setContentsNbrRowsToShow(10);
		assertEquals(10, classUnderTest.getContentsNbrRowsToShow());
	}

	@Test
	public void testGetSQLNbrRowsToShow() throws Exception
	{
		classUnderTest.setSQLNbrRowsToShow(10);
		assertEquals(10, classUnderTest.getSQLNbrRowsToShow());
	}

	@Test
	public void testGetContentsLimitRows() throws Exception
	{
		classUnderTest.setContentsLimitRows(true);
		assertEquals(true, classUnderTest.getContentsLimitRows());
	}

	@Test
	public void testGetSQLLimitRows() throws Exception
	{
		classUnderTest.setSQLLimitRows(true);
		assertEquals(true, classUnderTest.getSQLLimitRows());
	}

	@Test
	public void testGetSQLStatementSeparator() throws Exception
	{
		classUnderTest.setSQLStatementSeparator("aTestString");
		assertEquals("aTestString", classUnderTest.getSQLStatementSeparator());
	}

	@Test
	public void testGetCommitOnClosingConnection() throws Exception
	{
		classUnderTest.setCommitOnClosingConnection(true);
		assertEquals(true, classUnderTest.getCommitOnClosingConnection());
	}

	@Test
	public void testGetShowRowCount() throws Exception
	{
		classUnderTest.setShowRowCount(true);
		assertEquals(true, classUnderTest.getShowRowCount());
	}

	@Test
	public void testGetStartOfLineComment() throws Exception
	{
		classUnderTest.setStartOfLineComment("aTestString");
		assertEquals("aTestString", classUnderTest.getStartOfLineComment());
	}

	@Test
	public void testGetRemoveMultiLineComment() throws Exception
	{
		classUnderTest.setRemoveMultiLineComment(true);
		assertEquals(true, classUnderTest.getRemoveMultiLineComment());
	}

	@Test
	public void testGetFontInfo() throws Exception
	{
		classUnderTest.setFontInfo(null);
		assertNotNull(classUnderTest.getFontInfo());
	}

	@Test
	public void testGetLimitSQLEntryHistorySize() throws Exception
	{
		classUnderTest.setLimitSQLEntryHistorySize(true);
		assertEquals(true, classUnderTest.getLimitSQLEntryHistorySize());
	}

	@Test
	public void testGetSQLShareHistory() throws Exception
	{
		classUnderTest.setSQLShareHistory(true);
		assertEquals(true, classUnderTest.getSQLShareHistory());
	}

	@Test
	public void testGetSQLEntryHistorySize() throws Exception
	{
		classUnderTest.setSQLEntryHistorySize(10);
		assertEquals(10, classUnderTest.getSQLEntryHistorySize());
	}

	@Test
	public void testGetMainTabPlacement() throws Exception
	{
		classUnderTest.setMainTabPlacement(10);
		assertEquals(10, classUnderTest.getMainTabPlacement());
	}

	@Test
	public void testGetObjectTabPlacement() throws Exception
	{
		classUnderTest.setObjectTabPlacement(10);
		assertEquals(10, classUnderTest.getObjectTabPlacement());
	}

	@Test
	public void testGetSQLExecutionTabPlacement() throws Exception
	{
		classUnderTest.setSQLExecutionTabPlacement(10);
		assertEquals(10, classUnderTest.getSQLExecutionTabPlacement());
	}

	@Test
	public void testGetSQLResultsTabPlacement() throws Exception
	{
		classUnderTest.setSQLResultsTabPlacement(10);
		assertEquals(10, classUnderTest.getSQLResultsTabPlacement());
	}

	@Test
	public void testGetCatalogFilterInclude() throws Exception
	{
		classUnderTest.setCatalogFilterInclude("aTestString");
		assertEquals("aTestString", classUnderTest.getCatalogFilterInclude());
	}

	@Test
	public void testGetSchemaFilterInclude() throws Exception
	{
		classUnderTest.setSchemaFilterInclude("aTestString");
		assertEquals("aTestString", classUnderTest.getSchemaFilterInclude());
	}

	@Test
	public void testGetObjectFilterInclude() throws Exception
	{
		classUnderTest.setObjectFilterInclude("aTestString");
		assertEquals("aTestString", classUnderTest.getObjectFilterInclude());
	}

	@Test
	public void testGetCatalogFilterExclude() throws Exception
	{
		classUnderTest.setCatalogFilterExclude("aTestString");
		assertEquals("aTestString", classUnderTest.getCatalogFilterExclude());
	}

	@Test
	public void testGetSchemaFilterExclude() throws Exception
	{
		classUnderTest.setSchemaFilterExclude("aTestString");
		assertEquals("aTestString", classUnderTest.getSchemaFilterExclude());
	}

	@Test
	public void testGetObjectFilterExclude() throws Exception
	{
		classUnderTest.setObjectFilterExclude("aTestString");
		assertEquals("aTestString", classUnderTest.getObjectFilterExclude());
	}

	@Test
	public void testGetLoadSchemasCatalogs() throws Exception
	{
		classUnderTest.setLoadSchemasCatalogs(true);
		assertEquals(true, classUnderTest.getLoadSchemasCatalogs());
	}

	@Test
	public void testGetShowResultsMetaData() throws Exception
	{
		classUnderTest.setShowResultsMetaData(true);
		assertEquals(true, classUnderTest.getShowResultsMetaData());
	}

}
