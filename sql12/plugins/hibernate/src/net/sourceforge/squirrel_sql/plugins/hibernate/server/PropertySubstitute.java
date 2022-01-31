package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class PropertySubstitute  implements Serializable
{
   private HibernatePropertyInfo _hibernatePropertyInfo;
   private ArrayList<ObjectSubstitute> _objectSubstituteCollection;
   private Object _plainValue;
   private ObjectSubstitute _objectSubstitute;
   private boolean _initialized;

   PropertySubstitute(HibernatePropertyInfo hibernatePropertyInfo, ObjectSubstitute objectSubstitute, boolean isInitialized)
   {
      _hibernatePropertyInfo = hibernatePropertyInfo;
      _objectSubstitute = objectSubstitute;
      _initialized = isInitialized;
   }

   PropertySubstitute(HibernatePropertyInfo hibernatePropertyInfo, ArrayList<ObjectSubstitute> objectSubstituteCollection, boolean isInitialized)
   {
      _hibernatePropertyInfo = hibernatePropertyInfo;
      _objectSubstituteCollection = objectSubstituteCollection;
      _initialized = isInitialized;
   }

   PropertySubstitute(HibernatePropertyInfo hibernatePropertyInfo, Object plainValue, boolean isInitialized)
   {
      _hibernatePropertyInfo = hibernatePropertyInfo;
      _plainValue = plainValue;

      if(null != plainValue && plainValue.getClass().isEnum())
      {
         _plainValue = getEnumConstantName(plainValue);
      }

      _initialized = isInitialized;
   }

   private String getEnumConstantName(Object plainValue)
   {
      try
      {
         Class enumClass = plainValue.getClass();

         for( Field declaredField : enumClass.getDeclaredFields() )
         {
            declaredField.setAccessible(true);
            if(declaredField.isEnumConstant() && declaredField.get(enumClass) == plainValue )
            {
               return declaredField.getName();
            }
         }

         return "<unknown>";
      }
      catch(Throwable e)
      {
         return "<unknown> " + e;
      }
   }

   public HibernatePropertyInfo getHibernatePropertyInfo()
   {
      return _hibernatePropertyInfo;
   }

   public ArrayList<ObjectSubstitute> getObjectSubstituteCollection()
   {
      return _objectSubstituteCollection;
   }

   public boolean isInitialized()
   {
      return _initialized;
   }

   public Object getSingleValue()
   {
      if(null != _objectSubstituteCollection)
      {
         throw new IllegalStateException("Is a collection");
      }

      if(null == _plainValue)
      {
         return _objectSubstitute;
      }
      else
      {
         return _plainValue;
      }

   }

   public boolean isPersistenCollection()
   {
      return null != _objectSubstituteCollection;

   }

   public boolean isNull()
   {
      return null == _objectSubstituteCollection && null == _objectSubstitute &&  null == _plainValue;
   }
}
