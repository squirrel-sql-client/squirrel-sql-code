package net.sourceforge.squirrel_sql.plugins.laf;
/*
 * Copyright (C) 2001 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;

/**
 * The Look and Feel plugin class.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class LAFPlugin extends DefaultPlugin {
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(LAFPlugin.class);

	/** Plugin preferences. */
	private LAFPreferences _lafPrefs;

	/** A register of Look and Feels. */
	private LAFRegister _lafRegister;

	/** The folder that contains LAF jars. */
	private File _lafFolder;

	/** The folder that contains Skin LAF theme pack jars. */
	private File _themePacksFolder;

	/** Folder to store user settings in. */
	private File _userSettingsFolder;

	/**
	 * Return the internal name of this plugin.
	 *
	 * @return  the internal name of this plugin.
	 */
	public String getInternalName() {
		return "laf";
	}

	/**
	 * Return the descriptive name of this plugin.
	 *
	 * @return  the descriptive name of this plugin.
	 */
	public String getDescriptiveName() {
		return "Look & Feel Plugin";
	}

	/**
	 * Returns the current version of this plugin.
	 *
	 * @return  the current version of this plugin.
	 */
	public String getVersion() {
		return "0.20";
	}

	/**
	 * Returns the authors name.
	 *
	 * @return  the authors name.
	 */
	public String getAuthor() {
		return "Colin Bell";
	}

	/**
	 * Load this plugin.
	 *
	 * @param   app	 Application API.
	 */
	public synchronized void load(IApplication app) throws PluginException {
		super.load(app);

		// Folder within plugins folder that belongs to this
		// plugin.
		File pluginAppFolder = null;
		try {
			pluginAppFolder = getPluginAppSettingsFolder();
		} catch (IOException ex) {
			throw new PluginException(ex);
		}

		// Folder that stores Look and Feel jars.
		_lafFolder = new File(pluginAppFolder, "lafs");
		if (!_lafFolder.exists()) {
			_lafFolder.mkdir();
		}

		// Folder that stores themepacks for the Skin
		// Look and Feel.
		_themePacksFolder = new File(pluginAppFolder, "skinlf-theme-packs");
		if (!_themePacksFolder.exists()) {
			_themePacksFolder.mkdir();
		}

		// Folder to store user settings.
		try {
			_userSettingsFolder = getPluginUserSettingsFolder();
		} catch (IOException ex) {
			throw new PluginException(ex);
		}

		// Load plugin preferences.
		loadPrefs();

		// Create the Look and Feel register.
		_lafRegister = new LAFRegister(app, this);
	}

	/**
	 * Plugin initialization. Main frame has now been created so we can
	 * set the font for its status bar.
	 */
	public void initialize() {
		_lafRegister.updateStatusBarFont();
	}

	/**
	 * Application is shutting down so save preferences.
	 */
	public void unload() {
		savePrefs();
		super.unload();
	}

	/**
	 * Create Look and Feel preferences panel for the Global Preferences dialog.
	 *
	 * @return  Look and Feel preferences panel.
	 */
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
		// The fonts panel must be before the LAFPreferences panel
		// because the font info must be updated prior to the
		// LAF. If this isn't done this way the fonts
		// don't change on the screen.
		return new IGlobalPreferencesPanel[] {
			 new LAFFontsPanel(this, _lafRegister),
			 new LAFPreferencesPanel(this, _lafRegister),
		};
	}

	/**
	 * Return the folder that contains LAF jars.
	 *
	 * @return  folder as <TT>File</TT> that contains LAF jars.
	 */
	File getLookAndFeelFolder() {
		return _lafFolder;
	}

	/**
	 * Return the folder that contains Skin Theme packs.
	 *
	 * @return  folder (as <TT>File</TT>) that contains Skin Theme packs.
	 */
	File getSkinThemePackFolder() {
		return _themePacksFolder;
	}

	/**
	 * Get the preferences info object for this plugin.
	 *
	 * @return	The preferences info object for this plugin.
	 */
	LAFPreferences getLAFPreferences() {
		return _lafPrefs;
	}

	/**
	 * Load from preferences file.
	 */
	private void loadPrefs() {
		try {
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(
				new File(_userSettingsFolder, LAFConstants.USER_PREFS_FILE_NAME),
								getClass().getClassLoader());
			Iterator it = doc.iterator();
			if (it.hasNext()) {
				_lafPrefs = (LAFPreferences) it.next();
			}
		} catch (FileNotFoundException ignore) {
			// property file not found for user - first time user ran pgm.
		} catch (Exception ex) {
			s_log.error("Error occured reading from preferences file: "
					+ LAFConstants.USER_PREFS_FILE_NAME, ex);
		}
		if (_lafPrefs == null) {
			_lafPrefs = new LAFPreferences();
		}
	}

	/**
	 * Save preferences to disk.
	 */
	private void savePrefs() {
		try {
			XMLBeanWriter wtr = new XMLBeanWriter(_lafPrefs);
			wtr.save(new File(_userSettingsFolder, LAFConstants.USER_PREFS_FILE_NAME));
		} catch (Exception ex) {
			s_log.error("Error occured writing to preferences file: "
					+ LAFConstants.USER_PREFS_FILE_NAME, ex);
		}
	}
}