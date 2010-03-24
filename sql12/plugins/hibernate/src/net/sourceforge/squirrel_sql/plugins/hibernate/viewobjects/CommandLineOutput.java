package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.plugins.hibernate.ReflectionCaller;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.PropertyInfo;

import java.util.ArrayList;

public class CommandLineOutput
{
   public static void displayObjects(ArrayList<MappedClassInfo> mappedClassInfos, RootType root, Class persistenCollectionClass)
   {
      if(root.getResultType() instanceof SingleType)
      {
         SingleType type = (SingleType) root.getResultType();

         MappedClassInfo mci = type.getMappedClassInfo();
         System.out.println("##\nRoot-Result consists of only one mapped class: " + mci.getClassName() + "\n##");

         for (SingleResult result : type.getResults())
         {
            Object firstObject = result.getObject();
            displayMappedObject(mappedClassInfos, mci, firstObject, persistenCollectionClass);
         }
      }
      else
      {
         System.out.println("##\nRoot-Result consists more than one mapped class\n##");


         System.out.println("##Displaying result ordered by type:");
         for (IType kidType : root.getResultType().getKidTypes())
         {
            SingleType type = (SingleType) kidType;

            System.out.println("*Results for class: " + type.getMappedClassInfo().getClassName());

            for (SingleResult queryResult : type.getResults())
            {
               MappedClassInfo mci = queryResult.getMappedClassInfo();
               Object obj = queryResult.getObject();

               displayMappedObject(mappedClassInfos, mci, obj, persistenCollectionClass);
            }
         }

         System.out.println("##Displaying result ordered as read:\n");
         ArrayList<? extends IResult> results = root.getResultType().getResults();

         for (IResult result : results)
         {
            ArrayList<SingleResult> singleResults = ((TupelResult) result).getSingleResults();


            System.out.println("##Displaying result block:");
            for (SingleResult singleResult : singleResults)
            {
               MappedClassInfo mci = singleResult.getMappedClassInfo();
               Object obj = singleResult.getObject();

               displayMappedObject(mappedClassInfos, mci, obj, persistenCollectionClass);
            }
         }
      }
   }

   static void displayMappedObject(ArrayList<MappedClassInfo> mappedClassInfos, MappedClassInfo mci, Object obj, Class persistenCollectionClass)
   {
      PropertyInfo[] propertyInfos = mci.getAttributes();

      System.out.println("   New object:");

      for (PropertyInfo propertyInfo : propertyInfos)
      {
         String propertyName = propertyInfo.getHibernatePropertyInfo().getPropertyName();
         HibernatePropertyReader hpr = new HibernatePropertyReader(propertyName, obj);

         Object value = hpr.getValue();
         if (null != value && persistenCollectionClass.isAssignableFrom(value.getClass()))
         {
            ReflectionCaller rc = new ReflectionCaller(value);

            System.out.println("      PersistentCollection: " + hpr.getName() + "; wasInitialized=" + rc.callMethod("wasInitialized").getCallee());
         }
         else if (null != ViewObjectsUtil.findMappedClassInfo(hpr.getTypeName(), mappedClassInfos, true))
         {
            System.out.println("      " + hpr.getName() + " instance of mapped class: " + hpr.getTypeName());
         }
         else
         {
            System.out.println("      " + hpr.getName() + "=" + value + "; Type:" + hpr.getTypeName());
         }
      }
   }
}
