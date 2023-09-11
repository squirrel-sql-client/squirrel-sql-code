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

import net.sourceforge.squirrel_sql.client.session.action.UndoRedoActionContext;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLPanelListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLResultExecuterTabListener;
import net.sourceforge.squirrel_sql.client.session.filemanager.IFileEditorAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ISQLResultExecutor;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistoryItem;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanelPosition;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.ChangeTracker;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.SQLPanelSplitter;

import javax.swing.*;
import java.util.ArrayList;

/**
 * This interface defines the API through which plugins can work with the SQL
 * panel.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface ISQLPanelAPI extends IFileEditorAPI
{
	void addExecutor(ISQLResultExecutor exec);

	void removeExecutor(ISQLResultExecutor exec);

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
    ISQLResultExecutor getSQLResultExecuter();
    

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

	void executeAllSQLs();


	/**
	 * Close all the SQL result tabs.
	 */
	void closeAllSQLResultTabs();

	void closeAllSQLResultTabs(boolean isMemoryCleanUp);

   /**
	 * Close all the SQL result tabs except from the selected.
	 */
   void closeAllButCurrentResultTabs();

	void closeAllToResultTabs(boolean left);

	/**
	 * Close the selected result tab.
	 */
   void closeCurrentResultTab();

   /**
	 * Toggle if all further SQL resutls should go to the current tab.
	 */
   void toggleCurrentSQLResultTabSticky();

	/**
	 * Toggle if this tab won't be removed automatically.
	 */
	void toggleCurrentSQLResultTabAnchored();


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

	void addSeparatorToSQLEntryAreaMenu();

	boolean isInMainSessionWindow();

	void addToToolsPopUp(String selectionString, Action action);

	void showToolsPopup();

	boolean confirmClose();

	ArrayList<SQLHistoryItem> getSQLHistoryItems();

	void toggleMinimizeResults();

	void resetUnsavedEdits();

	SQLPanelSplitter getSQLPanelSplitter();

	ChangeTracker getChangeTracker();

   byte[] getBytesForSave();

   UndoRedoActionContext getUndoRedoActionContext();

	SQLPanelPosition getSQLPanelPosition();
}

