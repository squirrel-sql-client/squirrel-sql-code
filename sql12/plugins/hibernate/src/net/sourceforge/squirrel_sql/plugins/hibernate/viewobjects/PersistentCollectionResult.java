package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import java.util.ArrayList;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.PropertyInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ObjectSubstitute;


public class PersistentCollectionResult
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(PersistentCollectionResult.class);

   private PropertyInfo _propertyInfo;
   private String _toString;

   private ArrayList<SingleResult> _kidResults = new ArrayList<SingleResult>();

   public PersistentCollectionResult(HibernatePropertyReader hprPersistentCollection, PropertyInfo propertyInfo, ArrayList<MappedClassInfo> mappedClassInfos)
   {
      _propertyInfo = propertyInfo;


      String propertyName = _propertyInfo.getHibernatePropertyInfo().getPropertyName();

      String className = "<unknown>";
      if( null != _propertyInfo.getMappedClassInfo() )
      {
         className = _propertyInfo.getMappedClassInfo().getClassName();
      }

      if(hprPersistentCollection.wasInitialized())
      {
         _toString = s_stringMgr.getString("PersistentCollectionResult.initialized", propertyName, className);


         MappedClassInfo mci = null;
         for (ObjectSubstitute subs : hprPersistentCollection.getPersistentCollection())
         {
            if(null == mci)
            {
               mci =ViewObjectsUtil.findMappedClassInfo(subs.getClassName(), mappedClassInfos, false);
            }

            _kidResults.add(new SingleResult(subs, mci));
         }
      }
      else
      {
         _toString = s_stringMgr.getString("PersistentCollectionResult.uninitialized", propertyName, className);
      }
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
