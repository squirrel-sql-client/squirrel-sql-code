/*
 * Copyright (C) 2007 Rob Manning
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
package net.sourceforge.squirrel_sql.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ApplicationArgumentsTest
{

	private static final String LOG_CONFIG_FILE_PROP = "--" + ApplicationArguments.IOptions.LOG_FILE[1];

	private static final String USER_SETTINGS_DIR_PROP =
		"--" + ApplicationArguments.IOptions.USER_SETTINGS_DIR[1];

	private static final String SQUIRREL_HOME_PROP = "--" + ApplicationArguments.IOptions.SQUIRREL_HOME[1];

	private static final String NO_SPLASH_PROP = "--" + ApplicationArguments.IOptions.NO_SPLASH[1];

	private static final String NO_PLUGINS = "--" + ApplicationArguments.IOptions.NO_PLUGINS[1];

	private static final String SHOW_HELP = "--" + ApplicationArguments.IOptions.HELP[1];

	private static final String USE_DEFAULT_METAL_THEME =
		"--" + ApplicationArguments.IOptions.USE_DEFAULT_METAL_THEME[1];

	private static final String USE_NATIVE_LAF = "--" + ApplicationArguments.IOptions.USE_NATIVE_LAF[1];
	
	private static final String SQUIRREL_USERHOME = "/home/squirreluser";

	private static final String SQUIRREL_HOME = "/tmp";

	private static final String LOG_CONFIG_FILE = SQUIRREL_HOME + "log4j.properties";

	private static final String PLUGIN_LIST_PROP = "--plugin-classpath-list";

	private static final String PLUGIN_LIST = "net.sourceforge.squirrel_sql.plugins.graph.GraphPlugin";

	private static final String[] arguments =
		new String[] { SQUIRREL_HOME_PROP, SQUIRREL_HOME, USER_SETTINGS_DIR_PROP, SQUIRREL_USERHOME,
				LOG_CONFIG_FILE_PROP, LOG_CONFIG_FILE, PLUGIN_LIST_PROP, PLUGIN_LIST, NO_SPLASH_PROP, NO_PLUGINS };

	@Before
	public void setUp() throws Exception
	{
		assertTrue(ApplicationArguments.initialize(arguments));
	}

	@After
	public void tearDown() throws Exception
	{
		ApplicationArguments.reset();
	}

	@Test
	public final void testGetRawArguments() throws Exception
	{
		ApplicationArguments applicationArgumentsUnderTest = ApplicationArguments.getInstance();
		String[] rawArgs = applicationArgumentsUnderTest.getRawArguments();

		assertNotNull(rawArgs);
		assertEquals(arguments.length, rawArgs.length);
		assertEquals(SQUIRREL_HOME_PROP, rawArgs[0]);
		assertEquals(SQUIRREL_HOME, rawArgs[1]);
		assertEquals(USER_SETTINGS_DIR_PROP, rawArgs[2]);
		assertEquals(SQUIRREL_USERHOME, rawArgs[3]);
		assertEquals(LOG_CONFIG_FILE_PROP, rawArgs[4]);
		assertEquals(LOG_CONFIG_FILE, rawArgs[5]);
		assertEquals(PLUGIN_LIST_PROP, rawArgs[6]);
		assertEquals(PLUGIN_LIST, rawArgs[7]);
	}

	@Test
	public final void testGetSquirrelHomeDirectory()
	{
		ApplicationArguments applicationArgumentsUnderTest = ApplicationArguments.getInstance();
		assertEquals(SQUIRREL_HOME, applicationArgumentsUnderTest.getSquirrelHomeDirectory());
	}

	@Test
	public final void testGetUserSettingsDirectoryOverride()
	{
		ApplicationArguments applicationArgumentsUnderTest = ApplicationArguments.getInstance();
		assertEquals(SQUIRREL_USERHOME, applicationArgumentsUnderTest.getUserSettingsDirectoryOverride());
	}

	@Test
	public final void testGetLoggingConfigFileName()
	{
		ApplicationArguments applicationArgumentsUnderTest = ApplicationArguments.getInstance();
		assertEquals(LOG_CONFIG_FILE, applicationArgumentsUnderTest.getLoggingConfigFileName());
	}

	@Test
	public final void testGetPluginList()
	{
		ApplicationArguments applicationArgumentsUnderTest = ApplicationArguments.getInstance();
		List<String> pluginList = applicationArgumentsUnderTest.getPluginList();
		assertEquals(1, pluginList.size());
		String plugin = pluginList.get(0);
		assertEquals(PLUGIN_LIST, plugin);
	}

	@Test
	public final void testGetUserInterfaceDebugEnabled()
	{
		ApplicationArguments applicationArgumentsUnderTest = ApplicationArguments.getInstance();
		assertFalse(applicationArgumentsUnderTest.getUserInterfaceDebugEnabled());
	}

	@Test
	public final void testGetLoadPlugins()
	{
		ApplicationArguments applicationArgumentsUnderTest = ApplicationArguments.getInstance();
		assertFalse(applicationArgumentsUnderTest.getLoadPlugins());
		ApplicationArguments.reset();
		ApplicationArguments.initialize(new String[] { SQUIRREL_HOME_PROP, SQUIRREL_HOME });
		applicationArgumentsUnderTest = ApplicationArguments.getInstance();
		assertTrue(applicationArgumentsUnderTest.getLoadPlugins());
	}

	@Test
	public final void testGetShowSplashScreen()
	{
		ApplicationArguments applicationArgumentsUnderTest = ApplicationArguments.getInstance();
		assertFalse(applicationArgumentsUnderTest.getShowSplashScreen());
		ApplicationArguments.reset();
		ApplicationArguments.initialize(new String[] { SQUIRREL_HOME_PROP, SQUIRREL_HOME });
		applicationArgumentsUnderTest = ApplicationArguments.getInstance();
		assertTrue(applicationArgumentsUnderTest.getShowSplashScreen());
	}

	@Test
	public final void testGetShowHelp()
	{
		ApplicationArguments applicationArgumentsUnderTest = ApplicationArguments.getInstance();
		assertFalse(applicationArgumentsUnderTest.getShowHelp());
		ApplicationArguments.reset();
		ApplicationArguments.initialize(new String[] { SHOW_HELP });
		applicationArgumentsUnderTest = ApplicationArguments.getInstance();
		assertTrue(applicationArgumentsUnderTest.getShowHelp());
	}

	@Test
	public final void testUseDefaultMetalTheme()
	{
		ApplicationArguments applicationArgumentsUnderTest = ApplicationArguments.getInstance();
		assertFalse(applicationArgumentsUnderTest.useDefaultMetalTheme());
		ApplicationArguments.reset();
		ApplicationArguments.initialize(new String[] { USE_DEFAULT_METAL_THEME });
		applicationArgumentsUnderTest = ApplicationArguments.getInstance();
		assertTrue(applicationArgumentsUnderTest.useDefaultMetalTheme());
	}

	@Test
	public final void testUseNativeLAF()
	{
		ApplicationArguments applicationArgumentsUnderTest = ApplicationArguments.getInstance();
		assertFalse(applicationArgumentsUnderTest.useNativeLAF());
		ApplicationArguments.reset();
		ApplicationArguments.initialize(new String[] { USE_NATIVE_LAF });
		applicationArgumentsUnderTest = ApplicationArguments.getInstance();
		assertTrue(applicationArgumentsUnderTest.useNativeLAF());		
	}

}
