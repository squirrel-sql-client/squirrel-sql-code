package net.sourceforge.squirrel_sql.client;
/*
 * TODO: finish i18n
 */

/*
 * Copyright (C) 2001-2004 Colin Bell
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.util.Calendar;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.sql.DataCache;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.ProxyHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.TaskThreadPool;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.fw.datasetviewer.CellImportExportInfoSaver;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DTProperties;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.db.AliasMaintSheetFactory;
import net.sourceforge.squirrel_sql.client.db.DriverMaintSheetFactory;
import net.sourceforge.squirrel_sql.client.gui.FileViewerFactory;
import net.sourceforge.squirrel_sql.client.gui.SplashScreen;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.gui.laf.AllBluesBoldMetalTheme;
import net.sourceforge.squirrel_sql.client.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToStartupAliasesCommand;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginLoadInfo;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.DefaultSQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.client.session.SessionWindowManager;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistory;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.client.session.properties.EditWhereCols;
/**
 * Defines the API to do callbacks on the application.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 * @author	Lynn Pye
 */
class Application implements IApplication
{
	/** Logger for this class. */
	private static ILogger s_log;

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(Application.class);

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

	/** This object manages the open sessions.*/
	private final SessionManager _sessionManager = new SessionManager();

	/** This object manages the windows for sessions.*/
	private SessionWindowManager _sessionWindowManager;

	private LoggerController _loggerFactory;

	/** Factory used to create SQL entry panels. */
	private ISQLEntryPanelFactory _sqlEntryFactory = new DefaultSQLEntryPanelFactory();

	/** Output stream for JDBC debug logging. */
	private PrintStream _jdbcDebugOutputStream;

	/** Output writer for JDBC debug logging. */
	private PrintWriter _jdbcDebugOutputWriter;

	/** Contains info about fonts for squirrel. */
	private final FontInfoStore _fontInfoStore = new FontInfoStore();

	/** Application level SQL History. */
	private SQLHistory _sqlHistory;

	/** Current type of JDBC debug logging that we are doing. */
	private int _jdbcDebugType = SquirrelPreferences.IJdbcDebugTypes.NONE;

	/**
	 * Default ctor.
	 */
	Application()
	{
		super();
	}

