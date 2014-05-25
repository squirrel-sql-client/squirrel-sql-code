package org.squirrelsql.table.tableedit;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.sql.SQLResult;
import org.squirrelsql.table.NullMarker;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DatabaseTableUpdater
{
   public static void updateDatabase(Session session, SQLResult sqlResult, Object interpretedNewValue, TableColumn.CellEditEvent event, String tableNameFromSQL)
   {
      List row = (List) event.getRowValue();

      SQLResultMetaDataFacade sqlResultMetaDataFacade = new SQLResultMetaDataFacade(sqlResult.getResultMetaDataTableLoader());

      ArrayList parameters = new ArrayList();

      int editColIx = event.getTablePosition().getColumn();
      String sql =
            "UPDATE " + tableNameFromSQL +
            " SET " + sqlResultMetaDataFacade.getColumnNameAt(editColIx) + " = ? WHERE ";

      if (interpretedNewValue instanceof NullMarker)
      {
         parameters.add(null);
      }
      else
      {
         parameters.add(interpretedNewValue);
      }

      List<String> colNames = sqlResultMetaDataFacade.getColumnNames();

      HashSet<String> addedColNames = new HashSet<>();



      String catenation = "";
      for (int i = 0; i < colNames.size(); i++)
      {
         String colName = colNames.get(i);

         if(addedColNames.contains(colName))
         {
            continue;
         }
         addedColNames.add(colName);


         Object whereVal;

         if(i == editColIx)
         {
            whereVal = event.getOldValue();
         }
         else
         {
            whereVal = ((SimpleObjectProperty) row.get(i)).get();
         }

         if (null == whereVal || whereVal instanceof NullMarker)
         {
            sql += catenation + colName + " IS NULL ";
         }
         else
         {
            parameters.add(whereVal);
            sql += catenation + colName + " = ? ";
         }


         catenation = " AND ";
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
