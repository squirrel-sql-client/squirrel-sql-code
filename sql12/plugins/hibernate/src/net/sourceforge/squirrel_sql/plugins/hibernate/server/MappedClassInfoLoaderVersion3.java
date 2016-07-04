package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.util.Collection;
import java.util.HashMap;

public class MappedClassInfoLoaderVersion3
{
   public static HashMap<String, MappedClassInfoData> getMappedClassInfosVersion3(Object sessionFactoryImpl, ClassLoader cl, boolean server)
   {
      HashMap<String, MappedClassInfoData> infoDataByClassName = new HashMap();


      ReflectionCaller sessionFactoryImplcaller = new ReflectionCaller(sessionFactoryImpl);
      Collection<ReflectionCaller> entities = sessionFactoryImplcaller.callMethod("getAllClassMetadata").callCollectionMethod("values");

      for (ReflectionCaller entity : entities)
      {
         Object entityMode_POJO = entity.getClass("org.hibernate.EntityMode", cl).getField("POJO").getCallee();
         Class mappedClass = (Class) entity.callMethod("getMappedClass", new Object[]{entityMode_POJO}).getCallee();

         String identifierPropertyName = (String) entity.callMethod("getIdentifierPropertyName").getCallee();

         Class identifierPropertyClass = entity.callMethod("getIdentifierType").callMethod("getReturnedClass").getCalleeClass();

         String identifierPropertyClassName = identifierPropertyClass.getName();


         String tableName = (String) entity.callMethod("getTableName").getCallee();
         String[] identifierColumnNames = (String[]) entity.callMethod("getIdentifierColumnNames").getCallee();


         HibernatePropertyInfo identifierPropInfo =
            new HibernatePropertyInfo(identifierPropertyName, identifierPropertyClassName, tableName, identifierColumnNames);

         identifierPropInfo.setIdentifier(true);


         String[] propertyNames = (String[]) entity.callMethod("getPropertyNames").getCallee();

         HibernatePropertyInfo[] infos = new HibernatePropertyInfo[propertyNames.length];
         for (int i = 0; i < propertyNames.length; i++)
         {
            ReflectionCaller propertyTypeCaller = entity.callMethod("getPropertyType", propertyNames[i]);
            String mayBeCollectionTypeName = propertyTypeCaller.callMethod("getReturnedClass").getCalleeClass().getName();

            String propTableName = (String) entity.callMethod("getPropertyTableName", propertyNames[i]).getCallee();
            String[] propertyColumnNames = (String[]) entity.callMethod("getPropertyColumnNames", propertyNames[i]).getCallee();

            try
            {
               // If this isn't instanceof org.hibernate.type.CollectionType a NoSuchMethodException will be thrown
               String role = (String) propertyTypeCaller.callMethod("getRole").getCallee();

               ReflectionCaller collectionMetaDataCaller = sessionFactoryImplcaller.callMethod("getCollectionMetadata", role);
               String typeName = collectionMetaDataCaller.callMethod("getElementType").callMethod("getReturnedClass").getCalleeClass().getName();

               infos[i] = new HibernatePropertyInfo(propertyNames[i], typeName, propTableName, propertyColumnNames);
               infos[i].setCollectionClassName(mayBeCollectionTypeName);
            }
            catch(RuntimeException e)
            {
               if(HibernateServerExceptionUtil.getDeepestThrowable(e) instanceof NoSuchMethodException)
               {
                  infos[i] = new HibernatePropertyInfo(propertyNames[i], mayBeCollectionTypeName, propTableName, propertyColumnNames);
               }
               else
               {
                  throw (RuntimeException) HibernateServerExceptionUtil.prepareTransport(e, server);
               }
            }
         }
         infoDataByClassName.put(mappedClass.getName(), new MappedClassInfoData(mappedClass.getName(), tableName, identifierPropInfo, infos));
      }

      return infoDataByClassName;
   }

}
