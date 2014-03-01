/*
 * Copyright (C) 2005 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.dbcopy.prefs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PreferenceUtil;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactoryImpl;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;

public class PreferencesManager
{

	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(PreferencesManager.class);

	/** Name of preferences file. */
	private static final String USER_PREFS_FILE_NAME = "prefs.xml";

	/** Folder to store user settings in. */
	private static FileWrapper _userSettingsFolder;

	/** factory for creating FileWrappers which insulate the application from direct reference to File */
	private static FileWrapperFactory fileWrapperFactory = new FileWrapperFactoryImpl();

	private static DBCopyPreferenceBean _prefs = null;

	private static IPlugin plugin = null;

	public static void initialize(IPlugin thePlugin) throws PluginException
	{
		plugin = thePlugin;

		// Folder to store user settings.
		try
		{
			_userSettingsFolder = plugin.getPluginUserSettingsFolder();
		}
		catch (IOException ex)
		{
			throw new PluginException(ex);
		}

		loadPrefs();
	}

	public static DBCopyPreferenceBean getPreferences()
	{
		return _prefs;
	}

	public static void unload()
	{
		savePrefs();
	}

	/**
	 * @param fileWrapperFactory the fileWrapperFactory to set
	 */
	public static void setFileWrapperFactory(FileWrapperFactory fileWrapperFactory)
	{
		Utilities.checkNull("setFileWrapperFactory", "fileWrapperFactory", fileWrapperFactory);
		PreferencesManager.fileWrapperFactory = fileWrapperFactory;
	}
		
	/**
	 * Save preferences to disk. Always write to the user settings folder, not the application settings folder.
	 */
	public static void savePrefs()
	{
		try
		{
			XMLBeanWriter wtr = new XMLBeanWriter(_prefs);
			wtr.save(fileWrapperFactory.create(_userSettingsFolder, USER_PREFS_FILE_NAME));
		}
		catch (Exception ex)
		{
			s_log.error("Error occured writing to preferences file: " + USER_PREFS_FILE_NAME, ex);
		}
	}

	/**
	 * Load from preferences file.
	 */
	private static void loadPrefs()
	{
		FileWrapper prefFile = null;
		try
		{
			XMLBeanReader doc = new XMLBeanReader();

			prefFile = PreferenceUtil.getPreferenceFileToReadFrom(plugin);

			doc.load(prefFile, DBCopyPreferenceBean.class.getClassLoader());

			Iterator<Object> it = doc.iterator();
			if (it.hasNext())
			{
				_prefs = (DBCopyPreferenceBean) it.next();
			}
		}
		catch (FileNotFoundException ignore)
		{
			s_log.info(USER_PREFS_FILE_NAME + "(" + prefFile.getAbsolutePath() + ") not found - will be created");
		}
		catch (Exception ex)
		{
			s_log.error("Error occured reading from preferences file: " + USER_PREFS_FILE_NAME, ex);
		}
		if (_prefs == null)
		{
			_prefs = new DBCopyPreferenceBean();
		}

		_prefs.setClientName(Version.getApplicationName() + "/" + plugin.getDescriptiveName());
		_prefs.setClientVersion(Version.getShortVersion() + "/" + plugin.getVersion());
	}

}
