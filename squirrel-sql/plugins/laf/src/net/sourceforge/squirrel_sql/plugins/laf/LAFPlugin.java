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

import net.sourceforge.squirrel_sql.fw.util.Logger;
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
    /** Plugin preferences. */
    private LAFPreferences _lafPrefs;

    /** A register of Look and Feels. */
    private LAFRegister _lafRegister;

    /** The app folder for this plugin. */
    private File _pluginAppFolder;

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
        return "0.1";
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
     * @param   app     Application API.
     */
    public synchronized void load(IApplication app) throws PluginException {
        super.load(app);
        PluginManager pmgr = app.getPluginManager();

        // Folder within plugins folder that belongs to this
        // plugin.
        try {
            _pluginAppFolder = getPluginAppSettingsFolder();
        } catch (IOException ex) {
            throw new PluginException(ex);
        }

        // Folder that stores Look and Feel jars.
        _lafFolder = new File(_pluginAppFolder, "lafs");
        if (!_lafFolder.exists()) {
            _lafFolder.mkdir();
        }

        // Folder that stores themepacks for the Skin
        // Look and Feel.
        _themePacksFolder = new File(_pluginAppFolder, "theme_packs");
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
        return new IGlobalPreferencesPanel[] {
             new LAFPreferencesPanel(this, _lafRegister)};
    }

    /**
     * Return the app folder for this plugin.
     *
     * @return  the app folder (as <TT>File</TT>) for this plugin.
     */
    File getPluginAppFolder() {
        return _pluginAppFolder;
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
     * @return  The preferences info object for this plugin.
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
            Logger logger = getApplication().getLogger();
            logger.showMessage(
                Logger.ILogTypes.ERROR,
                "Error occured reading from preferences file: "
                    + LAFConstants.USER_PREFS_FILE_NAME);
            //i18n
            logger.showMessage(Logger.ILogTypes.ERROR, ex);
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
            Logger logger = getApplication().getLogger();
            logger.showMessage(
                Logger.ILogTypes.ERROR,
                "Error occured writing to preferences file: "
                    + LAFConstants.USER_PREFS_FILE_NAME);
            //i18n
            logger.showMessage(Logger.ILogTypes.ERROR, ex);
        }
    }
}