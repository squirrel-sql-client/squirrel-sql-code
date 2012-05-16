package net.sourceforge.squirrel_sql.plugins.multisource;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import net.sourceforge.squirrel_sql.client.plugin.AbstractSessionPluginTest;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;

import org.junit.Test;

/**
 * This test is for MultiSource Plugin.
 */
public class MultiSourcePluginTest extends AbstractSessionPluginTest
{
	@Override
	protected IPlugin getPluginToTest() throws Exception
	{
		return new MultiSourcePlugin();
	}	

	/**
	 *  Return the database name of sessions this plugin is interested in. 
	 */
	@Override
	protected String getDatabaseProductName()
	{
		return "UnityJDBC";
	}

	/**
	 * No version is required.
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
		((MultiSourcePlugin) classUnderTest).sessionStarted(mockSession);
	}

	@Test
	public void testSessionStartedNullApplication() throws Exception
	{
		try
		{
			when(mockSession.getApplication()).thenReturn(null);
			classUnderTest.initialize();
			((MultiSourcePlugin) classUnderTest).sessionStarted(mockSession);
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
