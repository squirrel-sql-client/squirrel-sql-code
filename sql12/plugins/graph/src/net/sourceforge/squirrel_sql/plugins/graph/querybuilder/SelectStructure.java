package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

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
