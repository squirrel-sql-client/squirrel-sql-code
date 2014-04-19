package org.squirrelsql.services;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;


public class CollectionUtil
{
   // calling this looks rather complicated.
   public static <T> void forEachFiltered(Collection<T> toVisitFiltered, Predicate<T> predicate, Consumer<T> consumer)
   {
      filter(toVisitFiltered, predicate).forEach(consumer);
   }


   public static <T> ArrayList<T> filter(Collection<T> toFilter, Predicate<T> predicate)
   {
      Stream<T> stream = toFilter.stream().filter(predicate);
      return new ArrayList(Arrays.asList(stream.toArray()));
   }

   public static <T>boolean contains(T[] toFilter, Predicate<T> predicate)
   {
      return contains((List<T>)Arrays.asList(toFilter), predicate);
   }

   public static <T>boolean contains(List<T> toFilter, Predicate<T> predicate)
   {
      return toFilter.stream().anyMatch(predicate);
   }


   public static <T> void removeAll(Collection<T> toRemoveFrom, Predicate<T> predicate)
   {
      toRemoveFrom.removeIf(predicate);
   }
}
