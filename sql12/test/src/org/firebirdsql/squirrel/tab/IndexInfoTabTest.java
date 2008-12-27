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
package org.firebirdsql.squirrel.tab;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBaseDataSetTabTest;

import org.easymock.EasyMock;
import org.junit.Before;

public class IndexInfoTabTest extends AbstractBaseDataSetTabTest
{

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		classUnderTest = new IndexInfoTab();
		classUnderTest.setDatabaseObjectInfo(mockDatabaseObjectInfo);
		clazz = IndexInfoTab.class;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBaseDataSetTabTest#testCreateDataSet()
	 */
	@Override
	public void testCreateDataSet() throws Exception
	{
		ResultSet mockResultSet = mockHelper.createMock(ResultSet.class);
		PreparedStatement mockPreparedStatement = mockHelper.createMock(PreparedStatement.class);

		expect(mockSQLConnection.prepareStatement(isA(String.class))).andStubReturn(mockPreparedStatement);
		mockPreparedStatement.setString(1, TEST_SIMPLE_NAME);
		expect(mockPreparedStatement.executeQuery()).andStubReturn(mockResultSet);

		expect(mockResultSet.next()).andReturn(true);
		expect(mockResultSet.getString(EasyMock.anyInt())).andReturn("testString").anyTimes();
		expect(mockResultSet.getInt(EasyMock.anyInt())).andReturn(0).anyTimes();
		expect(mockResultSet.getStatement()).andStubReturn(mockPreparedStatement);
		
		mockPreparedStatement.close();
		mockResultSet.close();
		super.testCreateDataSet();
	}

}
