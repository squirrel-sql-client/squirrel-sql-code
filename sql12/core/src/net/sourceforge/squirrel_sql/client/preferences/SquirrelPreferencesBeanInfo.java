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

import net.sourceforge.squirrel_sql.fw.util.Utilities;

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
      return new PropertyDescriptor[]
      {
            prop(SESSION_PROPERTIES, SquirrelPreferences.class, "getSessionProperties", "setSessionProperties"),
            prop(MAIN_FRAME_STATE, SquirrelPreferences.class, "getMainFrameWindowState", "setMainFrameWindowState"),
            prop(SHOW_CONTENTS_WHEN_DRAGGING, SquirrelPreferences.class, "getShowContentsWhenDragging", "setShowContentsWhenDragging"),

            prop(TABBED_STYLE, SquirrelPreferences.class, "getTabbedStyle", "setTabbedStyle"),
            prop(USE_SCROLLABLE_TABBED_PANES_FOR_SESSION_TABS, SquirrelPreferences.class, "getUseScrollableTabbedPanesForSessionTabs", "setUseScrollableTabbedPanesForSessionTabs"),
            prop(SHOW_TABBED_STYLE_HINT, SquirrelPreferences.class, "getShowTabbedStyleHint", "setShowTabbedStyleHint"),

            prop(LOGIN_TIMEOUT, SquirrelPreferences.class, "getLoginTimeout", "setLoginTimeout"),
            prop(LARGE_SCRIPT_STMT_COUNT, SquirrelPreferences.class, "getLargeScriptStmtCount", "setLargeScriptStmtCount"),

            prop(ALLOW_RUN_ALL_SQLS_IN_EDITOR, SquirrelPreferences.class, "isAllowRunAllSQLsInEditor", "setAllowRunAllSQLsInEditor"),

            prop(MARK_CURRENT_SQL, SquirrelPreferences.class, "isMarkCurrentSql", "setMarkCurrentSql"),
            prop(CURRENT_SQL_MARK_COLOR_RGB, SquirrelPreferences.class, "getCurrentSqlMarkColorRGB", "setCurrentSqlMarkColorRGB"),

            prop(USE_STATEMENT_SEPARATOR_AS_SQL_TO_EXECUTE_BOUNDS, SquirrelPreferences.class, "isUseStatementSeparatorAsSqlToExecuteBounds", "setUseStatementSeparatorAsSqlToExecuteBounds"),

            prop(JDBC_DEBUG_TYPE, SquirrelPreferences.class, "getJdbcDebugType", "setJdbcDebugType"),
            prop(SHOW_MAIN_STATUS_BAR, SquirrelPreferences.class, "getShowMainStatusBar", "setShowMainStatusBar"),
            prop(SHOW_MAIN_TOOL_BAR, SquirrelPreferences.class, "getShowMainToolBar", "setShowMainToolBar"),
            prop(SHOW_ALIASES_TOOL_BAR, SquirrelPreferences.class, "getShowAliasesToolBar", "setShowAliasesToolBar"),
            prop(SHOW_DRIVERS_TOOL_BAR, SquirrelPreferences.class, "getShowDriversToolBar", "setShowDriversToolBar"),
            prop(SHOW_TOOLTIPS, SquirrelPreferences.class, "getShowToolTips", "setShowToolTips"),
            prop(SCROLLABLE_TABBED_PANES, SquirrelPreferences.class, "getUseScrollableTabbedPanes", "setUseScrollableTabbedPanes"),
            ixProp(ACTION_KEYS, SquirrelPreferences.class, "getActionKeys", "setActionKeys", "getActionKeys", "setActionKeys"),
            prop(PROXY, SquirrelPreferences.class, "getProxySettings", "setProxySettings"),
            prop(SHOW_LOADED_DRIVERS_ONLY, SquirrelPreferences.class, "getShowLoadedDriversOnly", "setShowLoadedDriversOnly"),
            prop(MAXIMIMIZE_SESSION_SHEET_ON_OPEN, SquirrelPreferences.class, "getMaximizeSessionSheetOnOpen", "setMaximizeSessionSheetOnOpen"),
            prop(SHOW_COLOR_ICONS_IN_TOOLBAR, SquirrelPreferences.class, "getShowColoriconsInToolbar", "setShowColoriconsInToolbar"),
            prop(FIRST_RUN, SquirrelPreferences.class, "isFirstRun", "setFirstRun"),
            prop(CONFIRM_SESSION_CLOSE, SquirrelPreferences.class, "getConfirmSessionClose", "setConfirmSessionClose"),
            ixProp(PLUGIN_STATUSES, SquirrelPreferences.class, "getPluginStatuses", "setPluginStatuses", "getPluginStatus", "setPluginStatus"),
            prop(FILE_OPEN_IN_PREVIOUS_DIR, SquirrelPreferences.class, "isFileOpenInPreviousDir", "setFileOpenInPreviousDir"),
            prop(FILE_OPEN_IN_SPECIFIED_DIR, SquirrelPreferences.class, "isFileOpenInSpecifiedDir", "setFileOpenInSpecifiedDir"),
            prop(FILE_SPECIFIED_DIR, SquirrelPreferences.class, "getFileSpecifiedDir", "setFileSpecifiedDir"),
            prop(FILE_PREVIOUS_DIR, SquirrelPreferences.class, "getFilePreviousDir", "setFilePreviousDir"),
            prop(SHOW_PLUGIN_FILES_IN_SPLASH_SCREEN, SquirrelPreferences.class, "getShowPluginFilesInSplashScreen", "setShowPluginFilesInSplashScreen"),
            prop(USE_SHORT_SESSION_TITLE, SquirrelPreferences.class, "getUseShortSessionTitle", "setUseShortSessionTitle"),
            prop(WARN_JRE_JDBC_MISMATCH, SquirrelPreferences.class, "getWarnJreJdbcMismatch", "setWarnJreJdbcMismatch"),
            prop(WARN_FOR_UNSAVED_FILE_EDITS, SquirrelPreferences.class, "getWarnForUnsavedFileEdits", "setWarnForUnsavedFileEdits"),
            prop(WARN_FOR_UNSAVED_BUFFER_EDITS, SquirrelPreferences.class, "getWarnForUnsavedBufferEdits", "setWarnForUnsavedBufferEdits"),
            prop(SHOW_SESSION_STARTUP_TIME_HINT, SquirrelPreferences.class, "getShowSessionStartupTimeHint", "setShowSessionStartupTimeHint"),
            prop(SHOW_DEBUG_LOG_MESSAGES, SquirrelPreferences.class, "getShowDebugLogMessage", "setShowDebugLogMessages"),
            prop(SHOW_INFO_LOG_MESSAGES, SquirrelPreferences.class, "getShowInfoLogMessages", "setShowInfoLogMessages"),
            prop(SHOW_WARN_LOG_MESSAGES, SquirrelPreferences.class, "getShowWarnLogMessages", "setShowWarnLogMessages"),
            prop(SHOW_ERROR_LOG_MESSAGES, SquirrelPreferences.class, "getShowErrorLogMessages", "setShowErrorLogMessages"),

            prop(SAVE_PREFERENCES_IMMEDIATELY, SquirrelPreferences.class, "getSavePreferencesImmediately", "setSavePreferencesImmediately"),
            prop(SAVE_ALIASES_AND_DRIVERS_IMMEDIATELY, SquirrelPreferences.class, "getSaveAliasesAndDriversImmediately", "setSaveAliasesAndDriversImmediately"),

            prop(SELECT_ON_RIGHT_MOUSE_CLICK, SquirrelPreferences.class, "getSelectOnRightMouseClick", "setSelectOnRightMouseClick"),
            prop(SHOW_PLEASE_WAIT_DIALOG, SquirrelPreferences.class, "getShowPleaseWaitDialog", "setShowPleaseWaitDialog"),
            prop(PREFERRED_LOCALE, SquirrelPreferences.class, "getPreferredLocale", "setPreferredLocale"),

            prop(MAX_COLUMN_ADJUST_LENGTH_DEFINED, SquirrelPreferences.class, "getMaxColumnAdjustLengthDefined", "setMaxColumnAdjustLengthDefined"),

            prop(MAX_COLUMN_ADJUST_LENGTH, SquirrelPreferences.class, "getMaxColumnAdjustLength", "setMaxColumnAdjustLength"),

            prop(REMEMBER_VALUE_OF_POPUP, SquirrelPreferences.class, "isRememberValueOfPopup", "setRememberValueOfPopup"),

            prop(RELOAD_SQL_CONTENTS, SquirrelPreferences.class, "isReloadSqlContents", "setReloadSqlContents"),

            prop(MAX_TEXTOUTPUT_COLUMN_WIDTH, SquirrelPreferences.class, "getMaxTextOutputColumnWidth", "setMaxTextOutputColumnWidth"),

            prop(NOTIFY_EXTERNAL_FILE_CHANGES, SquirrelPreferences.class, "isNotifyExternalFileChanges", "setNotifyExternalFileChanges"),

            prop(ENABLE_CHANGE_TRACKING, SquirrelPreferences.class, "isEnableChangeTracking", "setEnableChangeTracking"),
            prop(GIT_COMMIT_MSG_MANUALLY, SquirrelPreferences.class, "isGitCommitMsgManually", "setGitCommitMsgManually"),
            prop(GIT_COMMIT_MSG_DEFAULT, SquirrelPreferences.class, "getGitCommitMsgDefault", "setGitCommitMsgDefault"),
            prop(DELETED_BOLD, SquirrelPreferences.class, "isDeletedBold", "setDeletedBold"),
            prop(DELETED_ITALICS, SquirrelPreferences.class, "isDeletedItalics", "setDeletedItalics"),
            prop(DELETED_FOREGROUND_RGB, SquirrelPreferences.class, "getDeletedForegroundRGB", "setDeletedForegroundRGB"),
            prop(INSERT_BEGIN_BACKGROUND_RGB, SquirrelPreferences.class, "getInsertBeginBackgroundRGB", "setInsertBeginBackgroundRGB"),
            prop(INSERT_END_BACKGROUND_RGB, SquirrelPreferences.class, "getInsertEndBackgroundRGB", "setInsertEndBackgroundRGB"),

            prop(MESSAGE_PANEL_MESSAGE_FOREGROUND, SquirrelPreferences.class, "getMessagePanelMessageForeground", "setMessagePanelMessageForeground"),
            prop(MESSAGE_PANEL_MESSAGE_BACKGROUND, SquirrelPreferences.class, "getMessagePanelMessageBackground", "setMessagePanelMessageBackground"),
            prop(MESSAGE_PANEL_MESSAGE_HISTORY_FOREGROUND, SquirrelPreferences.class, "getMessagePanelMessageHistoryForeground", "setMessagePanelMessageHistoryForeground"),
            prop(MESSAGE_PANEL_MESSAGE_HISTORY_BACKGROUND, SquirrelPreferences.class, "getMessagePanelMessageHistoryBackground", "setMessagePanelMessageHistoryBackground"),
            prop(MESSAGE_PANEL_WARNING_FOREGROUND, SquirrelPreferences.class, "getMessagePanelWarningForeground", "setMessagePanelWarningForeground"),
            prop(MESSAGE_PANEL_WARNING_BACKGROUND, SquirrelPreferences.class, "getMessagePanelWarningBackground", "setMessagePanelWarningBackground"),
            prop(MESSAGE_PANEL_WARNING_HISTORY_FOREGROUND, SquirrelPreferences.class, "getMessagePanelWarningHistoryForeground", "setMessagePanelWarningHistoryForeground"),
            prop(MESSAGE_PANEL_WARNING_HISTORY_BACKGROUND, SquirrelPreferences.class, "getMessagePanelWarningHistoryBackground", "setMessagePanelWarningHistoryBackground"),
            prop(MESSAGE_PANEL_ERROR_FOREGROUND, SquirrelPreferences.class, "getMessagePanelErrorForeground", "setMessagePanelErrorForeground"),
            prop(MESSAGE_PANEL_ERROR_BACKGROUND, SquirrelPreferences.class, "getMessagePanelErrorBackground", "setMessagePanelErrorBackground"),
            prop(MESSAGE_PANEL_ERROR_HISTORY_FOREGROUND, SquirrelPreferences.class, "getMessagePanelErrorHistoryForeground", "setMessagePanelErrorHistoryForeground"),
            prop(MESSAGE_PANEL_ERROR_HISTORY_BACKGROUND, SquirrelPreferences.class, "getMessagePanelErrorHistoryBackground", "setMessagePanelErrorHistoryBackground"),
            prop(MESSAGE_PANEL_WHITE_BACKGROUND_AS_UI_DEFAULT, SquirrelPreferences.class, "isMessagePanelWhiteBackgroundAsUIDefault", "setMessagePanelWhiteBackgroundAsUIDefault"),
            prop(MESSAGE_PANEL_BLACK_FOREGROUND_AS_UI_DEFAULT, SquirrelPreferences.class, "isMessagePanelBlackForegroundAsUIDefault", "setMessagePanelBlackForegroundAsUIDefault"),

            prop(SHOW_ALIAS_PASSWORD_COPY_BUTTON, SquirrelPreferences.class, "getShowAliasPasswordCopyButton", "setShowAliasPasswordCopyButton"),
            prop(SHOW_ALIAS_PASSWORD_SHOW_BUTTON, SquirrelPreferences.class, "getShowAliasPasswordShowButton", "setShowAliasPasswordShowButton"),

            prop(QUERY_TIMEOUT, SquirrelPreferences.class, "getQueryTimeout", "setQueryTimeout")
      };
   }

   private PropertyDescriptor prop(String propertyName, Class<?> beanClass, String readMethodName, String writeMethodName)
   {
      try
      {
         return new PropertyDescriptor(propertyName, beanClass, readMethodName, writeMethodName);
      }
      catch (IntrospectionException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   public IndexedPropertyDescriptor ixProp(String propertyName, Class<?> beanClass, String readMethodName, String writeMethodName,String indexedReadMethodName, String indexedWriteMethodName)
   {
      try
      {
         return new IndexedPropertyDescriptor(propertyName, beanClass, readMethodName, writeMethodName, indexedReadMethodName, indexedWriteMethodName);
      }
      catch (IntrospectionException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }


}
