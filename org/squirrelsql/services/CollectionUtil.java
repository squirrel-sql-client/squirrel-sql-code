package org.squirrelsql.services;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;


public class CollectionUtil
{
   public static <T> ArrayList<T> filter(List<T> toFilter, Predicate<T> predicate)
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
}
