package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import java.util.Vector;


public class IndexInfo extends Object
{
  String table;
  String ixName;
  Vector cols;

   public IndexInfo(String table, String ixName, Vector cols)
   {
      this.table = table;
      this.ixName = ixName;
      this.cols = cols;
   }
}
