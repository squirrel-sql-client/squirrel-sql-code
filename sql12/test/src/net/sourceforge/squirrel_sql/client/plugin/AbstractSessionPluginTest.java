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

import static org.easymock.EasyMock.expect;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.plugins.DatabaseProductVersionData;

import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractSessionPluginTest extends AbstractPluginTest implements DatabaseProductVersionData
{

	
	@Test
	public void testIsPluginSession() throws Exception {
		ISession mockSession = mockHelper.createMock(ISession.class);
		ISQLDatabaseMetaData mockSQLDatabaseMetaData = mockHelper.createMock(ISQLDatabaseMetaData.class);
		expect(mockSession.getMetaData()).andStubReturn(mockSQLDatabaseMetaData);
		expect(mockSQLDatabaseMetaData.getDatabaseProductName()).andStubReturn(getDatabaseProductName());
		expect(mockSQLDatabaseMetaData.getDatabaseProductVersion()).andStubReturn(getDatabaseProductVersion());
		
		mockHelper.replayAll();
		if (classUnderTest instanceof DefaultSessionPlugin) {
			DefaultSessionPlugin plugin = (DefaultSessionPlugin)classUnderTest;
			Assert.assertTrue(plugin.isPluginSession(mockSession));
		}
		mockHelper.verifyAll();
	}
	
	/**
	 * Subclass tests should provide a database product name for a session that corresponds to the plugin being
	 * tested by overriding this method 
	 */
	protected abstract String getDatabaseProductName();

	/**
	 * Subclass tests should provide a database product version for a session that corresponds to the plugin being
	 * tested by overriding this method 
	 */	
	protected abstract String getDatabaseProductVersion();
	
}
