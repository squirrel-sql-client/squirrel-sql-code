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
      ResultMetaDataTable globalDbTable = null;

      if(_updateableModel instanceof IDataSetUpdateableTableModel)
      {
         ITableInfo ti = ((IDataSetUpdateableTableModel)_updateableModel).getTableInfo();
         if (null != ti)
         {
            globalDbTable = new ResultMetaDataTable(ti.getCatalogName(), ti.getSchemaName(), ti.getSimpleName());
         }
      }


      ArrayList<InStatColumnInfo> inStatColumnInfos = new TableCopyInStatementCommand(_table, _session).getInStatColumnInfos();


      if (null == globalDbTable)
      {
         List<ResultMetaDataTable> tables = ShowReferencesUtil.findTables(inStatColumnInfos);

         if (0 == tables.size())
         {
            JOptionPane.showMessageDialog(_owningFrame, s_stringMgr.getString("ShowReferencesCommand.noTable"));
            return;
         }
         else if(1 < tables.size())
         {
            JOptionPane.showMessageDialog(_owningFrame, s_stringMgr.getString("ShowReferencesCommand.non.unique.table"));
            return;
         }


         globalDbTable = tables.get(0);
      }



      ReferencesFrameStarter.showReferences(new RootTable(globalDbTable, inStatColumnInfos), _session, _owningFrame);

   }

}
