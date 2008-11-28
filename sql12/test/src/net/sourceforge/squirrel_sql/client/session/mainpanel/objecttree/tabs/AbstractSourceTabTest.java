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

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Test;

import utils.EasyMockHelper;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;

public class AbstractSourceTabTest extends BaseSQuirreLJUnit4TestCase {

	private static final String TEST_QUALIFIED_NAME = "testQualifiedName";
	private static final String TEST_SIMPLE_NAME = "testSimpleName";
	private static final String TEST_CATALOG_NAME = "testCatalogName";
	private static final String TEST_SCHEMA_NAME = "testSchemaName";
	public static final String STMT_SEP = ";";
	public static final String HINT = "aHint";
	protected BaseSourceTab classUnderTest = null;
	protected EasyMockHelper mockHelper = new EasyMockHelper();
	protected ISession mockSession = mockHelper.createMock(ISession.class);
	protected IApplication mockApplication = mockHelper.createMock(IApplication.class);
	protected IIdentifier mockSessionId = mockHelper.createMock(IIdentifier.class);
	protected SessionManager mockSessionManager = mockHelper.createMock(SessionManager.class);
	protected IDatabaseObjectInfo mockDatabaseObjectInfo = mockHelper.createMock(IDatabaseObjectInfo.class);
	protected ISQLConnection mockSQLConnection = mockHelper.createMock(ISQLConnection.class);
	protected PreparedStatement mockPreparedStatement = mockHelper.createMock(PreparedStatement.class);
	protected ResultSet mockResultSet = mockHelper.createMock(ResultSet.class);

	public AbstractSourceTabTest() {
		super();
	}

	@After
   public void tearDown() throws Exception {
   	classUnderTest = null;
   }

	/**
	 * Verifies that the select method properly closes the PreparedStatement and ResultSet.
	 * @throws Exception
	 */
	@Test
   public final void testSelect() throws Exception {
   	
   	expect(mockSession.getApplication()).andStubReturn(mockApplication);
   	expect(mockSession.getIdentifier()).andStubReturn(mockSessionId);
   	expect(mockApplication.getSessionManager()).andStubReturn(mockSessionManager);
   	expect(mockSessionManager.getSession(mockSessionId)).andStubReturn(mockSession);
   	expect(mockDatabaseObjectInfo.getSchemaName()).andStubReturn(TEST_SCHEMA_NAME);
   	expect(mockDatabaseObjectInfo.getCatalogName()).andStubReturn(TEST_CATALOG_NAME);
   	expect(mockDatabaseObjectInfo.getSimpleName()).andStubReturn(TEST_SIMPLE_NAME);
   	expect(mockDatabaseObjectInfo.getQualifiedName()).andStubReturn(TEST_QUALIFIED_NAME);
   	expect(mockSession.getSQLConnection()).andStubReturn(mockSQLConnection);
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

}