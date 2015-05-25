package org.squirrelsql.table.tableedit;

import org.squirrelsql.services.Utils;
import org.squirrelsql.table.TableLoader;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class StringInterpreter
{
   public static Object interpret(String userEnteredString, String columnClassName) throws ClassNotFoundException, ParseException
   {
      if(Utils.isEmptyString(userEnteredString) || TableLoader.NULL_AS_STRING.equals(userEnteredString))
      {
         return TableLoader.NULL_AS_MARKER;
      }

      Class clazz = Class.forName(columnClassName);

      return _interpret(userEnteredString, clazz);
   }

   public static Object interpret(String userEnteredString, Class clazz)
   {
      try
      {
         return _interpret(userEnteredString, clazz);
      }
      catch (ParseException e)
      {
         throw new RuntimeException(e);
      }
   }


   private static Object _interpret(String userEnteredString, Class clazz) throws ParseException
   {
      if(Utils.isEmptyString(userEnteredString))
      {
         return null;
      }

      if(String.class.equals(clazz))
      {
         return userEnteredString;
      }

      if(Integer.class.equals(clazz))
      {
         return Integer.valueOf(userEnteredString);
      }

      if(Integer.class.equals(clazz))
      {
         return Integer.valueOf(userEnteredString);
      }

      if(Long.class.equals(clazz))
      {
         return Integer.valueOf(userEnteredString);
      }

      if(BigInteger.class.equals(clazz))
      {
         return new BigInteger(userEnteredString);
      }

      if(Short.class.equals(clazz))
      {
         return Short.valueOf(userEnteredString);
      }

      if(Boolean.class.equals(clazz))
      {
         try
         {
            Integer intVal = Integer.valueOf(userEnteredString);
            return !intVal.equals(0);
         }
         catch (NumberFormatException e)
         {
         }

         return Boolean.valueOf(userEnteredString);
      }

      if(Byte.class.equals(clazz))
      {
         return Byte.valueOf(userEnteredString);
      }

      if(Character.class.equals(clazz))
      {
         return Character.valueOf(userEnteredString.charAt(0));
      }

      if(Timestamp.class.equals(clazz))
      {
         String pattern = "yyyy-MM-dd HH:mm:ss";

         String buf = userEnteredString.trim().substring(0, pattern.length());

         return new Timestamp(new SimpleDateFormat(pattern).parse(buf).getTime());
      }

      if(Time.class.equals(clazz))
      {
         String pattern = "HH:mm:ss";

         String buf = userEnteredString.trim().substring(0, pattern.length());

         return new Time(new SimpleDateFormat(pattern).parse(buf).getTime());
      }

      if(java.sql.Date.class.equals(clazz))
      {
         String pattern = "yyyy-MM-dd";

         String buf = userEnteredString.trim().substring(0, pattern.length());

         return new java.sql.Date(new SimpleDateFormat(pattern).parse(buf).getTime());
      }

      if(Float.class.equals(clazz))
      {
         return Float.valueOf(userEnteredString);
      }

      if(Double.class.equals(clazz))
      {
         return Double.valueOf(userEnteredString);
      }

      if(BigDecimal.class.equals(clazz))
      {
         return new BigDecimal(userEnteredString);
      }


      return userEnteredString;
   }

   public static <T>  T interpret(String buf, Class<T> clazz, T defaultValue)
   {
      try
      {
         return (T)_interpret(buf, clazz);
      }
      catch (ParseException| NumberFormatException e)
      {
         return defaultValue;
      }
   }

   public static <T>  T interpretNonNull(String buf, Class<T> clazz, T defaultValue)
   {
      T ret = interpret(buf, clazz, defaultValue);

      if(null == ret)
      {
         return defaultValue;
      }

      return ret;
   }
}
