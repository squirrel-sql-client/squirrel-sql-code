package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.File;
import java.util.ArrayList;

public class ClassPathUtil
{
   public static String classPathToString(ClassPathItem[] classPathItems)
   {
      String[] paths = classPathAsStringArray(classPathItems);

      if(0 == paths.length)
      {
         return "";
      }

      String ret = paths[0];

      for (int i = 1; i < paths.length; i++)
      {
         ret += File.pathSeparator + paths[i];
      }

      return ret;
   }

   private static ArrayList<String> _genStringArray(ClassPathItem classPathItem)
   {
      ArrayList<String> ret = new ArrayList<String>();
      if (classPathItem.isJarDir())
      {
         File[] files = new File(classPathItem.getPath()).listFiles();

         if(null != files)
         {
            for (File file : files)
            {
               ret.add(file.getPath());
            }
         }
      }
      else
      {
         ret.add(classPathItem.getPath());
      }

      return ret;
   }

   public static String[] classPathAsStringArray(ClassPathItem[] classPathItems)
   {
      ArrayList<String> ret = new ArrayList<String>();
      for (int i = 0; i < classPathItems.length; i++)
      {
         ret.addAll(_genStringArray(classPathItems[i]));
      }

      return ret.toArray(new String[ret.size()]);
   }
}
