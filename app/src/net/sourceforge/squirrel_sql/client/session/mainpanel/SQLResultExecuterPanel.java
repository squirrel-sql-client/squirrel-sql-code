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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetMetaDataDataSet;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.client.session.event.IResultTabListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.event.ResultTabEvent;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
/**
 * This is the panel where SQL scripts are executed and results presented.
 *
 */
public class SQLResultExecuterPanel extends JPanel
									implements ISQLResultExecuter
{
	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(SQLResultExecuterPanel.class);

	private ISession _session;

	private MyPropertiesListener _propsListener;

	/** Each tab is a <TT>ResultTab</TT> showing the results of a query. */
	private JTabbedPane _tabbedResultsPanel;

	/**
	 * Collection of <TT>ResultTabInfo</TT> objects for all
	 * <TT>ResultTab</TT> objects that have been created. Keyed
	 * by <TT>ResultTab.getIdentifier()</TT>.
	 */
	private Map _allTabs = new HashMap();

	/**
	 * Pool of <TT>ResultTabInfo</TT> objects available for use.
	 */
	private List _availableTabs = new ArrayList();

	/**
	 * Pool of <TT>ResultTabInfo</TT> objects currently being used.
	 */
	private ArrayList _usedTabs = new ArrayList();

	/** Listeners */
	private EventListenerList _listeners = new EventListenerList();

	/** Factory for generating unique IDs for new <TT>ResultTab</TT> objects. */
	private IntegerIdentifierFactory _idFactory = new IntegerIdentifierFactory();

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
		return "Results";
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
			_session.getMessageHandler().showErrorMessage(
					"No SQL selected for execution.");
		}
	}

	/** Reference to the executor so that it can be called from the CancelPanel*/
	private SQLExecuterTask _executer;

	private void executeSQL(String sql)
	{
		if (sql != null && sql.trim().length() > 0)
		{
			fireSQLToBeExecutedEvent(sql);
			_executer = new SQLExecuterTask(_session, sql,
													new SQLExecutionHandler());
			_session.getApplication().getThreadPool().addTask(_executer);
		}
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

	void selected()
	{
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
		_tabbedResultsPanel.remove(tab);
		ResultTabInfo tabInfo = (ResultTabInfo)_allTabs
				.get(tab.getIdentifier());
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
		final int tabCount = _tabbedResultsPanel.getTabCount();
		if (tabCount > 1)
		{
			int nextTabIdx = _tabbedResultsPanel.getSelectedIndex() + 1;
			if (nextTabIdx >= tabCount)
			{
				nextTabIdx = 0;
			}
			_tabbedResultsPanel.setSelectedIndex(nextTabIdx);
		}
	}

	/**
	 * Display the previous tab in the SQL results.
	 */
	public void gotoPreviousResultsTab()
	{
		final int tabCount = _tabbedResultsPanel.getTabCount();
		if (tabCount > 1)
		{
			int prevTabIdx = _tabbedResultsPanel.getSelectedIndex() - 1;
			if (prevTabIdx < 0)
			{
				prevTabIdx = tabCount - 1;
			}
			_tabbedResultsPanel.setSelectedIndex(prevTabIdx);
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

	protected List fireAllSQLToBeExecutedEvent(List sql)
	{
		// Guaranteed to be non-null.
		Object[] listeners = _listeners.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event.
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == ISQLExecutionListener.class)
			{
				((ISQLExecutionListener)listeners[i + 1])
						.allStatementsExecuting(sql);
				if (sql.size() == 0)
				{
					break;
				}
			}
		}
		return sql;
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
				sql = ((ISQLExecutionListener)listeners[i + 1])
						.statementExecuting(sql);
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
		_tabbedResultsPanel.remove(tab);
		ResultFrame frame = new ResultFrame(_session, tab);
		ResultTabInfo tabInfo = (ResultTabInfo)_allTabs
				.get(tab.getIdentifier());
		tabInfo._resultFrame = frame;
		_session.getApplication().getMainFrame().addInternalFrame(frame, true,
				null);
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

		ResultTabInfo tabInfo = (ResultTabInfo)_allTabs
				.get(tab.getIdentifier());
		if (tabInfo._resultFrame != null)
		{
			addResultsTab(tab);
			fireTornOffResultTabReturned(tab);
			tabInfo._resultFrame = null;
		}
	}

	void setCancelPanel(final JPanel panel)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				_tabbedResultsPanel.addTab("Executing SQL", null, panel,
						"Press Cancel to Stop");
				_tabbedResultsPanel.setSelectedComponent(panel);
			}
		});
	}

	void addResultsTab(SQLExecutionInfo exInfo, ResultSetDataSet rsds,
					ResultSetMetaDataDataSet mdds, final JPanel cancelPanel,
					IDataSetUpdateableTableModel creator)
	{
		final ResultTab tab;
		if (_availableTabs.size() > 0)
		{
			ResultTabInfo ti = (ResultTabInfo)_availableTabs.remove(0);
			_usedTabs.add(ti);
			tab = ti._tab;
			s_log.debug("Using tab " + tab.getIdentifier().toString()
					+ " for results.");
		}
		else
		{
			tab = new ResultTab(_session, this, _idFactory.createIdentifier(),
									exInfo, creator);
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
					_tabbedResultsPanel.remove(cancelPanel);
					addResultsTab(tab);
					_tabbedResultsPanel.setSelectedComponent(tab);
					fireTabAddedEvent(tab);
				}
			});
		}
		catch (DataSetException dse)
		{
			_session.getMessageHandler().showErrorMessage(dse);
		}
	}

	void removeCancelPanel(final JPanel cancelPanel)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				_tabbedResultsPanel.remove(cancelPanel);
			}
		});
	}

	private void addResultsTab(ResultTab tab)
	{
		_tabbedResultsPanel.addTab(tab.getTitle(), null, tab, tab
				.getViewableSqlString());
	}

	/** JASON: This is a dead method it isnt called anywhere*/
	private String modifyIndividualScript(String sql)
	{
		// Guaranteed to be non-null.
		Object[] listeners = _listeners.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event.
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == ISQLExecutionListener.class)
			{
				sql = ((ISQLExecutionListener)listeners[i])
						.statementExecuting(sql);
				if (sql == null)
				{
					break;
				}
			}
		}

		return sql;
	}

	private void propertiesHaveChanged(String propName)
	{
		final SessionProperties props = _session.getProperties();

		if (propName == null
				|| propName
						.equals(SessionProperties.IPropertyNames.AUTO_COMMIT))
		{
			final SQLConnection conn = _session.getSQLConnection();
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
					_session.getMessageHandler().showErrorMessage(ex);
				}
				try
				{
					conn.setAutoCommit(props.getAutoCommit());
				}
				catch (SQLException ex)
				{
					props.setAutoCommit(auto);
					_session.getMessageHandler().showErrorMessage(ex);
				}
			}
		}

		if (propName == null
				|| propName
						.equals(SessionProperties.IPropertyNames.SQL_RESULTS_TAB_PLACEMENT))
		{
			_tabbedResultsPanel.setTabPlacement(props
					.getSQLResultsTabPlacement());
		}
	}

	private void createGUI()
	{
		final IApplication app = _session.getApplication();

		_tabbedResultsPanel = UIFactory.getInstance().createTabbedPane();

		setLayout(new BorderLayout());
		final SessionProperties props = _session.getProperties();

		add(_tabbedResultsPanel, BorderLayout.CENTER);
	}

	/** This class is the handler for the execution of sql against the SQLExecuterPanel
	 *
	 */
	private class SQLExecutionHandler implements ISQLExecuterHandler
	{
		private CancelPanel _cancelPanel = new CancelPanel();

		public SQLExecutionHandler()
		{
			super();
			setCancelPanel(_cancelPanel);
		}

		public void sqlToBeExecuted(String sql)
		{
			//JASON: Need to do something about this
			//_cancelPanel.setQueryCount(queryStrings.size());
			_cancelPanel.setSQL(StringUtilities.cleanString(sql));
			_cancelPanel.setStatusLabel("Executing SQL...");
		}

		public void sqlExecutionComplete(SQLExecutionInfo exInfo)
		{
			removeCancelPanel(_cancelPanel);

			// i18n
			final NumberFormat nbrFmt = NumberFormat.getNumberInstance();
			double executionLength = exInfo.getSQLExecutionElapsedMillis() / 1000.0;
			double outputLength = exInfo.getResultsProcessingElapsedMillis() / 1000.0;
			StringBuffer buf = new StringBuffer();
			buf.append("Query ").append(" elapsed time (seconds) - Total: ")
					.append(nbrFmt.format(executionLength + outputLength))
					.append(", SQL query: ").append(
							nbrFmt.format(executionLength)).append(
							", Building output: ").append(
							nbrFmt.format(outputLength));
			getSession().getMessageHandler().showMessage(buf.toString());
		}

		public void sqlExecutionCancelled()
		{
			if (rsds != null)
				rsds.cancelProcessing();
			getSession().getMessageHandler().showMessage(
					"Query execution cancelled by user.");
		}

		public void sqlDataUpdated(int updateCount)
		{
			getSession().getMessageHandler().showMessage(
					updateCount + " Rows Updated");
		}

		/** Hold onto the current ResultDataSet so if the execution is
		 *  cancelled then this can be cancelled.
		 */
		ResultSetDataSet rsds = null;

		public void sqlResultSetAvailable(ResultSet rs, SQLExecutionInfo info)
		{
			_cancelPanel.setStatusLabel("Building output...");
			rsds = new ResultSetDataSet();
			SessionProperties props = getSession().getProperties();
			ResultSetMetaDataDataSet rsmdds = null;
			try
			{
//				rsds.setResultSet(rs, props.getLargeResultSetObjectInfo());
				rsds.setResultSet(rs);
				rsmdds = new ResultSetMetaDataDataSet(rs);
			}
			catch (DataSetException ex)
			{
				getSession().getMessageHandler().showMessage(ex);
				return;
			}
			// JASON: Shouldn't be null for the last argument. We need an
			// instance of IDataSetUpdateableTableModel
			addResultsTab(info, rsds, rsmdds, _cancelPanel, null);
			rsds = null;
		}

		public void sqlExecutionWarning(SQLWarning warn)
		{
			getSession().getMessageHandler().showMessage(warn);
		}

		public void sqlExecutionException(Throwable th)
		{
			removeCancelPanel(_cancelPanel);
			getSession().getMessageHandler().showErrorMessage("Error: " + th);
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

				JButton cancelBtn = new JButton("Cancel");
				cancelBtn.addActionListener(this);

				GridBagConstraints gbc = new GridBagConstraints();

				gbc.anchor = GridBagConstraints.WEST;
				gbc.insets = new Insets(5, 10, 5, 10);

				gbc.gridx = 0;
				gbc.gridy = 0;
				add(new JLabel("SQL:"), gbc);

				gbc.weightx = 1;
				++gbc.gridx;
				add(_sqlLbl, gbc);

				gbc.weightx = 0;
				gbc.gridx = 0;
				++gbc.gridy;
				add(new JLabel("Status:"), gbc);

				++gbc.gridx;
				add(_currentStatusLbl, gbc);

				gbc.gridx = 0;
				++gbc.gridy;
				gbc.fill = GridBagConstraints.NONE;
				add(cancelBtn, gbc);
			}

			public void setSQL(String sql)
			{
				++_currentQueryIndex;
				StringBuffer buf = new StringBuffer();
				buf.append(String.valueOf(_currentQueryIndex)).append(" of ")
						.append(String.valueOf(_queryCount)).append(" - ")
						.append(sql);
				_sqlLbl.setText(buf.toString());
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

			public void actionPerformed(ActionEvent event)
			{
				try
				{
					_executer.cancel();
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