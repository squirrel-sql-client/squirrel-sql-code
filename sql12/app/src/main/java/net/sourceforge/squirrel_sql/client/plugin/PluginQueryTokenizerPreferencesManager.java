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
package net.sourceforge.squirrel_sql.client.plugin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactoryImpl;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;

/**
 * This is intended to be used by plugins that have custom IQueryTokenizers, which require some sort of
 * preference management (loading/storing preference beans to XML file and vice-versa). In reality this
 * functionality isn't specific to IQueryTokenizer preferences, so this could be further generalized. There is
 * no need at the present time to do so.
 * 
 * @author manningr
 */
public class PluginQueryTokenizerPreferencesManager
{

	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(PluginQueryTokenizerPreferencesManager.class);

	/** Name of preferences file. */
	private static final String USER_PREFS_FILE_NAME = "prefs.xml";

	/** Folder to store user settings in. */
	private FileWrapper _userSettingsFolder;

	/** The bean we will be loading from / storing to */
	private IQueryTokenizerPreferenceBean _prefs = null;

	/** Tells us which file to store the preference bean in */
	private IPlugin plugin = null;

	/** whether or not we've been initialized */
	private boolean _initialized = false;

	/** factory for creating FileWrappers which insulate the application from direct reference to File */
	private static FileWrapperFactory fileWrapperFactory = new FileWrapperFactoryImpl();

	/**
	 * Constructor.
	 */
	public PluginQueryTokenizerPreferencesManager()
	{
		/* Do Nothing */
	}

	/**
	 * Initializes this preference manager. This must be done prior to calling other methods.
	 * 
	 * @param thePlugin
	 *           which plugin we are handling preferences for.
	 * @param defaultPrefsBean
	 *           the bean to use if no preference file currently exists.
	 * @throws PluginException
	 */
	public void initialize(IPlugin thePlugin, IQueryTokenizerPreferenceBean defaultPrefsBean)
		throws PluginException
	{
		if (thePlugin == null)
		{
			throw new IllegalArgumentException("IPlugin arguement cannot be null");
		}
		if (defaultPrefsBean == null)
		{
			throw new IllegalArgumentException("IQueryTokenizerPreferenceBean arguement cannot be null");
		}
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
		_prefs = defaultPrefsBean;
		loadPrefs();
		_initialized = true;
	}

	/**
	 * Returns the preferences bean that this class manages.
	 * 
	 * @return an implementation instance of IQueryTokenizerPreferenceBean
	 */
	public IQueryTokenizerPreferenceBean getPreferences()
	{
		if (!_initialized)
		{
			throw new IllegalStateException("initialize() must be called first");
		}
		return _prefs;
	}

	/**
	 * Saves the preferences
	 */
	public void unload()
	{
		savePrefs();
	}

	/**
	 * Save preferences to disk.
	 */
	public void savePrefs()
	{
		if (!_initialized)
		{
			throw new IllegalStateException("initialize() must be called first");
		}
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
	 * @param fileWrapperFactory
	 *           the fileWrapperFactory to set
	 */
	public void setFileWrapperFactory(FileWrapperFactory fileWrapperFactory)
	{
		Utilities.checkNull("setFileWrapperFactory", "fileWrapperFactory", fileWrapperFactory);
		PluginQueryTokenizerPreferencesManager.fileWrapperFactory = fileWrapperFactory;
	}

	/**
	 * Load from preferences file.
	 */
	private void loadPrefs()
	{
		try
		{
			XMLBeanReader doc = new XMLBeanReader();

			FileWrapper prefFile = PreferenceUtil.getPreferenceFileToReadFrom(plugin);

			doc.load(prefFile, _prefs.getClass().getClassLoader());

			Iterator<Object> it = doc.iterator();

			if (it.hasNext())
			{
				_prefs = (IQueryTokenizerPreferenceBean) it.next();
			}
		}
		catch (FileNotFoundException ignore)
		{
			s_log.info(USER_PREFS_FILE_NAME + " not found - will be created");
		}
		catch (Exception ex)
		{
			s_log.error("Error occured reading from preferences file: " + USER_PREFS_FILE_NAME, ex);
		}

		_prefs.setClientName(Version.getApplicationName() + "/" + plugin.getDescriptiveName());
		_prefs.setClientVersion(Version.getShortVersion() + "/" + plugin.getVersion());
	}

}
