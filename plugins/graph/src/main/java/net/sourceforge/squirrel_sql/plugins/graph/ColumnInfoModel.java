package net.sourceforge.squirrel_sql.plugins.graph;

import java.util.ArrayList;
import java.util.Arrays;

public class ColumnInfoModel
{
   private ColumnInfo[] _colInfos = new ColumnInfo[0];
   private ColumnInfo[] _orderedColumnInfos  = new ColumnInfo[0];
   private ArrayList<ColumnInfoModelListener> _listeners = new ArrayList<ColumnInfoModelListener>();
   private ColumnInfoModelEventDispatcher _columnInfoModelEventDispatcher;


   public ColumnInfoModel()
   {
      _columnInfoModelEventDispatcher = new ColumnInfoModelEventDispatcher()
      {
         @Override
         public void fireChanged(TableFramesModelChangeType changeType)
         {
            ColumnInfoModel.this.fireChanged(changeType);
         }
      };
   }

   public int getColCount()
   {
      return _colInfos.length;
   }

   public ColumnInfo getColAt(int ix)
   {
      return _colInfos[ix];
   }

   public ColumnInfo[] getAll()
   {
      return _colInfos;
   }

   public void orderBy(OrderType orderType)
   {
      if(OrderType.ORDER_DB == orderType)
      {
         _orderedColumnInfos = _colInfos;
      }
      else
      {
         _orderedColumnInfos = new ColumnInfo[_colInfos.length];
         System.arraycopy(_colInfos, 0, _orderedColumnInfos, 0, _colInfos.length);
         Arrays.sort(_orderedColumnInfos, orderType.getComparator());
      }

      for (int i = 0; i < _orderedColumnInfos.length; i++)
      {
         _orderedColumnInfos[i].setIndex(i);
      }

      fireChanged(TableFramesModelChangeType.COLUMN_SORTING);
   }

   public ColumnInfo findColumnInfo(String colName)
   {
      return GraphUtil.findColumnInfo(colName, _colInfos);
   }

   public ColumnInfo getOrderedColAt(int ix)
   {
      return _orderedColumnInfos[ix];
   }

   public void addColumnInfoModelListener(ColumnInfoModelListener listener)
   {
      _listeners.add(listener);
   }

   public void querySelectAll(boolean b)
   {
      for (ColumnInfo colInfo : _colInfos)
      {
         colInfo.getQueryData().setInSelectClause(b);
      }

      fireChanged(TableFramesModelChangeType.COLUMN_SELECT_ALL);
   }


   public void clearAllFilters()
   {
      for (ColumnInfo colInfo : _colInfos)
      {
         colInfo.getQueryData().clearFilter();
      }

      fireChanged(TableFramesModelChangeType.COLUMN_WHERE_ALL);
   }

   private void fireChanged(TableFramesModelChangeType changeType)
   {
      ColumnInfoModelListener[] listeners = _listeners.toArray(new ColumnInfoModelListener[_listeners.size()]);

      for (ColumnInfoModelListener listener : listeners)
      {
         listener.columnInfosChanged(changeType);
      }
   }

   public void initCols(ColumnInfo[] refreshedCols, OrderType columnOrderType)
   {
      for (ColumnInfo colInfoNew : refreshedCols)
      {
         for (ColumnInfo colInfoOld : _colInfos)
         {
            if(colInfoNew.getColumnName().equalsIgnoreCase(colInfoOld.getColumnName()))
            {
               colInfoNew.setQueryData(colInfoOld.getQueryData());
            }
         }

         colInfoNew.setColumnInfoModelEventDispatcher(_columnInfoModelEventDispatcher);
      }

      _colInfos = refreshedCols;

      orderBy(columnOrderType);
   }
}
