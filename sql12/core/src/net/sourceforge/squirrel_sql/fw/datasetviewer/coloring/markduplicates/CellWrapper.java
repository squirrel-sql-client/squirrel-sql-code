package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates;

public class CellWrapper
{

   private int _rowIndex;
   private int _columnModelIndex;

   public CellWrapper(int rowIndex, int columnModelIndex)
   {
      _rowIndex = rowIndex;
      _columnModelIndex = columnModelIndex;
   }

   public CellWrapper()
   {
   }

   public void setRowIndex(int rowIndex)
   {
      _rowIndex = rowIndex;
   }

   public void setColumnModelIndex(int columnModelIndex)
   {
      _columnModelIndex = columnModelIndex;
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (o == null || getClass() != o.getClass())
      {
         return false;
      }

      CellWrapper that = (CellWrapper) o;

      if (_rowIndex != that._rowIndex)
      {
         return false;
      }
      return _columnModelIndex == that._columnModelIndex;
   }

   @Override
   public int hashCode()
   {
      int result = _rowIndex;
      result = 31 * result + _columnModelIndex;
      return result;
   }
}
