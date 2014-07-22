package net.sourceforge.squirrel_sql.fw.gui.action;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.*;
import net.sourceforge.squirrel_sql.fw.gui.action.showreferences.*;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

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


      showReferences(new RootTable(globalDbTable, inStatColumnInfos));

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

   private void showReferences(RootTable rootTable)
   {
      try
      {
         ISession session = _updateableModel.getSession();

         DatabaseMetaData jdbcMetaData = session.getSQLConnection().getSQLMetaData().getJDBCMetaData();
         ResultSet primaryKeys = jdbcMetaData.getPrimaryKeys(rootTable.getGlobalDbTable().getCatalogName(), rootTable.getGlobalDbTable().getSchemaName(), rootTable.getGlobalDbTable().getTableName());

         if(false == primaryKeys.next())
         {
            JOptionPane.showMessageDialog(_owningFrame, s_stringMgr.getString("ShowReferencesCommand.noPrimaryKey", rootTable.getGlobalDbTable().getQualifiedName()));
            return;
         }

         References references = ShowReferencesUtil.getReferences(rootTable.getGlobalDbTable(), session);

         if(references.isEmpty())
         {
            JOptionPane.showMessageDialog(_owningFrame, s_stringMgr.getString("ShowReferencesCommand.noForeignKeyReferences", rootTable.getGlobalDbTable().getQualifiedName()));
            return;
         }


         new ShowReferencesCtrl(session, _owningFrame, rootTable, references);


      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

}
