package net.sourceforge.squirrel_sql.fw.gui.action.showreferences;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultMetaDataTable;
import net.sourceforge.squirrel_sql.fw.gui.action.InStatColumnInfo;
import org.apache.commons.lang.ArrayUtils;

import javax.swing.tree.DefaultMutableTreeNode;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowReferencesUtil
{
   public static HashMap<String, ExportedKey> getExportedKeys(ResultMetaDataTable globalDbTable, ArrayList<InStatColumnInfo> inStatColumnInfos, ISession session)
   {
      try
      {
         DatabaseMetaData jdbcMetaData = session.getSQLConnection().getSQLMetaData().getJDBCMetaData();


         ResultSet exportedKeys = jdbcMetaData.getExportedKeys(globalDbTable.getCatalogName(), globalDbTable.getSchemaName(), globalDbTable.getTableName());

         HashMap<String, ExportedKey> fkName_exportedKey = new HashMap<String, ExportedKey>();
         while(exportedKeys.next())
         {
            String fkName = exportedKeys.getString("FK_NAME");
            String fktable_cat = exportedKeys.getString("FKTABLE_CAT");
            String fktable_schem = exportedKeys.getString("FKTABLE_SCHEM");
            String fktable_name = exportedKeys.getString("FKTABLE_NAME");

            String pktable_cat = exportedKeys.getString("PKTABLE_CAT");
            String pktable_schem = exportedKeys.getString("PKTABLE_SCHEM");
            String pktable_name = exportedKeys.getString("PKTABLE_NAME");

            String fkcolumn_name = exportedKeys.getString("FKCOLUMN_NAME");
            String pkcolumn_name = exportedKeys.getString("PKCOLUMN_NAME");

            ExportedKey exportedKey = fkName_exportedKey.get(fkName);

            if(null == exportedKey)
            {
               exportedKey = new ExportedKey(fkName, fktable_cat, fktable_schem, fktable_name, pktable_cat, pktable_schem, pktable_name);
               fkName_exportedKey.put(fkName, exportedKey);
            }
            exportedKey.addColumn(fkcolumn_name, pkcolumn_name);
         }
         exportedKeys.close();

         return fkName_exportedKey;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static JoinSQLInfo generateJoinSQLInfo(Object[] path)
   {
      ArrayUtils.reverse(path);

      String sql =  "";

      String tableToBeEdited = null;

      for (int i = 0; i < path.length - 1; i++) // path.length - 1 because we exclude root
      {
         ExportedKey exportedKey = (ExportedKey) ((DefaultMutableTreeNode)path[i]).getUserObject();

         if(i == 0)
         {
            tableToBeEdited = exportedKey.getFkResultMetaDataTable().getQualifiedName();
            sql += "SELECT " + tableToBeEdited + ".* FROM " + exportedKey.getFkResultMetaDataTable().getQualifiedName();
         }

         sql += " INNER JOIN " + exportedKey.getPkResultMetaDataTable().getQualifiedName();

         String joinFields = null;
         for (Map.Entry<String, String> fk_pk : exportedKey.getFkColumn_pkcolumnMap().entrySet())
         {
            if (null == joinFields)
            {
               joinFields = " ON " + exportedKey.getPkResultMetaDataTable().getQualifiedName() + "." + fk_pk.getValue() + " = " + exportedKey.getFkResultMetaDataTable().getQualifiedName() + "." + fk_pk.getKey();
            }
            else
            {
               joinFields += " AND " + exportedKey.getPkResultMetaDataTable().getQualifiedName() + "." + fk_pk.getValue() + " = " + exportedKey.getFkResultMetaDataTable().getQualifiedName() + "." + fk_pk.getKey();
            }

         }

         sql += joinFields;

      }

      RootTable rootTable = (RootTable) ((DefaultMutableTreeNode)path[path.length - 1]).getUserObject();

      for (int j = 0; j < rootTable.getInStatColumnInfos().size(); j++)
      {
         InStatColumnInfo inStatColumnInfo = rootTable.getInStatColumnInfos().get(j);

         if (0 == j)
         {
            sql += " WHERE " + rootTable.getGlobalDbTable() + "." + inStatColumnInfo.getColDef().getColumnName() + " IN " + inStatColumnInfo.getInstat();
         }
         else
         {
            sql += " AND " + rootTable.getGlobalDbTable() + "." + inStatColumnInfo.getColDef().getColumnName() + " IN " + inStatColumnInfo.getInstat();
         }
      }


      return new JoinSQLInfo(sql, tableToBeEdited);
   }

}
