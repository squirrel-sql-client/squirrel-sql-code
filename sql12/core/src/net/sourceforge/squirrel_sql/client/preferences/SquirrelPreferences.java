package net.sourceforge.squirrel_sql.client.preferences;
/*
 * Copyright (C) 2001-2004 Colin Bell
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

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.client.action.ActionKeys;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrameWindowState;
import net.sourceforge.squirrel_sql.client.plugin.PluginStatus;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.GitHandler;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.util.PropertyChangeReporter;
import net.sourceforge.squirrel_sql.fw.util.ProxySettings;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
/**
 * This class represents the application preferences.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SquirrelPreferences implements Serializable
{

   public interface IPropertyNames
   {
      String ACTION_KEYS = "actionKeys";
      String CONFIRM_SESSION_CLOSE = "confirmSessionClose";
      String FIRST_RUN = "firstRun";
      String JDBC_DEBUG_TYPE = "jdbcDebugtype";
      String LOGIN_TIMEOUT = "loginTimeout";
      String LARGE_SCRIPT_STMT_COUNT = "largeScriptStmtCount";
      String COPY_QUOTED_SQLS_TO_CLIP = "copyQuotedSqlsToClip";
      String ALLOW_RUN_ALL_SQLS_IN_EDITOR = "allowRunAllSQLsInEditor";
      String MARK_CURRENT_SQL = "markCurrentSql";
      String CURRENT_SQL_MARK_COLOR_RGB = "currentSqlMarkColorRGB";
		String USE_STATEMENT_SEPARATOR_AS_SQL_TO_EXECUTE_BOUNDS = "useStatementSeparatorAsSqlToExecuteBounds";
      String MAIN_FRAME_STATE = "mainFrameWindowState";
      String MAXIMIMIZE_SESSION_SHEET_ON_OPEN = "maximizeSessionSheetOnOpen";
		String PLUGIN_OBJECTS = "pluginObjects";
      String PLUGIN_STATUSES = "pluginStatuses";
      String PROXY = "proxyPerferences";
      String UPDATE = "updatePreferences";
      String SCROLLABLE_TABBED_PANES = "useScrollableTabbedPanes";
      String SESSION_PROPERTIES = "sessionProperties";
      String SHOW_ALIASES_TOOL_BAR = "showAliasesToolBar";
      String SHOW_CONTENTS_WHEN_DRAGGING = "showContentsWhenDragging";
      String TABBED_STYLE = "tabbedStyle";
      String USE_SCROLLABLE_TABBED_PANES_FOR_SESSION_TABS = "useScrollableTabbedPanesForSessionTabs";
      String SHOW_TABBED_STYLE_HINT = "showTabbedStyleHint";
      String SHOW_DRIVERS_TOOL_BAR = "showDriversToolBar";
      String SHOW_LOADED_DRIVERS_ONLY = "showLoadedDriversOnly";
      String SHOW_MAIN_STATUS_BAR = "showMainStatusBar";
      String SHOW_MAIN_TOOL_BAR = "showMainToolBar";
      String SHOW_TOOLTIPS = "showToolTips";
      String SHOW_COLOR_ICONS_IN_TOOLBAR = "showColorIconsInToolbars";
      String SHOW_PLUGIN_FILES_IN_SPLASH_SCREEN = "showPluginFilesInSplashScreen";
      String USE_SHORT_SESSION_TITLE = "useShortSessionTitle";
      String FILE_OPEN_IN_PREVIOUS_DIR = "fileOpenInPreviousDir";
      String FILE_OPEN_IN_SPECIFIED_DIR = "fileOpenInSpecifiedDir";
      String FILE_SPECIFIED_DIR = "fileSpecifiedDir";
      String FILE_PREVIOUS_DIR = "filePreviousdDir";
      String WARN_JRE_JDBC_MISMATCH = "warnJreJdbcMismatch";
      String WARN_FOR_UNSAVED_FILE_EDITS = "warnForUnsavedFileEdits";
      String WARN_FOR_UNSAVED_BUFFER_EDITS = "warnForUnsavedBufferEdits";
      String SHOW_SESSION_STARTUP_TIME_HINT = "showSessionStartupTimeHint";
      String SHOW_DEBUG_LOG_MESSAGES = "showDebugLogMessages";
      String SHOW_INFO_LOG_MESSAGES = "showInfoLogMessages";
      String SHOW_ERROR_LOG_MESSAGES = "showErrorLogMessages";
      String SHOW_WARN_LOG_MESSAGES = "showWarnLogMessages";
      String SAVE_PREFERENCES_IMMEDIATELY = "savePreferencesImmediately";
      String SAVE_ALIASES_AND_DRIVERS_IMMEDIATELY = "saveAliasesAndDriversImmediately";
      String SELECT_ON_RIGHT_MOUSE_CLICK = "selectOnRightMouseClick";
      String SHOW_PLEASE_WAIT_DIALOG = "showPleaseWaitDialog";
      String PREFERRED_LOCALE = "preferredLocale";
      String MAX_COLUMN_ADJUST_LENGTH_DEFINED = "maxColumnAdjustLengthDefined";
      String MAX_COLUMN_ADJUST_LENGTH = "maxColumnAdjustLength";
      String REMEMBER_VALUE_OF_POPUP = "rememberValueOfPopup";
      String RELOAD_SQL_CONTENTS = "ReloadSqlContents";
      String MAX_TEXTOUTPUT_COLUMN_WIDTH = "MaxTextOutputColumnWidth";
      String NOTIFY_EXTERNAL_FILE_CHANGES = "NotifyExternalFileChanges";

		String ENABLE_CHANGE_TRACKING = "enableChangeTracking";
		String GIT_COMMIT_MSG_MANUALLY = "gitCommitMsgManually";
		String GIT_COMMIT_MSG_DEFAULT = "gitCommitMsgDefault";
		String DELETED_BOLD = "deletedBold";
		String DELETED_ITALICS = "deletedItalics";
		String DELETED_FOREGROUND_RGB = "deletedForegroundRGB";
		String INSERT_BEGIN_BACKGROUND_RGB = "insertBeginBackgroundRGB";
		String INSERT_END_BACKGROUND_RGB = "insertEndBackgroundRGB";

		String MESSAGE_PANEL_MESSAGE_FOREGROUND = "messagePanelMessageForeground";
		String MESSAGE_PANEL_MESSAGE_BACKGROUND = "messagePanelMessageBackground";
		String MESSAGE_PANEL_MESSAGE_HISTORY_FOREGROUND = "messagePanelMessageHistoryForeground";
		String MESSAGE_PANEL_MESSAGE_HISTORY_BACKGROUND = "messagePanelMessageHistoryBackground";

		String MESSAGE_PANEL_WARNING_FOREGROUND = "messagePanelWarningForeground";
		String MESSAGE_PANEL_WARNING_BACKGROUND = "messagePanelWarningBackground";
		String MESSAGE_PANEL_WARNING_HISTORY_FOREGROUND = "messagePanelWarningHistoryForeground";
		String MESSAGE_PANEL_WARNING_HISTORY_BACKGROUND = "messagePanelWarningHistoryBackground";

		String MESSAGE_PANEL_ERROR_FOREGROUND = "messagePanelErrorForeground";
		String MESSAGE_PANEL_ERROR_BACKGROUND = "messagePanelErrorBackground";
		String MESSAGE_PANEL_ERROR_HISTORY_FOREGROUND = "messagePanelErrorHistoryForeground";
		String MESSAGE_PANEL_ERROR_HISTORY_BACKGROUND = "messagePanelErrorHistoryBackground";

		String MESSAGE_PANEL_WHITE_BACKGROUND_AS_UI_DEFAULT = "messagePanelWhiteBackgroundAsUIDefault";
		String MESSAGE_PANEL_BLACK_FOREGROUND_AS_UI_DEFAULT = "messagePanelBlackForegroundAsUIDefault";

		String SHOW_ALIAS_PASSWORD_COPY_BUTTON = "showAliasPasswordCopyButton";
		String SHOW_ALIAS_PASSWORD_SHOW_BUTTON = "showAliasPasswordShowButton";
		String QUERY_TIMEOUT = "queryTimeout";


	}

   public interface IJdbcDebugTypes
	{
		int NONE = 0;
		int TO_STREAM = 1;
		int TO_WRITER = 2;
	}

	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SquirrelPreferences.class);

	private final static ILogger s_log = LoggerController.createLogger(SquirrelPreferences.class);

	/** Bounds of the main frame. */
	private MainFrameWindowState _mainFrameState = new MainFrameWindowState();

	/** Properties for new sessions. */
	private SessionProperties _sessionProps = new SessionProperties();

	/**
	 * Show contents of internal frames when dragging. <CODE>false</CODE> makes
	 * dragging faster.
	 */
	private boolean _showContentsWhenDragging = false;


   private boolean _tabbedStyle = true;

   private boolean _useScrollableTabbedPanesForSessionTabs;

   private boolean _showTabbedStyleHint = true;

	private boolean _fileOpenInPreviousDir = true;

	private boolean _fileOpenInSpecifiedDir = false;

	private String _fileSpecifiedDir = "";

	private String _filePreviousDir = System.getProperty("user.home");

	/** JDBC Debug Type. */
	private int _jdbcDebugType = IJdbcDebugTypes.NONE;

	/** Login timeout (seconds). */
	private int _loginTimeout = 30;

    /** How many statements before we should consider using UI optimizations for
     *  large script execution
     */
   private int _largeScriptStmtCount = 200;

	private boolean _copyQuotedSqlsToClip;

	private boolean _allowRunAllSQLsInEditor = true;

	/** Show tooltips for controls. */
	private boolean _showToolTips = true;

	/** Use scrollable tabbed panes. JDK 1.4 and above only. */
	private boolean _useScrollableTabbedPanes = false;

	/** Show main statusbar. */
	private boolean _showMainStatusBar = true;

	/** Show main toolbar. */
	private boolean _showMainToolBar = true;

	/** Show toolbar in the drivers window. */
	private boolean _showDriversToolBar = true;

	/** Maximize session sheet on open. */
	private boolean _maxSessionSheetOnOpen = false;

	/** Show toolbar in the aliases window. */
	private boolean _showAliasesToolBar = true;

	/** Show color icons in toolbars. */
	private boolean _showColorIconsInToolbars = true;

    /** Show the name of each jar being loaded when loading plugins */
    private boolean _showPluginFilesInSplashScreen = false;
    
	/** Accelerators and mnemonics for actions. */
	private ActionKeys[] _actionsKeys = new ActionKeys[0];

	/** Proxy settings. */
	private ProxySettings _proxySettings = new ProxySettings();

	/** Show loaded drivers only in the Drivers window. */
	private boolean _showLoadedDriversOnly;

 	/** Is this the first time SQuirreL has been run? */
 	private boolean _firstRun = true;

	/** Confirm closing sessions */
	private boolean _confirmSessionClose = true;

	/** Warn for JRE/JDBC Driver API Version mismatch */
	private boolean _warnJreJdbcMismatch = true;

	/** Collection of <TT>PluginStatus</tt> objects. */
	private final ArrayList<PluginStatus> _pluginStatusInfoColl = new ArrayList<>();

	/** Warning when closing session if a file was edited but not saved. */
	private boolean _warnForUnsavedFileEdits = true;

	/** Warning when closing session if a buffer was edited but not saved. */
	private boolean _warnForUnsavedBufferEdits = true;

	/** Hint to Alias Schema Properties when Session startup takes considerable time */
	private boolean _showSessionStartupTimeHint = true;

	/** Show DEBUG log messages in the log viewer */
	private boolean _showDebugLogMessages = true;

	/** Show INFO log messages in the log viewer */
	private boolean _showInfoLogMessages = true;

	/** Show ERROR log messages in the log viewer */
	private boolean _showErrorLogMessages = true;

	/** Show WARN log messages in the log viewer */
	private boolean _showWarnLogMessages = true;

	/** Always save preferences immediately when they change, instead of at shutdown */
	private boolean _savePreferencesImmediately = false;

	private boolean _saveAliasesAndDriversImmediately = false;

	/** Whether or not to change the selection while right-clicking on list or tree node */
    private boolean _selectOnRightMouseClick = true;
        
	/** Object to handle property change events. */
	private transient PropertyChangeReporter _propChgReporter;

	private boolean _showPleaseWaitDialog;

	private String _preferredLocale = LocaleWrapperType.DONT_CHANGE.name();

   private boolean _maxColumnAdjustLengthDefined = false;

   private int _maxColumnAdjustLength = -1;

	private boolean _useShortSessionTitle = false;


	private int _currentSqlMarkColorRGB = Color.blue.getRGB();
	private boolean _markCurrentSql = true;

	private boolean _useStatementSeparatorAsSqlToExecuteBounds = false;

	private boolean _rememberValueOfPopup = false;

	private boolean _reloadSqlContents;

	private int _maxTextOutputColumnWidth = IDataSetViewer.MAX_COLUMN_WIDTH;
	private boolean _notifyExternalFileChanges = true;

	/////////////////////////////////////////////////////////////////////////
	// Change tracking properties
	private boolean _enableChangeTracking = true;
	private boolean _gitCommitMsgManually = false;
	private String _gitCommitMsgDefault = "SQuirreL commit of " + GitHandler.GIT_MSG_FILE_NAME_PLACEHOLDER;
	private boolean _deletedBold = true;
	private boolean _deletedItalics = false;
	private int _deletedForegroundRGB = Color.red.getRGB();
	private int _insertBeginBackgroundRGB = new Color(148, 255, 81).getRGB();
	private int _insertEndBackgroundRGB = new Color(255, 170, 109).getRGB();
	// Change tracking properties
	/////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////
	// Message panel properties
	private int _messagePanelMessageForeground = Color.black.getRGB();
	private int _messagePanelMessageBackground =  Color.green.getRGB();
	private int _messagePanelMessageHistoryForeground = Color.black.getRGB();
	private int _messagePanelMessageHistoryBackground = Color.white.getRGB();

	private int _messagePanelWarningForeground = Color.black.getRGB();
	private int _messagePanelWarningBackground = Color.yellow.getRGB();
	private int _messagePanelWarningHistoryForeground = Color.black.getRGB();
	private int _messagePanelWarningHistoryBackground = new Color(255,255,210).getRGB(); // a light yellow

	private int _messagePanelErrorForeground = Color.red.getRGB();
	private int _messagePanelErrorBackground = Color.white.getRGB();
	private int _messagePanelErrorHistoryForeground = new Color(255,102,102).getRGB();
	private int _messagePanelErrorHistoryBackground = Color.white.getRGB();

	private boolean _messagePanelWhiteBackgroundAsUIDefault = true;
	private boolean _messagePanelBlackForegroundAsUIDefault = true;
	// Message panel properties
	/////////////////////////////////////////////////////////////////////////


	private boolean _showAliasPasswordCopyButton;
	private boolean _showAliasPasswordShowButton;
	private int _queryTimeout = 0;


	public SquirrelPreferences()
	{
		loadDefaults();
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		getPropertyChangeReporter().addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		getPropertyChangeReporter().removePropertyChangeListener(listener);
	}

	public SessionProperties getSessionProperties()
	{
		return _sessionProps;
	}

	public synchronized void setSessionProperties(SessionProperties data)
	{
		if (_sessionProps != data)
		{
			final SessionProperties oldValue = _sessionProps;
			_sessionProps = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.SESSION_PROPERTIES,
												oldValue, _sessionProps);
		}
	}

	public MainFrameWindowState getMainFrameWindowState()
	{
		return _mainFrameState;
	}

	public synchronized void setMainFrameWindowState(MainFrameWindowState data)
	{
		final MainFrameWindowState oldValue = _mainFrameState;
		_mainFrameState = data;
		getPropertyChangeReporter().firePropertyChange(IPropertyNames.MAIN_FRAME_STATE,
											oldValue, _mainFrameState);
	}

	public boolean getTabbedStyle()
	{
		return _tabbedStyle;
	}

	public synchronized void setTabbedStyle(boolean data)
	{
		if (data != _tabbedStyle)
		{
			final boolean oldValue = _tabbedStyle;
			_tabbedStyle = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.TABBED_STYLE,
												oldValue, _tabbedStyle);
		}
	}

   public boolean getUseScrollableTabbedPanesForSessionTabs()
   {
      return _useScrollableTabbedPanesForSessionTabs;
   }

   public synchronized void setUseScrollableTabbedPanesForSessionTabs(boolean data)
   {
      if (data != _useScrollableTabbedPanesForSessionTabs)
      {
         final boolean oldValue = _useScrollableTabbedPanesForSessionTabs;
         _useScrollableTabbedPanesForSessionTabs = data;
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.USE_SCROLLABLE_TABBED_PANES_FOR_SESSION_TABS,
                                    oldValue, _useScrollableTabbedPanesForSessionTabs);
      }
   }

	public boolean getShowTabbedStyleHint()
	{
		return _showTabbedStyleHint;
	}

	public synchronized void setShowTabbedStyleHint(boolean data)
	{
		if (data != _showTabbedStyleHint)
		{
			final boolean oldValue = _showTabbedStyleHint;
			_showTabbedStyleHint = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.SHOW_TABBED_STYLE_HINT,
												oldValue, _showTabbedStyleHint);
		}
	}

	public boolean getShowContentsWhenDragging()
	{
		return _showContentsWhenDragging;
	}

	public synchronized void setShowContentsWhenDragging(boolean data)
	{
		if (data != _showContentsWhenDragging)
		{
			final boolean oldValue = _showContentsWhenDragging;
			_showContentsWhenDragging = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.SHOW_CONTENTS_WHEN_DRAGGING,
												oldValue, _showContentsWhenDragging);
		}
	}

	public boolean getShowMainStatusBar()
	{
		return _showMainStatusBar;
	}

	public synchronized void setShowMainStatusBar(boolean data)
	{
		if (data != _showMainStatusBar)
		{
			final boolean oldValue = _showMainStatusBar;
			_showMainStatusBar = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.SHOW_MAIN_STATUS_BAR,
											oldValue, _showMainStatusBar);
		}
	}

	public boolean getShowMainToolBar()
	{
		return _showMainToolBar;
	}

	public synchronized void setShowMainToolBar(boolean data)
	{
		if (data != _showMainToolBar)
		{
			final boolean oldValue = _showMainToolBar;
			_showMainToolBar = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.SHOW_MAIN_TOOL_BAR,
												oldValue, _showMainToolBar);
		}
	}

	public boolean getShowAliasesToolBar()
	{
		return _showAliasesToolBar;
	}

	public synchronized void setShowAliasesToolBar(boolean data)
	{
		if (data != _showAliasesToolBar)
		{
			final boolean oldValue = _showAliasesToolBar;
			_showAliasesToolBar = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.SHOW_ALIASES_TOOL_BAR,
												oldValue, _showAliasesToolBar);
		}
	}

	public boolean getShowDriversToolBar()
	{
		return _showDriversToolBar;
	}

	public synchronized void setShowDriversToolBar(boolean data)
	{
		if (data != _showDriversToolBar)
		{
			final boolean oldValue = _showDriversToolBar;
			_showDriversToolBar = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.SHOW_DRIVERS_TOOL_BAR,
												oldValue, _showDriversToolBar);
		}
	}

	public boolean getShowColoriconsInToolbar()
	{
		return _showColorIconsInToolbars;
	}

	public synchronized void setShowColoriconsInToolbar(boolean data)
	{
		if (data != _showColorIconsInToolbars)
		{
			final boolean oldValue = _showColorIconsInToolbars;
			_showColorIconsInToolbars = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.SHOW_COLOR_ICONS_IN_TOOLBAR,
												oldValue, _showColorIconsInToolbars);
		}
	}

    public boolean getShowPluginFilesInSplashScreen()
    {
        return _showPluginFilesInSplashScreen;
    }

    public synchronized void setShowPluginFilesInSplashScreen(boolean data)
    {
        if (data != _showPluginFilesInSplashScreen)
        {
            final boolean oldValue = _showPluginFilesInSplashScreen;
            _showPluginFilesInSplashScreen = data;
            getPropertyChangeReporter().firePropertyChange(
                            IPropertyNames.SHOW_PLUGIN_FILES_IN_SPLASH_SCREEN,
                            oldValue, 
                            _showPluginFilesInSplashScreen);
        }
    }    
    
	public int getLoginTimeout()
	{
		return _loginTimeout;
	}

	public synchronized void setLoginTimeout(int data)
	{
		if (data != _loginTimeout)
		{
			final int oldValue = _loginTimeout;
			_loginTimeout = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.LOGIN_TIMEOUT,
												oldValue, _loginTimeout);
		}
	}

    public int getLargeScriptStmtCount() {
        return _largeScriptStmtCount;
    }
    
    public synchronized void setLargeScriptStmtCount(int count) {
        if (count != _largeScriptStmtCount) {
            final int oldValue = _largeScriptStmtCount;
            _largeScriptStmtCount = count;
            getPropertyChangeReporter().firePropertyChange(
                                        IPropertyNames.LARGE_SCRIPT_STMT_COUNT,
                                        oldValue, 
                                        _largeScriptStmtCount);
        }
    }
    
	public int getJdbcDebugType()
	{
		return _jdbcDebugType;
	}

	public synchronized void setJdbcDebugType(int data)
	{
		if (data < IJdbcDebugTypes.NONE || data > IJdbcDebugTypes.TO_WRITER)
		{
			throw new IllegalArgumentException("Invalid setDebugJdbcToStream of :" + data);
		}

		if (data != _jdbcDebugType)
		{
			final int oldValue = _jdbcDebugType;
			_jdbcDebugType = data;
			getPropertyChangeReporter().firePropertyChange(
					IPropertyNames.JDBC_DEBUG_TYPE, oldValue, _jdbcDebugType);
		}
	}

	public boolean getShowToolTips()
	{
		return _showToolTips;
	}

	public synchronized void setShowToolTips(boolean data)
	{
		if (data != _showToolTips)
		{
			final boolean oldValue = _showToolTips;
			_showToolTips = data;
			getPropertyChangeReporter().firePropertyChange(
												IPropertyNames.SHOW_TOOLTIPS,
												oldValue, _showToolTips);
		}
	}

	public boolean getUseScrollableTabbedPanes()
	{
		return _useScrollableTabbedPanes;
	}

	public synchronized void setUseScrollableTabbedPanes(boolean data)
	{
		if (data != _useScrollableTabbedPanes)
		{
			final boolean oldValue = _useScrollableTabbedPanes;
			_useScrollableTabbedPanes = data;
			getPropertyChangeReporter().firePropertyChange(
										IPropertyNames.SCROLLABLE_TABBED_PANES,
										oldValue, _useScrollableTabbedPanes);
		}
	}

	public boolean getMaximizeSessionSheetOnOpen()
	{
		return _maxSessionSheetOnOpen;
	}

	public synchronized void setMaximizeSessionSheetOnOpen(boolean data)
	{
		if (data != _maxSessionSheetOnOpen)
		{
			final boolean oldValue = _maxSessionSheetOnOpen;
			_maxSessionSheetOnOpen= data;
			getPropertyChangeReporter().firePropertyChange(
							IPropertyNames.MAXIMIMIZE_SESSION_SHEET_ON_OPEN,
							oldValue, _maxSessionSheetOnOpen);
		}
	}

	public ActionKeys[] getActionKeys()
	{
		return _actionsKeys;
	}

	public ActionKeys getActionKeys(int idx)
	{
		return _actionsKeys[idx];
	}

	public synchronized void setActionKeys(ActionKeys[] data)
	{
		final ActionKeys[] oldValue = _actionsKeys;
		_actionsKeys = data != null ? data : new ActionKeys[0];
		getPropertyChangeReporter().firePropertyChange(IPropertyNames.ACTION_KEYS,
											oldValue, _actionsKeys);
	}

	public void setActionKeys(int idx, ActionKeys value)
	{
		final ActionKeys[] oldValue = _actionsKeys;
		_actionsKeys[idx] = value;
		getPropertyChangeReporter().firePropertyChange(IPropertyNames.ACTION_KEYS,
											oldValue, _actionsKeys);
	}

	public synchronized PluginStatus[] getPluginStatuses()
	{
		final PluginStatus[] ar = new PluginStatus[_pluginStatusInfoColl.size()];
		return _pluginStatusInfoColl.toArray(ar);
	}

	public PluginStatus getPluginStatus(int idx)
	{
		return _pluginStatusInfoColl.get(idx);
	}

	public synchronized void setPluginStatuses(PluginStatus[] data)
	{
		if (data == null)
		{
			data = new PluginStatus[0];
		}

		PluginStatus[] oldValue = new PluginStatus[_pluginStatusInfoColl.size()];
		oldValue = _pluginStatusInfoColl.toArray(oldValue);
		_pluginStatusInfoColl.clear();
		_pluginStatusInfoColl.addAll(Arrays.asList(data));
		getPropertyChangeReporter().firePropertyChange(IPropertyNames.PLUGIN_STATUSES,
											oldValue, data);
	}

	public synchronized void setPluginStatus(int idx, PluginStatus value)
	{
		_pluginStatusInfoColl.ensureCapacity(idx + 1);
		final PluginStatus oldValue = _pluginStatusInfoColl.get(idx);;
		_pluginStatusInfoColl.set(idx, value);
		getPropertyChangeReporter().firePropertyChange(IPropertyNames.PLUGIN_STATUSES,
											oldValue, value);
	}

	/**
	 * Retrieve the proxy settings. Note that this method returns a clone
	 * of the actual proxy settings used.
	 *
	 * @return	<TT>ProxySettings</TT> object.
	 */
	public ProxySettings getProxySettings()
	{
		return (ProxySettings)_proxySettings.clone();
	}


	public synchronized void setProxySettings(ProxySettings data)
	{
		if (data == null)
		{
			data = new ProxySettings();
		}
		final ProxySettings oldValue = _proxySettings;
		_proxySettings= data;
		getPropertyChangeReporter().firePropertyChange(IPropertyNames.PROXY,
											oldValue, _proxySettings);
	}

   /**
	 * @return	whether only the loaded JDBC drivers are displayed in the
	 *			Drivers window.
	 */
	public boolean getShowLoadedDriversOnly()
	{
		return _showLoadedDriversOnly;
	}

	/**
	 * Set whether only the loaded JDBC drivers are displayed in the
	 * Drivers window.
	 *
	 * @param	data	New value for this property.
	 */
	public synchronized void setShowLoadedDriversOnly(boolean data)
	{
		if (data != _showLoadedDriversOnly)
		{
			final boolean oldValue = _showLoadedDriversOnly;
			_showLoadedDriversOnly = data;
			getPropertyChangeReporter().firePropertyChange(
										IPropertyNames.SHOW_LOADED_DRIVERS_ONLY,
										oldValue, _showLoadedDriversOnly);
		}
	}

 	/**
 	 * Is this the first time SQuirreL has been run?
 	 *
 	 * @return	<tt>true</tt> if this is the first time SQuirreL has been run
 	 *			else <tt>false</tt>.
 	 */
 	public boolean isFirstRun()
 	{
 		return _firstRun;
 	}

 	public synchronized void setFirstRun(boolean data)
 	{
 		if (data != _firstRun)
 		{
 			final boolean oldValue = _firstRun;
 			_firstRun = data;
 			getPropertyChangeReporter().firePropertyChange(IPropertyNames.FIRST_RUN,
 											oldValue, _firstRun);
 		}
 	}
 	/**
 	 * Should user confirm whether sessions should be closed.
 	 *
 	 * @return	<tt>true</tt> if user should have to confirm session close
 	 *			else <tt>false</tt>.
 	 */
 	public boolean getConfirmSessionClose()
 	{
 		return _confirmSessionClose;
 	}

 	public synchronized void setConfirmSessionClose(boolean data)
 	{
 		if (data != _confirmSessionClose)
 		{
 			final boolean oldValue = _confirmSessionClose;
 			_confirmSessionClose = data;
 			getPropertyChangeReporter().firePropertyChange(
 										IPropertyNames.CONFIRM_SESSION_CLOSE,
 										oldValue, _confirmSessionClose);
 		}
 	}


   public boolean isFileOpenInPreviousDir()
   {
      return _fileOpenInPreviousDir;
   }

   public synchronized void setFileOpenInPreviousDir(boolean data)
   {
      if (data != _fileOpenInPreviousDir)
      {
         final boolean oldValue = _fileOpenInPreviousDir;
         _fileOpenInPreviousDir = data;
         getPropertyChangeReporter().firePropertyChange(
                              IPropertyNames.FILE_OPEN_IN_PREVIOUS_DIR,
                              oldValue, _fileOpenInPreviousDir);
      }
   }


   public boolean isFileOpenInSpecifiedDir()
   {
      return _fileOpenInSpecifiedDir;
   }

   public synchronized void setFileOpenInSpecifiedDir(boolean data)
   {
      if (data != _fileOpenInSpecifiedDir)
      {
         final boolean oldValue = _fileOpenInSpecifiedDir;
         _fileOpenInSpecifiedDir = data;
         getPropertyChangeReporter().firePropertyChange(
                              IPropertyNames.FILE_OPEN_IN_SPECIFIED_DIR,
                              oldValue, _fileOpenInSpecifiedDir);
      }
   }

   public String getFileSpecifiedDir()
   {
      return _fileSpecifiedDir;
   }

   public synchronized void setFileSpecifiedDir(String data)
   {
      if (false == ("" + data).equals(_fileSpecifiedDir))
      {
         final String oldValue = _fileSpecifiedDir;
         _fileSpecifiedDir = data;
         getPropertyChangeReporter().firePropertyChange(
                              IPropertyNames.FILE_SPECIFIED_DIR,
                              oldValue, _fileSpecifiedDir);
      }
   }

   public String getFilePreviousDir()
   {
      return _filePreviousDir;
   }

   public synchronized void setFilePreviousDir(String data)
   {
      if (false == ("" + data).equals(_filePreviousDir))
      {
         final String oldValue = _filePreviousDir;
         _filePreviousDir = data;
         getPropertyChangeReporter().firePropertyChange(
                              IPropertyNames.FILE_PREVIOUS_DIR,
                              oldValue, _filePreviousDir);
      }
   }



	/**
	 * Helper method.
	 */
	public boolean isJdbcDebugToStream()
	{
		return _jdbcDebugType == IJdbcDebugTypes.TO_STREAM;
	}

	/**
	 * Helper method.
	 */
	public boolean isJdbcDebugToWriter()
	{
		return _jdbcDebugType == IJdbcDebugTypes.TO_WRITER;
	}

	/**
	 * Helper method.
	 */
	public boolean isJdbcDebugDontDebug()
	{
		return !(isJdbcDebugToStream() || isJdbcDebugToWriter());
	}

	/**
	 * Helper method.
	 */
	public void doJdbcDebugToStream()
	{
		setJdbcDebugType(IJdbcDebugTypes.TO_STREAM);
	}

	/**
	 * Helper method.
	 */
	public void doJdbcDebugToWriter()
	{
		setJdbcDebugType(IJdbcDebugTypes.TO_WRITER);
	}

	/**
	 * Helper method.
	 */
	public void dontDoJdbcDebug()
	{
		setJdbcDebugType(IJdbcDebugTypes.NONE);
	}

    public static SquirrelPreferences load()
	{
		File prefsFile = new ApplicationFiles().getUserPreferencesFile();
		try
		{
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(prefsFile);
			Iterator it = doc.iterator();
			if (it.hasNext())
			{
				return (SquirrelPreferences)it.next();
			}
		}
		catch (FileNotFoundException ignore)
		{
			// property file not found for user - first time user ran pgm.
		}
		catch (Exception ex)
		{
			// This error message was formerly i18ned unnecessarily.
			// The new version is supposed to be more stable especially during bootstrap.
			if (null != prefsFile)
			{
				s_log.error("Error occurred reading from preferences file: " + prefsFile.getPath(), ex);
			}
			else
			{
				s_log.error("Error occurred reading preferences: ", ex);
			}
		}
		return new SquirrelPreferences();
	}

	/**
	 * Save preferences to disk.
	 */
	public synchronized void save()
	{
		File prefsFile = new ApplicationFiles().getUserPreferencesFile();
		try
		{
			XMLBeanWriter wtr = new XMLBeanWriter(this);
			wtr.save(prefsFile);
		}
		catch (Exception ex)
		{
			s_log.error(s_stringMgr.getString("SquirrelPreferences.error.writing",
												prefsFile.getPath()), ex);
		}
	}

	private void loadDefaults()
	{
		if (_loginTimeout == -1)
		{
			_loginTimeout = DriverManager.getLoginTimeout();
		}
	}

	private synchronized PropertyChangeReporter getPropertyChangeReporter()
	{
		if (_propChgReporter == null)
		{
			_propChgReporter = new PropertyChangeReporter(this);
		}
		return _propChgReporter;
	}

    /**
     * @param data The _warnJreJdbcMismatch to set.
     */
    public synchronized void setWarnJreJdbcMismatch(boolean data) {
        if (data != _warnJreJdbcMismatch)
        {
            final boolean oldValue = _warnJreJdbcMismatch;
            _warnJreJdbcMismatch = data;
            getPropertyChangeReporter().firePropertyChange(
                                        IPropertyNames.WARN_JRE_JDBC_MISMATCH,
                                        oldValue, _warnJreJdbcMismatch);
        }
    }

    /**
     * @return Returns the _warnJreJdbcMismatch.
     */
    public boolean getWarnJreJdbcMismatch() {
        return _warnJreJdbcMismatch;
    }

    /**
     * @param data The _warnForUnsaveFileEdits to set.
     */
    public synchronized void setWarnForUnsavedFileEdits(boolean data) {
        if (data != _warnForUnsavedFileEdits)
        {
            final boolean oldValue = _warnForUnsavedFileEdits;
            _warnForUnsavedFileEdits = data;
            getPropertyChangeReporter().firePropertyChange(
                                        IPropertyNames.WARN_FOR_UNSAVED_FILE_EDITS,
                                        oldValue, _warnForUnsavedFileEdits);
        }
    }

    /**
     * @return Returns the _warnForUnsaveFileEdits.
     */
    public boolean getWarnForUnsavedFileEdits() {
        return _warnForUnsavedFileEdits;
    }
    
    /**
     * @param data The _warnForUnsavedBufferEdits to set.
     */
    public synchronized void setWarnForUnsavedBufferEdits(boolean data) {
        if (data != _warnForUnsavedBufferEdits)
        {
            final boolean oldValue = _warnForUnsavedBufferEdits;
            _warnForUnsavedBufferEdits = data;
            getPropertyChangeReporter().firePropertyChange(
                                        IPropertyNames.WARN_FOR_UNSAVED_BUFFER_EDITS,
                                        oldValue, _warnForUnsavedBufferEdits);
        }
    }

    /**
     * @return Returns the _warnForUnsaveFileEdits.
     */
    public boolean getWarnForUnsavedBufferEdits() {
        return _warnForUnsavedBufferEdits;
    }



   /**
    * @param data The _warnForUnsavedBufferEdits to set.
    */
   public synchronized void setShowSessionStartupTimeHint(boolean data)
   {
      if (data != _showSessionStartupTimeHint)
      {
         final boolean oldValue = _showSessionStartupTimeHint;
         _showSessionStartupTimeHint = data;
         getPropertyChangeReporter().firePropertyChange(
            IPropertyNames.SHOW_SESSION_STARTUP_TIME_HINT,
            oldValue, _showSessionStartupTimeHint);
      }
   }

   /**
    * @return Returns the _warnForUnsaveFileEdits.
    */
   public boolean getShowSessionStartupTimeHint()
   {
      return _showSessionStartupTimeHint;
   }

   /**
    * @param data The _warnForUnsavedBufferEdits to set.
    */
   public synchronized void setShowDebugLogMessages(boolean data)
   {
      if (data != _showDebugLogMessages)
      {
         final boolean oldValue = _showDebugLogMessages;
         _showDebugLogMessages = data;
         getPropertyChangeReporter().firePropertyChange(
            IPropertyNames.SHOW_DEBUG_LOG_MESSAGES,
            oldValue, _showDebugLogMessages);
      }
   }

   /**
    * @return Returns the _warnForUnsaveFileEdits.
    */
   public boolean getShowDebugLogMessage()
   {
      return _showDebugLogMessages;
   }

 

