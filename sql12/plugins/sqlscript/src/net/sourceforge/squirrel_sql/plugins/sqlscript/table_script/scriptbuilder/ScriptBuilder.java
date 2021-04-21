package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.scriptbuilder;

public interface ScriptBuilder
{
   void append(String s);

   void append(StringBuilder sbValues);
}
