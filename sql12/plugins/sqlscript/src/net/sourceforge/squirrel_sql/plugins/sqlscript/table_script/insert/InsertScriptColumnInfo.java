package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.insert;

class InsertScriptColumnInfo
{
   int sqlType; // As in java.sql.Types
   String columnName;

   public InsertScriptColumnInfo(String columnName, int sqlType)
   {
      this.columnName = columnName;
      this.sqlType = sqlType;
   }
}
