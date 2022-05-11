package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

public class ObjectSubstitute implements Serializable
{
   private HashMap<String, PropertySubstitute> _substituteValueByPropertyName = new HashMap<String, PropertySubstitute>();
   private MappedClassInfoData _mappedClassInfoData;
   private String _toString;


   public static final String PLAIN_VALUES = "<plain values>";
   private Collection<Object> _plainValueArray;
   private HashMap<String, PlainValue> _plainValueByPropertyName = new HashMap<String, PlainValue>();

   /**
    * Is seriously meant to be package visible because it must not called nowhere except during initialization in ObjectSubstituteFactory
    */
   ObjectSubstitute(MappedClassInfoData mappedClassInfoData, String toString)
   {
      _mappedClassInfoData = mappedClassInfoData;
      _toString = toString;
   }

   ObjectSubstitute(Collection<Object> plainValueCollection)
   {
      _plainValueArray = plainValueCollection;
      //_toString = PLAIN_VALUES + "[" + plainValueCollection.size() + "]";
      _toString = PLAIN_VALUES + " size=" + plainValueCollection.size() + ", values: " + plainValuesAsString(plainValueCollection);


      Optional<Object> any = plainValueCollection.stream().findAny();
      String className = any.isEmpty() ? "<unknown>" : any.getClass().getName();
      String propertyName = "value " + (1);
      HibernatePropertyInfo indentifierHibernatePropertyInfo = new HibernatePropertyInfo(propertyName, className, "<unknown>", new String[]{"<unknown>"});

      _plainValueByPropertyName.put(propertyName, new PlainValue(any.orElse("<unknown>"), indentifierHibernatePropertyInfo));

      HibernatePropertyInfo[] hibernatePropertyInfos = new HibernatePropertyInfo[plainValueCollection.size() - 1];

      ArrayList<Object> plainValueList = new ArrayList<>(plainValueCollection);
      for ( int i = 1; i < plainValueList.size(); i++)
      {
         className = null == plainValueList.get(i) ? "<unknown>" : plainValueList.get(i).getClass().getName();
         propertyName = "value " + (i + 1);
         hibernatePropertyInfos[i-1] = new HibernatePropertyInfo(propertyName, className, "<unknown>", new String[]{"<unknown>"});

         _plainValueByPropertyName.put(propertyName, new PlainValue(plainValueList.get(i), hibernatePropertyInfos[i-1]));
      }


      _mappedClassInfoData = new MappedClassInfoData(PLAIN_VALUES, "<unknown>", indentifierHibernatePropertyInfo, hibernatePropertyInfos);
      _mappedClassInfoData.setPlainValueArray(true);
   }

   private String plainValuesAsString(Collection<Object> plainValueCollection)
   {
      StringBuilder ret = new StringBuilder();

      int count = 0;
      for( Object o : plainValueCollection )
      {
         if(0 == count)
         {
            ret.append(o);
         }
         else
         {
            ret.append(";").append(o);
         }

         ++count;

         if(count > 20)
         {
            ret.append(" ...");
            break;
         }
      }

      return ret.toString();
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
