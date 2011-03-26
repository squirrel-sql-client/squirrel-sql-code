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
package net.sourceforge.squirrel_sql.client.plugin;

import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public abstract class AbstractSessionPluginTest extends AbstractPluginTest implements
	DatabaseProductVersionData
{

	@Mock
	protected ISession mockSession;

	@Mock
	protected ISQLDatabaseMetaData mockSQLDatabaseMetaData;

	@Mock
	protected SessionInternalFrame mockSessionInternalFrame;

	@Mock
	protected IObjectTreeAPI mockObjectTreeAPI;

	@Mock
	protected SessionPanel mockSessionPanel;

	@Mock
	protected ISQLPanelAPI mockPanelAPI;
	
	@Mock
	protected ISQLConnection mockIsqlConnection;
	
	@Mock
	protected Connection mockConnection;

	@Mock
	protected DatabaseMetaData mockDatabaseMetaData; 
	
	@Mock
	protected IQueryTokenizer mockQueryTokenizer;
	
	@Before
	public void setUp() throws Exception
	{
		// Initializes the classUnderTest according to the sub-class test implementation. It is important
		// for tests that override setUp or declare @Before to call super.setUp to pickup initialization
		// code in base test classes.
		super.setUp();

		when(mockSession.getApplication()).thenReturn(mockApplication);
		when(mockSession.getMetaData()).thenReturn(mockSQLDatabaseMetaData);
		when(mockSession.getSessionInternalFrame()).thenReturn(mockSessionInternalFrame);
		when(mockSession.getSessionSheet()).thenReturn(mockSessionPanel);
		when(mockSession.getSQLConnection()).thenReturn(mockIsqlConnection);
		when(mockSession.getQueryTokenizer()).thenReturn(mockQueryTokenizer);
		when(mockSession.getObjectTreeAPIOfActiveSessionWindow()).thenReturn(mockObjectTreeAPI);
		when(mockIsqlConnection.getConnection()).thenReturn(mockConnection);
		when(mockSQLDatabaseMetaData.getDatabaseProductName()).thenReturn(getDatabaseProductName());
		when(mockSQLDatabaseMetaData.getDatabaseProductVersion()).thenReturn(getDatabaseProductVersion());
		when(mockSQLDatabaseMetaData.getJDBCMetaData()).thenReturn(mockDatabaseMetaData);
		when(mockSessionInternalFrame.getObjectTreeAPI()).thenReturn(mockObjectTreeAPI);
		when(mockSessionPanel.getSQLPaneAPI()).thenReturn(mockPanelAPI);
		when(mockQueryTokenizer.getSQLStatementSeparator()).thenReturn(";");
	}

	@Test
	public void testIsPluginSession() throws Exception
	{
		if (classUnderTest instanceof DefaultSessionPlugin)
		{
			DefaultSessionPlugin plugin = (DefaultSessionPlugin) classUnderTest;
			Assert.assertTrue(plugin.isPluginSession(mockSession));
		}
	}

	@Test
	public void testAllowSessionStartedInBackground()
	{
		((ISessionPlugin) classUnderTest).allowsSessionStartedInBackground();
	}

	/**
	 * Subclass tests should provide a database product name for a session that corresponds to the plugin being
	 * tested by overriding this method.  If the plugin merely listens for sessions, but doesn't care what 
	 * type of session they are, then this implementation will suffice.
	 */
	protected String getDatabaseProductName() {
		return null;
	}

	/**
	 * Subclass tests should provide a database product version for a session that corresponds to the plugin
	 * being tested by overriding this method. If the plugin merely listens for sessions, but doesn't care what 
	 * type of session they are, then this implementation will suffice.
	 */
	protected String getDatabaseProductVersion() {
		return null;
	}

}
