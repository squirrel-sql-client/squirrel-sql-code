package ${package};

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import net.sourceforge.squirrel_sql.client.plugin.AbstractSessionPluginTest;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;

import org.junit.Test;

/*
 * Copyright (C) 2010 Rob Manning
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

/**
 * This test extends AbstractSessionPluginTest which provides testing for many of the methods in the
 * ISessionPlugin interface that are implemented by such a plugin. Run this test through a code coverage tool
 * such as Emma and you will find that many of the simpler methods in the interface are covered.
 */
public class ${pluginName}PluginTest extends AbstractSessionPluginTest
{
	@Override
	protected IPlugin getPluginToTest() throws Exception
	{
		return new ${pluginName}Plugin();
	}

	/**
	 * getDatabaseProductName returns "DB2" because that is the product name for sessions that the example
	 * plugin is interested in. This value will be returned by the SQLDatabaseMetaData for the method called
	 * getDatabaseProductName() that is associated with Session that is used when testing methods that on the
	 * ISessionPlugin interface that require a Session.
	 * 
	 * @see net.sourceforge.squirrel_sql.client.plugin.AbstractSessionPluginTest#getDatabaseProductName()
	 */
	@Override
	protected String getDatabaseProductName()
	{
		return "DB2";
	}

	/**
	 * No version is given here, because it is not required to distinguish dialects, since there is only one
	 * DB2 dialect at the current time.
	 * 
	 * @see net.sourceforge.squirrel_sql.client.plugin.AbstractSessionPluginTest#getDatabaseProductVersion()
	 */
	@Override
	protected String getDatabaseProductVersion()
	{
		return null;
	}

	@Test
	public void testSessionStarted() throws Exception
	{
		classUnderTest.initialize();
		((${pluginName}Plugin) classUnderTest).sessionStarted(mockSession);
	}

	@Test
	public void testSessionStartedNullApplication() throws Exception
	{
		try
		{
			when(mockSession.getApplication()).thenReturn(null);
			classUnderTest.initialize();
			((${pluginName}Plugin) classUnderTest).sessionStarted(mockSession);
			fail("Exception to get an exception for null IApplication returned from Session.getApplication");
		}
		catch (Exception e)
		{
			// This is expected.
		}
	}

	@Test
	public void testGetGlobalPreferencePanels()
	{
		assertNotNull(classUnderTest.getGlobalPreferencePanels());
	}

}
