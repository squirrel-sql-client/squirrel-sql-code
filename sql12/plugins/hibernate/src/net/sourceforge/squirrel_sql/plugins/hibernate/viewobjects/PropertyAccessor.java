package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PropertyAccessor
{
   private Field _field;
   private Method _method;

   public PropertyAccessor(Field field)
   {
      _field = field;
      _field.setAccessible(true);
   }

   public PropertyAccessor(Method method)
   {
      _method = method;
      _method.setAccessible(true);
   }

   public Object get(Object obj)
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

   public Class getType()
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