	/**
	 * Application is starting up.
	 */
	public void startup()
	{
		LoggerController.registerLoggerFactory(new SquirrelLoggerFactory());
		s_log = LoggerController.createLogger(getClass());

		final ApplicationArguments args = ApplicationArguments.getInstance();

		// Setup the applications Look and Feel.
		setupLookAndFeel(args);

// TODO: Make properties file Application.properties so we can use class
// name to generate properties file name.
		_resources = new SquirrelResources("net.sourceforge.squirrel_sql.client.resources.squirrel");

		SplashScreen splash = null;
		if (args.getShowSplashScreen())
		{
			splash = new SplashScreen(_resources, 12);
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
				executeStartupTasks(splash, args);
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

	/**
	 * Application is shutting down.
	 */
	public void shutdown()
	{
		s_log.info(s_stringMgr.getString("Application.shutdown",
									Calendar.getInstance().getTime()));

		_sessionWindowManager.closeAllSessions();
		_pluginManager.unloadPlugins();

		// Remember the currently selected entries in the
		// aliases and drivers windows,
		int idx = _mainFrame.getAliasesToolWindow().getSelectedIndex();
		_prefs.setAliasesSelectedIndex(idx);
		idx = _mainFrame.getDriversToolWindow().getSelectedIndex();
		_prefs.setDriversSelectedIndex(idx);

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
			String msg = s_stringMgr.getString("Application.error.driversave",
												th.getMessage());
			showErrorDialog(msg, th);
			s_log.error(msg, th);
		}

		try
		{
			final File file = appFiles.getDatabaseAliasesFile();
			_cache.saveAliases(file);
		}
		catch (Throwable th)
		{
			String thMsg = th.getMessage();
			if (thMsg == null)
			{
				thMsg = "";
			}
			String msg = s_stringMgr.getString("Application.error.aliassave",
												th.getMessage());
			showErrorDialog(msg, th);
			s_log.error(msg, th);
		}

		if (_jdbcDebugOutputStream != null)
		{
			_jdbcDebugOutputStream.close();
			_jdbcDebugOutputStream = null;
		}

		if (_jdbcDebugOutputWriter != null)
		{
			_jdbcDebugOutputWriter.close();
			_jdbcDebugOutputWriter = null;
		}

		// Save Application level SQL history.
		saveSQLHistory();

		// Save options selected for Cell Import Export operations
		saveCellImportExportInfo();

		// Save options selected for Edit Where Columns
		saveEditWhereColsInfo();

		// Save options selected for DataType-specific properties
		saveDTProperties();

		String msg = s_stringMgr.getString("Application.shutdowncomplete",
										Calendar.getInstance().getTime());
		s_log.info(msg);
		LoggerController.shutdown();
	}

	public PluginManager getPluginManager()
	{
		return _pluginManager;
	}

	/**
	 * Return the manager responsible for session windows.
	 *
	 * @return	the manager responsible for session windows.
	 */
	public SessionWindowManager getSessionWindowManager()
	{
		return _sessionWindowManager;
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
	 * Retrieve the object that manages sessions.
	 *
	 * @return	<TT>SessionManager</TT>.
	 */
	public SessionManager getSessionManager()
	{
		return _sessionManager;
	}

	/**
	 * Display an error message dialog.
	 *
	 * @param	msg		The error msg.
	 */
	public void showErrorDialog(String msg)
	{
		new ErrorDialog(getMainFrame(), msg).show();
	}

	/**
	 * Display an error message dialog.
	 *
	 * @param	th	The Throwable that caused the error
	 */
	public void showErrorDialog(Throwable th)
	{
		new ErrorDialog(getMainFrame(), th).show();
	}

	/**
	 * Display an error message dialog.
	 *
	 * @param	msg The error msg.
	 * @param	th	The Throwable that caused the error
	 */
	public void showErrorDialog(String msg, Throwable th)
	{
		new ErrorDialog(getMainFrame(), msg, th).show();
	}

	/**
	 * Return the collection of <TT>FontInfo </TT> objects for this app.
	 *
	 *
	 *@return	the collection of <TT>FontInfo </TT> objects for this app.
	 */
	public FontInfoStore getFontInfoStore()
	{
		return _fontInfoStore;
	}

	/**
	 * Return the thread pool for this app.
	 *
	 * @return	the thread pool for this app.
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
	 * @return	the factory object used to create the SQL entry panel.
	 */
	public ISQLEntryPanelFactory getSQLEntryPanelFactory()
	{
		return _sqlEntryFactory;
	}

	/**
	 * Set the factory object used to create the SQL entry panel.
	 *
	 * @param	factory the factory object used to create the SQL entry panel.
	 */
	public void setSQLEntryPanelFactory(ISQLEntryPanelFactory factory)
	{
		_sqlEntryFactory = factory != null ? factory : new DefaultSQLEntryPanelFactory();
	}

	/**
	 * Retrieve the application level SQL History object.
	 *
	 * @return		the application level SQL History object.
	 */
	public SQLHistory getSQLHistory()
	{
		return _sqlHistory;
	}

	public synchronized void addToMenu(int menuId, JMenu menu)
	{
		if (_mainFrame != null)
		{
			_mainFrame.addToMenu(menuId, menu);
		}
		else
		{
			throw new IllegalStateException(s_stringMgr.getString("Application.error.menuadding"));
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
			throw new IllegalStateException(s_stringMgr.getString("Application.error.menuadding"));
		}
	}

	/**
	 * Add component to the main frames status bar.
	 *
	 * @param	comp	Component to add.
	 */
	public void addToStatusBar(JComponent comp)
	{
		if (_mainFrame != null)
		{
			_mainFrame.addToStatusBar(comp);
		}
		else
		{
			throw new IllegalStateException(s_stringMgr.getString("Application.error.compadding"));
		}
	}

	/**
	 * Remove component to the main frames status bar.
	 *
	 * @param	comp	Component to remove.
	 */
	public void removeFromStatusBar(JComponent comp)
	{
		if (_mainFrame != null)
		{
			_mainFrame.removeFromStatusBar(comp);
		}
		else
		{
			throw new IllegalStateException(s_stringMgr.getString("Application.error.compremoving"));
		}
	}

	/**
	 * Execute the taks required to start SQuirreL. Each of these is displayed
	 * as a message on the splash screen (if one is being used) in order to let the
	 * user know what is happening.
	 *
	 * @param	splash		The splash screen (can be null).
	 * @param	args		Application arguments.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>ApplicationArguments<.TT> is null.
	 */
	private void executeStartupTasks(SplashScreen splash, ApplicationArguments args)
	{
		if (args == null)
		{
			throw new IllegalArgumentException("ApplicationArguments == null");
		}

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.uifactoryinit"));
		AliasMaintSheetFactory.initialize(this);
		DriverMaintSheetFactory.initialize(this);
//		SessionPropertiesSheetFactory.initialize(this);
//		SQLFilterSheetFactory.initialize(this);
		_sessionWindowManager = new SessionWindowManager(this);

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadingprefs"));
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

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.uifactoryinit"));
		UIFactory.initialize(_prefs);

		final boolean loadPlugins = args.getLoadPlugins();
		if (loadPlugins)
		{
			indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadingplugins"));
		}
		else
		{
			indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.notloadingplugins"));
		}
		_pluginManager = new PluginManager(this);
		if (args.getLoadPlugins())
		{
			_pluginManager.loadPlugins();
		}

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadingactions"));
		_actions = new ActionCollection(this);

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadinguseracc"));
		_actions.loadActionKeys(_prefs.getActionKeys());

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.createjdbcmgr"));
		_driverMgr = new SQLDriverManager();

		// TODO: pass in a message handler so user gets error msgs.
		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadingjdbc"));
		final ApplicationFiles appFiles = new ApplicationFiles();
		_cache = new DataCache(_driverMgr, appFiles.getDatabaseDriversFile(),
								appFiles.getDatabaseAliasesFile(),
								_resources.getDefaultDriversUrl(), null);

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.createmainwindow"));
		_mainFrame = new MainFrame(this);

		indicateNewStartupTask(splash, loadPlugins ? "Initializing plugins..." : "No Plugins are to be loaded...");
		if (loadPlugins)
		{
			_pluginManager.initializePlugins();
			for (Iterator it = _pluginManager.getPluginLoadInfoIterator(); it.hasNext();)
			{
				PluginLoadInfo pli = (PluginLoadInfo)it.next();
				long created = pli.getCreationTime();
				long load = pli.getLoadTime();
				long init = pli.getInitializeTime();
				s_log.info("Plugin " + (pli.getInternalName())
						+ " created in " + created
						+ " ms, loaded in " + load
						+ " ms, initialized in " + init
						+ " ms, total " + (created + load + init) + " ms.");
			}
		}

		indicateNewStartupTask(splash, "Loading SQL history...");
		loadSQLHistory();

		indicateNewStartupTask(splash, "Loading Cell Import/Export selections...");
		loadCellImportExportInfo();

		indicateNewStartupTask(splash, "Loading Edit 'Where' Columns selections...");
		loadEditWhereColsInfo();

		indicateNewStartupTask(splash, "Loading Data Type Properties...");
		loadDTProperties();

		indicateNewStartupTask(splash, "Showing main window...");
		_mainFrame.setVisible(true);

		new ConnectToStartupAliasesCommand(this).execute();
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

		if (propName == null || propName.equals(SquirrelPreferences.IPropertyNames.SHOW_TOOLTIPS))
		{
			ToolTipManager.sharedInstance().setEnabled(_prefs.getShowToolTips());
		}

		if (propName == null || propName.equals(
					SquirrelPreferences.IPropertyNames.JDBC_DEBUG_TYPE))
		{
			setupJDBCLogging();
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

	/**
	 * Load application level SQL History for the current user.
	 */
	private void loadSQLHistory()
	{
		try
		{
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(new ApplicationFiles().getUserSQLHistoryFile());
			Iterator it = doc.iterator();
			if (it.hasNext())
			{
				_sqlHistory = (SQLHistory)it.next();
			}
		}
		catch (FileNotFoundException ignore)
		{
			// History file not found for user - first time user ran pgm.
		}
		catch (Exception ex)
		{
			s_log.error("Unable to load SQL history from persistant storage.", ex);
		}
		finally
		{
			if (_sqlHistory == null)
			{
				_sqlHistory = new SQLHistory();
			}
		}
	}

	/**
	 * Save application level SQL history for current user.
	 */
	private void saveSQLHistory()
	{
		// Get the history into an array.
		try
		{
			XMLBeanWriter wtr = new XMLBeanWriter(_sqlHistory);
			wtr.save(new ApplicationFiles().getUserSQLHistoryFile());
		}
		catch (Exception ex)
		{
			s_log.error("Unable to write SQL queries to persistant storage.", ex);
		}
	}


	/**
	 * Load the options previously selected by user for import/export of
	 * data in various Cells.
	 */
	private void loadCellImportExportInfo()
	{
		CellImportExportInfoSaver saverInstance = null;
		try
		{
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(new ApplicationFiles().getCellImportExportSelectionsFile());
			Iterator it = doc.iterator();
			if (it.hasNext())
			{
				saverInstance = (CellImportExportInfoSaver)it.next();
			}
		}
		catch (FileNotFoundException ignore)
		{
			// Cell Import/Export file not found for user - first time user ran pgm.
		}
		catch (Exception ex)
		{
			s_log.error("Unable to load Cell Import/Export selections from persistant storage.", ex);
		}
		finally
		{
			// set the singleton instance of the Saver class to be the
			// instance just created by the XMLBeanReader
			CellImportExportInfoSaver.setInstance(saverInstance);
		}
	}

	/**
	 * Save the options selected by user for Cell Import Export.
	 */
	private void saveCellImportExportInfo()
	{
		try
		{
			XMLBeanWriter wtr = new XMLBeanWriter(CellImportExportInfoSaver.getInstance());
			wtr.save(new ApplicationFiles().getCellImportExportSelectionsFile());
		}
		catch (Exception ex)
		{
			s_log.error("Unable to write Cell Import/Export options to persistant storage.", ex);
		}
	}

	/**
	 * Load the options previously selected by user for specific cols to use
	 * in WHERE clause when editing cells.
	 */
	private void loadEditWhereColsInfo()
	{
		EditWhereCols saverInstance = null;
		try
		{
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(new ApplicationFiles().getEditWhereColsFile());
			Iterator it = doc.iterator();
			if (it.hasNext())
			{
				saverInstance = (EditWhereCols)it.next();
				EditWhereCols x = saverInstance;
			}
		}
		catch (FileNotFoundException ignore)
		{
			// Cell Import/Export file not found for user - first time user ran pgm.
		}
		catch (Exception ex)
		{
			s_log.error("Unable to load Cell Import/Export selections from persistant storage.", ex);
		}
		finally
		{
			// nothing needed here??
		}
	}

	/**
	 * Save the options selected by user for Cell Import Export.
	 */
	private void saveEditWhereColsInfo()
	{
		try
		{
			XMLBeanWriter wtr = new XMLBeanWriter(new EditWhereCols());
			wtr.save(new ApplicationFiles().getEditWhereColsFile());
		}
		catch (Exception ex)
		{
			s_log.error("Unable to write Edit Where Cols options to persistant storage.", ex);
		}
	}

	/**
	 * Load the options previously selected by user for specific cols to use
	 * in WHERE clause when editing cells.
	 */
	private void loadDTProperties()
	{
		DTProperties saverInstance = null;
		try
		{
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(new ApplicationFiles().getDTPropertiesFile());
			Iterator it = doc.iterator();
			if (it.hasNext())
			{
				saverInstance = (DTProperties)it.next();
				DTProperties x = saverInstance;
			}
		}
		catch (FileNotFoundException ignore)
		{
			// Cell Import/Export file not found for user - first time user ran pgm.
		}
		catch (Exception ex)
		{
			s_log.error("Unable to load DataType Properties selections from persistant storage.", ex);
		}
		finally
		{
			// nothing needed here??
		}
	}

	/**
	 * Save the options selected by user for Cell Import Export.
	 */
	private void saveDTProperties()
	{
		try
		{
			XMLBeanWriter wtr = new XMLBeanWriter(new DTProperties());
			wtr.save(new ApplicationFiles().getDTPropertiesFile());
		}
		catch (Exception ex)
		{
			s_log.error("Unable to write DataType properties to persistant storage.", ex);
		}
	}


	/**
	 * Setup applications Look and Feel.
	 */
	private void setupLookAndFeel(ApplicationArguments args)
	{
		String lafClassName = args.useNativeLAF()
					? UIManager.getSystemLookAndFeelClassName()
					: MetalLookAndFeel.class.getName();

		if (!args.useDefaultMetalTheme())
		{
			MetalLookAndFeel.setCurrentTheme(new AllBluesBoldMetalTheme());
		}

		try
		{
			UIManager.setLookAndFeel(lafClassName);
		}
		catch (Exception ex)
		{
			s_log.error("Error setting LAF", ex);
		}
	}

	private void setupJDBCLogging()
	{
		// If logging has changed.
		if (_jdbcDebugType != _prefs.getJdbcDebugType())
		{
			final ApplicationFiles appFiles = new ApplicationFiles();
			final File outFile = appFiles.getJDBCDebugLogFile();

			// Close any previous logging.
			DriverManager.setLogStream(null);
			if (_jdbcDebugOutputStream != null)
			{
				_jdbcDebugOutputStream.close();
				_jdbcDebugOutputStream = null;
			}
			DriverManager.setLogWriter(null);
			if (_jdbcDebugOutputWriter != null)
			{
				_jdbcDebugOutputWriter.close();
				_jdbcDebugOutputWriter = null;
			}

			if (_prefs.isJdbcDebugToStream())
			{
				try
				{
					s_log.debug("Attempting to set JDBC debug log to output stream");
					_jdbcDebugOutputStream = new PrintStream(new FileOutputStream(outFile));
					DriverManager.setLogStream(_jdbcDebugOutputStream);
					s_log.debug("JDBC debug log set to output stream successfully");
				}
				catch (IOException ex)
				{
					final String msg = s_stringMgr.getString("Application.error.jdbcstream");
					s_log.error(msg, ex);
					showErrorDialog(msg, ex);
					DriverManager.setLogStream(System.out);
				}
			}

			if (_prefs.isJdbcDebugToWriter())
			{
				try
				{
					s_log.debug("Attempting to set JDBC debug log to writer");
					_jdbcDebugOutputWriter = new PrintWriter(new FileWriter(outFile));
					DriverManager.setLogWriter(_jdbcDebugOutputWriter);
					s_log.debug("JDBC debug log set to writer successfully");
				}
				catch (IOException ex)
				{
					final String msg = s_stringMgr.getString("Application.error.jdbcwriter");
					s_log.error(msg, ex);
					showErrorDialog(msg, ex);
					DriverManager.setLogWriter(new PrintWriter(System.out));
				}
			}

			_jdbcDebugType = _prefs.getJdbcDebugType();
		}
	}
}
