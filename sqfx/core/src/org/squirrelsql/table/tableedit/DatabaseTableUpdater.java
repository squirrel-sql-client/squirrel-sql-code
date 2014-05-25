package org.squirrelsql.table.tableedit;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.schemainfo.StructItem;
import org.squirrelsql.session.sql.SQLResult;
import org.squirrelsql.table.NullMarker;
import org.squirrelsql.table.TableLoader;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DatabaseTableUpdater
{
   public static DatabaseTableUpdateResult updateDatabase(Session session, SQLResult sqlResult, String userEnteredString, TableColumn.CellEditEvent event, String tableNameFromSQL)
   {
      List row = (List) event.getRowValue();

      SQLResultMetaDataFacade sqlResultMetaDataFacade = new SQLResultMetaDataFacade(sqlResult.getResultMetaDataTableLoader());

      ArrayList parameters = new ArrayList();

      int editColIx = event.getTablePosition().getColumn();
      String sql =
            "UPDATE " + tableNameFromSQL +
            " SET " + sqlResultMetaDataFacade.getColumnNameAt(editColIx) + " = ? WHERE ";


      Object interpretedNewValue;
      try
      {
         interpretedNewValue = interpret(userEnteredString, sqlResultMetaDataFacade.getColumnClassNameAt(editColIx));
      }
      catch (ClassNotFoundException e)
      {
         return new DatabaseTableUpdateResult(e);
      }

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



      int updateCount = -1;
      try
      {
         PreparedStatement prepStat = session.getSQLConnection().getConnection().prepareStatement(sql);

         for (int i = 0; i < parameters.size(); i++)
         {
            Object param = parameters.get(i);
            prepStat.setObject(i+1, param);
         }
         updateCount = prepStat.executeUpdate();
      }
      catch (SQLException e)
      {
         return new DatabaseTableUpdateResult(e);
      }

      return new DatabaseTableUpdateResult(interpretedNewValue, updateCount);

   }

   private static Object interpret(String userEnteredString, String columnClassName) throws ClassNotFoundException
   {
      if(null == userEnteredString || "".equals(userEnteredString) || TableLoader.NULL_AS_STRING.equals(userEnteredString))
      {
         return TableLoader.NULL_AS_MARKER;
      }

      Class clazz = Class.forName(columnClassName);

      if(String.class.equals(clazz))
      {
         return userEnteredString;
      }

      if(Integer.class.equals(clazz))
      {
         return Integer.valueOf(userEnteredString);
      }

      if(Integer.class.equals(clazz))
      {
         return Integer.valueOf(userEnteredString);
      }

      if(Long.class.equals(clazz))
      {
         return Integer.valueOf(userEnteredString);
      }

      if(Short.class.equals(clazz))
      {
         return Short.valueOf(userEnteredString);
      }

      if(Boolean.class.equals(clazz))
      {
         try
         {
            Integer intVal = Integer.valueOf(userEnteredString);
            return !intVal.equals(0);
         }
         catch (NumberFormatException e)
         {
         }

         return Boolean.valueOf(userEnteredString);
      }

      if(Byte.class.equals(clazz))
      {
         return Byte.valueOf(userEnteredString);
      }

      if(Character.class.equals(clazz))
      {
         return Character.valueOf(userEnteredString.charAt(0));
      }

      if(Double.class.equals(clazz))
      {
         return Double.valueOf(userEnteredString);
      }

      if(Float.class.equals(clazz))
      {
         return Float.valueOf(userEnteredString);
      }

      return userEnteredString;
   }

}
