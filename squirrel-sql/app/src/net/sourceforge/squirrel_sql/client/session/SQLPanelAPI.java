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
 *  * This class is the API through which plugins can work with the SQL Panel.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLPanelAPI implements ISQLPanelAPI
{
	/** Session containing the object tree. */
	private IClientSession _session;

	/**
	 * Ctor specifying the session.
	 * 
	 * @param	session	<TT>ISession</TT> containing the SQL Panel.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <T>null</TT> <TT>ISession</TT> passed.
	 */
	SQLPanelAPI(IClientSession session)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		_session = session;
	}

	/**
	 * Add a listener listening for SQL Execution.
	 *
	 * @param	lis	 Listener
	 *
	 * @throws	IllegalArgumentException
	 *			If a null <TT>ISQLExecutionListener</TT> passed.
	 */
	public synchronized void addSQLExecutionListener(ISQLExecutionListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("null ISQLExecutionListener passed");
		}
		_session.getSessionSheet().getSQLPanel().addSQLExecutionListener(lis);
	}

	/**
	 * Remove an SQL execution listener.
	 *
	 * @param	lis	 Listener
	 *
	 * @throws	IllegalArgumentException
	 *			If a null <TT>ISQLExecutionListener</TT> passed.
	 */
	public synchronized void removeSQLExecutionListener(ISQLExecutionListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("null ISQLExecutionListener passed");
		}
		_session.getSessionSheet().getSQLPanel().removeSQLExecutionListener(lis);
	}

	/**
	 * Add a listener for events in this sessions result tabs.
	 *
	 * @param	lis		The listener.
	 */
	public synchronized void addResultTabListener(IResultTabListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("null IResultTabListener passed");
		}
		_session.getSessionSheet().getSQLPanel().addResultTabListener(lis);
	}

	/**
	 * Remove a listener for events in this sessions result tabs.
	 *
	 * @param	lis		The listener.
	 */
	public synchronized void removeResultTabListener(IResultTabListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("null IResultTabListener passed");
		}
		_session.getSessionSheet().getSQLPanel().removeResultTabListener(lis);
	}

	/**
	 * Return the entire contents of the SQL entry area.
	 * 
	 * @return	the entire contents of the SQL entry area.
	 */
	public synchronized String getEntireSQLScript()
	{
		return _session.getSessionSheet().getSQLEntryPanel().getText();
	}

	/**
	 * Return the SQL script to be executed.
	 * 
	 * @return	the SQL script to be executed.
	 */
	public synchronized String getSQLScriptToBeExecuted()
	{
		return _session.getSessionSheet().getSQLEntryPanel().getSQLToBeExecuted();
	}

	/**
	 * Append the passed SQL script to the SQL entry area but don't select
	 * it.
	 * 
	 * @param	sqlScript	The script to be appended.
	 */
	public synchronized void appendSQLScript(String sqlScript)
	{
		_session.getSessionSheet().getSQLEntryPanel().appendText(sqlScript);
	}

	/**
	 * Append the passed SQL script to the SQL entry area and specify
	 * whether it should be selected.
	 * 
	 * @param	sqlScript	The script to be appended.
	 * @param	select		If <TT>true</TT> then select the passed script
	 * 						in the sql entry area.
	 */
	public synchronized void appendSQLScript(String sqlScript, boolean select)
	{
		_session.getSessionSheet().getSQLEntryPanel().appendText(sqlScript, select);
	}

	/**
	 * Replace the contents of the SQL entry area with the passed
	 * SQL script without selecting it.
	 * 
	 * @param	sqlScript	The script to be placed in the SQL entry area..
	 */
	public synchronized void setEntireSQLScript(String sqlScript)
	{
		_session.getSessionSheet().getSQLEntryPanel().setText(sqlScript);
	}

	/**
	 * Replace the contents of the SQL entry area with the passed
	 * SQL script and specify whether to select it.
	 * 
	 * @param	sqlScript	The script to be placed in the SQL entry area..
	 * @param	select		If <TT>true</TT> then select the passed script
	 * 						in the sql entry area.
	 */
	public synchronized void setEntireSQLScript(String sqlScript, boolean select)
	{
		_session.getSessionSheet().getSQLEntryPanel().setText(sqlScript, select);
	}

	/**
	 * Return the offset into the SQL entry area where the current select
	 * starts.
	 * 
	 * @return	the current selections start position.
	 */ 
	public synchronized int getSQLScriptSelectionStart()
	{
		return _session.getSessionSheet().getSQLEntryPanel().getSelectionStart();
	}

	/**
	 * Return the offset into the SQL entry area where the current select
	 * ends.
	 * 
	 * @return	the current selections end position.
	 */ 
	public synchronized int getSQLScriptSelectionEnd()
	{
		return _session.getSessionSheet().getSQLEntryPanel().getSelectionEnd();
	}

	/**
	 * Set the offset into the SQL entry area where the current select
	 * starts.
	 * 
	 * param	start	the new selections start position.
	 */ 
	public synchronized void setSQLScriptSelectionStart(int start)
	{
		_session.getSessionSheet().getSQLEntryPanel().setSelectionStart(start);
	}

	/**
	 * Set the offset into the SQL entry area where the current select
	 * ends.
	 * 
	 * param	start	the new selections start position.
	 */ 
	public synchronized void setSQLScriptSelectionEnd(int end)
	{
		_session.getSessionSheet().getSQLEntryPanel().setSelectionEnd(end);
	}

	/**
	 * Execute the current SQL. Not <TT>synchronized</TT> as multiple SQL statements
	 * can be executed simultaneously.
	 */
	public void executeCurrentSQL()
	{
		_session.getSessionSheet().getSQLPanel().executeCurrentSQL();
	}

	/**
	 * Execute the passed SQL. Not <TT>synchronized</TT> as multiple SQL statements
	 * can be executed simultaneously.
	 * 
	 * @param	sql		SQL to be executed.
	 */
	public void executeSQL(String sql)
	{
		_session.getSessionSheet().getSQLPanel().executeSQL(sql);
	}

	/**
	 * Commit the current SQL transaction.
	 */
	public synchronized void commit()
	{
		try
		{
			_session.getSQLConnection().commit();
			_session.getMessageHandler().showMessage("Commit completed normally."); // i18n
		}
		catch (Throwable ex)
		{
			_session.getMessageHandler().showErrorMessage(ex);
		}
	}

	/**
	 * Rollback the current SQL transaction.
	 */
	public synchronized void rollback()
	{
		try
		{
			_session.getSQLConnection().rollback();
			_session.getMessageHandler().showMessage("Rollback completed normally."); // i18n
		}
		catch (Exception ex)
		{
			_session.getMessageHandler().showErrorMessage(ex);
		}
	}

	/**
	 * Close all the SQL result tabs.
	 */
	public synchronized void closeAllSQLResultTabs()
	{
		_session.getSessionSheet().getSQLPanel().closeAllSQLResultTabs();
	}

	/**
	 * Close all the "torn off" SQL result frames.
	 */
	public synchronized void closeAllSQLResultFrames()
	{
		_session.getSessionSheet().getSQLPanel().closeAllSQLResultFrames();
	}

}
