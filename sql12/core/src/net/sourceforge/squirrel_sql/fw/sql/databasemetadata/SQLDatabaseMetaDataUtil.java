package net.sourceforge.squirrel_sql.fw.sql.databasemetadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class SQLDatabaseMetaDataUtil
{
   public static String getAsString(Object val)
   {
      if (null == val)
      {
         return null;
      }
      else
      {
         if (val instanceof String)
         {
            return (String) val;
         }
         else
         {
            return "" + val;
         }
      }

   }

   /**
    * Make a String array of the passed string. Commas separate the elements in the input string. The array is
    * sorted.
    *
    * @param data
    *           Data to be split into the array.
    * @return data as an array.
    */
   static String[] makeArray(String data)
   {
      if (data == null)
      {
         data = "";
      }

      final List<String> list = new ArrayList<String>();
      final StringTokenizer st = new StringTokenizer(data, ",");
      while (st.hasMoreTokens())
      {
         list.add(st.nextToken().trim());
      }
      Collections.sort(list);

      return list.toArray(new String[list.size()]);
   }
}
