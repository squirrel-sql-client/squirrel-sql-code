package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.plugins.hibernate.ReflectionCaller;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.PropertyInfo;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SingleType implements IType
{
   private MappedClassInfo _mappedClassInfo;
   private ArrayList<MappedClassInfo> _allMappedClassInfos;
   private Class _persistenCollectionClass;
   private ArrayList<SingleResult> _results = new ArrayList<SingleResult>();
   private ArrayList<IType> _kidTypes;
   private String _toString;

   public SingleType(MappedClassInfo mappedClassInfo, ArrayList<MappedClassInfo> allMappedClassInfos, Class persistenCollectionClass, List objects)
   {
      this(null, mappedClassInfo, allMappedClassInfos, persistenCollectionClass, objects);
   }

   public SingleType(String propertyName, MappedClassInfo mappedClassInfo, ArrayList<MappedClassInfo> allMappedClassInfos, Class persistenCollectionClass, List objects)
   {
      _mappedClassInfo = mappedClassInfo;
      _allMappedClassInfos = allMappedClassInfos;
      _persistenCollectionClass = persistenCollectionClass;

      for (Object object : objects)
      {
         _results.add(new SingleResult(propertyName, object, mappedClassInfo));
      }

      if (null == propertyName)
      {
         _toString = _mappedClassInfo.getClassName();
      }
      else
      {
         _toString = propertyName + ": " + _mappedClassInfo.getClassName();
      }

   }


   @Override
   public ArrayList<? extends IType> getKidTypes()
   {
      initKidTypes();
      return _kidTypes;
   }

   private void initKidTypes()
   {
      if(null != _kidTypes)
      {
         return;
      }


      PropertyInfo[] propertyInfos = _mappedClassInfo.getAttributes();

      _kidTypes = new ArrayList<IType>();

      for (PropertyInfo propertyInfo : propertyInfos)
      {
         String propertyName = propertyInfo.getHibernatePropertyInfo().getPropertyName();

         MappedClassInfo mci = null;
         ArrayList objects = new ArrayList();

         boolean persistentCollection = false;
         boolean persistentCollectionInitialized = false;

         for (SingleResult singleResult : _results)
         {
            HibernatePropertyReader hpr = new HibernatePropertyReader(propertyName, singleResult.getObject());

            Object value = hpr.getValue();
            if (null != value && _persistenCollectionClass.isAssignableFrom(value.getClass()))
            {
               persistentCollection = true;

               persistentCollectionInitialized = isPersistentCollectionIsInitialize(value);

               if(null == mci)
               {
                  mci = propertyInfo.getMappedClassInfo();
               }

               if (persistentCollectionInitialized)
               {
                  objects.addAll(getObjectsFromPersistentCollection(value));
               }
            }
            else if (null != ViewObjectsUtil.findMappedClassInfo(hpr.getTypeName(), _allMappedClassInfos, true))
            {
               if(null == mci)
               {
                  mci = ViewObjectsUtil.findMappedClassInfo(hpr.getTypeName(), _allMappedClassInfos, false);
               }

               objects.add(value);
            }
         }

         if(null != mci)
         {
            SingleType singleType = new SingleType(propertyName, mci, _allMappedClassInfos, _persistenCollectionClass, objects);
            if(persistentCollection)
            {
               _kidTypes.add(new PersistentCollectionType(propertyName, singleType, persistentCollectionInitialized));
            }
            else
            {
               _kidTypes.add(singleType);
            }
         }
      }
   }

   private ArrayList getObjectsFromPersistentCollection(Object persistentCollection)
   {
      Iterator iterator = ((Collection) new ReflectionCaller(persistentCollection).getCallee()).iterator();

      ArrayList ret = new ArrayList();
      while(iterator.hasNext())
      {
         ret.add(iterator.next());
      }

      return ret;
   }

   private boolean isPersistentCollectionIsInitialize(Object persistentCollection)
   {
      ReflectionCaller rc = new ReflectionCaller(persistentCollection);
      return (Boolean)rc.callMethod("wasInitialized").getCallee();
   }

   @Override
   public ArrayList<SingleResult> getResults()
   {
      return _results;
   }

   public MappedClassInfo getMappedClassInfo()
   {
      return _mappedClassInfo;
   }


   @Override
   public String toString()
   {
      return _toString;
   }


   public Class getPersistenCollectionClass()
   {
      return _persistenCollectionClass;
   }
}
