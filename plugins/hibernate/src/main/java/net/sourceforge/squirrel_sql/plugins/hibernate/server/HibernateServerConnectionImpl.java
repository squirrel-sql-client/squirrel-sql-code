package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;

public class HibernateServerConnectionImpl implements HibernateServerConnection
{
   private Object _sessionFactoryImpl;
   private ClassLoader _cl;
   private boolean _server;

   private HashMap<String, MappedClassInfoData> _infoDataByClassName;


   private ReflectionCaller _rcHibernateSession;
   private String _driverClassName;
   private String _url;
   private String _user;
   private String _password;


   HibernateServerConnectionImpl(Object sessionFactoryImpl, ClassLoader cl, boolean isServer) throws RemoteException
   {
      _sessionFactoryImpl = sessionFactoryImpl;
      _cl = cl;
      _server = isServer;
   }


   @Override
   public ArrayList<String> generateSQL(String hqlQuery)
   {
      try
      {
         JDBCTemporalEscapeParse jdbcTemporalEscapeParse = new JDBCTemporalEscapeParse(hqlQuery);

         if(jdbcTemporalEscapeParse.hasEscapes())
         {
            hqlQuery = jdbcTemporalEscapeParse.getHql();
         }

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
         _infoDataByClassName = null;
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
      return new ArrayList<MappedClassInfoData>(_infoDataByClassName.values());
   }

   private void initMappedClassInfos()
   {
      if(null != _infoDataByClassName)
      {
         return;
      }

      _infoDataByClassName = new HashMap<String, MappedClassInfoData>();

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
                  throw (RuntimeException)prepareTransport(e);
               }
            }
         }
         _infoDataByClassName.put(mappedClass.getName(), new MappedClassInfoData(mappedClass.getName(), tableName, identifierPropInfo, infos));
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
      if (null != _driverClassName)
      {
         _driverClassName = null;
         _url = null;
         _user = null;
         _password = null;
         _rcHibernateSession = null;
      }

      return _createResultList(hqlQuery, sqlNbrRowsToShow);
   }


   @Override
   public HqlQueryResult createQueryList(String hqlQuery, int sqlNbrRowsToShow, String driverClassName, String url, String user, String password) throws RemoteException
   {
      if(null == _driverClassName)
      {
         _driverClassName = driverClassName;
         _url = url;
         _user = user;
         _password = password;

         _rcHibernateSession = null;
      }


      return _createResultList(hqlQuery, sqlNbrRowsToShow);
   }


   private HqlQueryResult _createResultList(String hqlQuery, int sqlNbrRowsToShow)
   {
      HqlQueryResult ret = new HqlQueryResult();

      List queryResList = null;

      try
      {
         getRcHibernateSession().callMethod("getTransaction").callMethod("begin");
      }
      catch (Throwable t)
      {
         ret.putSessionAdminException("Exception occurced during call of Session.getTransaction().begin()", prepareTransport(t));
      }

      try
      {
         JDBCTemporalEscapeParse jdbcTemporalEscapeParse = new JDBCTemporalEscapeParse(hqlQuery);

         ReflectionCaller rc;
         if(jdbcTemporalEscapeParse.hasEscapes())
         {
            rc = getRcHibernateSession().callMethod("createQuery", jdbcTemporalEscapeParse.getHql());

            TreeMap<String, Date> datesByParamName = jdbcTemporalEscapeParse.getDatesByParamName();
            for (String paramName : datesByParamName.keySet())
            {
               RCParam param = new RCParam();
               param.add(paramName, String.class);
               param.add(datesByParamName.get(paramName), Object.class);
               rc.callMethod("setParameter", param);
            }
            
            ret.setMessagePanelInfoText("Temporal values were parameterized:\n" + jdbcTemporalEscapeParse.getMessagePanelInfoText());
         }
         else
         {
            rc = getRcHibernateSession().callMethod("createQuery", hqlQuery);
         }


         if (isDataUpdate(hqlQuery))
         {
            int updateCount = (Integer)rc.callMethod("executeUpdate").getCallee();
            ret.setUpdateCount(updateCount);
            getRcHibernateSession().callMethod("getTransaction").callMethod("commit");
         }
         else
         {
            if (0 <= sqlNbrRowsToShow)
            {
               rc = rc.callMethod("setMaxResults", new RCParam().add(sqlNbrRowsToShow, Integer.TYPE));
            }

            queryResList = (List) rc.callMethod("list").getCallee();
         }
      }
      catch (Throwable t)
      {
         ret.setExceptionOccuredWhenExecutingQuery(prepareTransport(t));
      }

      try
      {
         getRcHibernateSession().callMethod("getTransaction").callMethod("rollback");
      }
      catch (Throwable t)
      {
         ret.putSessionAdminException("Exception occurced during call of Session.getTransaction().rollback()", prepareTransport(t));
      }

      try
      {
         getRcHibernateSession().callMethod("clear");
      }
      catch (Throwable t)
      {
         ret.putSessionAdminException("Exception occurced during call of Session.clear()", prepareTransport(t));
      }

      if (null != queryResList)
      {
         ret.setQueryResultList(
               new ObjectSubstituteFactory(_cl).replaceObjectsWithSubstitutes(queryResList, _infoDataByClassName));
      }

      return ret;
   }

   private boolean isDataUpdate(String hqlQuery)
   {
      return null != hqlQuery
               && (
                           hqlQuery.trim().toLowerCase().startsWith("insert")
                        || hqlQuery.trim().toLowerCase().startsWith("update")
                        || hqlQuery.trim().toLowerCase().startsWith("delete")
                  );
   }

   private Throwable prepareTransport(Throwable t)
   {
      if(false == _server)
      {
         return t;
      }

      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      Throwable deepestThrowable = getDeepestThrowable(t);
      deepestThrowable.printStackTrace(pw);
      
      pw.flush();
      sw.flush();

      String messageIncludingOriginalStackTrace = "Exception occured on Hibernate Server Process: " + deepestThrowable.getMessage() + "\n";

      String stackTraceString = sw.toString();
      String deepestToString = deepestThrowable.toString();
      if(("" + deepestThrowable.getMessage()).equals(deepestToString) || stackTraceString.startsWith(deepestToString))
      {
         messageIncludingOriginalStackTrace += stackTraceString;
      }
      else
      {
         messageIncludingOriginalStackTrace +=  ( deepestToString + "\n" + stackTraceString);
      }
      

      return new SquirrelHibernateServerException(messageIncludingOriginalStackTrace, deepestThrowable.getMessage(), deepestToString, deepestThrowable.getClass().getName());
   }


   private ReflectionCaller getRcHibernateSession()
   {
      try
      {
         if(null == _rcHibernateSession)
         {
            if (null == _driverClassName)
            {
               _rcHibernateSession = new ReflectionCaller(_sessionFactoryImpl).callMethod("openSession");
            }
            else
            {
               ReflectionCaller driver = new ReflectionCaller().getClass(_driverClassName, _cl).newInstance();

               Properties props = new Properties();
               props.put("user", _user);
               props.put("password", _password);

               ReflectionCaller con = driver.callMethod("connect", _url, props);

               RCParam rcParam = new RCParam();
               rcParam.add(con.getCallee(), Connection.class);

               _rcHibernateSession = new ReflectionCaller(_sessionFactoryImpl).callMethod("openSession", rcParam);
            }
         }

         return _rcHibernateSession;
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
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
