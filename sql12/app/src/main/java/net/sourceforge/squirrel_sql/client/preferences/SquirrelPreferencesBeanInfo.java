package net.sourceforge.squirrel_sql.client.preferences;

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

import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * This is the <CODE>BeanInfo</CODE> class for <CODE>SquirrelPreferences</CODE>.
 * 
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SquirrelPreferencesBeanInfo extends SimpleBeanInfo implements SquirrelPreferences.IPropertyNames
{

	/**
	 * See http://tinyurl.com/63no6t for discussion of the proper thread-safe way to implement
	 * getPropertyDescriptors().
	 * 
	 * @see java.beans.SimpleBeanInfo#getPropertyDescriptors()
	 */
	@Override	
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			PropertyDescriptor[] result =
				new PropertyDescriptor[] {
						new PropertyDescriptor(SESSION_PROPERTIES, SquirrelPreferences.class,
							"getSessionProperties", "setSessionProperties"),
						new PropertyDescriptor(MAIN_FRAME_STATE, SquirrelPreferences.class,
							"getMainFrameWindowState", "setMainFrameWindowState"),
						new PropertyDescriptor(SHOW_CONTENTS_WHEN_DRAGGING, SquirrelPreferences.class,
							"getShowContentsWhenDragging", "setShowContentsWhenDragging"),

						new PropertyDescriptor(TABBED_STYLE, SquirrelPreferences.class,
							"getTabbedStyle", "setTabbedStyle"),
						new PropertyDescriptor(USE_SCROLLABLE_TABBED_PANES_FOR_SESSION_TABS, SquirrelPreferences.class,
							"getUseScrollableTabbedPanesForSessionTabs", "setUseScrollableTabbedPanesForSessionTabs"),
						new PropertyDescriptor(SHOW_TABBED_STYLE_HINT, SquirrelPreferences.class,
							"getShowTabbedStyleHint", "setShowTabbedStyleHint"),

						new PropertyDescriptor(LOGIN_TIMEOUT, SquirrelPreferences.class, "getLoginTimeout",
							"setLoginTimeout"),
						new PropertyDescriptor(LARGE_SCRIPT_STMT_COUNT, SquirrelPreferences.class,
							"getLargeScriptStmtCount", "setLargeScriptStmtCount"),
						new PropertyDescriptor(JDBC_DEBUG_TYPE, SquirrelPreferences.class, "getJdbcDebugType",
							"setJdbcDebugType"),
						new PropertyDescriptor(SHOW_MAIN_STATUS_BAR, SquirrelPreferences.class,
							"getShowMainStatusBar", "setShowMainStatusBar"),
						new PropertyDescriptor(SHOW_MAIN_TOOL_BAR, SquirrelPreferences.class, "getShowMainToolBar",
							"setShowMainToolBar"),
						new PropertyDescriptor(SHOW_ALIASES_TOOL_BAR, SquirrelPreferences.class,
							"getShowAliasesToolBar", "setShowAliasesToolBar"),
						new PropertyDescriptor(SHOW_DRIVERS_TOOL_BAR, SquirrelPreferences.class,
							"getShowDriversToolBar", "setShowDriversToolBar"),
						new PropertyDescriptor(SHOW_TOOLTIPS, SquirrelPreferences.class, "getShowToolTips",
							"setShowToolTips"),
						new PropertyDescriptor(SCROLLABLE_TABBED_PANES, SquirrelPreferences.class,
							"getUseScrollableTabbedPanes", "setUseScrollableTabbedPanes"),
						new IndexedPropertyDescriptor(ACTION_KEYS, SquirrelPreferences.class, "getActionKeys",
							"setActionKeys", "getActionKeys", "setActionKeys"),
						new PropertyDescriptor(PROXY, SquirrelPreferences.class, "getProxySettings",
							"setProxySettings"),
						new PropertyDescriptor(UPDATE, SquirrelPreferences.class, "getUpdateSettings",
							"setUpdateSettings"),
						new PropertyDescriptor(SHOW_LOADED_DRIVERS_ONLY, SquirrelPreferences.class,
							"getShowLoadedDriversOnly", "setShowLoadedDriversOnly"),
						new PropertyDescriptor(MAXIMIMIZE_SESSION_SHEET_ON_OPEN, SquirrelPreferences.class,
							"getMaximizeSessionSheetOnOpen", "setMaximizeSessionSheetOnOpen"),
						new PropertyDescriptor(SHOW_COLOR_ICONS_IN_TOOLBAR, SquirrelPreferences.class,
							"getShowColoriconsInToolbar", "setShowColoriconsInToolbar"),
						new PropertyDescriptor(FIRST_RUN, SquirrelPreferences.class, "isFirstRun", "setFirstRun"),
						new PropertyDescriptor(CONFIRM_SESSION_CLOSE, SquirrelPreferences.class,
							"getConfirmSessionClose", "setConfirmSessionClose"),
						new IndexedPropertyDescriptor(PLUGIN_STATUSES, SquirrelPreferences.class,
							"getPluginStatuses", "setPluginStatuses", "getPluginStatus", "setPluginStatus"),
						new PropertyDescriptor(NEW_SESSION_VIEW, SquirrelPreferences.class, "getNewSessionView",
							"setNewSessionView"),
						new PropertyDescriptor(FILE_OPEN_IN_PREVIOUS_DIR, SquirrelPreferences.class,
							"isFileOpenInPreviousDir", "setFileOpenInPreviousDir"),
						new PropertyDescriptor(FILE_OPEN_IN_SPECIFIED_DIR, SquirrelPreferences.class,
							"isFileOpenInSpecifiedDir", "setFileOpenInSpecifiedDir"),
						new PropertyDescriptor(FILE_SPECIFIED_DIR, SquirrelPreferences.class,
							"getFileSpecifiedDir", "setFileSpecifiedDir"),
						new PropertyDescriptor(FILE_PREVIOUS_DIR, SquirrelPreferences.class, "getFilePreviousDir",
							"setFilePreviousDir"),
						new PropertyDescriptor(SHOW_PLUGIN_FILES_IN_SPLASH_SCREEN, SquirrelPreferences.class,
							"getShowPluginFilesInSplashScreen", "setShowPluginFilesInSplashScreen"),
						new PropertyDescriptor(WARN_JRE_JDBC_MISMATCH, SquirrelPreferences.class,
							"getWarnJreJdbcMismatch", "setWarnJreJdbcMismatch"),
						new PropertyDescriptor(WARN_FOR_UNSAVED_FILE_EDITS, SquirrelPreferences.class,
							"getWarnForUnsavedFileEdits", "setWarnForUnsavedFileEdits"),
						new PropertyDescriptor(WARN_FOR_UNSAVED_BUFFER_EDITS, SquirrelPreferences.class,
							"getWarnForUnsavedBufferEdits", "setWarnForUnsavedBufferEdits"),
						new PropertyDescriptor(SHOW_SESSION_STARTUP_TIME_HINT, SquirrelPreferences.class,
							"getShowSessionStartupTimeHint", "setShowSessionStartupTimeHint"),
						new PropertyDescriptor(SHOW_DEBUG_LOG_MESSAGES, SquirrelPreferences.class,
							"getShowDebugLogMessage", "setShowDebugLogMessages"),
						new PropertyDescriptor(SHOW_INFO_LOG_MESSAGES, SquirrelPreferences.class,
							"getShowInfoLogMessages", "setShowInfoLogMessages"),
						new PropertyDescriptor(SHOW_ERROR_LOG_MESSAGES, SquirrelPreferences.class,
							"getShowErrorLogMessages", "setShowErrorLogMessages"),
						new PropertyDescriptor(SAVE_PREFERENCES_IMMEDIATELY, SquirrelPreferences.class,
							"getSavePreferencesImmediately", "setSavePreferencesImmediately"),
						new PropertyDescriptor(SELECT_ON_RIGHT_MOUSE_CLICK, SquirrelPreferences.class,
							"getSelectOnRightMouseClick", "setSelectOnRightMouseClick"),
						new PropertyDescriptor(SHOW_PLEASE_WAIT_DIALOG, SquirrelPreferences.class,
							"getShowPleaseWaitDialog", "setShowPleaseWaitDialog"),
						new PropertyDescriptor(PREFERRED_LOCALE, SquirrelPreferences.class, "getPreferredLocale",
							"setPreferredLocale"), };

			return result;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
	}
}
