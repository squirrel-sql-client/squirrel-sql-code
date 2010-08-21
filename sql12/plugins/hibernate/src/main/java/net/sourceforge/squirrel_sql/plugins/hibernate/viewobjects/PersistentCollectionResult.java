package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.hibernate.ReflectionCaller;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.PropertyInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


public class PersistentCollectionResult
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(PersistentCollectionResult.class);

   private Object _persistentCollection;
   private PropertyInfo _propertyInfo;
   private String _toString;
   private Boolean _initialized;

   private ArrayList<SingleResult> _kidResults = new ArrayList<SingleResult>();

   public PersistentCollectionResult(Object persistentCollection, PropertyInfo propertyInfo, ArrayList<MappedClassInfo> mappedClassInfos)
   {

      _persistentCollection = persistentCollection;
      _propertyInfo = propertyInfo;

      ReflectionCaller rc = new ReflectionCaller(persistentCollection);
      _initialized = (Boolean)rc.callMethod("wasInitialized").getCallee();

      String propertyName = _propertyInfo.getHibernatePropertyInfo().getPropertyName();
      String className = _propertyInfo.getMappedClassInfo().getClassName();

      if(_initialized)
      {
         _toString = s_stringMgr.getString("PersistentCollectionResult.initialized", propertyName, className);


         Iterator iterator = ((Collection) new ReflectionCaller(_persistentCollection).getCallee()).iterator();

         MappedClassInfo mci = null;
         while(iterator.hasNext())
         {
            Object obj = iterator.next();

            if(null == mci)
            {
               mci =ViewObjectsUtil.findMappedClassInfo(obj.getClass().getName(), mappedClassInfos, false);
            }

            _kidResults.add(new SingleResult(obj, mci));
         }
      }
      else
      {
         _toString = s_stringMgr.getString("PersistentCollectionResult.uninitialized", propertyName, className);
      }
   }

   public Boolean isInitialized()
   {
      return _initialized;
   }

   public ArrayList<SingleResult> getKidResults()
   {
      return _kidResults;
   }

   @Override
   public String toString()
   {
      return _toString;
   }
}
