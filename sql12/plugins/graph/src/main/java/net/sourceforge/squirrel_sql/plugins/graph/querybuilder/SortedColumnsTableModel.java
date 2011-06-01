package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SortedColumnsTableModel <T extends SortedColumn> extends DefaultTableModel
{

   private ArrayList<T> _sortedCols = new ArrayList<T>();


   public void updateOrderCols(ArrayList<T> newSortedCols)
   {
      ArrayList<T> toRemove = new ArrayList<T>();
      ArrayList<T> toAdd = new ArrayList<T>();

      for (T newSortedCol : newSortedCols)
      {
         boolean found = false;
         for (T sortedCol : _sortedCols)
         {
            if(sortedCol.equals(newSortedCol))
            {
               updateCol(sortedCol, newSortedCol);
               found = true;
               break;
            }
         }

         if(false == found)
         {
            toAdd.add(newSortedCol);
         }
      }

      for (T sortedCol : _sortedCols)
      {
         boolean found = false;
         for (T newOrderCol : newSortedCols)
         {
            if(sortedCol.equals(newOrderCol))
            {
               updateCol(sortedCol, newOrderCol);
               found = true;
               break;
            }
         }

         if(false == found)
         {
            toRemove.add(sortedCol);
         }
      }

      _sortedCols.removeAll(toRemove);
      _sortedCols.addAll(toAdd);

      fireTableDataChanged();
   }

   protected abstract void updateCol(T toBeUpdated, T update);


   public int[] moveUp(int[] selRows)
   {
      for (int i : selRows)
      {
         if (0 == i)
         {
            return selRows;
         }
      }

      int[] newSelRows = new int[selRows.length];
      for (int i = 0; i < selRows.length; ++i)
      {
         T col = _sortedCols.remove(selRows[i]);
         newSelRows[i] = selRows[i] - 1;
         _sortedCols.add(newSelRows[i], col);
      }

      return newSelRows;
   }

   public int[] moveDown(int[] selRows)
   {
      for (int i : selRows)
      {
         if (_sortedCols.size() - 1 == i)
         {
            return selRows;
         }
      }

      int[] newSelIx = new int[selRows.length];
      for (int i = selRows.length - 1; i >= 0; --i)
      {
         T col = _sortedCols.remove(selRows[i]);
         newSelIx[i] = selRows[i] + 1;
         _sortedCols.add(newSelIx[i], col);
      }

      return newSelIx;
   }

   public T[] getSortedCols(Class<? extends T> c)
   {
      return _sortedCols.toArray((T[])Array.newInstance(c,0));
   }

   protected void addCols(List<T> orderCols)
   {
      _sortedCols.addAll(orderCols);
   }

   protected T getSortedCol(int ix)
   {
      return _sortedCols.get(ix);
   }

   @Override
   public int getRowCount()
   {
      if(null == _sortedCols)
      {
         // This if is here because the method is called from the base class constructor.
         return 0;
      }

      return _sortedCols.size();
   }


   public abstract TableColumnModel getColumnModel();
}
