package net.sourceforge.squirrel_sql.plugins.dataimport;
/*
 * Copyright (C) 2001 Like Gao
 * lgao@gmu.edu
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

import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;

public class DataImportPlugin extends DefaultSessionPlugin {
    /** Plugin preferences. */
//    private Preferences _lafPrefs;

    /** The app folder for this plugin. */
    private File _pluginAppFolder;

    /** Folder to store user settings in. */
    private File _userSettingsFolder;

    /**
     * Return the internal name of this plugin.
     *
     * @return  the internal name of this plugin.
     */
    public String getInternalName() {
        return "dataimport";
    }

    /**
     * Return the descriptive name of this plugin.
     *
     * @return  the descriptive name of this plugin.
     */
    public String getDescriptiveName() {
        return "Data Import Plugin";
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
        return "Like Gao";
    }

    /**
     * Initialize this plugin.
     */
    public synchronized void initialize() throws PluginException {
        super.initialize();
        // Folder within plugins folder that belongs to this
        // plugin.
        try {
            _pluginAppFolder = getPluginAppSettingsFolder();
        } catch (IOException ex) {
            throw new PluginException(ex);
        }

        // Folder to store user settings.
        try {
            _userSettingsFolder = getPluginUserSettingsFolder();
        } catch (IOException ex) {
            throw new PluginException(ex);
        }

        // Load plugin preferences.
        loadPrefs();
    }

    /**
     * Application is shutting down so save preferences.
     */
    public void unload() {
        savePrefs();
        super.unload();
    }

    /**
     * Called when a session started. Add File Import tab to session window.
     *
     * @param   session     The session that is starting.
     *
     * @return  <TT>true</TT> to indicate that this plugin is
     *          applicable to passed session.
     */
    public boolean sessionStarted(ISession session) {
        session.addMainTab(new FileImportTab(session));
        return true;
    }

    /**
     * Create Look and Feel preferences panel for the Global Preferences dialog.
     *
     * @return  Look and Feel preferences panel.
     */
    public IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
        return null;
//        return new IGlobalPreferencesPanel[] {
//             new LAFPreferencesPanel(this, _lafRegister)};
    }

    /**
     * Load from preferences file.
     */
    private void loadPrefs() {
/*
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
        */
    }

    /**
     * Save preferences to disk.
     */
    private void savePrefs() {
        /*
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
        */
    }
}