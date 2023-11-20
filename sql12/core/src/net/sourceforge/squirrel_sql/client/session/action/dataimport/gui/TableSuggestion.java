package net.sourceforge.squirrel_sql.client.session.action.dataimport.gui;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.dataimport.EDTMessageBoxUtil;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.*;

public class TableSuggestion
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TableSuggestion.class);


   private String[][] _previewData;
   private boolean _headerIncluded;
   private ISession _session;

   private TableCreatedListener _tableCreatedListener;

   public TableSuggestion(ISession session, TableCreatedListener tableCreatedListener)
   {
      _session = session;
      _tableCreatedListener = tableCreatedListener;
   }


   public void showTableDialog(Window owningWindow, String tableName)
   {
      ImportTableDetailsCtrl importTableDetailsCtrl = new ImportTableDetailsCtrl(owningWindow, _session, _previewData, tableName, _headerIncluded);
      importTableDetailsCtrl.showDialog(); // modal

      if(importTableDetailsCtrl.isCreateTable())
      {
         _execCreateTableInDatabase(importTableDetailsCtrl.getCreateSql());
      }
   }


   public void execCreateTableInDatabase(String tableName)
   {
      _execCreateTableInDatabase(TableCreateUtils.suggestCreateScript(tableName, _session, _previewData, _headerIncluded));
   }

   private void _execCreateTableInDatabase(String createSql)
   {
      CreateTableInDatabaseResult createTableInDatabaseResult = TableCreateUtils.execCreateTableInDatabase(_session, createSql);

      if (null != createTableInDatabaseResult.getCreatedTable())
      {
         Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("TableSuggestion.created.table", createTableInDatabaseResult.getCreatedTable().getQualifiedName()));
         _tableCreatedListener.tableCreated(createTableInDatabaseResult.getCreatedTable());
      }
      else if(createTableInDatabaseResult.isErrorOccured())
      {
         String msg = s_stringMgr.getString("TableSuggestion.error.on.create.table.msg");
         String title = s_stringMgr.getString("TableSuggestion.error.on.create.table.title");
         EDTMessageBoxUtil.showMessageDialogOnEDT(msg, title);
      }
      else if(createTableInDatabaseResult.isTableNotFound())
      {
         String msg = s_stringMgr.getString("TableSuggestion.created.table.not.found.msg");
         String title = s_stringMgr.getString("TableSuggestion.created.table.not.found.title");
         EDTMessageBoxUtil.showMessageDialogOnEDT(msg, title);
      }
      else
      {
         throw new IllegalStateException("Failed to interpret table creation");
      }
   }

   public void updatePreviewData(String[][] previewData, boolean headerIncluded)
   {
      _previewData = previewData;
      _headerIncluded = headerIncluded;
   }

   public void clear()
   {
   }
}
