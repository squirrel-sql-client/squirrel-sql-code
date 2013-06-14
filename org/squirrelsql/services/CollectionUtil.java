package org.squirrelsql.services;


import org.squirrelsql.drivers.SQLDriver;

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
}
