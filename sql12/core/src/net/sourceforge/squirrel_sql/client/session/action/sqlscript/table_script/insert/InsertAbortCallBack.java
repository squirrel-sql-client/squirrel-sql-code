package net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.insert;

@FunctionalInterface
public interface InsertAbortCallBack
{
   boolean isAborted();
}
