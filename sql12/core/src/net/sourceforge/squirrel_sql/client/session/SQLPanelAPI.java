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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ToolsPopupController;
import net.sourceforge.squirrel_sql.client.preferences.PreferenceType;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.action.*;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLPanelListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLResultExecuterTabListener;
import net.sourceforge.squirrel_sql.client.session.filemanager.FileManager;
import net.sourceforge.squirrel_sql.client.session.filemanager.SQLPanelSelectionHandler;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ISQLResultExecuter;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistoryItem;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SqlPanelListener;
import net.sourceforge.squirrel_sql.client.util.PrintUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import java.io.File;
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
   private FileManager _fileManager = new FileManager(this);

	private boolean fileOpened = false;
	private boolean fileSaved = false;
	private boolean unsavedEdits = false;
   
	public SQLPanelAPI(SQLPanel panel)
	{
		super();
		if (panel == null)
		{
			throw new IllegalArgumentException("SQLPanel == null");
		}
		_panel = panel;
      _panel.getSQLEntryPanel().addUndoableEditListener(new SQLEntryUndoListener());
      initToolsPopUp();

      createStandardEntryAreaMenuItems();
	}

   private void initToolsPopUp()
   {
      _toolsPopupController = new ToolsPopupController(getSession(), _panel.getSQLEntryPanel());


      ActionCollection ac = getSession().getApplication().getActionCollection();

      _toolsPopupController.addAction("undo", _panel.getUndoAction());
      _toolsPopupController.addAction("redo", _panel.getRedoAction());
      _toolsPopupController.addAction("runsql", ac.get(ExecuteSqlAction.class));
      _toolsPopupController.addAction("runallsqls", ac.get(ExecuteAllSqlsAction.class));
      _toolsPopupController.addAction("filenew", ac.get(FileNewAction.class));
      _toolsPopupController.addAction("filedetach", ac.get(FileDetachAction.class));
      _toolsPopupController.addAction("fileopenrecent", ac.get(FileOpenRecentAction.class));
      _toolsPopupController.addAction("filesave", ac.get(FileSaveAction.class));
      _toolsPopupController.addAction("filesaveas", ac.get(FileSaveAsAction.class));
      _toolsPopupController.addAction("fileappend", ac.get(FileAppendAction.class));
      _toolsPopupController.addAction("fileclose", ac.get(FileCloseAction.class));
      _toolsPopupController.addAction("fileprint", ac.get(FilePrintAction.class));
      _toolsPopupController.addAction("filereload", ac.get(FileReloadAction.class));

      _toolsPopupController.addAction("tabnext", ac.get(GotoNextResultsTabAction.class));
      _toolsPopupController.addAction("tabprevious", ac.get(GotoPreviousResultsTabAction.class));
      _toolsPopupController.addAction("tabcloseall", ac.get(CloseAllSQLResultTabsAction.class));
      _toolsPopupController.addAction("tabcloseallbutcur", ac.get(CloseAllSQLResultTabsButCurrentAction.class));
      _toolsPopupController.addAction("tabclosealltoleft", ac.get(CloseAllSQLResultTabsToLeftAction.class));
      _toolsPopupController.addAction("tabclosealltoRight", ac.get(CloseAllSQLResultTabsToRightAction.class));
      _toolsPopupController.addAction("tabclosecur", ac.get(CloseCurrentSQLResultTabAction.class));
      _toolsPopupController.addAction("tabsticky", ac.get(ToggleCurrentSQLResultTabStickyAction.class));

      _toolsPopupController.addAction("minres", ac.get(ToggleMinimizeResultsAction.class));

      _toolsPopupController.addAction("sqlprevious", ac.get(PreviousSqlAction.class));
      _toolsPopupController.addAction("sqlnext", ac.get(NextSqlAction.class));
      _toolsPopupController.addAction("sqlselect", ac.get(SelectSqlAction.class));

      _toolsPopupController.addAction("format", ac.get(FormatSQLAction.class));

      _toolsPopupController.addAction("sqlhist", ac.get(OpenSqlHistoryAction.class));

      if (_panel.isInMainSessionWindow())
      {
         _toolsPopupController.addAction("viewinobjecttree", ac.get(ViewObjectAtCursorInObjectTreeAction.class));
      }

      _toolsPopupController.addAction("quote", ac.get(InQuotesAction.class));
      _toolsPopupController.addAction("unquote", ac.get(RemoveQuotesAction.class));
      _toolsPopupController.addAction("quotesb", ac.get(ConvertToStringBufferAction.class));
      _toolsPopupController.addAction("date", ac.get(EscapeDateAction.class));
      _toolsPopupController.addAction("sqlcut", ac.get(CutSqlAction.class));
      _toolsPopupController.addAction("sqlcopy", ac.get(CopySqlAction.class));
      _toolsPopupController.addAction("remnewlines", ac.get(RemoveNewLinesAction.class));
      _toolsPopupController.addAction("pastehist", ac.get(PasteFromHistoryAction.class));
   }


   private void createStandardEntryAreaMenuItems()
   {
      JMenuItem item;
      SquirrelResources resources = getSession().getApplication().getResources();


      ActionCollection ac = _panel.getSession().getApplication().getActionCollection();

      Action toolsPopupAction = ac.get(ToolsPopupAction.class);
      item = getSQLEntryPanel().addToSQLEntryAreaMenu(toolsPopupAction);
      resources.configureMenuItem(toolsPopupAction, item);

      if(_panel.isInMainSessionWindow())
      {
         Action vioAction = ac.get(ViewObjectAtCursorInObjectTreeAction.class);
         item = getSQLEntryPanel().addToSQLEntryAreaMenu(vioAction);
         resources.configureMenuItem(vioAction, item);
      }

      Action formatSqlAction = ac.get(FormatSQLAction.class);
      item = getSQLEntryPanel().addToSQLEntryAreaMenu(formatSqlAction);
      resources.configureMenuItem(formatSqlAction, item);



		getSQLEntryPanel().addSeparatorToSQLEntryAreaMenu();

		item = getSQLEntryPanel().addToSQLEntryAreaMenu(ac.get(InQuotesAction.class));
		resources.configureMenuItem(ac.get(InQuotesAction.class), item);

		item = getSQLEntryPanel().addToSQLEntryAreaMenu(ac.get(RemoveQuotesAction.class));
		resources.configureMenuItem(ac.get(RemoveQuotesAction.class), item);

      getSQLEntryPanel().addToSQLEntryAreaMenu(ac.get(ConvertToStringBufferAction.class));

		getSQLEntryPanel().addSeparatorToSQLEntryAreaMenu();





      item = getSQLEntryPanel().addToSQLEntryAreaMenu(ac.get(RemoveNewLinesAction.class));
      resources.configureMenuItem(ac.get(RemoveNewLinesAction.class), item);

      getSQLEntryPanel().addToSQLEntryAreaMenu(ac.get(EscapeDateAction.class));

      item = getSQLEntryPanel().addToSQLEntryAreaMenu(ac.get(CutSqlAction.class));
      resources.configureMenuItem(ac.get(CutSqlAction.class), item);

      item = getSQLEntryPanel().addToSQLEntryAreaMenu(ac.get(CopySqlAction.class));
      resources.configureMenuItem(ac.get(CopySqlAction.class), item);

      item = getSQLEntryPanel().addToSQLEntryAreaMenu(ac.get(PasteFromHistoryAction.class));
      resources.configureMenuItem(ac.get(PasteFromHistoryAction.class), item);

      item = getSQLEntryPanel().addToSQLEntryAreaMenu(ac.get(PasteFromHistoryAltAcceleratorAction.class));
      resources.configureMenuItem(ac.get(PasteFromHistoryAltAcceleratorAction.class), item);

      item = getSQLEntryPanel().addToSQLEntryAreaMenu(ac.get(ToggleMinimizeResultsAction.class));
      resources.configureMenuItem(ac.get(ToggleMinimizeResultsAction.class), item);

   }


   public void addToToolsPopUp(String selectionString, Action action)
   {
      _toolsPopupController.addAction(selectionString, action);
   }

   @Override
   public boolean fileSave()
   {
		if (_fileManager.save())
		{
			fileSaved = true;
			unsavedEdits = false;
			setUnsavedEdits(false);
			ActionCollection actions =
					getSession().getApplication().getActionCollection();
			actions.enableAction(FileSaveAction.class, false);
			return true;
		}
		else
		{
			return false;
		}
   }

   @Override
	public void fileAppend()
	{
		if (_fileManager.open(true))
		{
			fileOpened = true;
			fileSaved = false;
			unsavedEdits = false;
			ActionCollection actions =
					getSession().getApplication().getActionCollection();
			actions.enableAction(FileSaveAction.class, true);
		}
	}
   
   @Override
   public void fileClose()
   {
      _closeFile(true);
   }

   private boolean _closeFile(boolean clearEditor)
   {
      if (unsavedEdits)
      {
         if(false == showConfirmSaveDialog())
			{
				return false;
			}

      }
      if (clearEditor)
      {
         setEntireSQLScript("");
      }
		clearSqlFile();
		fileOpened = false;
      fileSaved = false;
      unsavedEdits = false;
      ActionCollection actions =
            getSession().getApplication().getActionCollection();
      actions.enableAction(FileSaveAction.class, true);
      _fileManager.clearCurrentFile();

		return true;
	}


	public void fileReload()
	{
		if(null == _fileManager.getFile())
		{
			Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("SQLPanelAPI.nofileToRelaod"));
			return;
		}

		File file = _fileManager.getFile();

		int caretPosition = getSQLEntryPanel().getCaretPosition();

		if(false == _closeFile(true))
		{
			return;
		}

		fileOpen(file);

		getSQLEntryPanel().setCaretPosition(Math.min(getSQLEntryPanel().getText().length(), caretPosition));

	}



	/* (non-Javadoc)
   * @see net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI#fileNew()
   */
   public void fileNew()
   {
       fileClose();
	}

   public void fileDetach()
   {
       _closeFile(false);
	}

   /* (non-Javadoc)
    * @see net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI#fileSaveAs()
    */
   public void fileSaveAs()
   {
		if (_fileManager.saveAs())
		{
			fileSaved = true;
			unsavedEdits = false;
			setUnsavedEdits(false);
			ActionCollection actions =
					getSession().getApplication().getActionCollection();
			actions.enableAction(FileSaveAction.class, false);
		}
   }

   /* (non-Javadoc)
    * @see net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI#fileOpen()
    */
   public void fileOpen()
   {
		if (unsavedEdits)
		{
			if(false == showConfirmSaveDialog())
			{
				return;
			}
		}

		if (_fileManager.open(false))
		{
			fileOpened = true;
			fileSaved = false;
			unsavedEdits = false;
			ActionCollection actions =
					getSession().getApplication().getActionCollection();
			actions.enableAction(FileSaveAction.class, false);
		}

		getSQLEntryPanel().setCaretPosition(0);

	}

   public void fileOpen(File f)
	{
      fileOpen(f, false);
   }

	public void fileOpen(File f, boolean append)
	{
		if (unsavedEdits)
		{
			showConfirmSaveDialog();
		}
		if (_fileManager.open(f, append))
		{
			fileOpened = true;
			fileSaved = false;
			unsavedEdits = false;
			ActionCollection actions =
					getSession().getApplication().getActionCollection();
			actions.enableAction(FileSaveAction.class, false);
		}

		getSQLEntryPanel().setCaretPosition(0);


	}

	/*
    * @see net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI#filePrint()
    */
	public void filePrint()
	{
		if (_panel == null)
		{
			throw new IllegalStateException("_panel is null");
		}
		ISQLEntryPanel panel = _panel.getSQLEntryPanel();
		if (panel == null)
		{
			throw new IllegalStateException("_panel.getMainSQLEntryPanel() is null");
		}
		PrintUtilities.printComponent(panel.getTextComponent());
	}
   
   public void showToolsPopup()
   {
      _toolsPopupController.showToolsPopup();
   }


   public void addExecutor(ISQLResultExecuter exec)
	{
		_panel.addExecutor(exec);
	}

	public void removeExecutor(ISQLResultExecuter exec)
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
	 public ISQLResultExecuter getSQLResultExecuter()
	 {
		 return _panel.getSQLExecPanel();
	 }
    
	/**
	 * Return the entire contents of the SQL entry area.
	 *
	 * @return	the entire contents of the SQL entry area.
	 */
	public synchronized String getEntireSQLScript()
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
		_panel.getSQLEntryPanel().setCaretPosition(0);
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
	public void addSQLToHistory(String sql)
	{
		if (sql == null)
		{
			throw new IllegalArgumentException("sql == null");
		}

      final ISession session = _panel.getSession();
		final SQLHistoryItem shi = new SQLHistoryItem(sql, session.getAlias().getName());
		if (session.getProperties().getSQLShareHistory())
		{
			session.getApplication().getSQLHistory().add(shi);
			session.getApplication().savePreferences(PreferenceType.SQLHISTORY);
		}
		_panel.addSQLToHistory(shi);
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

   public boolean isInMainSessionWindow()
   {
      return _panel.isInMainSessionWindow();
   }

	public boolean confirmClose()
	{
		if (unsavedEdits)
		{
			return showConfirmSaveDialog();
		}
		return true;
	}

   public void addSqlPanelListener(SqlPanelListener sqlPanelListener)
   {
      _panel.addSqlPanelListener(sqlPanelListener);
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
		unsavedEdits = false;
	}

	private boolean showConfirmSaveDialog()
   {
       File file = _fileManager.getFile();
       
       // i18n[SQLPanelAPI.untitledLabel=Untitled]
       String filename = s_stringMgr.getString("SQLPanelAPI.untitledLabel");
       
       if (file != null)
       {
           filename = file.getAbsolutePath();
       }
       String msg = s_stringMgr.getString("SQLPanelAPI.unsavedchanges",filename);
       
       
       
       String title =  s_stringMgr.getString("SQLPanelAPI.unsavedchangestitle",": "+_panel.getSession().getAlias().getName());
       
       JFrame f = (JFrame) SessionUtils.getOwningFrame(this);

       int option = JOptionPane.showConfirmDialog(f, msg, title, JOptionPane.YES_NO_CANCEL_OPTION);

       if (option == JOptionPane.YES_OPTION)
       {
           return fileSave();
       }
       else if(option == JOptionPane.CANCEL_OPTION)
		 {
		 	return false;
		 }

       return true;
   }   
   

	private void setUnsavedEdits(boolean b)
	{
		SQLPanelSelectionHandler.setUnsavedEdits(this, b);
	}

	private void clearSqlFile()
	{
		SQLPanelSelectionHandler.setSqlFile(this, null);
	}

	/**
	 * A class to listen for events that indicate that the content in the
	 * SQLEntryPanel has changed and could be lost.
	 */
	private class SQLEntryUndoListener implements UndoableEditListener
	{

		/*
		 * @see javax.swing.event.UndoableEditListener#undoableEditHappened(javax.swing.event.UndoableEditEvent)
		 */
		public void undoableEditHappened(UndoableEditEvent e)
		{
			IApplication app = getSession().getApplication();
			SquirrelPreferences prefs = app.getSquirrelPreferences();

			if (fileOpened || fileSaved)
			{
				if (prefs.getWarnForUnsavedFileEdits())
				{
					unsavedEdits = true;
				}
				setUnsavedEdits(true);
				ActionCollection actions =
						getSession().getApplication().getActionCollection();
				actions.enableAction(FileSaveAction.class, true);
			}
			else if (prefs.getWarnForUnsavedBufferEdits())
			{
				unsavedEdits = true;
			}
		}
	}

}
