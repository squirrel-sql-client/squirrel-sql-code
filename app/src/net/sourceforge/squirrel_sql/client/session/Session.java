package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications copyright (C) 2001 Johan Compagner
 * jcompagner@j-com.nl
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.event.EventListenerList;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnectionState;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.NullMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.mainframe.action.OpenConnectionCommand;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.event.ISessionListener;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.sqlfilter.SQLFilterClauses;
import net.sourceforge.squirrel_sql.client.session.parser.ParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
/**
 * Think of a session as being the users view of the database. IE it includes
 * the database connetion and the UI.
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

	/** Descriptive title for session. */
	private String _title = "";

	private SessionSheet _sessionSheet;

	/** The <TT>IIdentifier</TT> that uniquely identifies this object. */
	private IIdentifier _id = IdentifierFactory.getInstance().createIdentifier();

	/** Application API. */
	private IApplication _app;

	/** Connection to database. */
	private SQLConnection _conn;

	/** Driver used to connect to database. */
	private ISQLDriver _driver;

	/** Alias describing how to connect to database. */
	private ISQLAlias _alias;

	private final String _user;
	private final String _password;

	/** Properties for this session. */
	private SessionProperties _props;

	private SQLFilterClauses _sqlFilterClauses;

	/**
	 * Objects stored in session. Each entry is a <TT>Map</TT>
	 * keyed by <TT>IPlugin.getInternalName()</TT>. Each <TT>Map</TT>
	 * contains the objects saved for the plugin.
	 */
	private final Map _pluginObjects = new HashMap();

	private IMessageHandler _msgHandler = NullMessageHandler.getInstance();

	/** API for the object tree. */
	private final IObjectTreeAPI _objectTreeAPI;

	/** API object for the SQL panel. */
	private final ISQLPanelAPI _sqlPanelAPI;

	/** Xref info about the current connection. */
	private final SchemaInfo _defaultSchemaInfo = new SchemaInfo();

	private final Hashtable _schemaInfosByCatalogAndSchema = new Hashtable();

	/** Set to <TT>true</TT> once session closed. */
	private boolean _closed;

	/**
	 * Collection of listeners to this object tree.
	 */
	private EventListenerList _listenerList = new EventListenerList();

	private List _statusBarToBeAdded = new ArrayList();
	private ParserEventsProcessor _parserEventsProcessor;

	/**
	 * Create a new session.
	 *
	 * @param	app			Application API.
	 * @param	driver		JDBC driver for session.
	 * @param	alias		Defines URL to database.
	 * @param	conn		Connection to database.
	 * @param	user		User name connected with.
	 * @param	password	Password for <TT>user</TT>
	 *
	 * @throws IllegalArgumentException if any parameter is null.
	 */
	public Session(IApplication app, ISQLDriver driver, ISQLAlias alias,
					SQLConnection conn, String user, String password)
	{
		super();
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

		_app = app;
		_driver = driver;
		_alias = alias;
		_conn = conn;
		_user = user;
		_password = password;

		_title = createTitle();

		_props = (SessionProperties)_app.getSquirrelPreferences().getSessionProperties().clone();
		_sqlFilterClauses = new SQLFilterClauses();

		// Create the API objects that give access to various
		// areas of the session.
		_objectTreeAPI = new ObjectTreeAPI(this);
		_sqlPanelAPI = new SQLPanelAPI(this);

		_parserEventsProcessor = new ParserEventsProcessor(this);

		// Start loading table/column info about the current database.
		_app.getThreadPool().addTask(new Runnable()
		{
			public void run()
			{
				loadTableInfo();
			}
		});
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
			s_log.debug("Closing session: " + _id);

			try
			{
				_parserEventsProcessor.endProcessing();
			}
			catch(Exception e)
			{
				s_log.info("Error stopping parser event processor", e);
			}

			try
			{
				closeSQLConnection();
			}
			finally
			{
				// This is set here as SessionSheet.dispose() will attempt
				// to close the session.
				_closed = true;

				fireSessionClosedEvent();

				// Remove all listeners.
				_listenerList = null;

				if (_sessionSheet != null)
				{
					_sessionSheet.sessionHasClosed();
					_sessionSheet = null;
				}
			}
			s_log.debug("Successfully closed session: " + _id);
		}
	}

	/**
	 * Return the unique identifier for this session.
	 *
	 * @return	the unique identifier for this session.
	 */
	public IIdentifier getIdentifier()
	{
		return _id;
	}

	/**
	 * Retrieve whether this session has been closed.
	 *
	 * @return	<TT>true</TT> if session closed else <TT>false</TT>.
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
	public SQLConnection getSQLConnection()
	{
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
	public ISQLAlias getAlias()
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
		return _defaultSchemaInfo;
	}

	public SchemaInfo getSchemaInfo(String catalogName, String schemaName)
	{
		String key = catalogName + "," + schemaName;

		SchemaInfo ret = (SchemaInfo) _schemaInfosByCatalogAndSchema.get(key);
		if(null == ret)
		{
			ret = new SchemaInfo(getSQLConnection(), catalogName, schemaName);
			_schemaInfosByCatalogAndSchema.put(key, ret);
		}

		return ret;
	}

	/**
	 * Return the API for the Object Tree.
	 *
	 * @param	plugin	Plugin requesting the API.
	 *
	 * @return	the API object for the Object Tree.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if null IPlugin passed.
	 */
	public IObjectTreeAPI getObjectTreeAPI(IPlugin plugin)
	{
		if (plugin == null)
		{
			throw new IllegalArgumentException("IPlugin == null");
		}
		return _objectTreeAPI;
	}

	/**
	 * Return the API object for the SQL panel.
	 *
	 * @param	plugin	Plugin requesting the API.
	 *
	 * @return	the API object for the SQL panel.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if null IPlugin passed.
	 */
	public ISQLPanelAPI getSQLPanelAPI(IPlugin plugin)
	{
		if (plugin == null)
		{
			throw new IllegalArgumentException("IPlugin == null");
		}
		return _sqlPanelAPI;
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
		Map map = (Map) _pluginObjects.get(plugin.getInternalName());
		if (map == null)
		{
			map = new HashMap();
			_pluginObjects.put(plugin.getInternalName(), map);
		}
		return map.get(key);
	}

	/**
	 * Add a listener to this session
	 *
	 * @param	lis		The listener to add.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> listener passed.
	 */
	public void addSessionListener(ISessionListener lis)
	{
		_listenerList.add(ISessionListener.class, lis);
	}

	/**
	 * Remove a listener from this session
	 *
	 * @param	lis		The listener to remove.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> listener passed.
	 */
	public void removeSessionListener(ISessionListener lis)
	{
		_listenerList.remove(ISessionListener.class, lis);
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
		Map map = (Map) _pluginObjects.get(plugin.getInternalName());
		if (map == null)
		{
			map = new HashMap();
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
		Map map = (Map) _pluginObjects.get(plugin.getInternalName());
		if (map != null)
		{
			map.remove(key);
		}
	}

	/**
	 * Return the object that handles the SQL entry
	 * component.
	 *
	 * @return	<TT>ISQLEntryPanel</TT> object.
	 */
	public ISQLEntryPanel getSQLEntryPanel()
	{
		if (null == _sessionSheet)
		{
			return null;
		}
		return _sessionSheet.getSQLEntryPanel();
	}

	public ObjectTreePanel getObjectTreePanel()
	{
		if (null == _sessionSheet)
		{
			return null;
		}
		return _sessionSheet.getObjectTreePanel();
	}

	public synchronized void closeSQLConnection() throws SQLException
	{
		if (_conn != null)
		{
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

	/**
	 * Reconnect to the database.
	 */
	public void reconnect()
	{
		SQLConnectionState connState = null;
		if (_conn != null)
		{
			connState = new SQLConnectionState();
			try
			{
				connState.saveState(_conn, _msgHandler);
			}
			catch (SQLException ex)
			{
				s_log.error("Unexpected SQLException", ex);
			}
		}
		OpenConnectionCommand cmd = new OpenConnectionCommand(_app, _alias,
											_user, _password, connState.getConnectionProperties());
		try
		{
			closeSQLConnection();
		}
		catch (SQLException ex)
		{
			final String msg = s_stringMgr.getString("Session.error.connclose");
			s_log.error(msg, ex);
			_msgHandler.showErrorMessage(msg);
			_msgHandler.showErrorMessage(ex);
		}
		try
		{
			cmd.execute();
			_conn = cmd.getSQLConnection();
			if (connState != null)
			{
				connState.restoreState(_conn, _msgHandler);
			}
			final String msg = s_stringMgr.getString("Session.reconn", _alias.getName());
			_msgHandler.showMessage(msg);
			getObjectTreeAPI(_app.getDummyAppPlugin()).refreshTree();
		}
		catch (SQLException ex)
		{
			_msgHandler.showErrorMessage(ex);
		}
		catch (BaseException ex)
		{
			_msgHandler.showErrorMessage(ex);
		}
	}

	public IMessageHandler getMessageHandler()
	{
		return _msgHandler;
	}

	public void setMessageHandler(IMessageHandler handler)
	{
		_msgHandler = handler != null ? handler : NullMessageHandler.getInstance();
	}

	public synchronized void setSessionSheet(SessionSheet child)
	{
		_sessionSheet = child;
		if (_sessionSheet != null)
		{
			final ListIterator it = _statusBarToBeAdded.listIterator();
			while (it.hasNext())
			{
				addToStatusBar((JComponent)it.next());
				it.remove();
			}
		}
	}

	public SessionSheet getSessionSheet()
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

	/**
	 * Add a tab to the main tabbed panel.
	 *
	 * @param	tab	 The tab to be added.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>IMainPanelTab</TT> passed.
	 */
	public void addMainTab(IMainPanelTab tab)
	{
		_sessionSheet.addMainTab(tab);
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

	public SQLFilterClauses getSQLFilterClauses()
	{
		return _sqlFilterClauses;
	}

	/**
	 * Retrieve the descriptive title of this session.
	 *
	 * @return		The descriptive title of this session.
	 */
	public String getTitle()
	{
		return _title;
	}

	/**
	 * Fire a &quot;session closed&quot; event.
	 */
	protected void fireSessionClosedEvent()
	{
		Object[] listeners = _listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event.
		SessionEvent evt = null;
		for (int i = listeners.length - 2; i >= 0; i-=2 )
		{
			if (listeners[i] == ISessionListener.class)
			{
				// Lazily create the event.
				if (evt == null)
				{
					evt = new SessionEvent(this);
				}
				((ISessionListener)listeners[i + 1]).sessionClosed(evt);
			}
		}
	}

	/**
	 * Set the descriptive title for this session.
	 *
	 * @param	title	The descriptive title for this session.
	 */
	void setTitle(String value)
	{
		_title = value != null ? value : "";
	}

	/**
	 * Load table information about the current database.
	 */
	private void loadTableInfo()
	{
		_defaultSchemaInfo.load(getSQLConnection());
	}

	// TODO: i18n
	private String createTitle()
	{
		final StringBuffer title = new StringBuffer();
		title.append(getAlias().getName());
		String user = null;
		try
		{
			user = getSQLConnection().getSQLMetaData().getUserName();
		}
		catch (SQLException ex)
		{
			s_log.error("Error occured retrieving user name from Connection", ex);
		}
		if (user != null && user.length() > 0)
		{
			title.append(" as ").append(user); // i18n
		}
		return title.toString();
	}

	public IParserEventsProcessor getParserEventsProcessor()
	{
		return _parserEventsProcessor;
	}
}
