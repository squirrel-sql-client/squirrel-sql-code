package net.sourceforge.squirrel_sql.fw.gui.action;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultMetaDataTable;
import net.sourceforge.squirrel_sql.fw.gui.action.showreferences.ReferencesFrameStarter;
import net.sourceforge.squirrel_sql.fw.gui.action.showreferences.RootTable;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ShowReferencesCommand
{
   static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ShowReferencesCommand.class);


   private final JTable _table;
   private final IDataSetUpdateableModel _updateableModel;
   private JFrame _owningFrame;
   private ISession _session;

   public ShowReferencesCommand(JTable table, IDataSetUpdateableModel updateableModel, JFrame owningFrame, ISession session)
   {
      _table = table;
      _updateableModel = updateableModel;
      _owningFrame = owningFrame;
      _session = session;
   }

   public void execute()
   {
      // Fallback for databases that don't implement ResultSetMetadata.getTableName().
      ResultMetaDataTable fallbackTable = tryGetFallbackTableFromUpdatableTableModel(_updateableModel);

      ArrayList<InStatColumnInfo> inStatColumnInfos = new TableCopyInStatementCommand(_table, _session).getInStatColumnInfos();
      List<ResultMetaDataTable> tablesOfSelectedCols = ShowReferencesUtil.findTables(inStatColumnInfos);

      if(null == fallbackTable && tablesOfSelectedCols.isEmpty())
      {
         JOptionPane.showMessageDialog(_owningFrame, s_stringMgr.getString("ShowReferencesCommand.noTable"));
         return;
      }

      if(1 < tablesOfSelectedCols.size())
      {
         JOptionPane.showMessageDialog(_owningFrame, s_stringMgr.getString("ShowReferencesCommand.non.unique.table"));
         return;
      }

      ResultMetaDataTable foreignKeysRefDialogRootTable = fallbackTable;
      if(false == tablesOfSelectedCols.isEmpty())
      {
         foreignKeysRefDialogRootTable = tablesOfSelectedCols.get(0);
      }

      ReferencesFrameStarter.showReferences(new RootTable(foreignKeysRefDialogRootTable, inStatColumnInfos), _session, _owningFrame);

   }

   private ResultMetaDataTable tryGetFallbackTableFromUpdatableTableModel(IDataSetUpdateableModel updateableModel)
   {
      ResultMetaDataTable ret = null;

      if(updateableModel instanceof IDataSetUpdateableTableModel)
      {
         ITableInfo ti = ((IDataSetUpdateableTableModel) updateableModel).getTableInfo();
         if (null != ti)
         {
            ret = new ResultMetaDataTable(ti.getCatalogName(), ti.getSchemaName(), ti.getSimpleName());
         }
      }
      return ret;
   }

}
