package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2002 Colin Bell and Johan Compagner
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
import net.sourceforge.squirrel_sql.client.session.event.IResultTabListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
/**
 * This interface defines the API through which plugins can work with the SQL
 * panel.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface ISQLPanelAPI
{
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
	 * Return the entire contents of the SQL entry area.
	 * 
	 * @return	the entire contents of the SQL entry area.
	 */
	String getEntireSQLScript();

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
	 * @param	sqlScript	The script to be placed in the SQL entry area..
	 */
	void setEntireSQLScript(String sqlScript);

	/**
	 * Replace the contents of the SQL entry area with the passed
	 * SQL script and specify whether to select it.
	 * 
	 * @param	sqlScript	The script to be placed in the SQL entry area..
	 * @param	select		If <TT>true</TT> then select the passed script
	 * 						in the sql entry area.
	 */
	void setEntireSQLScript(String sqlScript, boolean select);

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
	 * Execute the passed SQL.
	 * 
	 * @param	sql		SQL to be executed.
	 */
	void executeSQL(String sql);

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
	 * Close all the SQL result tabs.
	 */
	void closeAllSQLResultTabs();

	/**
	 * Close all the "torn off" SQL result frames.
	 */
	void closeAllSQLResultFrames();
}
