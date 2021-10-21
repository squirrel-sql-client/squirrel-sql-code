package net.sourceforge.squirrel_sql.client.session.connectionpool;

import net.sourceforge.squirrel_sql.client.mainframe.action.openconnection.OpenConnectionUtil;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnectionState;
import net.sourceforge.squirrel_sql.fw.timeoutproxy.TimeOutUtil;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SessionConnectionPool
{
   private static final ILogger s_log = LoggerController.createLogger(SessionConnectionPool.class);

   private SessionConnectionPoolProperties _sessionConnectionPoolProperties = new SessionConnectionPoolProperties();

   private final SQLConnection _masterConnection;
   private SessionProperties _sessionProperties;
   private final ISQLAlias _sqlAlias;
   private final String _userName;
   private final String _password;
   private final MessageHandlerReader _messageHandlerReader;
   private CatalogComboReader _catalogComboReader;

   private HashMap<ISQLConnection, Integer> _querySQLConnections_checkOutCount = new HashMap<>();

   private SessionConnectionPoolChangeListener _sessionConnectionPoolChangeListener;

   /**
    * When autoCommit is set to false the connection pool is switched off.
    * I.e. {@link #checkOutUserQuerySQLConnection()} will return the {@link #_masterConnection} only.
    * Note: this variable is read by the UI. That's why _masterConnection.getAutoCommit() is buffered here.
    *
    * This variable's value is deliberately false by default.
    * Which means that as long as we don't
    */
   private volatile boolean _autoCommit;


   public SessionConnectionPool(SQLConnection masterConnection,
                                ISQLAlias sqlAlias,
                                String userName,
                                String password,
                                SessionProperties sessionProperties,
                                MessageHandlerReader messageHandlerReader,
                                CatalogComboReader catalogComboReader)
   {
      _masterConnection = masterConnection;

      try
      {
         _autoCommit = TimeOutUtil.callWithTimeout(() -> _masterConnection.getAutoCommit());
      }
      catch (Exception e)
      {
         s_log.error("Failed to read the connection's autoCommit flag.", e);
      }

      _sessionProperties = sessionProperties;
      _sqlAlias = sqlAlias;
      _userName = userName;
      _password = password;
      _messageHandlerReader = messageHandlerReader;
      _catalogComboReader = catalogComboReader;
   }

   public synchronized ISQLConnection getMasterSQLConnection()
   {
      return _masterConnection;
   }

   public synchronized ISQLConnection checkOutUserQuerySQLConnection()
   {
      try
      {
         if(false == _autoCommit || 0 == _sessionConnectionPoolProperties.getMaxQuerySqlConnectionsCount() )
         {
            return _masterConnection;
         }

         // From here on we know the pool is active.

         if ( 0 == _querySQLConnections_checkOutCount.size())
         {
            // No connection created yet: Create the first and return it.
            final ISQLConnection buf = createNewQuerySQLConnection();
            return buf;
         }


         ISQLConnection ret = getMinCheckoutCountQuerySQLConnections();
         int checkOutCount = _querySQLConnections_checkOutCount.get(ret);

         if ( 0 == checkOutCount)
         {
            _querySQLConnections_checkOutCount.put(ret, 1);
            return ret;
         }

         if ( _querySQLConnections_checkOutCount.size() < _sessionConnectionPoolProperties.getMaxQuerySqlConnectionsCount())
         {
            final ISQLConnection buf = createNewQuerySQLConnection();
            return buf;
         }

         _querySQLConnections_checkOutCount.put(ret, ++checkOutCount);
         return ret;
      }
      finally
      {
         fireChanged();
      }
   }


   public synchronized void returnUserQuerySQLConnection(ISQLConnection conn)
   {
      try
      {
         if(_masterConnection != conn)
         {
            _querySQLConnections_checkOutCount.put(conn, Math.max(_querySQLConnections_checkOutCount.get(conn) - 1, 0));
         }
      }
      finally
      {
         fireChanged();
      }
   }



   private ISQLConnection getMinCheckoutCountQuerySQLConnections()
   {
      int minCeckoutCount = Integer.MAX_VALUE;
      ISQLConnection ret = null;

      for (Map.Entry<ISQLConnection, Integer> entry : _querySQLConnections_checkOutCount.entrySet())
      {
         if(0 == entry.getValue())
         {
            return entry.getKey();
         }

         if(entry.getValue() < minCeckoutCount)
         {
            ret = entry.getKey();
            minCeckoutCount = entry.getValue();
         }
      }

      return ret;

   }

   /**
    * Note: This method will not be called when {@link #_autoCommit} is false;
    * I.e. query connections always be created with autoCommit true.
    */
   private ISQLConnection createNewQuerySQLConnection()
   {
      try
      {
         if(false == _autoCommit)
         {
            throw new IllegalStateException("How could we get here?");
         }

         SQLConnectionState sqlConnectionState = new SQLConnectionState();
         sqlConnectionState.saveState(_masterConnection, _sessionProperties, _messageHandlerReader.getMessageHandler(), _catalogComboReader.getCatalogFormComboBox());

         final SQLConnection sqlConnection = OpenConnectionUtil.createSQLConnection(_sqlAlias, _userName, _password, sqlConnectionState.getConnectionProperties());

         sqlConnectionState.restoreState(sqlConnection, _messageHandlerReader.getMessageHandler());

         _querySQLConnections_checkOutCount.put(sqlConnection, 1);

         return sqlConnection;
      }
      finally
      {
         fireChanged();
      }
   }

   public void close()
   {
      getAllSQLConnections().forEach(con -> closeConnection(con));
   }

   private void closeConnection(ISQLConnection con)
   {
      try
      {
         TimeOutUtil.invokeWithTimeout(() -> con.close());
      }
      catch (Throwable t)
      {
         s_log.error("Error closing connection", t);
      }
   }

   public synchronized Set<ISQLConnection> getAllSQLConnections()
   {
      HashSet<ISQLConnection> ret = new HashSet<>();
      ret.add(_masterConnection);
      ret.addAll(_querySQLConnections_checkOutCount.keySet());
      return ret;
   }

   /**
    * When autoCommit is set to false the connection pool is switched off.
    * I.e. {@link #checkOutUserQuerySQLConnection()} will return the {@link #_masterConnection} only.
    * That is why we change autoCommit of the _masterConnection only.
    */
   public void setSessionAutoCommit(boolean autoCommit)
   {
      _autoCommit = false;
      try
      {
         TimeOutUtil.invokeWithTimeout(() -> _masterConnection.setAutoCommit(autoCommit));
         _autoCommit = autoCommit;
      }
      catch (Exception e)
      {
         s_log.error("Setting autoCommit failed", e);
         try
         {
            _autoCommit = TimeOutUtil.callWithTimeout(() -> _masterConnection.getAutoCommit());
         }
         catch (Exception ex)
         {
            s_log.error("Error trying to read autoCommit after setting failed (former exception is of higher interest): " + e);
         }

         throw Utilities.wrapRuntime(e);
      }
      finally
      {
         fireChanged();
      }
   }

   public void setSessionCatalog(String selectedCatalog)
   {
      setPropertyInAllConnections("catalog", con -> con.setCatalog(selectedCatalog));
   }

   public void setSessionCommitOnClose(boolean commitOnClosingConnection)
   {
      setPropertyInAllConnections("commitOnClosingConnection", con -> con.setCommitOnClose(commitOnClosingConnection));
   }


   private void setPropertyInAllConnections(String propertyDesc, ConnectionPropertySetter connectionPropertySetter)
   {
      try
      {
         TimeOutUtil.invokeWithTimeout(() -> connectionPropertySetter.setProperty(_masterConnection));
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }

      HashSet<ISQLConnection> toClose = new HashSet<>();
      for (ISQLConnection con : _querySQLConnections_checkOutCount.keySet())
      {
         try
         {
            TimeOutUtil.invokeWithTimeout(() -> connectionPropertySetter.setProperty(con));
         }
         catch (Exception e)
         {
            s_log.error("Error setting " + propertyDesc + " for query connections", e);
            toClose.add(con);
         }
      }

      toClose.forEach(con -> closeConnection(con));
   }

   public void setPoolChangeListner(SessionConnectionPoolChangeListener sessionConnectionPoolChangeListener)
   {
      _sessionConnectionPoolChangeListener = sessionConnectionPoolChangeListener;
   }

   private void fireChanged()
   {
      if (null != _sessionConnectionPoolChangeListener)
      {
         GUIUtils.processOnSwingEventThread(() -> _sessionConnectionPoolChangeListener.changed());
      }
   }

   public SessionConnectionPoolProperties getConnectionPoolProperties()
   {
      return _sessionConnectionPoolProperties;
   }


   public boolean isAutoCommit()
   {
      return _autoCommit;
   }

   public int getInUseQuerySqlConnectionsCount()
   {
      if(false == _autoCommit)
      {
         return 0;
      }

      return (int) _querySQLConnections_checkOutCount.values().stream().filter(inUseCount ->  inUseCount > 0).count();

   }

   public int getMaxCheckoutCount()
   {
      return _querySQLConnections_checkOutCount.values().stream().max(Integer::compare).get();
   }
}
