package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;

public class HibernateServerConnectionImpl implements HibernateServerConnection
{
   private Object _sessionFactoryImpl;
   private ClassLoader _cl;
   private ArrayList<MappedClassInfoData> _mappedClassInfoData;
   private ReflectionCaller m_rcHibernateSession;
   private HashSet<String> _mappedClassNames;


   HibernateServerConnectionImpl(Object sessionFactoryImpl, ClassLoader cl) throws RemoteException
   {
      _sessionFactoryImpl = sessionFactoryImpl;
      _cl = cl;
   }


   @Override
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



   @Override
   public void closeConnection()
   {
      Throwable reThrow = null;

      try
      {
         new ReflectionCaller(_sessionFactoryImpl).callMethod("close");
      }
      catch (Throwable t)
      {
         reThrow = t;
      }
      finally
      {
         _sessionFactoryImpl = null;
         _cl = null;
         _mappedClassInfoData = null;
         System.gc();

         if(null != reThrow)
         {
            throw new RuntimeException(reThrow);
         }
      }
   }

   @Override
   public ArrayList<MappedClassInfoData> getMappedClassInfoData()
   {
      initMappedClassInfos();
      return _mappedClassInfoData;
   }

   @Override
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
      if(null != _mappedClassInfoData)
      {
         return;
      }

      _mappedClassInfoData = new ArrayList<MappedClassInfoData>();
      _mappedClassNames =  new HashSet<String>();

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
               if(getDeepestThrowable(e) instanceof NoSuchMethodException)
               {
                  infos[i] = new HibernatePropertyInfo(propertyNames[i], mayBeCollectionTypeName, propTableName, propertyColumnNames);
               }
               else
               {
                  throw e;
               }
            }
         }
         _mappedClassInfoData.add(new MappedClassInfoData(mappedClass.getName(), tableName, identifierPropInfo, infos));
         _mappedClassNames.add(mappedClass.getName());
      }
   }


   @Override
   public HibernateSqlConnectionData getHibernateSqlConnectionData()
   {
      try
      {
         Connection con = (Connection) getRcHibernateSession().callMethod("getJDBCContext").callMethod("getConnectionManager").callMethod("getConnection").getCallee();
         DatabaseMetaData md = con.getMetaData();
         return new HibernateSqlConnectionData(md.getURL(), md.getUserName(), md.getDriverName(), md.getDriverVersion());
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public HqlQueryResult createQueryList(String hqlQuery, int sqlNbrRowsToShow)
   {
      HqlQueryResult ret = new HqlQueryResult();

      try
      {
         getRcHibernateSession().callMethod("getTransaction").callMethod("begin");
      }
      catch (Throwable t)
      {
         ret.putSessionAdminException("Exception occurced during call of Session.getTransaction().begin()", t);
      }

      try
      {
         ReflectionCaller rc = getRcHibernateSession().callMethod("createQuery", hqlQuery);

         if (0 <= sqlNbrRowsToShow)
         {
            rc = rc.callMethod("setMaxResults", new RCParam().add(sqlNbrRowsToShow, Integer.TYPE));
         }

         ret.setQueryResultList((List) rc.callMethod("list").getCallee());
      }
      catch (Throwable t)
      {
         ret.setExceptionOccuredWhenExecutingQuery(t);
      }

      try
      {
         getRcHibernateSession().callMethod("getTransaction").callMethod("rollback");
      }
      catch (Throwable t)
      {
         ret.putSessionAdminException("Exception occurced during call of Session.getTransaction().rollback()", t);
      }

      try
      {
         getRcHibernateSession().callMethod("clear");
      }
      catch (Throwable t)
      {
         ret.putSessionAdminException("Exception occurced during call of Session.clear()", t);
      }

       if (null != ret.getQueryResultList())
       {
           new HibernateProxyHandler(_cl).prepareHibernateProxies(ret.getQueryResultList(), _mappedClassNames);
       }

       return ret;
   }


   private ReflectionCaller getRcHibernateSession()
   {
      if(null == m_rcHibernateSession)
      {
         m_rcHibernateSession = new ReflectionCaller(_sessionFactoryImpl).callMethod("openSession");
      }

      return m_rcHibernateSession;
   }


   private Throwable getDeepestThrowable(Throwable t)
   {
      Throwable parent = t;
      Throwable child = t.getCause();
      while(null != child)
      {
         parent = child;
         child = parent.getCause();
      }

      return parent;

   }


}
