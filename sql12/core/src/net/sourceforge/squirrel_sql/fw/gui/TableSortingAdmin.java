package net.sourceforge.squirrel_sql.fw.gui;

import net.sourceforge.squirrel_sql.fw.resources.LibraryResources;

import javax.swing.Icon;

public class TableSortingAdmin
{
   /** Icon for "Sorted ascending". */
   private Icon _ascIcon;

   /** Icon for "Sorted descending". */
   private Icon _descIcon;

   //////////////////////////////////////////////////////////////////////
   // Originating from ButtonTableHeader TODO: Fix redundancies to below
   //
   /** Icon for the currently sorted column. */
   private Icon _currentSortedColumnIcon;

   private int _currentlySortedModelIdx = -1;
   //
   /////////////////////////////////////////////////////////////////


   //////////////////////////////////////////////////////////////////////
   // Originating from SortableTableModel TODO: Fix redundancies to above
   //
   /** Column currently being sorted by. -1 means unsorted. */
   private int _sortedColumn = -1;

   private ColumnOrder _columnOrder = ColumnOrder.NATURAL;
   //
   /////////////////////////////////////////////////////////////////


   public TableSortingAdmin()
   {
      LibraryResources rsrc = new LibraryResources();
      _descIcon = rsrc.getIcon(LibraryResources.IImageNames.TABLE_DESCENDING);
      _ascIcon = rsrc.getIcon(LibraryResources.IImageNames.TABLE_ASCENDING);
   }

   public void sort(int modelColumnIx, ColumnOrder columnOrder)
   {
      if (ColumnOrder.ASC == columnOrder)
      {
         _currentSortedColumnIcon = _ascIcon;
      }
      else if (ColumnOrder.DESC == columnOrder)
      {
         _currentSortedColumnIcon = _descIcon;
      }
      else
      {
         _currentSortedColumnIcon = null;
      }
      _currentlySortedModelIdx = modelColumnIx;
   }

   public void sort2(int sortedColumn, ColumnOrder newOrder)
   {
      _sortedColumn = sortedColumn;
      _columnOrder = newOrder;
   }


   public void clear()
   {
      _currentSortedColumnIcon = null;
      _currentlySortedModelIdx = -1;
   }

   // ButtonTableHeader
   public int getCurrentlySortedModelIdx()
   {
      return _currentlySortedModelIdx;
   }

   // ButtonTableHeader
   public Icon getCurrentSortedColumnIcon()
   {
      return _currentSortedColumnIcon;
   }

   public void clearCurrentSortedColumnIcon()
   {
      _currentSortedColumnIcon = null;
   }

   // from SortableTableModel
   public int getSortedColumn()
   {
      return _sortedColumn;
   }

   // from SortableTableModel
   public ColumnOrder getColumnOrder()
   {
      return _columnOrder;
   }
}
