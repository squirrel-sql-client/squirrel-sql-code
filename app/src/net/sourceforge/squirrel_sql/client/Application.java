package net.sourceforge.squirrel_sql.client;
/*
 * Copyright (C) 2001-2002 Colin Bell
 * colbell@users.sourceforge.net
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.ProxyHandler;
import net.sourceforge.squirrel_sql.fw.util.TaskThreadPool;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.db.AliasMaintSheetFactory;
import net.sourceforge.squirrel_sql.client.db.DataCache;
import net.sourceforge.squirrel_sql.client.db.DriverMaintSheetFactory;
import net.sourceforge.squirrel_sql.client.gui.FileViewerFactory;
import net.sourceforge.squirrel_sql.client.gui.SplashScreen;
import net.sourceforge.squirrel_sql.client.gui.laf.AllBluesBoldMetalTheme;
import net.sourceforge.squirrel_sql.client.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.DefaultSQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionSheet;
import net.sourceforge.squirrel_sql.client.session.properties.SessionPropertiesSheetFactory;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
/**
 * Defines the API to do callbacks on the application.
 *
 *@author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class Application implements IApplication
{
	/** Logger for this class. */
	private static ILogger s_log;

	private SquirrelPreferences _prefs;
	private SQLDriverManager _driverMgr;
	private DataCache _cache;
	private ActionCollection _actions;

	/** Applications main frame. */
	private MainFrame _mainFrame;

	/** Object to manage plugins. */
	private PluginManager _pluginManager;

	private final DummyAppPlugin _dummyPlugin = new DummyAppPlugin();

	private SquirrelResources _resources;

	/** Thread pool for long running tasks. */
	private final TaskThreadPool _threadPool = new TaskThreadPool();

	private LoggerController _loggerFactory;

	/** Factory used to create SQL entry panels. */
	private ISQLEntryPanelFactory _sqlEntryFactory = new DefaultSQLEntryPanelFactory();

	/** Output stream for JDBC debug logging. */
	private PrintStream _jdbcDebugOutput;

	/** Contains info about fonts for squirrel. */
	private final FontInfoStore _fontInfoStore = new FontInfoStore();

	/**
	 * ctor.
	 */
	Application()
	{
		super();
	}

	public void startup()
	{
		LoggerController.registerLoggerFactory(new SquirrelLoggerFactory());
		s_log = LoggerController.createLogger(getClass());

		final ApplicationArguments args = ApplicationArguments.getInstance();

		// Load default LAF info.
		if (!args.useDefaultMetalTheme())
		{
			MetalLookAndFeel.setCurrentTheme(new AllBluesBoldMetalTheme());
		}
		try
		{
			UIManager.setLookAndFeel(MetalLookAndFeel.class.getName());
		}
		catch (Exception th)
		{
			s_log.error("Error setting LAF", th);
		}

		_resources = new SquirrelResources("net.sourceforge.squirrel_sql.client.resources.squirrel");

		final boolean loadPlugins = args.getLoadPlugins();

		SplashScreen splash = null;
		if (args.getShowSplashScreen())
		{
			splash = new SplashScreen(_resources, 10);
		}

		try
		{
			CursorChanger chg = null;
			if (splash != null)
			{
				chg = new CursorChanger(splash);
				chg.show();
			}
			try
			{
				indicateNewStartupTask(splash, "Initializing UI factories...");
				AliasMaintSheetFactory.initialize(this);
				DriverMaintSheetFactory.initialize(this);
				SessionPropertiesSheetFactory.initialize(this);

				indicateNewStartupTask(splash, loadPlugins ? "Loading plugins..." : "No Plugins are to be loaded...");
				_pluginManager = new PluginManager(this);
				if (loadPlugins)
				{
					_pluginManager.loadPlugins();
				}

				indicateNewStartupTask(splash, "Loading preferences...");
				_prefs = SquirrelPreferences.load();
				preferencesHaveChanged(null);
				_prefs.addPropertyChangeListener(
					new PropertyChangeListener()
					{
						public void propertyChange(PropertyChangeEvent evt)
						{
							preferencesHaveChanged(evt);
						}
					});

				indicateNewStartupTask(splash, "Loading actions...");
				_actions = new ActionCollection(this);

				indicateNewStartupTask(splash, "Loading user specified accelerators and mnemonics...");
				_actions.loadActionKeys(_prefs.getActionKeys());

				indicateNewStartupTask(splash, "Creating JDBC driver manager...");
				_driverMgr = new SQLDriverManager();

				// TODO: pass in a message handler so user gets error msgs.
				indicateNewStartupTask(splash, "Loading JDBC driver and alias information...");
				_cache = new DataCache(_driverMgr, _resources, null);

				indicateNewStartupTask(splash, "Creating main window...");
				_mainFrame = new MainFrame(this);

				indicateNewStartupTask(splash, "Initializing plugins...");
				_pluginManager.initializePlugins();

				indicateNewStartupTask(splash, "Showing main window...");
				_mainFrame.setVisible(true);
			}
			finally
			{
				if (chg != null)
				{
					chg.restore();
				}
			}
		}
		finally
		{
			if (splash != null)
			{
				splash.dispose();
			}
		}

	}

	public void shutdown()
	{
		_pluginManager.unloadPlugins();

		_prefs.save();

		FileViewerFactory.getInstance().closeAllViewers();

		final ApplicationFiles appFiles = new ApplicationFiles();

		try
		{
			final File file = appFiles.getDatabaseDriversFile();
			_cache.saveDrivers(file);
		}
		catch (Throwable th)
		{
			showErrorDialog("Error occured saving Driver Definitions", th);
		}

		try
		{
			final File file = appFiles.getDatabaseAliasesFile();
			_cache.saveAliases(file);
		}
		catch (Throwable th)
		{
			showErrorDialog("Error occured saving Alias Definitions", th);
		}

		if (_jdbcDebugOutput != null)
		{
			_jdbcDebugOutput.close();
			_jdbcDebugOutput = null;
		}

		_loggerFactory.shutdown();
	}

	public PluginManager getPluginManager()
	{
		return _pluginManager;
	}

	public ActionCollection getActionCollection()
	{
		return _actions;
	}

	public SQLDriverManager getSQLDriverManager()
	{
		return _driverMgr;
	}

	public DataCache getDataCache()
	{
		return _cache;
	}

	public IPlugin getDummyAppPlugin()
	{
		return _dummyPlugin;
	}

	public SquirrelResources getResources()
	{
		return _resources;
	}

	public SquirrelPreferences getSquirrelPreferences()
	{
		return _prefs;
	}

	public MainFrame getMainFrame()
	{
		return _mainFrame;
	}

	/**
	 * Display an error message dialog.
	 *
	 *
	 *@param  msg The error msg.
	 */
	public void showErrorDialog(String msg)
	{
		new ErrorDialog(getMainFrame(), msg).show();
	}

	/**
	 * Display an error message dialog.
	 *
	 *
	 *@param  th The Throwable that caused the error
	 */
	public void showErrorDialog(Throwable th)
	{
		new ErrorDialog(getMainFrame(), th).show();
	}

	/**
	 * Display an error message dialog.
	 *
	 *
	 *@param  msg The error msg.
	 *@param  th The Throwable that caused the error
	 */
	public void showErrorDialog(String msg, Throwable th)
	{
		new ErrorDialog(getMainFrame(), msg, th).show();
	}

	/**
	 * Return the collection of <TT>FontInfo </TT> objects for this app.
	 *
	 *
	 *@return  the collection of <TT>FontInfo </TT> objects for this app.
	 */
	public FontInfoStore getFontInfoStore()
	{
		return _fontInfoStore;
	}

	/**
	 * Return the thread pool for this app.
	 *
	 *@return  the thread pool for this app.
	 */
	public TaskThreadPool getThreadPool()
	{
		return _threadPool;
	}

	public LoggerController getLoggerFactory()
	{
		return _loggerFactory;
	}

	/**
	 * Return the factory object used to create the SQL entry panel.
	 *
	 *@return  the factory object used to create the SQL entry panel.
	 */
	public ISQLEntryPanelFactory getSQLEntryPanelFactory()
	{
		return _sqlEntryFactory;
	}

	/**
	 * Set the factory object used to create the SQL entry panel.
	 *
	 *@param  factory the factory object used to create the SQL entry panel.
	 */
	public void setSQLEntryPanelFactory(ISQLEntryPanelFactory factory)
	{
		_sqlEntryFactory = factory != null ? factory : new DefaultSQLEntryPanelFactory();
	}

	/**
	 * Return an array of all the sessions currently active.
	 * 
	 * @return	array of all active sessions.
	 */
	public synchronized ISession[] getActiveSessions()
	{
		final JInternalFrame[] frames = GUIUtils.getOpenNonToolWindows(_mainFrame.getDesktopPane().getAllFrames());
		final List sessions = new ArrayList();
		for (int i = 0; i < frames.length; ++i)
		{
			if (frames[i] instanceof SessionSheet)
			{
				sessions.add(((SessionSheet)frames[i]).getSession());
			}
		}
		return (ISession[])sessions.toArray(new ISession[sessions.size()]);
	}

	public synchronized void addToMenu(int menuId, JMenu menu)
	{
		if (_mainFrame != null)
		{
			_mainFrame.addToMenu(menuId, menu);
		}
		else
		{
			throw new IllegalStateException("Cannot add items to menus prior to menu being created.");
		}
	}

	public synchronized void addToMenu(int menuId, Action action)
	{
		if (_mainFrame != null)
		{
			_mainFrame.addToMenu(menuId, action);
		}
		else
		{
			throw new IllegalStateException("Cannot add items to menus prior to menu being created.");
		}
	}

	/**
	 * If we are running with a splash screen then indicate in the splash
	 * screen that a new task has commenced.
	 *
	 * @param	splash			Splash screen.
	 * @param	taskDescription	Description of new task.
	 */
	private void indicateNewStartupTask(SplashScreen splash,
										String taskDescription)
	{
		if (splash != null)
		{
			splash.indicateNewTask(taskDescription);
		}
	}

	private void preferencesHaveChanged(PropertyChangeEvent evt)
	{
		final String propName = evt != null ? evt.getPropertyName() : null;
		final ApplicationFiles appFiles = new ApplicationFiles();

		if (propName == null || propName.equals(SquirrelPreferences.IPropertyNames.SHOW_TOOLTIPS))
		{
			ToolTipManager.sharedInstance().setEnabled(_prefs.getShowToolTips());
		}

		if (propName == null || propName.equals(SquirrelPreferences.IPropertyNames.DEBUG_JDBC))
		{
			if (_prefs.getDebugJdbc())
			{
				try
				{
					_jdbcDebugOutput = new PrintStream(new FileOutputStream(appFiles.getJDBCDebugLogFile()));
					DriverManager.setLogStream(_jdbcDebugOutput);
				}
				catch (IOException ex)
				{
					DriverManager.setLogStream(System.out);
				}
			}
			else
			{
				if (_jdbcDebugOutput != null)
				{
					_jdbcDebugOutput.close();
					_jdbcDebugOutput = null;
				}
				DriverManager.setLogWriter(null);
			}
		}

		if (propName == null || propName.equals(SquirrelPreferences.IPropertyNames.LOGIN_TIMEOUT))
		{
			DriverManager.setLoginTimeout(_prefs.getLoginTimeout());
		}

		if (propName == null || propName == SquirrelPreferences.IPropertyNames.PROXY)
		{
			new ProxyHandler().apply(_prefs.getProxySettings());
		}
	}
}
