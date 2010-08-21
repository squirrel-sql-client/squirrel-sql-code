package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;

import java.lang.reflect.Array;
import java.util.*;

public class RootType
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RootType.class);

   private IType _type;

   public RootType(List objects, ArrayList<MappedClassInfo> allMappedClassInfos, Class persistenCollectionClass)
   {
      _type = createResultType(objects.get(0), allMappedClassInfos, persistenCollectionClass, objects);
   }

   private IType createResultType(Object object, ArrayList<MappedClassInfo> allMappedClassInfos, Class persistenCollectionClass, List objects)
   {
      if(object.getClass().isArray())
      {
         ArrayList<String> mappedClassNames = new ArrayList<String>();
         for(int i=0; i < Array.getLength(object); ++i)
         {
            Object buf = Array.get(object, i);
            String mappedClassName = buf.getClass().getName();
            mappedClassNames.add(mappedClassName);
         }

         return createResultTupelType(mappedClassNames, allMappedClassInfos, persistenCollectionClass, objects);
      }
      else
      {
         String mappedClassName = object.getClass().getName();
         return createSingleTypeResultType(mappedClassName, allMappedClassInfos, persistenCollectionClass, objects);

      }
   }

   private SingleType createSingleTypeResultType(String mappedClassName, ArrayList<MappedClassInfo> allMappedClassInfos, Class persistenCollectionClass, List objects)
   {
      MappedClassInfo mappedClassInfo = ViewObjectsUtil.findMappedClassInfo(mappedClassName, allMappedClassInfos, false);

      return new SingleType(mappedClassInfo, allMappedClassInfos, persistenCollectionClass, objects);
   }

   private TupelType createResultTupelType(ArrayList<String> mappedClassNames, ArrayList<MappedClassInfo> allMappedClassInfos, Class persistenCollectionClass, List objects)
   {

      ArrayList<MappedClassInfo> mappedClassInfos = new ArrayList<MappedClassInfo>();
      for (String mappedClassName : mappedClassNames)
      {
         mappedClassInfos.add(ViewObjectsUtil.findMappedClassInfo(mappedClassName, allMappedClassInfos, false));
      }

      return new TupelType(mappedClassInfos, allMappedClassInfos, persistenCollectionClass, objects);
   }


   public IType getResultType()
   {
      return _type;
   }

   @Override
   public String toString()
   {
      return s_stringMgr.getString("QueryResultNode.objectTree");
   }
}

