/*
 * Copyright (C) 2008 Dieter Engelhardt
 * dieter@ew6.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.squirrel_sql.plugins.sqlreplace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallbackAdaptor;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Main entry into the SQL Replace plugin. <p/> This plugin allows you to maintain a set of frequently used
 * SQL scripts for easy playback. There is also a parameter replacement syntax available for the SQL files.
 * 
 * @author Dieter
 */
public class SQLReplacePlugin extends DefaultSessionPlugin
{

	Map<String, String> cache;

	/**
	 * Remember which sqlpanelapis we've registered listeners with so that we can unregister them when it's
	 * time to unload.
	 */
	HashMap<ISQLPanelAPI, ISQLExecutionListener> panelListenerMap =
		new HashMap<ISQLPanelAPI, ISQLExecutionListener>();

	/**
	 * Loggers for this class
	 */
	private final static ILogger log = LoggerController.createLogger(SQLReplacePlugin.class);

	public final static String RESOURCE_PATH = "net.sourceforge.squirrel_sql.plugins.sqlreplace.sqlreplace";

	private static ILogger logger = LoggerController.createLogger(SQLReplacePlugin.class);

	/**
	 * The app folder for this plugin.
	 */
	@SuppressWarnings("unused")
	private File pluginAppFolder;

	private PluginResources resources;

	private ReplacementManager replacementManager;

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getAuthor()
	 */
	public String getAuthor()
	{
		return "Dieter Engelhardt";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getDescriptiveName()
	 */
	public String getDescriptiveName()
	{
		return "SQLReplace Plugin";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getInternalName()
	 */
	public String getInternalName()
	{
		return "sqlreplace";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getVersion()
	 */
	public String getVersion()
	{
		return "0.0.1";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#getChangeLogFileName()
	 */
	@Override
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#getHelpFileName()
	 */
	@Override
	public String getHelpFileName()
	{
		return "readme.txt";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#getLicenceFileName()
	 */
	@Override
	public String getLicenceFileName()
	{
		return "licence.txt";
	}

	/**
	 * Initialize this plugin.
	 */
	public synchronized void initialize() throws PluginException
	{
		super.initialize();

		// Folder within plugins folder that belongs to this
		// plugin.
		try
		{
			pluginAppFolder = getPluginAppSettingsFolder();
		}
		catch (IOException ex)
		{
			throw new PluginException(ex);
		}

		// Load resources such as menu items, etc...
		resources = new SQLReplaceResources(RESOURCE_PATH, this);
		replacementManager = new ReplacementManager(this);
		// Load plugin preferences.
		try
		{
			replacementManager.load();
		}
		catch (IOException e)
		{
			if (!(e instanceof FileNotFoundException))
			{
				logger.error("Problem loading replacementManager", e);
			}
		}

	}

	/**
	 * @return ReplacementManager
	 */
	ReplacementManager getReplacementManager()
	{
		return replacementManager;
	}

	/**
	 * Get and return a string from the plugin resources.
	 * 
	 * @param name
	 *           name of the resource string to return.
	 * @return resource string.
	 */
	protected String getResourceString(String name)
	{
		return resources.getString(name);
	}

	/**
	 * Create and return a preferences object.
	 * 
	 * @return The global preferences object.
	 */
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
	{
		return new IGlobalPreferencesPanel[] { new SQLReplacePreferencesController(this) };
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.ISessionPlugin#sessionStarted(net.sourceforge.squirrel_sql.client.session.ISession)
	 */
	public PluginSessionCallback sessionStarted(ISession session)
	{
		try
		{
			ISQLPanelAPI sqlPaneAPI = session.getSessionSheet().getSQLPaneAPI();

			initSQLReplace(sqlPaneAPI, session);

			return new PluginSessionCallbackAdaptor(this);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Called on plugin unloading.
	 */
	public void unload()
	{
		for (ISQLPanelAPI api : panelListenerMap.keySet())
		{
			api.removeSQLExecutionListener(panelListenerMap.get(api));
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin#allowsSessionStartedInBackground()
	 */
	public boolean allowsSessionStartedInBackground()
	{
		return true;
	}

	/**
	 * Called on session creating by callback.
	 * 
	 * @param session
	 *           The session
	 */
	@Override
	public void sessionCreated(ISession session)
	{
		try
		{
			replacementManager.load();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Now we register our SQLLIstener to catch the executionevent
	 * 
	 * @param sqlPaneAPI
	 * @param session
	 */
	private void initSQLReplace(final ISQLPanelAPI sqlPaneAPI, final ISession session)
	{
		final SQLReplacePlugin plugin = this;

		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				log.info("Adding SQL execution listener.");
				ISQLExecutionListener listener = new SQLReplaceExecutionListener(plugin, session);
				sqlPaneAPI.addSQLExecutionListener(listener);
				panelListenerMap.put(sqlPaneAPI, listener);
			}

		});
	}

	/**
	 * This method is called on session closing an needs to free resources.
	 * 
	 * @param session
	 *           the session to be closed
	 */
	@Override
	public void sessionEnding(ISession session)
	{
		ISQLPanelAPI sqlPaneAPI = session.getSessionSheet().getSQLPaneAPI();
		ISQLExecutionListener listener = panelListenerMap.remove(sqlPaneAPI);
		sqlPaneAPI.removeSQLExecutionListener(listener);
	}

}
