package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnectionState;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.NullMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.mainframe.action.OpenConnectionCommand;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;

class Session implements IClientSession
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(Session.class);

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

		_props = (SessionProperties)_app.getSquirrelPreferences().getSessionProperties().clone();

		// Create the API objects that give access to various
		// areas of the session.
		_objectTreeAPI = new ObjectTreeAPI(this);
		_sqlPanelAPI = new SQLPanelAPI(this);
	}

	/**
	 * Close this session.
	 */
	public void close()
	{
		if (_sessionSheet != null)
		{
			_sessionSheet.dispose();
			_sessionSheet = null;
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
		return _sessionSheet.getSQLEntryPanel();
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
		final OpenConnectionCommand cmd =
			new OpenConnectionCommand(_app, _alias, _user, _password);
		try
		{
			closeSQLConnection();
		}
		catch (SQLException ex)
		{
			final String msg = "Error occured closing connection";
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

	public void setSessionSheet(SessionSheet child)
	{
		_sessionSheet = child;

	}

	public SessionSheet getSessionSheet()
	{
		return _sessionSheet;
	}

	/**
	 * Select a tab in the main tabbed pane.
	 *
	 * @param	tabIndex   The tab to select. @see ISession.IMainTabIndexes
	 *
	 * @throws	IllegalArgumentException
	 *		  Thrown if an invalid <TT>tabId</TT> passed.
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
}
