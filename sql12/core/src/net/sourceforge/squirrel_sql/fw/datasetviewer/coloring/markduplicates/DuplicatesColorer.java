package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates;

import net.sourceforge.squirrel_sql.fw.util.SquirrelConstants;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


/**
 * Non consecutive duplicate coloring
 */
public class DuplicatesColorer
{
   public static HashMap<Object, Color> getColorByDuplicateValueMap(ValueListReader rdr)
   {
      TreeMap<Integer, Object> duplicateValueByOrderOfOccurrence = new TreeMap<>();

      int orderOfOccurrenceCounter = 0;
      HashMap<Object, Integer> orderOfOccurrenceByValue = new HashMap<>();

      for (int j = 0; j < rdr.size(); ++j)
      {
         Object val = rdr.get(j);

         final Integer orderOfOccurrence = orderOfOccurrenceByValue.get(val);
         if(null != orderOfOccurrence)
         {
            duplicateValueByOrderOfOccurrence.put(orderOfOccurrence, val);
         }
         else
         {
            orderOfOccurrenceByValue.put(val, ++orderOfOccurrenceCounter);
         }
      }

      HashMap<Object, Color> colorByDuplicateValue = new HashMap<>();


      int ix = 0;
      for (Map.Entry<Integer, Object> orderOfOccurrence_value : duplicateValueByOrderOfOccurrence.entrySet())
      {
         if(0 == (++ix) % 2)
         {
            colorByDuplicateValue.put(orderOfOccurrence_value.getValue(), SquirrelConstants.DUPLICATE_COLOR_DARKER);
         }
         else
         {
            colorByDuplicateValue.put(orderOfOccurrence_value.getValue(), SquirrelConstants.DUPLICATE_COLOR);
         }
      }
      return colorByDuplicateValue;
   }
}
