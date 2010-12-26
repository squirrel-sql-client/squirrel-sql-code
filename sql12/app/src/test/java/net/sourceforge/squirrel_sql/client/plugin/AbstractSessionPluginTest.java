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
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionPanel;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

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
	protected IApplication mockApplication;

	@Mock
	protected IMessageHandler mockMessageHandler;
	
	@Mock
	protected SessionInternalFrame mockSessionInternalFrame;
	
	@Mock
	protected SquirrelPreferences mockSquirrelPreferences;
	
	@Mock 
	protected IObjectTreeAPI mockObjectTreeAPI;

	@Mock
	protected SessionPanel mockSessionPanel;
	
	@Mock
	protected ISQLPanelAPI mockPanelAPI;
	

	@Before
	public void setUp() throws Exception {
			when(mockApplication.getMessageHandler()).thenReturn(mockMessageHandler);
			when(mockApplication.getSquirrelPreferences()).thenReturn(mockSquirrelPreferences);
			when(mockSession.getApplication()).thenReturn(mockApplication);
			when(mockSession.getMetaData()).thenReturn(mockSQLDatabaseMetaData);
			when(mockSession.getSessionInternalFrame()).thenReturn(mockSessionInternalFrame);
			when(mockSession.getSessionSheet()).thenReturn(mockSessionPanel);
			when(mockSQLDatabaseMetaData.getDatabaseProductName()).thenReturn(getDatabaseProductName());
			when(mockSQLDatabaseMetaData.getDatabaseProductVersion()).thenReturn(getDatabaseProductVersion());
			when(mockSessionInternalFrame.getObjectTreeAPI()).thenReturn(mockObjectTreeAPI);
			when(mockSessionPanel.getSQLPaneAPI()).thenReturn(mockPanelAPI);
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

	/**
	 * Subclass tests should provide a database product name for a session that corresponds to the plugin being
	 * tested by overriding this method
	 */
	protected abstract String getDatabaseProductName();

	/**
	 * Subclass tests should provide a database product version for a session that corresponds to the plugin
	 * being tested by overriding this method
	 */
	protected abstract String getDatabaseProductVersion();

}
