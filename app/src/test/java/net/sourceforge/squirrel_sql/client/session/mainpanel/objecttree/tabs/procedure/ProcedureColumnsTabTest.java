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
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.procedure;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

public class ProcedureColumnsTabTest extends BaseSQuirreLJUnit4TestCase
{
	private ProcedureColumnsTab classUnderTest = null;

	private EasyMockHelper mockHelper = new EasyMockHelper();
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new ProcedureColumnsTab();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testGetTitle()
	{
		assertNotNull(classUnderTest.getTitle());
	}

	@Test
	public void testGetHint()
	{
		assertNotNull(classUnderTest.getHint());
	}

	@Test
	public void testSetGetProcedureInfo()
	{
		IProcedureInfo value = mockHelper.createMock(IProcedureInfo.class);
		
		mockHelper.replayAll();
		classUnderTest.setProcedureInfo(value);
		assertEquals(value, classUnderTest.getProcedureInfo());
		mockHelper.verifyAll();
	}

	@Test
	public void testCreateDataSet() throws DataSetException {
		
		ISession mockSession = mockHelper.createMock(ISession.class); 
		IApplication mockApplication = mockHelper.createMock(IApplication.class);
		IIdentifier mockIdentifier = mockHelper.createMock(IIdentifier.class);
		SessionManager mockSessionManager = mockHelper.createMock(SessionManager.class);
		ISQLConnection mockSqlConnection = mockHelper.createMock(ISQLConnection.class);
		SQLDatabaseMetaData mockMetaData = mockHelper.createMock(SQLDatabaseMetaData.class);
		IProcedureInfo mockProcedureInfo = mockHelper.createMock(IProcedureInfo.class);
		
		expect(mockSessionManager.getSession(mockIdentifier)).andStubReturn(mockSession);
		expect(mockSession.getApplication()).andStubReturn(mockApplication);
		expect(mockSession.getIdentifier()).andStubReturn(mockIdentifier);
		expect(mockApplication.getSessionManager()).andStubReturn(mockSessionManager);
		expect(mockSession.getSQLConnection()).andStubReturn(mockSqlConnection);
		expect(mockSqlConnection.getSQLMetaData()).andStubReturn(mockMetaData);
		expect(mockMetaData.getProcedureColumnsDataSet(mockProcedureInfo)).andStubReturn(null);
		
		mockHelper.replayAll();
		classUnderTest.setProcedureInfo(mockProcedureInfo);
		classUnderTest.setSession(mockSession);
		classUnderTest.createDataSet();
		mockHelper.verifyAll();
	}

}
