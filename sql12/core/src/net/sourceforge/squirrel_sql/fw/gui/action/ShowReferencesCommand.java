package net.sourceforge.squirrel_sql.fw.gui.action;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.*;
import net.sourceforge.squirrel_sql.fw.gui.action.showreferences.ExportedKey;
import net.sourceforge.squirrel_sql.fw.gui.action.showreferences.ShowReferencesCtrl;
import net.sourceforge.squirrel_sql.fw.gui.action.showreferences.ShowReferencesUtil;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
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

      if(1 == inStatColumnInfos.size())
      {
         InStatColumnInfo singleInStatColumnInfo = inStatColumnInfos.get(0);

         if(null == globalDbTable)
         {
            if(null != singleInStatColumnInfo.getColDef() && null != singleInStatColumnInfo.getColDef().getResultMetaDataTable())
            {
               globalDbTable = singleInStatColumnInfo.getColDef().getResultMetaDataTable();
            }
         }

         if(null == globalDbTable)
         {
            JOptionPane.showMessageDialog(_owningFrame, s_stringMgr.getString("ShowReferencesCommand.noTable"));
            return;
         }


         showReferences(globalDbTable, singleInStatColumnInfo.getColDef(), singleInStatColumnInfo.getInstat().toString());

      }
      else if(1 < inStatColumnInfos.size())
      {
         JOptionPane.showMessageDialog(_owningFrame, s_stringMgr.getString("ShowReferencesCommand.compoundNotSupported"));

//         WhereStatColumnInfo whereStatColumnInfo = new TableCopyWhereStatementCommand(_table).getWhereStatColumnInfos();
//
//         if(null == globalDbTable)
//         {
//            globalDbTable = whereStatColumnInfo.getDistinctTable();
//         }
//
//         if(null == globalDbTable)
//         {
//            JOptionPane.showMessageDialog(_owningFrame, "ShowReferencesCommand.noTable");
//            return;
//         }
//
//         String select =
//               "SELECT * FROM " + globalDbTable.getQualifiedName() + " " +
//
//         showReferences(globalDbTable, singleInStatColumnInfo.getColDef(), select);
//
      }
   }

   private void showReferences(ResultMetaDataTable globalDbTable, ColumnDisplayDefinition colDef, String inStat)
   {
      try
      {
         ISession session = _updateableModel.getSession();

         DatabaseMetaData jdbcMetaData = session.getSQLConnection().getSQLMetaData().getJDBCMetaData();
         ResultSet primaryKeys = jdbcMetaData.getPrimaryKeys(globalDbTable.getCatalogName(), globalDbTable.getSchemaName(), globalDbTable.getTableName());

         if(false == primaryKeys.next())
         {
            messageNoPrimaryKeyColumn(globalDbTable, colDef);
            return;
         }

         if(false == colDef.getColumnName().equalsIgnoreCase(primaryKeys.getString("COLUMN_NAME")))
         {
            messageNoPrimaryKeyColumn(globalDbTable, colDef);
            return;
         }

         if(primaryKeys.next())
         {
            JOptionPane.showMessageDialog(_owningFrame, s_stringMgr.getString("ShowReferencesCommand.noSinglePrimaryKeyColumn"));
            return;
         }


         ArrayList<ExportedKey> arrExportedKey = ShowReferencesUtil.getExportedKeys(globalDbTable, inStat, session);

         if(0 == arrExportedKey.size())
         {
            JOptionPane.showMessageDialog(_owningFrame, s_stringMgr.getString("ShowReferencesCommand.noForeignKeyReferences"));
            return;
         }


         new ShowReferencesCtrl(session, _owningFrame, globalDbTable, colDef, arrExportedKey);


      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void messageNoPrimaryKeyColumn(ResultMetaDataTable globalDbTable, ColumnDisplayDefinition colDef)
   {
      JOptionPane.showMessageDialog(_owningFrame, s_stringMgr.getString("ShowReferencesCommand.noPrimaryKeyColumn", globalDbTable.getTableName() + "." + colDef.getColumnName()));
   }
}
