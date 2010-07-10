package net.sourceforge.squirrel_sql.plugins.sqlval;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;

import net.sourceforge.squirrel_sql.plugins.sqlval.action.*;
/**
 * This plugin provides an interface to the SQL Validation web service provided
 * by Mimer SQL. See http://sqlvalidator.mimer.com/ for more information.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLValidatorPlugin extends DefaultSessionPlugin
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(SQLValidatorPlugin.class);

	private interface IMenuResourceKeys
	{
		String SQLVAL = "sqlval";
	}

	/** Name of preferences file. */
	private static final String USER_PREFS_FILE_NAME = "prefs.xml";

	private static final String PREFS_KEY = "sessionprefs";

	/** Plugin settings. */
	private WebServicePreferences _prefs;

	/** Folder to store user settings in. */
	private File _userSettingsFolder;

	/** Resources for this plugin. */
	private PluginResources _resources;

	/**
	 * Return the internal name of this plugin.
	 *
	 * @return  the internal name of this plugin.
	 */
	public String getInternalName()
	{
		return "sqlval";
	}

	/**
	 * Return the descriptive name of this plugin.
	 *
	 * @return  the descriptive name of this plugin.
	 */
	public String getDescriptiveName()
	{
		return "SQL Validator plugin";
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
	 * Returns a comma separated list of other contributors.
	 *
	 * @return	Contributors names.
	 */
	public String getContributors()
	{
		return "Olof Edlund";
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
		return "readme.html";
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

		_resources = new PluginResources(getClass().getName(), this);

		// Folder to store user settings.
		try
		{
			_userSettingsFolder = getPluginUserSettingsFolder();
		}
		catch (IOException ex)
		{
			throw new PluginException(ex);
		}

		// Load plugin preferences.
		loadPrefs();

		// Add menu.
		final IApplication app = getApplication();
		final ActionCollection coll = app.getActionCollection();
		coll.add(new ConnectAction(app, _resources, _prefs, this));
		coll.add(new DisconnectAction(app, _resources, _prefs, this));
		coll.add(new ValidateSQLAction(app, _resources, _prefs, this));
		createMenu();
	}

	/**
	 * Application is shutting down so save preferences.
	 */
	public void unload()
	{
		savePrefs();
		super.unload();
	}

	/**
	 * Called when a session started.
	 *
	 * @param	session	The session that is starting.
	 */
	public void sessionCreated(ISession session)
	{
		super.sessionStarted(session);
		WebServiceSessionProperties props = new WebServiceSessionProperties(_prefs);
		props.setSQLConnection(session.getSQLConnection());
		session.putPluginObject(this, PREFS_KEY, props);
	}

	/**
	 * Called when a session shutdown.
	 * 
	 * @param	session	The session that is ending.
	 */
	public void sessionEnding(ISession session)
	{
		getWebServiceSessionProperties(session).getWebServiceSession().close();
		session.removePluginObject(this, PREFS_KEY);
		super.sessionEnding(session);
	}

	/**
	 * Create panel for the Global Properties dialog.
	 * 
	 * @return	properties panel.
	 */
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
	{
		return new IGlobalPreferencesPanel[]
		{
			new ValidatorGlobalPreferencesTab(_prefs),		};
	}

	PluginResources getResources()
	{
		return _resources;
	}

	public WebServiceSessionProperties getWebServiceSessionProperties(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		return (WebServiceSessionProperties)session.getPluginObject(this, PREFS_KEY);
	}

	/**
	 * Load from preferences file.
	 */
	private void loadPrefs()
	{
		try
		{
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(new File(_userSettingsFolder, USER_PREFS_FILE_NAME),
								getClass().getClassLoader());
			Iterator it = doc.iterator();
			if (it.hasNext())
			{
				_prefs = (WebServicePreferences)it.next();
			}
		}
		catch (FileNotFoundException ignore)
		{
			s_log.info(USER_PREFS_FILE_NAME + " not found - will be created");
		}
		catch (Exception ex)
		{
			s_log.error("Error occured reading from preferences file: "
					+ USER_PREFS_FILE_NAME, ex);
		}
		if (_prefs == null)
		{
			_prefs = new WebServicePreferences();
		}

		_prefs.setClientName(Version.getApplicationName() + "/" + getDescriptiveName());
		_prefs.setClientVersion(Version.getShortVersion() + "/" + getVersion());
	}

	/**
	 * Save preferences to disk.
	 */
	private void savePrefs()
	{
		try
		{
			XMLBeanWriter wtr = new XMLBeanWriter(_prefs);
			wtr.save(new File(_userSettingsFolder, USER_PREFS_FILE_NAME));
		}
		catch (Exception ex)
		{
			s_log.error("Error occured writing to preferences file: "
					+ USER_PREFS_FILE_NAME, ex);
		}
	}

	private void createMenu()
	{
		final IApplication app = getApplication();
		final ActionCollection coll = app.getActionCollection();

		final JMenu menu = _resources.createMenu(IMenuResourceKeys.SQLVAL);
		_resources.addToMenu(coll.get(ConnectAction.class), menu);
		_resources.addToMenu(coll.get(DisconnectAction.class), menu);
		_resources.addToMenu(coll.get(ValidateSQLAction.class), menu);

		app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, menu);
	}
}
