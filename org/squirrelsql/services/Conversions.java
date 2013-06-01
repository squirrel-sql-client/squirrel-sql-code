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
}
