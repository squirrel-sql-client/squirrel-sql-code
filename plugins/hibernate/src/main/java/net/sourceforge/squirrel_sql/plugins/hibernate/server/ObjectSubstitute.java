package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ObjectSubstitute implements Serializable
{
   private HashMap<String, PropertySubstitute> _substituteValueByPropertyName = new HashMap<String, PropertySubstitute>();
   private MappedClassInfoData _mappedClassInfoData;
   private String _toString;

   /**
    * Is seriously meant to be package visible because it must not called nowhere except during initialzation in ObjectSubstituteFactory
    */
   ObjectSubstitute(MappedClassInfoData mappedClassInfoData, String toString)
   {
      _mappedClassInfoData = mappedClassInfoData;
      _toString = toString;
   }

   void putSubstituteValueByPropertyName(String propertyName, PropertySubstitute propertySubstitute)
   {
      _substituteValueByPropertyName.put(propertyName, propertySubstitute);
   }

   public String getClassName()
   {
      return _mappedClassInfoData.getMappedClassName();
   }

   /**
    * @return May return ObjectSubstitute or plain primitive mapped value
    */
   public Object getValue(String propertyName)
   {
      return _substituteValueByPropertyName.get(propertyName).getSingleValue();
   }

   public String getTypeName(String propertyName)
   {
      return _substituteValueByPropertyName.get(propertyName).getHibernatePropertyInfo().getClassName();
   }

   public boolean wasInitialized(String propertyName)
   {
      return _substituteValueByPropertyName.get(propertyName).isInitialized();
   }

   public Collection<? extends ObjectSubstitute> getPersistentCollection(String propertyName)
   {
      return _substituteValueByPropertyName.get(propertyName).getObjectSubstituteCollection();
   }

   public boolean isPersistenCollection(String propertyName)
   {
      return _substituteValueByPropertyName.get(propertyName).isPersistenCollection();
   }

   public boolean isNull(String propertyName)
   {
      return _substituteValueByPropertyName.get(propertyName).isNull();  //To change body of created methods use File | Settings | File Templates.
   }

   public HibernatePropertyInfo getHibernatePropertyInfo(String propertyName)
   {
      return _substituteValueByPropertyName.get(propertyName).getHibernatePropertyInfo();
   }

   @Override
   public String toString()
   {
      return _toString;
   }
}
