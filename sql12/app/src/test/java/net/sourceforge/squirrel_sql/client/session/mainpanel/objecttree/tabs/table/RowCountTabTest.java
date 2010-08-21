/*
 * Copyright (C) 2009 Rob Manning
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
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import static java.sql.Types.INTEGER;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBaseDataSetTabTest;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import org.junit.Before;

import utils.EasyMockHelper;

public class RowCountTabTest extends AbstractBaseDataSetTabTest
{

	EasyMockHelper localMockHelper = new EasyMockHelper();

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		classUnderTest = new RowCountTab();
		super.clazz = RowCountTab.class;

		Statement localMockStatement = localMockHelper.createMock("localMockStatement", Statement.class);
		ITableInfo localMockTableInfo = localMockHelper.createMock("localMockTableInfo", ITableInfo.class);
		ResultSet localMockResultSet = localMockHelper.createMock("localMockResultSet", ResultSet.class);
		ResultSetMetaData localMockResultSetMetaData =
			localMockHelper.createMock("localMockResultSetMetaData", ResultSetMetaData.class);

		expect(localMockStatement.executeQuery(isA(String.class))).andStubReturn(localMockResultSet);
		expect(localMockResultSet.getMetaData()).andStubReturn(localMockResultSetMetaData);
		expect(localMockResultSetMetaData.getColumnCount()).andStubReturn(1);
		expect(localMockResultSetMetaData.isNullable(1)).andStubReturn(ResultSetMetaData.columnNullable);
		expect(localMockResultSetMetaData.getPrecision(1)).andStubReturn(1);
		expect(localMockResultSetMetaData.isSigned(1)).andStubReturn(false);
		expect(localMockResultSetMetaData.isCurrency(1)).andStubReturn(false);
		expect(localMockResultSetMetaData.isAutoIncrement(1)).andStubReturn(false);
		expect(localMockResultSetMetaData.getColumnName(1)).andStubReturn("testColumnName");
		expect(localMockResultSetMetaData.getColumnTypeName(1)).andStubReturn("INTEGER");
		expect(localMockResultSetMetaData.getColumnType(1)).andStubReturn(INTEGER);
		expect(localMockResultSetMetaData.getColumnDisplaySize(1)).andStubReturn(10);
		expect(localMockResultSetMetaData.getColumnLabel(1)).andStubReturn("testColumnLabel");
		expect(localMockResultSetMetaData.getScale(1)).andStubReturn(0);
		expect(localMockResultSet.next()).andReturn(true);
		expect(localMockResultSet.next()).andReturn(false);
		expect(localMockResultSet.getObject(1)).andStubReturn("10");
		expect(localMockResultSet.wasNull()).andReturn(false);
		
		
		localMockStatement.close();
		localMockResultSet.close();
		

		expect(localMockTableInfo.getQualifiedName()).andStubReturn(TEST_QUALIFIED_NAME);

		expect(mockSQLConnection.createStatement()).andStubReturn(localMockStatement);
		localMockHelper.replayAll();

		((RowCountTab) classUnderTest).setTableInfo(localMockTableInfo);
	}

}
