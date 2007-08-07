package net.sourceforge.squirrel_sql.plugins.laf;
/*
 * Copyright (C) 2001-2006 Colin Bell
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

import javax.swing.JTabbedPane;

import com.jgoodies.looks.Options;

import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactoryAdapter;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactoryComponentCreatedEvent;
import net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
/**
 * The Look and Feel plugin class.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class LAFPlugin extends DefaultPlugin
{
	/** Logger for this class. */
	private final static ILogger
			s_log = LoggerController.createLogger(LAFPlugin.class);

	/** Old name of file to store user prefs in. Replaced by USER_PREFS_FILE_NAME. */
	static final String OLD_USER_PREFS_FILE_NAME = "LAFPrefs.xml";

	/** Name of file to store user prefs in. */
	static final String USER_PREFS_FILE_NAME = "LAFPreferences.xml";

	/** Resources for this plugin. */
	private LAFPluginResources _resources;

	/** Plugin preferences. */
	private LAFPreferences _lafPrefs;

	/** A register of Look and Feels. */
	private LAFRegister _lafRegister;

	/** The folder that contains LAF jars. */
	private File _lafFolder;

	/** Folder to store user settings in. */
	private File _userSettingsFolder;

	/** Folder to store extra LAFs supplied by the user. */
	private File _userExtraLAFFolder;

	/** Cache of settings for the plugin. */
	private final XMLObjectCache<LAFPreferences> _settingsCache = new XMLObjectCache<LAFPreferences>();

	/**
	 * Return the internal name of this plugin.
	 *
	 * @return  the internal name of this plugin.
	 */
	public String getInternalName()
	{
		return "laf";
	}

	/**
	 * Return the descriptive name of this plugin.
	 *
	 * @return  the descriptive name of this plugin.
	 */
	public String getDescriptiveName()
	{
		return "Look & Feel Plugin";
	}

	/**
	 * Returns the current version of this plugin.
	 *
	 * @return  the current version of this plugin.
	 */
	public String getVersion()
	{
		return "1.1";
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
		return "licences.html";
	}

	/**
	 * Load this plugin.
	 *
	 * @param   app	 Application API.
	 */
	public synchronized void load(IApplication app) throws PluginException
	{
		super.load(app);

		// Load resources.
		_resources = new LAFPluginResources(this);

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

		// Folder that stores Look and Feel jars.
		_lafFolder = new File(pluginAppFolder, "lafs");
		if (!_lafFolder.exists())
		{
			_lafFolder.mkdir();
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

		// Folder to contain extra LAFs supplied by the user.
		_userExtraLAFFolder = new File(_userSettingsFolder, ILAFConstants.USER_EXTRA_LAFS_FOLDER); 

		// Create empty required files in user settings directory.
		createEmptyRequiredUserFiles();

		// Load plugin preferences.
		loadPrefs();

		// Setup preferences to make jGoodies LAF look better.
//		UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
//		UIManager.put(Options.USE_NARROW_BUTTONS_KEY, Boolean.TRUE);

		// Create the Look and Feel register.
		_lafRegister = new LAFRegister(app, this);

		// Listen for GUI components being created.
		UIFactory.getInstance().addListener(new UIFactoryListener());

		// Update font used for status bars.
		_lafRegister.updateStatusBarFont();
	}

	/**
	 * Application is shutting down so save preferences.
	 */
	public void unload()
	{
		try
		{
			savePrefs(new File(_userSettingsFolder, USER_PREFS_FILE_NAME));
		}
		catch (IOException ex)
		{
			s_log.error("Error occured writing to preferences file: "
							+ USER_PREFS_FILE_NAME,
						ex);
		}
		catch (XMLException ex)
		{
			s_log.error("Error occured writing to preferences file: "
							+ USER_PREFS_FILE_NAME,
						ex);
		}
		super.unload();
	}

	/**
	 * Create Look and Feel preferences panels for the Global Preferences dialog.
	 *
	 * @return  Look and Feel preferences panels.
	 */
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
	{
		return new IGlobalPreferencesPanel[]
			{
				new LAFPreferencesTab(this, _lafRegister),
				new LAFFontsTab(this, _lafRegister),
			};
	}

	/**
	 * Return the folder that contains LAF jars.
	 *
	 * @return  folder as <TT>File</TT> that contains LAF jars.
	 */
	File getLookAndFeelFolder()
	{
		return _lafFolder;
	}

	/**
	 * Retrieve the directory that contains the extra LAFs supplied
	 * by the user.
	 * 
	 * @return	folder as <TT>File</TT> that contains the extra LAFs supplied
	 * 			by the user.
	 */
	File getUsersExtraLAFFolder()
	{
		return _userExtraLAFFolder;
	}

	/**
	 * Get the preferences info object for this plugin.
	 *
	 * @return	The preferences info object for this plugin.
	 */
	LAFPreferences getLAFPreferences()
	{
		return _lafPrefs;
	}

	/**
	 * Retrieve plugins resources.
	 * 
	 * @return	Plugins resources.
	 */
	PluginResources getResources()
	{
		return _resources;
	}

	XMLObjectCache<LAFPreferences> getSettingsCache()
	{
		return _settingsCache;
	}

	/**
	 * Load from preferences file.
	 */
	private void loadPrefs()
	{
		final File oldPrefsFile = new File(_userSettingsFolder, OLD_USER_PREFS_FILE_NAME);
		final File newPrefsFile = new File(_userSettingsFolder, USER_PREFS_FILE_NAME);
		final boolean oldExists = oldPrefsFile.exists();
		final boolean newExists = newPrefsFile.exists();

		try
		{
			if (oldExists)
			{
				loadOldPrefs(oldPrefsFile);
				try
				{
					_settingsCache.add(_lafPrefs);
				}
				catch (DuplicateObjectException ex)
				{
					s_log.error("LAFPreferences object already in cache", ex);
				}
				savePrefs(newPrefsFile);
				if (!oldPrefsFile.delete())
				{
					s_log.error("Unable to delete old LAF preferences file");
				}
				
			}
			else if (newExists)
			{
				loadNewPrefs(newPrefsFile);
			}
		}
		catch (IOException ex)
		{
			s_log.error("Error occured in preferences file", ex);
		}
		catch (XMLException ex)
		{
			s_log.error("Error occured in preferences file", ex);
		}

		
		if (_lafPrefs == null)
		{
			_lafPrefs = new LAFPreferences(IdentifierFactory.getInstance().createIdentifier());
         _lafPrefs.setLookAndFeelClassName(PlasticLookAndFeelController.DEFAULT_LOOK_AND_FEEL_CLASS_NAME);
			try
			{
				_settingsCache.add(_lafPrefs);
			}
			catch (DuplicateObjectException ex)
			{
				s_log.error("LAFPreferences object already in cache", ex);
			}
		}
	}

	/**
	 * Load preferences from the old file format.
	 * 
	 * @param	oldPrefsFile	File containing the preferences info.
	 * 
	 * @throws	XMLException	Thrown if an error occurs eradign the rpeferences data.
	 */
	private void loadOldPrefs(File oldPrefsFile) throws XMLException
	{
		try
		{
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(oldPrefsFile, getClass().getClassLoader());
			Iterator<?> it = doc.iterator();
			if (it.hasNext())
			{
				_lafPrefs = (LAFPreferences) it.next();
			}
		}
		catch (FileNotFoundException ignore)
		{
			// property file not found for user - first time user ran pgm.
		}
	}

	/**
	 * Load preferences from the new file format.
	 * 
	 * @param	newPerfsFile	File containing the preferences information.
	 * 
	 * @throws	XMLException	Thrown if error reading preferences file.
	 */
	private void loadNewPrefs(File newPrefsFile) throws XMLException
	{
		try
		{
			try
			{
				_settingsCache.load(newPrefsFile.getPath(), getClass().getClassLoader());
			}
			catch (DuplicateObjectException ex)
			{
				s_log.error("Cache should have been empty", ex);
			}
			Iterator<LAFPreferences> it = 
                _settingsCache.getAllForClass(LAFPreferences.class);
			if (it.hasNext())
			{
				_lafPrefs = it.next();
			}
			else
			{
				s_log.error("LAFPreferences object not loaded");
			}
		}
		catch (FileNotFoundException ignore)
		{
			// property file not found for user - first time user ran pgm.
		}
	}

	/**
	 * Save preferences to disk.
	 * 
	 * @param	prefsFile	File to save preferences to.
	 */
	private void savePrefs(File prefsFile)
		throws IOException, XMLException
	{
		_settingsCache.save(prefsFile.getPath());
	}

	private void createEmptyRequiredUserFiles()
	{
		_userExtraLAFFolder.mkdirs();

		File file = new File(_userExtraLAFFolder, ILAFConstants.USER_EXTRA_LAFS_PROPS_FILE);
		try
		{
			file.createNewFile();
		}
		catch (IOException ex)
		{
			s_log.error("Error creating file " + file.getAbsolutePath(), ex);
		}
	}

	private static class UIFactoryListener extends UIFactoryAdapter
	{
		/**
		 * A tabbed panel object has been created.
		 * 
		 * @param	evt	event object.
		 */
		public void tabbedPaneCreated(UIFactoryComponentCreatedEvent evt)
		{
			final JTabbedPane pnl = (JTabbedPane)evt.getComponent();
			pnl.putClientProperty(Options.NO_CONTENT_BORDER_KEY, Boolean.TRUE);
		}
	}
}
