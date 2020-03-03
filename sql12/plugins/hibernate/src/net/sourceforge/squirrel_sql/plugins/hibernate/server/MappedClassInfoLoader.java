package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MappedClassInfoLoader
{

   public static HashMap<String, MappedClassInfoData> getMappedClassInfos(FactoryWrapper factoryWrapper, ClassLoader cl, boolean server)
   {
      HashMap<String, MappedClassInfoData> infoDataByClassName = new HashMap();


      Collection<ReflectionCaller> entityTypes = new ReflectionCaller(factoryWrapper.getEntityManagerFactory(), false).callMethod("getMetamodel").callCollectionMethod("getEntities");

      for (ReflectionCaller entityType : entityTypes)
      {
         if(null == entityType.callMethod("getJavaType").getCallee())
         {
            System.out.println("Entity " + entityType.getCallee() + " has no Java type and won't be loaded.");
            continue;
         }

         ReflectionCaller getIdTypeCaller = entityType.callMethod("getIdType");

         if(null == getIdTypeCaller.getCallee())
         {
            System.out.println("Entity type " + entityType.callMethod("getJavaType").callMethod("getName").getCallee() + " has no unique ID and won't be loaded.");
            continue;
         }

         ReflectionCaller pkJavaType = getIdTypeCaller.callMethod("getJavaType");
         ReflectionCaller pkSingularAttribute = entityType.callMethod("getId", pkJavaType.getCallee()).setTreatClassCalleeAsType(false);

         String identifierPropertyName = (String) pkSingularAttribute.callMethod("getJavaMember").callMethod("getName").getCallee();
         String identifierPropertyClassName = (String) pkSingularAttribute.callMethod("getJavaType").callMethod("getName").getCallee();


         String tableName = getTableName(cl, entityType);


         String identifierColumnName = getMappedColumnName(cl, entityType, identifierPropertyName);

         HibernatePropertyInfo identifierPropInfo =
               new HibernatePropertyInfo(identifierPropertyName, identifierPropertyClassName, tableName, new String[]{identifierColumnName});

         identifierPropInfo.setIdentifier(true);

         ArrayList<HibernatePropertyInfo> infos = new ArrayList<>();

         for (ReflectionCaller attr: entityType.callCollectionMethod("getAttributes"))
         {
            String propertyName = (String) attr.callMethod("getJavaMember").callMethod("getName").getCallee();
            Class propertyClass = (Class) attr.callMethod("getJavaType").getCallee();
            String[] propertyColumnNames = new String[]{getMappedColumnName(cl, entityType, propertyName)};

            HibernatePropertyInfo hibernatePropertyInfo = null;

            if (ReflectionCaller.getClassPlain("javax.persistence.metamodel.PluralAttribute", cl).isAssignableFrom(attr.getCalleeClass()))
            {
               propertyClass = (Class) attr.callMethod("getElementType").callMethod("getJavaType").getCallee();

               hibernatePropertyInfo = new HibernatePropertyInfo(propertyName, propertyClass.getName(), tableName, propertyColumnNames);
               hibernatePropertyInfo.setCollectionClassName((String) attr.callMethod("getJavaType").callMethod("getName").getCallee());
            }
            else if(Boolean.FALSE.equals(attr.callMethod("isId").getCallee()))
            {
               hibernatePropertyInfo = new HibernatePropertyInfo(propertyName, propertyClass.getName(), tableName, propertyColumnNames);
            }

            if (null != hibernatePropertyInfo)
            {
               infos.add(hibernatePropertyInfo);
            }
         }

         String entityClassName = (String) entityType.callMethod("getJavaType").callMethod("getName").getCallee();
         infoDataByClassName.put(entityClassName, new MappedClassInfoData(entityClassName, tableName, identifierPropInfo, infos.toArray(new HibernatePropertyInfo[infos.size()])));

      }

      return infoDataByClassName;
   }

   private static String getMappedColumnName(ClassLoader cl, ReflectionCaller entity, String propertyName)
   {
      Class entityClass = (Class) entity.callMethod("getJavaType").getCallee();

      String identifierColumnName = propertyName;

      if(null != getDeclaredField(entityClass, propertyName))
      {
         Field identifiereField = getDeclaredField(entityClass, propertyName);
         if(identifiereField.isAnnotationPresent(ReflectionCaller.getClassPlain("javax.persistence.Column", cl)))
         {
            ReflectionCaller columnAnno = new ReflectionCaller(identifiereField.getAnnotation(ReflectionCaller.getClassPlain("javax.persistence.Column", cl)), false);
            String columnName = (String) columnAnno.callMethod("name").getCallee();

            if (null != columnName)
            {
               identifierColumnName = columnName;
            }
         }
      }
      else if (null != getDeclaredMethod(entityClass, propertyName))
      {
         Method identifierMethod = getDeclaredMethod(entityClass, propertyName);

         if(identifierMethod.isAnnotationPresent(ReflectionCaller.getClassPlain("javax.persistence.Column", cl)))
         {
            ReflectionCaller columnAnno = new ReflectionCaller(identifierMethod.getAnnotation(ReflectionCaller.getClassPlain("javax.persistence.Column", cl)), false);
            String columnName = (String) columnAnno.callMethod("name").getCallee();

            if (null != columnName)
            {
               identifierColumnName = columnName;
            }
         }
      }


      return identifierColumnName;
   }


   private static Method getDeclaredMethod(Class entityClass, String identifierPropertyName)
   {
      try
      {
         return entityClass.getDeclaredMethod(identifierPropertyName);
      }
      catch (NoSuchMethodException e)
      {
         return null;
      }
   }

   private static Field getDeclaredField(Class entityClass, String identifierPropertyName)
   {
      try
      {
         return entityClass.getDeclaredField(identifierPropertyName);
      }
      catch (NoSuchFieldException e)
      {
         return null;
      }
   }


   private static String getTableName(ClassLoader cl, ReflectionCaller entityType)
   {
      String tableName = (String) entityType.callMethod("getName").getCallee();
      Class entityJavaType = (Class) entityType.callMethod("getJavaType").getCallee();
      if(entityJavaType.isAnnotationPresent(ReflectionCaller.getClassPlain("javax.persistence.Table", cl)))
      {
         ReflectionCaller tableAnno = new ReflectionCaller(entityJavaType.getAnnotation(ReflectionCaller.getClassPlain("javax.persistence.Table", cl)));
         if(null != tableAnno.callMethod("name"))
         {
            tableName = (String) tableAnno.callMethod("name").getCallee();
         }
      }
      return tableName;
   }
}
