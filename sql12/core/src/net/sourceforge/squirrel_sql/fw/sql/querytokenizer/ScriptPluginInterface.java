package net.sourceforge.squirrel_sql.fw.sql.querytokenizer;


import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

public interface ScriptPluginInterface
{
   boolean handledBySqlToFileHandler(QueryHolder sql);

   String formatTableName(ITableInfo tInfo);

   boolean isQualifyTableRequired();

   String formatColumnName(TableColumnInfo tcInfo);

   String formatColumnName(String columnName);
}
