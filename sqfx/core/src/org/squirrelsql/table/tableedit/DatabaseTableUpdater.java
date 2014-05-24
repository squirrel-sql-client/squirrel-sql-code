package org.squirrelsql.table.tableedit;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.sql.SQLResult;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DatabaseTableUpdater
{
   public static void updateDatabase(Session session, SQLResult sqlResult, TableColumn.CellEditEvent event, String tableNameFromSQL)
   {
      List row = (List) event.getRowValue();

      SQLResultMetaDataFacade sqlResultMetaDataFacade = new SQLResultMetaDataFacade(sqlResult.getResultMetaDataTableLoader());

      ArrayList parameters = new ArrayList();

      int editColIx = event.getTablePosition().getColumn();
      String sql =
            "UPDATE " + tableNameFromSQL +
            " SET " + sqlResultMetaDataFacade.getColumnNameAt(editColIx) + " = ? WHERE ";

      parameters.add(event.getNewValue());

      List<String> colNames = sqlResultMetaDataFacade.getColumnNames();

      HashSet<String> addedColNames = new HashSet<>();

      for (int i = 0; i < colNames.size(); i++)
      {
         String colName = colNames.get(i);

         if(addedColNames.contains(colName))
         {
            continue;
         }


         if(i == editColIx)
         {
            parameters.add(event.getOldValue());
         }
         else
         {
            Object val = ((SimpleObjectProperty) row.get(i)).get();
            parameters.add(val);
         }

         if(i == 0)
         {
            sql += colName + " = ? ";
         }
         else
         {
            sql += " AND " + colName + " = ? ";
         }

         addedColNames.add(colName);
      }

      try
      {
         PreparedStatement prepStat = session.getSQLConnection().getConnection().prepareStatement(sql);

         for (int i = 0; i < parameters.size(); i++)
         {
            Object param = parameters.get(i);
            prepStat.setObject(i+1, param);
         }
         int updateCount = prepStat.executeUpdate();
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }
}
