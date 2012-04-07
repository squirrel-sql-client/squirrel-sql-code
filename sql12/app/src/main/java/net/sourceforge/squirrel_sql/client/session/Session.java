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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasConnectionProperties;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionPanel;
import net.sourceforge.squirrel_sql.client.mainframe.action.OpenConnectionCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.OpenConnectionCommandListener;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.event.SimpleSessionListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.parser.ParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.parser.ParserEventsProcessorDummy;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.*;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.DefaultExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.NullMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Think of a session as being the users view of the database. IE it includes
 * the database connection and the UI.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class Session implements ISession
{
   /** Logger for this class. */
   private static final ILogger s_log =
      LoggerController.createLogger(Session.class);

   /** Internationalized strings for this class. */
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(Session.class);

   /** Factory used to generate unique IDs for sessions.
   /** Descriptive title for session. */
   private String _title = "";

   private SessionPanel _sessionSheet;

   /** The <TT>IIdentifier</TT> that uniquely identifies this object. */
   private final IIdentifier _id;

   /** Application API. */
   private IApplication _app;

   /** Connection to database. */
   private SQLConnection _conn;

   /** Driver used to connect to database. */
   private ISQLDriver _driver;

   /** Alias describing how to connect to database. */
   private SQLAlias _alias;

   private final String _user;
   private final String _password;

   /** Properties for this session. */
   private SessionProperties _props;

   /**
    * Objects stored in session. Each entry is a <TT>Map</TT>
    * keyed by <TT>IPlugin.getInternalName()</TT>. Each <TT>Map</TT>
    * contains the objects saved for the plugin.
    */
   private final Map<String, Map<String, Object>> _pluginObjects = 
       new HashMap<String, Map<String, Object>>();

   private IMessageHandler _msgHandler = NullMessageHandler.getInstance();

   /** Xref info about the current connection. */
   private final net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo _schemaInfo;

   /** Set to <TT>true</TT> once session closed. */
   private boolean _closed;

   private List<JComponent> _statusBarToBeAdded = 
       new ArrayList<JComponent>();

   private SQLConnectionListener _connLis = null;

   private ISessionWidget _activeActiveSessionWindow;
   private SessionInternalFrame _sessionInternalFrame;
   private Hashtable<IIdentifier, IParserEventsProcessor>  _parserEventsProcessorsByEntryPanelIdentifier = new Hashtable<IIdentifier, IParserEventsProcessor>();


   /** flag to track whether or not the table data has been loaded in the object tree */
   private boolean _finishedLoading = false;

   /** flag to track whether or not the plugins have finished loading for this new session */
   private boolean _pluginsFinishedLoading = false;

   /** This is set to true when a plugin sets a custom IQueryTokenizer */
   private boolean customTokenizerInstalled = false;
   
   private IQueryTokenizer tokenizer = null;
   
   /** The default exception formatter */
   private DefaultExceptionFormatter formatter = new DefaultExceptionFormatter();
   
   private SessionConnectionKeepAlive _sessionConnectionKeepAlive = null;
   private SimpleSessionListenerManager _simpleSessionListenerManager;

   /**
    * Create a new session.
    *
    * @param	app			Application API.
    * @param	driver		JDBC driver for session.
    * @param	alias		Defines URL to database.
    * @param	conn		Connection to database.
    * @param	user		User name connected with.
    * @param	password	Password for <TT>user</TT>
    * @param	sessionId	ID that uniquely identifies this session.
    *
    * @throws IllegalArgumentException if any parameter is null.
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
         throw new IllegalArgumentException("null ISQLAlias passed");
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

      try
      {
         _alias.assignFrom(alias, true);
      }
      catch (ValidationException e)
      {
         throw new RuntimeException(e);
      }


      _conn = conn;
      _user = user;
      _password = password;
      _id = sessionId;

      setupTitle();

      _props = (SessionProperties)_app.getSquirrelPreferences().getSessionProperties().clone();

      _connLis = new SQLConnectionListener();
      _conn.addPropertyChangeListener(_connLis);

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
   }

   private void startKeepAliveTaskIfNecessary() {
      SQLAliasConnectionProperties connProps = _alias.getConnectionProperties();
      
      if (connProps.isEnableConnectionKeepAlive()) {
      	String keepAliveSql = connProps.getKeepAliveSqlStatement();
      	long sleepMillis = connProps.getKeepAliveSleepTimeSeconds() * 1000;
      	_sessionConnectionKeepAlive = new SessionConnectionKeepAlive(_conn, sleepMillis, keepAliveSql, 
      		_alias.getName());
			_app.getThreadPool().addTask(_sessionConnectionKeepAlive,
				"Session Connection Keep-Alive (" + _alias.getName() + ")");
      }         	
   }
   
   private void stopKeepAliveTaskIfNecessary() {
   	if (_sessionConnectionKeepAlive != null) {
   		_sessionConnectionKeepAlive.setStopped(true);
   	}
   }
   
   /**
    * Close this session.
    *
    * @throws	SQLException
    * 			Thrown if an error closing the SQL connection. The session
    * 			will still be closed even though the connection may not have
    *			been.
    */
   public void close() throws SQLException
   {
      if (!_closed)
      {
      	if (s_log.isDebugEnabled()) {
      		s_log.debug("Closing session: " + _id);
      	}
      	stopKeepAliveTaskIfNecessary();
         if (null != _conn)
         {
            // _conn is null when session is closed after reconnect (ctrl t) failure.
            _conn.removePropertyChangeListener(_connLis);
         }
         _connLis = null;


         IParserEventsProcessor[] procs =
            _parserEventsProcessorsByEntryPanelIdentifier.values().toArray(new IParserEventsProcessor[0]);


         for (int i = 0; i < procs.length; i++)
         {
            try
            {
               if(procs[i] instanceof ParserEventsProcessor)
               {
                  ((ParserEventsProcessor)procs[i]).endProcessing();
               }
            }
            catch(Exception e)
            {
            	if (s_log.isInfoEnabled()) {
            		s_log.info("Error stopping parser event processor", e);
            	}
            }
         }

         _schemaInfo.dispose();


         try
         {
            closeSQLConnection();
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
             *  To reduced the used memory by such not reachable sessions, we remove all SQLResultTabs, when the session is closed.
             *  This helps users, they often open and close sessions without restarting SQuirrel.
             */
            if(_sessionInternalFrame != null){
            	_sessionInternalFrame.getSQLPanelAPI().closeAllSQLResultTabs();
            }
            
            
         }
         if (s_log.isDebugEnabled()) {
         	s_log.debug("Successfully closed session: " + _id);
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
    * @return	the Application API object.
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
        checkThread();
      return _conn;
   }

   /**
    * @return <TT>ISQLDriver</TT> for this session.
    */
   public ISQLDriver getDriver()
   {
      return _driver;
   }

   /**
    * @return <TT>ISQLAlias</TT> for this session.
    */
   public ISQLAliasExt getAlias()
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
    * @param	action	Action to be added.
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

   public synchronized void closeSQLConnection() throws SQLException
   {
      if (_conn != null)
      {
      	stopKeepAliveTaskIfNecessary();
         try
         {
            _conn.close();
         }
         finally
         {
            _conn = null;
         }
      }
   }

   @Override
   public JdbcConnectionData getJdbcData()
   {
      IIdentifier driverID = _alias.getDriverIdentifier();
      ISQLDriver sqlDriver = _app.getDataCache().getDriver(driverID);

      return new JdbcConnectionData(sqlDriver.getDriverClassName(), _alias.getUrl(), _user, _password);
   }


   /**
    * Reconnect to the database.
    */
   public void reconnect()
   {
      final SQLConnectionState connState = new SQLConnectionState();
      if (_conn != null)
      {
         try
         {
            connState.saveState(_conn, getProperties(), _msgHandler);
         }
         catch (SQLException ex)
         {
            s_log.error("Unexpected SQLException", ex);
         }
      }
      final OpenConnectionCommand cmd = new OpenConnectionCommand(_app, _alias,
                                 _user, _password, connState.getConnectionProperties());
      try
      {
         closeSQLConnection();
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
         cmd.execute(new OpenConnectionCommandListener()
         {
            @Override
            public void openConnectionFinished(Throwable t)
            {
               reconnectDone(connState, cmd, t);
            }
         });
      }
      catch (Throwable t)
      {
         final String msg = s_stringMgr.getString("Session.reconnError", _alias.getName());
         _msgHandler.showErrorMessage(msg +"\n" + t.toString());
         s_log.error(msg, t);
         _app.getSessionManager().fireReconnectFailed(this);
      }
   }

   private void reconnectDone(SQLConnectionState connState, OpenConnectionCommand cmd, Throwable t)
   {
      try
      {
         if(null != t)
         {
            throw t;
         }
         
         _conn = cmd.getSQLConnection();
         if (connState != null)
         {
            connState.restoreState(_conn, _msgHandler);
            getProperties().setAutoCommit(connState.getAutoCommit());
         }
         final String msg = s_stringMgr.getString("Session.reconn", _alias.getName());
         _msgHandler.showMessage(msg);
         _app.getSessionManager().fireReconnected(this);
         startKeepAliveTaskIfNecessary();
      }
      catch (Throwable th)
      {
         final String msg = s_stringMgr.getString("Session.reconnError", _alias.getName());
         _msgHandler.showErrorMessage(msg +"\n" + th.toString());
         s_log.error(msg, th);
         _app.getSessionManager().fireReconnectFailed(this);
      }
   }

   public void setMessageHandler(IMessageHandler handler)
   {
      _msgHandler = handler != null ? handler : NullMessageHandler.getInstance();
   }

   public synchronized void setSessionSheet(SessionPanel child)
   {
      _sessionSheet = child;
      if (_sessionSheet != null)
      {
         final ListIterator<JComponent> it = _statusBarToBeAdded.listIterator();
         while (it.hasNext())
         {
            addToStatusBar(it.next());
            it.remove();
         }
      }
   }

   public synchronized void setSessionInternalFrame(SessionInternalFrame sif)
   {
      _sessionInternalFrame = sif;

      // This is a reasonable default and makes initialization code run well
      _activeActiveSessionWindow = sif;

      _sessionSheet = sif.getSessionPanel();
      final ListIterator<JComponent> it = _statusBarToBeAdded.listIterator();
      while (it.hasNext())
      {
         addToStatusBar(it.next());
         it.remove();
      }
   }

   public synchronized SessionInternalFrame getSessionInternalFrame()
   {
      return _sessionInternalFrame;
   }

   public synchronized SessionPanel getSessionSheet()
   {
      return _sessionSheet;
   }

   /**
    * Select a tab in the main tabbed pane.
    *
    * @param	tabIndex	The tab to select. @see ISession.IMainTabIndexes
    *
    * @throws	IllegalArgumentException
    *			Thrown if an invalid <TT>tabId</TT> passed.
    */
   public void selectMainTab(int tabIndex)
   {
      _sessionSheet.selectMainTab(tabIndex);
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
    * @param	tab	 The tab to be added.
    *
    * @return the index of the new tab that was added.
    *
    * @throws	IllegalArgumentException
    *			Thrown if a <TT>null</TT> <TT>IMainPanelTab</TT> passed.
    */
   public int addMainTab(IMainPanelTab tab)
   {
      return _sessionSheet.addMainTab(tab);
   }

   /**
    * Add component to the session sheets status bar.
    *
    * @param	comp	Component to add.
    */
   public synchronized void addToStatusBar(JComponent comp)
   {
      if (_sessionSheet != null)
      {
         _sessionSheet.addToStatusBar(comp);
      }
      else
      {
         _statusBarToBeAdded.add(comp);
      }
   }

   /**
    * Remove component from the session sheets status bar.
    *
    * @param	comp	Component to remove.
    */
   public synchronized void removeFromStatusBar(JComponent comp)
   {
      if (_sessionSheet != null)
      {
         _sessionSheet.removeFromStatusBar(comp);
      }
      else
      {
         _statusBarToBeAdded.remove(comp);
      }
   }

//	public SQLFilterClauses getSQLFilterClauses()
//	{
//		return _sqlFilterClauses;
//	}

   /**
    * Retrieve the descriptive title of this session.
    *
    * @return		The descriptive title of this session.
    */
   public String getTitle()
   {
      return _title;
   }

   public void setTitle(String newTitle)
   {
      _title = newTitle;
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
         s_log.error("Error occured retrieving current catalog from Connection", ex);
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
      if (user.length() > 0)
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

      _title = _id + " - " + title;
   }


   /**
    * The code in any SQLEditor is parsed in the background. You may attach a listener to the ParserEventsProcessor
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

      if(null == pep)
      {
         ISQLPanelAPI panelAPI = getSqlPanelApi(entryPanelIdentifier);

         if(null != panelAPI)
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

   private ISQLPanelAPI getSqlPanelApi(IIdentifier entryPanelIdentifier)
   {
      ISessionWidget[] frames = getApplication().getWindowManager().getAllFramesOfSession(getIdentifier());

      for (int i = 0; i < frames.length; i++)
      {
         if(frames[i] instanceof SQLInternalFrame)
         {
            ISQLPanelAPI sqlPanelAPI = ((SQLInternalFrame)frames[i]).getSQLPanelAPI();
            IIdentifier id = sqlPanelAPI.getSQLEntryPanel().getIdentifier();

            if(id.equals(entryPanelIdentifier))
            {
               return sqlPanelAPI;
            }
         }

         if(frames[i] instanceof SessionInternalFrame)
         {
            ISQLPanelAPI sqlPanelAPI = ((SessionInternalFrame)frames[i]).getSQLPanelAPI();
            IIdentifier sqlEditorID = sqlPanelAPI.getSQLEntryPanel().getIdentifier();

            if(sqlEditorID.equals(entryPanelIdentifier))
            {
               return sqlPanelAPI;
            }

            IObjectTreeAPI objectTreeApi = ((SessionInternalFrame)frames[i]).getObjectTreeAPI();
            IIdentifier findEditorID = objectTreeApi.getFindController().getFindEntryPanel().getIdentifier();

            if(findEditorID.equals(entryPanelIdentifier))
            {
               return null;
            }
         }

         if(frames[i] instanceof ObjectTreeInternalFrame)
         {
            IObjectTreeAPI objectTreeApi = ((ObjectTreeInternalFrame)frames[i]).getObjectTreeAPI();
            IIdentifier findEditorID = objectTreeApi.getFindController().getFindEntryPanel().getIdentifier();

            if(findEditorID.equals(entryPanelIdentifier))
            {
               return null;
            }
         }
      }

      throw new IllegalStateException("Session has no entry panel for ID=" + entryPanelIdentifier);
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
    *
    * @throws IllegalStateException if ActiveSessionWindow doesn't provide an SQLPanelAPI
    * for example if it is an ObjectTreeInternalFrame
    */
   public ISQLPanelAPI getSQLPanelAPIOfActiveSessionWindow()
   {
      ISQLPanelAPI sqlPanelAPI;
      if(isSessionWidgetActive())
      {
         sqlPanelAPI = ((SessionInternalFrame)_activeActiveSessionWindow).getSQLPanelAPI();
      }
      else if(_activeActiveSessionWindow instanceof SQLInternalFrame)
      {
         sqlPanelAPI = ((SQLInternalFrame)_activeActiveSessionWindow).getSQLPanelAPI();
      }
      else
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
    *
    * @throws IllegalStateException if ActiveSessionWindow doesn't provide an IObjectTreeAPI
    * for example if it is an SQLInternalFrame
    */
   public IObjectTreeAPI getObjectTreeAPIOfActiveSessionWindow()
   {
      IObjectTreeAPI objectTreeAPI;
      if(isSessionWidgetActive())
      {
         objectTreeAPI = ((SessionInternalFrame)_activeActiveSessionWindow).getObjectTreeAPI();
      }
      else if(_activeActiveSessionWindow instanceof ObjectTreeInternalFrame)
      {
         objectTreeAPI = ((ObjectTreeInternalFrame)_activeActiveSessionWindow).getObjectTreeAPI();
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
    private void checkDriverVersion() {
        if (!_app.getSquirrelPreferences().getWarnJreJdbcMismatch()) {
            return;
        }
        String javaVersion = System.getProperty("java.vm.version");
        boolean javaVersionIsAtLeast14 = true;
        if (javaVersion != null) {
            if (javaVersion.startsWith("1.1")
                    || javaVersion.startsWith("1.2")
                    || javaVersion.startsWith("1.3"))
            {
                javaVersionIsAtLeast14 = false;
            }
        }
        if (!javaVersionIsAtLeast14) {
            return;
        }
        // At this point we know that we have a 1.4 or higher java runtime
        boolean driverIs21Compliant = true;

        // Since 1.4 implements interfaces that became available in JDBC 3.0, 
        // let's warn the user if the driver doesn't support DatabaseMetaData
        // methods that were added in JDBC 2.1 and JDBC 3.0 specifications. 

        SQLDatabaseMetaData md = _conn.getSQLMetaData();
        try {
            md.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY);
        } catch (Throwable e) {
            driverIs21Compliant = false;
        }

        if (!driverIs21Compliant) {
            // i18n[Session.driverCompliance=The driver being used for alias ''{0}'' is not JDBC 2.1 compliant.\nYou should consider getting a more recent version of this driver]
            String msg =
                s_stringMgr.getString("Session.driverCompliance", _alias.getName());
            // i18n[Session.driverComplianceTitle=JRE/JDBC Version Mismatch]
            String title =
                s_stringMgr.getString("Session.driverComplianceTitle");
            showMessageDialog(msg, title, JOptionPane.WARNING_MESSAGE);
            s_log.info(msg);
            return;
        }
        boolean driverIs30Compliant = true;
        try {
            md.supportsSavepoints();
        } catch (Throwable e) {
      	   if (s_log.isDebugEnabled()) {
      	   	s_log.debug(e);
      	   }
            driverIs30Compliant = false;
        }

        if (!driverIs30Compliant) {
            // i18n[Session.driverCompliance3.0=The driver being used for alias ''{0}'' is not JDBC 3.0 compliant.\nYou should consider getting a more recent version of this driver]
            String msg =
                s_stringMgr.getString("Session.driverCompliance3.0", _alias.getName());
            // i18n[Session.driverComplianceTitle=JRE/JDBC Version Mismatch]
            String title =
                s_stringMgr.getString("Session.driverComplianceTitle");
            showMessageDialog(msg, title, JOptionPane.WARNING_MESSAGE);
            if (s_log.isInfoEnabled()) {
            	s_log.info(msg);
            }
        }
    }

    private void showMessageDialog(final String message,
                                   final String title,
                                   final int messageType)
    {
        final JFrame f = _app.getMainFrame();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(f,
                        message,
                        title,
                        messageType);
            }
        });
    }

    /**
     * Check the thread of the caller to see if it is the event dispatch thread
     * and if we are debugging print a debug log message with the call trace.
     */
    private void checkThread() {
        /* This is extremely useful when trying to track down Swing UI freezing.
         * However, it currently fills the log which obscures other debug 
         * messages even though UI performance is acceptable, so it is commented 
         * out until it is needed later. 
        if (s_log.isDebugEnabled() && SwingUtilities.isEventDispatchThread()) {
            try {
                throw new Exception("GUI Thread is doing database work");
            } catch (Exception e) {
                s_log.debug(e.getMessage(), e);
            }
        }
        */
    }

   private class SQLConnectionListener implements PropertyChangeListener
   {
      public void propertyChange(PropertyChangeEvent evt)
      {
         final String propName = evt.getPropertyName();
         if (propName == null || propName == ISQLConnection.IPropertyNames.CATALOG)
         {
            setupTitle();
         }
      }
   }


   protected void finalize() throws Throwable
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
    public void setPluginsfinishedLoading(boolean pluginsFinishedLoading) {
        this._pluginsFinishedLoading = pluginsFinishedLoading;
    }

    /**
     * @return Returns the _finishedLoading.
     */
    public boolean isfinishedLoading() {
        return _finishedLoading && _pluginsFinishedLoading;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.session.ISession#confirmCloseWithUnsavedEdits()
     */
    public boolean confirmClose()
    {
       if(getActiveSessionWindow() instanceof SQLInternalFrame || getActiveSessionWindow() instanceof SessionInternalFrame)
       {
          if (getSQLPanelAPIOfActiveSessionWindow().confirmClose())
          {
             return true;
          }
          else
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
    public IQueryTokenizer getQueryTokenizer() {
        if (tokenizer == null || !customTokenizerInstalled) {
            // No tokenizer has been set by any installed plugin.  Go ahead and
            // give the default tokenizer.  It is important to not cache this 
            // object so that session property changes to the current session 
            // are reflected in this default tokenizer.
            tokenizer = new QueryTokenizer(_props.getSQLStatementSeparator(),
                                           _props.getStartOfLineComment(),
                                           _props.getRemoveMultiLineComment());
        }
        return tokenizer;
    }

    /**
     * Sets the IQueryTokenizer implementation to use for this session.
     * 
     * @param tokenizer
     * 
     * @throws IllegalArgumentException for null argument
     * @throws IllegalStateException if a custom tokenizer is already installed.
     */    
    public void setQueryTokenizer(IQueryTokenizer aTokenizer) {
        if (aTokenizer == null) {
            throw new IllegalArgumentException("aTokenizer arg cannot be null");
        }        
        if (customTokenizerInstalled) {
            String currentTokenizer = tokenizer.getClass().getName();
            String newTokenizer = tokenizer.getClass().getName();
            throw new IllegalStateException(
                "Only one custom query tokenizer can be installed.  " +
                "Current tokenizer is "+currentTokenizer+". New tokenizer is "+
                newTokenizer);
        }
        customTokenizerInstalled = true;
        tokenizer = aTokenizer;

       TokenizerSessPropsInteractions tep = tokenizer.getTokenizerSessPropsInteractions();

       if(tep.isTokenizerDefinesStatementSeparator())
       {
         _props.setSQLStatementSeparator(aTokenizer.getSQLStatementSeparator());
       }

       if(tep.isTokenizerDefinesStatementSeparator())
       {
          _props.setStartOfLineComment(aTokenizer.getLineCommentBegin());
       }

       if(tep.isTokenizerDefinesStatementSeparator())
       {
         _props.setRemoveMultiLineComment(aTokenizer.isRemoveMultiLineComment());
       }
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.session.ISession#getMetaData()
     */
    public ISQLDatabaseMetaData getMetaData() {
        if (_conn != null) {
            return _conn.getSQLMetaData();
        } else {
            return null;
        }
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.session.ISession#setExceptionFormatter(net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter)
     */
    public void setExceptionFormatter(ExceptionFormatter formatter) {
        this.formatter.setCustomExceptionFormatter(formatter);
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.session.ISession#getExceptionFormatter()
     */
    public ExceptionFormatter getExceptionFormatter() {
        return this.formatter; 
    }
    
    /**
     * @see net.sourceforge.squirrel_sql.client.session.ISession#formatException(java.lang.Throwable)
     */
    public String formatException(Throwable th) {
        return this.formatter.format(th);
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.session.ISession#showErrorMessage(java.lang.String)
     */
    public void showErrorMessage(String msg) {
        _msgHandler.showErrorMessage(msg);
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.session.ISession#showErrorMessage(java.lang.Throwable, net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter)
     */
    public void showErrorMessage(Throwable th) {
        _msgHandler.showErrorMessage(th, formatter);
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.session.ISession#showMessage(java.lang.String)
     */
    public void showMessage(String msg) {
        _msgHandler.showMessage(msg);
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.session.ISession#showMessage(java.lang.Throwable, net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter)
     */
    public void showMessage(Throwable th) {
        _msgHandler.showMessage(th, formatter);
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.session.ISession#showWarningMessage(java.lang.String)
     */
    public void showWarningMessage(String msg) {
        _msgHandler.showWarningMessage(msg);
    }
    
    /**
     * @see net.sourceforge.squirrel_sql.client.session.ISession#createUnmanagedConnection()
     */
    public SQLConnection createUnmanagedConnection()
    {
       SQLConnectionState connState = new SQLConnectionState();

       OpenConnectionCommand cmd = new OpenConnectionCommand(_app, _alias,
             _user, _password, connState.getConnectionProperties());
       try
       {
          cmd.executeAndWait();
       }
       catch (Exception e)
       {
          showErrorMessage(e);
          return null;
       }

       return cmd.getSQLConnection();
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
}
