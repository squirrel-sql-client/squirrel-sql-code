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
package net.sourceforge.squirrel_sql.client.plugin;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import javax.swing.Action;
import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactoryImpl;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.IResources;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * This class provides common tests for plugins. Each plugin test should simply extend this class to pickup
 * the common tests.
 */
@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public abstract class AbstractPluginTest
{
	protected IPlugin classUnderTest = null;

	@Mock
	protected IApplication mockApplication;

	@Mock
	protected IMessageHandler mockMessageHandler;

	@Mock
	protected SquirrelPreferences mockSquirrelPreferences;

	@Mock
	protected ActionCollection mockActionCollection;
	
	@Mock
	protected SessionManager mockSessionManager;

	@Mock
	protected IResources mockIResources;

	@Mock
	protected IPluginResourcesFactory mockIPluginResourcesFactory;

	@Mock
	protected JMenu mockJMenu;

	@Mock
	private Action mockAction;
	
	@Mock
	protected ApplicationFileWrappers mockApplicationFileWrappers;
	
	protected FileWrapperFactory fileWrapperFactory = new FileWrapperFactoryImpl();
	
	// Locations for various commonly accessed directories
	protected FileWrapper targetDirectory = fileWrapperFactory.create("./target");
	protected FileWrapper testHomeDirectory = fileWrapperFactory.create(targetDirectory, "test-home-dir");
	protected FileWrapper testUpdateDirectory = fileWrapperFactory.create(testHomeDirectory, "update");
	protected FileWrapper testPluginsDirectory = fileWrapperFactory.create(testHomeDirectory, "plugins");
	protected FileWrapper testLibDirectory = fileWrapperFactory.create(testHomeDirectory, "lib");
	
	/**
	 * Sub-class tests must implement this to return an instance of the Plugin being tested.
	 * 
	 * @return an instance of the Plugin being tested.
	 */
	protected abstract IPlugin getPluginToTest() throws Exception;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception
	{
		when(mockApplication.getMessageHandler()).thenReturn(mockMessageHandler);
		when(mockApplication.getSquirrelPreferences()).thenReturn(mockSquirrelPreferences);
		when(mockApplication.getActionCollection()).thenReturn(mockActionCollection);
		when(mockApplication.getSessionManager()).thenReturn(mockSessionManager);
		when(mockIPluginResourcesFactory.createResource(anyString(), any(IPlugin.class))).thenReturn(
			mockIResources);
		when(mockIResources.createMenu(Mockito.anyString())).thenReturn(mockJMenu);
		when(mockActionCollection.get((Class<? extends Action>)Mockito.any())).thenReturn(mockAction);

		try {
			UIFactory.initialize(mockSquirrelPreferences, mockApplication);
		} catch (Exception e) {}
		
		classUnderTest = getPluginToTest();
		
		classUnderTest.setFileWrapperFactory(fileWrapperFactory);
		classUnderTest.setApplicationFiles(mockApplicationFileWrappers);
		
		when(mockApplicationFileWrappers.getPluginsDirectory()).thenReturn(testPluginsDirectory);
		when(mockApplicationFileWrappers.getLibraryDirectory()).thenReturn(testLibDirectory);
		when(mockApplicationFileWrappers.getPluginsUserSettingsDirectory()).thenReturn(testPluginsDirectory);
		when(mockApplicationFileWrappers.getSquirrelHomeDir()).thenReturn(testHomeDirectory);
		when(mockApplicationFileWrappers.getUpdateDirectory()).thenReturn(testUpdateDirectory);
		
		classUnderTest.load(mockApplication);
		classUnderTest.initialize();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest.unload();
		classUnderTest = null;
	}

	@Test
	public void testGetInternalName()
	{
		assertNotNull(classUnderTest.getInternalName());
	}

	@Test
	public void testGetDescriptiveName()
	{
		assertNotNull(classUnderTest.getDescriptiveName());
	}

	@Test
	public void testGetVersion()
	{
		assertNotNull(classUnderTest.getVersion());
	}

	@Test
	public void testGetAuthor()
	{
		assertNotNull(classUnderTest.getAuthor());
	}

	@Test
	public void testGetChangeLogFilename()
	{
		assertNotNull(classUnderTest.getChangeLogFileName());
	}

	@Test
	public void testGetHelpFilename()
	{
		assertNotNull(classUnderTest.getHelpFileName());
	}

	@Test
	public void testGetLicenseFilename()
	{
		assertNotNull(classUnderTest.getLicenceFileName());
	}

	@Test
	public void testGetWebsite()
	{
		assertNotNull(classUnderTest.getWebSite());
	}

	@Test
	public void testGetContributors()
	{
		assertNotNull(classUnderTest.getContributors());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLoad() throws Exception
	{
		classUnderTest.load(null);
	}

	@Test
	public void testGetGlobalPreferencePanels()
	{
		// Result should be null or a non-empty array.
		IGlobalPreferencesPanel[] result = classUnderTest.getGlobalPreferencePanels();
		if (result != null)
		{
			Assert.assertTrue(result.length > 0);
		}
	}

	@Test
	public void testGetNewSessionPropertiesPanel()
	{
		// Result should be null or a non-empty array.
		INewSessionPropertiesPanel[] result = classUnderTest.getNewSessionPropertiesPanels();
		if (result != null)
		{
			Assert.assertTrue(result.length > 0);
		}
	}
}
