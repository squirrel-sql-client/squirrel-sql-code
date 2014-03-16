package org.squirrelsql.services;

import com.google.common.base.Strings;
import org.squirrelsql.session.SQLResult;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Utils
{
   public static boolean isEmptyString(String text)
   {
      return Strings.isNullOrEmpty(text) || Strings.isNullOrEmpty(text.trim());
   }

   public static boolean compareRespectEmpty(String s1, String s2)
   {
      if(null == s1 && null == s2)
      {
         return true;
      }

      if(s1 == null)
      {
         return false;
      }

      return s1.equalsIgnoreCase(s2);
   }

   public static void close(ResultSet res)
   {
      if(null == res)
      {
         return;
      }

      try
      {
         res.close();
      }
      catch(Throwable t)
      {
      }
   }

   public static void close(Statement stat)
   {
      if(null == stat)
      {
         return;
      }

      try
      {
         stat.close();
      }
      catch(Throwable t)
      {
      }
   }

   public static <T> ArrayList<T> asArray(T ... ts)
   {
      ArrayList<T> ret = new ArrayList<>();

      for (T t : ts)
      {
         ret.add(t);
      }
      return ret;
   }
}
