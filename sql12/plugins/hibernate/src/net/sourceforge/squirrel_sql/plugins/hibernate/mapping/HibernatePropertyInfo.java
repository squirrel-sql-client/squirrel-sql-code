package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

public class HibernatePropertyInfo
{
   private String _propertyName;
   private String _typeName;
   private String _toString;

   public HibernatePropertyInfo(String propertyName, String typeName)
   {
      _propertyName = propertyName;
      _typeName = typeName;

      _toString = propertyName + " " + typeName;
   }


   public String getPropertyName()
   {
      return _propertyName;
   }

   public String getTypeName()
   {
      return _typeName;
   }


   public String toString()
   {
      return _toString;
   }
}
