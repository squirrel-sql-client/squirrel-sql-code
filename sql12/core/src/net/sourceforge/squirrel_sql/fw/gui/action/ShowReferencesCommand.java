package net.sourceforge.squirrel_sql.fw.gui.action;

import net.sourceforge.squirrel_sql.fw.datasetviewer.*;
import net.sourceforge.squirrel_sql.fw.gui.action.showreferences.*;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.util.ArrayList;

public class ShowReferencesCommand
{
   static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ShowReferencesCommand.class);


   private final JTable _table;
   private final IDataSetUpdateableModel _updateableModel;
   private JFrame _owningFrame;

   public ShowReferencesCommand(JTable table, IDataSetUpdateableModel updateableModel, JFrame owningFrame)
   {
      _table = table;
      _updateableModel = updateableModel;
      _owningFrame = owningFrame;
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


      ArrayList<InStatColumnInfo> inStatColumnInfos = new TableCopyInStatementCommand(_table).getInStatColumnInfos();


      if (null == globalDbTable)
      {
         ResultMetaDataTable buf = findTable(inStatColumnInfos);

         if(null != buf)
         {
            globalDbTable = buf;
         }
      }

      if (null == globalDbTable)
      {
         JOptionPane.showMessageDialog(_owningFrame, s_stringMgr.getString("ShowReferencesCommand.noTable"));
         return;
      }


      ReferencesFrameStarter.showReferences(new RootTable(globalDbTable, inStatColumnInfos), _updateableModel.getSession(), _owningFrame);

   }

   private ResultMetaDataTable findTable(ArrayList<InStatColumnInfo> inStatColumnInfos)
   {
      ResultMetaDataTable ret = null;
      for (InStatColumnInfo inStatColumnInfo : inStatColumnInfos)
      {
         ResultMetaDataTable buf = inStatColumnInfo.getColDef().getResultMetaDataTable();
         if (null == ret)
         {
            ret = buf;
         }
         else if (false == ret.getQualifiedName().equalsIgnoreCase(buf.getQualifiedName()))
         {
            return null;
         }
      }

      return ret;
   }

}
