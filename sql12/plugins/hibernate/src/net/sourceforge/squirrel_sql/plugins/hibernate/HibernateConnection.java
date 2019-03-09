package net.sourceforge.squirrel_sql.plugins.hibernate;

import java.rmi.RemoteException;
import java.security.Permission;
import java.util.ArrayList;

import net.sourceforge.squirrel_sql.client.session.JdbcConnectionData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.*;

public class HibernateConnection
{

   private static final ILogger s_log = LoggerController.createLogger(HibernateConnection.class);

   private final HibernateServerConnection _hibernateServerConnection;
   private final boolean _process;
   private final ServerMain _serverMain;
   private final boolean _endProcessOnDisconnect;
   private ArrayList<MappedClassInfo> _mappedClassInfos;

   private final SecurityManager _rmiSecurityManager = new SecurityManager()
   {
      @Override
      public void checkPermission(Permission perm)
      {
      }
   };

   public HibernateConnection(HibernateServerConnection hibernateServerConnection, boolean process, ServerMain serverMain, boolean endProcessOnDisconnect)
   {
      _hibernateServerConnection = hibernateServerConnection;
      _process = process;
      _serverMain = serverMain;
      _endProcessOnDisconnect = endProcessOnDisconnect;
   }

   public String generateSQL(String hqlQuery)
   {
      try
      {
         if (_process)
         {
            SecurityManager old = System.getSecurityManager();

            try
            {
               System.setSecurityManager(_rmiSecurityManager);
               return _hibernateServerConnection.generateSQL(hqlQuery);
            }
            finally
            {
               System.setSecurityManager(old);
            }
         }
         else
         {
            return _hibernateServerConnection.generateSQL(hqlQuery);
         }
      }
      catch (RemoteException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void close()
   {
      if (_process)
      {
         if (_endProcessOnDisconnect)
         {
            try
            {
               _hibernateServerConnection.closeConnection();
            }
            catch (Throwable t)
            {
               s_log.error("Error closing Hibernate connection.", t);
            }

            try
            {
               _serverMain.exit();
            }
            catch (Throwable t)
            {
               // This call will result in failure because the process VM exits during this call and will not return.
            }
         }
      }
      else
      {
         try
         {
            _hibernateServerConnection.closeConnection();
         }
         catch (Throwable t)
         {
            s_log.error("Error closing Hibernate connection.", t);
         }
      }
   }

   public ArrayList<MappedClassInfo> getMappedClassInfos()
   {
      try
      {
         if (null == _mappedClassInfos)
         {
            _mappedClassInfos = new ArrayList<MappedClassInfo>();

            ArrayList<MappedClassInfoData> mappedClassInfoData;
            if (_process)
            {
               SecurityManager old = System.getSecurityManager();

               try
               {
                  System.setSecurityManager(_rmiSecurityManager);
                  mappedClassInfoData = _hibernateServerConnection.getMappedClassInfoData();
               }
               finally
               {
                  System.setSecurityManager(old);
               }

            }
            else
            {
               mappedClassInfoData = _hibernateServerConnection.getMappedClassInfoData();
            }

            for (MappedClassInfoData aMappedClassInfoData : mappedClassInfoData)
            {
               _mappedClassInfos.add(new MappedClassInfo(aMappedClassInfoData));
            }
         }

         return _mappedClassInfos;
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }

   }

   public HibernateSqlConnectionData getHibernateSqlConnectionData()
   {
      try
      {
         return _hibernateServerConnection.getHibernateSqlConnectionData();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public HqlQueryResult createQueryList(String hqlQuery, int sqlNbrRowsToShow, JdbcConnectionData jdbcData)
   {
      if (_process)
      {
         SecurityManager old = System.getSecurityManager();

         try
         {
            System.setSecurityManager(_rmiSecurityManager);
            return callCreateQueryList(hqlQuery, sqlNbrRowsToShow, jdbcData);
         }
         finally
         {
            System.setSecurityManager(old);
         }
      }
      else
      {
         return callCreateQueryList(hqlQuery, sqlNbrRowsToShow, jdbcData);
      }
   }

   private HqlQueryResult callCreateQueryList(String hqlQuery, int sqlNbrRowsToShow, JdbcConnectionData jdbcData)
   {
      try
      {
         if (null == jdbcData)
         {
            return _hibernateServerConnection.createQueryList(hqlQuery, sqlNbrRowsToShow);
         }
         else
         {
            return _hibernateServerConnection.createQueryList(hqlQuery, sqlNbrRowsToShow, jdbcData.getDriverClassName(), jdbcData.getUrl(), jdbcData.getUser(), jdbcData.getPassword());
         }
      }
      catch (RemoteException e)
      {
         throw new RuntimeException(e);
      }
   }
}
