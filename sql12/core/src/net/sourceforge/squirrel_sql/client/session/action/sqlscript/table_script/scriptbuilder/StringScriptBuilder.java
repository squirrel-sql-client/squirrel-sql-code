package net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.scriptbuilder;

public class StringScriptBuilder implements ScriptBuilder
{
   private final StringBuilder _sb;

   public StringScriptBuilder()
   {
      _sb = new StringBuilder();
   }

   public StringBuilder getStringBuilder()
   {
      return _sb;
   }

   @Override
   public void append(String s)
   {
      _sb.append(s);
   }

   @Override
   public void append(StringBuilder sb)
   {
      _sb.append(sb);
   }

   public StringBuilder getStringBuilderClone()
   {
      return new StringBuilder(_sb);
   }
}
