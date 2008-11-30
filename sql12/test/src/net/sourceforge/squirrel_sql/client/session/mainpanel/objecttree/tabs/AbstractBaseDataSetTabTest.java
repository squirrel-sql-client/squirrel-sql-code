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

import java.util.Date;


import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class AbstractBaseDataSetTabTest extends AbstractTabTest
{

	protected BaseDataSetTab classUnderTest = null;

	public AbstractBaseDataSetTabTest()
	{
		super();
	}

	public void setUp() throws Exception
	{
		expect(mockSession.getApplication()).andStubReturn(mockApplication);
		expect(mockSession.getSQLConnection()).andStubReturn(mockSQLConnection);
		expect(mockSession.getMetaData()).andStubReturn(mockMetaData);
		expect(mockSession.getIdentifier()).andStubReturn(mockSessionId);
		expect(mockApplication.getSessionManager()).andStubReturn(mockSessionManager);
		expect(mockSessionManager.getSession(mockSessionId)).andStubReturn(mockSession);
		expect(mockApplication.getThreadPool()).andStubReturn(mockThreadPool);
		expect(mockSQLConnection.getSQLMetaData()).andStubReturn(mockSQLMetaData);
		expect(mockSQLMetaData.getCatalogs()).andStubReturn(mockCatalogs);
		expect(mockSQLConnection.getConnection()).andStubReturn(mockConnection);
		expect(mockConnection.isClosed()).andStubReturn(false);
		expect(mockConnection.isReadOnly()).andStubReturn(false);
		expect(mockConnection.getCatalog()).andStubReturn(TEST_CATALOG_NAME);
		expect(mockSQLConnection.getCatalog()).andStubReturn(TEST_CATALOG_NAME);
		expect(mockSQLConnection.getAutoCommit()).andStubReturn(true);
		expect(mockConnection.getTransactionIsolation()).andStubReturn(TRANSACTION_ISOLATION);
		expect(mockSQLConnection.getTimeOpened()).andStubReturn(new Date());

		mockThreadPool.addTask(isA(Runnable.class));
		expectLastCall().anyTimes();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testRefreshComponent() throws Exception
	{
		mockHelper.replayAll();
		classUnderTest.setSession(mockSession);
		classUnderTest.refreshComponent();
		mockHelper.verifyAll();
	}

	@Test
	public void testGetHint()
	{
		mockHelper.replayAll();
		classUnderTest.setSession(mockSession);
		Assert.assertNotNull(classUnderTest.getHint());
		mockHelper.verifyAll();
	}

	@Test
	public void testGetTitle()
	{
		mockHelper.replayAll();
		classUnderTest.setSession(mockSession);
		Assert.assertNotNull(classUnderTest.getTitle());
		mockHelper.verifyAll();
	}

}