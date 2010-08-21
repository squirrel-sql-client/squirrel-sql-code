package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects.PropertyAccessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class HibernatePropertyReader
{
   private String _propertyName;
   private Object _obj;
   private PropertyAccessor _propertyAccessor;

   public HibernatePropertyReader(String propertyName, Object obj)
   {
      _propertyName = propertyName;
      _obj = obj;
      _propertyAccessor = getAccessor(_obj.getClass());
   }

   public String getName()
   {
      return _propertyName;
   }


   public Object getValue()
   {
      try
      {
         return _propertyAccessor.get(_obj);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private PropertyAccessor getAccessor(Class<? extends Object> clazz)
   {
      if(null == clazz || clazz.equals(Object.class))
      {
         return null;
      }

      try
      {
         Field f = clazz.getDeclaredField(_propertyName);
         return new PropertyAccessor(f);
      }
      catch (NoSuchFieldException nsfe)
      {
         try
         {
            Method m = clazz.getDeclaredMethod(toGetter(_propertyName, false));
            return new PropertyAccessor(m);
         }
         catch (NoSuchMethodException nsme)
         {
            try
            {
               Method m = clazz.getDeclaredMethod(toGetter(_propertyName, true));
               return new PropertyAccessor(m);
            }
            catch (NoSuchMethodException e)
            {
               return getAccessor(clazz.getSuperclass());
            }
         }
      }
   }

   private String toGetter(String propertyName, boolean isBoolean)
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

   public String getTypeName()
   {
      return _propertyAccessor.getType().getName();
   }

   public Class getType()
   {
      return _propertyAccessor.getType();
   }


}
