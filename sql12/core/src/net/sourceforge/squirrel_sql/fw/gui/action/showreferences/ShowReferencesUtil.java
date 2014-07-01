package net.sourceforge.squirrel_sql.fw.gui.action.showreferences;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultMetaDataTable;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by gerd on 01.07.14.
 */
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
            ExportedKey exportedKey =
                  new ExportedKey(exportedKeys.getString("FKTABLE_CAT"),
                        exportedKeys.getString("FKTABLE_SCHEM"),
                        exportedKeys.getString("FKTABLE_NAME"),
                        exportedKeys.getString("FKCOLUMN_NAME"),
                        inStat);

            arrExportedKey.add(exportedKey);
         }

         return arrExportedKey;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }
}
