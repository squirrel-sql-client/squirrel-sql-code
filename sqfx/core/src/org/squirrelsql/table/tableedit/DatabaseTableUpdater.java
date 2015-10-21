package org.squirrelsql.table.tableedit;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import org.squirrelsql.AppState;
import org.squirrelsql.ExceptionHandler;
import org.squirrelsql.services.FXMessageBox;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.sql.SQLResult;
import org.squirrelsql.table.NullMarker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DatabaseTableUpdater
{
   public static DatabaseTableUpdateResult updateDatabase(Session session, SQLResult sqlResult, String userEnteredString, SquirrelTableEditData tableEditData, String tableNameFromSQL)
   {
      return _doUpdate(session, sqlResult, tableNameFromSQL, true, userEnteredString, tableEditData, null);
   }

   private static DatabaseTableUpdateResult _doUpdate(Session session, SQLResult sqlResult, String tableNameFromSQL, boolean update, String userEnteredString, SquirrelTableEditData tableEditData, List deleteRow)
   {
      I18n i18n = new I18n(DatabaseTableUpdater.class);

      List row;

      if (update)
      {
         row = tableEditData.getRowValue();
      }
      else
      {
         row = deleteRow;
      }

      SQLResultMetaDataFacade sqlResultMetaDataFacade = new SQLResultMetaDataFacade(sqlResult.getResultMetaDataTableLoader());

      ArrayList<PrepStatParam> updSqlParams = new ArrayList();
      ArrayList<PrepStatParam> selSqlParams = new ArrayList();


      String selectSql =
            "SELECT COUNT(*) FROM " + tableNameFromSQL;


      int editColIx = -1;
      if (update)
      {
         editColIx = tableEditData.getTablePosition().getColumn();
      }

      String updateSql;

      if (update)
      {
         updateSql = "UPDATE " + tableNameFromSQL + " SET " + sqlResultMetaDataFacade.getColumnNameAt(editColIx) + " = ? ";
      }
      else
      {
         updateSql = "DELETE FROM " + tableNameFromSQL;
      }

      Object interpretedNewValue = null;

      if (update)
      {
         try
         {
            interpretedNewValue = StringInterpreter.interpret(userEnteredString, sqlResultMetaDataFacade.getColumnClassNameAt(editColIx));
         }
         catch (Throwable t)
         {
            FXMessageBox.showInfoOk(AppState.get().getPrimaryStage(), i18n.t("failed.to.update", t.getMessage()));
            ExceptionHandler.handle(t);
            return new DatabaseTableUpdateResult(DatabaseTableUpdateResult.CancelReason.FAILED_TO_INTERPRET_USER_EDIT);
         }

         if (interpretedNewValue instanceof NullMarker)
         {
            updSqlParams.add(new PrepStatParam(null, sqlResultMetaDataFacade.getSqlTypeAt(editColIx)));
         }
         else
         {
            updSqlParams.add(new PrepStatParam(interpretedNewValue, sqlResultMetaDataFacade.getSqlTypeAt(editColIx)));
         }
      }


      List<String> colNames = sqlResultMetaDataFacade.getColumnNames();

      HashSet<String> addedColNames = new HashSet<>();


      String whereStat = " WHERE ";
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

         if(update && i == editColIx)
         {
            whereVal = tableEditData.getOldValue();
         }
         else
         {
            whereVal = ((SimpleObjectProperty) row.get(i)).get();
         }

         if (null == whereVal || whereVal instanceof NullMarker)
         {
            whereStat += catenation + colName + " IS NULL ";
         }
         else
         {
            PrepStatParam param = new PrepStatParam(whereVal, sqlResultMetaDataFacade.getSqlTypeAt(i));
            updSqlParams.add(param);
            selSqlParams.add(param);
            whereStat += catenation + colName + " = ? ";
         }


         catenation = " AND ";
      }


      int updateCount = -1;
      try
      {
         Connection con = session.getSQLConnection().getConnection();
         PreparedStatement prepStat;

         prepStat = con.prepareStatement(selectSql + whereStat);
         setParams(selSqlParams, prepStat);
         ResultSet res = prepStat.executeQuery();
         res.next();
         int count = res.getInt(1);
         try
         {
            if(0 == count)
            {
               String msg;
               if (update)
               {
                  msg = i18n.t("update.effects.no.rows");
               }
               else
               {
                  msg = i18n.t("delete.effects.no.rows");
               }
               FXMessageBox.showInfoOk(AppState.get().getPrimaryStage(), msg);
               return new DatabaseTableUpdateResult(DatabaseTableUpdateResult.CancelReason.NO_ROWS_AFFECTED);
            }
            else if (1 < count)
            {
               String msg;

               if (update)
               {
                  msg = i18n.t("update.effects.more.rows", count);
               }
               else
               {
                  msg = i18n.t("delete.effects.more.rows", count);
               }

               if(FXMessageBox.NO.equals(FXMessageBox.showYesNo(AppState.get().getPrimaryStage(), msg)))
               {
                  return new DatabaseTableUpdateResult(DatabaseTableUpdateResult.CancelReason.CANCELED_BY_USER);
               }
            }
         }
         finally
         {
            prepStat.close();
         }

         prepStat = con.prepareStatement(updateSql + whereStat);

         setParams(updSqlParams, prepStat);
         updateCount = prepStat.executeUpdate();
         prepStat.close();
      }
      catch (SQLException e)
      {
         String msg;

         if (update)
         {
            msg = i18n.t("update.error", e.getMessage());
         }
         else
         {
            msg = i18n.t("delete.error", e.getMessage());
         }

         FXMessageBox.showInfoOk(AppState.get().getPrimaryStage(), msg);
         ExceptionHandler.handle(e);
         return new DatabaseTableUpdateResult(DatabaseTableUpdateResult.CancelReason.FAILED_TO_EXECUTE_UPDATE);
      }

      return new DatabaseTableUpdateResult(interpretedNewValue, updateCount);
   }

   private static void setParams(ArrayList<PrepStatParam> updSqlParams, PreparedStatement prepStat) throws SQLException
   {
      for (int i = 0; i < updSqlParams.size(); i++)
      {
         PrepStatParam param = updSqlParams.get(i);
         prepStat.setObject(i+1, param.getVal(), param.getSqlType());
      }
   }

   public static ArrayList<List> deleteFromDatabase(Session session, SQLResult sqlResult, ObservableList<List> selectedRows, String tableNameFromSQL)
   {
      ArrayList<List> deletedRows = new ArrayList<>();

      for (List selectedRow : selectedRows)
      {
         DatabaseTableUpdateResult databaseTableUpdateResult = _doUpdate(session, sqlResult, tableNameFromSQL, false, null, null, selectedRow);

         if(databaseTableUpdateResult.success())
         {
            deletedRows.add(selectedRow);
         }
         else
         {
            break;
         }
      }

      return deletedRows;
   }
}
