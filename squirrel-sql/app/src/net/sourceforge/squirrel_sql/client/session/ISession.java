package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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

import javax.swing.Icon;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
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
	public interface ISessionKeys {
        String DATABASE_DETAIL_PANEL_KEY = DatabaseNode.class.getName() + "_DETAIL_PANEL_KEY";
        String PROCEDURE_DETAIL_PANEL_KEY = ProcedureNode.class.getName() + "_DETAIL_PANEL_KEY";
		String TABLE_DETAIL_PANEL_KEY = TableNode.class.getName() + "_DETAIL_PANEL_KEY";
	}

	/**
	 * IDs of tabs in the main tabbed pane.
	 */
	public interface IMainTabIndexes extends SessionSheet.IMainTabIndexes {
	}

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

	String getEntireSQLScript();
	String getSQLScriptToBeExecuted();
	void setEntireSQLScript(String sqlScript);
	void appendSQLScript(String sqlScript);

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
	public void addSQLExecutionListener(ISQLExecutionListener lis)
		throws IllegalArgumentException;

	/**
	 * Remove an SQL execution listener.
	 *
	 * @param	lis	 Listener
	 *
	 * @throws	IllegalArgumentException
	 *			If a null <TT>ISQLExecutionListener</TT> passed.
	 */
	public void removeSQLExecutionListener(ISQLExecutionListener lis)
		throws IllegalArgumentException;

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
	 * title	The title to display in the tab.
	 * icon		The icon to display in the tab. If <TT>null</TT> then no icon displayed.
	 * comp		The component to be shown when the tab is active.
	 * tip		The tooltip to be displayed for the tab. Can be <TT>null</TT>.
	 *
	 * @throws	IllegalArgumentException
	 *			If <TT>title</TT> or <TT>comp</TT> is <TT>null</TT>.
	 */
	void addMainTab(String title, Icon icon, Component comp, String tip)
			throws IllegalArgumentException;

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
	void addDatabasePanelTab(IDatabasePanelTab tab) throws IllegalArgumentException;

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
	void addTablePanelTab(ITablePanelTab tab) throws IllegalArgumentException;

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
	void addProcedurePanelTab(IProcedurePanelTab tab) throws IllegalArgumentException;
}
