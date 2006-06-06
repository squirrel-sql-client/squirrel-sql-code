package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaProperties;
import net.sourceforge.squirrel_sql.client.gui.db.ConnectToAliasCallBack;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaDetailProperties;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Driver;
import java.sql.SQLException;

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


   public void initialize(SQLAlias alias, IApplication app)
   {
      _alias = alias;
      _app = app;
      _pnl = new SchemaPropertiesPanel();

      _schemaTableModel = new SchemaTableModel(alias.getSchemaProperties().getSchemaDetails());
      _pnl.tblSchemas.setModel(_schemaTableModel);

      TableColumnModel cm = _pnl.tblSchemas.getColumnModel();

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

      tc = new TableColumn(SchemaTableModel.IX_FUNCTION);
      // i18n[SchemaPropertiesController.tableHeader.functions=Functions]
      tc.setHeaderValue(s_stringMgr.getString("SchemaPropertiesController.tableHeader.functions"));
      tc.setCellEditor(new DefaultCellEditor(initCbo(_cboFunction)));
      cm.addColumn(tc);

      _pnl.radLoadAllAndCacheNone.setSelected(alias.getSchemaProperties().getGlobalState() == SQLAliasSchemaProperties.GLOBAL_STATE_LOAD_ALL_CACHE_NONE);
      _pnl.radLoadAndCacheAll.setSelected(alias.getSchemaProperties().getGlobalState() == SQLAliasSchemaProperties.GLOBAL_STATE_LOAD_AND_CACHE_ALL);
      _pnl.radSpecifySchemas.setSelected(alias.getSchemaProperties().getGlobalState() == SQLAliasSchemaProperties.GLOBAL_STATE_SPECIFY_SCHEMAS);
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
            onSpecifySchema();
         }
      });

      _pnl.btnUpdateSchemas.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onSpecifySchema();
         }
      });

   }

   private void onSpecifySchema()
   {
      if(0 == _alias.getSchemaProperties().getSchemaDetails().length)
      {
         IIdentifier drID = _alias.getDriverIdentifier();
         final Driver jdbcDriver = _app.getSQLDriverManager().getJDBCDriver(drID);
         if (jdbcDriver == null)
         {
            // I18n[DriverPropertiesController.loadingDriverFailed=Loading JDBC driver "{0}" failed.\nCan not load schemas.]
            String errMsg = s_stringMgr.getString("DriverPropertiesController.loadingDriverFailed", _app.getDataCache().getDriver(drID).getName());
            JOptionPane.showMessageDialog(_app.getMainFrame(), errMsg);
         }

         ConnectToAliasCallBack cb = new ConnectToAliasCallBack(_app, _alias)
         {
            public void connected(SQLConnection conn)
            {
               onConnected(conn);
            }
         };

         ConnectToAliasCommand cmd = new ConnectToAliasCommand(_app, _alias, false, cb);
         cmd.execute();
      }
   }


   private void onConnected(SQLConnection conn)
   {
      try
      {
         String[] schemas = conn.getSQLMetaData().getSchemas();

         SQLAliasSchemaDetailProperties[] details  = new SQLAliasSchemaDetailProperties[schemas.length];

         for (int i = 0; i < details.length; i++)
         {
            details[i] = new SQLAliasSchemaDetailProperties();
            details[i].setSchemaName(schemas[i]);
         }

         _schemaTableModel.updateSchemas(details);

         updateEnabled();
      }
      catch (SQLException e)
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
      //To change body of implemented methods use File | Settings | File Templates.
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
}
