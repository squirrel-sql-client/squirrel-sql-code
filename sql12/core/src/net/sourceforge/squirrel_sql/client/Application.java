package net.sourceforge.squirrel_sql.client;


/*
 * Copyright (C) 2001-2006 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
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
import java.io.*;
import java.sql.DriverManager;
import java.util.*;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

import net.sourceforge.squirrel_sql.client.action.ActionRegistry;
import net.sourceforge.squirrel_sql.client.edtwatcher.EventDispatchThreadWatcher;
import net.sourceforge.squirrel_sql.client.gui.recentfiles.RecentFilesManager;
import net.sourceforge.squirrel_sql.client.preferences.LocaleWrapper;
import net.sourceforge.squirrel_sql.client.session.mainpanel.multiclipboard.PasteHistory;
import net.sourceforge.squirrel_sql.client.shortcut.ShortcutManager;
import net.sourceforge.squirrel_sql.fw.gui.action.rowselectionwindow.RowsWindowFrameRegistry;
import net.sourceforge.squirrel_sql.fw.props.PropsImpl;
import net.sourceforge.squirrel_sql.fw.resources.LazyResourceBundle;

import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.FileViewerFactory;
import net.sourceforge.squirrel_sql.client.gui.SquirrelSplashScreen;
import net.sourceforge.squirrel_sql.client.gui.WindowManager;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DesktopStyle;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.gui.db.DataCache;
import net.sourceforge.squirrel_sql.client.gui.laf.AllBluesBoldMetalTheme;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToStartupAliasesCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.ViewHelpCommand;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginLoadInfo;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.preferences.PreferenceType;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.DefaultSQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistory;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistoryItem;
import net.sourceforge.squirrel_sql.client.session.properties.EditWhereCols;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfoCacheSerializer;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.datasetviewer.CellImportExportInfoSaver;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DTProperties;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfigurationFactory;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.WikiTableConfigurationFactory;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.WikiTableConfigurationStorage;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.BareBonesBrowserLaunch;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ClassLoaderListener;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.ProxyHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.TaskThreadPool;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
/**
 * Defines the API to do callbacks on the application.
 * 
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 * @author Lynn Pye
 */
public class Application implements IApplication
{	
	private static ILogger s_log = LoggerController.createLogger(Application.class);

	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(Application.class);

	private SquirrelPreferences _prefs;

   private DesktopStyle _desktopStyle;

	private SQLDriverManager _driverMgr;

	private DataCache _cache;

	private ActionRegistry _actionRegistry;

	/** Object to manage plugins. */
	private IPluginManager _pluginManager;

	private final DummyAppPlugin _dummyPlugin = new DummyAppPlugin();

	private SquirrelResources _resources;

	/** Thread pool for long running tasks. */
	private final TaskThreadPool _threadPool = new TaskThreadPool();

	/** This object manages the open sessions. */
	private SessionManager _sessionManager;

	/** This object manages the windows for this application. */
	private WindowManager _windowManager;

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
	
	/**
	 * Configuration factory for WIKI tables.
	 */
	private IWikiTableConfigurationFactory wikiTableConfigFactory = WikiTableConfigurationFactory.getInstance();

	/** Current type of JDBC debug logging that we are doing. */
	private int _jdbcDebugType = SquirrelPreferences.IJdbcDebugTypes.NONE;

	/**
	 * contains info about files and directories used by the application.
	 */
	private ApplicationFiles _appFiles = null;

	private EditWhereCols editWhereCols = new EditWhereCols();

	private List<ApplicationListener> _listeners = new ArrayList<ApplicationListener>();

//	private UpdateCheckTimer updateCheckTimer = null;

	private IShutdownTimer _shutdownTimer = new ShutdownTimer();

	private MultipleWindowsHandler _multipleWindowsHandler = new MultipleWindowsHandler(this);

	private RecentFilesManager _recentFilesManager = new RecentFilesManager();

	private PasteHistory _pasteHistory = new PasteHistory();

	private RowsWindowFrameRegistry _rowsWindowFrameRegistry = new RowsWindowFrameRegistry();

	// Must be done here because Plugin loading added more actions to _actionCollection.
	private ShortcutManager _shortcutManager = new ShortcutManager();

	private PropsImpl _propsImpl;

	public Application()
	{
	}

