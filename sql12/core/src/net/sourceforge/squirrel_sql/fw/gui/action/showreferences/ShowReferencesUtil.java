package net.sourceforge.squirrel_sql.fw.gui.action.showreferences;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultMetaDataTable;

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
}
