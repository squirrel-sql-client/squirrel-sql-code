package net.sourceforge.squirrel_sql.plugins.dbcopy.sqlscript;

import java.util.Vector;


/**
 * From the SQL Scripts plugin by Johan Compagner and Gerd Wagner
 */
public class IndexInfo extends Object
{
  public String table;
  public String ixName;
  public Vector<IndexColInfo> cols;

   public IndexInfo(String table, String ixName, Vector<IndexColInfo> cols)
   {
      this.table = table;
      this.ixName = ixName;
      this.cols = cols;
   }
}
