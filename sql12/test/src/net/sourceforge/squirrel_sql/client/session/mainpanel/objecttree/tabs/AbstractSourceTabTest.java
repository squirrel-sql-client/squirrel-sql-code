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
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Test;

public class AbstractSourceTabTest extends AbstractTabTest
{


	protected BaseSourceTab classUnderTest = null;

	protected IDatabaseObjectInfo mockDatabaseObjectInfo = mockHelper.createMock(IDatabaseObjectInfo.class);

	protected PreparedStatement mockPreparedStatement = mockHelper.createMock(PreparedStatement.class);

	protected ResultSet mockResultSet = mockHelper.createMock(ResultSet.class);

	protected ISQLDatabaseMetaData mockMetaData = mockHelper.createMock(ISQLDatabaseMetaData.class);
	
	public AbstractSourceTabTest()
	{
		super();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	/**
	 * Verifies that the select method properly closes the PreparedStatement and ResultSet.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testSelect() throws Exception
	{

		expect(mockSession.getApplication()).andStubReturn(mockApplication);
		expect(mockSession.getIdentifier()).andStubReturn(mockSessionId);
		expect(mockSession.getMetaData()).andStubReturn(mockMetaData);
		expect(mockSession.getSQLConnection()).andStubReturn(mockSQLConnection);
		
		setupMockDatabaseObjectInfo();
		
		expect(mockApplication.getSessionManager()).andStubReturn(mockSessionManager);
		expect(mockSessionManager.getSession(mockSessionId)).andStubReturn(mockSession);
		
		expect(mockSQLConnection.prepareStatement(isA(String.class))).andStubReturn(mockPreparedStatement);
		mockPreparedStatement.setString(EasyMock.anyInt(), isA(String.class));
		expectLastCall().anyTimes();
		expect(mockPreparedStatement.executeQuery()).andReturn(mockResultSet);
		expect(mockResultSet.next()).andStubReturn(false);
		mockResultSet.close();
		mockPreparedStatement.close();

		mockHelper.replayAll();
		classUnderTest.getComponent();
		classUnderTest.setSession(mockSession);
		classUnderTest.setDatabaseObjectInfo(mockDatabaseObjectInfo);
		classUnderTest.select();
		mockHelper.verifyAll();
	}

	protected void setupMockDatabaseObjectInfo() {
		expect(mockDatabaseObjectInfo.getSchemaName()).andStubReturn(TEST_SCHEMA_NAME);
		expect(mockDatabaseObjectInfo.getCatalogName()).andStubReturn(TEST_CATALOG_NAME);
		expect(mockDatabaseObjectInfo.getSimpleName()).andStubReturn(TEST_SIMPLE_NAME);
		expect(mockDatabaseObjectInfo.getQualifiedName()).andStubReturn(TEST_QUALIFIED_NAME);		
	}
}