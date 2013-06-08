package org.squirrelsql.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Conversions
{
   public static ArrayList<String> toPathString(List<File> files)
   {
      ArrayList<String> ret = new ArrayList<>();

      for (File file : files)
      {
         ret.add(file.getAbsolutePath());
      }

      return ret;

   }

   public static Integer[] toIntegers(int[] ints)
   {
      Integer[] ret = new Integer[ints.length];

      for (int i = 0; i < ret.length; i++)
      {
         ret[i] = ints[i];
      }

      return ret;
   }

   public static int[] toInts(Integer[] integers)
   {
      int[] ret = new int[integers.length];

      for (int i = 0; i < ret.length; i++)
      {
         ret[i] = integers[i];
      }


      return ret;
   }

   public static String changeFileNameToClassName(String name)
   {
      String className = null;
      if (name.toLowerCase().endsWith(".class"))
      {
         className = name.replace('/', '.');
         className = className.replace('\\', '.');
         className = className.substring(0, className.length() - 6);
      }
      return className;
   }


   public static interface NameExtractor<T>{String getName(T t);}

   public static <U> ArrayList<String> toNames(ArrayList<U> us, NameExtractor<U> nameExtractor)
   {
      ArrayList<String> ret = new ArrayList<>();

      for (U u : us)
      {
         ret.add(nameExtractor.getName(u));
      }

      return ret;
   }
}
