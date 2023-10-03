package net.sourceforge.squirrel_sql.client.session.action.sqlscript;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.sqltofile.ExportToFileHandler;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.sqltofile.ExportToMultiSheetMsExcelHandler;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.CreateTableScriptCommand;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.ScriptUtil;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.QueryHolder;


public class SQLScriptServices
{
   public static void scriptTablesToSQLEntryArea(ISession sess, ITableInfo[] tis)
   {
      new CreateTableScriptCommand(sess.getObjectTreeAPIOfActiveSessionWindow()).scriptTablesToSQLEntryArea(tis);
   }

   public static boolean handledBySqlToFileHandler(QueryHolder sql)
   {
      return ExportToFileHandler.willBeHandledByMe(sql) || ExportToMultiSheetMsExcelHandler.willBeHandledByMe(sql);
   }

   public static String formatTableName(ITableInfo tableInfo)
   {
      return ScriptUtil.getTableName(tableInfo);
   }

   public static boolean isQualifyTableRequired()
   {
      return ScriptUtil.isQualifyTableRequired();
   }

   public static String formatColumnName(TableColumnInfo tcInfo)
   {
      return ScriptUtil.getColumnName(tcInfo);
   }

   public static String formatColumnName(String columnName)
   {
      return ScriptUtil.getColumnName(columnName);
   }

}