	/**
	 * Application is starting up.
	 */
   @Override
	public void startup()
	{
		initResourcesAndPrefs();

		final ApplicationArguments args = ApplicationArguments.getInstance();

		// Setup the applications Look and Feel.
		setupLookAndFeel(args);

      _desktopStyle = new DesktopStyle(_prefs);

		preferencesHaveChanged(null);
		_prefs.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				preferencesHaveChanged(evt);
			}
		});

		SquirrelSplashScreen splash = null;
		if (args.getShowSplashScreen())
		{
			splash = new SquirrelSplashScreen(_prefs, 18);
		}

      executeStartupTasks(splash, args);

      if( args.detectLongRunningSwingEDTEvents() )
      {
         new EventDispatchThreadWatcher();
      }
   }

	public void initResourcesAndPrefs()
	{
		_prefs = SquirrelPreferences.load();

		Locale locale = LocaleWrapper.constructPreferredLocale(_prefs);
		if (null != locale)
		{
			Locale.setDefault(locale);
		}
		LazyResourceBundle.setLocaleInitialized();

		_resources = new SquirrelResources(SquirrelResources.BUNDLE_BASE_NAME);
	}

	/**
	 * Application is shutting down.
	 */
	public boolean shutdown(boolean updateLaunchScript)
	{
      long begin = System.currentTimeMillis();

		s_log.info("Application.shutdown: BEGIN: " + Calendar.getInstance().getTime());

		_saveApplicationState_beforeWidgetClosing(begin);
      s_log.info("Application.shutdown: saveApplicationState() ELAPSED: " + (System.currentTimeMillis() - begin));

      if (!closeAllSessions())
      {
         return false;
      }

      // E.g. the SQL panel divider location didn't get saved on closing.
		_saveApplicationState_afterWidgetClosing(begin);

      _pluginManager.unloadPlugins();
      s_log.info("Application.shutdown: _pluginManager.unloadPlugins() ELAPSED: " + (System.currentTimeMillis() - begin));

		closeAllViewers();
      s_log.info("Application.shutdown: closeAllViewers() ELAPSED: " + (System.currentTimeMillis() - begin));

		closeOutputStreams();
      s_log.info("Application.shutdown: closeOutputStreams() ELAPSED: " + (System.currentTimeMillis() - begin));

		SchemaInfoCacheSerializer.waitTillStoringIsDone();
      s_log.info("Application.shutdown: SchemaInfoCacheSerializer.waitTillStoringIsDone() ELAPSED: " + (System.currentTimeMillis() - begin));

      s_log.info("Application.shutdown END: " + Calendar.getInstance().getTime());
		LoggerController.shutdown();

		return true;
	}

	/**
	 * Saves off preferences and all state present in the application.
	 */
	public void saveApplicationState()
	{
		long begin = System.currentTimeMillis();
      _saveApplicationState_beforeWidgetClosing(begin);
      _saveApplicationState_afterWidgetClosing(begin);
	}

	private void _saveApplicationState_beforeWidgetClosing(long begin)
   {

      _prefs.setFirstRun(false);
      s_log.info("saveApplicationState: _prefs.setFirstRun(false) ELAPSED: " + (System.currentTimeMillis() - begin));

      for (ApplicationListener l : _listeners.toArray(new ApplicationListener[0]))
      {
         l.saveApplicationState();
      }
      s_log.info("saveApplicationState: _listeners ELAPSED: " + (System.currentTimeMillis() - begin));

      saveDrivers();
      s_log.info("saveApplicationState: saveDrivers() ELAPSED: " + (System.currentTimeMillis() - begin));

      saveAliases();
      s_log.info("saveApplicationState: saveAliases() ELAPSED: " + (System.currentTimeMillis() - begin));

      _recentFilesManager.saveJsonBean(_appFiles.getRecentFilesJsonBeanFile());
      s_log.info("saveApplicationState: saveRecentFiles() ELAPSED: " + (System.currentTimeMillis() - begin));

      // Save Application level SQL history.
      saveSQLHistory();
      s_log.info("saveApplicationState: saveSQLHistory() ELAPSED: " + (System.currentTimeMillis() - begin));

      // Save options selected for Cell Import Export operations
      saveCellImportExportInfo();
      s_log.info("saveApplicationState: saveCellImportExportInfo() ELAPSED: " + (System.currentTimeMillis() - begin));

      // Save options selected for Edit Where Columns
      saveEditWhereColsInfo();
      s_log.info("saveApplicationState: saveEditWhereColsInfo() ELAPSED: " + (System.currentTimeMillis() - begin));

      // Save options selected for DataType-specific properties
      saveDataTypePreferences();
      s_log.info("saveApplicationState: saveDataTypePreferences() ELAPSED: " + (System.currentTimeMillis() - begin));

      // Save user specific WIKI configurations
      saveUserSpecificWikiConfigurations();
      s_log.info("saveApplicationState: saveUserSpecificWikiConfigurations() ELAPSED: " + (System.currentTimeMillis() - begin));

      _prefs.save();
   }

	private void _saveApplicationState_afterWidgetClosing(long begin)
	{
		_propsImpl.saveProperties();
		s_log.info("saveApplicationState: _propsImpl.saveProperties() ELAPSED: " + (System.currentTimeMillis() - begin));
	}


	/**
     * 
     */
	private void closeOutputStreams()
	{
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
	}

	/**
	 * Saves alias definitions that are in memory to the aliases file.
	 */
	private void saveAliases()
	{
		try
		{
			final File file = _appFiles.getDatabaseAliasesFile();
			_cache.saveAliases(file);
		}
		catch (Throwable th)
		{
			String thMsg = th.getMessage();
			if (thMsg == null)
			{
				thMsg = "";
			}
			String msg = s_stringMgr.getString("Application.error.aliassave", th.getMessage());
			showErrorDialog(msg, th);
			s_log.error(msg, th);
		}
	}

	/**
	 * Saves the driver definitions that are in memory to the drivers file.
	 */
	private void saveDrivers()
	{
		try
		{
			final File file = _appFiles.getDatabaseDriversFile();
			_cache.saveDrivers(file);
		}
		catch (Throwable th)
		{
			String msg = s_stringMgr.getString("Application.error.driversave", th.getMessage());
			showErrorDialog(msg, th);
			s_log.error(msg, th);
		}
	}

	/**
     * 
     */
	private void closeAllViewers()
	{
		try
		{
			FileViewerFactory.getInstance().closeAllViewers();
		}
		catch (Throwable t)
		{
			// i18n[Application.error.closeFileViewers=Unable to close all file viewers]
			s_log.error(s_stringMgr.getString("Application.error.closeFileViewers"), t);
		}
	}

	/**
	 * Returns true is closing all sessions was successful.
	 * 
	 * @return
	 */
	private boolean closeAllSessions()
	{
		boolean result = false;
		try
		{
			if (!_sessionManager.closeAllSessions())
			{
				s_log.info(s_stringMgr.getString("Application.shutdownCancelled", Calendar.getInstance()
					.getTime()));
			}
			else
			{
				result = true;
			}
		}
		catch (Throwable t)
		{
			String msg = s_stringMgr.getString("Application.error.closeAllSessions", t.getMessage());
			s_log.error(msg, t);
		}
		return result;
	}


	public IPluginManager getPluginManager()
	{
		return _pluginManager;
	}

	/**
	 * Return the manager responsible for windows.
	 * 
	 * @return the manager responsible for windows.
	 */
	@Override
	public WindowManager getWindowManager()
	{
		return _windowManager;
	}

	@Override
	public ActionCollection getActionCollection()
	{
		return _actionRegistry.getActionCollection();
	}

	@Override
	public ActionRegistry getActionRegistry()
	{
		return _actionRegistry;
	}

	@Override
	public SQLDriverManager getSQLDriverManager()
	{
		return _driverMgr;
	}

	@Override
	public DataCache getDataCache()
	{
		return _cache;
	}

	@Override
	public IPlugin getDummyAppPlugin()
	{
		return _dummyPlugin;
	}

	@Override
	public SquirrelResources getResources()
	{
		return _resources;
	}

	@Override
	public IMessageHandler getMessageHandler()
	{
		return getMainFrame().getMessagePanel();
	}

	@Override
	public SquirrelPreferences getSquirrelPreferences()
	{
		return _prefs;
	}

	@Override
   public DesktopStyle getDesktopStyle()
   {
      return _desktopStyle;
   }

   @Override
	public ShortcutManager getShortcutManager()
	{
		return _shortcutManager;
	}

   public MainFrame getMainFrame()
	{
		// return _mainFrame;
		return _windowManager.getMainFrame();
	}

	/**
	 * Retrieve the object that manages sessions.
	 * 
	 * @return <TT>SessionManager</TT>.
	 */
	public SessionManager getSessionManager()
	{
		return _sessionManager;
	}

	/**
	 * Display an error message dialog.
	 * 
	 * @param msg
	 *           The error msg.
	 */
	public void showErrorDialog(String msg)
	{
		s_log.error(msg);
		new ErrorDialog(getMainFrame(), msg).setVisible(true);
	}

	/**
	 * Display an error message dialog.
	 * 
	 * @param th
	 *           The Throwable that caused the error
	 */
	public void showErrorDialog(Throwable th)
	{
		s_log.error(th);
		new ErrorDialog(getMainFrame(), th).setVisible(true);
	}

	/**
	 * Display an error message dialog.
	 * 
	 * @param msg
	 *           The error msg.
	 * @param th
	 *           The Throwable that caused the error
	 */
	public void showErrorDialog(String msg, Throwable th)
	{
		s_log.error(msg, th);
		new ErrorDialog(getMainFrame(), msg, th).setVisible(true);
	}

	/**
	 * Return the collection of <TT>FontInfo </TT> objects for this app.
	 * 
	 * @return the collection of <TT>FontInfo </TT> objects for this app.
	 */
	public FontInfoStore getFontInfoStore()
	{
		return _fontInfoStore;
	}

	/**
	 * Return the thread pool for this app.
	 * 
	 * @return the thread pool for this app.
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
	 * @return the factory object used to create the SQL entry panel.
	 */
	public ISQLEntryPanelFactory getSQLEntryPanelFactory()
	{
		return _sqlEntryFactory;
	}

	/**
	 * Set the factory object used to create the SQL entry panel.
	 * 
	 * @param factory
	 *           the factory object used to create the SQL entry panel.
	 */
	public void setSQLEntryPanelFactory(ISQLEntryPanelFactory factory)
	{
		_sqlEntryFactory = factory != null ? factory : new DefaultSQLEntryPanelFactory();
	}

	/**
	 * Retrieve the application level SQL History object.
	 * 
	 * @return the application level SQL History object.
	 */
	public SQLHistory getSQLHistory()
	{
		return _sqlHistory;
	}

	public synchronized void addToMenu(int menuId, JMenu menu)
	{
		final MainFrame mf = getMainFrame();
		if (mf != null)
		{
			mf.addToMenu(menuId, menu);
		}
		else
		{
			throw new IllegalStateException(s_stringMgr.getString("Application.error.menuadding"));
		}
	}

	public synchronized void addToMenu(int menuId, Action action)
	{
		final MainFrame mf = getMainFrame();
		if (mf != null)
		{
			mf.addToMenu(menuId, action);
		}
		else
		{
			throw new IllegalStateException(s_stringMgr.getString("Application.error.menuadding"));
		}
	}

	/**
	 * Add component to the main frames status bar.
	 * 
	 * @param comp
	 *           Component to add.
	 */
	public void addToStatusBar(JComponent comp)
	{
		final MainFrame mf = getMainFrame();
		if (mf != null)
		{
			mf.addToStatusBar(comp);
		}
		else
		{
			throw new IllegalStateException(s_stringMgr.getString("Application.error.compadding"));
		}
	}

	/**
	 * Remove component to the main frames status bar.
	 * 
	 * @param comp
	 *           Component to remove.
	 */
	public void removeFromStatusBar(JComponent comp)
	{
		final MainFrame mf = getMainFrame();
		if (mf != null)
		{
			mf.removeFromStatusBar(comp);
		}
		else
		{
			throw new IllegalStateException(s_stringMgr.getString("Application.error.compremoving"));
		}
	}

	/**
	 * Launches the specified url in the system default web-browser
	 * 
	 * @param url
	 *           the URL of the web page to display.
	 */
	public void openURL(String url)
	{
		BareBonesBrowserLaunch.openURL(url);
	}

	/**
	 * Execute the taks required to start SQuirreL. Each of these is displayed as a message on the splash
	 * screen (if one is being used) in order to let the user know what is happening.
	 * 
	 * @param splash
	 *           The splash screen (can be null).
	 * @param args
	 *           Application arguments.
	 * @throws IllegalArgumentException
	 *            Thrown if <TT>ApplicationArguments<.TT> is null.
	 */
	private void executeStartupTasks(SquirrelSplashScreen splash, ApplicationArguments args)
	{
		if (args == null) { throw new IllegalArgumentException("ApplicationArguments == null"); }

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.createSessionManager"));
		// AliasMaintSheetFactory.initialize(this);
		// DriverMaintSheetFactory.initialize(this);
		_sessionManager = new SessionManager(this);

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadingprefs"));

		final boolean loadPlugins = args.getLoadPlugins();
		if (loadPlugins)
		{
			indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadingplugins"));
		}
		else
		{
			indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.notloadingplugins"));
		}

		UIFactory.initialize(_prefs, this);
		_pluginManager = new PluginManager(this);
		if (args.getLoadPlugins())
		{
			if (null != splash && _prefs.getShowPluginFilesInSplashScreen())
			{
				ClassLoaderListener listener = splash.getClassLoaderListener();
				_pluginManager.setClassLoaderListener(listener);
			}

			if (args.getPluginList() != null)
			{
				_pluginManager.loadPluginsFromList(args.getPluginList());
			}
			else
			{
				_pluginManager.loadPlugins();
			}
		}

      // Final argument validation after all plugins have been loaded.  This will exit if there is an unrecognized argument in the list.
      args.validateArgs(true);

      indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadingactions"));
		_actionRegistry = new ActionRegistry();

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadinguseracc"));
		_actionRegistry.loadActionKeys(_prefs.getActionKeys());

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.createjdbcmgr"));
		initDriverManager();

		// TODO: pass in a message handler so user gets error msgs.
		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadingjdbc"));
		initAppFiles();

		String errMsg = FileTransformer.transform(_appFiles);
		if (null != errMsg)
		{
			System.err.println(errMsg);
			JOptionPane.showMessageDialog(null, errMsg, "SQuirreL failed to start", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}

		initDataCache();

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.createWindowManager"));
		_windowManager = new WindowManager(this, args.getUserInterfaceDebugEnabled());

		// _mainFrame = new MainFrame(this);

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.uifactoryinit"));
		// AliasMaintSheetFactory.initialize(this);
		// DriverMaintSheetFactory.initialize(this);

		String initializingPlugins = s_stringMgr.getString("Application.splash.initializingplugins");
		String notloadingplugins = s_stringMgr.getString("Application.splash.notloadingplugins");
		String task = (loadPlugins ? initializingPlugins : notloadingplugins);
		indicateNewStartupTask(splash, task);
		if (loadPlugins)
		{
			_pluginManager.initializePlugins();
			for (Iterator<PluginLoadInfo> it = _pluginManager.getPluginLoadInfoIterator(); it.hasNext();)
			{
				PluginLoadInfo pli = it.next();
				long created = pli.getCreationTime();
				long load = pli.getLoadTime();
				long init = pli.getInitializeTime();
				Object[] params = new Object[] { pli.getInternalName(), created, load, init, created + load + init};
				String pluginLoadMsg = s_stringMgr.getString("Application.splash.loadplugintime", params);
				s_log.info(pluginLoadMsg);
			}
		}

		// i18n[Application.splash.loadsqlhistory=Loading SQL history...]
		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.recentfiles"));
		loadRecentFileHistory();


		// i18n[Application.splash.loadsqlhistory=Loading SQL history...]
		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadsqlhistory"));
		loadSQLHistory();

		// i18n[Application.splash.loadcellselections=Loading Cell Import/Export selections...]
		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadcellselections"));
		loadCellImportExportInfo();

		// i18n[Application.splash.loadeditselections=Loading Edit 'Where' Columns selections...]
		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadeditselections"));
		loadEditWhereColsInfo();

		// i18n[Application.splash.loaddatatypeprops=Loading Data Type Properties...]
		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loaddatatypeprops"));
		loadDTProperties();
		
		// i18n[Application.splash.loadsqlhistory=Loading user specific WIKI configurations...]
		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadUserSpecificWikiConfiguration"));
		loadUserSpecificWikiTableConfigurations();

		// i18n[Application.splash.showmainwindow=Showing main window...]
		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.showmainwindow"));
		_windowManager.moveToFront(_windowManager.getMainFrame());
		_threadPool.setParentForMessages(_windowManager.getMainFrame());


		new ConnectToStartupAliasesCommand(this).execute();

		if (_prefs.isFirstRun())
		{
			try
			{
				new ViewHelpCommand(this).execute();
			}
			catch (BaseException ex)
			{
				// i18n[Application.error.showhelpwindow=Error showing help window]
				s_log.error(s_stringMgr.getString("Application.error.showhelpwindow"), ex);
			}
		}

		_actionRegistry.registerMissingActionsToShortcutManager();

		if (args.getShutdownTimerSeconds() != null)
      {
			_shutdownTimer.setShutdownSeconds(args.getShutdownTimerSeconds());
			_shutdownTimer.setApplication(this);
			_shutdownTimer.start();
		}
	}

	public void initDriverManager()
	{
		_driverMgr = new SQLDriverManager();
	}

	public void initAppFiles()
	{
		_appFiles = new ApplicationFiles();
	}

	public void initDataCache()
	{
		_cache =
			new DataCache(_driverMgr, _appFiles.getDatabaseDriversFile(), _appFiles.getDatabaseAliasesFile(),
				_resources.getDefaultDriversUrl(), this);
	}

	/**
	 * If we are running with a splash screen then indicate in the splash screen that a new task has commenced.
	 * 
	 * @param splash
	 *           Splash screen.
	 * @param taskDescription
	 *           Description of new task.
	 */
	private void indicateNewStartupTask(SquirrelSplashScreen splash, String taskDescription)
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

		if (propName == null || propName.equals(SquirrelPreferences.IPropertyNames.JDBC_DEBUG_TYPE))
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
				_sqlHistory = (SQLHistory) it.next();
			}
		}
		catch (FileNotFoundException ignore)
		{
			// History file not found for user - first time user ran pgm.
		}
		catch (Exception ex)
		{
			// i18n[Application.error.loadsqlhistory=Unable to load SQL history from persistant storage.]
			s_log.error(s_stringMgr.getString("Application.error.loadsqlhistory"), ex);
		}
		finally
		{
			if (_sqlHistory == null)
			{
				_sqlHistory = new SQLHistory();
			}
		}
	}

	private void loadRecentFileHistory()
	{
		getRecentFilesManager().initJSonBean(new ApplicationFiles().getRecentFilesJsonBeanFile());
	}


	/**
	 * Load the configurations for WIKI tables.
	 * @see WikiTableConfigurationStorage
	 */
	private void loadUserSpecificWikiTableConfigurations()
	{
		try
		{
			WikiTableConfigurationStorage data;
			
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(new ApplicationFiles().getUserSpecificWikiConfigurationsFile());
			Iterator it = doc.iterator();
			if (it.hasNext()){
				data =   (WikiTableConfigurationStorage) it.next();
				wikiTableConfigFactory.replaceUserSpecificConfigurations(data.configurationsAsList());
			}
		}
		catch (FileNotFoundException ignore)
		{
			// History file not found for user - first time user ran pgm.
		}
		catch (Exception ex)
		{
			// i18n[Application.error.loadsqlhistory=Unable to load SQL history from persistant storage.]
			s_log.error(s_stringMgr.getString("Application.error.loadUserSpecificWikiConfiguration"), ex);
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
			if (_prefs.getSessionProperties().getLimitSQLEntryHistorySize())
			{
				SQLHistoryItem[] data = _sqlHistory.getData();

				int maxSize = _prefs.getSessionProperties().getSQLEntryHistorySize();
				if (data.length > maxSize)
				{
					SQLHistoryItem[] reducedData = new SQLHistoryItem[maxSize];
					System.arraycopy(data, 0, reducedData, 0, maxSize);
					_sqlHistory.setData(reducedData);
				}
			}

			XMLBeanWriter wtr = new XMLBeanWriter(_sqlHistory);
			wtr.save(new ApplicationFiles().getUserSQLHistoryFile());
		}
		catch (Exception ex)
		{
			// i18n[Application.error.savesqlhistory=Unable to write SQL queries to persistant storage.]
			s_log.error(s_stringMgr.getString("Application.error.savesqlhistory"), ex);
		}
	}
	
	/**
	 * Save user specific configurations for WIKI tables
	 */
	private void saveUserSpecificWikiConfigurations()
	{
		// Get the history into an array.
		try
		{
			WikiTableConfigurationStorage data = new WikiTableConfigurationStorage(wikiTableConfigFactory.getUserSpecificConfigurations());
			
			XMLBeanWriter wtr = new XMLBeanWriter(data);
			wtr.save(new ApplicationFiles().getUserSpecificWikiConfigurationsFile());
		}
		catch (Exception ex)
		{
			s_log.error(s_stringMgr.getString("Application.error.saveUserSpecificWikiConfiguration"), ex);
		}
	}

	/**
	 * Load the options previously selected by user for import/export of data in various Cells.
	 */
	@SuppressWarnings("unchecked")
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
				saverInstance = (CellImportExportInfoSaver) it.next();
			}
		}
		catch (FileNotFoundException ignore)
		{
			// Cell Import/Export file not found for user - first time user ran pgm.
		}
		catch (Exception ex)
		{
			// i18n[Application.error.loadcellselections=Unable to load Cell Import/Export selections from
			// persistant storage.]
			s_log.error(s_stringMgr.getString("Application.error.loadcellselections"), ex);
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
			// i18n[Application.error.writecellselections=Unable to write Cell Import/Export options to
			// persistant storage.]
			s_log.error(s_stringMgr.getString("Application.error.writecellselections"), ex);
		}
	}

	/**
	 * Load the options previously selected by user for specific cols to use in WHERE clause when editing
	 * cells.
	 */
	@SuppressWarnings("all")
	private void loadEditWhereColsInfo()
	{

		try
		{
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(new ApplicationFiles().getEditWhereColsFile());
			Iterator it = doc.iterator();
			if (it.hasNext())
			{
				editWhereCols = (EditWhereCols) it.next();
			}
		}
		catch (FileNotFoundException ignore)
		{
			// Cell Import/Export file not found for user - first time user ran pgm.
		}
		catch (Exception ex)
		{
			// i18n[Application.error.loadcolsinfo=Unable to load Edit 'Where' Columns selections.]
			s_log.error(s_stringMgr.getString("Application.error.loadcolsinfo"), ex);
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
			XMLBeanWriter wtr = new XMLBeanWriter(editWhereCols);
			wtr.save(new ApplicationFiles().getEditWhereColsFile());
		}
		catch (Exception ex)
		{
			// i18n[Application.error.savecolsinfo=Unable to write Edit Where Cols options to persistant
			// storage.]
			s_log.error(s_stringMgr.getString("Application.error.savecolsinfo"), ex);
		}
	}

	/**
	 * Load the options previously selected by user for specific cols to use in WHERE clause when editing
	 * cells.
	 */
	@SuppressWarnings("all")
	private void loadDTProperties()
	{
		DTProperties saverInstance = null;
		try
		{
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(new ApplicationFiles().getDTPropertiesFile());
			Iterator<Object> it = doc.iterator();
			if (it.hasNext())
			{
				saverInstance = (DTProperties) it.next();
				DTProperties x = saverInstance;
			}
		}
		catch (FileNotFoundException ignore)
		{
			// Cell Import/Export file not found for user - first time user ran pgm.
		}
		catch (Exception ex)
		{
			// i18n[Application.error.loaddatatypeprops=Unable to load DataType Properties selections from
			// persistant storage.]
			s_log.error(s_stringMgr.getString("Application.error.loaddatatypeprops"), ex);
		}
		finally
		{
			// nothing needed here??
		}
	}

	/**
	 * Save the options selected by user for Cell Import Export.
	 */
	private void saveDataTypePreferences()
	{
		try
		{
			XMLBeanWriter wtr = new XMLBeanWriter(new DTProperties());
			wtr.save(new ApplicationFiles().getDTPropertiesFile());
		}
		catch (Exception ex)
		{
			// i18n[Application.error.savedatatypeprops=Unable to write DataType properties to persistant
			// storage.]
			s_log.error(s_stringMgr.getString("Application.error.savedatatypeprops"), ex);
		}
	}

	/**
	 * Persists the specified category of preferences to file if the user has the
	 * "always save preferences immediately" preference checked.
	 * 
	 * @param preferenceType
	 *           the enumerated type that indicates what category of preferences to be persisted.
	 */
	public void savePreferences(PreferenceType preferenceType)
	{
		if (!_prefs.getSavePreferencesImmediately()) { return; }
		switch (preferenceType)
		{
		case ALIAS_DEFINITIONS:
			saveAliases();
			break;
		case DRIVER_DEFINITIONS:
			saveDrivers();
			break;
		case DATATYPE_PREFERENCES:
			saveDataTypePreferences();
			break;
		case CELLIMPORTEXPORT_PREFERENCES:
			saveCellImportExportInfo();
			break;
		case SQLHISTORY:
			saveSQLHistory();
			break;
		case EDITWHERECOL_PREFERENCES:
			saveEditWhereColsInfo();
			break;
		case WIKI_CONFIGURATION:
			saveUserSpecificWikiConfigurations();
			break;
		default:
			s_log.error("Unknown preference type: " + preferenceType);
		}
	}

	public void addApplicationListener(ApplicationListener l)
	{
		_listeners.add(l);
	}

	public void removeApplicationListener(ApplicationListener l)
	{
		_listeners.remove(l);
	}

	/**
	 * Setup applications Look and Feel.
	 */
	protected void setupLookAndFeel(IApplicationArguments args)
	{
		/* 
		 * Don't prevent the user from overriding the laf is they choose to use 
		 * Swing's default laf prop 
		 */
		String userSpecifiedOverride = System.getProperty("swing.defaultlaf");
		if (userSpecifiedOverride != null && !"".equals(userSpecifiedOverride)) { return; }

		String lafClassName = args.useNativeLAF() ? UIManager.getSystemLookAndFeelClassName() : MetalLookAndFeel.class.getName();

		if (!args.useDefaultMetalTheme())
		{
			// Will be overwritten if the L&F Plugin is used.
			MetalLookAndFeel.setCurrentTheme(new AllBluesBoldMetalTheme());
		}

		try
		{
			// The following is a work-around for the problem on Mac OS X where
			// the Apple LAF delegates to the Swing Popup factory but then
			// tries to set a 90% alpha on the underlying Cocoa window, which
			// will always be null if you're using JGoodies L&F
			// see http://www.caimito.net/pebble/2005/07/26/1122392314480.html#comment1127522262179
			// This has no effect on Linux/Windows
			PopupFactory.setSharedInstance(new PopupFactory());

			UIManager.setLookAndFeel(lafClassName);
		}
		catch (Exception ex)
		{
			// i18n[Application.error.setlaf=Error setting LAF]
			s_log.error(s_stringMgr.getString("Application.error.setlaf"), ex);
		}
	}

	@SuppressWarnings("deprecation")
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
					// i18n[Application.info.setjdbcdebuglog=Attempting to set JDBC debug log to output stream]
					s_log.debug(s_stringMgr.getString("Application.info.setjdbcdebuglog"));
					_jdbcDebugOutputStream = new PrintStream(new FileOutputStream(outFile));
					DriverManager.setLogStream(_jdbcDebugOutputStream);
					// i18n[Application.info.setjdbcdebuglogsuccess=JDBC debug log set to output stream
					// successfully]
					s_log.debug(s_stringMgr.getString("Application.info.setjdbcdebuglogsuccess"));
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
					// i18n[Application.info.jdbcwriter=Attempting to set JDBC debug log to writer]
					s_log.debug(s_stringMgr.getString("Application.info.jdbcwriter"));
					_jdbcDebugOutputWriter = new PrintWriter(new FileWriter(outFile));
					DriverManager.setLogWriter(_jdbcDebugOutputWriter);
					// i18n[Application.info.jdbcwritersuccess=JDBC debug log set to writer successfully]
					s_log.debug(s_stringMgr.getString("Application.info.jdbcwritersuccess"));
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

	/**
	 * @see net.sourceforge.squirrel_sql.client.IApplication#getWikiTableConfigFactory()
	 */
	@Override
	public IWikiTableConfigurationFactory getWikiTableConfigFactory() {
		return wikiTableConfigFactory;
	}


	@Override
   public MultipleWindowsHandler getMultipleWindowsHandler()
   {
      return _multipleWindowsHandler;
   }

   @Override
   public RecentFilesManager getRecentFilesManager()
   {
      return _recentFilesManager;
   }

	@Override
	public PasteHistory getPasteHistory()
	{
		return _pasteHistory;
	}

   @Override
   public RowsWindowFrameRegistry getRowsWindowFrameRegistry()
   {
		return _rowsWindowFrameRegistry;
   }

   @Override
   public PropsImpl getPropsImpl()
   {
		if(null == _propsImpl)
		{
			_propsImpl = new PropsImpl();
		}

		return _propsImpl;
   }

}
