package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;


/**
 * Needed to remove lazy loading non collection proxies. These proxies caused serialization
 * problems when transferred from the external hibernate process.
 */
public class HibernateProxyHandler
{
   private ClassLoader _cl;
   private ReflectionCaller _rc;

   public HibernateProxyHandler(ClassLoader cl)
   {
      _cl = cl;
      _rc = new ReflectionCaller();
   }


   public void prepareHibernateProxies(Collection col, HashSet<String> mappedClassNames)
   {
      HashSet doneObjs = new HashSet();
      _prepareHibernateProxiesForCollection(col, mappedClassNames, doneObjs, true);
   }

   private void _prepareHibernateProxiesForCollection(Collection col, HashSet<String> mappedClassNames, HashSet doneObjs, boolean isQueryList)
   {
      try
      {
         if (null == col)
         {
            return;
         }

         boolean isInitialized = isQueryList ||
               (Boolean) _rc.callStaticMethod(_cl, "org.hibernate.Hibernate", "isInitialized", new Class[]{Object.class}, new Object[]{col}).getCallee();

         if (isInitialized)
         {
            for (Object o : col)
            {
               _prepareHibernateProxies(o, mappedClassNames, doneObjs);
            }
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private void _prepareHibernateProxies(Object o, HashSet<String> mappedClassNames, HashSet doneObjs) throws IllegalAccessException
   {
      if (null == o || doneObjs.contains(o))
      {
         return;
      }

      doneObjs.add(o);

      Field[] declaredFields = o.getClass().getDeclaredFields();

      for (Field declaredField : declaredFields)
      {
         if (mappedClassNames.contains(declaredField.getType().getName()))
         {
            declaredField.setAccessible(true);
            Object fielddObj = declaredField.get(o);
            if (null != fielddObj)
            {
               if (-1 < fielddObj.getClass().getName().indexOf('$'))
               {
                  declaredField.set(o, null);
               }
               else
               {
                  _prepareHibernateProxies(fielddObj, mappedClassNames, doneObjs);
               }
            }
         }
         else if (Collection.class.isAssignableFrom(declaredField.getType()))
         {
            declaredField.setAccessible(true);
            Object fielddObj = declaredField.get(o);
            _prepareHibernateProxiesForCollection((Collection) fielddObj, mappedClassNames, doneObjs, false);
         }
      }
   }
}
