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
}
