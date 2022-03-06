package net.sourceforge.squirrel_sql.fw.props;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * See answer 13 at
 * https://stackoverflow.com/questions/10275862/how-to-sort-properties-in-java
 */
class SortedProperties extends Properties
{
   @Override
   public Set<Object> keySet()
   {
      return Collections.unmodifiableSet(new TreeSet<>(super.keySet()));
   }


   @Override
   public Set<Map.Entry<Object, Object>> entrySet()
   {
      Set<Map.Entry<Object, Object>> set1 = super.entrySet();
      Set<Map.Entry<Object, Object>> set2 = new LinkedHashSet<>(set1.size());

      Iterator<Map.Entry<Object, Object>> iterator = set1.stream().sorted(Comparator.comparing(o -> o.getKey().toString())).iterator();

      while (iterator.hasNext())
      {
         set2.add(iterator.next());
      }

      return set2;
   }

   @Override
   public synchronized Enumeration<Object> keys()
   {
      return Collections.enumeration(new TreeSet<>(super.keySet()));
   }

}


