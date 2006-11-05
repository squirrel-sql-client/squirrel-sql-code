package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import java.util.Vector;


public class ConstraintInfo extends Object
{
  String fkTable;
  String pkTable;
  String fkName;
  Vector fkCols;
  Vector pkCols;
  short updateRule;
  short deleteRule;

   public ConstraintInfo(String fkTable, 
                         String pkTable, 
                         String fkName, 
                         Vector fkCols, 
                         Vector pkCols,
                         short deleteRule,
                         short updateRule)
   {
      this.fkTable = fkTable;
      this.pkTable = pkTable;
      this.fkName = fkName;
      this.fkCols = fkCols;
      this.pkCols = pkCols;
      this.deleteRule = deleteRule;
      this.updateRule = updateRule;
   }
}
