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
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetListModel;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;

import org.junit.After;
import org.junit.Test;

import utils.EasyMockHelper;

public class AbstractStatementTabTest extends BaseSQuirreLJUnit4TestCase
{

	protected BaseObjectTab classUnderTest = null;
	protected EasyMockHelper mockHelper = new EasyMockHelper();
	private static final String TEST_COLUMN_NAME = "aColumnName";
	protected static final String DATABASE_PRODUCT_VERSION = "1.0";
	protected String databaseProductName = null;
	private static final String TEST_QUALIFIED_NAME = "testQualifiedName";
	private static final String TEST_SIMPLE_NAME = "testSimpleName";
	private static final String TEST_CATALOG_NAME = "testCatalogName";
	private static final String TEST_SCHEMA_NAME = "testSchemaName";
	public static final String STMT_SEP = ";";
	public static final String HINT = "aHint";
	public static final String METADATA_OUTPUT_CLASSNAME = "aMetaDataOutputClassName";
	protected ISession mockSession = mockHelper.createMock(ISession.class);
	protected SessionProperties mockSessionProperties = mockHelper.createMock(SessionProperties.class);
	protected IApplication mockApplication = mockHelper.createMock(IApplication.class);
	protected IIdentifier mockSessionId = mockHelper.createMock(IIdentifier.class);
	protected SessionManager mockSessionManager = mockHelper.createMock(SessionManager.class);
	protected IDatabaseObjectInfo mockDatabaseObjectInfo = mockHelper.createMock(IDatabaseObjectInfo.class);
	protected ISQLConnection mockSQLConnection = mockHelper.createMock(ISQLConnection.class);
	protected Statement mockStatement = mockHelper.createMock(Statement.class);
	protected ResultSet mockResultSet = mockHelper.createMock(ResultSet.class);
	protected ISQLDatabaseMetaData mockMetaData = mockHelper.createMock(ISQLDatabaseMetaData.class);
	protected ResultSetMetaData mockResultSetMetaData = mockHelper.createMock(ResultSetMetaData.class);

	public AbstractStatementTabTest()
	{
		super();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testSelect() throws Exception
	{
		
		expect(mockSession.getApplication()).andStubReturn(mockApplication);
		expect(mockSession.getIdentifier()).andStubReturn(mockSessionId);
		expect(mockSession.getProperties()).andStubReturn(mockSessionProperties);
		expect(mockSession.getMetaData()).andStubReturn(mockMetaData);
		expect(mockSessionProperties.getMetaDataOutputClassName()).andStubReturn(DataSetListModel.class.getName());
		expect(mockApplication.getSessionManager()).andStubReturn(mockSessionManager);
		expect(mockSessionManager.getSession(mockSessionId)).andStubReturn(mockSession);
		expect(mockDatabaseObjectInfo.getSchemaName()).andStubReturn(TEST_SCHEMA_NAME);
		expect(mockDatabaseObjectInfo.getCatalogName()).andStubReturn(TEST_CATALOG_NAME);
		expect(mockDatabaseObjectInfo.getSimpleName()).andStubReturn(TEST_SIMPLE_NAME);
		expect(mockDatabaseObjectInfo.getQualifiedName()).andStubReturn(TEST_QUALIFIED_NAME);
		expect(mockSession.getSQLConnection()).andStubReturn(mockSQLConnection);
		expect(mockSQLConnection.createStatement()).andStubReturn(mockStatement);
		expect(mockMetaData.getDatabaseProductName()).andStubReturn(databaseProductName);
		expect(mockMetaData.getDatabaseProductVersion()).andStubReturn(DATABASE_PRODUCT_VERSION);
		expect(mockStatement.executeQuery(isA(String.class))).andStubReturn(mockResultSet);
		expect(mockResultSet.next()).andStubReturn(false);
		expect(mockResultSet.getMetaData()).andStubReturn(mockResultSetMetaData);
		expect(mockResultSetMetaData.getColumnCount()).andStubReturn(1);
		expect(mockResultSetMetaData.isNullable(1)).andStubReturn(ResultSetMetaData.columnNoNulls);
		expect(mockResultSetMetaData.getPrecision(1)).andStubReturn(10);
		expect(mockResultSetMetaData.isSigned(1)).andReturn(true);
		expect(mockResultSetMetaData.isCurrency(1)).andReturn(true);
		expect(mockResultSetMetaData.isAutoIncrement(1)).andReturn(true);
		expect(mockResultSetMetaData.getColumnName(1)).andReturn(TEST_COLUMN_NAME);
		expect(mockResultSetMetaData.getColumnTypeName(1)).andReturn("VARCHAR");
		expect(mockResultSetMetaData.getColumnType(1)).andReturn(Types.VARCHAR);
		expect(mockResultSetMetaData.getColumnDisplaySize(1)).andStubReturn(10);
		expect(mockResultSetMetaData.getColumnLabel(1)).andStubReturn(TEST_COLUMN_NAME);
		expect(mockResultSetMetaData.getScale(1)).andStubReturn(3);
		mockResultSet.close();
		mockStatement.close();
		
		mockHelper.replayAll();
		classUnderTest.setSession(mockSession);
		classUnderTest.setDatabaseObjectInfo(mockDatabaseObjectInfo);		
		classUnderTest.getComponent();		
		classUnderTest.select();
		mockHelper.verifyAll();
	}

}