package net.sourceforge.squirrel_sql.fw.dialects;

import java.util.Vector;


public class ConstraintInfo extends Object
{
  String fkTable;
  String fkTableSchema;
  String pkTable;
  String pkTableSchema;
   String fkName;
   Vector<String> fkCols;
  Vector<String> pkCols;
  short updateRule;
  short deleteRule;

   public ConstraintInfo(String fkTable,
                         String fkTableSchema,
                         String pkTable,
                         String pkTableSchema,
                         String fkName,
                         Vector<String> fkCols,
                         Vector<String> pkCols,
                         short deleteRule,
                         short updateRule)
   {
      this.fkTable = fkTable;
      this.fkTableSchema = fkTableSchema;
      this.pkTable = pkTable;
      this.pkTableSchema = pkTableSchema;
      this.fkName = fkName;
      this.fkCols = fkCols;
      this.pkCols = pkCols;
      this.deleteRule = deleteRule;
      this.updateRule = updateRule;
   }
}
