package net.sourceforge.squirrel_sql.fw.gui.action.showreferences;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultMetaDataTable;
import org.apache.commons.lang.ArrayUtils;

import javax.swing.tree.DefaultMutableTreeNode;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ShowReferencesUtil
{
   public static ArrayList<ExportedKey> getExportedKeys(ResultMetaDataTable globalDbTable, String inStat, ISession session)
   {
      try
      {
         DatabaseMetaData jdbcMetaData = session.getSQLConnection().getSQLMetaData().getJDBCMetaData();


         ResultSet exportedKeys = jdbcMetaData.getExportedKeys(globalDbTable.getCatalogName(), globalDbTable.getSchemaName(), globalDbTable.getTableName());

         ArrayList<ExportedKey> arrExportedKey = new ArrayList<ExportedKey>();
         while(exportedKeys.next())
         {
            String fktable_cat = exportedKeys.getString("FKTABLE_CAT");
            String fktable_schem = exportedKeys.getString("FKTABLE_SCHEM");
            String fktable_name = exportedKeys.getString("FKTABLE_NAME");
            String fkcolumn_name = exportedKeys.getString("FKCOLUMN_NAME");

            String fkTablesPkColumnName = null;
            ResultSet fkTablesPrimaryKey = jdbcMetaData.getPrimaryKeys(fktable_cat, fktable_schem, fktable_name);

            if (fkTablesPrimaryKey.next())
            {
               String buf = fkTablesPrimaryKey.getString("COLUMN_NAME");

               if(false == fkTablesPrimaryKey.next())
               {
                  // Single column PK found
                  fkTablesPkColumnName = buf;
               }

               fkTablesPrimaryKey.close();
            }

            ExportedKey exportedKey =
                  new ExportedKey(fktable_cat,
                        fktable_schem,
                        fktable_name,
                        fkcolumn_name,
                        fkTablesPkColumnName,
                        inStat);

            arrExportedKey.add(exportedKey);
         }
         exportedKeys.close();

         return arrExportedKey;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static String generateJoinSQL(Object[] path)
   {
      ArrayUtils.reverse(path);

      String sql =  "";

      for (int i = 0; i < path.length - 1; i++) // path.length - 1 because we exclude root
      {

         ExportedKey exportedKey = (ExportedKey) ((DefaultMutableTreeNode)path[i]).getUserObject();

         if(i == 0)
         {
            sql += "SELECT " + exportedKey.getResultMetaDataTable().getQualifiedName() + ".* FROM " + exportedKey.getResultMetaDataTable().getQualifiedName();
         }
         else
         {
            ExportedKey formerExportedKey = (ExportedKey) ((DefaultMutableTreeNode)path[i-1]).getUserObject();
            sql += " INNER JOIN " + exportedKey.getResultMetaDataTable().getQualifiedName() +
                  " ON " + formerExportedKey.getResultMetaDataTable().getQualifiedName() + "." + formerExportedKey.getFkColumn() + " = " + exportedKey.getResultMetaDataTable().getQualifiedName() + "." + exportedKey.getTablesPrimaryKey();
         }

         if(i == path.length - 2)
         {
            sql += " WHERE " + exportedKey.getResultMetaDataTable() + "." + exportedKey.getFkColumn() + " IN " + exportedKey.getInStat();
         }

      }
      return sql;
   }

   public static String generateInStatSQL(Object[] path)
   {
      //ExportedKey parentExportedKey = (ExportedKey) ((DefaultMutableTreeNode)path[0]).getUserObject();

      //String sql =  "SELECT * FROM " + parentExportedKey.getResultMetaDataTable().getQualifiedName() + " WHERE " + parentExportedKey.getColumn() + " IN ";
      String sql =  "";

      for (int i = 0; i < path.length - 1; i++) // path.length - 1 because we exclude root
      {
         ExportedKey exportedKey = (ExportedKey) ((DefaultMutableTreeNode)path[i]).getUserObject();


         String selection;

         if (0 == i)
         {
            selection = "*";
         }
         else
         {
            sql += "(";
            selection = exportedKey.getTablesPrimaryKey();
         }

         sql +=  "SELECT " + selection + " FROM " + exportedKey.getResultMetaDataTable().getQualifiedName() + " WHERE " + exportedKey.getFkColumn() + " IN ";

         if(i == path.length - 2)
         {
            sql += exportedKey.getInStat();
         }
      }

      for (int i = 0; i < path.length - 2; i++)
      {
         sql += ")";
      }
      return sql;
   }
}
