package net.sourceforge.squirrel_sql.fw.gui.table;

import javax.swing.*;

@FunctionalInterface
public interface TableAccessForHeader
{
   JTable getTable();

   default TableSortingAdmin getTableSortingAdmin()
   {
      if (getTable().getModel() instanceof SortableTableModel)
      {
         return ((SortableTableModel)getTable().getModel()).getTableSortingAdmin();
      }
      else
      {
         // Dummy
         return new TableSortingAdmin();
      }
   }

}
