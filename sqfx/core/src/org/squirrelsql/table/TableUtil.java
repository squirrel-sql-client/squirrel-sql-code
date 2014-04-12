package org.squirrelsql.table;

import javafx.beans.property.SimpleObjectProperty;

import java.util.ArrayList;

public class TableUtil
{
   public static ArrayList<SimpleObjectProperty> createSimpleObjectPropertyRow(Object[] row)
   {
      ArrayList<SimpleObjectProperty> buf = new ArrayList<>();

      for (Object o : row)
      {
         buf.add(new SimpleObjectProperty(o));
      }
      return buf;
   }

   public static ArrayList<SimpleObjectProperty> createSimpleObjectPropertyRow(ArrayList row)
   {
      return createSimpleObjectPropertyRow(row.toArray(new Object[row.size()]));
   }
}
