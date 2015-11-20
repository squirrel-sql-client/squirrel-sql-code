package org.squirrelsql.services;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class CollectionUtil
{
   // calling this looks rather complicated.
   public static <T> void forEachFiltered(Collection<T> toVisitFiltered, Predicate<T> predicate, Consumer<T> consumer)
   {
      filter(toVisitFiltered, predicate).forEach(consumer);
   }


   public static <T> List<T> filter(Collection<T> toFilter, Predicate<T> predicate)
   {
	   return toFilter.stream().filter(predicate).collect(Collectors.toList());
   }

   public static <T> List<T> filter(T[] toFilter, Predicate<T> predicate)
   {
	   return filter((List<T>)Arrays.asList(toFilter), predicate);
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

   public static <T, R> List<T>transform(List<R> in, Function<R, T> function)
   {
      List<T> ret = new ArrayList<>();
      for (R r : in)
      {
         ret.add(function.apply(r));
      }
      
      return ret;
   }

   public static <T> String getCommaSeparatedNames(List<T> list, Function<T, String> function)
   {
      return list.stream().map(function).collect(Collectors.joining(","));
   }
}
