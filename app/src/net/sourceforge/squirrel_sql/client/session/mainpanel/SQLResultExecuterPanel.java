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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.client.session.action.CloseAllSQLResultTabsAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseAllSQLResultTabsButCurrentAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseCurrentSQLResultTabAction;
import net.sourceforge.squirrel_sql.client.session.action.ToggleCurrentSQLResultTabStickyAction;
import net.sourceforge.squirrel_sql.client.session.event.IResultTabListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.event.ResultTabEvent;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetMetaDataDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeClob;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * This is the panel where SQL scripts are executed and results presented.
 *
 */
public class SQLResultExecuterPanel extends JPanel
									implements ISQLResultExecuter
{
    static final long serialVersionUID = 6961615570741567740L;
    
	/** Logger for this class. */
	private static final ILogger s_log = 
        LoggerController.createLogger(SQLResultExecuterPanel.class);

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(SQLResultExecuterPanel.class);
    
	private ISession _session;

	private MyPropertiesListener _propsListener;

	/** Each tab is a <TT>ResultTab</TT> showing the results of a query. */
	private JTabbedPane _tabbedExecutionsPanel;

	/**
	 * Collection of <TT>ResultTabInfo</TT> objects for all
	 * <TT>ResultTab</TT> objects that have been created. Keyed
	 * by <TT>ResultTab.getIdentifier()</TT>.
	 */
	private Map<IIdentifier,ResultTabInfo> _allTabs = 
        new HashMap<IIdentifier,ResultTabInfo>();

	/**
	 * Pool of <TT>ResultTabInfo</TT> objects available for use.
	 */
	private List<ResultTabInfo> _availableTabs = new ArrayList<ResultTabInfo>();

	/**
	 * Pool of <TT>ResultTabInfo</TT> objects currently being used.
	 */
	private ArrayList<ResultTabInfo> _usedTabs = new ArrayList<ResultTabInfo>();

	/** Listeners */
	private EventListenerList _listeners = new EventListenerList();

	/** Factory for generating unique IDs for new <TT>ResultTab</TT> objects. */
	private IntegerIdentifierFactory _idFactory = new IntegerIdentifierFactory();
   private ResultTab _stickyTab;
   
   private SquirrelPreferences _prefs = null;

	/** Reference to the executor so that it can be called from the CancelPanel*/
	private SQLExecuterTask _executer;

    private static enum SQLType { INSERT, SELECT, UPDATE, DELETE, UNKNOWN };
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
		super();
		setSession(session);
		createGUI();
		propertiesHaveChanged(null);
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
        _prefs = _session.getApplication().getSquirrelPreferences();
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

	/**
	 * Add a listener listening for events on result tabs.
	 *
	 * @param	lis	Listener
	 *
	 * @throws	IllegalArgumentException
	 *			If a null <TT>IResultTabListener</TT> passed.
	 */
	public synchronized void addResultTabListener(IResultTabListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("IResultTabListener == null");
		}
		_listeners.add(IResultTabListener.class, lis);
	}

	/**
	 * Remove a listener listening for events on result tabs.
	 *
	 * @param	lis	Listener
	 *
	 * @throws	IllegalArgumentException
	 *			If a null <TT>IResultTabListener</TT> passed.
	 */
	public synchronized void removeResultTabListener(IResultTabListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("IResultTabListener == null");
		}
		_listeners.remove(IResultTabListener.class, lis);
	}

	public void execute(ISQLEntryPanel sqlPanel)
	{
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

	public void executeSQL(String sql)
	{
		if (sql != null && sql.trim().length() > 0)
		{
            String origSQL = sql; 
	        sql = fireSQLToBeExecutedEvent(sql);
            
            // This can happen if an impl of ISQLExecutionListener returns null 
            // from the statementExecuting API method, to indicate that the SQL 
            // shouldn't be executed.            
            if (sql == null) {
                s_log.info(
                    "executeSQL: An ISQLExecutionListener veto'd execution of "+
                    "the following SQL: "+origSQL);
                return;
            }
            
            ISQLExecutionListener[] executionListeners =
                _listeners.getListeners(ISQLExecutionListener.class);
            SQLExecutionHandler handler = new SQLExecutionHandler(null);
            _executer = new SQLExecuterTask(_session, sql, handler, executionListeners);
	        
            if (_prefs.getLargeScriptStmtCount() > 0 
                    && _executer.getQueryCount() > _prefs.getLargeScriptStmtCount()) {
                _executer.setExecutionListeners(new ISQLExecutionListener[0]);
                handler.setLargeScript(true);
            }
            
            _session.getApplication().getThreadPool().addTask(_executer);
		}
	}

   private void onRerunSQL(String sql, ResultTab resultTab)
   {
      _executer = new SQLExecuterTask(_session, sql, new SQLExecutionHandler(resultTab), new ISQLExecutionListener[0]);
      _session.getApplication().getThreadPool().addTask(_executer);
   }


   /**
	 * Close all the Results frames.
	 */
	public synchronized void closeAllSQLResultFrames()
	{
		List tabs = (List)_usedTabs.clone();
		for (Iterator it = tabs.iterator(); it.hasNext();)
		{
			ResultTabInfo ti = (ResultTabInfo)it.next();
			if (ti._resultFrame != null)
			{
				ti._resultFrame.dispose();
				ti._resultFrame = null;
			}
		}
	}

	/**
	 * Close all the Results tabs.
	 */
	public synchronized void closeAllSQLResultTabs()
	{
		List tabs = (List)_usedTabs.clone();
		for (Iterator it = tabs.iterator(); it.hasNext();)
		{
			ResultTabInfo ti = (ResultTabInfo)it.next();
			if (ti._resultFrame == null)
			{
				closeTab(ti._tab);
			}
		}
	}

   public synchronized void closeAllButCurrentResultTabs()
   {
      Component selectedTab = _tabbedExecutionsPanel.getSelectedComponent();

      List tabs = (List)_usedTabs.clone();
      for (Iterator it = tabs.iterator(); it.hasNext();)
      {
         ResultTabInfo ti = (ResultTabInfo)it.next();
         if(false == ti._tab.equals(selectedTab))
         {
            if (ti._resultFrame == null)
            {
               closeTab(ti._tab);
            }
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

      if(false == _tabbedExecutionsPanel.getSelectedComponent() instanceof ResultTab)
      {
          //i18n[SQLResultExecuterPanel.nonStickyPanel=Cannot make a cancel panel sticky]
          String msg = 
              s_stringMgr.getString("SQLResultExecuterPanel.nonStickyPanel");
         JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), 
                                       msg);
         return;
      }

      _stickyTab = (ResultTab) _tabbedExecutionsPanel.getSelectedComponent();
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

   private int getIndexOfTab(ResultTab resultTab)
   {
      if(null == resultTab)
      {
         return -1;
      }

      for (int i = 0; i < _tabbedExecutionsPanel.getTabCount(); i++)
      {
         if (resultTab.equals(_tabbedExecutionsPanel.getComponentAt(i)))
         {
            return i;
         }
      }
      return -1;
   }



   public synchronized void closeCurrentResultTab()
   {
      Component selectedTab = _tabbedExecutionsPanel.getSelectedComponent();

      List tabs = (List)_usedTabs.clone();
      for (Iterator it = tabs.iterator(); it.hasNext();)
      {
         ResultTabInfo ti = (ResultTabInfo)it.next();
         if(ti._tab.equals(selectedTab))
         {
            if (ti._resultFrame == null)
            {
               closeTab(ti._tab);
            }
         }
      }
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

	/**
	 * Close the passed <TT>ResultTab</TT>. This is done by clearing
	 * all data from the tab, removing it from the tabbed panel
	 * and adding it to the list of available tabs.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ResultTab</TT> passed.
	 */
	public void closeTab(ResultTab tab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null ResultTab passed");
		}
		s_log
				.debug("SQLPanel.closeTab(" + tab.getIdentifier().toString()
						+ ")");
		tab.clear();
		_tabbedExecutionsPanel.remove(tab);
		ResultTabInfo tabInfo = _allTabs.get(tab.getIdentifier());
		_availableTabs.add(tabInfo);
		_usedTabs.remove(tabInfo);
		tabInfo._resultFrame = null;
		fireTabRemovedEvent(tab);
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

	protected void fireTabAddedEvent(ResultTab tab)
	{
		// Guaranteed to be non-null.
		Object[] listeners = _listeners.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event.
		ResultTabEvent evt = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == IResultTabListener.class)
			{
				// Lazily create the event:
				if (evt == null)
				{
					evt = new ResultTabEvent(_session, tab);
				}
				((IResultTabListener)listeners[i + 1]).resultTabAdded(evt);
			}
		}
	}

	protected void fireTabRemovedEvent(ResultTab tab)
	{
		// Guaranteed to be non-null.
		Object[] listeners = _listeners.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event.
		ResultTabEvent evt = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == IResultTabListener.class)
			{
				// Lazily create the event:
				if (evt == null)
				{
					evt = new ResultTabEvent(_session, tab);
				}
				((IResultTabListener)listeners[i + 1]).resultTabRemoved(evt);
			}
		}
	}

	protected void fireTabTornOffEvent(ResultTab tab)
	{
		// Guaranteed to be non-null.
		Object[] listeners = _listeners.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event.
		ResultTabEvent evt = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == IResultTabListener.class)
			{
				// Lazily create the event:
				if (evt == null)
				{
					evt = new ResultTabEvent(_session, tab);
				}
				((IResultTabListener)listeners[i + 1]).resultTabTornOff(evt);
			}
		}
	}

	protected void fireTornOffResultTabReturned(ResultTab tab)
	{
		// Guaranteed to be non-null.
		Object[] listeners = _listeners.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event.
		ResultTabEvent evt = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == IResultTabListener.class)
			{
				// Lazily create the event:
				if (evt == null)
				{
					evt = new ResultTabEvent(_session, tab);
				}
				((IResultTabListener)listeners[i + 1])
						.tornOffResultTabReturned(evt);
			}
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
	public void createWindow(ResultTab tab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null ResultTab passed");
		}
		s_log.debug("SQLPanel.createWindow(" + tab.getIdentifier().toString()
				+ ")");
		_tabbedExecutionsPanel.remove(tab);
		ResultFrame frame = new ResultFrame(_session, tab);
		ResultTabInfo tabInfo = _allTabs.get(tab.getIdentifier());
		tabInfo._resultFrame = frame;
		_session.getApplication().getMainFrame().addInternalFrame(frame, true, null, JLayeredPane.PALETTE_LAYER);
		fireTabTornOffEvent(tab);
		frame.setVisible(true);

		// There used to be a frame.pack() here but it resized the frame
		// to be very wide if text output was used.

		frame.toFront();
		frame.requestFocus();

   }

	/**
	 * Return the passed tab back into the tabbed pane.
	 *
	 * @param	tab	<TT>Resulttab</TT> to be returned
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ResultTab</TT> passed.
	 */
	public void returnToTabbedPane(ResultTab tab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null ResultTab passed");
		}

		s_log.debug("SQLPanel.returnToTabbedPane("
				+ tab.getIdentifier().toString() + ")");

		ResultTabInfo tabInfo = _allTabs.get(tab.getIdentifier());
		if (tabInfo._resultFrame != null)
		{
			addResultsTab(tab, null);
			fireTornOffResultTabReturned(tab);
			tabInfo._resultFrame = null;
		}
	}

    public ResultTab getSelectedResultTab() {
        return (ResultTab)_tabbedExecutionsPanel.getSelectedComponent();
    }
    
    /**
     * The idea here is to allow the caller to force the result to be re-run, 
     * potentially altering the output in the case where readFully is true and 
     * the result previously contained data placeholders (<clob>).  This will 
     * be useful for automating export to CSV/Excel of CLOB data, where it is 
     * most likely that the user doesn't want to see <clob> in the resultant 
     * export file.
     * 
     * @param readFully if true, then the configuration will (temporarily) be
     * set so that clob data is read and displayed.
     */
    public void reRunSelectedResultTab(boolean readFully) {
        boolean clobReadOrigVal = DataTypeClob.getReadCompleteClob();
        if (readFully) {
            DataTypeClob.setReadCompleteClob(true);
        }
        ResultTab rt = (ResultTab)_tabbedExecutionsPanel.getSelectedComponent();
        rt.reRunSQL();
        if (readFully) {
            DataTypeClob.setReadCompleteClob(clobReadOrigVal);
        }
    }

	private void addResultsTab(SQLExecutionInfo exInfo,
                              ResultSetDataSet rsds,
                              ResultSetMetaDataDataSet mdds,
                              final JPanel cancelPanel,
                              IDataSetUpdateableTableModel creator,
                              final ResultTab resultTabToReplace)
	{
		final ResultTab tab;
		if (_availableTabs.size() > 0)
		{
			ResultTabInfo ti = _availableTabs.remove(0);
			_usedTabs.add(ti);
			tab = ti._tab;
			tab.reInit(creator, exInfo);
			s_log.debug("Using tab " + tab.getIdentifier().toString()
					+ " for results.");
		}
		else
		{
         ResultTabListener resultTabListener = new ResultTabListener()
         {
            public void rerunSQL(String sql, ResultTab resultTab)
            {
               onRerunSQL(sql, resultTab);
            }
         };

         tab = new ResultTab(_session, this, _idFactory.createIdentifier(), exInfo, creator, resultTabListener);
			ResultTabInfo ti = new ResultTabInfo(tab);
			_allTabs.put(tab.getIdentifier(), ti);
			_usedTabs.add(ti);
			s_log.debug("Created new tab " + tab.getIdentifier().toString()
					+ " for results.");
		}

		try
		{
			tab.showResults(rsds, mdds, exInfo);
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					_tabbedExecutionsPanel.remove(cancelPanel);
					addResultsTab(tab, resultTabToReplace);
					_tabbedExecutionsPanel.setSelectedComponent(tab);
					fireTabAddedEvent(tab);
				}
			});
		}
		catch (DataSetException dse)
		{
			_session.showErrorMessage(dse);
		}
	}

	private void addResultsTab(ResultTab tab, ResultTab resultTabToReplace)
	{
      if(null == resultTabToReplace && null == _stickyTab)
      {
   		_tabbedExecutionsPanel.addTab(tab.getTitle(), null, tab, tab.getViewableSqlString());
         checkResultTabLimit();
      }
      else
      {
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

         closeResultTabAt(indexToReplace);
         _tabbedExecutionsPanel.insertTab(tab.getTitle(), tabIcon, tab, tab.getViewableSqlString(), indexToReplace);
      }
	}

   private void checkResultTabLimit()
   {
      SessionProperties props = _session.getProperties();

      while(props.getLimitSQLResultTabs() && props.getSqlResultTabLimit() < _tabbedExecutionsPanel.getTabCount())
      {
         closeResultTabAt(0);
      }
   }


   private void closeResultTabAt(int index)
   {
      Component selectedTab = _tabbedExecutionsPanel.getComponentAt(index);

      List tabs = (List)_usedTabs.clone();
      for (Iterator it = tabs.iterator(); it.hasNext();)
      {
         ResultTabInfo ti = (ResultTabInfo)it.next();
         if(ti._tab.equals(selectedTab))
         {
            if (ti._resultFrame == null)
            {
               closeTab(ti._tab);
            }
         }
      }
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
		_tabbedExecutionsPanel = UIFactory.getInstance().createTabbedPane(props.getSQLExecutionTabPlacement());


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

   private void initAccelerator(Class actionClass, JMenuItem mnuItem)
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

   private SQLType getSQLType(String sql) {
       SQLType result = SQLType.UNKNOWN;
       if (sql.toLowerCase().startsWith("insert")) {
           result = SQLType.INSERT;
       }
       if (sql.toLowerCase().startsWith("update")) {
           result = SQLType.UPDATE;
       }
       if (sql.toLowerCase().startsWith("select")) {
           result = SQLType.SELECT;
       }
       if (sql.toLowerCase().startsWith("delete")) {
           result = SQLType.DELETE;
       }
       return result;
   }
   
   /** This class is the handler for the execution of sql against the SQLExecuterPanel
	*
	*/
    private class SQLExecutionHandler implements ISQLExecuterHandler
	{
        private CancelPanel _cancelPanel = new CancelPanel();

        /**
         * Hold onto the current ResultDataSet so if the execution is
         * cancelled then this can be cancelled.
         */
        private ResultSetDataSet rsds = null;

        private String sqlToBeExecuted = null;
        private SQLType sqlType = null;
        private ResultTab _resultTabToReplace;
        private boolean _largeScript = false;
        private double _scriptTotalTime = 0;
        private double _scriptQueryTime = 0;
        private double _scriptOutptutTime = 0;
        private int _scriptRowsInserted = 0;
        private int _scriptRowsSelected = 0;
        private int _scriptRowsUpdated = 0;
        private int _scriptRowsDeleted = 0;
      
        public SQLExecutionHandler(ResultTab resultTabToReplace)
        {
            super();
            _resultTabToReplace = resultTabToReplace;
            setCancelPanel(_cancelPanel);
        }

        /**
         * Set whether or not the script is large.  If the script is large, then
         * do some performance optimizations with the GUI so that it remains 
         * responsive.  If the UI is not responsive, then the user is not able
         * to see what is happening, nor are they able to control it (cancelling
         * becomes ineffective)
         * 
         * @param aBoolean whether or not the script is large.
         */
        public void setLargeScript(boolean aBoolean) {
            _largeScript = aBoolean;
        }
      
        /**
         * Determines whether or not the current statement SQL should be rendered.
         * Since too many statements can cause the UI to stop rendering the 
         * statements, we back off rendering after many statements so that the UI 
         * can continue to provide feedback to the user. 
         * 
         * @param current
         * @param total
         * @return
         */
        private boolean shouldRenderSQL(int current, int total) {
            if (!_largeScript) {
                return true;
            }
            boolean result = true;
            // Back-off a bit after a hundred updates to allow the UI to update
            if (total > 200 && current > 100 && current % 10 != 0) {
                result = false;
            }
            if (total > 1000 && current > 500 && current % 50 != 0) {
                result = false;
            }
            if (total > 2000 && current > 1000 && current % 100 != 0) {
                result = false;
            }
            return result;
        }

        public void sqlToBeExecuted(final String sql)
        {
            _cancelPanel.incCurrentQueryIndex();
            int currentStmtCount = _cancelPanel.getCurrentQueryIndex();
            if (!shouldRenderSQL(currentStmtCount,_cancelPanel.getTotalCount())) {
                return;
            }
            final String cleanSQL = StringUtilities.cleanString(sql);
            sqlToBeExecuted = cleanSQL;
            sqlType = getSQLType(cleanSQL);

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    _cancelPanel.setSQL(cleanSQL);

                    // i18n[SQLResultExecuterPanel.execStatus=Executing SQL...]
                    String status = 
                        s_stringMgr.getString("SQLResultExecuterPanel.execStatus");
                    _cancelPanel.setStatusLabel(status);
                }
            });
        }

        /**
         * This will - depending on the size of the script - print a message 
         * indicating the time that it took to execute one or more queries.  
         * When executing a large script (as defined by the user, but default is
         * > 200 statements) we don't want to keep sending messages to the 
         * message panel, otherwise the UI will get behind and slow the execution
         * of the script and prevent the user from cancelling the operation.  So
         * this method will track the total time when executing a large script, 
         * otherwise for small scripts it puts out a message for every statement.
         */
        public void sqlExecutionComplete(SQLExecutionInfo exInfo, 
                int processedStatementCount, 
                int statementCount)
        {
            double executionLength = ((double)exInfo.getSQLExecutionElapsedMillis())/1000d;
            double outputLength = ((double)exInfo.getResultsProcessingElapsedMillis())/1000d;            
            double totalLength = executionLength + outputLength;

            if (_largeScript) {
                // Track the time in aggregate for the script.
                _scriptQueryTime += executionLength;
                _scriptOutptutTime += outputLength;
                _scriptTotalTime += totalLength;

                // When we get to the last statement, if the script is large,
                // show the user the total execution time.                 
                if (statementCount == processedStatementCount) {
                    printScriptExecDetails(statementCount,
                            _scriptQueryTime,
                            _scriptOutptutTime,
                            _scriptTotalTime);
                }
            } else {
                printStatementExecTime(processedStatementCount,
                        statementCount,
                        executionLength,
                        outputLength,
                        totalLength);
            }
        }

        private void printScriptExecDetails(int statementCount, 
                double executionLength,
                double outputLength,
                double totalLength) 
        {
            final NumberFormat nbrFmt = NumberFormat.getNumberInstance();

            Object[] args = new Object[] {new Integer(statementCount),
                    nbrFmt.format(totalLength),
                    nbrFmt.format(executionLength),
                    nbrFmt.format(outputLength)};

            //i18n[SQLResultExecuterPanel.scriptQueryStatistics=Executed {0} 
            //queries; elapsed time (seconds) - Total: {1}, SQL query: {2}, 
            //Building output: {3}]
            String stats = 
                s_stringMgr.getString(
                        "SQLResultExecuterPanel.scriptQueryStatistics", 
                        args);

            String[] counts = 
                new String[] {Integer.toString(_scriptRowsInserted),
                    Integer.toString(_scriptRowsSelected),
                    Integer.toString(_scriptRowsUpdated),
                    Integer.toString(_scriptRowsDeleted)};

            //i18n[SQLResultExecuterPanel.scriptStmtCounts=Row update 
            //counts: {0} Inserts, {1} Selects, {2} Updates, {3} Deletes
            String msg = 
                s_stringMgr.getString("SQLResultExecuterPanel.scriptStmtCounts",
                        counts);
            getSession().showMessage(msg);
            getSession().showMessage(stats);
        }

        private void printStatementExecTime(int processedStatementCount, 
                int statementCount,
                double executionLength,
                double outputLength,
                double totalLength)
        {
            final NumberFormat nbrFmt = NumberFormat.getNumberInstance();

            Object[] args = new Object[] {new Integer(processedStatementCount),
                    new Integer(statementCount),
                    nbrFmt.format(totalLength),
                    nbrFmt.format(executionLength),
                    nbrFmt.format(outputLength)};

            //i18n[SQLResultExecuterPanel.queryStatistics=Query {0} of {1} 
            //elapsed time (seconds) - Total: {2}, SQL query: {3}, 
            //Building output: {4}]
            String stats = 
                s_stringMgr.getString("SQLResultExecuterPanel.queryStatistics", 
                        args);

            getSession().showMessage(stats);            
        }

        public void sqlExecutionCancelled()
        {
            if (rsds != null) {
                rsds.cancelProcessing();
            }
            // i18n[SQLResultExecuterPanel.canceleRequested=Query execution cancel requested by user.]
//          String canc = 
//          s_stringMgr.getString("SQLResultExecuterPanel.canceleRequested");
//          getSession().getMessageHandler().showMessage(canc);
        }

		public void sqlDataUpdated(int updateCount)
		{
            
            Integer count = new Integer(updateCount);
            String msg = "";
            
            switch (sqlType) {
                case INSERT:
                    if (_largeScript) {
                        _scriptRowsInserted++;
                    } else {
                        // i18n[SQLResultExecuterPanel.rowsUpdated={0} Row(s) Inserted]
                        msg = s_stringMgr.getString("SQLResultExecuterPanel.rowsInserted",
                                                    count);                               
                    }
                    break;
                case SELECT:
                    if (_largeScript) {
                        _scriptRowsSelected++;
                    } else {
                        // i18n[SQLResultExecuterPanel.rowsSelected={0} Row(s) Selected]
                        msg = s_stringMgr.getString("SQLResultExecuterPanel.rowsSelected",
                                                    count);
                    }
                    break;
                case UPDATE:
                    if (_largeScript) {
                        _scriptRowsUpdated++;
                    } else {
                        // i18n[SQLResultExecuterPanel.rowsUpdated={0} Row(s) Updated]
                        msg = s_stringMgr.getString("SQLResultExecuterPanel.rowsUpdated",
                                                  count);
                    }
                    break;
                case DELETE:
                    if (_largeScript) {
                        _scriptRowsDeleted++;
                    } else {
                        // i18n[SQLResultExecuterPanel.rowsDeleted={0} Row(s) Deleted]
                        msg = s_stringMgr.getString("SQLResultExecuterPanel.rowsDeleted",
                                                    count);                                                        
                    }
                    break;
            }            
            if (_largeScript) {
                return;
            }
            getSession().showMessage(msg);
		}

		public void sqlResultSetAvailable(ResultSet rs, SQLExecutionInfo info,
				IDataSetUpdateableTableModel model) throws DataSetException
		{
            // i18n[SQLResultExecuterPanel.outputStatus=Building output...]
            String outputStatus = 
                s_stringMgr.getString("SQLResultExecuterPanel.outputStatus");
			_cancelPanel.setStatusLabel(outputStatus);
			rsds = new ResultSetDataSet();
			SessionProperties props = getSession().getProperties();
			ResultSetMetaDataDataSet rsmdds = null;
            if (props.getShowResultsMetaData())
            {
               rsmdds = new ResultSetMetaDataDataSet(rs);
            }
			rsds.setResultSet(rs);

			addResultsTab(info, rsds, rsmdds, _cancelPanel, model, _resultTabToReplace);
			rsds = null;
		}

		public void sqlExecutionWarning(SQLWarning warn)
		{
		    getSession().showMessage(warn);
		}

		public void sqlStatementCount(int statementCount)
		{
		    _cancelPanel.setQueryCount(statementCount);
		}

		public void sqlCloseExecutionHandler()
		{
		    removeCancelPanel(_cancelPanel);
		    _executer = null;
		}

		public void sqlExecutionException(Throwable th, String postErrorString)
		{
		    SQLExecutionException ex = 
		        new SQLExecutionException(th, postErrorString);

		    ExceptionFormatter formatter = getSession().getExceptionFormatter();

		    String message = formatter.format(ex);

		    getSession().showErrorMessage(message);


		    if(getSession().getProperties().getWriteSQLErrorsToLog())
		    {
		        s_log.info(message);   
		    }
		}


		private void removeCancelPanel(final JPanel cancelPanel)
		{
		    SwingUtilities.invokeLater(new Runnable()
		    {
		        public void run()
		        {
		            _tabbedExecutionsPanel.remove(cancelPanel);

		            int indexToSelect = -1;
		            if(null == _resultTabToReplace)
		            {
		                indexToSelect = getIndexOfTab(_stickyTab);
		            }
		            else
		            {
		                indexToSelect = getIndexOfTab(_resultTabToReplace);
		            }

		            if(-1 != indexToSelect)
		            {
		                _tabbedExecutionsPanel.setSelectedIndex(indexToSelect);
		            }

		        }
		    });
		}

		private void setCancelPanel(final JPanel panel)
		{
		    SwingUtilities.invokeLater(new Runnable()
		    {
		        public void run()
		        {
		            // i18n[SQLResultExecuterPanel.exec=Executing SQL]
		            String execMsg = 
		                s_stringMgr.getString("SQLResultExecuterPanel.exec");
		            // i18n[SQLResultExecuterPanel.cancelMsg=Press Cancel to Stop]
		            String cancMsg = 
		                s_stringMgr.getString("SQLResultExecuterPanel.cancelMsg");

		            _tabbedExecutionsPanel.addTab(execMsg, null, panel,	cancMsg);
		            _tabbedExecutionsPanel.setSelectedComponent(panel);
		        }
		    });
		}
      
		private final class CancelPanel extends JPanel
		implements ActionListener
		{
		    private JLabel _sqlLbl = new JLabel();
		    private JLabel _currentStatusLbl = new JLabel();

		    /** Total number of queries that will be executed. */
		    private int _queryCount;

		    /** Number of the query currently being executed (starts from 1). */
		    private int _currentQueryIndex = 0;

		    private CancelPanel()
		    {
		        super(new GridBagLayout());

		        // i18n[SQLResultExecuterPanel.cancelButtonLabel=Cancel]
		        String label = 
		            s_stringMgr.getString("SQLResultExecuterPanel.cancelButtonLabel");
		        JButton cancelBtn = new JButton(label);
		        cancelBtn.addActionListener(this);

		        GridBagConstraints gbc = new GridBagConstraints();

		        gbc.anchor = GridBagConstraints.WEST;
		        gbc.insets = new Insets(5, 10, 5, 10);

		        gbc.gridx = 0;
		        gbc.gridy = 0;

		        // i18n[SQLResultExecuterPanel.sqlLabel=SQL:]
		        label = s_stringMgr.getString("SQLResultExecuterPanel.sqlLabel");
		        add(new JLabel(label), gbc);

		        gbc.weightx = 1;
		        ++gbc.gridx;
		        add(_sqlLbl, gbc);

		        gbc.weightx = 0;
		        gbc.gridx = 0;
		        ++gbc.gridy;
		        // i18n[SQLResultExecuterPanel.statusLabel=Status:]
		        label = 
		            s_stringMgr.getString("SQLResultExecuterPanel.statusLabel");
		        add(new JLabel(label), gbc);

		        ++gbc.gridx;
		        add(_currentStatusLbl, gbc);

		        gbc.gridx = 0;
		        ++gbc.gridy;
		        gbc.fill = GridBagConstraints.NONE;
		        add(cancelBtn, gbc);
		    }

		    public void incCurrentQueryIndex() {
		        ++_currentQueryIndex;
		    }

		    public void setSQL(String sql)
		    {
		        // i18n[SQLResultExecuterPanel.currentSQLLabel={0} of {1} - {2}]
		        String label = 
		            s_stringMgr.getString("SQLResultExecuterPanel.currentSQLLabel",
		                    new Object[] { String.valueOf(_currentQueryIndex),
		                    String.valueOf(_queryCount),
		                    sql} );                
		        _sqlLbl.setText(label);
		    }

		    public void setStatusLabel(String text)
		    {
		        _currentStatusLbl.setText(text);
		    }

		    public void setQueryCount(int value)
		    {
		        _queryCount = value;
		        _currentQueryIndex = 0;
		    }

		    public int getTotalCount() {
		        return _queryCount;
		    }

		    public int getCurrentQueryIndex() {
		        return _currentQueryIndex;
		    }


		    public void actionPerformed(ActionEvent event)
		    {
		        try
		        {
		            if (_executer != null){
		                _executer.cancel();
		            }
		        }
		        catch (Throwable th)
		        {
		            s_log.error("Error occured cancelling SQL", th);
		        }
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