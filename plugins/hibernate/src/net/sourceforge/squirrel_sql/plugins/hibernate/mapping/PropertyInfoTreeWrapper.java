package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

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
}
