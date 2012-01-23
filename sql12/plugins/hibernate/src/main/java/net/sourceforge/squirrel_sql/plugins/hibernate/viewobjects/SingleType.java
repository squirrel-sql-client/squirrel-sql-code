package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.plugins.hibernate.server.ObjectSubstitute;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.PropertyInfo;

import java.util.ArrayList;
import java.util.List;

public class SingleType implements IType
{
   private MappedClassInfo _mappedClassInfo;
   private ArrayList<MappedClassInfo> _allMappedClassInfos;
   private ArrayList<SingleResult> _results = new ArrayList<SingleResult>();
   private ArrayList<IType> _kidTypes;
   private String _toString;

   public SingleType(MappedClassInfo mappedClassInfo, ArrayList<MappedClassInfo> allMappedClassInfos, List<ObjectSubstitute> objects)
   {
      this(null, mappedClassInfo, allMappedClassInfos, objects);
   }

   public SingleType(String propertyName, MappedClassInfo mappedClassInfo, ArrayList<MappedClassInfo> allMappedClassInfos, List<ObjectSubstitute> objects)
   {
      _mappedClassInfo = mappedClassInfo;
      _allMappedClassInfos = allMappedClassInfos;

      for (ObjectSubstitute object : objects)
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
         ArrayList<ObjectSubstitute> objects = new ArrayList<ObjectSubstitute>();

         boolean persistentCollection = false;
         boolean persistentCollectionInitialized = false;

         for (SingleResult singleResult : _results)
         {
            HibernatePropertyReader hpr = new HibernatePropertyReader(propertyName, singleResult.getObject());

            //if (null != value && _persistenCollectionClass.isAssignableFrom(value.getClass()))
            if (false == hpr.isNull() && hpr.isPersistenCollection())
            {
               persistentCollection = true;

               persistentCollectionInitialized = hpr.wasInitialized();

               if(null == mci)
               {
                  mci = propertyInfo.getMappedClassInfo();
               }

               if (persistentCollectionInitialized)
               {
                  objects.addAll(hpr.getPersistentCollection());
               }
            }
            else if (null != ViewObjectsUtil.findMappedClassInfo(hpr.getTypeName(), _allMappedClassInfos, true))
            {
               if(null == mci)
               {
                  mci = ViewObjectsUtil.findMappedClassInfo(hpr.getTypeName(), _allMappedClassInfos, false);
               }

               objects.add((ObjectSubstitute) hpr.getValue());
            }
         }

         if(null != mci)
         {
            SingleType singleType = new SingleType(propertyName, mci, _allMappedClassInfos, objects);
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


   public ArrayList<MappedClassInfo> getAllMappedClassInfos()
   {
      return _allMappedClassInfos;
   }
}
