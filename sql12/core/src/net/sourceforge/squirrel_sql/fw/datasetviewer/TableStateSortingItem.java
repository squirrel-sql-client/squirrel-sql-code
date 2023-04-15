package net.sourceforge.squirrel_sql.fw.datasetviewer;

import net.sourceforge.squirrel_sql.fw.gui.ColumnOrder;

public class TableStateSortingItem
{
   private int _sortedModelColumn;
   private ColumnOrder _columnOrder;

   public TableStateSortingItem(int sortedModelColumn, ColumnOrder columnOrder)
   {
      _sortedModelColumn = sortedModelColumn;
      _columnOrder = columnOrder;
   }

   public int getSortedModelColumn()
   {
      return _sortedModelColumn;
   }

   public ColumnOrder getColumnOrder()
   {
      return _columnOrder;
   }
}
