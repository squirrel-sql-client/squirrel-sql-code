package net.sourceforge.squirrel_sql.fw.gui.table;

import net.sourceforge.squirrel_sql.fw.gui.ColumnOrder;
import net.sourceforge.squirrel_sql.fw.resources.LibraryResources;

import javax.swing.Icon;

public class TableSortingAdmin
{
   /** Icon for "Sorted ascending". */
   private Icon _ascIcon;

   /** Icon for "Sorted descending". */
   private Icon _descIcon;

   /** Icon for the currently sorted column. */
   private Icon _sortedColumnIcon;

   /** Column currently being sorted by. -1 means unsorted. */
   private int _sortedColumn = -1;

   private ColumnOrder _columnOrder = ColumnOrder.NATURAL;

   public TableSortingAdmin()
   {
      LibraryResources rsrc = new LibraryResources();
      _descIcon = rsrc.getIcon(LibraryResources.IImageNames.TABLE_DESCENDING);
      _ascIcon = rsrc.getIcon(LibraryResources.IImageNames.TABLE_ASCENDING);
   }


   public void sort(int sortedColumn, ColumnOrder newOrder)
   {
      _sortedColumn = sortedColumn;
      _columnOrder = newOrder;
      if (ColumnOrder.ASC == _columnOrder)
      {
         _sortedColumnIcon = _ascIcon;
      }
      else if (ColumnOrder.DESC == _columnOrder)
      {
         _sortedColumnIcon = _descIcon;
      }
      else
      {
         _sortedColumnIcon = null;
      }
   }


   public void clear()
   {
      _sortedColumnIcon = null;
      _sortedColumn = -1;
      _columnOrder = ColumnOrder.NATURAL;
   }

   public Icon getSortedColumnIcon()
   {
      return _sortedColumnIcon;
   }

   public int getSortedColumn()
   {
      return _sortedColumn;
   }

   public ColumnOrder getColumnOrder()
   {
      return _columnOrder;
   }
}
