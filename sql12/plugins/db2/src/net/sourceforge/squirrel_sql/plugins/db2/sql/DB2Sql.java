package net.sourceforge.squirrel_sql.plugins.db2.sql;

public interface DB2Sql
{

   String getUserDefinedFunctionSourceSql();

   String getUserDefinedFunctionDetailsSql();

   String getTriggerDetailsSql();

   String getViewSourceSql();

   String getTableSourceSql();

   String getSequenceDetailsSql();

   String getProcedureSourceSql();

   String getUserDefinedFunctionListSql();

   String getSequenceListSql();

   String getTableIndexListSql();

   String getTableTriggerListSql();

   String getIndexDetailsSql();

   String getTriggerSourceSql();

   String getDB2SpecificColumnDetailsSql();
}