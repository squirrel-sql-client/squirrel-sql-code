package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

public class HibernatePropertyInfo
{
   private String _propertyName;
   private String _className;
   private String _toString;
   private String _collectionClassName;

   public HibernatePropertyInfo(String propertyName, String className, String collectionClassName)
   {
      _propertyName = propertyName;
      _className = className;
      _collectionClassName = collectionClassName;

      if(null != _collectionClassName)
      {
         _toString = propertyName + " " + _collectionClassName + "<" + className + ">";
      }
      else
      {
         _toString = propertyName + " " + className;
      }
   }


   public String getPropertyName()
   {
      return _propertyName;
   }

   public String getClassName()
   {
      return _className;
   }


   public String toString()
   {
      return _toString;
   }

   public String getCollectionClassName()
   {
      return _collectionClassName;
   }
}
