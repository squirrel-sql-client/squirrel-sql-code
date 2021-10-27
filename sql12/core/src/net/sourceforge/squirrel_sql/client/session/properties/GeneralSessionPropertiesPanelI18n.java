package net.sourceforge.squirrel_sql.client.session.properties;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This interface defines locale specific strings. This should be
 * replaced with a property file.
 */
interface GeneralSessionPropertiesPanelI18n
{
   StringManager s_stringMgr = StringManagerFactory.getStringManager(GeneralSessionPropertiesPanelI18n.class);

   // i18n[generalSessionPropertiesPanel.generalSettings=General settings for the current session]
   String HINT = s_stringMgr.getString("generalSessionPropertiesPanel.generalSettings");
   // i18n[generalSessionPropertiesPanel.mainTabs=Main Tabs:]
   String MAIN_TAB_PLACEMENT = s_stringMgr.getString("generalSessionPropertiesPanel.mainTabs");
   // i18n[generalSessionPropertiesPanel.metaData=Meta Data:]
   String META_DATA = s_stringMgr.getString("generalSessionPropertiesPanel.metaData");
   // i18n[generalSessionPropertiesPanel.showToolbar=Show toolbar]
   String SHOW_TOOLBAR = s_stringMgr.getString("generalSessionPropertiesPanel.showToolbar");
   // i18n[generalSessionPropertiesPanel.objectTabs=Object Tabs:]
   String OBJECT_TAB_PLACEMENT = s_stringMgr.getString("generalSessionPropertiesPanel.objectTabs");
   // i18n[generalSessionPropertiesPanel.sqlExecTabs=SQL Execution Tabs:]
   String SQL_EXECUTION_TAB_PLACEMENT = s_stringMgr.getString("generalSessionPropertiesPanel.sqlExecTabs");
   // i18n[generalSessionPropertiesPanel.sqlResults=SQL Results:]
   String SQL_RESULTS = s_stringMgr.getString("generalSessionPropertiesPanel.sqlResults");
   // i18n[generalSessionPropertiesPanel.sqlResultTabs=SQL Results Tabs:]
   String SQL_RESULTS_TAB_PLACEMENT = s_stringMgr.getString("generalSessionPropertiesPanel.sqlResultTabs");
   // i18n[generalSessionPropertiesPanel.general=General]
   String TITLE = s_stringMgr.getString("generalSessionPropertiesPanel.general");
   // i18n[generalSessionPropertiesPanel.tableContents=Table Contents:]
   String TABLE_CONTENTS = s_stringMgr.getString("generalSessionPropertiesPanel.tableContents");

   // i18n[generalSessionPropertiesPanel.table=Table]
   String TABLE = s_stringMgr.getString("generalSessionPropertiesPanel.table");
   // i18n[generalSessionPropertiesPanel.editableTable=Editable Table]
   String EDITABLE_TABLE = s_stringMgr.getString("generalSessionPropertiesPanel.editableTable");
   // i18n[generalSessionPropertiesPanel.chkKeepTableLayoutOnRerun=Keep table layout on rerun SQL]
   String KEEP_TABLE_LAYOUT_ON_RERUN = s_stringMgr.getString("generalSessionPropertiesPanel.chkKeepTableLayoutOnRerun");

   String SHOW_ROW_NUMBERS_IN_TEXT_LAYOUT = s_stringMgr.getString("generalSessionPropertiesPanel.chkShowRowNumberInTextLayout");
   // i18n[generalSessionPropertiesPanel.text=Text]
   String TEXT = s_stringMgr.getString("generalSessionPropertiesPanel.text");

   // i18n[generalSessionPropertiesPanel.dataTYpe1=Properties for the individual Data Types may be set in the]
   String DATA_TYPE1 = s_stringMgr.getString("generalSessionPropertiesPanel.dataTYpe1");
   // i18n[generalSessionPropertiesPanel.dataTYpe2='General Preferences' window under the 'Data Type Controls' tab.]
   String DATA_TYPE2 = s_stringMgr.getString("generalSessionPropertiesPanel.dataTYpe2");

   String SQL_PANEL_ORIENTATION = s_stringMgr.getString("generalSessionPropertiesPanel.sqlPanelOrientation");
}
