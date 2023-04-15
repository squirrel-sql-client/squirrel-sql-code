package net.sourceforge.squirrel_sql.fw.gui.table;

import net.sourceforge.squirrel_sql.client.Main;

import javax.swing.table.TableModel;
import java.util.Comparator;

class TableModelComparator implements Comparator<Integer>
{
   private final TableModel _actualModel;
   private final TableSortingAdmin _tableSortingAdmin;
   private final SquirrelTableCellValueCollator _collator = new SquirrelTableCellValueCollator();

   private boolean _nullIsHighest;

   public TableModelComparator(TableModel actualModel, TableSortingAdmin tableSortingAdmin)
   {
      _actualModel = actualModel;
      _tableSortingAdmin = tableSortingAdmin;
      _nullIsHighest = isSortNullsAsHighestValue();

      for (TableSortingItem tableSortingItem : tableSortingAdmin.getTableSortingItems())
      {
         tableSortingItem.setAllDataString(true);
         tableSortingItem.initAscendingInt();
         for (int i = 0, limit = _actualModel.getRowCount(); i < limit; ++i)
         {
            final Object data = _actualModel.getValueAt(i, tableSortingItem.getSortedModelColumn());
            if(!(data instanceof String))
            {
               tableSortingItem.setAllDataString(false);
               break;
            }
         }
      }
   }

   public int compare(final Integer i1, final Integer i2)
   {
      for (TableSortingItem tableSortingItem : _tableSortingAdmin.getTableSortingItems())
      {
         final Object data1 = _actualModel.getValueAt(i1, tableSortingItem.getSortedModelColumn());
         final Object data2 = _actualModel.getValueAt(i2, tableSortingItem.getSortedModelColumn());
         int res = _collator.compareTableCellValues(data1, data2, tableSortingItem.getAscendingInt(), tableSortingItem.isAllDataString(), _nullIsHighest);

         if(0 != res)
         {
            return res;
         }
      }

      return 0;
   }


   public boolean isSortNullsAsHighestValue()
   {
      boolean nullIsHighest;
      if(null != Main.getApplication().getSessionManager().getActiveSession())
      {
         nullIsHighest = Main.getApplication().getSessionManager().getActiveSession().getProperties().isSortNullsAsHighestValue();
      }
      else
      {
         nullIsHighest = Main.getApplication().getSquirrelPreferences().getSessionProperties().isSortNullsAsHighestValue();
      }
      return nullIsHighest;
   }

}
