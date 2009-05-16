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
package net.sourceforge.squirrel_sql.plugins.sqlparam;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import net.sourceforge.squirrel_sql.client.gui.session.SessionPanel;
import net.sourceforge.squirrel_sql.client.plugin.AbstractPluginTest;
import net.sourceforge.squirrel_sql.client.plugin.ISessionPlugin;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.plugins.DatabaseProductVersionData;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class SQLParamPluginTest extends AbstractPluginTest implements DatabaseProductVersionData
{	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SQLParamPlugin();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}		

	// Bug 2746982: Error occured in IPlugin.sessionEnded() for SQL Parametrisat
	// My theory here is that the session ending method was called twice (possibly user clicked the close 
	// session tab 'X' and close application window 'X' in rapid succession.  In any case, the plugin 
	// shouldn't behave badly if this happens - no guarantees that the PluginManager won't send spurious 
	// close events on shutdown.
	@Test
	public void testMultipleSessionEndings() {
		
		ISession mockSession = mockHelper.createMock("mockSession", ISession.class);
		SessionPanel mockSessionPanel = mockHelper.createMock("mockSessionPanel", SessionPanel.class);
		ISQLPanelAPI mockSQLPanelAPI = mockHelper.createMock("mockSQLPanelAPI", ISQLPanelAPI.class); 
		EasyMock.makeThreadSafe(mockSQLPanelAPI, true);
		
		expect(mockSession.getSessionSheet()).andStubReturn(mockSessionPanel);
		expect(mockSessionPanel.getSQLPaneAPI()).andStubReturn(mockSQLPanelAPI);
		mockSQLPanelAPI.addSQLExecutionListener(isA(ISQLExecutionListener.class));
		expectLastCall().anyTimes();
		mockSQLPanelAPI.removeSQLExecutionListener(isA(ISQLExecutionListener.class));
		expectLastCall().anyTimes();
		
		mockHelper.replayAll();
		
		((ISessionPlugin)classUnderTest).sessionCreated(mockSession);
		((ISessionPlugin)classUnderTest).sessionStarted(mockSession);
		// Give the AWT thread time to finish processing the event.
		Thread.yield();
		
		((ISessionPlugin)classUnderTest).sessionEnding(mockSession);
		((ISessionPlugin)classUnderTest).sessionEnding(mockSession);

		
		mockHelper.verifyAll();
	}
	
}
