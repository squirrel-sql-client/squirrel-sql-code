package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications copyright (C) 2001 Johan Compagner
 * jcompagner@j-com.nl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.awt.Component;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.NullMessageHandler;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.objectstree.DatabasePanel;
import net.sourceforge.squirrel_sql.client.session.objectstree.ProcedurePanel;
import net.sourceforge.squirrel_sql.client.session.objectstree.TablePanel;
import net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel.IDatabasePanelTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.procedurepanel.IProcedurePanelTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.ITablePanelTab;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;

class Session implements ISession {
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

	/** Properties for this session. */
	private SessionProperties _props = new SessionProperties();

	/**
	 * Objects stored in session. Each entry is a <TT>Map</TT>
	 * keyed by <TT>IPlugin.getInternalName()</TT>. Each <TT>Map</TT>
	 * contains the objects saved for the plugin.
	 */
	private Map _pluginObjects = new HashMap();

	private IMessageHandler _msgHandler = NullMessageHandler.getInstance();

	/**
	 * Create a new session.
	 *
	 * @param   app	 Application API.
	 * @param   driver  JDBC driver for session.
	 * @param   alias   Defines URL to database.
	 * @param   conn	Connection to database.
	 *
	 * @throws IllegalArgumentException if any parameter is null.
	 */
	public Session(
		IApplication app,
		ISQLDriver driver,
		ISQLAlias alias,
		SQLConnection conn) {
		super();
		if (app == null) {
			throw new IllegalArgumentException("null IApplication passed");
		}
		if (driver == null) {
			throw new IllegalArgumentException("null ISQLDriver passed");
		}
		if (alias == null) {
			throw new IllegalArgumentException("null ISQLAlias passed");
		}
		if (conn == null) {
			throw new IllegalArgumentException("null SQLConnection passed");
		}

		_app = app;
		_driver = driver;
		_alias = alias;
		_conn = conn;

		_props.assignFrom(_app.getSquirrelPreferences().getSessionProperties());

		final IPlugin plugin = getApplication().getDummyAppPlugin();
		
		//?? This crap should be done better.
		putPluginObject(plugin, ISessionKeys.DATABASE_DETAIL_PANEL_KEY, new DatabasePanel(this));
		putPluginObject(plugin, ISessionKeys.PROCEDURE_DETAIL_PANEL_KEY, new ProcedurePanel(this));
		putPluginObject(plugin, ISessionKeys.TABLE_DETAIL_PANEL_KEY, new TablePanel(this));
	}

	public IIdentifier getIdentifier() {
		return _id;
	}

	public IApplication getApplication() {
		return _app;
	}

	/**
	 * @return <TT>SQLConnection</TT> for this session.
	 */
	public SQLConnection getSQLConnection() {
		return _conn;
	}

	/**
	 * @return <TT>ISQLDriver</TT> for this session.
	 */
	public ISQLDriver getDriver() {
		return _driver;
	}

	/**
	 * @return <TT>ISQLAlias</TT> for this session.
	 */
	public ISQLAlias getAlias() {
		return _alias;
	}

	public SessionProperties getProperties() {
		return _props;
	}

	/**
	 * Return an array of <TT>IDatabaseObjectInfo</TT> objects representing all
	 * the objects selected in the objects tree.
	 *
	 * @return	array of <TT>IDatabaseObjectInfo</TT> objects.
	 */
	public IDatabaseObjectInfo[] getSelectedDatabaseObjects() {
		return _sessionSheet.getObjectPanel().getSelectedDatabaseObjects();
	}

	public synchronized Object getPluginObject(IPlugin plugin, String key) {
		if (plugin == null) {
			throw new IllegalArgumentException("Null IPlugin passed");
		}
		if (key == null) {
			throw new IllegalArgumentException("Null key passed");
		}
		Map map = (Map) _pluginObjects.get(plugin.getInternalName());
		if (map == null) {
			map = new HashMap();
			_pluginObjects.put(plugin.getInternalName(), map);
		}
		return map.get(key);
	}

	public synchronized Object putPluginObject(
		IPlugin plugin,
		String key,
		Object value) {
		if (plugin == null) {
			throw new IllegalArgumentException("Null IPlugin passed");
		}
		if (key == null) {
			throw new IllegalArgumentException("Null key passed");
		}
		Map map = (Map) _pluginObjects.get(plugin.getInternalName());
		if (map == null) {
			map = new HashMap();
			_pluginObjects.put(plugin.getInternalName(), map);
		}
		return map.put(key, value);
	}

	public String getSQLScript() {
		return _sessionSheet.getSQLScript();
	}

	public void setSQLScript(String sqlScript) {
		_sessionSheet.setSQLScript(sqlScript);
	}

	public void closeSQLConnection() throws SQLException {
		if (_conn != null) {
			try {
				_conn.close();
			} finally {
				_conn = null;
			}
		}
	}

