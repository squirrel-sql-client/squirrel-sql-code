package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import java.util.Vector;


public class ConstraintInfo extends Object
{
  String fkTable;
  String pkTable;
  String fkName;
  Vector fkCols;
  Vector pkCols;

   public ConstraintInfo(String fkTable, String pkTable, String fkName, Vector fkCols, Vector pkCols)
   {
      this.fkTable = fkTable;
      this.pkTable = pkTable;
      this.fkName = fkName;
      this.fkCols = fkCols;
      this.pkCols = pkCols;
   }
}
