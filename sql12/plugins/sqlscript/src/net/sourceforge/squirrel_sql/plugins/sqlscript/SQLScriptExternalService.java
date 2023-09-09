package net.sourceforge.squirrel_sql.plugins.sqlscript;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.QueryHolder;
import net.sourceforge.squirrel_sql.plugins.sqlscript.sqltofile.ExportToFileHandler;
import net.sourceforge.squirrel_sql.plugins.sqlscript.sqltofile.ExportToMultiSheetMsExcelHandler;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.CreateTableScriptCommand;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.ScriptUtil;


public class SQLScriptExternalService
{
   private SQLScriptPlugin _sqlScriptPlugin;

   public SQLScriptExternalService(SQLScriptPlugin sqlScriptPlugin)
   {
      _sqlScriptPlugin = sqlScriptPlugin;
   }

   public void scriptTablesToSQLEntryArea(ISession sess, ITableInfo[] tis)
   {
      new CreateTableScriptCommand(sess.getObjectTreeAPIOfActiveSessionWindow(), _sqlScriptPlugin).scriptTablesToSQLEntryArea(tis);
   }

   public boolean handledBySqlToFileHandler(QueryHolder sql)
   {
      return ExportToFileHandler.willBeHandledByMe(sql) || ExportToMultiSheetMsExcelHandler.willBeHandledByMe(sql);
   }

   public String formatTableName(ITableInfo tableInfo)
   {
      return ScriptUtil.getTableName(tableInfo);
   }

   public boolean isQualifyTableRequired()
   {
      return ScriptUtil.isQualifyTableRequired();
   }

   public String formatColumnName(TableColumnInfo tcInfo)
   {
      return ScriptUtil.getColumnName(tcInfo);
   }

   public String formatColumnName(String columnName)
   {
      return ScriptUtil.getColumnName(columnName);
   }

}
