package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2002-2004 Colin Bell and Johan Compagner
 * colbell@users.sourceforge.net
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
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLPanelListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLResultExecuterTabListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ISQLResultExecuter;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SqlPanelListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistoryItem;

import java.io.File;
import java.util.ArrayList;

/**
 * This interface defines the API through which plugins can work with the SQL
 * panel.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface ISQLPanelAPI
{
	void addExecutor(ISQLResultExecuter exec);

	void removeExecutor(ISQLResultExecuter exec);

	/**
	 * Add a listener listening for SQL Execution.
	 *
	 * @param	lis	 Listener
	 *
	 * @throws	IllegalArgumentException
	 *			If a null <TT>ISQLExecutionListener</TT> passed.
	 */
	void addSQLExecutionListener(ISQLExecutionListener lis);

	/**
	 * Remove an SQL execution listener.
	 *
	 * @param	lis	 Listener
	 *
	 * @throws	IllegalArgumentException
	 *			If a null <TT>ISQLExecutionListener</TT> passed.
	 */
	void removeSQLExecutionListener(ISQLExecutionListener lis);


	/**
	 * Add a listener for events in this SQL Panel.
	 *
	 * @param	lis	 Listener
	 *
	 * @throws	IllegalArgumentException
	 *			If a null <TT>ISQLPanelListener</TT> passed.
	 */
	void addSQLPanelListener(ISQLPanelListener lis);

	/**
	 * Remove a listener.
	 *
	 * @param	lis	Listener
	 *
	 * @throws	IllegalArgumentException
	 *			If a null <TT>ISQLPanelListener</TT> passed.
	 */
	void removeSQLPanelListener(ISQLPanelListener lis);

	/**
	 * Add a listener for events in this sql panel executer tabs.
	 *
	 * @param	lis		The listener.
	 */
	void addExecuterTabListener(ISQLResultExecuterTabListener lis);

	/**
	 * Remove a listener for events in this sql panel executer tabs.
	 *
	 * @param	lis		The listener.
	 */
	void removeExecuterTabListener(ISQLResultExecuterTabListener lis);


	ISQLEntryPanel getSQLEntryPanel();

    /**
     * Returns the result execution panel that stores such things as IResultTabs
     * 
     * @return an implementation of ISQLResultExecuter 
     */
    ISQLResultExecuter getSQLResultExecuter();
    
	/**
	 * Return the entire contents of the SQL entry area.
	 *
	 * @return	the entire contents of the SQL entry area.
	 */
	String getEntireSQLScript();

	/**
	 * Return the selected contents of the SQL entry area.
	 *
	 * @return	the selected contents of the SQL entry area.
	 */
	String getSelectedSQLScript();

	/**
	 * Return the SQL script to be executed.
	 *
	 * @return	the SQL script to be executed.
	 */
	String getSQLScriptToBeExecuted();

	/**
	 * Append the passed SQL script to the SQL entry area but don't select
	 * it.
	 *
	 * @param	sqlScript	The script to be appended.
	 */
	void appendSQLScript(String sqlScript);

	/**
	 * Append the passed SQL script to the SQL entry area and specify
	 * whether it should be selected.
	 *
	 * @param	sqlScript	The script to be appended.
	 * @param	select		If <TT>true</TT> then select the passed script
	 * 						in the sql entry area.
	 */
	void appendSQLScript(String sqlScript, boolean select);

	/**
	 * Replace the contents of the SQL entry area with the passed
	 * SQL script without selecting it.
	 *
	 * @param	sqlScript	The script to be placed in the SQL entry area.
	 */
	void setEntireSQLScript(String sqlScript);

	/**
	 * Replace the contents of the SQL entry area with the passed
	 * SQL script and specify whether to select it.
	 *
	 * @param	sqlScript	The script to be placed in the SQL entry area.
	 * @param	select		If <TT>true</TT> then select the passed script
	 * 						in the sql entry area.
	 */
	void setEntireSQLScript(String sqlScript, boolean select);

	/**
	 * Replace the currently selected text in the SQL entry area
	 * with the passed text.
	 *
	 * @param	sqlScript	The script to be placed in the SQL entry area.
	 * @param	select		If <TT>true</TT> then select the passed script
	 * 						in the sql entry area.
	 */
	void replaceSelectedSQLScript(String sqlScript, boolean select);

	/**
	 * Return the offset into the SQL entry area where the current select
	 * starts.
	 *
	 * @return	the current selections start position.
	 */
	int getSQLScriptSelectionStart();

	/**
	 * Return the offset into the SQL entry area where the current select
	 * ends.
	 *
	 * @return	the current selections end position.
	 */
	int getSQLScriptSelectionEnd();

	/**
	 * Set the offset into the SQL entry area where the current select
	 * starts.
	 *
	 * param	start	the new selections start position.
	 */
	void setSQLScriptSelectionStart(int start);

	/**
	 * Set the offset into the SQL entry area where the current select
	 * ends.
	 *
	 * param	start	the new selections start position.
	 */
	void setSQLScriptSelectionEnd(int end);

   /**
	 * Execute the current SQL.
	 */
	void executeCurrentSQL();

   /**
	 * Close all the SQL result tabs.
	 */
	void closeAllSQLResultTabs();

   /**
	 * Close all the SQL result tabs except from the selected.
	 */
   void closeAllButCurrentResultTabs();

   /**
	 * Close the selected result tab.
	 */
   void closeCurrentResultTab();

   /**
	 * Toggle if all further SQL resutls should go to the current tab.
	 */
   void toggleCurrentSQLResultTabSticky();


	/**
	 * Close all the "torn off" SQL result frames.
	 */
	void closeAllSQLResultFrames();

	/**
	 * Display the next tab in the SQL results.
	 */
	void gotoNextResultsTab();

	/**
	 * Display the previous tab in the SQL results.
	 */
	void gotoPreviousResultsTab();

	/**
	 * The passed SQL should be added to the SQL history.
	 *
	 * @param	sql		SQL to be added to history.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>sql</TT> passed.
	 */
	void addSQLToHistory(String sql);

	/**
	 * Add a hierarchical menu to the SQL Entry Area popup menu.
	 *
	 * @param	menu	The menu that will be added.
	 */
	void addToSQLEntryAreaMenu(JMenu menu);

	/**
	 * Add an <TT>Action</TT> to the SQL Entry Area popup menu.
	 *
	 * @param	action	The action to be added.
	 */
	JMenuItem addToSQLEntryAreaMenu(Action action);

	ISession getSession();

	boolean isInMainSessionWindow();

	void addToToolsPopUp(String selectionString, Action action);

	boolean fileSave();

	void fileSaveAs();

	void fileOpen();
	
	void fileOpen(File f);

	void fileAppend();

	void fileClose();

	void fileNew();

	void filePrint();

	void showToolsPopup();

	boolean confirmClose();

	void addSqlPanelListener(SqlPanelListener sqlPanelListener);

	ArrayList<SQLHistoryItem> getSQLHistoryItems();
}

