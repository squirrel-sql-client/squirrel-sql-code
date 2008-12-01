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

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyLong;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetListModel;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import org.junit.After;
import org.junit.Test;

public class AbstractBasePreparedStatementTabTest extends AbstractTabTest
{

	protected BasePreparedStatementTab classUnderTest = null;

	protected SessionProperties mockSessionProperties = mockHelper.createMock(SessionProperties.class);

	protected IDatabaseObjectInfo mockDatabaseObjectInfo = mockHelper.createMock(IDatabaseObjectInfo.class);

	protected PreparedStatement mockPreparedStatement = mockHelper.createMock(PreparedStatement.class);

	protected ResultSet mockResultSet = mockHelper.createMock(ResultSet.class);

	protected ResultSetMetaData mockResultSetMetaData = mockHelper.createMock(ResultSetMetaData.class);

	public AbstractBasePreparedStatementTabTest()
	{
		super();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testSelect() throws SQLException
	{
		expect(mockSession.getApplication()).andStubReturn(mockApplication);
		expect(mockSession.getIdentifier()).andStubReturn(mockSessionId);
		expect(mockSession.getProperties()).andStubReturn(mockSessionProperties);
		expect(mockSession.getMetaData()).andStubReturn(mockSQLMetaData);
		expect(mockSessionProperties.getMetaDataOutputClassName()).andStubReturn(
			DataSetListModel.class.getName());
		expect(mockApplication.getSessionManager()).andStubReturn(mockSessionManager);
		expect(mockSessionManager.getSession(mockSessionId)).andStubReturn(mockSession);
		expect(mockDatabaseObjectInfo.getSchemaName()).andStubReturn(TEST_SCHEMA_NAME);
		expect(mockDatabaseObjectInfo.getCatalogName()).andStubReturn(TEST_CATALOG_NAME);
		expect(mockDatabaseObjectInfo.getSimpleName()).andStubReturn(TEST_SIMPLE_NAME);
		expect(mockDatabaseObjectInfo.getQualifiedName()).andStubReturn(TEST_QUALIFIED_NAME);
		expect(mockSession.getSQLConnection()).andStubReturn(mockSQLConnection);
		expect(mockSQLConnection.prepareStatement(isA(String.class))).andStubReturn(mockPreparedStatement);
		expect(mockSQLMetaData.getDatabaseProductName()).andStubReturn(databaseProductName);
		expect(mockSQLMetaData.getDatabaseProductVersion()).andStubReturn(DATABASE_PRODUCT_VERSION);
		mockPreparedStatement.setString(anyInt(), isA(String.class));
		expectLastCall().anyTimes();
		mockPreparedStatement.setLong(anyInt(), anyLong());
		expectLastCall().anyTimes();
		expect(mockPreparedStatement.executeQuery()).andStubReturn(mockResultSet);
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
		mockPreparedStatement.close();

		mockHelper.replayAll();
		classUnderTest.setSession(mockSession);
		classUnderTest.setDatabaseObjectInfo(mockDatabaseObjectInfo);
		classUnderTest.getComponent();
		classUnderTest.select();
		mockHelper.verifyAll();
	}

}