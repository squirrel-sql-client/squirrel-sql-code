package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfoCacheSerializer;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaProperties;
import net.sourceforge.squirrel_sql.client.gui.db.ConnectToAliasCallBack;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.DefaultTableColumnModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class SchemaPropertiesController implements IAliasPropertiesPanelController
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SchemaPropertiesController.class);

   private static final ILogger s_log =
      LoggerController.createLogger(SchemaPropertiesController.class);



   private SchemaPropertiesPanel _pnl;

   private JComboBox _cboTables = new JComboBox();
   private JComboBox _cboView = new JComboBox();
   private JComboBox _cboFunction = new JComboBox();
   private Color _origTblColor;
   private SQLAlias _alias;
   private IApplication _app;
   private SchemaTableModel _schemaTableModel;


   public SchemaPropertiesController(SQLAlias alias, IApplication app)
   {
      _alias = alias;
      _app = app;
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

      _pnl.tblSchemas.setColumnModel(cm);

      _pnl.radLoadAllAndCacheNone.setSelected(alias.getSchemaProperties().getGlobalState() == SQLAliasSchemaProperties.GLOBAL_STATE_LOAD_ALL_CACHE_NONE);
      _pnl.radLoadAndCacheAll.setSelected(alias.getSchemaProperties().getGlobalState() == SQLAliasSchemaProperties.GLOBAL_STATE_LOAD_AND_CACHE_ALL);
      _pnl.radSpecifySchemas.setSelected(alias.getSchemaProperties().getGlobalState() == SQLAliasSchemaProperties.GLOBAL_STATE_SPECIFY_SCHEMAS);

      _pnl.chkCacheSchemaIndepndentMetaData.setSelected(alias.getSchemaProperties().isCacheSchemaIndependentMetaData());

      _pnl.cboSchemaTableUpdateWhat.addItem(SchemaTableUpdateWhatItem.ALL);
      _pnl.cboSchemaTableUpdateWhat.addItem(SchemaTableUpdateWhatItem.TABLES);
      _pnl.cboSchemaTableUpdateWhat.addItem(SchemaTableUpdateWhatItem.VIEWS);
      _pnl.cboSchemaTableUpdateWhat.addItem(SchemaTableUpdateWhatItem.PROCEDURES);

      for (int i = 0; i < SchemaTableCboItem.items.length; i++)
      {
         _pnl.cboSchemaTableUpdateTo.addItem(SchemaTableCboItem.items[i]);
      }


      _pnl.btnSchemaTableUpdateApply.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onSchemaTableUpdateApply();
         }
      });

      updateEnabled();

      _pnl.radLoadAllAndCacheNone.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            updateEnabled();
         }
      });
      _pnl.radLoadAndCacheAll.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            updateEnabled();
         }
      });
      _pnl.radSpecifySchemas.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            updateEnabled();
         }
      });

      _pnl.btnUpdateSchemas.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRefreshSchemaTable();
         }
      });

      _pnl.btnPrintCacheFileLocation.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onPrintCacheFileLocation();
         }
      });

      _pnl.btnDeleteCache.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onDeleteCache();
         }
      });

   }

   private void onDeleteCache()
   {
      SchemaInfoCacheSerializer.deleteCacheFile(_app, _alias, true);
   }

   private void onPrintCacheFileLocation()
   {
      File schemaCacheFile = SchemaInfoCacheSerializer.getSchemaCacheFile(_alias);

      String aliasName = null == _alias.getName() || 0 == _alias.getName().trim().length() ? "<unnamed>" : _alias.getName();
      String[] params = new String[]{aliasName, schemaCacheFile.getPath()};

      if(schemaCacheFile.exists())
      {
         // i18n[SchemaPropertiesController.cacheFilePath=Cache file path for Alias "{0}": {1}]
         _app.getMessageHandler().showMessage(s_stringMgr.getString("SchemaPropertiesController.cacheFilePath", params));
      }
      else
      {
         // i18n[SchemaPropertiesController.cacheFilePathNotExists=Cache file for Alias "{0}" does not exist. If it existed the path would be: {1}]
         _app.getMessageHandler().showMessage(s_stringMgr.getString("SchemaPropertiesController.cacheFilePathNotExists", params));
      }
   }

   private void onRefreshSchemaTable()
   {
      ConnectToAliasCallBack cb = new ConnectToAliasCallBack(_app, _alias)
      {
         public void connected(ISQLConnection conn)
         {
            onConnected(conn);
         }
      };

      ConnectToAliasCommand cmd = new ConnectToAliasCommand(_app, _alias, false, cb);
      cmd.execute();
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
         _schemaTableModel.setColumnTo(SchemaTableModel.IX_TABLE, selToItem);
      }
      else if(SchemaTableUpdateWhatItem.VIEWS == selWhatItem)
      {
         _schemaTableModel.setColumnTo(SchemaTableModel.IX_VIEW, selToItem);
      }
      else if(SchemaTableUpdateWhatItem.PROCEDURES == selWhatItem)
      {
         _schemaTableModel.setColumnTo(SchemaTableModel.IX_PROCEDURE, selToItem);
      } else {
          _schemaTableModel.setColumnTo(SchemaTableModel.IX_TABLE, selToItem);
          _schemaTableModel.setColumnTo(SchemaTableModel.IX_VIEW, selToItem);
          _schemaTableModel.setColumnTo(SchemaTableModel.IX_PROCEDURE, selToItem);
      }
   }



   /**
    * synchronized because the user may connect twice from within the Schema Properties Panel.
    * @param conn
    */
   private synchronized void onConnected(ISQLConnection conn)
   {
      try
      {
         String[] schemas = _app.getSessionManager().getAllowedSchemas(conn, _alias);

         _schemaTableModel.updateSchemas(schemas);

         updateEnabled();
      }
      catch (Exception e)
      {
         s_log.error("Failed to load Schemas", e);
      }
   }


   private void updateEnabled()
   {
      if(null == _origTblColor)
      {
         _origTblColor = _pnl.tblSchemas.getForeground();
      }


      _pnl.btnUpdateSchemas.setEnabled(_pnl.radSpecifySchemas.isSelected());

      _pnl.tblSchemas.setEnabled(_pnl.radSpecifySchemas.isSelected());

      _pnl.cboSchemaTableUpdateWhat.setEnabled(_pnl.radSpecifySchemas.isSelected());
      _pnl.cboSchemaTableUpdateTo.setEnabled(_pnl.radSpecifySchemas.isSelected());
      _pnl.btnSchemaTableUpdateApply.setEnabled(_pnl.radSpecifySchemas.isSelected());

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
      else if(_pnl.radSpecifySchemas.isSelected())
      {
         _alias.getSchemaProperties().setGlobalState(SQLAliasSchemaProperties.GLOBAL_STATE_SPECIFY_SCHEMAS);
      }

      _alias.getSchemaProperties().setSchemaDetails(_schemaTableModel.getData());

      _alias.getSchemaProperties().setCacheSchemaIndependentMetaData(_pnl.chkCacheSchemaIndepndentMetaData.isSelected());

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
