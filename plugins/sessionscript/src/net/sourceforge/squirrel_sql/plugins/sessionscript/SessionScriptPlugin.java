package net.sourceforge.squirrel_sql.plugins.sessionscript;
/*
 * Copyright (C) 2002 Colin Bell
 * colbell@users.sourceforge.net
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
import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;

/**
 * The plugin class.
 */
public class SessionScriptPlugin extends DefaultSessionPlugin
{
	private interface IMenuResourceKeys
	{
		String SCRIPTS = "scripts";
	}

	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(SessionScriptPlugin.class);

	/** The app folder for this plugin. */
	private File _pluginAppFolder;

	/** Folder to store user settings in. */
	private File _userSettingsFolder;

	/** Cache of session scripts. */
	private AliasScriptCache _cache;

	private PluginResources _resources;

	/**
	 * Return the internal name of this plugin.
	 *
	 * @return  the internal name of this plugin.
	 */
	public String getInternalName()
	{
		return "sessionscript";
	}

	/**
	 * Return the descriptive name of this plugin.
	 *
	 * @return  the descriptive name of this plugin.
	 */
	public String getDescriptiveName()
	{
		return "Session Scripts Plugin";
	}

	/**
	 * Returns the current version of this plugin.
	 *
	 * @return  the current version of this plugin.
	 */
	public String getVersion()
	{
		return "0.12";
	}

	/**
	 * Returns the authors name.
	 *
	 * @return  the authors name.
	 */
	public String getAuthor()
	{
		return "Colin Bell";
	}

	/**
	 * Returns the name of the change log for the plugin. This should
	 * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
	 * directory.
	 *
	 * @return	the changelog file name or <TT>null</TT> if plugin doesn't have
	 * 			a change log.
	 */
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	/**
	 * Returns the name of the Help file for the plugin. This should
	 * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
	 * directory.
	 *
	 * @return	the Help file name or <TT>null</TT> if plugin doesn't have
	 * 			a help file.
	 */
	public String getHelpFileName()
	{
		return "readme.txt";
	}

	/**
	 * Returns the name of the Licence file for the plugin. This should
	 * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
	 * directory.
	 *
	 * @return	the Licence file name or <TT>null</TT> if plugin doesn't have
	 * 			a licence file.
	 */
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
		IApplication app = getApplication();

		PluginManager pmgr = app.getPluginManager();

		// Folder within plugins folder that belongs to this
		// plugin.
		try
		{
			_pluginAppFolder = getPluginAppSettingsFolder();
		}
		catch (IOException ex)
		{
			throw new PluginException(ex);
		}

		// Folder to store user settings.
		try
		{
			_userSettingsFolder = getPluginUserSettingsFolder();
		}
		catch (IOException ex)
		{
			throw new PluginException(ex);
		}

		_resources = new SessionScriptResources(
			"net.sourceforge.squirrel_sql.plugins.sessionscript.sessionscript",
			this);

		ActionCollection coll = app.getActionCollection();
		ViewSessionScriptsAction action = new ViewSessionScriptsAction(app, _resources, this);
		coll.add(action);
		app.addToMenu(IApplication.IMenuIDs.PLUGINS_MENU, action);

		try
		{
			_cache = new AliasScriptCache(this);
		}
		catch (IOException ex)
		{
			throw new PluginException(ex);
		}
		_cache.load();

	}

	/**
	 * Application is shutting down so save data.
	 */
	public void unload()
	{
		if (_cache != null)
		{
			_cache.save();
		}
		super.unload();
	}

	public boolean sessionStarted(ISession session)
	{
		boolean rc = false;

		AliasScript script = (AliasScript)_cache.get(session.getAlias());
		if (script != null)
		{
			String sql = script.getSQL();
			if (sql != null && sql.length() > 0)
			{
				rc = true;
				ISQLPanelAPI api = session.getSQLPanelAPI(this);
				api.setEntireSQLScript(sql);
				api.executeCurrentSQL();
			}
		}

		return rc;
	}

	/**
	 * Return the scripts cache.
	 * 
	 * @return	The scripts cache.
	 */
	AliasScriptCache getScriptsCache()
	{
		return _cache;
	}
}
