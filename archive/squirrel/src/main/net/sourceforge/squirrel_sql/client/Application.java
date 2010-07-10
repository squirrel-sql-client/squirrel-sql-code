package net.sourceforge.squirrel_sql.client;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.Debug;
import net.sourceforge.squirrel_sql.fw.util.Logger;
import net.sourceforge.squirrel_sql.fw.util.Pair;

import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.db.DataCache;
import net.sourceforge.squirrel_sql.client.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.client.util.SplashScreen;
import net.sourceforge.squirrel_sql.client.util.SquirrelLogger;

/**
 * Defines the API to do callbacks on the application.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class Application implements IApplication {
    /** Application arguments. */
    private ApplicationArguments _args;
    
    /** Splash screen used during startup process. */
    private SplashScreen _splash;

    private SquirrelPreferences _prefs;
    private SQLDriverManager _driverMgr;
    private DataCache _cache;
    private ActionCollection _actions;

    private MainFrame _mainFrame;

    /** Object for Application level logging. */
    private Logger _logger;

    /** Object to manage plugins. */
    private PluginManager _pluginManager;

    private DummyAppPlugin _dummyPlugin = new DummyAppPlugin();

    private SquirrelResources _resources;

    /**
     * ctor.
     *
     * @param   args    Application arguments.
     */
    public Application(ApplicationArguments args) {
        super();
        _args = args;
    }

    public void startup() {
        _resources = new SquirrelResources("net.sourceforge.squirrel_sql.client.resources.squirrel");
        if (_args.getShowSplashScreen()) {
            _splash = new SplashScreen(_resources, 9);
        }
        
        try {
            CursorChanger chg = null;
            if (_splash != null) {
                chg = new CursorChanger(_splash);
                chg.show();
            }
            try {
                // Create a logger object that logs to a text file in the users
                // preferences directory. If that fails log to standard output.
                indicateNewStartupTask("Creating logger...");
                try {
                    _logger = new SquirrelLogger(ApplicationFiles.EXECUTION_LOG_FILE);
                } catch (IOException ex) {
                    _logger = new Logger();
                    _logger.showMessage(Logger.ILogTypes.ERROR, "Unable to write to log file: " + ApplicationFiles.EXECUTION_LOG_FILE);
                    _logger.showMessage(Logger.ILogTypes.ERROR, ex);
                }

                indicateNewStartupTask("Loading plugins...");
                _pluginManager = new PluginManager(this);
                _pluginManager.loadPlugins();

                indicateNewStartupTask("Loading preferences...");
                _prefs = new SquirrelPreferences();
                _prefs.setApplication(this);
                _prefs.load();
                Debug.setDebugMode(_prefs.isDebugMode());

                indicateNewStartupTask("Loading actions...");
                _actions = new ActionCollection(this);

                indicateNewStartupTask("Creating JDBC driver manager...");
                _driverMgr = new SQLDriverManager(_logger);

                indicateNewStartupTask("Loading JDBC driver and alias information...");
                _cache = new DataCache(this);

                indicateNewStartupTask("Creating main window...");
                _mainFrame = MainFrame.create(this);

                indicateNewStartupTask("Initializing plugins...");
                _pluginManager.initializePlugins();

                indicateNewStartupTask("Showing main window...");
                _mainFrame.setVisible(true);
            } finally {
                if (chg != null) {
                    chg.restore();
                }
            }
        } finally {
            if (_splash != null) {
                _splash.dispose();
            }
            _splash = null;
        }

    }

    public void shutdown() {
        _pluginManager.unloadPlugins();
        _prefs.save();
        _cache.save();
        _logger.close();
    }

    public Logger getLogger() {
        return _logger;
    }

    public PluginManager getPluginManager() {
        return _pluginManager;
    }

    public ActionCollection getActionCollection() {
        return _actions;
    }

    public  SQLDriverManager getSQLDriverManager() {
        return _driverMgr;
    }

    public DataCache getDataCache() {
        return _cache;
    }

    public IPlugin getDummyAppPlugin() {
        return _dummyPlugin;
    }

    public SquirrelResources getResources() {
        return _resources;
    }

    public SquirrelPreferences getSquirrelPreferences() {
        return _prefs;
    }

    public MainFrame getMainFrame() {
        return _mainFrame;
    }

    public synchronized void addToMenu(int menuId, JMenu menu) {
        if (_mainFrame != null) {
            _mainFrame.addToMenu(menuId, menu);
        } else {
            throw new IllegalStateException("Cannot add items to menus prior to menu being created.");
        }
    }

    public synchronized void addToMenu(int menuId, Action action) {
        if (_mainFrame != null) {
            _mainFrame.addToMenu(menuId, action);
        } else {
            throw new IllegalStateException("Cannot add items to menus prior to menu being created.");
        }
    }
    
    /**
     * If we are running with a splash screen then indicate in the splash
     * screen that a new task has commenced.
     *
     * @param   taskDescription     Description of new task.
     */
    private void indicateNewStartupTask(String taskDescription) {
        if (_splash != null) {
            _splash.indicateNewTask(taskDescription);
        }
    }
}

