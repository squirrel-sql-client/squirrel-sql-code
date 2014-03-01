package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

import net.sourceforge.squirrel_sql.plugins.graph.querybuilder.SelectCol;

public class SelectStructureXmlBean
{
   private SelectCol[] _selectCols;

   /**
    * Needed for XML deserialization
    */
   public SelectStructureXmlBean()
   {
   }

   public SelectStructureXmlBean(SelectCol[] selectCols)
   {
      _selectCols = selectCols;
   }

   public SelectCol[] getSelectCols()
   {
      return _selectCols;
   }

   /**
    * Needed for XML deserialization
    */
   public void setSelectCols(SelectCol[] selectColumns)
   {
      _selectCols = selectColumns;
   }
}