	public IMessageHandler getMessageHandler() {
		return _msgHandler;
	}

	public void setMessageHandler(IMessageHandler handler) {
		_msgHandler = handler != null ? handler : NullMessageHandler.getInstance();
	}

	public void showMessage(Exception ex) {
		_msgHandler.showMessage(ex);
	}

	public void showMessage(String msg) {
		_msgHandler.showMessage(msg);
	}

	public void setSessionSheet(SessionSheet child) {
		_sessionSheet = child;

	}

	public SessionSheet getSessionSheet() {
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
	public void selectMainTab(int tabIndex) throws IllegalArgumentException {
		_sessionSheet.selectMainTab(tabIndex);
	}

	/**
	 * Execute the current SQL.
	 */
	public void executeCurrentSQL() {
		_sessionSheet.executeCurrentSQL();
	}

	/**
	 * Commit the current SQL transaction.
	 */
	public void commit() {
		try {
			getSQLConnection().commit();
			getMessageHandler().showMessage("Commit completed normally."); // i18n
		} catch (Exception ex) {
			getMessageHandler().showMessage(ex);
		}
	}

	/**
	 * Rollback the current SQL transaction.
	 */
	public void rollback() {
		try {
			getSQLConnection().rollback();
			getMessageHandler().showMessage("Rollback completed normally."); // i18n
		} catch (Exception ex) {
			getMessageHandler().showMessage(ex);
		}
	}
	/**
	 * Add a listener listening for SQL Execution.
	 *
	 * @param   lis	 Listener
	 *
	 * @throws  IllegalArgumentException
	 *			  If a null <TT>ISQLExecutionListener</TT> passed.
	 */
	public void addSQLExecutionListener(ISQLExecutionListener lis)
		throws IllegalArgumentException {
		if (lis == null) {
			throw new IllegalArgumentException("null ISQLExecutionListener passed");
		}
		_sessionSheet.getSQLPanel().addSQLExecutionListener(lis);
	}

	/**
	 * Remove an SQL execution listener.
	 *
	 * @param   lis	 Listener
	 *
	 * @throws  IllegalArgumentException
	 *			  If a null <TT>ISQLExecutionListener</TT> passed.
	 */
	public void removeSQLExecutionListener(ISQLExecutionListener lis)
		throws IllegalArgumentException {
		if (lis == null) {
			throw new IllegalArgumentException("null ISQLExecutionListener passed");
		}
		_sessionSheet.getSQLPanel().removeSQLExecutionListener(lis);
	}

	/**
	 * Add a tab to the main tabbed panel.
	 *
	 * title	The title to display in the tab.
	 * icon	 The icon to display in the tab. If <TT>null</TT> then no icon displayed.
	 * comp	 The component to be shown when the tab is active.
	 * tip	  The tooltip to be displayed for the tab. Can be <TT>null</TT>.
	 *
	 * @throws	IllegalArgumentException
	 *		  If <TT>title</TT> or <TT>comp</TT> is <TT>null</TT>.
	 */
	public void addMainTab(String title, Icon icon, Component comp, String tip)
			throws IllegalArgumentException {
		_sessionSheet.addMainTab(title, icon, comp, tip);
	}

	/**
	 * Add a tab to the panel shown when the database selected in the
	 * object tree. If a tab with this title already exists it is
	 * removed from the tabbed pane and the passed tab inserted in its
	 * place. New tabs are inserted at the end.
	 *
	 * @param	tab	 The tab to be added.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ITablePanelTab</TT> passed.
	 */
	public void addDatabasePanelTab(IDatabasePanelTab tab) throws IllegalArgumentException {
		_sessionSheet.getDatabasePanel().addDatabasePanelTab(tab);
	}

	/**
	 * Add a tab to the panel shown when a table selected in the
	 * object tree. If a tab with this title already exists it is
	 * removed from the tabbed pane and the passed tab inserted in its
	 * place. New tabs are inserted at the end.
	 *
	 * @param	tab	 The tab to be added.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ITablePanelTab</TT> passed.
	 */
	public void addTablePanelTab(ITablePanelTab tab) throws IllegalArgumentException {
		if (tab == null) {
			throw new IllegalArgumentException("Null ITablePanelTab passed");
		}
		_sessionSheet.getTablePanel().addTablePanelTab(tab);
	}

	/**
	 * Add a tab to the panel shown when a procedure selected in the
	 * object tree. If a tab with this title already exists it is
	 * removed from the tabbed pane and the passed tab inserted in its
	 * place. New tabs are inserted at the end.
	 *
	 * @param	tab	 The tab to be added.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>IProcedurePanelTab</TT> passed.
	 */
	public void addProcedurePanelTab(IProcedurePanelTab tab) throws IllegalArgumentException {
		if (tab == null) {
			throw new IllegalArgumentException("Null IProcedurePanelTab passed");
		}
		_sessionSheet.getProcedurePanel().addProcedurePanelTab(tab);
	}
}

