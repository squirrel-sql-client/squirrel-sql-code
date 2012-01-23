package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ObjectSubstitute;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ObjectSubstituteRoot;

import java.util.*;

public class RootType
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RootType.class);

   private IType _type;

   public RootType(List<ObjectSubstituteRoot> objects, ArrayList<MappedClassInfo> allMappedClassInfos)
   {
      _type = createResultType(objects.get(0), allMappedClassInfos, objects);
   }

   private IType createResultType(ObjectSubstituteRoot object, ArrayList<MappedClassInfo> allMappedClassInfos, List<ObjectSubstituteRoot> objects)
   {
      if(object.isArray())
      {
         ArrayList<String> mappedClassNames = new ArrayList<String>();
         for(int i=0; i < object.getArraySize(); ++i)
         {
            ObjectSubstitute buf = object.getArrayItemAt(i);
            String mappedClassName = buf.getClassName();
            mappedClassNames.add(mappedClassName);
         }

         return createResultTupelType(mappedClassNames, allMappedClassInfos, objects);
      }
      else
      {
         String mappedClassName = object.getObjectSubstitute().getClassName();
         return createSingleTypeResultType(mappedClassName, allMappedClassInfos, objects);

      }
   }

   private SingleType createSingleTypeResultType(String mappedClassName, ArrayList<MappedClassInfo> allMappedClassInfos, List<ObjectSubstituteRoot> objects)
   {
      MappedClassInfo mappedClassInfo = ViewObjectsUtil.findMappedClassInfo(mappedClassName, allMappedClassInfos, false);

      return new SingleType(mappedClassInfo, allMappedClassInfos, ObjectSubstituteRoot.toObjectSubstitutes(objects));
   }

   private TupelType createResultTupelType(ArrayList<String> mappedClassNames, ArrayList<MappedClassInfo> allMappedClassInfos, List<ObjectSubstituteRoot> objects)
   {

      ArrayList<MappedClassInfo> mappedClassInfos = new ArrayList<MappedClassInfo>();
      for (String mappedClassName : mappedClassNames)
      {
         mappedClassInfos.add(ViewObjectsUtil.findMappedClassInfo(mappedClassName, allMappedClassInfos, false));
      }

      return new TupelType(mappedClassInfos, allMappedClassInfos, objects);
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

