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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.ToolTipManager;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.util.TaskThreadPool;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.Log4jLogger;
import net.sourceforge.squirrel_sql.fw.util.Pair;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.db.DataCache;
import net.sourceforge.squirrel_sql.client.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.DefaultSQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.client.util.SplashScreen;

/**
 * Defines the API to do callbacks on the application.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class Application implements IApplication {
	/** Logger for this class. */
	private static ILogger s_log;
	
	/** Splash screen used during startup process. */
	private SplashScreen _splash;

	private SquirrelPreferences _prefs;
	private SQLDriverManager _driverMgr;
	private DataCache _cache;
	private ActionCollection _actions;

	private MainFrame _mainFrame;

	/** Object to manage plugins. */
	private PluginManager _pluginManager;

	private DummyAppPlugin _dummyPlugin = new DummyAppPlugin();

	private SquirrelResources _resources;
	
	/** Thread pool for long running tasks. */
	private TaskThreadPool _threadPool = new TaskThreadPool();

	private LoggerController _loggerFactory;

	/** Factory used to create SQL entry panels. */
	private ISQLEntryPanelFactory _sqlEntryFactory = new DefaultSQLEntryPanelFactory();

	/** Output stream for JDBC debug logging. */
	private PrintStream _jdbcDebugOutput;

	/**
	 * ctor.
	 *
	 * @param   args	Application arguments.
	 */
	Application() {
		super();
	}

	public void startup() {
		LoggerController.registerLoggerFactory(new SquirrelLoggerFactory());
		s_log = LoggerController.createLogger(getClass());

		_resources = new SquirrelResources("net.sourceforge.squirrel_sql.client.resources.squirrel");
		final ApplicationArguments args = ApplicationArguments.getInstance();
		if (args.getShowSplashScreen()) {
			_splash = new SplashScreen(_resources, 8);
		}
		
		try {
			CursorChanger chg = null;
			if (_splash != null) {
				chg = new CursorChanger(_splash);
				chg.show();
			}
			try {
				indicateNewStartupTask("Loading plugins...");
				_pluginManager = new PluginManager(this);
				_pluginManager.loadPlugins();

				indicateNewStartupTask("Loading preferences...");
				_prefs = new SquirrelPreferences();
				_prefs.setApplication(this);
				_prefs.load();
				preferencesHaveChanged(null);
				_prefs.addPropertyChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						preferencesHaveChanged(evt);
					}
				});

				indicateNewStartupTask("Loading actions...");
				_actions = new ActionCollection(this);

				indicateNewStartupTask("Creating JDBC driver manager...");
				_driverMgr = new SQLDriverManager();

				indicateNewStartupTask("Loading JDBC driver and alias information...");
				_cache = new DataCache(this);

				indicateNewStartupTask("Creating main window...");
				_mainFrame = new MainFrame(this);

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

		if (_jdbcDebugOutput != null) {
			_jdbcDebugOutput.close();
			_jdbcDebugOutput = null;
		}

		_loggerFactory.shutdown();
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

	/**
	 * Return the thread pool for this app.
	 * 
	 * @return	the thread pool for this app.
	 */
	public TaskThreadPool getThreadPool() {
		return _threadPool;
	}

	public LoggerController getLoggerFactory() {
		return _loggerFactory;
	}

	/**
	 * Return the factory object used to create the SQL entry panel.
	 * 
	 * @return	the factory object used to create the SQL entry panel.
	 */
	public ISQLEntryPanelFactory getSQLEntryPanelFactory() {
		return _sqlEntryFactory;
	}

	/**
	 * Set the factory object used to create the SQL entry panel.
	 * 
	 * @param	factory	the factory object used to create the SQL entry panel.
	 */
	public void setSQLEntryPanelFactory(ISQLEntryPanelFactory factory) {
		_sqlEntryFactory = factory != null ? factory : new DefaultSQLEntryPanelFactory();
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
	 * @param   taskDescription	 Description of new task.
	 */
	private void indicateNewStartupTask(String taskDescription) {
		if (_splash != null) {
			_splash.indicateNewTask(taskDescription);
		}
	}

	private void preferencesHaveChanged(PropertyChangeEvent evt) {
		final String propName = evt != null ? evt.getPropertyName() : null;
		final ApplicationFiles appFiles = new ApplicationFiles();

		if (propName == null || propName.equals(SquirrelPreferences.IPropertyNames.SHOW_TOOLTIPS)) {
			ToolTipManager.sharedInstance().setEnabled(_prefs.getShowToolTips());
		}

		if (propName == null || propName.equals(SquirrelPreferences.IPropertyNames.DEBUG_JDBC)) {
			if (_prefs.getDebugJdbc()) {
				try {
					_jdbcDebugOutput = new PrintStream(new FileOutputStream(appFiles.getJDBCDebugLogFile()));
					DriverManager.setLogStream(_jdbcDebugOutput);
				} catch (IOException ex) {
					DriverManager.setLogStream(System.out);
				}
			} else {
				if (_jdbcDebugOutput != null) {
					_jdbcDebugOutput.close();
					_jdbcDebugOutput = null;
				}
				DriverManager.setLogWriter(null);
			}
		}

		if (propName == null || propName.equals(SquirrelPreferences.IPropertyNames.LOGIN_TIMEOUT)) {
			DriverManager.setLoginTimeout(_prefs.getLoginTimeout());
		}
	}
}

