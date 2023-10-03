package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2002-2004 Colin Bell and Johan Compagner
 * colbell@users.sourceforge.net
 * jcompagner@j-com.nl
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
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

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ToolsPopupController;
import net.sourceforge.squirrel_sql.client.gui.titlefilepath.TitleFilePathHandler;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.action.*;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.actions.CompareToClipboardAction;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.SQLScriptMenuFactory;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLPanelListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLResultExecuterTabListener;
import net.sourceforge.squirrel_sql.client.session.filemanager.FileHandler;
import net.sourceforge.squirrel_sql.client.session.filemanager.SQLPanelSelectionHandler;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ISQLResultExecutor;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistoryItem;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanelPosition;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.ChangeTracker;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.SQLPanelSplitter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * This class is the API through which plugins can work with the SQL Panel.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLPanelAPI implements ISQLPanelAPI
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLPanelAPI.class);

	private SQLPanel _panel;

   private ToolsPopupController _toolsPopupController;
   private FileHandler _fileHandler;


	public SQLPanelAPI(SQLPanel panel, TitleFilePathHandler titleFileHandler)
	{

		_panel = panel;
		_fileHandler = new FileHandler(this, titleFileHandler);
		_panel.getSQLEntryPanel().addUndoableEditListener(_fileHandler.createEditListener());
      initToolsPopUp();

      createStandardEntryAreaMenuItems();
	}

   private void initToolsPopUp()
   {
      _toolsPopupController = new ToolsPopupController(getSession(), _panel.getSQLEntryPanel());


      ActionCollection ac = getSession().getApplication().getActionCollection();

		Main.getApplication().getActionRegistry().registerToolsPopupActions(ac, _toolsPopupController, _panel, isInMainSessionWindow());
	}

	public boolean isInMainSessionWindow()
	{
		return SQLPanelPosition.MAIN_TAB_IN_SESSION_WINDOW == _panel.getSQLPanelPosition()  || SQLPanelPosition.ADDITIONAL_TAB_IN_SESSION_WINDOW == _panel.getSQLPanelPosition();
	}


	private void createStandardEntryAreaMenuItems()
   {
      JMenuItem item;
      SquirrelResources resources = getSession().getApplication().getResources();


      ActionCollection ac = _panel.getSession().getApplication().getActionCollection();

      Action toolsPopupAction = ac.get(ToolsPopupAction.class);
      item = getSQLEntryPanel().addToSQLEntryAreaMenu(toolsPopupAction);
      resources.configureMenuItem(toolsPopupAction, item);

      if(isInMainSessionWindow())
      {
         Action vioAction = ac.get(ViewObjectAtCursorInObjectTreeAction.class);
         item = getSQLEntryPanel().addToSQLEntryAreaMenu(vioAction);
         resources.configureMenuItem(vioAction, item);
      }

      Action formatSqlAction = ac.get(FormatSQLAction.class);
      item = getSQLEntryPanel().addToSQLEntryAreaMenu(formatSqlAction);
      resources.configureMenuItem(formatSqlAction, item);

      Action compareToClipboardAction = ac.get(CompareToClipboardAction.class);
      item = getSQLEntryPanel().addToSQLEntryAreaMenu(compareToClipboardAction);
      resources.configureMenuItem(compareToClipboardAction, item);

		getSQLEntryPanel().addSeparatorToSQLEntryAreaMenu();

		item = getSQLEntryPanel().addToSQLEntryAreaMenu(ac.get(InQuotesAction.class));
		resources.configureMenuItem(ac.get(InQuotesAction.class), item);

		item = getSQLEntryPanel().addToSQLEntryAreaMenu(ac.get(RemoveQuotesAction.class));
		resources.configureMenuItem(ac.get(RemoveQuotesAction.class), item);

      getSQLEntryPanel().addToSQLEntryAreaMenu(ac.get(ConvertToStringBuilderAction.class));

		getSQLEntryPanel().addSeparatorToSQLEntryAreaMenu();

      item = getSQLEntryPanel().addToSQLEntryAreaMenu(ac.get(DeleteCurrentLineAction.class));
      resources.configureMenuItem(ac.get(DeleteCurrentLineAction.class), item);

      item = getSQLEntryPanel().addToSQLEntryAreaMenu(ac.get(RemoveNewLinesAction.class));
      resources.configureMenuItem(ac.get(RemoveNewLinesAction.class), item);

      getSQLEntryPanel().addToSQLEntryAreaMenu(ac.get(EscapeDateAction.class));

      item = getSQLEntryPanel().addToSQLEntryAreaMenu(ac.get(CutSqlAction.class));
      resources.configureMenuItem(ac.get(CutSqlAction.class), item);

      item = getSQLEntryPanel().addToSQLEntryAreaMenu(ac.get(CopySqlAction.class));
      resources.configureMenuItem(ac.get(CopySqlAction.class), item);

		item = getSQLEntryPanel().addToSQLEntryAreaMenu(ac.get(DeleteSqlAction.class));
		resources.configureMenuItem(ac.get(DeleteSqlAction.class), item);

      item = getSQLEntryPanel().addToSQLEntryAreaMenu(ac.get(PasteFromHistoryAction.class));
      resources.configureMenuItem(ac.get(PasteFromHistoryAction.class), item);

      item = getSQLEntryPanel().addToSQLEntryAreaMenu(ac.get(PasteFromHistoryAltAcceleratorAction.class));
      resources.configureMenuItem(ac.get(PasteFromHistoryAltAcceleratorAction.class), item);

      item = getSQLEntryPanel().addToSQLEntryAreaMenu(ac.get(ToggleMinimizeResultsAction.class));
      resources.configureMenuItem(ac.get(ToggleMinimizeResultsAction.class), item);

		getSQLEntryPanel().addSeparatorToSQLEntryAreaMenu();
		SQLScriptMenuFactory.addMenuItemsToSQLPanelApi(this);
		getSQLEntryPanel().addSeparatorToSQLEntryAreaMenu();
   }


   public void addToToolsPopUp(String selectionString, Action action)
   {
      _toolsPopupController.addAction(selectionString, action);
   }


   public void showToolsPopup()
   {
      _toolsPopupController.showToolsPopup();
   }


   public void addExecutor(ISQLResultExecutor exec)
	{
		_panel.addExecutor(exec);
	}

	public void removeExecutor(ISQLResultExecutor exec)
	{
		_panel.removeExecutor(exec);
	}

	/**
	 * Add a listener listening for SQL Execution.
	 *
	 * @param	lis		Listener to add
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a null <TT>ISQLExecutionListener</TT> passed.
	 */
	public synchronized void addSQLExecutionListener(ISQLExecutionListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("null ISQLExecutionListener passed");
		}
		_panel.addSQLExecutionListener(lis);
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
		_panel.removeSQLExecutionListener(lis);
	}

	/**
	 * Add a listener for events in this sql panel executer tabs.
	 *
	 * @param	lis		The listener.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a null <TT>ISQLResultExecuterTabListener</TT> passed.
	 */
	public void addExecuterTabListener(ISQLResultExecuterTabListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("ISQLResultExecuterTabListener == null");
		}
		_panel.addExecuterTabListener(lis);
	}

	/**
	 * Remove a listener for events from this sql panel executer tabs.
	 *
	 * @param	lis		The listener.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a null <TT>ISQLResultExecuterTabListener</TT> passed.
	 */
	public void removeExecuterTabListener(ISQLResultExecuterTabListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("ISQLResultExecuterTabListener == null");
		}
		_panel.removeExecuterTabListener(lis);
	}

	/**
	 * Add a listener for events in this SQL Panel.
	 *
	 * @param	lis		Listener to add
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a null <TT>ISQLPanelListener</TT> passed.
	 */
	public synchronized void addSQLPanelListener(ISQLPanelListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("ISQLPanelListener == null");
		}
		_panel.addSQLPanelListener(lis);
	}

	/**
	 * Remove a listener.
	 *
	 * @param	lis		Listener to remove
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a null <TT>ISQLPanelListener</TT> passed.
	 */
	public synchronized void removeSQLPanelListener(ISQLPanelListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("ISQLPanelListener == null");
		}
		_panel.removeSQLPanelListener(lis);
	}

	public ISQLEntryPanel getSQLEntryPanel()
	{
		return _panel.getSQLEntryPanel();
	}

    /**
     * Returns the result execution panel that stores such things as IResultTabs
     * 
     * @return an implementation of ISQLResultExecuter 
     */
	 public ISQLResultExecutor getSQLResultExecuter()
	 {
		 return _panel.getSQLExecPanel();
	 }
    
	/**
	 * Return the entire contents of the SQL entry area.
	 *
	 * @return	the entire contents of the SQL entry area.
	 */
	public String getEntireSQLScript()
	{
		return _panel.getSQLEntryPanel().getText();
	}

	/**
	 * Return the selected contents of the SQL entry area.
	 *
	 * @return	the selected contents of the SQL entry area.
	 */
	public String getSelectedSQLScript()
	{
		return _panel.getSQLEntryPanel().getSelectedText();
	}

	/**
	 * Return the SQL script to be executed.
	 *
	 * @return	the SQL script to be executed.
	 */
	public String getSQLScriptToBeExecuted()
	{
		return _panel.getSQLEntryPanel().getSQLToBeExecuted();
	}

	/**
	 * Append the passed SQL script to the SQL entry area but don't select
	 * it.
	 *
	 * @param	sqlScript	The script to be appended.
	 */
	public void appendSQLScript(String sqlScript)
	{
		_panel.getSQLEntryPanel().appendText(sqlScript);
	}

	/**
	 * Append the passed SQL script to the SQL entry area and specify
	 * whether it should be selected.
	 *
	 * @param	sqlScript	The script to be appended.
	 * @param	select		If <TT>true</TT> then select the passed script
	 * 						in the sql entry area.
	 */
	public void appendSQLScript(String sqlScript, boolean select)
	{
        _panel.getSQLEntryPanel().appendText(sqlScript, select);
	}

	/**
	 * Replace the contents of the SQL entry area with the passed
	 * SQL script without selecting it.
	 *
	 * @param	sqlScript	The script to be placed in the SQL entry area.
	 */
	public void setEntireSQLScript(String sqlScript)
	{
		_panel.getSQLEntryPanel().setText(sqlScript);
	}

	/**
	 * Replace the contents of the SQL entry area with the passed
	 * SQL script and specify whether to select it.
	 *
	 * @param	sqlScript	The script to be placed in the SQL entry area.
	 * @param	select		If <TT>true</TT> then select the passed script
	 * 						in the sql entry area.
	 */
	public void setEntireSQLScript(String sqlScript, boolean select)
	{
		_panel.getSQLEntryPanel().setText(sqlScript, select);
	}

	/**
	 * Replace the currently selected text in the SQL entry area
	 * with the passed text.
	 *
	 * @param	sqlScript	The script to be placed in the SQL entry area.
	 * @param	select		If <TT>true</TT> then select the passed script
	 * 						in the sql entry area.
	 */
	public void replaceSelectedSQLScript(String sqlScript, boolean select)
	{
		if (sqlScript == null)
		{
			sqlScript = "";
		}

		final ISQLEntryPanel pnl = _panel.getSQLEntryPanel();
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
	public int getSQLScriptSelectionStart()
	{
		return _panel.getSQLEntryPanel().getSelectionStart();
	}

	/**
	 * Return the offset into the SQL entry area where the current select
	 * ends.
	 *
	 * @return	the current selections end position.
	 */
	public int getSQLScriptSelectionEnd()
	{
		return _panel.getSQLEntryPanel().getSelectionEnd();
	}

	/**
	 * Set the offset into the SQL entry area where the current select
	 * starts.
	 *
	 * param	start	the new selections start position.
	 */
	public void setSQLScriptSelectionStart(int start)
	{
		_panel.getSQLEntryPanel().setSelectionStart(start);
	}

	/**
	 * Set the offset into the SQL entry area where the current select
	 * ends.
	 *
	 * param	start	the new selections start position.
	 */
	public void setSQLScriptSelectionEnd(int end)
	{
		_panel.getSQLEntryPanel().setSelectionEnd(end);
	}

	public void executeCurrentSQL()
	{
		_panel.runCurrentExecuter();
	}

   @Override
   public void executeAllSQLs()
   {
      _panel.runAllSqlsExecuter();
   }

   /**
	 * Close all the SQL result tabs.
	 */
	public void closeAllSQLResultTabs()
	{
		_panel.getSQLExecPanel().closeAllSQLResultTabs();
	}

	public void closeAllSQLResultTabs(boolean isMemoryCleanUp)
	{
		_panel.getSQLExecPanel().closeAllSQLResultTabs(isMemoryCleanUp);
	}

	public void closeAllButCurrentResultTabs()
   {
      _panel.getSQLExecPanel().closeAllButCurrentResultTabs();
   }

   @Override
   public void closeAllToResultTabs(boolean left)
   {
		_panel.getSQLExecPanel().closeAllToResultTabs(left);
   }

   public void closeCurrentResultTab()
   {
      _panel.getSQLExecPanel().closeCurrentResultTab();
   }

   public void toggleCurrentSQLResultTabSticky()
   {
      _panel.getSQLExecPanel().toggleCurrentSQLResultTabSticky();
   }

	@Override
	public void toggleCurrentSQLResultTabAnchored()
	{
		_panel.getSQLExecPanel().toggleCurrentSQLResultTabAnchored();
	}

	/**
	 * Close all the "torn off" SQL result frames.
	 */
	public void closeAllSQLResultFrames()
	{
		_panel.getSQLExecPanel().closeAllSQLResultFrames();
	}

	/**
	 * Display the next tab in the SQL results.
	 */
	public synchronized void gotoNextResultsTab()
	{
		_panel.getSQLExecPanel().gotoNextResultsTab();
	}

	/**
	 * Display the previous tab in the SQL results.
	 */
	public void gotoPreviousResultsTab()
	{
		_panel.getSQLExecPanel().gotoPreviousResultsTab();
	}

	/**
	 * The passed SQL should be added to the SQL history.
	 *
	 * @param	sql		SQL to be added to history.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>sql</TT> passed.
	 */

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
		_panel.addToSQLEntryAreaMenu(menu);
	}

	/**
	 * Add an <TT>Action</TT> to the SQL Entry Area popup menu.
	 *
	 * @param	action	The action to be added.
	 *
	 * @return	The newly create menu item.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>Action</TT> passed.
	 */
	public JMenuItem addToSQLEntryAreaMenu(Action action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Action == null");
		}
		return _panel.addToSQLEntryAreaMenu(action);
	}

	@Override
	public void addSeparatorToSQLEntryAreaMenu()
	{
		_panel.addSeparatorToSQLEntryAreaMenu();
	}

	/** JASON: Remove once deprecated interface removed*/
	public ISession getSession()
	{
		return _panel.getSession();
	}

	public boolean confirmClose()
	{
		if(false == _panel.getSQLExecPanel().confirmClose())
		{
			return false;
		}

		if(false == _fileHandler.showConfirmCloseIfNecessary())
		{
			return false;
		}

		return true;
	}

	public ArrayList<SQLHistoryItem> getSQLHistoryItems()
   {
      return _panel.getSQLHistoryItems();
   }

	@Override
	public void toggleMinimizeResults()
	{
		_panel.toggleMinimizeResults();
	}

	@Override
	public void resetUnsavedEdits()
	{
		_fileHandler.resetUnsavedEdits();
	}

	@Override
	public SQLPanelSplitter getSQLPanelSplitter()
	{
		return _panel.getSqlPanelSplitter();
	}

	@Override
	public ChangeTracker getChangeTracker()
	{
		return _panel.getChangeTracker();
	}

   @Override
	public void selectWidgetOrTab()
	{
		SQLPanelSelectionHandler.selectSqlPanel(this);
	}

	@Override
	public Frame getOwningFrame()
	{
		return SessionUtils.getOwningFrame(this);
	}

   @Override
	public int getCaretPosition()
	{
		return _panel.getSQLEntryPanel().getCaretPosition();
	}

	@Override
	public void setCaretPosition(int caretPos)
	{
		_panel.getSQLEntryPanel().setCaretPosition(caretPos);
	}

	@Override
	public JTextArea getTextComponent()
	{
		return _panel.getSQLEntryPanel().getTextComponent();
	}

   @Override
   public FileHandler getFileHandler()
   {
      return _fileHandler;
   }

   @Override
   public ISQLPanelAPI getSQLPanelAPIOrNull()
   {
      return this;
   }

   @Override
	public byte[] getBytesForSave()
	{
		return _fileHandler.getBytesForSave();
   }

   @Override
   public UndoRedoActionContext getUndoRedoActionContext()
   {
      return _panel.getUndoHandlerImpl().getUndoRedoActionContext();
   }

	@Override
	public SQLPanelPosition getSQLPanelPosition()
	{
		return _panel.getSQLPanelPosition();
	}

}
