package net.sourceforge.squirrel_sql.plugins.sqlscript;
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

import javax.swing.Action;
import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.fw.util.Logger;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.ISession;

import net.sourceforge.squirrel_sql.plugins.sqlscript.session_script.SessionScriptCache;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.CreateDataScriptAction;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.CreateTableScriptAction;

/**
 * The SQL Script plugin class.
 */
public class SQLScriptPlugin extends DefaultSessionPlugin {
    private interface IMenuResourceKeys {
        String SCRIPTS = "scripts";
    }

    /** Plugin preferences. */
    private SQLScriptPreferences _prefs;

    /** The app folder for this plugin. */
    private File _pluginAppFolder;

    /** Folder to store user settings in. */
    private File _userSettingsFolder;

    /** Cache of session scripts. */
    private SessionScriptCache _cache;

    private PluginResources _resources;

    /**
     * Return the internal name of this plugin.
     *
     * @return  the internal name of this plugin.
     */
    public String getInternalName() {
        return "sqlscript";
    }

    /**
     * Return the descriptive name of this plugin.
     *
     * @return  the descriptive name of this plugin.
     */
    public String getDescriptiveName() {
        return "SQL Scripts Plugin";
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
        return "Johan Compagner, Colin Bell";
    }

    /**
     * Create preferences panel for the Global Preferences dialog.
     *
     * @return  Preferences panel.
     */
    public IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
        return new IGlobalPreferencesPanel[] { new SQLScriptPreferencesPanel(_prefs)};
    }

    /**
     * Initialize this plugin.
     */
    public synchronized void initialize() throws PluginException {
        super.initialize();
        IApplication app = getApplication();
        PluginManager pmgr = app.getPluginManager();

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

        _resources =
            new SQLPluginResources(
                "net.sourceforge.squirrel_sql.plugins.sqlscript.sqlscript",
                this);

        // Load plugin preferences.
        loadPrefs();

        ActionCollection coll = app.getActionCollection();
        coll.add(new SaveScriptAction(app, _resources, this));
        coll.add(new LoadScriptAction(app, _resources, this));
        coll.add(new CreateTableScriptAction(app, _resources));
        coll.add(new CreateDataScriptAction(app, _resources));
        createMenu();

        try {
            _cache = new SessionScriptCache(this);
        } catch (IOException ex) {
            throw new PluginException(ex);
        }
        _cache.load();

    }

    /**
     * Application is shutting down so save data.
     */
    public void unload() {
        savePrefs();
        if (_cache != null) {
            _cache.save();
        }
        super.unload();
    }

    /**
     * Called when a session started. See if any startup scripts
     * defined for this driver/alias and if so execute them.
     *
     * @param   session     The session that is starting.
     *
     * @return  <TT>true</TT> to indicate that this plugin is
     *          applicable to passed session.
     */
    public boolean sessionStarted(ISession session) {
        return true;
    }

    /**
     * Load from preferences file.
     */
    void loadPrefs() {
        try {
            XMLBeanReader doc = new XMLBeanReader();
            doc.load(
                new File(_userSettingsFolder, SQLScriptConstants.USER_PREFS_FILE_NAME),
                getClass().getClassLoader());
            Iterator it = doc.iterator();
            if (it.hasNext()) {
                _prefs = (SQLScriptPreferences) it.next();
            }
        } catch (FileNotFoundException ignore) {
            // property file not found for user - first time user ran pgm.
        } catch (Exception ex) {
            Logger logger = getApplication().getLogger();
            logger.showMessage(
                Logger.ILogTypes.ERROR,
                "Error occured reading from preferences file: "
                    + SQLScriptConstants.USER_PREFS_FILE_NAME);
            //i18n
            logger.showMessage(Logger.ILogTypes.ERROR, ex);
        }
        if (_prefs == null) {
            _prefs = new SQLScriptPreferences();
        }
    }

    /**
     * Save preferences to disk.
     */
    synchronized void savePrefs() {
        if (_prefs != null) {
            try {
                XMLBeanWriter wtr = new XMLBeanWriter(_prefs);
                wtr.save(
                    new File(_userSettingsFolder, SQLScriptConstants.USER_PREFS_FILE_NAME));
            } catch (Exception ex) {
                Logger logger = getApplication().getLogger();
                logger.showMessage(
                    Logger.ILogTypes.ERROR,
                    "Error occured writing to preferences file: "
                        + SQLScriptConstants.USER_PREFS_FILE_NAME);
                //i18n
                logger.showMessage(Logger.ILogTypes.ERROR, ex);
            }
        }
    }

    SQLScriptPreferences getPreferences() {
        return _prefs;
    }

    private void createMenu() {
        IApplication app = getApplication();
        ActionCollection coll = app.getActionCollection();

        JMenu menu = _resources.createMenu(IMenuResourceKeys.SCRIPTS);
        _resources.addToMenu(coll.get(LoadScriptAction.class), menu);
        _resources.addToMenu(coll.get(SaveScriptAction.class), menu);
        _resources.addToMenu(coll.get(CreateDataScriptAction.class), menu);
        _resources.addToMenu(coll.get(CreateTableScriptAction.class), menu);

        app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, menu);
    }
}
