package net.sourceforge.squirrel_sql.plugins.oracle;
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
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
/**
 * Oracle plugin class.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class OraclePlugin extends DefaultSessionPlugin
{
	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(OraclePlugin.class);

	/** Name of file to store user prefs in. */
//	static final String USER_PREFS_FILE_NAME = "OraclePreferences.xml";

	/** Plugin preferences. */
//	private LAFPreferences _lafPrefs;

	/** Folder to store user settings in. */
	private File _userSettingsFolder;

	/** API for the Obejct Tree. */
	private IObjectTreeAPI _treeAPI;

	/**
	 * Return the internal name of this plugin.
	 *
	 * @return  the internal name of this plugin.
	 */
	public String getInternalName()
	{
		return "oracle";
	}

	/**
	 * Return the descriptive name of this plugin.
	 *
	 * @return  the descriptive name of this plugin.
	 */
	public String getDescriptiveName()
	{
		return "Oracle Plugin";
	}

	/**
	 * Returns the current version of this plugin.
	 *
	 * @return  the current version of this plugin.
	 */
	public String getVersion()
	{
		return "0.10";
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
	 * Load this plugin.
	 *
	 * @param   app	 Application API.
	 */
	public synchronized void load(IApplication app) throws PluginException
	{
		super.load(app);

		// Folder within plugins folder that belongs to this
		// plugin.
		File pluginAppFolder = null;
		try
		{
			pluginAppFolder = getPluginAppSettingsFolder();
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

		// Load plugin preferences.
		loadPrefs();
	}

	/**
	 * Application is shutting down so save preferences.
	 */
	public void unload()
	{
//		try
//		{
//			savePrefs(new File(_userSettingsFolder, USER_PREFS_FILE_NAME));
//		}
//		catch (IOException ex)
//		{
//			s_log.error("Error occured writing to preferences file: "
//							+ USER_PREFS_FILE_NAME,
//						ex);
//		}
//		catch (XMLException ex)
//		{
//			s_log.error("Error occured writing to preferences file: "
//							+ USER_PREFS_FILE_NAME,
//						ex);
//		}
		super.unload();
	}

	/**
	 * Session has been started. If this is an Oracle session then
	 * register an extra expander for the Schema nodes to show
	 * Oracle Packages.
	 * 
	 * @param	session		Session that has started.
	 * 
	 * @return	<TT>true</TT> if session is Oracle in which case this plugin
	 * 							is interested in it.
	 */
	public boolean sessionStarted(ISession session)
	{
		boolean isOracle = false;
		if( super.sessionStarted(session))
		{
			isOracle = isOracle(session);
			if (isOracle)
			{
				_treeAPI = session.getObjectTreeAPI();
				_treeAPI.registerExpander(ObjectTreeNode.IObjectTreeNodeType.SCHEMA, new SchemaExpander());
				_treeAPI.registerExpander(ObjectTreeNode.IObjectTreeNodeType.PACKAGE, new PackageExpander());
			}
		}
		return isOracle;
	}

	private boolean isOracle(ISession session)
	{
		final String ORACLE = "oracle";
		String dbms = null;
		try
		{
			dbms = session.getSQLConnection().getMetaData().getDatabaseProductName();
		}
		catch (BaseSQLException ex)
		{
			s_log.debug("Error in getDatabaseProductName()", ex);
		}
		catch (SQLException ex)
		{
			s_log.debug("Error in getDatabaseProductName()", ex);
		}
		return (dbms != null && dbms.substring(0, ORACLE.length()).equalsIgnoreCase(ORACLE));
	}

	/**
	 * Create Look and Feel preferences panels for the Global Preferences dialog.
	 *
	 * @return  Look and Feel preferences panels.
	 */
//	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
//	{
//		return new IGlobalPreferencesPanel[]
//			{
//				new LAFPreferencesTab(this, _lafRegister),
//				new LAFFontsTab(this, _lafRegister),
//			};
//	}

	/**
	 * Get the preferences info object for this plugin.
	 *
	 * @return	The preferences info object for this plugin.
	 */
//	LAFPreferences getLAFPreferences()
//	{
//		return _lafPrefs;
//	}

	/**
	 * Load from preferences file.
	 */
	private void loadPrefs()
	{
//		final File oldPrefsFile = new File(_userSettingsFolder, OLD_USER_PREFS_FILE_NAME);
//		final File newPrefsFile = new File(_userSettingsFolder, USER_PREFS_FILE_NAME);
//		final boolean oldExists = oldPrefsFile.exists();
//		final boolean newExists = newPrefsFile.exists();

//		try
//		{
//			if (oldExists)
//			{
//				loadOldPrefs(oldPrefsFile);
//				try
//				{
//					_settingsCache.add(_lafPrefs);
//				}
//				catch (DuplicateObjectException ex)
//				{
//					s_log.error("LAFPreferences object already in cache", ex);
//				}
//				savePrefs(newPrefsFile);
//				if (!oldPrefsFile.delete())
//				{
//					s_log.error("Unable to delete old LAF preferences file");
//				}
//				
//			}
//			else if (newExists)
//			{
//				loadNewPrefs(newPrefsFile);
//			}
//		}
//		catch (IOException ex)
//		{
//			s_log.error("Error occured in preferences file", ex);
//		}
//		catch (XMLException ex)
//		{
//			s_log.error("Error occured in preferences file", ex);
//		}

		
//		if (_lafPrefs == null)
//		{
//			_lafPrefs = new LAFPreferences(IdentifierFactory.getInstance().createIdentifier());
//			try
//			{
//				_settingsCache.add(_lafPrefs);
//			}
//			catch (DuplicateObjectException ex)
//			{
//				s_log.error("LAFPreferences object already in cache", ex);
//			}
//		}
	}
}