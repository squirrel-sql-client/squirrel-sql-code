package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


/**
 * Needed to remove lazy loading non collection proxies. These proxies caused serialization
 * problems when transferred from the external hibernate process.
 */
public class ObjectSubstituteFactory
{
   private ClassLoader _cl;
   private ReflectionCaller _rc;

   public ObjectSubstituteFactory(ClassLoader cl)
   {
      _cl = cl;
      _rc = new ReflectionCaller();
   }


   public ArrayList<ObjectSubstituteRoot> replaceObjectsWithSubstitutes(Collection col, HashMap<String, MappedClassInfoData> infoDataByClassName)
   {
      try
      {
         HashMap<Object, ObjectSubstitute> doneObjs = new HashMap<Object, ObjectSubstitute>();

         if (null == col)
         {
            return null;
         }


         ArrayList<ObjectSubstituteRoot> ret = new ArrayList<ObjectSubstituteRoot>();
         for (Object o : col)
         {
            ObjectSubstituteRoot buf;

            if(o instanceof Object[])
            {
               ArrayList<ObjectSubstitute> arrBuf = new ArrayList<ObjectSubstitute>();
               Object[] arr = (Object[]) o;
               for (Object entry : arr)
               {
                  arrBuf.add(_prepareObjectSubstitute(entry, infoDataByClassName, null ,doneObjs));
               }
               buf = new ObjectSubstituteRoot(arrBuf);
            }
            else
            {
               buf = new ObjectSubstituteRoot(_prepareObjectSubstitute(o, infoDataByClassName, null,doneObjs));
            }

            ret.add(buf);
         }
         return ret;
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(e);
      }

   }

   private ArrayList<ObjectSubstitute> _prepareObjectSubstitutesForCollection(Collection col, HashMap<String, MappedClassInfoData> infoDataByClassName, HashMap<Object, ObjectSubstitute> doneObjs)
   {
      try
      {

         ArrayList<ObjectSubstitute> ret = new ArrayList<ObjectSubstitute>();
         if (isInitialized(col))
         {
            for (Object o : col)
            {
               ret.add(_prepareObjectSubstitute(o, infoDataByClassName, null, doneObjs));
            }
         }
         return ret;

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private ObjectSubstitute _prepareObjectSubstitute(Object o, HashMap<String, MappedClassInfoData> infoDataByClassName, MappedClassInfoData infoDataFromProperty, HashMap<Object, ObjectSubstitute> doneObjs) throws IllegalAccessException
   {
      if (null == o)
      {
         return null;
      }

      ObjectSubstitute ret = doneObjs.get(o);
      if (null != ret)
      {
         return ret;
      }

      MappedClassInfoData mappedClassInfoData = findMappedClassInfoData(o, infoDataByClassName, infoDataFromProperty);

      ret = new ObjectSubstitute(mappedClassInfoData, o.toString());
      doneObjs.put(o, ret);

      PropertyAccessor pkAccessor = PropertyAccessor.createAccessor(o.getClass(), mappedClassInfoData.getIndentifierHibernatePropertyInfo().getPropertyName());
      PropertySubstitute pkPropertySubstitute = new PropertySubstitute(mappedClassInfoData.getIndentifierHibernatePropertyInfo(), pkAccessor.get(o), true);
      ret.putSubstituteValueByPropertyName(mappedClassInfoData.getIndentifierHibernatePropertyInfo().getPropertyName(), pkPropertySubstitute);



      for (HibernatePropertyInfo hibernatePropertyInfo : mappedClassInfoData.getHibernatePropertyInfos())
      {
         PropertyAccessor accessor = PropertyAccessor.createAccessor(o.getClass(), hibernatePropertyInfo.getPropertyName());

         if( null == hibernatePropertyInfo.getCollectionClassName() )
         {

            MappedClassInfoData propMappedClassInfoData = infoDataByClassName.get(hibernatePropertyInfo.getClassName());
            if(null == propMappedClassInfoData)
            {
               PropertySubstitute propertySubstitute = new PropertySubstitute(hibernatePropertyInfo, accessor.get(o), true);
               ret.putSubstituteValueByPropertyName(hibernatePropertyInfo.getPropertyName(), propertySubstitute);
            }
            else
            {
               if(isInitialized(accessor.get(o)))
               {
                  ObjectSubstitute objectSubstitute = _prepareObjectSubstitute(accessor.get(o), infoDataByClassName, propMappedClassInfoData,doneObjs);
                  PropertySubstitute propertySubstitute = new PropertySubstitute(hibernatePropertyInfo, objectSubstitute, true);
                  ret.putSubstituteValueByPropertyName(hibernatePropertyInfo.getPropertyName(), propertySubstitute);
               }
               else
               {
                  PropertySubstitute propertySubstitute = new PropertySubstitute(hibernatePropertyInfo, (ObjectSubstitute)null, false);
                  ret.putSubstituteValueByPropertyName(hibernatePropertyInfo.getPropertyName(), propertySubstitute);
               }
            }
         }
         else
         {
            ArrayList<ObjectSubstitute> objectSubstituteCollection = new ArrayList<ObjectSubstitute>();

            Collection col = (Collection) accessor.get(o);
            if (isInitialized(col))
            {
               objectSubstituteCollection = _prepareObjectSubstitutesForCollection(col, infoDataByClassName, doneObjs);
            }

            PropertySubstitute propertySubstitute = new PropertySubstitute(hibernatePropertyInfo, objectSubstituteCollection, isInitialized(col));
            ret.putSubstituteValueByPropertyName(hibernatePropertyInfo.getPropertyName(), propertySubstitute);
         }
      }

      return ret;
   }

   private MappedClassInfoData findMappedClassInfoData(Object o, HashMap<String, MappedClassInfoData> infoDataByClassName, MappedClassInfoData infoDataFromProperty)
   {
      MappedClassInfoData mappedClassInfoData;
      if (null != infoDataFromProperty)
      {
         mappedClassInfoData = infoDataFromProperty;
      }
      else
      {
         mappedClassInfoData = infoDataByClassName.get(o.getClass().getName());

         if(null == mappedClassInfoData)
         {
            int javaAssisNamePartBegin = o.getClass().getName().indexOf("_$$_");
            if (0 < javaAssisNamePartBegin)
            {
               String className = o.getClass().getName().substring(0, javaAssisNamePartBegin);
               mappedClassInfoData = infoDataByClassName.get(className);
            }
         }
      }

      if(null == mappedClassInfoData)
      {
         throw new IllegalStateException("Could not find mapping infos for class: " + o.getClass().getName());
      }

      return mappedClassInfoData;
   }

   private Boolean isInitialized(Object obj)
   {
      return (Boolean) _rc.callStaticMethod(_cl, "org.hibernate.Hibernate", "isInitialized", new Class[]{Object.class}, new Object[]{obj}).getCallee();
   }

}
