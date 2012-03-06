package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ObjectSubstitute implements Serializable
{
   private HashMap<String, PropertySubstitute> _substituteValueByPropertyName = new HashMap<String, PropertySubstitute>();
   private MappedClassInfoData _mappedClassInfoData;
   private String _toString;


   public static final String PLAIN_VALUES = "<plain values>";
   private ArrayList<Object> _plainValueArray;
   private HashMap<String, PlainValue> _plainValueByPropertyName = new HashMap<String, PlainValue>();

   /**
    * Is seriously meant to be package visible because it must not called nowhere except during initialzation in ObjectSubstituteFactory
    */
   ObjectSubstitute(MappedClassInfoData mappedClassInfoData, String toString)
   {
      _mappedClassInfoData = mappedClassInfoData;
      _toString = toString;
   }

   ObjectSubstitute(ArrayList<Object> plainValueArray)
   {
      _plainValueArray = plainValueArray;
      _toString = PLAIN_VALUES + "[" + plainValueArray.size() + "]";


      String className = null == plainValueArray.get(0) ? "<unknown>" : plainValueArray.get(0).getClass().getName();
      String propertyName = "value " + (1);
      HibernatePropertyInfo indentifierHibernatePropertyInfo = new HibernatePropertyInfo(propertyName, className, "<unknown>", new String[]{"<unknown>"});

      _plainValueByPropertyName.put(propertyName, new PlainValue(plainValueArray.get(0), indentifierHibernatePropertyInfo));

      HibernatePropertyInfo[] hibernatePropertyInfos = new HibernatePropertyInfo[plainValueArray.size() - 1];
      for (int i = 1; i < plainValueArray.size(); i++)
      {
         className = null == plainValueArray.get(i) ? "<unknown>" : plainValueArray.get(i).getClass().getName();
         propertyName = "value " + (i + 1);
         hibernatePropertyInfos[i-1] = new HibernatePropertyInfo(propertyName, className, "<unknown>", new String[]{"<unknown>"});

         _plainValueByPropertyName.put(propertyName, new PlainValue(plainValueArray.get(i), hibernatePropertyInfos[i-1]));
      }


      _mappedClassInfoData = new MappedClassInfoData(PLAIN_VALUES, "<unknown>", indentifierHibernatePropertyInfo, hibernatePropertyInfos);
      _mappedClassInfoData.setPlainValueArray(true);
   }

   void putSubstituteValueByPropertyName(String propertyName, PropertySubstitute propertySubstitute)
   {
      if (null == _plainValueArray)
      {
         _substituteValueByPropertyName.put(propertyName, propertySubstitute);
      }
      else
      {
         throw new IllegalStateException("Should not be called for plain values");
      }
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
      if (null == _plainValueArray)
      {
         return _substituteValueByPropertyName.get(propertyName).getSingleValue();
      }
      else
      {
         return _plainValueByPropertyName.get(propertyName).getValue();
      }
   }

   public String getTypeName(String propertyName)
   {
      if (null == _plainValueArray)
      {
         return _substituteValueByPropertyName.get(propertyName).getHibernatePropertyInfo().getClassName();
      }
      else
      {
         return _plainValueByPropertyName.get(propertyName).getHibernatePropertyInfo().getClassName();
      }
   }

   public boolean wasInitialized(String propertyName)
   {
      if (null == _plainValueArray)
      {
         return _substituteValueByPropertyName.get(propertyName).isInitialized();
      }
      else
      {
         return true;
      }
   }

   public Collection<? extends ObjectSubstitute> getPersistentCollection(String propertyName)
   {
      return _substituteValueByPropertyName.get(propertyName).getObjectSubstituteCollection();
   }

   public boolean isPersistenCollection(String propertyName)
   {
      if (null == _plainValueArray)
      {
         return _substituteValueByPropertyName.get(propertyName).isPersistenCollection();
      }
      else
      {
         return false;
      }
   }

   public boolean isNull(String propertyName)
   {
      if (null == _plainValueArray)
      {
         return _substituteValueByPropertyName.get(propertyName).isNull();
      }
      else
      {
         return null == _plainValueByPropertyName.get(propertyName).getValue();
      }
   }

   public HibernatePropertyInfo getHibernatePropertyInfo(String propertyName)
   {
      if (null == _plainValueArray)
      {
         return _substituteValueByPropertyName.get(propertyName).getHibernatePropertyInfo();
      }
      else
      {
         return _plainValueByPropertyName.get(propertyName).getHibernatePropertyInfo();
      }
   }

   @Override
   public String toString()
   {
      return _toString;
   }

   public MappedClassInfoData getPlainValueArrayMappedClassInfo()
   {
      if (null == _plainValueArray)
      {
         return null;
      }
      else
      {
         return _mappedClassInfoData;
      }
   }
}
