package net.sourceforge.squirrel_sql.plugins.graph.nondbconst;

import net.sourceforge.squirrel_sql.plugins.graph.ColumnInfo;

public class ContraintDisplayData
{
   private ColumnInfo _fkCol;
   private ColumnInfo _pkCol;

   public ContraintDisplayData(ColumnInfo fkCol, ColumnInfo pkCol)
   {
      _fkCol = fkCol;
      _pkCol = pkCol;
   }

   public ColumnInfo getFkCol()
   {
      return _fkCol;
   }

   public ColumnInfo getPkCol()
   {
      return _pkCol;
   }
}
