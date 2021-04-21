package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.insert;

@FunctionalInterface
public interface InsertAbortCallBack
{
   boolean isAborted();
}
