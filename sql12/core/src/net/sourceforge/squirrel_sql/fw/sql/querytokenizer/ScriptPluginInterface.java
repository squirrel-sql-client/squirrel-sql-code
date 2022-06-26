package net.sourceforge.squirrel_sql.fw.sql.querytokenizer;


import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

public interface ScriptPluginInterface
{
   boolean startsWithSqlToFileMarker(String sql);

   String formatTableName(ITableInfo tInfo);

   boolean isQualifyTableRequired();
}
