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

import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.client.session.action.CloseAllSQLResultTabsAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseAllSQLResultTabsButCurrentAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseCurrentSQLResultTabAction;
import net.sourceforge.squirrel_sql.client.session.action.ToggleCurrentSQLResultTabStickyAction;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.datasetviewer.*;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * This is the panel where SQL scripts are executed and results presented.
 *
 */
public class SQLResultExecuterPanel extends JPanel
									implements ISQLResultExecuter
{
	/** Logger for this class. */
	private static final ILogger s_log = 
        LoggerController.createLogger(SQLResultExecuterPanel.class);

    /** Internationalized strings for this class. */
   private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(SQLResultExecuterPanel.class);

   static interface i18n {
        // i18n[SQLResultExecuterPanel.exec=Executing SQL]
        String EXEC_SQL_MSG = 
            s_stringMgr.getString("SQLResultExecuterPanel.exec");
        // i18n[SQLResultExecuterPanel.cancelMsg=Press Cancel to Stop]
        String CANCEL_SQL_MSG = 
            s_stringMgr.getString("SQLResultExecuterPanel.cancelMsg");
        
    }
    
	private ISession _session;

	private MyPropertiesListener _propsListener;

	/** Each tab is a <TT>ResultTab</TT> showing the results of a query. */
	private JTabbedPane _tabbedExecutionsPanel;

   private ArrayList<ResultFrame>_sqlResultFrames = new ArrayList<ResultFrame>();


	/** Listeners */
	private EventListenerList _listeners = new EventListenerList();

   private ResultTabFactory _resultTabFactory;

   private IResultTab _stickyTab;

   /**
	 * Ctor.
	 *
	 * @param	session	 Current session.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public SQLResultExecuterPanel(ISession session)
	{
      _resultTabFactory = new ResultTabFactory(session, createSQLResultExecuterPanelFacade());
		setSession(session);
		createGUI();
		propertiesHaveChanged(null);
	}

   private SQLResultExecuterPanelFacade createSQLResultExecuterPanelFacade()
   {
      return new SQLResultExecuterPanelFacade()
      {
         @Override
         public void closeResultTab(ResultTab resultTab)
         {
            SQLResultExecuterPanel.this.closeResultTab(resultTab);
         }

         @Override
         public void returnToTabbedPane(ResultTab resultTab)
         {
            SQLResultExecuterPanel.this.returnToTabbedPane(resultTab);
         }

         @Override
         public void createSQLResultFrame(ResultTab resultTab)
         {
            SQLResultExecuterPanel.this.createSQLResultFrame(resultTab);
         }

         @Override
         public void rerunSQL(String sql, IResultTab resultTab)
         {
            SQLResultExecuterPanel.this.rerunSQL(sql, resultTab);
         }

         @Override
         public void removeErrorPanel(ErrorPanel errorPanel)
         {
            SQLResultExecuterPanel.this.removeErrorPanel(errorPanel);
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

	/**
	 * Set the current session.
	 *
	 * @param	session	 Current session.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public synchronized void setSession(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		sessionClosing();
		_session = session;
		_propsListener = new MyPropertiesListener();
		_session.getProperties().addPropertyChangeListener(_propsListener);
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
	public synchronized void addSQLExecutionListener(ISQLExecutionListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("ISQLExecutionListener == null");
		}
		_listeners.add(ISQLExecutionListener.class, lis);
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
		_listeners.remove(ISQLExecutionListener.class, lis);
	}


	public void execute(ISQLEntryPanel sqlPanel)
	{
      removeErrorPanels();

		String sql = sqlPanel.getSQLToBeExecuted();
		if (sql != null && sql.length() > 0)
		{
			executeSQL(sql);
		}
		else
		{
            // i18n[SQLResultExecuterPanel.nosqlselected=No SQL selected for execution.]
            String msg = 
                s_stringMgr.getString("SQLResultExecuterPanel.nosqlselected");
			_session.showErrorMessage(msg);
		}
	}

   private void removeErrorPanels()
   {
      ArrayList<ErrorPanel> toRemove = new ArrayList<ErrorPanel>();

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
         closeTab(errorPanel);
      }
   }

   public void executeSQL(String sql)
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

         ISQLExecutionListener[] executionListeners =
               _listeners.getListeners(ISQLExecutionListener.class);

         ISQLExecutionHandlerListener executionHandlerListener = createSQLExecutionHandlerListener();

         new SQLExecutionHandler((IResultTab)null, _session, sql, executionHandlerListener, executionListeners);
      }
   }

   private ISQLExecutionHandlerListener createSQLExecutionHandlerListener()
   {
      return
         new ISQLExecutionHandlerListener()
         {
            @Override
            public void addResultsTab(SQLExecutionInfo info, ResultSetDataSet rsds, ResultSetMetaDataDataSet rsmdds, IDataSetUpdateableTableModel model, IResultTab resultTabToReplace)
            {
               onAddResultsTab(info, rsds, rsmdds, model, resultTabToReplace);
            }

            @Override
            public void removeCancelPanel(CancelPanelCtrl cancelPanelCtrl, IResultTab resultTabToReplace)
            {
               onRemoveCancelPanel(cancelPanelCtrl, resultTabToReplace);
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
      Runnable runnable = new Runnable()
      {
         public void run()
         {
            showErrorPanel(sqlExecErrorMsgs, lastExecutedStatement);
         }
      };

      SwingUtilities.invokeLater(runnable);
   }

   private void showErrorPanel(ArrayList<String> sqlExecErrorMsgs, String lastExecutedStatement)
   {
      ErrorPanel errorPanel = _resultTabFactory.createErrorPanel(sqlExecErrorMsgs, lastExecutedStatement);
      _tabbedExecutionsPanel.add(s_stringMgr.getString("SQLResultExecuterPanel.ErrorTabHeader"), errorPanel);
      _tabbedExecutionsPanel.setSelectedComponent(errorPanel);
   }


   private void removeErrorPanel(ErrorPanel errorPanel)
   {
      _tabbedExecutionsPanel.remove(errorPanel);
   }

   private void rerunSQL(String sql, IResultTab resultTab)
   {
      new SQLExecutionHandler(resultTab, _session, sql, createSQLExecutionHandlerListener(), new ISQLExecutionListener[0]);
   }


   /**
	 * Close all the Results frames.
	 */
	public synchronized void closeAllSQLResultFrames()
	{
      for (ResultFrame sqlResultFrame : _sqlResultFrames)
      {
         sqlResultFrame.dispose();
      }
	}

	/**
	 * Close all the Results tabs.
	 */
	public synchronized void closeAllSQLResultTabs()
	{
      ArrayList<Component> allTabs = getAllTabs();

      for (Component tab : allTabs)
      {
         closeTab(tab);
      }

	}

   private ArrayList<Component> getAllTabs()
   {
      ArrayList<Component> allTabs = new ArrayList<Component>();
      for (int i = 0; i < _tabbedExecutionsPanel.getTabCount(); i++)
      {
         Component component = _tabbedExecutionsPanel.getComponentAt(i);
         if (false == component instanceof CancelPanel)
         {
            allTabs.add(component);
         }
      }
      return allTabs;
   }

   public synchronized void closeAllButCurrentResultTabs()
   {
      Component selectedTab = _tabbedExecutionsPanel.getSelectedComponent();

      ArrayList<Component> allTabs = getAllTabs();


      for (Component tab : allTabs)
      {
         if(tab != selectedTab)
         {
            closeTab(tab);
         }

      }
   }

   public synchronized void toggleCurrentSQLResultTabSticky()
   {
      if (null != _stickyTab)
      {
         if(_stickyTab.equals(_tabbedExecutionsPanel.getSelectedComponent()))
         {
            // Sticky is turned off. Just remove sticky and return.
            _stickyTab = null;
            _tabbedExecutionsPanel.setIconAt(_tabbedExecutionsPanel.getSelectedIndex(), null);
            return;

         }
         else
         {
            // remove old sticky tab
            int indexOfStickyTab = getIndexOfTab(_stickyTab);
            if(-1 != indexOfStickyTab)
            {
               _tabbedExecutionsPanel.setIconAt(indexOfStickyTab, null);
            }
            _stickyTab = null;
         }
      }

      if(false == _tabbedExecutionsPanel.getSelectedComponent() instanceof IResultTab)
      {
          //i18n[SQLResultExecuterPanel.nonStickyPanel=Cannot make a cancel panel sticky]
          String msg = 
              s_stringMgr.getString("SQLResultExecuterPanel.nonStickyPanel");
         JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), 
                                       msg);
         return;
      }

      _stickyTab = (IResultTab) _tabbedExecutionsPanel.getSelectedComponent();
      int selectedIndex = _tabbedExecutionsPanel.getSelectedIndex();

      ImageIcon icon = getStickyIcon();

      _tabbedExecutionsPanel.setIconAt(selectedIndex, icon);
   }

   private ImageIcon getStickyIcon()
   {
      ActionCollection actionCollection = _session.getApplication().getActionCollection();

      ImageIcon icon =
         (ImageIcon) actionCollection.get(ToggleCurrentSQLResultTabStickyAction.class).getValue(Action.SMALL_ICON);
      return icon;
   }

   private int getIndexOfTab(IResultTab resultTab)
   {
      return getIndexOfTab((JComponent)resultTab);
   }


   private int getIndexOfTab(JComponent tab)
   {
      if(null == tab)
      {
         return -1;
      }

      for (int i = 0; i < _tabbedExecutionsPanel.getTabCount(); i++)
      {
         if (tab == _tabbedExecutionsPanel.getComponentAt(i))
         {
            return i;
         }
      }
      return -1;
   }



   public synchronized void closeCurrentResultTab()
   {
      Component selectedTab = _tabbedExecutionsPanel.getSelectedComponent();
      closeTab(selectedTab);
   }

   /**
	 * Sesssion is ending.
	 * Remove all listeners that this component has setup. Close all
	 * torn off result tab windows.
	 */
	void sessionClosing()
	{
		if (_propsListener != null)
		{
			_session.getProperties().removePropertyChangeListener(
					_propsListener);
			_propsListener = null;
		}

		closeAllSQLResultFrames();
	}

   private void closeTab(Component tab)
   {
      if (tab instanceof ErrorPanel)
      {
         _tabbedExecutionsPanel.remove(tab);
      }
      else if (tab instanceof ResultTab)
      {
         closeResultTab((ResultTab) tab);
      }
      else if (tab instanceof CancelPanel)
      {
         ((CancelPanel)tab).closeBtn.doClick();
      }
   }


   /**
	 * Close the passed <TT>ResultTab</TT>. This is done by clearing
	 * all data from the tab, removing it from the tabbed panel
	 * and adding it to the list of available tabs.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ResultTab</TT> passed.
	 */
	private void closeResultTab(ResultTab tab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null ResultTab passed");
		}
		s_log
				.debug("SQLPanel.closeResultTab(" + tab.getIdentifier().toString()
                  + ")");
		tab.clear();
		_tabbedExecutionsPanel.remove(tab);
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
		Object[] listeners = _listeners.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event.
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == ISQLExecutionListener.class)
			{
				sql = ((ISQLExecutionListener)listeners[i + 1]).statementExecuting(sql);
				if (sql == null)
				{
					break;
				}
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
	private void createSQLResultFrame(ResultTab tab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null ResultTab passed");
		}
		s_log.debug("SQLPanel.createSQLResultFrame(" + tab.getIdentifier().toString()
				+ ")");
		_tabbedExecutionsPanel.remove(tab);


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

		s_log.debug("SQLPanel.returnToTabbedPane("
				+ tab.getIdentifier().toString() + ")");

      for (ResultFrame sqlResultFrame : _sqlResultFrames)
      {
         if(tab == sqlResultFrame.getTab())
         {
            _sqlResultFrames.remove(sqlResultFrame);
            break;
         }
      }


      addResultsTab(tab, null);
	}

    /**
     * @see net.sourceforge.squirrel_sql.client.session.mainpanel.ISQLResultExecuter#getSelectedResultTab()
     */
    public IResultTab getSelectedResultTab() {
        return (IResultTab)_tabbedExecutionsPanel.getSelectedComponent();
    }

    private void onAddResultsTab(final SQLExecutionInfo exInfo,
    		final ResultSetDataSet rsds,
    		final ResultSetMetaDataDataSet mdds,
    		final IDataSetUpdateableTableModel creator,
    		final IResultTab resultTabToReplace)
    {
    		SwingUtilities.invokeLater(new Runnable()
    		{
             public void run()
             {
                try
                {
                   ResultTab tab = _resultTabFactory.createResultTab(exInfo, creator, rsds, mdds);
                   addResultsTab(tab, resultTabToReplace);
                   _tabbedExecutionsPanel.setSelectedComponent(tab);
                }
                catch (Throwable t)
                {
                   _session.showErrorMessage(t);
                }
             }
          });
    }

   private void onRemoveCancelPanel(final CancelPanelCtrl cancelPanelCtrl, final IResultTab resultTabToReplace)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _tabbedExecutionsPanel.remove(cancelPanelCtrl.getPanel());

            int indexToSelect = -1;
            if (null == resultTabToReplace)
            {
               indexToSelect = getIndexOfTab(_stickyTab);
            }
            else
            {
               indexToSelect = getIndexOfTab(resultTabToReplace);
            }

            if (-1 != indexToSelect)
            {
               _tabbedExecutionsPanel.setSelectedIndex(indexToSelect);
            }

         }
      });
   }

   private void onSetCancelPanel(final CancelPanelCtrl cancelPanelCtrl)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _tabbedExecutionsPanel.addTab(SQLResultExecuterPanel.i18n.EXEC_SQL_MSG,
                  null,
                  cancelPanelCtrl.getPanel(),
                  SQLResultExecuterPanel.i18n.CANCEL_SQL_MSG);
            _tabbedExecutionsPanel.setSelectedComponent(cancelPanelCtrl.getPanel());
         }
      });
   }



	private void addResultsTab(ResultTab tab, IResultTab resultTabToReplace)
	{
      if(null == resultTabToReplace && null == _stickyTab)
      {
   		_tabbedExecutionsPanel.addTab(tab.getTitle(), null, tab, tab.getViewableSqlString());
         checkResultTabLimit();
      }
      else
      {
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

         // Either resultTabToReplace or _stickyTab must be not null here
         if(null != resultTabToReplace && _stickyTab != resultTabToReplace)
         {
            indexToReplace = getIndexOfTab(resultTabToReplace);
         }
         else
         {
            indexToReplace = getIndexOfTab(_stickyTab);
            if(-1 == indexToReplace)
            {
               // sticky tab was closed
               _stickyTab = null;
            }
            else
            {
               tabIcon = getStickyIcon();
               _stickyTab = tab;
            }
         }


         if(-1 == indexToReplace)
         {
            // Just add the tab
            addResultsTab(tab, null);
            return;
         }

         closeTabAt(indexToReplace);
         _tabbedExecutionsPanel.insertTab(tab.getTitle(), tabIcon, tab, tab.getViewableSqlString(), indexToReplace);
      }
	}

   private void checkResultTabLimit()
   {
      SessionProperties props = _session.getProperties();

      while(props.getLimitSQLResultTabs() && props.getSqlResultTabLimit() < _tabbedExecutionsPanel.getTabCount())
      {
         closeTabAt(0);
      }
   }


   private void closeTabAt(int index)
   {
      Component selectedTab = _tabbedExecutionsPanel.getComponentAt(index);
      closeTab(selectedTab);
   }


   private void propertiesHaveChanged(String propName)
	{
		final SessionProperties props = _session.getProperties();

		if (propName == null
		        || propName.equals(SessionProperties.IPropertyNames.AUTO_COMMIT))
		{
            SetAutoCommitTask task = new SetAutoCommitTask();
		    if (SwingUtilities.isEventDispatchThread()) {
                _session.getApplication().getThreadPool().addTask(task);
            } else {
                task.run();
            }
        }

		if (propName == null
				|| propName
						.equals(SessionProperties.IPropertyNames.SQL_EXECUTION_TAB_PLACEMENT))
		{
			_tabbedExecutionsPanel.setTabPlacement(props.getSQLExecutionTabPlacement());
		}
	}

    private class SetAutoCommitTask implements Runnable {
                
        public void run() {
            final ISQLConnection conn = _session.getSQLConnection();
            final SessionProperties props = _session.getProperties();
            if (conn != null)
            {
                boolean auto = true;
                try
                {
                    auto = conn.getAutoCommit();
                }
                catch (SQLException ex)
                {
                    s_log.error("Error with transaction control", ex);
                    _session.showErrorMessage(ex);
                }
                try
                {
                    conn.setAutoCommit(props.getAutoCommit());
                }
                catch (SQLException ex)
                {
                    props.setAutoCommit(auto);
                    _session.showErrorMessage(ex);
                }
            }        
        }
    }
   
	private void createGUI()
	{
      final SessionProperties props = _session.getProperties();
		_tabbedExecutionsPanel = UIFactory.getInstance().createTabbedPane(props.getSQLExecutionTabPlacement(), true);


      createTabPopup();


      setLayout(new BorderLayout());

		add(_tabbedExecutionsPanel, BorderLayout.CENTER);
	}


   /**
    * Due to JDK 1.4 Bug 4465870 this doesn't work with JDK 1.4. when scrollable tabbed pane is used.
    */
   private void createTabPopup()
   {
      final JPopupMenu popup = new JPopupMenu();

      // i18n[SQLResultExecuterPanel.close=Close]
      String closeLabel = s_stringMgr.getString("SQLResultExecuterPanel.close");
      JMenuItem mnuClose = new JMenuItem(closeLabel);
      initAccelerator(CloseCurrentSQLResultTabAction.class, mnuClose);
      mnuClose.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            closeCurrentResultTab();
         }
      });
      popup.add(mnuClose);

      // i18n[SQLResultExecuterPanel.closeAllButThis=Close all but this]
      String cabtLabel = 
          s_stringMgr.getString("SQLResultExecuterPanel.closeAllButThis");
      JMenuItem mnuCloseAllButThis = new JMenuItem(cabtLabel);
      initAccelerator(CloseAllSQLResultTabsButCurrentAction.class, mnuCloseAllButThis);
      mnuCloseAllButThis.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            closeAllButCurrentResultTabs();
         }
      });
      popup.add(mnuCloseAllButThis);

      // i18n[SQLResultExecuterPanel.closeAll=Close all]
      String caLabel = s_stringMgr.getString("SQLResultExecuterPanel.closeAll");
      JMenuItem mnuCloseAll = new JMenuItem(caLabel);
      initAccelerator(CloseAllSQLResultTabsAction.class, mnuCloseAll);
      mnuCloseAll.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            closeAllSQLResultTabs();
         }
      });
      popup.add(mnuCloseAll);

      // i18n[SQLResultExecuterPanel.toggleSticky=Toggle sticky]
      String tsLabel = 
          s_stringMgr.getString("SQLResultExecuterPanel.toggleSticky");
      JMenuItem mnuToggleSticky = new JMenuItem(tsLabel);
      initAccelerator(ToggleCurrentSQLResultTabStickyAction.class, mnuToggleSticky);
      mnuToggleSticky.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            toggleCurrentSQLResultTabSticky();
         }
      });
      popup.add(mnuToggleSticky);

      _tabbedExecutionsPanel.addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent e)
         {
            maybeShowPopup(e, popup);
         }

         public void mouseReleased(MouseEvent e)
         {
            maybeShowPopup(e, popup);
         }
      });
   }

   private void initAccelerator(Class<? extends Action> actionClass, JMenuItem mnuItem)
   {
      Action action = _session.getApplication().getActionCollection().get(actionClass);

      String accel = (String) action.getValue(Resources.ACCELERATOR_STRING);
      if(   null != accel
         && 0 != accel.trim().length())
      {
         mnuItem.setAccelerator(KeyStroke.getKeyStroke(accel));
      }
   }

   private void maybeShowPopup(MouseEvent e, JPopupMenu popup)
   {
      if (e.isPopupTrigger())
      {
         int tab = _tabbedExecutionsPanel.getUI().tabForCoordinate(_tabbedExecutionsPanel, e.getX(), e.getY());
         if (-1 != tab)
         {
            popup.show(e.getComponent(), e.getX(), e.getY());
         }
      }
   }


   private class MyPropertiesListener implements PropertyChangeListener
	{
		private boolean _listening = true;

		void stopListening()
		{
			_listening = false;
		}

		void startListening()
		{
			_listening = true;
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			if (_listening)
			{
				propertiesHaveChanged(evt.getPropertyName());
			}
		}
	}

	private final static class ResultTabInfo
	{
		final ResultTab _tab;
		ResultFrame _resultFrame;

		ResultTabInfo(ResultTab tab)
		{
			if (tab == null)
			{
				throw new IllegalArgumentException("Null ResultTab passed");
			}
			_tab = tab;
		}
	}
}
