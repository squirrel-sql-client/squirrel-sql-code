package net.sourceforge.squirrel_sql.plugins.hibernate;

import java.net.URLClassLoader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.HibernatePropertyInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;

public class HibernateConnection
{
   private static ILogger s_log = LoggerController.createLogger(HibernateConnection.class);
   private Object _sessionFactoryImpl;
   private URLClassLoader _cl;
   private ArrayList<MappedClassInfo> _mappedClassInfos;
   private ReflectionCaller m_rcHibernateSession;


   public HibernateConnection(Object sessionFactoryImpl, URLClassLoader cl)
   {
      _sessionFactoryImpl = sessionFactoryImpl;
      _cl = cl;
   }


   public ArrayList<String> generateSQL(String hqlQuery)
   {
      try
      {


         Class sessionFactoryImplementorClass = (Class) new ReflectionCaller().getClass("org.hibernate.engine.SessionFactoryImplementor", _cl).getCallee();

         List<ReflectionCaller> translators =
         new ReflectionCaller(_cl)
            .getClass("org.hibernate.engine.query.HQLQueryPlan", _cl)
            .callConstructor(new Class[]{String.class, Boolean.TYPE, Map.class, sessionFactoryImplementorClass}, new Object[]{hqlQuery, false, Collections.EMPTY_MAP, _sessionFactoryImpl})
            .callArrayMethod("getTranslators");

         ArrayList<String> ret = new ArrayList<String>();


         for (ReflectionCaller translator : translators)
         {
            List sqls = (List) translator.callMethod("collectSqlStrings").getCallee();

            for (Object sql : sqls)
            {
               ret.add(sql.toString());
            }
         }
         return ret;
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }



   public void close()
   {
      try
      {
         new ReflectionCaller(_sessionFactoryImpl).callMethod("close");
      }
      catch (Throwable t)
      {
         s_log.error(t);
      }
      _sessionFactoryImpl = null;
      _cl = null;
      _mappedClassInfos = null;
      System.gc();

   }

   public ArrayList<MappedClassInfo> getMappedClassInfos()
   {
      initMappedClassInfos();
      return _mappedClassInfos;
   }

   public Class getPersistenCollectionClass()
   {
      try
      {
         return _cl.loadClass("org.hibernate.collection.PersistentCollection");
      }
      catch (ClassNotFoundException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void initMappedClassInfos()
   {
      if(null != _mappedClassInfos)
      {
         return;
      }


      _mappedClassInfos = new ArrayList<MappedClassInfo>();

      ReflectionCaller sessionFactoryImplcaller = new ReflectionCaller(_sessionFactoryImpl);
      Collection<ReflectionCaller> persisters = sessionFactoryImplcaller.callMethod("getAllClassMetadata").callCollectionMethod("values");

      for (ReflectionCaller persister : persisters)
      {
         Object entityMode_POJO = persister.getClass("org.hibernate.EntityMode", _cl).getField("POJO").getCallee();
         Class mappedClass = (Class) persister.callMethod("getMappedClass", new Object[]{entityMode_POJO}).getCallee();

         String identifierPropertyName = (String) persister.callMethod("getIdentifierPropertyName").getCallee();

         Class identifierPropertyClass = persister.callMethod("getIdentifierType").callMethod("getReturnedClass").getCalleeClass();

         String identifierPropertyClassName = identifierPropertyClass.getName();


         String tableName = (String) persister.callMethod("getTableName").getCallee();
         String[] identifierColumnNames = (String[]) persister.callMethod("getIdentifierColumnNames").getCallee();


         HibernatePropertyInfo identifierPropInfo =
            new HibernatePropertyInfo(identifierPropertyName, identifierPropertyClassName, tableName, identifierColumnNames);

         identifierPropInfo.setIdentifier(true);


         String[] propertyNames = (String[]) persister.callMethod("getPropertyNames").getCallee();

         HibernatePropertyInfo[] infos = new HibernatePropertyInfo[propertyNames.length];
         for (int i = 0; i < propertyNames.length; i++)
         {
            ReflectionCaller propertyTypeCaller = persister.callMethod("getPropertyType", propertyNames[i]);
            String mayBeCollectionTypeName = propertyTypeCaller.callMethod("getReturnedClass").getCalleeClass().getName();

            String propTableName = (String) persister.callMethod("getPropertyTableName", propertyNames[i]).getCallee();
            String[] propertyColumnNames = (String[]) persister.callMethod("getPropertyColumnNames", propertyNames[i]).getCallee();

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
               if(Utilities.getDeepestThrowable(e) instanceof NoSuchMethodException)
               {
                  infos[i] = new HibernatePropertyInfo(propertyNames[i], mayBeCollectionTypeName, propTableName, propertyColumnNames);
               }
               else
               {
                  throw e;
               }
            }
         }

         _mappedClassInfos.add(new MappedClassInfo(mappedClass.getName(), tableName, identifierPropInfo, infos));
      }
   }


   public Connection getSqlConnection()
   {
      return (Connection) getRcHibernateSession().callMethod("getJDBCContext").callMethod("getConnectionManager").callMethod("getConnection").getCallee();
   }

   public List createQueryList(String hqlQuery, int sqlNbrRowsToShow)
   {
      ReflectionCaller  rc = getRcHibernateSession().callMethod("createQuery", hqlQuery);

      if (0 <= sqlNbrRowsToShow)
      {
         rc = rc.callMethod("setMaxResults", new RCParam().add(sqlNbrRowsToShow, Integer.TYPE));
      }

      return (List) rc.callMethod("list").getCallee();
   }

   private ReflectionCaller getRcHibernateSession()
   {
      if(null == m_rcHibernateSession)
      {
         m_rcHibernateSession = new ReflectionCaller(_sessionFactoryImpl).callMethod("openSession");
      }

      return m_rcHibernateSession;
   }
}
