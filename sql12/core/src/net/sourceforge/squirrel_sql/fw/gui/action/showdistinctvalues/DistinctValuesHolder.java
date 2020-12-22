package net.sourceforge.squirrel_sql.fw.gui.action.showdistinctvalues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DistinctValuesHolder
{
   private HashMap<Integer, DistinctValuesData> _colIx_distinctValuesData = new HashMap<>();

   private boolean _nullsAdded;

   public void addDistinct(int colIx, Object value)
   {
      DistinctValuesData distinctValuesData = _colIx_distinctValuesData.computeIfAbsent(colIx, ix -> new DistinctValuesData());

      if(false == distinctValuesData.distinctCheck.contains(value))
      {
         distinctValuesData.distinctValuesInSourceTableOrder.add(value);
         distinctValuesData.distinctCheck.add(value);
      }
   }

   public List<Object[]> getDistinctRows()
   {
      ArrayList<Object[]> ret = new ArrayList<>();


      for (int rowIx = 0; rowIx < getMaxDistinctValues(); rowIx++)
      {
         Object[] row = new Object[_colIx_distinctValuesData.size()];

         for (int colIx = 0; colIx < _colIx_distinctValuesData.size(); colIx++)
         {
            if(rowIx < _colIx_distinctValuesData.get(colIx).distinctValuesInSourceTableOrder.size())
            {
               row[colIx] = _colIx_distinctValuesData.get(colIx).distinctValuesInSourceTableOrder.get(rowIx);
            }
            else
            {
               _nullsAdded = true;
            }
         }

         ret.add(row);
      }

      return ret;
   }

   private int getMaxDistinctValues()
   {
      return _colIx_distinctValuesData.values().stream().mapToInt( d -> d.distinctValuesInSourceTableOrder.size()).max().getAsInt();
   }

   public int getCountDistinctForColumn(int colIx)
   {
      return _colIx_distinctValuesData.get(colIx).distinctValuesInSourceTableOrder.size();
   }

   private static class DistinctValuesData
   {
      ArrayList<Object> distinctValuesInSourceTableOrder = new ArrayList();
      HashSet<Object> distinctCheck = new HashSet<>();
   }

   public boolean isNullsAdded()
   {
      return _nullsAdded;
   }
}
