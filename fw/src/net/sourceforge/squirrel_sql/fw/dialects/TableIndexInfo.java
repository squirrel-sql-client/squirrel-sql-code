package net.sourceforge.squirrel_sql.fw.dialects;

import java.util.List;


public class TableIndexInfo extends Object
{
  String table;
  String tableSchema;
  String ixName;
  List<IndexColInfo> cols;
  boolean unique;

   public TableIndexInfo(String table, String tableSchema, String ixName, List<IndexColInfo> cols, boolean unique)
   {
      this.table = table;
      this.tableSchema = tableSchema;
      this.ixName = ixName;
      this.cols = cols;
      this.unique = unique;
   }
}
