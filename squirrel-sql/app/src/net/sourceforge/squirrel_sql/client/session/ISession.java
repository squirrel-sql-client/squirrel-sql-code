package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.event.IResultTabListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.DatabaseNode;
import net.sourceforge.squirrel_sql.client.session.objectstree.ProcedureNode;
import net.sourceforge.squirrel_sql.client.session.objectstree.TableNode;
import net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel.IDatabasePanelTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.procedurepanel.IProcedurePanelTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.ITablePanelTab;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

/**
 * The current session.
 */
public interface ISession extends IHasIdentifier {
	/**
	 * Keys to objects stored in session.
	 */
//CB?? Get rid of this crap in the object tree rewrite.
	public interface ISessionKeys {
		String DATABASE_DETAIL_PANEL_KEY = DatabaseNode.class.getName() + "_DETAIL_PANEL_KEY";
		String PROCEDURE_DETAIL_PANEL_KEY = ProcedureNode.class.getName() + "_DETAIL_PANEL_KEY";
		String TABLE_DETAIL_PANEL_KEY = TableNode.class.getName() + "_DETAIL_PANEL_KEY";
	}

	public interface IMainPanelTabIndexes extends MainPanel.ITabIndexes {
	}

	/**
	 * Close this session.
	 */
	void close();

	/**
	 * Close the current connection to the database.
	 *
	 * @throws	SQLException  if an SQL error occurs.
	 */
	void closeSQLConnection() throws SQLException;

	/**
	 * Return the Application API object.
	 *
	 * @return the Application API object.
	 */
	IApplication getApplication();

	/**
	 * Return the current SQL connection object.
	 *
	 * @return the current SQL connection object.
	 */
	SQLConnection getSQLConnection();

	/**
	 * Return the driver used to connect to the database.
	 *
	 * @return the driver used to connect to the database.
	 */
	ISQLDriver getDriver();

	/**
	 * Return the alias used to connect to the database.
	 *
	 * @return the alias used to connect to the database.
	 */
	ISQLAlias getAlias();

	/**
	 * Return the properties for this session.
	 *
	 * @return the properties for this session.
	 */
	SessionProperties getProperties();

	Object getPluginObject(IPlugin plugin, String key);
	Object putPluginObject(IPlugin plugin, String key, Object obj);
	void removePluginObject(IPlugin plugin, String key);

	void setMessageHandler(IMessageHandler handler);
	IMessageHandler getMessageHandler();

	void closeAllSQLResultTabs();
	void closeAllSQLResultFrames();

	String getEntireSQLScript();
	String getSQLScriptToBeExecuted();
	void setEntireSQLScript(String sqlScript);
	void appendSQLScript(String sqlScript);
	int getSQLScriptSelectionStart();
	int getSQLScriptSelectionEnd();
	void setSQLScriptSelectionStart(int start);
	void setSQLScriptSelectionEnd(int start);
	void executeSQL(String sql);

	/**
	 * Return an array of <TT>IDatabaseObjectInfo</TT> objects representing all
	 * the objects selected in the objects tree.
	 *
	 * @return	array of <TT>IDatabaseObjectInfo</TT> objects.
	 */
	IDatabaseObjectInfo[] getSelectedDatabaseObjects();

	void setSessionSheet(SessionSheet child);
	SessionSheet getSessionSheet();

	/**
	 * Add a listener listening for SQL Execution.
	 *
	 * @param	lis	 Listener
	 *
	 * @throws	IllegalArgumentException
	 *			If a null <TT>ISQLExecutionListener</TT> passed.
	 */
	public void addSQLExecutionListener(ISQLExecutionListener lis);

	/**
	 * Remove an SQL execution listener.
	 *
	 * @param	lis	 Listener
	 *
	 * @throws	IllegalArgumentException
	 *			If a null <TT>ISQLExecutionListener</TT> passed.
	 */
	public void removeSQLExecutionListener(ISQLExecutionListener lis);

	/**
	 * Select a tab in the main tabbed pane.
	 *
	 * @param	tabIndex   The tab to select. @see #IMainTabIndexes
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if an invalid <TT>tabIndex</TT> passed.
	 */
	void selectMainTab(int tabIndex) throws IllegalArgumentException;

	/**
	 * Execute the current SQL.
	 */
	void executeCurrentSQL();

	/**
	 * Commit the current SQL transaction.
	 */
	void commit();

	/**
	 * Rollback the current SQL transaction.
	 */
	void rollback();

	/**
	 * Add a listener for events in this sessions result tabs.
	 *
	 * @param	lis		The listener.
	 */
	void addResultTabListener(IResultTabListener lis);

	/**
	 * Remove a listener for events in this sessions result tabs.
	 *
	 * @param	lis		The listener.
	 */
	void removeResultTabListener(IResultTabListener lis);

	/**
	 * Add a tab to the main tabbed panel.
	 *
	 * @param	tab	 The tab to be added.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>IMainPanelTab</TT> passed.
	 */
	void addMainTab(IMainPanelTab tab);

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
	void addDatabasePanelTab(IDatabasePanelTab tab);

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
	void addTablePanelTab(ITablePanelTab tab);

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
	void addProcedurePanelTab(IProcedurePanelTab tab);
}
