package net.sourceforge.squirrel_sql.fw.gui.table;

import net.sourceforge.squirrel_sql.fw.gui.ColumnOrder;
import net.sourceforge.squirrel_sql.fw.resources.LibraryResources;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TableSortingAdmin
{
   /** Icon for "Sorted ascending". */
   private final Icon _ascIcon;

   /** Icon for "Sorted descending". */
   private final Icon _descIcon;

   private List<TableSortingItem> _tableSortingItems = new ArrayList<>();
   private TableSortingItem _lastChangedSortingItem;

   /** Icon for the currently sorted column. */

   public TableSortingAdmin()
   {
      LibraryResources rsrc = new LibraryResources();
      _descIcon = rsrc.getIcon(LibraryResources.IImageNames.TABLE_DESCENDING);
      _ascIcon = rsrc.getIcon(LibraryResources.IImageNames.TABLE_ASCENDING);
   }


   public void updateSortedColumn(int sortedColumn, ColumnOrder columnOrder)
   {
      updateSortedColumn(sortedColumn, columnOrder, true);
   }
   public void updateSortedColumn(int sortedColumn, ColumnOrder columnOrder, boolean clearFirst)
   {
      if(clearFirst)
      {
         clear();
      }

      if(columnOrder == ColumnOrder.NATURAL)
      {
         _tableSortingItems.removeIf(si -> si.getSortedModelColumn() == sortedColumn);
         return;
      }

      Icon sortedColumnIcon = columnOrder == ColumnOrder.ASC ? _ascIcon : _descIcon;

      Optional<TableSortingItem> match = _tableSortingItems.stream().filter(si -> si.getSortedModelColumn() == sortedColumn).findFirst();
      if(match.isPresent())
      {
         match.get().setOrder(columnOrder, sortedColumnIcon);
         _lastChangedSortingItem = match.get();
      }
      else
      {
         TableSortingItem newTableSortingItem = new TableSortingItem(sortedColumn, columnOrder, sortedColumnIcon);
         _tableSortingItems.add(newTableSortingItem);
         _lastChangedSortingItem = newTableSortingItem;
      }
   }

   public void clear()
   {
      _tableSortingItems.clear();
   }


   public List<TableSortingItem> getTableSortingItems()
   {
      return _tableSortingItems;
   }

   public TableSortingItem getTableSortingItem(int sortModelColumn)
   {
      return _tableSortingItems.stream().filter(si -> si.getSortedModelColumn() == sortModelColumn).findFirst().orElse(null);
   }

   public boolean hasSortedColumns()
   {
      return false == _tableSortingItems.isEmpty();
   }

   public TableSortingItem getLastChangedSortingItem()
   {
      return _lastChangedSortingItem;
   }

   public int getFirstSortedColumn()
   {
      if(hasSortedColumns())
      {
         return _tableSortingItems.get(0).getSortedModelColumn();
      }

      return -1;
   }

   public ColumnOrder getFirstColumnOrder()
   {
      if(hasSortedColumns())
      {
         return _tableSortingItems.get(0).getColumnOrder();
      }

      return ColumnOrder.NATURAL;
   }

}
