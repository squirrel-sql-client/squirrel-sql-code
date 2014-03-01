package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.plugins.hibernate.server.HibernatePropertyInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ObjectSubstitute;
import net.sourceforge.squirrel_sql.plugins.hibernate.util.HibernateUtil;

import java.util.Collection;

public class HibernatePropertyReader
{
   private String _propertyName;
   private ObjectSubstitute _obj;

   public HibernatePropertyReader(String propertyName, ObjectSubstitute obj)
   {
      _propertyName = propertyName;
      _obj = obj;
   }

   public String getName()
   {
      return _propertyName;
   }


   public Object getValue()
   {
      if (null == _obj)
      {
         return HibernateUtil.OBJECT_IS_NULL;
      }
      else
      {
         return _obj.getValue(_propertyName);
      }
   }

   public String getTypeName()
   {

      if (null == _obj)
      {
         return HibernateUtil.OBJECT_IS_NULL;
      }
      else
      {
         return _obj.getTypeName(_propertyName);
      }
   }

   public boolean wasInitialized()
   {
      if (null == _obj)
      {
          return true;
      }
      else
      {
         return _obj.wasInitialized(_propertyName);
      }
   }

   public boolean isPersistenCollection()
   {
      if (null == _obj)
      {
         return false;
      }
      else
      {
         return _obj.isPersistenCollection(_propertyName);
      }
   }

   public Collection<? extends ObjectSubstitute> getPersistentCollection()
   {
      if (null == _obj)
      {
         return null;
      }
      else
      {
         return _obj.getPersistentCollection(_propertyName);
      }
   }

   public boolean isNull()
   {
      if (null == _obj)
      {
         return false;
      }
      else
      {
         return _obj.isNull(_propertyName);
      }
   }

   public HibernatePropertyInfo getHibernatePropertyInfo()
   {
      return _obj.getHibernatePropertyInfo(_propertyName);
   }
}
