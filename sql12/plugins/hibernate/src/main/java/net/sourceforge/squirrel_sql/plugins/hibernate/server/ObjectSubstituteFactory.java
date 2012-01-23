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
                  arrBuf.add(_prepareObjectSubstitute(entry, infoDataByClassName, doneObjs));
               }
               buf = new ObjectSubstituteRoot(arrBuf);
            }
            else
            {
               buf = new ObjectSubstituteRoot(_prepareObjectSubstitute(o, infoDataByClassName, doneObjs));
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
               ret.add(_prepareObjectSubstitute(o, infoDataByClassName, doneObjs));
            }
         }
         return ret;

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private ObjectSubstitute _prepareObjectSubstitute(Object o, HashMap<String, MappedClassInfoData> infoDataByClassName, HashMap<Object, ObjectSubstitute> doneObjs) throws IllegalAccessException
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

      MappedClassInfoData mappedClassInfoData = infoDataByClassName.get(o.getClass().getName());

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
                  ObjectSubstitute objectSubstitute = _prepareObjectSubstitute(accessor.get(o), infoDataByClassName, doneObjs);
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

//      Field[] declaredFields = o.getClass().getDeclaredFields();
//      for (Field declaredField : declaredFields)
//      {
//         if (infoDataByClassName.containsKey(declaredField.getType().getName()))
//         {
//            declaredField.setAccessible(true);
//            Object fielddObj = declaredField.get(o);
//            if (null != fielddObj)
//            {
//               if (-1 < fielddObj.getClass().getName().indexOf('$'))
//               {
//                  ret.set(declaredField, null);
//                  //declaredField.set(o, null);
//               }
//               else
//               {
//                  ret.set(declaredField, _prepareObjectSubstitutes(fielddObj, infoDataByClassName, doneObjs));
//               }
//            }
//         }
//         else if (Collection.class.isAssignableFrom(declaredField.getType()))
//         {
//            declaredField.setAccessible(true);
//            Object fielddObj = declaredField.get(o);
//            ret.setList(declaredField, _prepareObjectSubstitutesForCollection((Collection) fielddObj, infoDataByClassName, doneObjs, false));
//         }
//      }
      
      return ret;
   }

   private Boolean isInitialized(Object obj)
   {
      return (Boolean) _rc.callStaticMethod(_cl, "org.hibernate.Hibernate", "isInitialized", new Class[]{Object.class}, new Object[]{obj}).getCallee();
   }

}
