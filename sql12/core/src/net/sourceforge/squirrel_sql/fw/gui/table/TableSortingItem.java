package net.sourceforge.squirrel_sql.fw.gui.table;

import net.sourceforge.squirrel_sql.fw.gui.ColumnOrder;

import javax.swing.Icon;

public class TableSortingItem
{
   private Icon _sortedColumnIcon;

   /** Column currently being sorted by. -1 means unsorted. */
   private final int _sortedModelColumn;

   private ColumnOrder _columnOrder;

   //////////////////////////////////////////////////////////////////////////
   // For internal and temporary usage during the sorting process only.
   private boolean _allDataString;
   private int _ascendingInt;
   //
   //////////////////////////////////////////////////////////////////////////

   public TableSortingItem(int sortedModelColumn, ColumnOrder columnOrder, Icon sortedColumnIcon)
   {
      _sortedColumnIcon = sortedColumnIcon;
      _sortedModelColumn = sortedModelColumn;
      _columnOrder = columnOrder;
   }

   public void setOrder(ColumnOrder columnOrder, Icon sortedColumnIcon)
   {
      _columnOrder = columnOrder;
      _sortedColumnIcon = sortedColumnIcon;
   }

   public Icon getSortedColumnIcon()
   {
      return _sortedColumnIcon;
   }

   public int getSortedModelColumn()
   {
      return _sortedModelColumn;
   }

   public ColumnOrder getColumnOrder()
   {
      return _columnOrder;
   }

   public void setAllDataString(boolean allDataString)
   {
      _allDataString = allDataString;
   }

   public boolean isAllDataString()
   {
      return _allDataString;
   }

   public void initAscendingInt()
   {
      _ascendingInt = (_columnOrder == ColumnOrder.ASC) ? 1 : -1;
   }

   public int getAscendingInt()
   {
      return _ascendingInt;
   }
}
