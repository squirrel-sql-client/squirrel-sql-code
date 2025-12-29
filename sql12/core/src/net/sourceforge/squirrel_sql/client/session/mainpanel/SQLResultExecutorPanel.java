package net.sourceforge.squirrel_sql.client.session.mainpanel;
/*
 * Copyright (C) 2003-2004 Jason Height
 * jmheight@users.sourceforge.net
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.client.session.action.CloseAllSQLResultTabsAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseAllSQLResultTabsButCurrentAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseAllSQLResultTabsToLeftAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseAllSQLResultTabsToRightAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseCurrentSQLResultTabAction;
import net.sourceforge.squirrel_sql.client.session.action.ToggleCurrentSQLResultTabAnchoredAction;
import net.sourceforge.squirrel_sql.client.session.action.ToggleCurrentSQLResultTabStickyAction;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.custompanel.CustomResultPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabheader.ResultTabAdder;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabheader.ResultTabComponent;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetMetaDataDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.TableState;
import net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates.MarkDuplicatesChooserController;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.ButtonTabComponent;
import net.sourceforge.squirrel_sql.fw.resources.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This is the panel where SQL scripts are executed and results presented.
 *
 */
public class SQLResultExecutorPanel extends JPanel implements ISQLResultExecutor
{
	private static final ILogger s_log = LoggerController.createLogger(SQLResultExecutorPanel.class);

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLResultExecutorPanel.class);

   private ISession _session;

	/** Each tab is a <TT>ResultTab</TT> showing the results of a query. */
	private JTabbedPane _tabbedExecutionsPanel;
   private ResultTabAdder _tabAdder = new ResultTabAdder();

   private ArrayList<ResultFrame>_sqlResultFrames = new ArrayList<>();


	/** Listeners */
	private ArrayList<ISQLExecutionListener> _sqlExecutionListeners = new ArrayList<>();

   private ResultTabFactory _resultTabFactory;

   private IResultTab _stickyTab;

   private TabIconManager _tabIconManager = new TabIconManager();

   private final ResultTabClosing _resultTabClosing;

   public SQLResultExecutorPanel(ISession session)
	{
      this(session, false);
   }

   public SQLResultExecutorPanel(ISession session, boolean processSessionPropertyChanges)
	{
      _resultTabFactory = new ResultTabFactory(session, createSQLResultExecuterPanelFacade());
      _session = session;

      if (processSessionPropertyChanges)
      {
         _session.getProperties().addPropertyChangeListener(evt -> propertiesHaveChanged(evt.getPropertyName()));
      }

      createGUI();

      if (processSessionPropertyChanges)
      {
         propertiesHaveChanged(null);
      }

      _resultTabClosing = new ResultTabClosing(_tabIconManager, _tabbedExecutionsPanel);
   }

   private SQLResultExecuterPanelFacade createSQLResultExecuterPanelFacade()
   {
      return new SQLResultExecuterPanelFacade()
      {
         @Override
         public void closeResultTab(IResultTab resultTab)
         {
            _resultTabClosing.closeTab(resultTab.getCompleteResultTab());
         }

         @Override
         public void returnToTabbedPane(ResultTab resultTab)
         {
            SQLResultExecutorPanel.this.returnToTabbedPane(resultTab);
         }

         @Override
         public void createSQLResultFrame(IResultTab resultTab)
         {
            SQLResultExecutorPanel.this.createSQLResultFrame(resultTab);
         }

         @Override
         public void rerunSQL(String sql, IResultTab resultTabToReplace)
         {
            SQLResultExecutorPanel.this.rerunSQL(sql, resultTabToReplace);
         }

         @Override
         public void removeErrorPanel(ErrorPanel errorPanel)
         {
            SQLResultExecutorPanel.this.removeErrorPanel(errorPanel);
         }
      };
   }

   public String getTitle()
	{
        // i18n[SQLResultExecuterPanel.title=Results]
		return s_stringMgr.getString("SQLResultExecuterPanel.title");
	}

	public JComponent getComponent()
	{
		return this;
	}

   /** Current session. */
	public ISession getSession()
	{
		return _session;
	}

	/**
	 * Add a listener listening for SQL Execution.
	 *
	 * @param	lis	 Listener
	 *
	 * @throws	IllegalArgumentException
	 *			If a null <TT>ISQLExecutionListener</TT> passed.
	 */
	public void addSQLExecutionListener(ISQLExecutionListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("ISQLExecutionListener == null");
		}
		_sqlExecutionListeners.add(lis);
	}

	/**
	 * Remove an SQL execution listener.
	 *
	 * @param	lis	Listener
	 *
	 * @throws	IllegalArgumentException
	 *			If a null <TT>ISQLExecutionListener</TT> passed.
	 */
	public synchronized void removeSQLExecutionListener(ISQLExecutionListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("ISQLExecutionListener == null");
		}
		_sqlExecutionListeners.remove(lis);
	}


	public void execute(ISQLEntryPanel sqlPanel, ExecutionScope executionScope)
	{
      removeErrorPanels();

		String sql;

      if(ExecutionScope.EXEC_CURRENT_SQL == executionScope)
      {
         sql = sqlPanel.getSQLToBeExecuted();
      }
      else // if(ExecutionScope.EXEC_ALL_SQLS == executionScope)
      {
         sql = sqlPanel.getText();
      }


      if (sql != null && sql.length() > 0)
		{
			executeSQL(sql);
		}
		else
		{
          // i18n[SQLResultExecuterPanel.nosqlselected=No SQL selected for execution.]
          String msg = s_stringMgr.getString("SQLResultExecuterPanel.nosqlselected");
			_session.showErrorMessage(msg);
		}
	}

   private void removeErrorPanels()
   {
      ArrayList<ErrorPanel> toRemove = new ArrayList<>();

      for (int i = 0; i < _tabbedExecutionsPanel.getTabCount(); i++)
      {
         Component tab = _tabbedExecutionsPanel.getComponentAt(i);
         if(tab instanceof ErrorPanel)
         {
            toRemove.add((ErrorPanel) tab);
         }
      }

      for (ErrorPanel errorPanel : toRemove)
      {
         _resultTabClosing.closeTab(errorPanel);
      }
   }

   public void executeSQL(String sql)
	{
      executeSQL(sql, null);
   }

   public void executeSQL(String sql, String tableToBeEdited)
	{
      if (sql != null && sql.trim().length() > 0)
      {
         removeErrorPanels();

         String origSQL = sql;
         sql = fireSQLToBeExecutedEvent(sql);

         // This can happen if an impl of ISQLExecutionListener returns null
         // from the statementExecuting API method, to indicate that the SQL
         // shouldn't be executed.
         if (sql == null)
         {
            s_log.info(
                  "executeSQL: An ISQLExecutionListener veto'd execution of " +
                        "the following SQL: " + origSQL);
            return;
         }

         ISQLExecutionListener[] executionListeners = _sqlExecutionListeners.toArray(new ISQLExecutionListener[0]);

         new SQLExecutionHandler(null, _session, sql, createSQLExecutionHandlerListener(), executionListeners, tableToBeEdited);
      }
   }

   private ISQLExecutionHandlerListener createSQLExecutionHandlerListener()
   {
      return
         new ISQLExecutionHandlerListener()
         {
            @Override
            public void addResultsTab(SQLExecutionInfo info, ResultSetDataSet rsds, ResultSetMetaDataDataSet rsmdds, IDataSetUpdateableTableModel dataSetUpdateableTableModel, IResultTab resultTabToReplace)
            {
               SwingUtilities.invokeLater(() -> onAddResultsTab(info, rsds, rsmdds, dataSetUpdateableTableModel, resultTabToReplace));
            }

            @Override
            public void removeCancelPanel(CancelPanelCtrl cancelPanelCtrl, IResultTab resultTabToReplace)
            {
               SwingUtilities.invokeLater(() -> onRemoveCancelPanel(cancelPanelCtrl, resultTabToReplace));
            }

            @Override
            public void setCancelPanel(CancelPanelCtrl cancelPanelCtrl)
            {
               onSetCancelPanel(cancelPanelCtrl);
            }

            @Override
            public void displayErrors(ArrayList<String> sqlExecErrorMsgs, String lastExecutedStatement)
            {
               onDisplayErrors(sqlExecErrorMsgs, lastExecutedStatement);
            }
         };
   }

   private void onDisplayErrors(final ArrayList<String> sqlExecErrorMsgs, final String lastExecutedStatement)
   {
      Runnable runnable = () -> showErrorPanel(sqlExecErrorMsgs, lastExecutedStatement);
      SwingUtilities.invokeLater(runnable);
   }

   private void showErrorPanel(ArrayList<String> sqlExecErrorMsgs, String lastExecutedStatement)
   {
      ErrorPanel errorPanel = _resultTabFactory.createErrorPanel(sqlExecErrorMsgs, lastExecutedStatement);
      _tabAdder.add(s_stringMgr.getString("SQLResultExecuterPanel.ErrorTabHeader"), errorPanel);
      _tabbedExecutionsPanel.setSelectedComponent(errorPanel);
   }


   private void removeErrorPanel(ErrorPanel errorPanel)
   {
      _tabbedExecutionsPanel.remove(errorPanel);
   }

   private void rerunSQL(String sql, IResultTab resultTabToReplace)
   {
      new SQLExecutionHandler(resultTabToReplace, _session, sql, createSQLExecutionHandlerListener(), new ISQLExecutionListener[0]);
   }


   /**
	 * Close all the Results frames.
	 */
	public void closeAllSQLResultFrames()
	{
      for (ResultFrame sqlResultFrame : _sqlResultFrames)
      {
         sqlResultFrame.dispose();
      }
	}

	/**
	 * Close all the Results tabs.
	 */
	public void closeAllSQLResultTabs()
	{
      closeAllSQLResultTabs(false);
   }

	public void closeAllSQLResultTabs(boolean isMemoryCleanUp)
	{
      ArrayList<JComponent> allTabs = getAllTabs();

      _resultTabClosing.closeTabs(allTabs, isMemoryCleanUp);
   }

   private ArrayList<JComponent> getAllTabs()
   {
      ArrayList<JComponent> allTabs = new ArrayList<>();
      for (int i = 0; i < _tabbedExecutionsPanel.getTabCount(); i++)
      {
         JComponent component = (JComponent) _tabbedExecutionsPanel.getComponentAt(i);
         if (false == component instanceof CancelPanel)
         {
            allTabs.add(component);
         }
      }
      return allTabs;
   }

   public void closeAllButCurrentResultTabs()
   {
      Component selectedTab = _tabbedExecutionsPanel.getSelectedComponent();

      ArrayList<JComponent> allTabs = getAllTabs();

      ArrayList<JComponent> allButCurrent = new ArrayList<>(allTabs);
      allButCurrent.remove(selectedTab);


      _resultTabClosing.closeTabs(allButCurrent);
   }

   public boolean confirmClose()
   {
      return _resultTabClosing.confirmSqlPanelClose(getAllTabs());
   }


   public void closeAllToResultTabs(boolean left)
   {
      int selectedIndex = _tabbedExecutionsPanel.getSelectedIndex();

      ArrayList<JComponent> tabsToClose = new ArrayList<>();

      if(left)
      {
         for (int i = 0; i < selectedIndex; i++)
         {
            tabsToClose.add((JComponent) _tabbedExecutionsPanel.getComponentAt(i));
         }
      }
      else
      {
         for (int i = selectedIndex + 1; i < _tabbedExecutionsPanel.getTabCount(); i++)
         {
            tabsToClose.add((JComponent) _tabbedExecutionsPanel.getComponentAt(i));
         }
      }

      _resultTabClosing.closeTabs(tabsToClose);
   }

   public void toggleCurrentSQLResultTabSticky()
   {
      if (null != _stickyTab)
      {
         if(_stickyTab.equals(_tabbedExecutionsPanel.getSelectedComponent()))
         {
            // Sticky is turned off. Just remove sticky and return.
            _stickyTab = null;
            _tabAdder.setIconAt(_tabbedExecutionsPanel.getSelectedIndex(), null);
            return;

         }
         else
         {
            // remove old sticky tab
            int indexOfStickyTab = TabbedExcutionPanelUtil.getIndexOfTab(_stickyTab, _tabbedExecutionsPanel);
            if(-1 != indexOfStickyTab)
            {
               _tabAdder.setIconAt(indexOfStickyTab, null);
            }
            _stickyTab = null;
         }
      }

      if (false == _tabbedExecutionsPanel.getSelectedComponent() instanceof IResultTab)
      {
         if(null != _tabbedExecutionsPanel.getSelectedComponent())
         {
            String msg = s_stringMgr.getString("SQLResultExecuterPanel.nonStickyPanel");
            JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), msg);
         }
         return;
      }

      _stickyTab = (IResultTab) _tabbedExecutionsPanel.getSelectedComponent();
      int selectedIndex = _tabbedExecutionsPanel.getSelectedIndex();

      ImageIcon icon = _tabIconManager.getStickyIcon();

      _tabAdder.setIconAt(selectedIndex, icon);
   }

   public void toggleCurrentSQLResultTabAnchored()
   {
      if (null != _stickyTab && _stickyTab.equals(_tabbedExecutionsPanel.getSelectedComponent()))
      {
         _stickyTab = null;
      }

      int selectedIndex = _tabbedExecutionsPanel.getSelectedIndex();

      if(-1 == selectedIndex)
      {
         // Occurs when no tabs exist
         return;
      }

      if (_resultTabClosing.isAnchoredAt(selectedIndex))
      {
         _tabAdder.setIconAt(selectedIndex, null);
      }
      else
      {
         _tabAdder.setIconAt(selectedIndex, _tabIconManager.getAnchorIcon());
      }
   }



   public void closeCurrentResultTab()
   {
      JComponent selectedTab = (JComponent) _tabbedExecutionsPanel.getSelectedComponent();
      _resultTabClosing.closeTab(selectedTab);
   }

   /**
	 * Display the next tab in the SQL results.
	 */
	public void gotoNextResultsTab()
	{
		final int tabCount = _tabbedExecutionsPanel.getTabCount();
		if (tabCount > 1)
		{
			int nextTabIdx = _tabbedExecutionsPanel.getSelectedIndex() + 1;
			if (nextTabIdx >= tabCount)
			{
				nextTabIdx = 0;
			}
			_tabbedExecutionsPanel.setSelectedIndex(nextTabIdx);
		}
	}

	/**
	 * Display the previous tab in the SQL results.
	 */
	public void gotoPreviousResultsTab()
	{
		final int tabCount = _tabbedExecutionsPanel.getTabCount();
		if (tabCount > 1)
		{
			int prevTabIdx = _tabbedExecutionsPanel.getSelectedIndex() - 1;
			if (prevTabIdx < 0)
			{
				prevTabIdx = tabCount - 1;
			}
			_tabbedExecutionsPanel.setSelectedIndex(prevTabIdx);
		}
	}


   protected String fireSQLToBeExecutedEvent(String sql)
	{
		// Guaranteed to be non-null.
      ISQLExecutionListener[] listeners = _sqlExecutionListeners.toArray(new ISQLExecutionListener[0]);

      // Process the listeners last to first, notifying
      // those that are interested in this event.
      for (int i = listeners.length - 1; i >= 0; i -= 1)
      {
         sql = listeners[i].statementExecuting(sql);
         if (sql == null)
         {
            break;
         }
      }
      return sql;
	}

	/**
	 * Create an internal frame for the specified tab and
	 * display the tab in the internal frame after removing
	 * it from the tabbed pane.
	 *
	 * @param	tab	<TT>ResultTab</TT> to be displayed in
	 *				an internal frame.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ResultTab</TT> passed.
	 */
	private void createSQLResultFrame(IResultTab tab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null ResultTab passed");
		}

		_tabbedExecutionsPanel.remove(tab.getCompleteResultTab());


      ResultFrameListener resultFrameListener = new ResultFrameListener()
      {
         @Override
         public void frameReplaced(ResultFrame oldFrame, ResultFrame newFrame)
         {
            onFrameReplaced(oldFrame, newFrame);
         }
      };


      ResultFrame frame = new ResultFrame(_session, tab, _resultTabFactory, resultFrameListener,true, false);
      _sqlResultFrames.add(frame);
   }

   private void onFrameReplaced(ResultFrame oldFrame, ResultFrame newFrame)
   {
      _sqlResultFrames.remove(oldFrame);
      _sqlResultFrames.add(newFrame);
   }

	/**
	 * Return the passed tab back into the tabbed pane.
	 *
	 * @param	tab	<TT>Resulttab</TT> to be returned
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ResultTab</TT> passed.
	 */
	private void returnToTabbedPane(ResultTab tab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null ResultTab passed");
		}

      MarkDuplicatesChooserController resultFramesMarkDuplicatesChooserController = null;
      for (ResultFrame sqlResultFrame : _sqlResultFrames)
      {
         if(tab == sqlResultFrame.getResultTab())
         {
            resultFramesMarkDuplicatesChooserController = sqlResultFrame.getMarkDuplicatesChooserController();
            _sqlResultFrames.remove(sqlResultFrame);
            break;
         }
      }


      addResultsTab(tab, null);

      tab.wasReturnedToTabbedPane(resultFramesMarkDuplicatesChooserController);

      _tabbedExecutionsPanel.setSelectedComponent(tab);


   }

    /**
     * @see ISQLResultExecutor#getSelectedResultTab()
     */
    public IResultTab getSelectedResultTab()
    {
        return (IResultTab)_tabbedExecutionsPanel.getSelectedComponent();
    }

   @Override
   public void selectResultTab(IResultTab resultTab)
   {
      _tabbedExecutionsPanel.setSelectedComponent(resultTab.getCompleteResultTab());
   }

   @Override
   public void addCustomResult(CustomResultPanel resultPanel, String title, Icon icon)
   {
      _tabAdder.add(title, resultPanel);
      _tabbedExecutionsPanel.setSelectedComponent(resultPanel);
      int selectedIndex = _tabbedExecutionsPanel.getSelectedIndex();

      ButtonTabComponent tabComponent = new ButtonTabComponent(title, icon);
      tabComponent.getClosebutton().addActionListener(e -> onCustomTabClose(resultPanel));
      tabComponent.getToWindowButton().setVisible(false);
      _tabbedExecutionsPanel.setTabComponentAt(selectedIndex, tabComponent);

      checkResultTabLimit();
   }

   @Override
   public List<IResultTab> getAllSqlResultTabs()
   {
      return getAllTabs().stream().filter(t -> t instanceof IResultTab).map(t -> (IResultTab)t).collect(Collectors.toList());
   }

   private void onCustomTabClose(CustomResultPanel customResultPanel)
   {
      _resultTabClosing.closeTab(customResultPanel);
   }

   private void onAddResultsTab(SQLExecutionInfo exInfo, ResultSetDataSet rsds, ResultSetMetaDataDataSet mdds, IDataSetUpdateableTableModel dataSetUpdateableTableModel, IResultTab resultTabToReplace)
    {
       try
       {
          ResultTab tab = _resultTabFactory.createResultTab(exInfo, dataSetUpdateableTableModel, rsds, mdds);
          addResultsTab(tab, resultTabToReplace);
          _tabbedExecutionsPanel.setSelectedComponent(tab);
       }
       catch (Throwable t)
       {
          _session.showErrorMessage(t);
       }
    }

   private void onRemoveCancelPanel(final CancelPanelCtrl cancelPanelCtrl, final IResultTab resultTabToReplace)
   {
         _tabbedExecutionsPanel.remove(cancelPanelCtrl.getPanel());

         int indexToSelect = -1;
         if (null == resultTabToReplace)
         {
            indexToSelect = TabbedExcutionPanelUtil.getIndexOfTab(_stickyTab, SQLResultExecutorPanel.this._tabbedExecutionsPanel);
         }
         else
         {
            indexToSelect = TabbedExcutionPanelUtil.getIndexOfTab(resultTabToReplace, SQLResultExecutorPanel.this._tabbedExecutionsPanel);
         }

         if (-1 != indexToSelect)
         {
            _tabbedExecutionsPanel.setSelectedIndex(indexToSelect);
         }

         cancelPanelCtrl.wasRemoved();
   }

   private void onSetCancelPanel(final CancelPanelCtrl cancelPanelCtrl)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _tabAdder.add(s_stringMgr.getString("SQLResultExecuterPanel.exec"),
                    null,
                    cancelPanelCtrl.getPanel(),
                    s_stringMgr.getString("SQLResultExecuterPanel.cancelMsg"));

            _tabbedExecutionsPanel.setSelectedComponent(cancelPanelCtrl.getPanel());
         }
      });
   }



	private void addResultsTab(ResultTab tab, IResultTab resultTabToReplace)
	{

      if(null == resultTabToReplace && null == _stickyTab)
      {
         _tabAdder.add(getTabHeaderTitle(tab), null, tab, tab.getViewableSqlString());

         checkResultTabLimit();
         return;
      }

      if (null != resultTabToReplace && _session.getProperties().getKeepTableLayoutOnRerun())
      {
         TableState sortableTableState = resultTabToReplace.getResultSortableTableState();
         if (null != sortableTableState)
         {
            tab.applyResultSortableTableState(sortableTableState);
         }
      }


      int indexToReplace = -1;
      ImageIcon tabIcon = null;

      boolean replaceStickyTab = null != _stickyTab && (null == resultTabToReplace || _stickyTab == resultTabToReplace);

      if(replaceStickyTab)
      {
         // Result goes to _stickyTab
         indexToReplace = TabbedExcutionPanelUtil.getIndexOfTab(_stickyTab, _tabbedExecutionsPanel);
         if(-1 == indexToReplace)
         {
            // sticky tab was closed
            _stickyTab = null;
         }
         else
         {
            tabIcon = _tabIconManager.getStickyIcon();
            _stickyTab = tab;
         }
      }
      else if (null != resultTabToReplace)
      {
         indexToReplace = TabbedExcutionPanelUtil.getIndexOfTab(resultTabToReplace, _tabbedExecutionsPanel);

         if(_resultTabClosing.isAnchored((JComponent) resultTabToReplace))
         {
            tabIcon = _tabIconManager.getAnchorIcon();
         }
      }

      if(-1 == indexToReplace)
      {
         // Just add the tab
         addResultsTab(tab, null);
      }
      else
      {
         // We need to make sure this is fired before the ResultTabCloseListener of resultTabToReplace is fired.
         resultTabToReplace.aboutToBeReplacedBy(tab);

         _resultTabClosing.closeTabAt(indexToReplace);
         _tabAdder.insert(getTabHeaderTitle(tab), tabIcon, tab, tab.getViewableSqlString(), indexToReplace);
      }
	}

   private String getTabHeaderTitle(ResultTab tab)
   {
      int maxChars = Main.getApplication().getSquirrelPreferences().getResultTabHeaderMaxCharsInTab();
      return StringUtilities.shortenEnd(tab.getViewableSqlString(), maxChars, null);
   }

   private void checkResultTabLimit()
   {
      SessionProperties props = _session.getProperties();

      int indexToRemove = 0;
      while(props.getLimitSQLResultTabs() && props.getSqlResultTabLimit() < _tabbedExecutionsPanel.getTabCount())
      {
         if(_tabbedExecutionsPanel.getComponentAt(indexToRemove) instanceof CancelPanel)
         {
            break;
         }

         if(_resultTabClosing.isAnchoredAt(indexToRemove))
         {
            ++indexToRemove;
         }
         else
         {
            _resultTabClosing.closeTabAt(indexToRemove);
         }

         if(indexToRemove == _tabbedExecutionsPanel.getTabCount() -1)
         {
            break;
         }
      }
   }


   private void propertiesHaveChanged(String propName)
	{
		final SessionProperties props = _session.getProperties();

      if (propName == null || propName.equals(SessionProperties.IPropertyNames.AUTO_COMMIT))
      {
         SetSessionAutoCommitTask task = new SetSessionAutoCommitTask(_session);
         if (SwingUtilities.isEventDispatchThread())
         {
            _session.getApplication().getThreadPool().addTask(task);
         }
         else
         {
            task.run();
         }
      }

		if (propName == null || propName.equals(SessionProperties.IPropertyNames.SQL_EXECUTION_TAB_PLACEMENT))
		{
			_tabbedExecutionsPanel.setTabPlacement(props.getSQLExecutionTabPlacement());
		}
	}

   private void createGUI()
	{
      final SessionProperties props = _session.getProperties();
		_tabbedExecutionsPanel = UIFactory.getInstance().createTabbedPane(props.getSQLExecutionTabPlacement(), true);
      _tabAdder.setTabbedPane(_tabbedExecutionsPanel);

      initTabPopup();

      GUIUtils.listenToMouseWheelClickOnTab(_tabbedExecutionsPanel, (tabIndex, tabComponent) -> doCloseOnMiddleMouseClick(tabIndex));

      setLayout(new BorderLayout());

		add(_tabbedExecutionsPanel, BorderLayout.CENTER);
	}

   private void doCloseOnMiddleMouseClick(int tabIndex)
   {
      Component comp = _tabbedExecutionsPanel.getComponentAt(tabIndex);
      if(comp instanceof IResultTab || comp instanceof ErrorPanel || comp instanceof CustomResultPanel)
      {
         _resultTabClosing.closeTab((JComponent) comp);
      }
   }


   private void initTabPopup()
   {
      final JPopupMenu popup = new JPopupMenu();

      // i18n[SQLResultExecuterPanel.close=Close]
      String closeLabel = s_stringMgr.getString("SQLResultExecuterPanel.close");
      JMenuItem mnuClose = new JMenuItem(closeLabel);
      initAccelerator(CloseCurrentSQLResultTabAction.class, mnuClose);
      mnuClose.addActionListener(e -> closeCurrentResultTab());
      popup.add(mnuClose);

      // i18n[SQLResultExecuterPanel.closeAllButThis=Close all but this]
      String cabtLabel = s_stringMgr.getString("SQLResultExecuterPanel.closeAllButThis");
      JMenuItem mnuCloseAllButThis = new JMenuItem(cabtLabel);
      initAccelerator(CloseAllSQLResultTabsButCurrentAction.class, mnuCloseAllButThis);
      mnuCloseAllButThis.addActionListener(e -> closeAllButCurrentResultTabs());
      popup.add(mnuCloseAllButThis);

      // i18n[SQLResultExecuterPanel.closeAll=Close all]
      String caLabel = s_stringMgr.getString("SQLResultExecuterPanel.closeAll");
      JMenuItem mnuCloseAll = new JMenuItem(caLabel);
      initAccelerator(CloseAllSQLResultTabsAction.class, mnuCloseAll);
      mnuCloseAll.addActionListener(e -> closeAllSQLResultTabs());
      popup.add(mnuCloseAll);

      String closeAlltoLeftLabel = s_stringMgr.getString("SQLResultExecuterPanel.closeAllToLeft");
      JMenuItem mnuCloseAllToLeft = new JMenuItem(closeAlltoLeftLabel);
      initAccelerator(CloseAllSQLResultTabsToLeftAction.class, mnuCloseAllToLeft);
      mnuCloseAllToLeft.addActionListener(e -> closeAllToResultTabs(true));
      popup.add(mnuCloseAllToLeft);

      String closeAlltoRightLabel = s_stringMgr.getString("SQLResultExecuterPanel.closeAllToRight");
      JMenuItem mnuCloseAllToRight = new JMenuItem(closeAlltoRightLabel);
      initAccelerator(CloseAllSQLResultTabsToRightAction.class, mnuCloseAllToLeft);
      mnuCloseAllToRight.addActionListener(e -> closeAllToResultTabs(false));
      popup.add(mnuCloseAllToRight);


      String tsLabel = s_stringMgr.getString("SQLResultExecuterPanel.toggleSticky");
      JMenuItem mnuToggleSticky = new JMenuItem(tsLabel);
      initAccelerator(ToggleCurrentSQLResultTabStickyAction.class, mnuToggleSticky);
      mnuToggleSticky.addActionListener(e -> toggleCurrentSQLResultTabSticky());
      mnuToggleSticky.setToolTipText(s_stringMgr.getString("SQLResultExecuterPanel.toggleSticky.tooltip"));
      popup.add(mnuToggleSticky);

      String taLabel = s_stringMgr.getString("SQLResultExecuterPanel.toggleAnchored");
      JMenuItem mnuToggleAnchored = new JMenuItem(taLabel);
      initAccelerator(ToggleCurrentSQLResultTabAnchoredAction.class, mnuToggleAnchored);
      mnuToggleAnchored.addActionListener(e -> toggleCurrentSQLResultTabAnchored());
      mnuToggleAnchored.setToolTipText(s_stringMgr.getString("SQLResultExecuterPanel.toggleAnchored.tooltip"));
      popup.add(mnuToggleAnchored);

      GUIUtils.listenToRightMouseClickOnTabComponent(_tabbedExecutionsPanel, (tabIndex, tabComponent, clickPosX, clickPosY) -> popup.show(tabComponent, clickPosX, clickPosY));
   }

   private void initAccelerator(Class<? extends Action> actionClass, JMenuItem mnuItem)
   {
      Action action = _session.getApplication().getActionCollection().get(actionClass);

      String accel = (String) action.getValue(Resources.ACCELERATOR_STRING);
      Main.getApplication().getShortcutManager().setAccelerator(mnuItem, KeyStroke.getKeyStroke(accel), action);
   }

   public JTabbedPane getTabbedPane()
   {
      return _tabbedExecutionsPanel;
   }

   public ResultTabComponent getResultTabComponentAt(int tabIx)
   {
      return _tabAdder.getResultTabComponentAt(tabIx);
   }
}
