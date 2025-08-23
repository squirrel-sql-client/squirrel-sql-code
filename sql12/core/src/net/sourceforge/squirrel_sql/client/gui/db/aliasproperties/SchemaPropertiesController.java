package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.ConnectToAliasCallBack;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaProperties;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfoCacheSerializer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.apache.commons.lang3.StringUtils;

public class SchemaPropertiesController implements IAliasPropertiesPanelController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SchemaPropertiesController.class);

   private static final ILogger s_log = LoggerController.createLogger(SchemaPropertiesController.class);

   private static final String PREF_KEY_LAST_SCHEMA_FILTER = "SchemaPropertiesController.last.schema.filter";


   private SchemaPropertiesPanel _pnl;

   private JComboBox _cboTables = new JComboBox();
   private JComboBox _cboView = new JComboBox();
   private JComboBox _cboFunction = new JComboBox();
   private JComboBox _cboUDT = new JComboBox();
   private Color _origTblColor;
   private SQLAlias _alias;
   private SchemaTableModel _schemaTableModel;
   private boolean _schemaTableWasCleared = false;


   public SchemaPropertiesController(SQLAlias alias)
   {
      _alias = alias;
      _pnl = new SchemaPropertiesPanel();

      _schemaTableModel = new SchemaTableModel(alias.getSchemaProperties().getSchemaDetails());
      _pnl.tblSchemas.setModel(_schemaTableModel);

      TableColumnModel cm = new DefaultTableColumnModel();

      TableColumn tc;
      tc = new TableColumn(SchemaTableModel.IX_SCHEMA_NAME);
      // i18n[SchemaPropertiesController.tableHeader.schema=Schema]
      tc.setHeaderValue(s_stringMgr.getString("SchemaPropertiesController.tableHeader.schema"));
      cm.addColumn(tc);

      tc = new TableColumn(SchemaTableModel.IX_TABLE);
      // i18n[SchemaPropertiesController.tableHeader.tables=Tables]
      tc.setHeaderValue(s_stringMgr.getString("SchemaPropertiesController.tableHeader.tables"));
      tc.setCellEditor(new DefaultCellEditor(initCbo(_cboTables)));
      cm.addColumn(tc);

      tc = new TableColumn(SchemaTableModel.IX_VIEW);
      // i18n[SchemaPropertiesController.tableHeader.views=Views]
      tc.setHeaderValue(s_stringMgr.getString("SchemaPropertiesController.tableHeader.views"));
      tc.setCellEditor(new DefaultCellEditor(initCbo(_cboView)));
      cm.addColumn(tc);

      tc = new TableColumn(SchemaTableModel.IX_PROCEDURE);
      // i18n[SchemaPropertiesController.tableHeader.procedures=Procedures]
      tc.setHeaderValue(s_stringMgr.getString("SchemaPropertiesController.tableHeader.procedures"));
      tc.setCellEditor(new DefaultCellEditor(initCbo(_cboFunction)));
      cm.addColumn(tc);

      tc = new TableColumn(SchemaTableModel.IX_UDT);
      // i18n[SchemaPropertiesController.tableHeader.procedures=Procedures]
      tc.setHeaderValue(s_stringMgr.getString("SchemaPropertiesController.tableHeader.udts"));
      tc.setCellEditor(new DefaultCellEditor(initCbo(_cboUDT)));
      cm.addColumn(tc);

      _pnl.tblSchemas.setColumnModel(cm);

      _pnl.radLoadAllAndCacheNone.setSelected(alias.getSchemaProperties().getGlobalState() == SQLAliasSchemaProperties.GLOBAL_STATE_LOAD_ALL_CACHE_NONE);
      _pnl.radLoadAndCacheAll.setSelected(alias.getSchemaProperties().getGlobalState() == SQLAliasSchemaProperties.GLOBAL_STATE_LOAD_AND_CACHE_ALL);

      _pnl.radSpecifySchemasByLikeString.setSelected(alias.getSchemaProperties().getGlobalState() == SQLAliasSchemaProperties.GLOBAL_STATE_SPECIFY_SCHEMAS_BY_LIKE_STRING);
      _pnl.txtSchemasByLikeStringInclude.setText(alias.getSchemaProperties().getByLikeStringInclude());
      _pnl.txtSchemasByLikeStringExclude.setText(alias.getSchemaProperties().getByLikeStringExclude());
      _pnl.chkLoadTables.setSelected(alias.getSchemaProperties().isLoadTables());
      _pnl.chkLoadViews.setSelected(alias.getSchemaProperties().isLoadViews());
      _pnl.chkLoadProcedures.setSelected(alias.getSchemaProperties().isLoadProcedures());
      _pnl.chkLoadUDTs.setSelected(alias.getSchemaProperties().isLoadUDTs());

      _pnl.radSpecifySchemas.setSelected(alias.getSchemaProperties().getGlobalState() == SQLAliasSchemaProperties.GLOBAL_STATE_SPECIFY_SCHEMAS);

      _pnl.chkCacheSchemaIndepndentMetaData.setSelected(alias.getSchemaProperties().isCacheSchemaIndependentMetaData());

      _pnl.txtSchemaFilter.setText(Main.getApplication().getPropsImpl().getString(PREF_KEY_LAST_SCHEMA_FILTER, null));

      _pnl.cboSchemaTableUpdateWhat.addItem(SchemaTableUpdateWhatItem.ALL);
      _pnl.cboSchemaTableUpdateWhat.addItem(SchemaTableUpdateWhatItem.TABLES);
      _pnl.cboSchemaTableUpdateWhat.addItem(SchemaTableUpdateWhatItem.VIEWS);
      _pnl.cboSchemaTableUpdateWhat.addItem(SchemaTableUpdateWhatItem.PROCEDURES);
      _pnl.cboSchemaTableUpdateWhat.addItem(SchemaTableUpdateWhatItem.UDTS);

      for (int i = 0; i < SchemaTableCboItem.items.length; i++)
      {
         _pnl.cboSchemaTableUpdateTo.addItem(SchemaTableCboItem.items[i]);
      }


      _pnl.btnSchemaTableUpdateApply.addActionListener(e -> onSchemaTableUpdateApply());

      updateEnabled();

      _pnl.radLoadAllAndCacheNone.addActionListener(e -> updateEnabled());
      _pnl.radLoadAndCacheAll.addActionListener(e -> updateEnabled());
      _pnl.radSpecifySchemasByLikeString.addActionListener(e -> updateEnabled());
      _pnl.radSpecifySchemas.addActionListener(e -> updateEnabled());

      _pnl.btnUpdateSchemasTable.addActionListener(e -> onRefreshSchemaTable());
      _pnl.btnClearSchemasTable.addActionListener(e -> onClearSchemaTable());

      _pnl.btnPrintCacheFileLocation.addActionListener(e -> onPrintCacheFileLocation());

      _pnl.btnDeleteCache.addActionListener(e -> onDeleteCache());

   }

   private void onDeleteCache()
   {
      SchemaInfoCacheSerializer.deleteCacheFile(Main.getApplication(), _alias, true);
   }

   private void onPrintCacheFileLocation()
   {
      File schemaCacheFile = SchemaInfoCacheSerializer.getSchemaCacheFile(_alias);

      String aliasName = null == _alias.getName() || 0 == _alias.getName().trim().length() ? "<unnamed>" : _alias.getName();
      String[] params = new String[]{aliasName, schemaCacheFile.getPath()};

      if(schemaCacheFile.exists())
      {
         // i18n[SchemaPropertiesController.cacheFilePath=Cache file path for Alias "{0}": {1}]
         Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("SchemaPropertiesController.cacheFilePath", params));
      }
      else
      {
         // i18n[SchemaPropertiesController.cacheFilePathNotExists=Cache file for Alias "{0}" does not exist. If it existed the path would be: {1}]
         Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("SchemaPropertiesController.cacheFilePathNotExists", params));
      }
   }

   private void onRefreshSchemaTable()
   {
      ConnectToAliasCallBack cb = new ConnectToAliasCallBack(_alias)
      {
         public void connected(ISQLConnection conn)
         {
            onConnected(conn);
         }
      };

      ConnectToAliasCommand cmd = new ConnectToAliasCommand(_alias, false, cb);
      cmd.executeConnect();
   }

   private void onSchemaTableUpdateApply()
   {
      TableCellEditor cellEditor = _pnl.tblSchemas.getCellEditor();
      if(null != cellEditor)
      {
         cellEditor.stopCellEditing();
      }


      SchemaTableUpdateWhatItem selWhatItem = (SchemaTableUpdateWhatItem) _pnl.cboSchemaTableUpdateWhat.getSelectedItem();

      SchemaTableCboItem selToItem = (SchemaTableCboItem) _pnl.cboSchemaTableUpdateTo.getSelectedItem();

      if(SchemaTableUpdateWhatItem.TABLES == selWhatItem)
      {
         _schemaTableModel.setColumnTo(SchemaTableModel.IX_TABLE, selToItem, _pnl.getSchemaFilter());
      }
      else if(SchemaTableUpdateWhatItem.VIEWS == selWhatItem)
      {
         _schemaTableModel.setColumnTo(SchemaTableModel.IX_VIEW, selToItem, _pnl.getSchemaFilter());
      }
      else if(SchemaTableUpdateWhatItem.PROCEDURES == selWhatItem)
      {
         _schemaTableModel.setColumnTo(SchemaTableModel.IX_PROCEDURE, selToItem, _pnl.getSchemaFilter());
      }
      else if(SchemaTableUpdateWhatItem.UDTS == selWhatItem)
      {
         _schemaTableModel.setColumnTo(SchemaTableModel.IX_UDT, selToItem, _pnl.getSchemaFilter());
      }
      else
      {
          _schemaTableModel.setColumnTo(SchemaTableModel.IX_TABLE, selToItem, _pnl.getSchemaFilter());
          _schemaTableModel.setColumnTo(SchemaTableModel.IX_VIEW, selToItem, _pnl.getSchemaFilter());
          _schemaTableModel.setColumnTo(SchemaTableModel.IX_PROCEDURE, selToItem, _pnl.getSchemaFilter());
          _schemaTableModel.setColumnTo(SchemaTableModel.IX_UDT, selToItem, _pnl.getSchemaFilter());
      }
   }



   /**
    * synchronized because the user may connect twice from within the Schema Properties Panel.
    */
   private synchronized void onConnected(ISQLConnection conn)
   {
      try
      {
         String[] schemas = Main.getApplication().getSessionManager().getAllowedSchemas(conn, _alias, null);

         _schemaTableModel.updateSchemas(schemas);
         _schemaTableWasCleared = false;

         updateEnabled();
      }
      catch (Exception e)
      {
         s_log.error("Failed to load Schemas", e);
      }
   }

   private void onClearSchemaTable()
   {
      TableCellEditor cellEditor = _pnl.tblSchemas.getCellEditor();
      if(null != cellEditor)
      {
         cellEditor.cancelCellEditing();
      }

      _schemaTableModel.clear();
      _schemaTableWasCleared = true;
   }

   private void updateEnabled()
   {
      if(null == _origTblColor)
      {
         _origTblColor = _pnl.tblSchemas.getForeground();
      }


      _pnl.btnUpdateSchemasTable.setEnabled(_pnl.radSpecifySchemas.isSelected());

      _pnl.tblSchemas.setEnabled(_pnl.radSpecifySchemas.isSelected());

      _pnl.cboSchemaTableUpdateWhat.setEnabled(_pnl.radSpecifySchemas.isSelected());
      //_pnl.btnShowSchemaFilter.setEnabled(_pnl.radSpecifySchemas.isSelected());
      _pnl.txtSchemaFilter.setEnabled(_pnl.radSpecifySchemas.isSelected());
      _pnl.cboSchemaTableUpdateTo.setEnabled(_pnl.radSpecifySchemas.isSelected());
      _pnl.btnSchemaTableUpdateApply.setEnabled(_pnl.radSpecifySchemas.isSelected());

      _pnl.txtSchemasByLikeStringInclude.setEnabled(_pnl.radSpecifySchemasByLikeString.isSelected());
      _pnl.txtSchemasByLikeStringExclude.setEnabled(_pnl.radSpecifySchemasByLikeString.isSelected());

      _pnl.chkLoadTables.setEnabled(_pnl.radSpecifySchemasByLikeString.isSelected());
      _pnl.chkLoadViews.setEnabled(_pnl.radSpecifySchemasByLikeString.isSelected());
      _pnl.chkLoadProcedures.setEnabled(_pnl.radSpecifySchemasByLikeString.isSelected());
      _pnl.chkLoadUDTs.setEnabled(_pnl.radSpecifySchemasByLikeString.isSelected());
      _pnl.lblNamesInclude.setEnabled(_pnl.radSpecifySchemasByLikeString.isSelected());
      _pnl.lblNamesExclude.setEnabled(_pnl.radSpecifySchemasByLikeString.isSelected());
      _pnl.lblTypesToLoad.setEnabled(_pnl.radSpecifySchemasByLikeString.isSelected());

      if(_pnl.radSpecifySchemas.isSelected())
      {
         _pnl.tblSchemas.setForeground(_origTblColor);
      }
      else
      {
         _pnl.tblSchemas.setForeground(Color.lightGray);
      }
   }


   private JComboBox initCbo(JComboBox cbo)
   {
      cbo.setEditable(false);

      for (int i = 0; i < SchemaTableCboItem.items.length; i++)
      {
         cbo.addItem(SchemaTableCboItem.items[i]);
      }
      cbo.setSelectedIndex(0);
      return cbo;
   }




   public void applyChanges()
   {

      TableCellEditor cellEditor = _pnl.tblSchemas.getCellEditor();
      if(null != cellEditor)
      {
         cellEditor.stopCellEditing();
      }


      if(_pnl.radLoadAllAndCacheNone.isSelected())
      {
         _alias.getSchemaProperties().setGlobalState(SQLAliasSchemaProperties.GLOBAL_STATE_LOAD_ALL_CACHE_NONE);
      }
      else if(_pnl.radLoadAndCacheAll.isSelected())
      {
         _alias.getSchemaProperties().setGlobalState(SQLAliasSchemaProperties.GLOBAL_STATE_LOAD_AND_CACHE_ALL);
      }
      else if(_pnl.radSpecifySchemasByLikeString.isSelected())
      {
         _alias.getSchemaProperties().setGlobalState(SQLAliasSchemaProperties.GLOBAL_STATE_SPECIFY_SCHEMAS_BY_LIKE_STRING);
      }
      else if(_pnl.radSpecifySchemas.isSelected())
      {
         _alias.getSchemaProperties().setGlobalState(SQLAliasSchemaProperties.GLOBAL_STATE_SPECIFY_SCHEMAS);
      }

      _alias.getSchemaProperties().setByLikeStringInclude(StringUtils.stripToNull(_pnl.txtSchemasByLikeStringInclude.getText()));
      _alias.getSchemaProperties().setByLikeStringExclude(StringUtils.stripToNull(_pnl.txtSchemasByLikeStringExclude.getText()));
      _alias.getSchemaProperties().setLoadTables(_pnl.chkLoadTables.isSelected());
      _alias.getSchemaProperties().setLoadViews(_pnl.chkLoadViews.isSelected());
      _alias.getSchemaProperties().setLoadProcedures(_pnl.chkLoadProcedures.isSelected());
      _alias.getSchemaProperties().setLoadUDTs(_pnl.chkLoadUDTs.isSelected());

      _alias.getSchemaProperties().setSchemaDetails(_schemaTableModel.getData());

      _alias.getSchemaProperties().setSchemaTableWasCleared_transientForMultiAliasModificationOnly(_schemaTableWasCleared);

      _alias.getSchemaProperties().setCacheSchemaIndependentMetaData(_pnl.chkCacheSchemaIndepndentMetaData.isSelected());

      Main.getApplication().getPropsImpl().put(PREF_KEY_LAST_SCHEMA_FILTER, _pnl.txtSchemaFilter.getText());
   }

   public String getTitle()
   {
      // i18n[SchemaPropertiesController.title=Schemas]
      return s_stringMgr.getString("SchemaPropertiesController.title");
   }

   public String getHint()
   {
      // i18n[SchemaPropertiesController.hint=Schemas (loading and caching)]
      return s_stringMgr.getString("SchemaPropertiesController.hint");
   }

   public Component getPanelComponent()
   {
      return _pnl;
   }

   private static class SchemaTableUpdateWhatItem
   {
      // i18n[SchemaTableUpdateWhatItem.tables=Tables]
      public static final SchemaTableUpdateWhatItem TABLES = new SchemaTableUpdateWhatItem(s_stringMgr.getString("SchemaTableUpdateWhatItem.tables"));
      // i18n[SchemaTableUpdateWhatItem.views=Views]
      public static final SchemaTableUpdateWhatItem VIEWS = new SchemaTableUpdateWhatItem(s_stringMgr.getString("SchemaTableUpdateWhatItem.views"));
      // i18n[SchemaTableUpdateWhatItem.procedures=Procedures]
      public static final SchemaTableUpdateWhatItem PROCEDURES = new SchemaTableUpdateWhatItem(s_stringMgr.getString("SchemaTableUpdateWhatItem.procedures"));
      // i18n[SchemaTableUpdateWhatItem.procedures=Procedures]
      public static final SchemaTableUpdateWhatItem UDTS = new SchemaTableUpdateWhatItem(s_stringMgr.getString("SchemaTableUpdateWhatItem.udts"));
      // i18n[SchemaTableUpdateWhatItem.allObjects=All Objects]
      public static final SchemaTableUpdateWhatItem ALL = new SchemaTableUpdateWhatItem(s_stringMgr.getString("SchemaTableUpdateWhatItem.allObjects"));
      
      private String _name;

      private SchemaTableUpdateWhatItem(String name)
      {
         _name = name;
      }

      public String toString()
      {
         return _name;
      }
   }


}
