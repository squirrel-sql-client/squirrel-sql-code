package net.sourceforge.squirrel_sql.fw.gui.action.showdistinctvalues;

import java.util.ArrayList;
import java.util.HashMap;

class DistinctValuesData
{
   private ArrayList<Object> distinctValuesInSourceTableOrder = new ArrayList();
   private HashMap<Object, Integer> distinctValuesCount = new HashMap<>();

   public void addUnique(Object value)
   {
      if(false == distinctValuesCount.containsKey(value))
      {
         distinctValuesInSourceTableOrder.add(value);
         distinctValuesCount.put(value, 1);
      }
      else
      {
         distinctValuesCount.put(value, distinctValuesCount.get(value) + 1);
      }
   }

   public int size()
   {
      return distinctValuesInSourceTableOrder.size();
   }

   public Object getValue(int rowIx)
   {
      return distinctValuesInSourceTableOrder.get(rowIx);
   }

   public Integer getValueCount(int rowIx)
   {
      return distinctValuesCount.get(distinctValuesInSourceTableOrder.get(rowIx));
   }
}
