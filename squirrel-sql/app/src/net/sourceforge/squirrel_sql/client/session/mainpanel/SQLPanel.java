package net.sourceforge.squirrel_sql.client.session.mainpanel;
/*
 * Copyright (C) 2001-2002 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications Copyright (C) 2001-2002 Johan Compagner
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
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.undo.UndoManager;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetMetaDataDataSet;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.MemoryComboBox;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.SquirrelTabbedPane;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.RedoAction;
import net.sourceforge.squirrel_sql.client.session.action.UndoAction;
import net.sourceforge.squirrel_sql.client.session.event.IResultTabListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.event.ResultTabEvent;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

/**
 * This is the panel where SQL scripts can be entered and executed.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLPanel extends JPanel
{
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(SQLPanel.class);

	/** Current session. */
	private ISession _session;

	private MemoryComboBox _sqlCombo = new MemoryComboBox();
	private ISQLEntryPanel _sqlEntry;
	private JCheckBox _limitRowsChk = new JCheckBox("Limit rows: ");
	private IntegerField _nbrRows = new IntegerField();

	private SqlComboItemListener _sqlComboItemListener = new SqlComboItemListener();
	private MyPropertiesListener _propsListener;

	/** Each tab is a <TT>ResultTab</TT> showing the results of a query. */
	private SquirrelTabbedPane _tabbedResultsPanel;

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

	private boolean _hasBeenVisible = false;
	private JSplitPane _splitPane;

	/** Listeners */
	private EventListenerList _listeners = new EventListenerList();

	private UndoManager _undoManager = new UndoManager();

	/** Factory for generating unique IDs for new <TT>ResultTab</TT> objects. */
	private IntegerIdentifierFactory _idFactory = new IntegerIdentifierFactory();

	/** Listens to caret events in data entry area. */
	private final DataEntryAreaCaretListener _dataEntryCaretListener = new DataEntryAreaCaretListener();

	/**
	 * Ctor.
	 *
	 * @param	session	 Current session.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public SQLPanel(ISession session)
	{
		super();
		setSession(session);
		createUserInterface();
		propertiesHaveChanged(null);
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

	/**
	 * Add a listener listening for SQL Execution.
	 *
	 * @param   lis	 Listener
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
			throw new IllegalArgumentException("null ISQLExecutionListener passed");
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
			throw new IllegalArgumentException("null IResultTabListener passed");
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
			throw new IllegalArgumentException("null IResultTabListener passed");
		}
		_listeners.remove(IResultTabListener.class, lis);
	}

	public ISQLEntryPanel getSQLEntryPanel()
	{
		return _sqlEntry;
	}

	public void executeCurrentSQL()
	{
		executeSQL(getSQLEntryPanel().getSQLToBeExecuted());
	}

	public void executeSQL(String sql)
	{
		if (sql != null && sql.trim().length() > 0)
		{
			SQLExecuterTask task = new SQLExecuterTask(this, _session, sql);
			_session.getApplication().getThreadPool().addTask(task);
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
			ResultTabInfo ti = (ResultTabInfo) it.next();
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
			ResultTabInfo ti = (ResultTabInfo) it.next();
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
			_session.getProperties().removePropertyChangeListener(_propsListener);
			_propsListener = null;
		}

		closeAllSQLResultFrames();
	}

	public void replaceSQLEntryPanel(ISQLEntryPanel pnl)
	{
		if (pnl == null)
		{
			throw new IllegalArgumentException("Null ISQLEntryPanel passed");
		}

		SQLEntryState state = new SQLEntryState(this, _sqlEntry);
		final int pos = _splitPane.getDividerLocation();
		if (_sqlEntry != null)
		{
			_sqlEntry.removeUndoableEditListener(_undoManager);
			_sqlEntry.removeCaretListener(_dataEntryCaretListener);
			_splitPane.remove(_sqlEntry.getJComponent());
		}
		_splitPane.add(pnl.getJComponent(), JSplitPane.LEFT);
		_splitPane.setDividerLocation(pos);
		state.restoreState(pnl);
		_sqlEntry = pnl;

		_sqlEntry.addCaretListener(_dataEntryCaretListener);

		if (!_sqlEntry.hasOwnUndoableManager())
		{
			IApplication app = _session.getApplication();
			Resources res = app.getResources();
			UndoAction undo = new UndoAction(app, _undoManager);
			RedoAction redo = new RedoAction(app, _undoManager);

			_sqlEntry.getJComponent().registerKeyboardAction(
				undo,
				res.getKeyStroke(undo),
				this.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			_sqlEntry.getJComponent().registerKeyboardAction(
				redo,
				res.getKeyStroke(redo),
				this.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			_sqlEntry.setUndoActions(undo, redo);

			_sqlEntry.addUndoableEditListener(_undoManager);
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
	public void closeTab(ResultTab tab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null ResultTab passed");
		}
		s_log.debug("SQLPanel.closeTab(" + tab.getIdentifier().toString() + ")");
		tab.clear();
		_tabbedResultsPanel.remove(tab);
		ResultTabInfo tabInfo = (ResultTabInfo) _allTabs.get(tab.getIdentifier());
		_availableTabs.add(tabInfo);
		_usedTabs.remove(tabInfo);
		tabInfo._resultFrame = null;
		fireTabRemovedEvent(tab);
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
		s_log.debug("SQLPanel.createWindow(" + tab.getIdentifier().toString() + ")");
		_tabbedResultsPanel.remove(tab);
		ResultFrame frame = new ResultFrame(_session, tab);
		ResultTabInfo tabInfo = (ResultTabInfo) _allTabs.get(tab.getIdentifier());
		tabInfo._resultFrame = frame;
		_session.getApplication().getMainFrame().addInternalFrame(frame, true, null);
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

		s_log.debug("SQLPanel.returnToTabbedPane(" + tab.getIdentifier().toString() + ")");

		ResultTabInfo tabInfo = (ResultTabInfo) _allTabs.get(tab.getIdentifier());
		if (tabInfo._resultFrame != null)
		{
			addResultsTab(tab);
			fireTornOffResultTabReturned(tab);
			tabInfo._resultFrame = null;
		}
	}

	public void setVisible(boolean value)
	{
		super.setVisible(value);
		if (!_hasBeenVisible && value == true)
		{
			_splitPane.setDividerLocation(0.2d);
			_hasBeenVisible = true;
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
				((IResultTabListener) listeners[i + 1]).resultTabAdded(evt);
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
				((IResultTabListener) listeners[i + 1]).resultTabRemoved(evt);
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
				((IResultTabListener) listeners[i + 1]).resultTabTornOff(evt);
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
				((IResultTabListener) listeners[i + 1]).tornOffResultTabReturned(evt);
			}
		}
	}

	void setCancelPanel(final JPanel panel)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				_tabbedResultsPanel.addTab("Executing SQL", null, panel, "Press Cancel to Stop");
				_tabbedResultsPanel.setSelectedComponent(panel);
			}
		});
	}

	void addSQLToHistory(String sql)
	{
		_sqlComboItemListener.stopListening();
		_sqlCombo.addItem(new SqlComboItem(sql));
		_sqlComboItemListener.startListening();
	}

	void addResultsTab(
		final String sToken,
		ResultSetDataSet rsds,
		ResultSetMetaDataDataSet mdds,
		final JPanel cancelPanel)
	{
		final ResultTab tab;
		if (_availableTabs.size() > 0)
		{
			ResultTabInfo ti = (ResultTabInfo) _availableTabs.remove(0);
			_usedTabs.add(ti);
			tab = ti._tab;
			s_log.debug("Using tab " + tab.getIdentifier().toString() + " for results.");
		}
		else
		{
			tab = new ResultTab(_session, this, _idFactory.createIdentifier());
			ResultTabInfo ti = new ResultTabInfo(tab);
			_allTabs.put(tab.getIdentifier(), ti);
			_usedTabs.add(ti);
			s_log.debug("Created new tab " + tab.getIdentifier().toString() + " for results.");
		}

		try
		{
			tab.showResults(rsds, mdds, sToken);
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
		_tabbedResultsPanel.addTab(tab.getTitle(), null, tab, tab.getViewableSqlString());
	}

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
				sql = ((ISQLExecutionListener) listeners[i]).statementExecuting(sql);
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
		/*
				if (propertyName == null || propertyName.equals(
						SessionProperties.IPropertyNames.SQL_OUTPUT_CLASS_NAME)) {
					final IDataSetViewerDestination previous = _output;
					try {
						Class destClass = Class.forName(props.getSqlOutputClassName());
						if (IDataSetViewerDestination.class.isAssignableFrom(destClass) &&
								Component.class.isAssignableFrom(destClass)) {
							_output = (IDataSetViewerDestination)destClass.newInstance();
						}
		
					} catch (Exception ex) {
						_session.getApplication().getLogger().showMessage(Logger.ILogTypes.ERROR, ex.getMessage());
					}
					if (_output == null) {
						_output = new DataSetViewerTextPanel();
					}
					_viewer.setDestination(_output);
					_outputSp.setRowHeader(null);
					_outputSp.setViewportView((Component)_output);
				}
		*/
		//	  if (propertyName == null || propertyName.equals(
		//			  SessionProperties.IPropertyNames.SQL_REUSE_OUTPUT_TABS)) {
		//		  if (props.getSqlReuseOutputTabs()) {
		//			  for (int i = _tabbedResultsPanel.getTabCount() - 1;
		//					  i > 0; --i) {
		//				  _tabbedResultsPanel.remove(i);
		//			  }
		//			  _availableTabs.clear();
		//			  if (_usedTabs.size() > 0) {
		//				  Object tab = _usedTabs.get(0);
		//				  _usedTabs.clear();
		//				  _usedTabs.add(tab);
		//			  }
		//		  }
		//	  }

		if (propName == null || propName.equals(SessionProperties.IPropertyNames.AUTO_COMMIT))
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
		if (propName == null || propName.equals(SessionProperties.IPropertyNames.SQL_LIMIT_ROWS))
		{
			_limitRowsChk.setSelected(props.getSQLLimitRows());
		}

		if (propName == null
			|| propName.equals(SessionProperties.IPropertyNames.SQL_NBR_ROWS_TO_SHOW))
		{
			_nbrRows.setInt(props.getSQLNbrRowsToShow());
		}

		if (propName == null || propName.equals(SessionProperties.IPropertyNames.FONT_INFO))
		{
			FontInfo fi = props.getFontInfo();
			if (fi != null)
			{
				_sqlEntry.setFont(fi.createFont());
			}
		}

	}

	private void createUserInterface()
	{
		final IApplication app = _session.getApplication();

		_tabbedResultsPanel = new SquirrelTabbedPane(app.getSquirrelPreferences());

		setLayout(new BorderLayout());

		_sqlEntry = app.getSQLEntryPanelFactory().createSQLEntryPanel(_session);

		_nbrRows.setColumns(8);

		_sqlCombo.setEditable(false);
		{
			JPanel pnl = new JPanel();
			pnl.setLayout(new BorderLayout());
			pnl.add(_sqlCombo, BorderLayout.CENTER);
			Box box = Box.createHorizontalBox();
			box.add(Box.createHorizontalStrut(10));
			box.add(_limitRowsChk, BorderLayout.EAST);
			box.add(Box.createHorizontalStrut(5));
			box.add(_nbrRows, BorderLayout.EAST);
			pnl.add(box, BorderLayout.EAST);
			add(pnl, BorderLayout.NORTH);
		}

		_splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		_splitPane.setOneTouchExpandable(true);

		replaceSQLEntryPanel(app.getSQLEntryPanelFactory().createSQLEntryPanel(_session));
		_splitPane.add(_tabbedResultsPanel, JSplitPane.RIGHT);

		add(_splitPane, BorderLayout.CENTER);

//		propertiesHaveChanged(null);

		_sqlCombo.addActionListener(_sqlComboItemListener);
		_limitRowsChk.addChangeListener(new LimitRowsCheckBoxListener());
		_nbrRows.getDocument().addDocumentListener(new LimitRowsTextBoxListener());

		// Set focus to the SQL entry panel.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				_sqlEntry.getJComponent().requestFocus();
			}
		});
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

	private class SqlComboItemListener implements ActionListener
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

		public void actionPerformed(ActionEvent evt)
		{
			if (_listening)
			{
				SqlComboItem item = (SqlComboItem) _sqlCombo.getSelectedItem();
				if (item != null)
				{
					_sqlEntry.appendText("\n\n" + item.getText());
					_sqlEntry.setCaretPosition(_sqlEntry.getText().length() - 1);
				}
			}
		}
	}

	private static class SqlComboItem
	{
		private String _sql;
		private String _firstLine;

		SqlComboItem(String sql)
		{
			super();
			_sql = sql.trim();
			_firstLine = getFirstLine(sql);
		}

		public boolean equals(Object rhs)
		{
			boolean rc = false;
			if (this == rhs)
			{
				rc = true;
			}
			else if (rhs != null && rhs.getClass().equals(getClass()))
			{
				rc = ((SqlComboItem) rhs).getText().equals(getText());
			}
			return rc;
		}

		public String toString()
		{
			return _firstLine;
		}

		String getText()
		{
			return _sql;
		}

		private String getFirstLine(String sql)
		{
			int idx1 = sql.indexOf('\n');
			int idx2 = sql.indexOf('\r');
			if (idx1 == -1)
			{
				idx1 = idx2;
			}
			if (idx2 != -1 && idx2 < idx1)
			{
				idx1 = idx2;
			}
			sql = idx1 == -1 ? sql : sql.substring(0, idx1);
			return sql.replace('\t', ' ');
		}
	}

	private class LimitRowsCheckBoxListener implements ChangeListener
	{
		public void stateChanged(ChangeEvent evt)
		{
			if (_propsListener != null)
			{
				_propsListener.stopListening();
			}
			try
			{
				final boolean limitRows = ((JCheckBox) evt.getSource()).isSelected();
				_nbrRows.setEnabled(limitRows);
				_session.getProperties().setSQLLimitRows(limitRows);
			}
			finally
			{
				if (_propsListener != null)
				{
					_propsListener.startListening();
				}
			}
		}
	}

	private class LimitRowsTextBoxListener implements DocumentListener
	{
		public void insertUpdate(DocumentEvent evt)
		{
			updateProperties(evt);
		}

		public void changedUpdate(DocumentEvent evt)
		{
			updateProperties(evt);
		}

		public void removeUpdate(DocumentEvent evt)
		{
			updateProperties(evt);
		}

		private void updateProperties(DocumentEvent evt)
		{
			if (_propsListener != null)
			{
				_propsListener.stopListening();
			}
			try
			{
				_session.getProperties().setSQLNbrRowsToShow(_nbrRows.getInt());
			}
			finally
			{
				if (_propsListener != null)
				{
					_propsListener.startListening();
				}
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

	private final class SQLEntryState
	{
		private SQLPanel _sqlPnl;
		private boolean _saved = false;
		private String _text;
		private int _caretPos;
		private int _selStart;
		private int _selEnd;
		private boolean _hasFocus;

		SQLEntryState(SQLPanel sqlPnl, ISQLEntryPanel pnl)
		{
			super();
			_sqlPnl = sqlPnl;
			if (pnl != null)
			{
				_saved = true;
				_text = pnl.getText();
				_selStart = pnl.getSelectionStart();
				_selEnd = pnl.getSelectionEnd();
				_caretPos = pnl.getCaretPosition();
				//??
				//				_hasFocus = SwingUtilities.findFocusOwner(sqlPnl) == pnl.getComponent();
				//				_hasFocus = pnl.hasFocus();
				//				_hasFocus = sqlPnl.f pnl.hasFocus();
				_hasFocus = true;
			}
		}

		void restoreState(final ISQLEntryPanel pnl)
		{
			if (_saved && pnl != null)
			{
				pnl.setText(_text);
				pnl.setSelectionStart(_selStart);
				pnl.setSelectionEnd(_selEnd);
				//pnl.setCaretPosition(_caretPos);
				//				if (_hasFocus) {
				//					SwingUtilities.invokeLater(new Runnable() {
				//						public void run() {
				//							pnl.requestFocus();
				//						}
				//					});
				//				}
			}
		}
	}

	private final class DataEntryAreaCaretListener implements CaretListener
	{
		public void caretUpdate(CaretEvent evt)
		{
			final StringBuffer msg = new StringBuffer();
			msg.append(_sqlEntry.getCaretLineNumber() + 1)
				.append(",").append(_sqlEntry.getCaretLinePosition() + 1);
			_session.getSessionSheet().setStatusBarMessage(msg.toString());
		}
	}
}