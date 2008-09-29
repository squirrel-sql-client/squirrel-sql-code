package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import java.util.Hashtable;

public class PropertyInfoTreeWrapper
{
   private PropertyInfo _propertyInfo;
   private MappedClassInfo _mappedClassInfo;

   public PropertyInfoTreeWrapper(PropertyInfo propertyInfo, MappedClassInfo mappedClassInfo)
   {
      _propertyInfo = propertyInfo;
      _mappedClassInfo = mappedClassInfo;
   }

   public MappedClassInfo getMappedClassInfo()
   {
      return _mappedClassInfo;
   }


   public String toString()
   {
      return _propertyInfo.toString();
   }

   public Hashtable<String, String> getMappingProperties()
   {
      Hashtable<String, String> ret = new Hashtable<String, String>();

      if(null != _mappedClassInfo)
      {
         ret.put("Mapped class", _mappedClassInfo.getClassName());
      }

      if(null != _propertyInfo.getHibernatePropertyInfo())
      {
         ret.put("Qualified name", _propertyInfo.getHibernatePropertyInfo().toString());
         ret.put("Property name", _propertyInfo.getHibernatePropertyInfo().getPropertyName());
      }

      return ret;
   }
}
