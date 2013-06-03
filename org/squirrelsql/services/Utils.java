package org.squirrelsql.services;

public class Utils
{
   public static String changeFileNameToClassName(String name)
   {
      if (name == null)
      {
         throw new IllegalArgumentException("File Name == null");
      }
      String className = null;
      if (name.toLowerCase().endsWith(".class"))
      {
         className = name.replace('/', '.');
         className = className.replace('\\', '.');
         className = className.substring(0, className.length() - 6);
      }
      return className;
   }
}
