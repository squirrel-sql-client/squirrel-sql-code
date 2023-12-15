package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications copyright (C) 2001-2004 Johan Compagner
 * jcompagner@j-com.nl
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasConnectionProperties;
import net.sourceforge.squirrel_sql.client.gui.db.encryption.AliasPasswordHandler;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.client.gui.session.*;
import net.sourceforge.squirrel_sql.client.mainframe.action.openconnection.OpenConnectionCommand;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.action.reconnect.ReconnectInfo;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SavedSessionJsonBean;
import net.sourceforge.squirrel_sql.client.session.connectionpool.SessionConnectionPool;
import net.sourceforge.squirrel_sql.client.session.event.SimpleSessionListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.parser.ParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.parser.ParserEventsProcessorDummy;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.*;
import net.sourceforge.squirrel_sql.fw.sql.databasemetadata.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.TokenizerSessPropsInteractions;
import net.sourceforge.squirrel_sql.fw.util.*;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Think of a session as being the users view of the database. IE it includes
 * the database connection and the UI.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class Session implements ISession
{
   private static final ILogger s_log = LoggerController.createLogger(Session.class);

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(Session.class);

   private String _title = "";

   private SessionPanel _sessionSheet;

   /**
    * The <TT>IIdentifier</TT> that uniquely identifies this object.
    */
   private final IIdentifier _id;

   /**
    * Application API.
    */
   private IApplication _app;

   /**
    * Connection to database.
    */
   private SessionConnectionPool _sessionConnectionPool;

   /**
    * Driver used to connect to database.
    */
   private ISQLDriver _driver;

   /**
    * Alias describing how to connect to database.
    */
   private SQLAlias _alias;

   private String _user;
   private String _password;

   /**
    * Properties for this session.
    */
   private SessionProperties _props;

   /**
    * Objects stored in session. Each entry is a <TT>Map</TT>
    * keyed by <TT>IPlugin.getInternalName()</TT>. Each <TT>Map</TT>
    * contains the objects saved for the plugin.
    */
   private final Map<String, Map<String, Object>> _pluginObjects = new HashMap<>();

   private IMessageHandler _msgHandler = NullMessageHandler.getInstance();

   /**
    * Xref info about the current connection.
    */
   private final net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo _schemaInfo;

   /**
    * Set to <TT>true</TT> once session closed.
    */
   private boolean _closed;

   private SQLConnectionListener _connLis = null;

   private ISessionWidget _activeActiveSessionWindow;
   private SessionInternalFrame _sessionInternalFrame;
   private Hashtable<IIdentifier, IParserEventsProcessor> _parserEventsProcessorsByEntryPanelIdentifier = new Hashtable<IIdentifier, IParserEventsProcessor>();


   /**
    * flag to track whether or not the table data has been loaded in the object tree
    */
   private volatile boolean _finishedLoading = false;

   /**
    * flag to track whether or not the plugins have finished loading for this new session
    */
   private volatile boolean _pluginsFinishedLoading = false;

   /**
    * This is set to true when a plugin sets a custom IQueryTokenizer
    */
   private boolean customTokenizerInstalled = false;

   private IQueryTokenizer tokenizer = null;

   /**
    * The default exception formatter
    */
   private DefaultExceptionFormatter formatter = new DefaultExceptionFormatter();

   private SessionConnectionKeepAlive _sessionConnectionKeepAlive = null;
   private SimpleSessionListenerManager _simpleSessionListenerManager;


   private HashMap<Object, Object> _sessionLocales = new HashMap<>();

   private SavedSessionJsonBean _savedSession;
   private CurrentSchemaModel _currentSchemaModel;
   private ModificationAwareSessionTitle _modificationAwareSessionTitle = new ModificationAwareSessionTitle();

   /**
    * Create a new session.
    *
    * @throws IllegalArgumentException if any parameter is null.
    * @param   app         Application API.
    * @param   driver      JDBC driver for session.
    * @param   alias      Defines URL to database.
    * @param   conn      Connection to database.
    * @param   user      User name connected with.
    * @param   password   Password for <TT>user</TT>
    * @param   sessionId   ID that uniquely identifies this session.
    */
   public Session(IApplication app, ISQLDriver driver, SQLAlias alias,
                  SQLConnection conn, String user, String password,
                  IIdentifier sessionId)
   {
      if (app == null)
      {
         throw new IllegalArgumentException("null IApplication passed");
      }
      if (driver == null)
      {
         throw new IllegalArgumentException("null ISQLDriver passed");
      }
      if (alias == null)
      {
         throw new IllegalArgumentException("null SQLAlias passed");
      }
      if (conn == null)
      {
         throw new IllegalArgumentException("null SQLConnection passed");
      }
      if (sessionId == null)
      {
         throw new IllegalArgumentException("sessionId == null");
      }

      _schemaInfo = new SchemaInfo(app);

      _app = app;
      _driver = driver;

      _alias = new SQLAlias();

      _alias.assignFrom(alias, true);

      _props = (SessionProperties) _app.getSquirrelPreferences().getSessionProperties().clone();

      _user = user;
      _password = password;
      _id = sessionId;

      //_conn = conn;
      _sessionConnectionPool = createConnectionPool(conn);

      setupTitle();

      _connLis = new SQLConnectionListener();
      _sessionConnectionPool.getMasterSQLConnection().addPropertyChangeListener(_connLis);

      checkDriverVersion();

      // Start loading table/column info about the current database.
      _app.getThreadPool().addTask(new Runnable()
      {
         public void run()
         {
            _schemaInfo.initialLoad(Session.this);
            _finishedLoading = true;
         }
      });
      startKeepAliveTaskIfNecessary();
      _simpleSessionListenerManager = new SimpleSessionListenerManager(app, this);

      _currentSchemaModel = new CurrentSchemaModel(this);
   }

   private SessionConnectionPool createConnectionPool(SQLConnection conn)
   {
      return new SessionConnectionPool(conn, _alias, _user, _password, _props, () -> _msgHandler, () -> _sessionSheet.getSelectedCatalogFromCatalogsComboBox());
   }

   private void startKeepAliveTaskIfNecessary()
   {
      SQLAliasConnectionProperties connProps = _alias.getConnectionProperties();

      if (connProps.isEnableConnectionKeepAlive())
      {
         String keepAliveSql = connProps.getKeepAliveSqlStatement();

         long sleepMillis = connProps.getKeepAliveSleepTimeSeconds() * 1000;

         if (StringUtilities.isEmpty(keepAliveSql, true))
         {
            String msg = s_stringMgr.getString("alias.properties.no.keepAliveSql");
            getApplication().getMessageHandler().showErrorMessage(msg);
            s_log.error(msg);
            return;
         }


         _sessionConnectionKeepAlive = new SessionConnectionKeepAlive(_sessionConnectionPool, sleepMillis, keepAliveSql, _alias.getName());

         _app.getThreadPool().addTask(_sessionConnectionKeepAlive, "Session Connection Keep-Alive (" + _alias.getName() + ")");
      }
   }

   private void stopKeepAliveTaskIfNecessary()
   {
      if (_sessionConnectionKeepAlive != null)
      {
         _sessionConnectionKeepAlive.setStopped(true);
      }
   }

   /**
    * Close this session.
    *
    * @throws SQLException Thrown if an error closing the SQL connection. The session
    * will still be closed even though the connection may not have
    * been.
    */
   public void close() throws SQLException
   {
      if (!_closed)
      {
         stopKeepAliveTaskIfNecessary();
         if (null != _sessionConnectionPool)
         {
            // _conn is null when session is closed after reconnect (ctrl t) failure.
            _sessionConnectionPool.getMasterSQLConnection().removePropertyChangeListener(_connLis);
         }
         _connLis = null;


         IParserEventsProcessor[] procs = _parserEventsProcessorsByEntryPanelIdentifier.values().toArray(new IParserEventsProcessor[0]);


         for (int i = 0; i < procs.length; i++)
         {
            try
            {
               if (procs[i] instanceof ParserEventsProcessor)
               {
                  ((ParserEventsProcessor) procs[i]).endProcessing();
               }
            }
            catch (Exception e)
            {
            }
         }

         _schemaInfo.dispose();


         try
         {
            closeConnectionPool();
         }
         finally
         {
            // This is set here as SessionPanel.dispose() will attempt
            // to close the session.
            _closed = true;

            if (_sessionSheet != null)
            {
               _sessionSheet.sessionHasClosed();
               _sessionSheet = null;
            }

            /*
             *  If the session is closed, we can remove all SQLResultTabs.
             *  This would be not be necessary, if all closed Sessions will be ready for garbage collecting.
             *  Often, some code keeps a reference to this session and the Session is not ready for garbage collecting.
             *  E.g. when dialogs are only set to visible = false and not disposed correctly.
             *  To reduce the used memory by such not reachable sessions, we remove all SQLResultTabs, when the session is closed.
             *  This helps users, they often open and close sessions without restarting SQuirrel.
             */
            if (_sessionInternalFrame != null)
            {
               _sessionInternalFrame.getMainSQLPanelAPI().closeAllSQLResultTabs(true);
            }


         }
      }
   }

   /**
    * Commit the current SQL transaction.
    */
   public synchronized void commit()
   {
      try
      {
         getSQLConnection().commit();
         final String msg = s_stringMgr.getString("Session.commit");
         _msgHandler.showMessage(msg);
      }
      catch (Throwable ex)
      {
         _msgHandler.showErrorMessage(ex, formatter);
      }
   }

   /**
    * Rollback the current SQL transaction.
    */
   public synchronized void rollback()
   {
      try
      {
         getSQLConnection().rollback();
         final String msg = s_stringMgr.getString("Session.rollback");
         _msgHandler.showMessage(msg);
      }
      catch (Exception ex)
      {
         _msgHandler.showErrorMessage(ex, formatter);
      }
   }

   /**
    * Return the unique identifier for this session.
    *
    * @return the unique identifier for this session.
    */
   public IIdentifier getIdentifier()
   {
      return _id;
   }

   /**
    * Retrieve whether this session has been closed.
    *
    * @return <TT>true</TT> if session closed else <TT>false</TT>.
    */
   public boolean isClosed()
   {
      return _closed;
   }

   /**
    * Return the Application API object.
    *
    * @return the Application API object.
    */
   public IApplication getApplication()
   {
      return _app;
   }

   /**
    * @return <TT>SQLConnection</TT> for this session.
    */
   public ISQLConnection getSQLConnection()
   {
      checkConnectionPool();
      return _sessionConnectionPool.getMasterSQLConnection();
   }

   @Override
   public ISQLConnection checkOutUserQuerySQLConnection()
   {
      checkConnectionPool();
      return _sessionConnectionPool.checkOutUserQuerySQLConnection();
   }

   @Override
   public void returnUserQuerySQLConnection(ISQLConnection conn)
   {
      // During reconnnect connections of pools that were closed may be returned. We ignore those.
      if(null != _sessionConnectionPool)
      {
         _sessionConnectionPool.returnUserQuerySQLConnection(conn);
      }
   }

   /**
    * @return <TT>ISQLDriver</TT> for this session.
    */
   public ISQLDriver getDriver()
   {
      return _driver;
   }

   /**
    * @return <TT>SQLAlias</TT> for this session.
    */
   public SQLAlias getAlias()
   {
      return _alias;
   }

   public SessionProperties getProperties()
   {
      return _props;
   }

   /**
    * Retrieve the schema information object for this session.
    */
   public SchemaInfo getSchemaInfo()
   {
      return _schemaInfo;
   }

   public synchronized Object getPluginObject(IPlugin plugin, String key)
   {
      if (plugin == null)
      {
         throw new IllegalArgumentException("Null IPlugin passed");
      }
      if (key == null)
      {
         throw new IllegalArgumentException("Null key passed");
      }
      Map<String, Object> map = _pluginObjects.get(plugin.getInternalName());
      if (map == null)
      {
         map = new HashMap<String, Object>();
         _pluginObjects.put(plugin.getInternalName(), map);
      }
      return map.get(key);
   }

   /**
    * Add the passed action to the session toolbar.
    *
    * @param   action   Action to be added.
    */
   public void addToToolbar(Action action)
   {
      _sessionSheet.addToToolbar(action);
   }

   public void addSeparatorToToolbar()
   {
      _sessionSheet.addSeparatorToToolbar();
   }


   public synchronized Object putPluginObject(IPlugin plugin, String key,
                                              Object value)
   {
      if (plugin == null)
      {
         throw new IllegalArgumentException("Null IPlugin passed");
      }
      if (key == null)
      {
         throw new IllegalArgumentException("Null key passed");
      }
      Map<String, Object> map = _pluginObjects.get(plugin.getInternalName());
      if (map == null)
      {
         map = new HashMap<String, Object>();
         _pluginObjects.put(plugin.getInternalName(), map);
      }
      return map.put(key, value);
   }

   public synchronized void removePluginObject(IPlugin plugin, String key)
   {
      if (plugin == null)
      {
         throw new IllegalArgumentException("Null IPlugin passed");
      }
      if (key == null)
      {
         throw new IllegalArgumentException("Null key passed");
      }
      Map<String, Object> map = _pluginObjects.get(plugin.getInternalName());
      if (map != null)
      {
         map.remove(key);
      }
   }

   private synchronized void closeConnectionPool() throws SQLException
   {
      if (_sessionConnectionPool != null)
      {
         stopKeepAliveTaskIfNecessary();
         try
         {
            _sessionConnectionPool.close();
         }
         finally
         {
            _sessionConnectionPool = null;
         }
      }
   }

   @Override
   public JdbcConnectionData getJdbcData()
   {
      IIdentifier driverID = _alias.getDriverIdentifier();
      ISQLDriver sqlDriver = _app.getAliasesAndDriversManager().getDriver(driverID);

      return new JdbcConnectionData(sqlDriver.getDriverClassName(), _alias.getUrl(), _user, _password);
   }


   /**
    * Reconnect to the database.
    *
    * @param reconnectInfo
    */
   public void reconnect(ReconnectInfo reconnectInfo)
   {
      final SQLConnectionState connState = new SQLConnectionState();

      try
      {
         if(null != _sessionConnectionPool)
         {
            connState.saveState(_sessionConnectionPool.getMasterSQLConnection(), getProperties(), _msgHandler, _sessionSheet.getSelectedCatalogFromCatalogsComboBox());
         }
         else
         {
            connState.saveState(null, getProperties(), _msgHandler, _sessionSheet.getSelectedCatalogFromCatalogsComboBox());
         }
      }
      catch (Exception e)
      {
         s_log.error("Failed to save connection state", e);
      }

      final OpenConnectionCommand cmd = new OpenConnectionCommand(_alias, _user, _password, connState.getConnectionProperties(), reconnectInfo);
      try
      {
         closeConnectionPool();
         _app.getSessionManager().fireConnectionClosedForReconnect(this);
      }
      catch (SQLException ex)
      {
         final String msg = s_stringMgr.getString("Session.error.connclose");
         s_log.error(msg, ex);
         _msgHandler.showErrorMessage(msg);
         _msgHandler.showErrorMessage(ex, this.getExceptionFormatter());
      }
      try
      {
         cmd.execute(t -> reconnectDone(connState, cmd, t, reconnectInfo));
      }
      catch (Throwable t)
      {
         final String msg = s_stringMgr.getString("Session.reconnError", _alias.getName());
         _msgHandler.showErrorMessage(msg + "\n" + t.toString());
         s_log.error(msg, t);
         _app.getSessionManager().fireReconnectFailed(this);
      }
   }

   private void reconnectDone(SQLConnectionState connState, OpenConnectionCommand cmd, Throwable t, ReconnectInfo reconnectInfo)
   {
      try
      {
         if (null != t)
         {
            throw t;
         }

         if (false == StringUtilities.isEmpty(reconnectInfo.getUrl()))
         {
            _alias.setUrl(reconnectInfo.getUrl());
         }

         if (false == StringUtilities.isEmpty(reconnectInfo.getUser()))
         {
            _user = reconnectInfo.getUser();
            _alias.setUserName(_user);
         }

         if (false == StringUtilities.isEmpty(reconnectInfo.getPassword()))
         {
            _password = reconnectInfo.getPassword();
            AliasPasswordHandler.setPassword(_alias, _password);
         }

         if(reconnectInfo.isSkipOpeningNewConnection())
         {
            String usrRequestedNoRecoonectMsg = s_stringMgr.getString("Session.reconn.skiped.on.user.request");
            throw new RuntimeException(usrRequestedNoRecoonectMsg);
         }

         final SQLConnection conn = cmd.getSQLConnection();
         try
         {
            if (connState != null)
            {
               connState.restoreState(conn, _msgHandler);
            }
         }
         finally
         {
            _sessionConnectionPool = createConnectionPool(conn);
         }

         if (connState != null)
         {
            // Do this after _sessionConnectionPool is initialized because it accesses the pool in SetSessionAutoCommitTask
            _props.setAutoCommit(connState.getAutoCommit());
         }

         final String msg = s_stringMgr.getString("Session.reconn", _alias.getName());
         _msgHandler.showMessage(msg);
         _app.getSessionManager().fireReconnected(this);
         startKeepAliveTaskIfNecessary();
      }
      catch (Throwable th)
      {
         final String msg = s_stringMgr.getString("Session.reconnError", _alias.getName());
         _msgHandler.showErrorMessage(msg + "\n" + th.toString());
         s_log.error(msg, th);
         _app.getSessionManager().fireReconnectFailed(this);
      }
   }

   public void setMessageHandler(IMessageHandler handler)
   {
      _msgHandler = handler != null ? handler : NullMessageHandler.getInstance();
   }

   public void setSessionInternalFrame(SessionInternalFrame sif)
   {
      _sessionInternalFrame = sif;

      // This is a reasonable default and makes initialization code run well
      _activeActiveSessionWindow = sif;

      _sessionSheet = sif.getSessionPanel();
   }

   public synchronized SessionInternalFrame getSessionInternalFrame()
   {
      return _sessionInternalFrame;
   }

   public synchronized SessionPanel getSessionPanel()
   {
      return _sessionSheet;
   }

   /**
    * Select a tab in the main tabbed pane.
    *
    * @param   tabIndex   The tab to select. @see ISession.IMainTabIndexes
    * @throws IllegalArgumentException Thrown if an invalid <TT>tabId</TT> passed.
    */
   public void selectMainTab(int tabIndex)
   {
      _sessionSheet.selectMainTab(tabIndex);
   }

   @Override
   public int getMainPanelTabIndex(IMainPanelTab mainPanelTab)
   {
      return _sessionSheet.getMainPanelTabIndex(mainPanelTab);
   }

   public int getSelectedMainTabIndex()
   {
      return _sessionSheet.getSelectedMainTabIndex();
   }

   @Override
   public IMainPanelTab getSelectedMainTab()
   {
      return _sessionSheet.getSelectedMainTab();
   }


   /**
    * Add a tab to the main tabbed panel.
    *
    * @return the index of the new tab that was added.
    * @param   tab    The tab to be added.
    * @throws IllegalArgumentException Thrown if a <TT>null</TT> <TT>IMainPanelTab</TT> passed.
    */
   public int addMainTab(IMainPanelTab tab)
   {
      return _sessionSheet.addMainTab(tab);
   }

   /**
    * Retrieve the descriptive title of this session.
    *
    * @return The descriptive title of this session.
    */
   public String getTitle()
   {
      return getTitleModificationAware().getTitle();
   }

   @Override
   public ModificationAwareSessionTitle getTitleModificationAware()
   {
      return _modificationAwareSessionTitle;
   }

   public void setTitle(String newTitle)
   {
      _modificationAwareSessionTitle.setTitle(newTitle);
   }

   public String toString()
   {
      return getTitle();
   }

   private void setupTitle()
   {
      String catalog = null;
      try
      {
         catalog = getSQLConnection().getCatalog();
      }
      catch (SQLException ex)
      {
         s_log.error("Error occurred retrieving current catalog from Connection", ex);
      }
      if (catalog == null)
      {
         catalog = "";
      }
      else
      {
         catalog = "(" + catalog + ")";
      }

      String title = null;
      String user = _user != null ? _user : "";
      if (getApplication().getSquirrelPreferences().getUseShortSessionTitle())
      {
         title = getAlias().getName();
      }
      else if (user.length() > 0)
      {
         String[] args = new String[3];
         args[0] = getAlias().getName();
         args[1] = catalog;
         args[2] = user;
         title = s_stringMgr.getString("Session.title1", args);
      }
      else
      {
         String[] args = new String[2];
         args[0] = getAlias().getName();
         args[1] = catalog;
         title = s_stringMgr.getString("Session.title0", args);
      }

      _modificationAwareSessionTitle.setTitle(_id + " - " + title);
   }


   /**
    * The code in any SQLEditor is parsed in background. You may attach a listener to the ParserEventsProcessor
    * to get to know about the results of parsing. The events are passed synchron with the event queue
    * (via SwingUtils.invokeLater()). At the moment events are produced for errors in the SQLScript
    * which are highlighted in the syntax plugin and for aliases of table names which are used in the
    * code completion plugin.
    * <p>
    * If you want the ParserEventsProcessor to produce further events feel free to contact gerdwagner@users.sourceforge.net.
    */
   public IParserEventsProcessor getParserEventsProcessor(IIdentifier entryPanelIdentifier)
   {
      IParserEventsProcessor pep = _parserEventsProcessorsByEntryPanelIdentifier.get(entryPanelIdentifier);

      if (null == pep)
      {
         ISQLPanelAPI panelAPI = SessionUtils.getSqlPanelApi(entryPanelIdentifier, getIdentifier());

         if (null != panelAPI)
         {
            pep = new ParserEventsProcessor(panelAPI, this);
         }
         else
         {
            // If there is no sqlPanelAPI (e.g. the Object tree find editor) we assume no parser is necessary and thus provide a dummy impl.
            pep = new ParserEventsProcessorDummy();
         }

         _parserEventsProcessorsByEntryPanelIdentifier.put(entryPanelIdentifier, pep);
      }
      return pep;
   }

   public void setActiveSessionWindow(ISessionWidget activeActiveSessionWindow)
   {
      _activeActiveSessionWindow = activeActiveSessionWindow;
   }

   public ISessionWidget getActiveSessionWindow()
   {
      return _activeActiveSessionWindow;
   }

   /**
    * @throws IllegalStateException if ActiveSessionWindow doesn't provide an SQLPanelAPI
    *                               for example if it is an ObjectTreeInternalFrame
    */
   public ISQLPanelAPI getSQLPanelAPIOfActiveSessionWindow()
   {
      return getSQLPanelAPIOfActiveSessionWindow(false);
   }

   public ISQLPanelAPI getSQLPanelAPIOfActiveSessionWindow(boolean allowReturnNull)
   {
      ISQLPanelAPI sqlPanelAPI = null;
      if (isSessionWidgetActive())
      {
         sqlPanelAPI = ((SessionInternalFrame) _activeActiveSessionWindow).getSelectedOrMainSQLPanelAPI();
      }
      else if (_activeActiveSessionWindow instanceof SQLInternalFrame)
      {
         sqlPanelAPI = ((SQLInternalFrame) _activeActiveSessionWindow).getMainSQLPanelAPI();
      }
      else if(false == allowReturnNull)
      {
         throw new IllegalStateException("SQLPanelApi can only be provided for SessionInternalFrame or SQLInternalFrame");
      }

      return sqlPanelAPI;
   }

   public boolean isSessionWidgetActive()
   {
      return _activeActiveSessionWindow instanceof SessionInternalFrame;
   }

   /**
    * @throws IllegalStateException if ActiveSessionWindow doesn't provide an IObjectTreeAPI
    *                               for example if it is an SQLInternalFrame
    */
   public IObjectTreeAPI getObjectTreeAPIOfActiveSessionWindow()
   {
      IObjectTreeAPI objectTreeAPI;
      if (isSessionWidgetActive())
      {
         objectTreeAPI = ((SessionInternalFrame) _activeActiveSessionWindow).getObjectTreeAPI();
      }
      else if (_activeActiveSessionWindow instanceof ObjectTreeInternalFrame)
      {
         objectTreeAPI = ((ObjectTreeInternalFrame) _activeActiveSessionWindow).getObjectTreeAPI();
      }
      else
      {
         throw new IllegalStateException("ObjectTreeApi can only be provided for SessionInternalFrame or ObjectTreeInternalFrame");
      }

      return objectTreeAPI;
   }

   /**
    * The point of this method is to try to determine if the driver being used
    * for this session supports the API methods we are likely to use with this
    * version of the Java runtime environment.  It's not a showstopper to use
    * an older driver, but we noticed that in some cases, older versions of
    * drivers connecting to newer databases causes various unpredictable error
    * conditions that are hard to troubleshoot, given that we don't have the
    * source to the driver.  Be that as it may, the user will inevitably claim
    * that their xyz java app works fine with their antiquated driver,
    * whereas SQuirreL does not - therefore it's a SQuirreL bug. So this will
    * warn the user when this condition exists and hopefully persuade them to
    * correct the problem.
    */
   private void checkDriverVersion()
   {
      if (!_app.getSquirrelPreferences().getWarnJreJdbcMismatch())
      {
         return;
      }

      // At this point we know that we have a 1.4 or higher java runtime
      boolean driverIs21Compliant = true;

      // Since 1.4 implements interfaces that became available in JDBC 3.0,
      // let's warn the user if the driver doesn't support DatabaseMetaData
      // methods that were added in JDBC 2.1 and JDBC 3.0 specifications.

      SQLDatabaseMetaData md = _sessionConnectionPool.getMasterSQLConnection().getSQLMetaData();
      try
      {
         md.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY);
      }
      catch (Throwable e)
      {
         driverIs21Compliant = false;
      }

      if (!driverIs21Compliant)
      {
         // i18n[Session.driverCompliance=The driver being used for alias ''{0}'' is not JDBC 2.1 compliant.\nYou should consider getting a more recent version of this driver]
         String msg = s_stringMgr.getString("Session.driverCompliance", _alias.getName());
         // i18n[Session.driverComplianceTitle=JRE/JDBC Version Mismatch]
         String title = s_stringMgr.getString("Session.driverComplianceTitle");
         showMessageDialog(msg, title, JOptionPane.WARNING_MESSAGE);
         s_log.info(msg);
         return;
      }
      boolean driverIs30Compliant = true;
      try
      {
         md.supportsSavepoints();
      }
      catch (Throwable e)
      {
         driverIs30Compliant = false;
      }

      if (!driverIs30Compliant)
      {
         // i18n[Session.driverCompliance3.0=The driver being used for alias ''{0}'' is not JDBC 3.0 compliant.\nYou should consider getting a more recent version of this driver]
         String msg =
               s_stringMgr.getString("Session.driverCompliance3.0", _alias.getName());
         // i18n[Session.driverComplianceTitle=JRE/JDBC Version Mismatch]
         String title =
               s_stringMgr.getString("Session.driverComplianceTitle");
         showMessageDialog(msg, title, JOptionPane.WARNING_MESSAGE);
         if (s_log.isInfoEnabled())
         {
            s_log.info(msg);
         }
      }
   }

   private void showMessageDialog(final String message,
                                  final String title,
                                  final int messageType)
   {
      final JFrame f = _app.getMainFrame();
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            JOptionPane.showMessageDialog(f,
                  message,
                  title,
                  messageType);
         }
      });
   }

   private class SQLConnectionListener implements PropertyChangeListener
   {
      public void propertyChange(PropertyChangeEvent evt)
      {
         final String propName = evt.getPropertyName();
         if (propName == null || ISQLConnection.IPropertyNames.CATALOG.equalsIgnoreCase(propName) )
         {
            GUIUtils.processOnSwingEventThread(() -> setupTitle());
         }
      }
   }


   protected void finalize()
   {
//		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//		System.out.println("+ Finalize " + getClass() + ". Hash code:" + hashCode());
//		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
      _app.getSessionManager().fireSessionFinalized(_id);

   }

   /**
    * @param _finishedLoading The _finishedLoading to set.
    */
   public void setPluginsfinishedLoading(boolean pluginsFinishedLoading)
   {
      this._pluginsFinishedLoading = pluginsFinishedLoading;
   }

   /**
    * @return Returns the _finishedLoading.
    */
   public boolean isfinishedLoading()
   {
      return _finishedLoading && _pluginsFinishedLoading;
   }

   public boolean confirmClose()
   {
      ISessionWidget[] frames = getApplication().getWindowManager().getAllFramesOfSession(getIdentifier());

      for (ISessionWidget frame : frames)
      {
         if (frame instanceof SQLInternalFrame)
         {
            if (false == ((SQLInternalFrame) frame).getMainSQLPanelAPI().confirmClose())
            {
               frame.moveToFront();
               return false;
            }
         }
      }

      for (SQLPanel sqlPanel : getSessionInternalFrame().getSessionPanel().getAllSQLPanels())
      {
         if (false == sqlPanel.getSQLPanelAPI().confirmClose())
         {
            return false;
         }
      }

      return true;
   }

   /**
    * Returns the IQueryTokenizer implementation to use for tokenizing scripts
    * statements that should be sent to the server.  If the tokenizer hasn't
    * been initialized yet, then a default one will be created.  If a cutom
    * tokenizer has been installed, this will just return that one, in lieu of
    * the default one.
    *
    * @return an implementation of IQueryTokenizer
    */
   public IQueryTokenizer getQueryTokenizer()
   {
      if (tokenizer == null || !customTokenizerInstalled)
      {
         // No tokenizer has been set by any installed plugin.  Go ahead and
         // give the default tokenizer.  It is important to not cache this
         // object so that session property changes to the current session
         // are reflected in this default tokenizer.
         tokenizer = new QueryTokenizer(_props.getSQLStatementSeparator(),
                                        _props.getStartOfLineComment(),
                                        _props.getRemoveMultiLineComment(),
                                        _props.getRemoveLineComment());
      }
      return tokenizer;
   }

   /**
    * Sets the IQueryTokenizer implementation to use for this session.
    *
    * @param tokenizer
    * @throws IllegalArgumentException for null argument
    * @throws IllegalStateException    if a custom tokenizer is already installed.
    */
   public void setQueryTokenizer(IQueryTokenizer aTokenizer)
   {
      if (aTokenizer == null)
      {
         throw new IllegalArgumentException("aTokenizer arg cannot be null");
      }
      if (customTokenizerInstalled)
      {
         String currentTokenizer = tokenizer.getClass().getName();
         String newTokenizer = tokenizer.getClass().getName();
         throw new IllegalStateException(
               "Only one custom query tokenizer can be installed.  " +
                     "Current tokenizer is " + currentTokenizer + ". New tokenizer is " +
                     newTokenizer);
      }
      customTokenizerInstalled = true;
      tokenizer = aTokenizer;

      TokenizerSessPropsInteractions tep = tokenizer.getTokenizerSessPropsInteractions();

      if (tep.isTokenizerDefinesStatementSeparator())
      {
         _props.setSQLStatementSeparator(aTokenizer.getSQLStatementSeparator());
      }

      if (tep.isTokenizerDefinesStartOfLineComment())
      {
         _props.setStartOfLineComment(aTokenizer.getLineCommentBegin());
      }

      if (tep.isTokenizerDefinesRemoveMultiLineComment())
      {
         _props.setRemoveMultiLineComment(aTokenizer.isRemoveMultiLineComment());
      }

      if (tep.isTokenizerDefinesRemoveLineComment())
      {
         _props.setRemoveMultiLineComment(aTokenizer.isRemoveLineComment());
      }
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.session.ISession#getMetaData()
    */
   public ISQLDatabaseMetaData getMetaData()
   {
      if (_sessionConnectionPool != null)
      {
         return _sessionConnectionPool.getMasterSQLConnection().getSQLMetaData();
      }
      else
      {
         return null;
      }
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.session.ISession#setExceptionFormatter(net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter)
    */
   public void setExceptionFormatter(ExceptionFormatter formatter)
   {
      this.formatter.setCustomExceptionFormatter(formatter);
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.session.ISession#getExceptionFormatter()
    */
   public ExceptionFormatter getExceptionFormatter()
   {
      return this.formatter;
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.session.ISession#formatException(java.lang.Throwable)
    */
   public String formatException(Throwable th)
   {
      return this.formatter.format(th);
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.session.ISession#showErrorMessage(java.lang.String)
    */
   public void showErrorMessage(String msg)
   {
      _msgHandler.showErrorMessage(msg);
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.session.ISession#showErrorMessage(java.lang.Throwable, net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter)
    */
   public void showErrorMessage(Throwable th)
   {
      _msgHandler.showErrorMessage(th, formatter);
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.session.ISession#showMessage(java.lang.String)
    */
   public void showMessage(String msg)
   {
      _msgHandler.showMessage(msg);
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.session.ISession#showMessage(java.lang.Throwable, net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter)
    */
   public void showMessage(Throwable th)
   {
      _msgHandler.showMessage(th, formatter);
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.session.ISession#showWarningMessage(java.lang.String)
    */
   public void showWarningMessage(String msg)
   {
      _msgHandler.showWarningMessage(msg);
   }

   @Override
   public void showWarningMessage(Throwable th)
   {
      _msgHandler.showWarningMessage(th, formatter);
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.session.ISession#createUnmanagedConnection()
    */
   public SQLConnection createUnmanagedConnection()
   {
      try
      {
         SQLConnectionState connState = new SQLConnectionState();
         OpenConnectionCommand cmd = new OpenConnectionCommand(_alias, _user, _password, connState.getConnectionProperties());

         cmd.executeAndWait();
         return cmd.getSQLConnection();
      }
      catch (Exception e)
      {
         s_log.error(e);
         showErrorMessage(e);
         return null;
      }
   }

   @Override
   public void addSimpleSessionListener(SimpleSessionListener simpleSessionListener)
   {
      _simpleSessionListenerManager.addListener(simpleSessionListener);
   }

   @Override
   public void removeSimpleSessionListener(SimpleSessionListener simpleSessionListener)
   {
      _simpleSessionListenerManager.removeListener(simpleSessionListener);
   }

   @Override
   public void putSessionLocal(Object key, Object value)
   {
      _sessionLocales.put(key, value);
   }

   @Override
   public Object getSessionLocal(Object key)
   {
      return _sessionLocales.get(key);
   }

   @Override
   public SessionConnectionPool getConnectionPool()
   {
      checkConnectionPool();
      return _sessionConnectionPool;
   }

   private void checkConnectionPool()
   {
      if(null == _sessionConnectionPool)
      {
         if(0 == _props.getQueryConnectionPoolSize())
         {
            throw new IllegalStateException("No Connection instance. This may happen when reconnect (Ctrl+T) failed.");
         }
         else
         {
            throw new IllegalStateException("No ConnectionPool instance. This may happen when reconnect (Ctrl+T) failed.");
         }
      }
   }

   @Override
   public SavedSessionJsonBean getSavedSession()
   {
      return _savedSession;
   }

   @Override
   public void setSavedSession(SavedSessionJsonBean savedSession)
   {
      _savedSession = savedSession;
   }

   @Override
   public CurrentSchemaModel getCurrentSchemaModel()
   {
      return _currentSchemaModel;
   }
}
