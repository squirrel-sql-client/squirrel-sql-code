package org.squirrelsql.services;


import java.util.ArrayList;
import java.util.List;


public class CollectionUtil
{

   public static interface Criterion<T>{boolean matches(T t);}


   /**
    * Should be inlined when JDK 8 filtering is there.
    */
   public static <T> ArrayList<T> filter(List<T> toFilter, Criterion<T> criterion)
   {
      ArrayList<T> ret = new ArrayList<>();

      for (T t : toFilter)
      {
         if(criterion.matches(t))
         {
            ret.add(t);
         }
      }

      return ret;
   }
}
