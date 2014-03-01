package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PropertyAccessor
{
   private Field _field;
   private Method _method;

   private PropertyAccessor(Field field)
   {
      _field = field;
      _field.setAccessible(true);
   }

   private PropertyAccessor(Method method)
   {
      _method = method;
      _method.setAccessible(true);
   }

   static PropertyAccessor createAccessor(Class<? extends Object> clazz, String propertyName)
   {
      if(null == clazz || clazz.equals(Object.class))
      {
         return null;
      }

      try
      {
         Field f = clazz.getDeclaredField(propertyName);
         return new PropertyAccessor(f);
      }
      catch (NoSuchFieldException nsfe)
      {
         try
         {
            Method m = clazz.getDeclaredMethod(toGetter(propertyName, false));
            return new PropertyAccessor(m);
         }
         catch (NoSuchMethodException nsme)
         {
            try
            {
               Method m = clazz.getDeclaredMethod(toGetter(propertyName, true));
               return new PropertyAccessor(m);
            }
            catch (NoSuchMethodException e)
            {
               return createAccessor(clazz.getSuperclass(), propertyName);
            }
         }
      }
   }

   private static String toGetter(String propertyName, boolean isBoolean)
   {
      String ret = Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);

      if(isBoolean)
      {
         return "is" + ret;
      }
      else
      {
         return "get" + ret;
      }
   }

   Object get(Object obj)
   {
      try
      {
         if(null != _field)
         {
            return _field.get(obj);
         }
         else
         {
            return _method.invoke(obj, new Object[0]);
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   Class getType()
   {
      if(null != _field)
      {
         return _field.getType();
      }
      else
      {
         return _method.getReturnType();
      }
   }
}
