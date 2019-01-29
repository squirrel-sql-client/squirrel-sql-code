package net.sourceforge.squirrel_sql.fw.props;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class StringInterpreter
{

   public static <T>  T interpret(String value, Class<T> clazz, T defaultValue)
   {
      return interpret(value, clazz, defaultValue, false);
   }

   public static <T>  T interpret(String value, Class<T> clazz, T defaultValue, boolean allowWhiteSpacesOnly)
   {
      try
      {
         return (T)_interpret(value, clazz, defaultValue, allowWhiteSpacesOnly);
      }
      catch (ParseException| NumberFormatException e)
      {
         return defaultValue;
      }
   }


   private static Object _interpret(String value, Class clazz, Object defaultValue, boolean allowWhiteSpacesOnly) throws ParseException
   {
      if(StringUtilities.isEmpty(value, false == allowWhiteSpacesOnly))
      {
         return defaultValue;
      }

      if(String.class.equals(clazz))
      {
         return value;
      }

      if(Integer.class.equals(clazz))
      {
         return Integer.valueOf(value);
      }

      if(Long.class.equals(clazz))
      {
         return Integer.valueOf(value);
      }

      if(BigInteger.class.equals(clazz))
      {
         return new BigInteger(value);
      }

      if(Short.class.equals(clazz))
      {
         return Short.valueOf(value);
      }

      if(Boolean.class.equals(clazz))
      {
         try
         {
            Integer intVal = Integer.valueOf(value);
            return !intVal.equals(0);
         }
         catch (NumberFormatException e)
         {
         }

         return Boolean.valueOf(value);
      }

      if(Byte.class.equals(clazz))
      {
         return Byte.valueOf(value);
      }

      if(Character.class.equals(clazz))
      {
         return Character.valueOf(value.charAt(0));
      }

      if(Timestamp.class.equals(clazz))
      {
         String pattern = "yyyy-MM-dd HH:mm:ss";

         String buf = value.trim().substring(0, pattern.length());

         return new Timestamp(new SimpleDateFormat(pattern).parse(buf).getTime());
      }

      if(Time.class.equals(clazz))
      {
         String pattern = "HH:mm:ss";

         String buf = value.trim().substring(0, pattern.length());

         return new Time(new SimpleDateFormat(pattern).parse(buf).getTime());
      }

      if(java.sql.Date.class.equals(clazz))
      {
         String pattern = "yyyy-MM-dd";

         String buf = value.trim().substring(0, pattern.length());

         return new java.sql.Date(new SimpleDateFormat(pattern).parse(buf).getTime());
      }

      if(Float.class.equals(clazz))
      {
         return Float.valueOf(value);
      }

      if(Double.class.equals(clazz))
      {
         return Double.valueOf(value);
      }

      if(BigDecimal.class.equals(clazz))
      {
         return new BigDecimal(value);
      }


      return value;
   }
}