/**
    * @param data the _showInfoLogMessages to set
    */
   public void setShowInfoLogMessages(boolean data) {
       if (data != _showInfoLogMessages)
       {
          final boolean oldValue = _showInfoLogMessages;
          _showInfoLogMessages = data;
          getPropertyChangeReporter().firePropertyChange(
             IPropertyNames.SHOW_INFO_LOG_MESSAGES,
             oldValue, _showInfoLogMessages);
       }
   }

   /**
    * @return the _showInfoLogMessages
    */
   public boolean getShowInfoLogMessages() {
       return _showInfoLogMessages;
   }

	public void setShowErrorLogMessages(boolean data)
	{
		if(data != _showErrorLogMessages)
		{
			final boolean oldValue = _showErrorLogMessages;
			_showErrorLogMessages = data;
			getPropertyChangeReporter().firePropertyChange( IPropertyNames.SHOW_ERROR_LOG_MESSAGES, oldValue, _showErrorLogMessages);
		}
	}

	public boolean getShowErrorLogMessages()
	{
		return _showErrorLogMessages;
	}

	public void setShowWarnLogMessages(boolean data)
	{
		if(data != _showWarnLogMessages)
		{
			final boolean oldValue = _showWarnLogMessages;
			_showWarnLogMessages = data;
			getPropertyChangeReporter().firePropertyChange( IPropertyNames.SHOW_ERROR_LOG_MESSAGES, oldValue, _showWarnLogMessages);
		}
	}

	public boolean getShowWarnLogMessages()
	{
		return _showWarnLogMessages;
	}

   /**
    * @param data the _savePreferencesImmediately to set
    */
	public void setSavePreferencesImmediately(boolean data)
	{
		if (data != _savePreferencesImmediately)
		{
			final boolean oldValue = _savePreferencesImmediately;
			_savePreferencesImmediately = data;
			getPropertyChangeReporter().firePropertyChange( IPropertyNames.SAVE_PREFERENCES_IMMEDIATELY, oldValue, _savePreferencesImmediately);
		}
	}

   /**
    * @return the _showErrorLogMessages
    */
   public boolean getSavePreferencesImmediately() {
       return _savePreferencesImmediately;
   }


	public boolean getSaveAliasesAndDriversImmediately()
	{
		return _saveAliasesAndDriversImmediately;
	}

	public void setSaveAliasesAndDriversImmediately(boolean data)
	{
		if (data != _savePreferencesImmediately)
		{
			final boolean oldValue = _savePreferencesImmediately;
			_savePreferencesImmediately = data;
			getPropertyChangeReporter().firePropertyChange( IPropertyNames.SAVE_ALIASES_AND_DRIVERS_IMMEDIATELY, oldValue, _savePreferencesImmediately);
		}

		_saveAliasesAndDriversImmediately = data;
	}


	/**
    * Sets the behavior of changing the selected nodes in a list / tree when the popup menu is accessed. 
    * 
    * @param selectOnRightMouseClick if true, then if the popup is triggered over a non-selected node, that 
    * node is selected prior to showing the popup menu.
    */
   public void setSelectOnRightMouseClick(boolean selectOnRightMouseClick) {
   	this._selectOnRightMouseClick = selectOnRightMouseClick;
   }
   
   /**
    * @return a boolean value indicating whether or not to change the selected node in a tree or
    * list on a right-mouse click just before the popup is displayed.
    */
   public boolean getSelectOnRightMouseClick() {
   	return _selectOnRightMouseClick;
   }

   /**
    * @return a boolean value indicating whether or not to show a cancel dialog that allows a user to cancel
    * long-running queries.
    */
	public boolean getShowPleaseWaitDialog() 
	{
		return _showPleaseWaitDialog;
	}
	
	/**
	 * Sets whether or not to show a cancel dialog that allows a user to cancel long-running queries.
	 * 
	 * @param showPleaseWaitDialog boolean value 
	 */
	public void setShowPleaseWaitDialog(boolean showPleaseWaitDialog) {
		this._showPleaseWaitDialog = showPleaseWaitDialog;
	}

	/**
	 * @return the preferredLocale
	 */
	public String getPreferredLocale()
	{
		return _preferredLocale;
	}

	/**
	 * @param locale the preferredLocale to set
	 */
	public void setPreferredLocale(String locale)
	{
		_preferredLocale = locale;
	}

   public void setMaxColumnAdjustLengthDefined(boolean maxColumnAdjustLengthDefined)
   {
      _maxColumnAdjustLengthDefined = maxColumnAdjustLengthDefined;
   }

   public void setMaxColumnAdjustLength(int maxColumnAdjustLength)
   {
      _maxColumnAdjustLength = maxColumnAdjustLength;
   }


   public boolean getMaxColumnAdjustLengthDefined()
   {
      return _maxColumnAdjustLengthDefined;
   }

   public int getMaxColumnAdjustLength()
   {
      return _maxColumnAdjustLength;
   }

	public void setUseShortSessionTitle(boolean useShortSessionTitle)
	{
		_useShortSessionTitle = useShortSessionTitle;
	}

	public boolean getUseShortSessionTitle()
	{
		return _useShortSessionTitle;
	}

	public boolean getCopyQuotedSqlsToClip()
	{
		return _copyQuotedSqlsToClip;
	}

	public boolean isCopyQuotedSqlsToClip()
	{
		return _copyQuotedSqlsToClip;
	}

	public void setCopyQuotedSqlsToClip(boolean copyQuotedSqlsToClip)
	{
		_copyQuotedSqlsToClip = copyQuotedSqlsToClip;
	}

	public boolean getAllowRunAllSQLsInEditor()
	{
		return _allowRunAllSQLsInEditor;
	}

	public boolean isAllowRunAllSQLsInEditor()
	{
		return _allowRunAllSQLsInEditor;
	}

	public void setAllowRunAllSQLsInEditor(boolean allowRunAllSQLsInEditor)
	{
		_allowRunAllSQLsInEditor = allowRunAllSQLsInEditor;
	}

	public boolean isMarkCurrentSql()
	{
		return _markCurrentSql;
	}

	public void setMarkCurrentSql(boolean markCurrentSql)
	{
		_markCurrentSql = markCurrentSql;
	}

	public boolean isUseStatementSeparatorAsSqlToExecuteBounds()
	{
		return _useStatementSeparatorAsSqlToExecuteBounds;
	}

	public void setUseStatementSeparatorAsSqlToExecuteBounds(boolean useStatementSeparatorAsSqlToExecuteBounds)
	{
		_useStatementSeparatorAsSqlToExecuteBounds = useStatementSeparatorAsSqlToExecuteBounds;
	}


	public int getCurrentSqlMarkColorRGB()
	{
		return _currentSqlMarkColorRGB;
	}

	public void setCurrentSqlMarkColorRGB(int currentSqlMarkColorRGB)
	{
		_currentSqlMarkColorRGB = currentSqlMarkColorRGB;
	}


	public Color getCurrentSqlMarkColor()
	{
		return new Color(_currentSqlMarkColorRGB);
	}

	public boolean isRememberValueOfPopup()
	{
		return _rememberValueOfPopup;
	}

	public void setRememberValueOfPopup(boolean b)
	{
		_rememberValueOfPopup = b;
	}

	public boolean isReloadSqlContents()
	{
		return _reloadSqlContents;
	}

	public void setReloadSqlContents(boolean reloadSqlContents)
	{
		_reloadSqlContents = reloadSqlContents;
	}

	public int getMaxTextOutputColumnWidth()
	{
		return _maxTextOutputColumnWidth;
	}

	public void setMaxTextOutputColumnWidth(int maxTextOutputColumnWidth)
	{
		_maxTextOutputColumnWidth = maxTextOutputColumnWidth;
	}



	public boolean isEnableChangeTracking()
	{
		return _enableChangeTracking;
	}

	public void setEnableChangeTracking(boolean enableChangeTracking)
	{
		_enableChangeTracking = enableChangeTracking;
	}

	public boolean isGitCommitMsgManually()
	{
		return _gitCommitMsgManually;
	}

	public void setGitCommitMsgManually(boolean gitCommitMsgManually)
	{
		_gitCommitMsgManually = gitCommitMsgManually;
	}

	public String getGitCommitMsgDefault()
	{
		return _gitCommitMsgDefault;
	}

	public void setGitCommitMsgDefault(String gitCommitMsgDefault)
	{
		_gitCommitMsgDefault = gitCommitMsgDefault;
	}

	public boolean isDeletedBold()
	{
		return _deletedBold;
	}

	public void setDeletedBold(boolean deletedBold)
	{
		_deletedBold = deletedBold;
	}

	public boolean isDeletedItalics()
	{
		return _deletedItalics;
	}

	public void setDeletedItalics(boolean deletedItalics)
	{
		_deletedItalics = deletedItalics;
	}

	public int getDeletedForegroundRGB()
	{
		return _deletedForegroundRGB;
	}

	public void setDeletedForegroundRGB(int deletedForegroundRGB)
	{
		_deletedForegroundRGB = deletedForegroundRGB;
	}

	public int getInsertBeginBackgroundRGB()
	{
		return _insertBeginBackgroundRGB;
	}

	public void setInsertBeginBackgroundRGB(int insertBeginBackgroundRGB)
	{
		_insertBeginBackgroundRGB = insertBeginBackgroundRGB;
	}

	public int getInsertEndBackgroundRGB()
	{
		return _insertEndBackgroundRGB;
	}

	public void setInsertEndBackgroundRGB(int insertEndBackgroundRGB)
	{
		_insertEndBackgroundRGB = insertEndBackgroundRGB;
	}

	public void setNotifyExternalFileChanges(boolean notifyExternalFileChanges)
	{
		_notifyExternalFileChanges = notifyExternalFileChanges;
	}

	public boolean isNotifyExternalFileChanges()
	{
		return _notifyExternalFileChanges;
	}

	public int getMessagePanelMessageForeground()
	{
		return _messagePanelMessageForeground;
	}

	public void setMessagePanelMessageForeground(int messagePanelMessageForeground)
	{
		_messagePanelMessageForeground = messagePanelMessageForeground;
	}

	public int getMessagePanelMessageBackground()
	{
		return _messagePanelMessageBackground;
	}

	public void setMessagePanelMessageBackground(int messagePanelMessageBackground)
	{
		_messagePanelMessageBackground = messagePanelMessageBackground;
	}

	public int getMessagePanelMessageHistoryForeground()
	{
		return _messagePanelMessageHistoryForeground;
	}

	public void setMessagePanelMessageHistoryForeground(int messagePanelMessageHistoryForeground)
	{
		_messagePanelMessageHistoryForeground = messagePanelMessageHistoryForeground;
	}

	public int getMessagePanelMessageHistoryBackground()
	{
		return _messagePanelMessageHistoryBackground;
	}

	public void setMessagePanelMessageHistoryBackground(int messagePanelMessageHistoryBackground)
	{
		_messagePanelMessageHistoryBackground = messagePanelMessageHistoryBackground;
	}

	public int getMessagePanelWarningForeground()
	{
		return _messagePanelWarningForeground;
	}

	public void setMessagePanelWarningForeground(int messagePanelWarningForeground)
	{
		_messagePanelWarningForeground = messagePanelWarningForeground;
	}

	public int getMessagePanelWarningBackground()
	{
		return _messagePanelWarningBackground;
	}

	public void setMessagePanelWarningBackground(int messagePanelWarningBackground)
	{
		_messagePanelWarningBackground = messagePanelWarningBackground;
	}

	public int getMessagePanelWarningHistoryForeground()
	{
		return _messagePanelWarningHistoryForeground;
	}

	public void setMessagePanelWarningHistoryForeground(int messagePanelWarningHistoryForeground)
	{
		_messagePanelWarningHistoryForeground = messagePanelWarningHistoryForeground;
	}

	public int getMessagePanelWarningHistoryBackground()
	{
		return _messagePanelWarningHistoryBackground;
	}

	public void setMessagePanelWarningHistoryBackground(int messagePanelWarningHistoryBackground)
	{
		_messagePanelWarningHistoryBackground = messagePanelWarningHistoryBackground;
	}

	public int getMessagePanelErrorForeground()
	{
		return _messagePanelErrorForeground;
	}

	public void setMessagePanelErrorForeground(int messagePanelErrorForeground)
	{
		_messagePanelErrorForeground = messagePanelErrorForeground;
	}

	public int getMessagePanelErrorBackground()
	{
		return _messagePanelErrorBackground;
	}

	public void setMessagePanelErrorBackground(int messagePanelErrorBackground)
	{
		_messagePanelErrorBackground = messagePanelErrorBackground;
	}

	public int getMessagePanelErrorHistoryForeground()
	{
		return _messagePanelErrorHistoryForeground;
	}

	public void setMessagePanelErrorHistoryForeground(int messagePanelErrorHistoryForeground)
	{
		_messagePanelErrorHistoryForeground = messagePanelErrorHistoryForeground;
	}

	public int getMessagePanelErrorHistoryBackground()
	{
		return _messagePanelErrorHistoryBackground;
	}

	public void setMessagePanelErrorHistoryBackground(int messagePanelErrorHistoryBackground)
	{
		_messagePanelErrorHistoryBackground = messagePanelErrorHistoryBackground;
	}

	public boolean isMessagePanelWhiteBackgroundAsUIDefault()
	{
		return _messagePanelWhiteBackgroundAsUIDefault;
	}

	public void setMessagePanelWhiteBackgroundAsUIDefault(boolean messagePanelWhiteBackgroundAsUIDefault)
	{
		_messagePanelWhiteBackgroundAsUIDefault = messagePanelWhiteBackgroundAsUIDefault;
	}

	public boolean isMessagePanelBlackForegroundAsUIDefault()
	{
		return _messagePanelBlackForegroundAsUIDefault;
	}

	public void setMessagePanelBlackForegroundAsUIDefault(boolean messagePanelBlackForegroundAsUIDefault)
	{
		_messagePanelBlackForegroundAsUIDefault = messagePanelBlackForegroundAsUIDefault;
	}

	public boolean getShowAliasPasswordCopyButton()
	{
		return _showAliasPasswordCopyButton;
	}

	public void setShowAliasPasswordCopyButton(boolean aliasPasswordCopyButton)
	{
		_showAliasPasswordCopyButton = aliasPasswordCopyButton;
	}

	public boolean getShowAliasPasswordShowButton()
	{
		return _showAliasPasswordShowButton;
	}

	public void setShowAliasPasswordShowButton(boolean aliasPasswordShowButton)
	{
		_showAliasPasswordShowButton = aliasPasswordShowButton;
	}

	public int getQueryTimeout()
	{
		return _queryTimeout;
	}

	public void setQueryTimeout(int queryTimeout)
	{
		_queryTimeout = queryTimeout;
	}
}
