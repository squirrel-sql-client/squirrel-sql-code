package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;

public class HibernateServerConnectionImpl implements HibernateServerConnection
{
   private FactoryWrapper _sessionFactoryImpl;
   private ClassLoader _cl;
   private final String _jpaRootPackage;
   private boolean _server;

   private HashMap<String, MappedClassInfoData> _infoDataByClassName;


   private ReflectionCaller _rcHibernateSession;
   private String _driverClassName;
   private String _url;
   private String _user;
   private String _password;


   HibernateServerConnectionImpl(FactoryWrapper sessionFactoryImpl, ClassLoader cl, String jpaRootPackage, boolean isServer) throws RemoteException
   {
      _sessionFactoryImpl = sessionFactoryImpl;
      _cl = cl;
      _jpaRootPackage = jpaRootPackage;
      _server = isServer;
   }


   @Override
   public String generateSQL(String hqlQuery)
   {
      return SQLGenerator.generateSql(createQuery(hqlQuery).getRcQuery(), _cl, _sessionFactoryImpl);
   }


   @Override
   public void closeConnection()
   {
      Throwable reThrow = null;

      try
      {
         new ReflectionCaller(_sessionFactoryImpl.getEntityManagerFactory()).callMethod("close");
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
      return new ArrayList<>(_infoDataByClassName.values());
   }

   private void initMappedClassInfos()
   {
      if(null != _infoDataByClassName)
      {
         return;
      }

      _infoDataByClassName = MappedClassInfoLoader.getMappedClassInfos(_sessionFactoryImpl, _cl, _jpaRootPackage, _server);
   }


   @Override
   public HibernateSqlConnectionData getHibernateSqlConnectionData()
   {
      return JDBCConnectionAccess.getHibernateSqlConnectionData(_cl, getRcHibernateSession());
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
         ret.putSessionAdminException("Exception occurced during call of Session.getTransaction().begin()", HibernateServerExceptionUtil.prepareTransport(t, _server));
      }

      try
      {
         QueryCreationResult queryCreationResult = createQuery(hqlQuery);

         if(null != queryCreationResult.getMessagePanelInfoText())
         {
            ret.setMessagePanelInfoText(queryCreationResult.getMessagePanelInfoText());
         }

         ReflectionCaller rcQuery = queryCreationResult.getRcQuery();


         if (isDataUpdate(hqlQuery))
         {
            int updateCount = (Integer)rcQuery.callMethod("executeUpdate").getCallee();
            ret.setUpdateCount(updateCount);
            getRcHibernateSession().callMethod("getTransaction").callMethod("commit");

            // No rollback TX after commit because it leads to
            // org.hibernate.TransactionException: Transaction not successfully started
            // tryRollbackTx(ret);

         }
         else
         {
            if (0 <= sqlNbrRowsToShow)
            {
               rcQuery = rcQuery.callMethod("setMaxResults", new RCParam().add(sqlNbrRowsToShow, Integer.TYPE));
            }

            queryResList = (List) rcQuery.callMethod("list").getCallee();
            tryRollbackTx(ret);

         }
      }
      catch (Throwable t)
      {
         ret.setExceptionOccuredWhenExecutingQuery(HibernateServerExceptionUtil.prepareTransport(t, _server));
         tryRollbackTx(ret);
      }

      if (null != queryResList)
      {
         ret.setQueryResultList(
               new ObjectSubstituteFactory(_cl).replaceObjectsWithSubstitutes(queryResList, _infoDataByClassName));
      }

      return ret;
   }

   private QueryCreationResult createQuery(String hqlQuery)
   {
      JDBCTemporalEscapeParse jdbcTemporalEscapeParse = new JDBCTemporalEscapeParse(hqlQuery);

      QueryCreationResult queryCreationResult = new QueryCreationResult();
      ReflectionCaller rcQuery;
      if(jdbcTemporalEscapeParse.hasEscapes())
      {
         rcQuery = getRcHibernateSession().callMethod("createQuery", jdbcTemporalEscapeParse.getHql());

         TreeMap<String, Date> datesByParamName = jdbcTemporalEscapeParse.getDatesByParamName();
         for (String paramName : datesByParamName.keySet())
         {
            RCParam param = new RCParam();
            param.add(paramName, String.class);
            param.add(datesByParamName.get(paramName), Object.class);
            rcQuery.callMethod("setParameter", param);
         }

         queryCreationResult.setMessagePanelInfoText("Temporal values were parameterized:\n" + jdbcTemporalEscapeParse.getMessagePanelInfoText());
      }
      else
      {
         rcQuery = getRcHibernateSession().callMethod("createQuery", hqlQuery);
      }

      queryCreationResult.setRcQuery(rcQuery);
      return queryCreationResult;
   }

   private void tryRollbackTx(HqlQueryResult ret)
   {
      try
      {
         getRcHibernateSession().callMethod("getTransaction").callMethod("rollback");
      }
      catch (Throwable t)
      {
         ret.putSessionAdminException("Exception occurced during call of Session.getTransaction().rollback()", HibernateServerExceptionUtil.prepareTransport(t, _server));
      }
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


   private ReflectionCaller getRcHibernateSession()
   {
      try
      {
         if(null == _rcHibernateSession)
         {
            if (null == _driverClassName)
            {
               _rcHibernateSession = new ReflectionCaller(_sessionFactoryImpl.getSessionFactory()).callMethod("openSession");
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

               //_rcHibernateSession = new ReflectionCaller(_sessionFactoryImpl.getSessionFactory()).callMethod("openStatelessSession", rcParam);

               ReflectionCaller sessionBuilderRc = new ReflectionCaller(_sessionFactoryImpl.getSessionFactory()).callMethod("withOptions");

               _rcHibernateSession = sessionBuilderRc.callMethod("connection", rcParam).callMethod("openSession");
            }
         }

         return _rcHibernateSession;
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }


}
