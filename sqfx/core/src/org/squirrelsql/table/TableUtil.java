package org.squirrelsql.table;

import javafx.beans.property.SimpleObjectProperty;

import java.util.ArrayList;
import java.util.List;

public class TableUtil
{
   public static List<SimpleObjectProperty> createSimpleObjectPropertyRow(Object[] row)
   {
      List<SimpleObjectProperty> buf = new ArrayList<>();

      for (Object o : row)
      {
         buf.add(new SimpleObjectProperty(o));
      }
      return buf;
   }

   public static List<SimpleObjectProperty> createSimpleObjectPropertyRow(List row)
   {
      return createSimpleObjectPropertyRow(row.toArray(new Object[row.size()]));
   }
}
