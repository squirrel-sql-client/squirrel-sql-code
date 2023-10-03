package net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.scriptbuilder;

public interface ScriptBuilder
{
   void append(String s);

   void append(StringBuilder sbValues);
}
