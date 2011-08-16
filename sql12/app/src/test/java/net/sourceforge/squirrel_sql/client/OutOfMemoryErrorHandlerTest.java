/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.client;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionManager;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Stefan Willinger
 *
 */
public class OutOfMemoryErrorHandlerTest extends BaseSQuirreLJUnit4TestCase {

	private OutOfMemoryErrorHandler classUnderTest = null;
	
	private static final String expectedErrorMessage = OutOfMemoryErrorHandler.i18n.message;
	
	
	IApplication mockApplication;
	SessionManager mockSessionManager;
	ISession mockSession;
	ISQLPanelAPI mockSQLPanelApi;
	SessionInternalFrame sessionInternalFrame;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		mockSession = mockHelper.createMock(ISession.class);
		mockApplication = mockHelper.createMock(IApplication.class);
		sessionInternalFrame = mockHelper.createMock(SessionInternalFrame.class);
		mockSQLPanelApi = mockHelper.createMock(ISQLPanelAPI.class);
		mockSessionManager = mockHelper.createMock(SessionManager.class);

		
		ISession[] connectedSessions = new ISession[]{mockSession};
		EasyMock.expect(mockApplication.getSessionManager()).andStubReturn(mockSessionManager);
		EasyMock.expect(mockSessionManager.getConnectedSessions()).andStubReturn(connectedSessions);
		EasyMock.expect(mockSessionManager.getActiveSession()).andStubReturn(mockSession);
		EasyMock.expect(mockSession.getSessionInternalFrame()).andStubReturn(sessionInternalFrame);
		EasyMock.expect(sessionInternalFrame.getSQLPanelAPI()).andStubReturn(mockSQLPanelApi);

		// Make sure, the Result-Tabs are closed
		mockSQLPanelApi.closeAllSQLResultTabs();
		EasyMock.expectLastCall();

		// Make sure, we show the right message
		mockSession.showErrorMessage(expectedErrorMessage);
		EasyMock.expectLastCall();
		
		// Make sure, we show a error dialog.
		mockApplication.showErrorDialog(expectedErrorMessage);
		EasyMock.expectLastCall();

	}

	/**
	 * Ensures, that the handler closes all ResultTabs of all sessions.
	 */
	@Test
	public void testHandlingOutOfMemory() {
		classUnderTest = new OutOfMemoryErrorHandler(mockApplication);
		mockHelper.replayAll();
		classUnderTest.handleOutOfMemoryError();
	}
	
	/**
	 * Ensures, that the application must be set via the setter.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetApplicationNotNull() {
		classUnderTest = new OutOfMemoryErrorHandler();
		classUnderTest.setApplication(null);
	}
	
	/**
	 * Ensures, that the application must be set via the constructor.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testApplicationNotNull() {
		classUnderTest = new OutOfMemoryErrorHandler(null);
	}

}
