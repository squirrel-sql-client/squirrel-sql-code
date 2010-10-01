package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.HibernateServerConnection;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.HibernateSqlConnectionData;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.MappedClassInfoData;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ServerMain;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

public class HibernateConnection
{
   private static ILogger s_log = LoggerController.createLogger(HibernateConnection.class);

   private HibernateServerConnection _hibernateServerConnection;
   private boolean _process;
   private ServerMain _serverMain;
   private boolean _endProcessOnDisconnect;
   private ArrayList<MappedClassInfo> _mappedClassInfos;


   private RMISecurityManager _rmiSecurityManager =
         new RMISecurityManager()
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

   public ArrayList<String> generateSQL(String hqlQuery)
   {
      try
      {
         if (_process)
         {
            SecurityManager old =System.getSecurityManager();

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
         if(_endProcessOnDisconnect)
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
         if(null == _mappedClassInfos)
         {
            _mappedClassInfos = new ArrayList<MappedClassInfo>();

            ArrayList<MappedClassInfoData> mappedClassInfoData;
            if (_process)
            {
               SecurityManager old =System.getSecurityManager();

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

   public Class getPersistenCollectionClass()
   {
      try
      {
         return _hibernateServerConnection.getPersistenCollectionClass();
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

   public List createQueryList(String hqlQuery, int sqlNbrRowsToShow)
   {
      try
      {
         if (_process)
         {
            SecurityManager old =System.getSecurityManager();

            try
            {
               System.setSecurityManager(_rmiSecurityManager);
               return _hibernateServerConnection.createQueryList(hqlQuery, sqlNbrRowsToShow);
            }
            finally
            {
               System.setSecurityManager(old);
            }
         }
         else
         {
            return _hibernateServerConnection.createQueryList(hqlQuery, sqlNbrRowsToShow);
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
