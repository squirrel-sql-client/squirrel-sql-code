package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2002-2003 Colin Bell and Johan Compagner
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

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.session.event.IResultTabListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLPanelListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistoryItem;
/**
 * This class is the API through which plugins can work with the SQL Panel.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLPanelAPI implements ISQLPanelAPI
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(MainPanel.class);

	/** Session containing the SQL Panel. */
	private ISession _session;

	/**
	 * Ctor specifying the session.
	 *
	 * @param	session	<TT>ISession</TT> containing the SQL Panel.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <T>null</TT> <TT>ISession</TT> passed.
	 */
	SQLPanelAPI(ISession session)
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
	 * Add a listener for events in this SQL Panel.
	 *
	 * @param	lis	 Listener
	 *
	 * @throws	IllegalArgumentException
	 *			If a null <TT>ISQLPanelListener</TT> passed.
	 */
	public synchronized void addSQLPanelListener(ISQLPanelListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("null ISQLPanelListener passed");
		}
		_session.getSessionSheet().getSQLPanel().addSQLPanelListener(lis);
	}

	/**
	 * Remove a listener.
	 *
	 * @param	lis	Listener
	 *
	 * @throws	IllegalArgumentException
	 *			If a null <TT>ISQLPanelListener</TT> passed.
	 */
	public synchronized void removeSQLPanelListener(ISQLPanelListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("null ISQLPanelListener passed");
		}
		_session.getSessionSheet().getSQLPanel().removeSQLPanelListener(lis);
	}

	/**
	 * Replace the SQL entry area with the passed one.
	 *
	 * @param	pnl		New SQL entry area.
	 */
	public synchronized void installSQLEntryPanel(ISQLEntryPanel pnl)
	{
		_session.getSessionSheet().installSQLEntryPanel(pnl);
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
	 * Return the selected contents of the SQL entry area.
	 *
	 * @return	the selected contents of the SQL entry area.
	 */
	public String getSelectedSQLScript()
	{
		return _session.getSessionSheet().getSQLEntryPanel().getSelectedText();
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
	 * @param	sqlScript	The script to be placed in the SQL entry area.
	 */
	public synchronized void setEntireSQLScript(String sqlScript)
	{
		_session.getSessionSheet().getSQLEntryPanel().setText(sqlScript);
	}

	/**
	 * Replace the contents of the SQL entry area with the passed
	 * SQL script and specify whether to select it.
	 *
	 * @param	sqlScript	The script to be placed in the SQL entry area.
	 * @param	select		If <TT>true</TT> then select the passed script
	 * 						in the sql entry area.
	 */
	public synchronized void setEntireSQLScript(String sqlScript, boolean select)
	{
		_session.getSessionSheet().getSQLEntryPanel().setText(sqlScript, select);
	}

	/**
	 * Replace the currently selected text in the SQL entry area
	 * with the passed text.
	 *
	 * @param	sqlScript	The script to be placed in the SQL entry area.
	 * @param	select		If <TT>true</TT> then select the passed script
	 * 						in the sql entry area.
	 */
	public synchronized void replaceSelectedSQLScript(String sqlScript, boolean select)
	{
		if (sqlScript == null)
		{
			sqlScript = "";
		}
		ISQLEntryPanel pnl = _session.getSessionSheet().getSQLEntryPanel();
		int selStart = -1;
		if (select)
		{
			selStart = pnl.getSelectionStart();
		}
		pnl.replaceSelection(sqlScript);
		if (select)
		{
			int entireLen = getEntireSQLScript().length();
			if (selStart == -1)
			{
				selStart = 0;
			}
			int selEnd = selStart + sqlScript.length() - 1;
			if (selEnd > entireLen - 1)
			{
				selEnd = entireLen - 1;
			}
			if (selStart <= selEnd)
			{
				pnl.setSelectionStart(selStart);
				pnl.setSelectionEnd(selEnd);
			}
		}
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
			final String msg = s_stringMgr.getString("SQLPanelAPI.commit");
			_session.getMessageHandler().showMessage(msg);
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
			final String msg = s_stringMgr.getString("SQLPanelAPI.rollback");
			_session.getMessageHandler().showMessage(msg);
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

	/**
	 * Display the next tab in the SQL results.
	 */
	public synchronized void gotoNextResultsTab()
	{
		_session.getSessionSheet().getSQLPanel().gotoNextResultsTab();
	}

	/**
	 * Display the previous tab in the SQL results.
	 */
	public synchronized void gotoPreviousResultsTab()
	{
		_session.getSessionSheet().getSQLPanel().gotoPreviousResultsTab();
	}

	/**
	 * The passed SQL should be added to the SQL history.
	 *
	 * @param	sql		SQL to be added to history.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>sql</TT> passed.
	 */
	public synchronized void addSQLToHistory(String sql)
	{
		if (sql == null)
		{
			throw new IllegalArgumentException("sql == null");
		}

		SQLHistoryItem shi = new SQLHistoryItem(sql);
		if (_session.getProperties().getSQLShareHistory())
		{
			_session.getApplication().getSQLHistory().add(shi);
		}
		_session.getSessionSheet().getSQLPanel().addSQLToHistory(shi);
	}

	/**
	 * Add a hierarchical menu to the SQL Entry Area popup menu.
	 *
	 * @param	menu	The menu that will be added.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>Menu</TT> passed.
	 */
	public void addToSQLEntryAreaMenu(JMenu menu)
	{
		if (menu == null)
		{
			throw new IllegalArgumentException("Menu == null");
		}
		_session.getSessionSheet().getSQLPanel().addToSQLEntryAreaMenu(menu);
	}

	/**
	 * Add an <TT>Action</TT> to the SQL Entry Area popup menu.
	 *
	 * @param	action	The action to be added.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>Action</TT> passed.
	 */
	public void addToSQLEntryAreaMenu(Action action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Action == null");
		}
		_session.getSessionSheet().getSQLPanel().addToSQLEntryAreaMenu(action);
	}
}
