package net.sourceforge.squirrel_sql.plugins.db2.sql;

public interface DB2Sql
{

	String getUserDefinedFunctionSourceSql();

	String getUserDefinedFunctionDetailsSql();

	String getTriggerDetailsSql();

	public abstract String getViewSourceSql();

	public abstract String getSequenceDetailsSql();

	public abstract String getProcedureSourceSql();

	public abstract String getUserDefinedFunctionListSql();

	public abstract String getSequenceListSql();

	public abstract String getTableIndexListSql();

	public abstract String getTableTriggerListSql();

	public abstract String getIndexDetailsSql();

	public abstract String getTriggerSourceSql();

}