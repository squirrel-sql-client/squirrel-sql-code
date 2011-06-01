package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.plugins.graph.querybuilder.sqlgen.SelectClauseRes;

public class SelectStructure
{
   private SelectCol[] _selectCols;

   public SelectStructure(SelectCol[] selectCols)
   {
      _selectCols = selectCols;
   }

   public SelectCol[] getSelectCols()
   {
      return _selectCols;
   }
}
