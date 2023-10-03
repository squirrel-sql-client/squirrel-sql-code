package net.sourceforge.squirrel_sql.fw.sql.tablenamefind;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.SQLScriptServices;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableNameFindService
{
   private static final Pattern FILL_COLUMN_NAME_PATTERN = Pattern.compile(".+:([^:]+):[^:]+$");

   public static String findTableNameBySqlOrResultMetaData(String sql, ResultSet srcResult, ISession session) throws SQLException
   {
      QualifyResult qualifyResult = TableQualifyingService.qualifyIfNeeded(getFirstTableNameFromResultSetMetaData(srcResult, session), session);

      if (qualifyResult.getState() == QualifyResultState.EMPTY || qualifyResult.getState() == QualifyResultState.HEURISTIC_USED)
      {
         qualifyResult = TableQualifyingService.qualifyIfNeeded(_findTableNameInSQL(sql), session);
      }

      if (qualifyResult.getState() == QualifyResultState.EMPTY)
      {
         return "PressCtrlH";
      }


      return qualifyResult.getTableName();
   }


   public static String findTableNameInSQL(String sql, ISession session)
   {
      return TableQualifyingService.qualifyIfNeeded(_findTableNameInSQL(sql), session).getTableName();
   }

   private static String _findTableNameInSQL(String sql)
   {
      Pattern patternBeforeTable = Pattern.compile("SELECT\\s+[A-Z0-9_\\*\\.',\\s\"]*\\s+FROM\\s+([A-Z0-9_\\.\"]+)");
      String ucSql = sql.toUpperCase().trim();
      // Bug 1371587 - remove useless accent characters if they exist
      ucSql = ucSql.replaceAll("\\`", "");
      Matcher matcher;

      matcher = patternBeforeTable.matcher(ucSql);
      if(false == matcher.find())
      {
         return null;
      }

      // Get the table name in its original upper-lower case.
      String table = sql.trim().substring(matcher.start(1), matcher.end(1));

      String behindTable = ucSql.substring(matcher.end(1)).trim();

      SingleTableSqlEnum ret = behindTableAllowsEditing(behindTable);

      if(SingleTableSqlEnum.SINGLE_TABLE_SQL_UNKNOWN == ret)
      {
         // This might be because an table alias is used maybe with an AS before it.

         Pattern patternBehindTable;
         if(behindTable.startsWith("AS") && 2 < behindTable.length() && Character.isWhitespace(behindTable.charAt(2)))
         {
            patternBehindTable = Pattern.compile("AS\\s+([A-Z0-9_]+)\\s+");
         }
         else
         {
            patternBehindTable = Pattern.compile("([A-Z0-9_]+)\\s+|[A-Z0-9_]+$");
         }

         matcher = patternBehindTable.matcher(behindTable);
         if(false == matcher.find())
         {
            return null;
         }

         String behindAlias = behindTable.substring(matcher.end(0)).trim();

         ret = behindTableAllowsEditing(behindAlias);

         if(SingleTableSqlEnum.SINGLE_TABLE_SQL_TRUE == ret)
         {
            return table;
         }
         else
         {
            return null;
         }
      }
      else if(SingleTableSqlEnum.SINGLE_TABLE_SQL_TRUE == ret)
      {
         return table;
      }
      else //(ALLOWS_EDITING_FALSE == ret)
      {
         return null;
      }
   }

   /**
    * This method may never be called?!
    * In case it is called {@link TableQualifyingService#qualifyIfNeeded(String, ISession)} ma be necessary.
    */
   public static String findTableNameInColumnDisplayDefinition(ColumnDisplayDefinition colDef)
   {
      if(false == StringUtilities.isEmpty(colDef.getTableName(), true))
      {
         return colDef.getTableName();
      }

      if (null != colDef.getResultMetaDataTable()
          && false == StringUtilities.isEmpty(colDef.getResultMetaDataTable().getTableName(), true))
      {
         return colDef.getResultMetaDataTable().getTableName();
      }

      if (false == StringUtilities.isEmpty(colDef.getFullTableColumnName(), true))
      {
         Matcher matcher = FILL_COLUMN_NAME_PATTERN.matcher(colDef.getFullTableColumnName());

         if (matcher.matches())
         {
            return matcher.group(1);
         }
      }

      return null;
   }

   private static String getFirstTableNameFromResultSetMetaData(ResultSet srcResult, ISession session) throws SQLException
   {
      ResultSetMetaData metaData = srcResult.getMetaData();
      //String tableName = metaData.getTableName(1);

      for (int i = 1; i <= metaData.getColumnCount(); i++)
      {
         ITableInfo tInfo = new TableInfo(
               metaData.getCatalogName(i),
               metaData.getSchemaName(i),
               metaData.getTableName(i),
               "TABLE", "",
               session.getMetaData());

         String tableName = SQLScriptServices.formatTableName(tInfo);

         if(false == StringUtilities.isEmpty(tableName, true))
         {
            return tableName;
         }

      }

      return null;
   }

   private static SingleTableSqlEnum behindTableAllowsEditing(String behindTable)
   {
      if(0 == behindTable.length())
      {
         return SingleTableSqlEnum.SINGLE_TABLE_SQL_TRUE;
      }
      else if(   behindTable.startsWith("WHERE")
         || behindTable.startsWith("ORDER")
         || behindTable.startsWith("GROUP"))
      {
         return SingleTableSqlEnum.SINGLE_TABLE_SQL_TRUE;
      }
      else if(   behindTable.startsWith(",")
         || behindTable.startsWith("INNER")
         || behindTable.startsWith("LEFT")
         || behindTable.startsWith("RIGHT")
         || behindTable.startsWith("OUTER")
         || behindTable.startsWith(","))
      {
         return SingleTableSqlEnum.SINGLE_TABLE_SQL_FALSE;
      }
      else
      {
         return SingleTableSqlEnum.SINGLE_TABLE_SQL_UNKNOWN;
      }
   }

}
