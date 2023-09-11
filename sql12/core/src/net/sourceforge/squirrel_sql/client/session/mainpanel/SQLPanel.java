package net.sourceforge.squirrel_sql.client.session.mainpanel;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications Copyright (C) 2001-2004 Johan Compagner
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
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.gui.titlefilepath.TitleFilePathHandler;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.action.OpenSqlHistoryAction;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLPanelListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLResultExecuterTabListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.ChangeTracker;
import net.sourceforge.squirrel_sql.client.session.mainpanel.multiclipboard.PasteFromHistoryAttach;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.SQLPanelSplitter;
import net.sourceforge.squirrel_sql.client.session.properties.ResultLimitAndReadOnPanelSmallPanel;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.gui.MemoryComboBox;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * This is the panel where SQL scripts can be entered and executed.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLPanel extends JPanel
{
	private static final ILogger s_log = LoggerController.createLogger(SQLPanel.class);

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLPanel.class);
    
	/** Used to separate lines in the SQL entry area. */
	private final static String LINE_SEPARATOR = "\n";

	/**
	 * Set to <TT>true</TT> once SQL history has been loaded from the file
	 * system.
	 */
	private static boolean s_loadedSQLHistory;

	// private final SQLExecutionAdapter _sqlExecutorHistoryAdapter;
	private SqlListenerService _sqlListenerService;
	private final SQLPanelSplitter _sqlPanelSplitter;

	/** Current session. */
	transient private ISession _session;

	private SQLHistoryComboBox _sqlHistoryComboBox;
	private ISQLEntryPanel _sqlEntry;


	private SqlComboListener _sqlComboListener = new SqlComboListener();
   private MyPropertiesListener _propsListener;

	private SQLPanelListenerManager _sqlPanelListenerManager = new SQLPanelListenerManager();

   /**
    * Is the bottom component of the split.
    * Holds the _simpleExecuterPanel if there is just one entry in _executors,
    * holds the _tabbedExecuterPanel if there is more that one element in _executors, 
    */
   private JPanel _executerPanleHolder;

	private JTabbedPane _tabbedExecuterPanel;
	private JPanel _simpleExecuterPanel;

	private boolean _hasBeenVisible = false;
	private JSplitPane _splitPane;



	private final List<ISQLResultExecutor> _executors = new ArrayList<>();

	private SQLResultExecutorPanel _sqlExecPanel;

	private ISQLPanelAPI _panelAPI;


   private UndoHandlerImpl _undoHandler;
   private ResultLimitAndReadOnPanelSmallPanel _resultLimitAndReadOnPanelSmallPanel = new ResultLimitAndReadOnPanelSmallPanel();
	private ToggleResultMinimizeHandler _toggleResultMinimizeHandler;
	private SQLPanelPosition _sqlPanelPosition;
	private ChangeTracker _changeTracker;


	public SQLPanel(ISession session, SQLPanelPosition sqlPanelPosition, TitleFilePathHandler titleFileHandler)
	{
		_sqlPanelPosition = sqlPanelPosition;
		setSession(session);
		createGUI();
		propertiesHaveChanged(null);
		_sqlExecPanel = new SQLResultExecutorPanel(session, true);


		_sqlListenerService = new SqlListenerService(session, sqlHistItem -> addSQLToHistoryComboBox(sqlHistItem));
		_sqlListenerService.getSqlExecutionListeners().forEach(l -> _sqlExecPanel.addSQLExecutionListener(l));


		addExecutor(_sqlExecPanel);
		_panelAPI = new SQLPanelAPI(this, titleFileHandler);
		_changeTracker.initChangeTracking(_panelAPI);

		_resultLimitAndReadOnPanelSmallPanel.loadData(session.getProperties());

      _toggleResultMinimizeHandler = new ToggleResultMinimizeHandler(_splitPane);


		if(SQLPanelPosition.MAIN_TAB_IN_SESSION_WINDOW == _sqlPanelPosition)
		{
			SessionStartupMainSQLTabContentLoader.handleLoadFileAtSessionStart(session, _panelAPI);
		}

		_sqlPanelSplitter = new SQLPanelSplitter(this);
	}

	public SQLPanelSplitter getSqlPanelSplitter()
	{
		return _sqlPanelSplitter;
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
		if (null == _propsListener)
		{
			_propsListener = new MyPropertiesListener();
		}
		else
		{
			_session.getProperties().removePropertyChangeListener(_propsListener);
		}
		_session.getProperties().addPropertyChangeListener(_propsListener);
	}

	/**
	 * JASON: This method may go eventually if the SQLPanel implements the
	 * ISQLPanelAPI interface.
	 */
	public ISQLPanelAPI getSQLPanelAPI()
	{
		return _panelAPI;
	}

	/** Current session. */
	public synchronized ISession getSession()
	{
		return _session;
	}

	public void addExecutor(ISQLResultExecutor exec)
	{
		_executors.add(exec);

      if(1 == _executors.size())
      {
         _executerPanleHolder.remove(_tabbedExecuterPanel);
         _executerPanleHolder.add(_simpleExecuterPanel);
      }
      else if(2 == _executors.size())
      {
         _executerPanleHolder.remove(_simpleExecuterPanel);
         _executerPanleHolder.add(_tabbedExecuterPanel);
         _executors.get(0);
         ISQLResultExecutor buf = _executors.get(0);
         _tabbedExecuterPanel.addTab(buf.getTitle(), null, buf.getComponent(), buf.getTitle());
      }


      if( 1 < _executors.size())
      {
         _tabbedExecuterPanel.addTab(exec.getTitle(), null, exec.getComponent(), exec.getTitle());
      }
      else
      {
         _simpleExecuterPanel.add(exec.getComponent());
      }

		_sqlPanelListenerManager.fireExecuterTabAdded(exec);
	}

	public void removeExecutor(ISQLResultExecutor exec)
	{
		_executors.remove(exec);
	}

	public SQLResultExecutorPanel getSQLExecPanel()
	{
		return _sqlExecPanel;
	}

	public ChangeTracker getChangeTracker()
	{
		return _changeTracker;
	}

	public synchronized void addSQLExecutionListener(ISQLExecutionListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("null ISQLExecutionListener passed");
		}
		//_listeners.add(ISQLExecutionListener.class, lis);
      _sqlExecPanel.addSQLExecutionListener(lis);
   }

	public synchronized void removeSQLExecutionListener(ISQLExecutionListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("null ISQLExecutionListener passed");
		}
		//_listeners.remove(ISQLExecutionListener.class, lis);
      _sqlExecPanel.removeSQLExecutionListener(lis);
	}

	public void addSQLPanelListener(ISQLPanelListener lis)
	{
		_sqlPanelListenerManager.addSQLPanelListener(lis);
	}

	public void removeSQLPanelListener(ISQLPanelListener lis)
	{
		_sqlPanelListenerManager.removeSQLPanelListener(lis);
	}

	public void addExecuterTabListener(ISQLResultExecuterTabListener lis)
	{
		_sqlPanelListenerManager.addExecuterTabListener(lis);
	}


	public synchronized void removeExecuterTabListener(ISQLResultExecuterTabListener lis)
	{
		_sqlPanelListenerManager.removeExecuterTabListener(lis);
	}


	public ISQLEntryPanel getSQLEntryPanel()
	{
		return _sqlEntry;
	}


	public void runCurrentExecuter()
	{
      _runExecuter(ISQLResultExecutor.ExecutionScope.EXEC_CURRENT_SQL);
   }

   public void runAllSqlsExecuter()
	{
      _runExecuter(ISQLResultExecutor.ExecutionScope.EXEC_ALL_SQLS);
	}


   private void _runExecuter(ISQLResultExecutor.ExecutionScope executionScope)
   {
      if(1 == _executors.size())
      {
         ISQLResultExecutor exec = _executors.get(0);
         exec.execute(_sqlEntry, executionScope);
      }
      else
      {
         int selectedIndex = _tabbedExecuterPanel.getSelectedIndex();
         ISQLResultExecutor exec = _executors.get(selectedIndex);
         exec.execute(_sqlEntry, executionScope);
      }
   }

   /**
	 * Sesssion is ending.
	 * Remove all listeners that this component has setup. Close all
	 * torn off result tab windows.
	 */
   public void sessionClosing()
	{
		if (_propsListener == null)
		{
			return;
		}

		_session.getProperties().removePropertyChangeListener(_propsListener);
		_propsListener = null;

		if(Main.getApplication().getSquirrelPreferences().isReloadSqlContents())
		{
			if (SQLPanelPosition.MAIN_TAB_IN_SESSION_WINDOW == _sqlPanelPosition)
			{
				String entireSQLScript = _panelAPI.getEntireSQLScript();

				if (StringUtilities.isEmpty(entireSQLScript, true))
				{
					ReloadSqlContentsHelper.tryDeleteContentsFile(getSession().getAlias());
				}
				else
				{
					ReloadSqlContentsHelper.writeLastSqlContent(getSession().getAlias(), entireSQLScript);
				}
			}
		}
		else
		{
			ReloadSqlContentsHelper.tryDeleteContentsFile(getSession().getAlias());
		}

	}

   public void sessionWorksheetOrTabClosing()
   {

		_sqlPanelListenerManager.fireSQLEntryAreaClosed();

		_sqlPanelSplitter.sessionWindowClosing();

      if (_hasBeenVisible)
      {
         SQLPanelSplitPaneFactory.saveOrientationDependingDividerLocation(_splitPane);
		}

		_sqlHistoryComboBox.removeActionListener(_sqlComboListener);
		_sqlHistoryComboBox.dispose();
		_sqlListenerService.getSqlExecutionListeners().forEach(l -> _sqlExecPanel.removeSQLExecutionListener(l));
		_sqlListenerService.close();

		_changeTracker.close();

		_sqlPanelListenerManager.fireSQLPanelParentClosing();
   }


	private void installSQLEntryPanel(ISQLEntryPanel pnl)
	{
		if (pnl == null)
		{
			throw new IllegalArgumentException("Null ISQLEntryPanel passed");
		}

		_sqlEntry = pnl;

		final int pos = _splitPane.getDividerLocation();

		_changeTracker = new ChangeTracker(_sqlEntry);

		_splitPane.add(_changeTracker.embedInTracking(), JSplitPane.LEFT); // /home/gerd/tmp/testdiff.patch


		_splitPane.setDividerLocation(pos);

      _undoHandler = new UndoHandlerImpl(_sqlEntry);

      new PasteFromHistoryAttach(_sqlEntry);

		_sqlPanelListenerManager.fireSQLEntryAreaInstalled();
	}

   public void storeSplitPanePositionOnSessionClose(boolean value)
   {
      super.setVisible(value);
      if (value)
      {
         _hasBeenVisible = true;
      }
   }


   /**
	 * Add the passed item to end of the SQL history. If the item
	 * at the end of the history is the same as the passed one
	 * then don't add it.
	 *
	 * @param	sql		SQL item to add.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> sql passed.
	 */
	private void addSQLToHistoryComboBox(SQLHistoryItem sql)
	{
		if (sql == null)
		{
			throw new IllegalArgumentException("SQLHistoryItem == null");
		}

		_sqlComboListener.stopListening();
		try
		{
			_sqlHistoryComboBox.removeItem(sql);
			_sqlHistoryComboBox.insertItemAt(sql, 0);
			_sqlHistoryComboBox.setSelectedIndex(0);

         _sqlHistoryComboBox.repaint();
		}
		finally
		{
			_sqlComboListener.startListening();
		}
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
		getSQLEntryPanel().addToSQLEntryAreaMenu(menu);
	}

	/**
	 * Add an <TT>Action</TT> to the SQL Entry Area popup menu.
	 *
	 * @param	action	The action to be added.
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
		return getSQLEntryPanel().addToSQLEntryAreaMenu(action);
	}

	public void addSeparatorToSQLEntryAreaMenu()
	{
		getSQLEntryPanel().addSeparatorToSQLEntryAreaMenu();
	}


	private void appendSQL(String sql)
	{
		if (_sqlEntry.getText().length() > 0)
		{
			_sqlEntry.appendText(LINE_SEPARATOR + LINE_SEPARATOR);
		}
		_sqlEntry.appendText(sql, true);
		_sqlEntry.requestFocus();
	}

	private void copySelectedItemToEntryArea()
	{
		SQLHistoryItem item = (SQLHistoryItem) _sqlHistoryComboBox.getSelectedItem();
		if (item != null)
		{
			appendSQL(item.getSQL());
		}
	}

	@SuppressWarnings("unused")
	private void openSQLHistory()
	{
      new SQLHistoryController(_session, getSQLPanelAPI(), ((SQLHistoryComboBoxModel) _sqlHistoryComboBox.getModel()).getItems());
   }

	private void propertiesHaveChanged(String propName)
	{
		final SessionProperties props = _session.getProperties();
		if (propName == null || propName.equals(SessionProperties.IPropertyNames.SQL_SHARE_HISTORY))
		{
			_sqlHistoryComboBox.setUseSharedModel(props.getSQLShareHistory());
		}

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

		if (propName == null || propName.equals(SessionProperties.IPropertyNames.SQL_LIMIT_ROWS))
		{
         _resultLimitAndReadOnPanelSmallPanel.propsChanged(props);
		}

		if (propName == null || propName.equals(SessionProperties.IPropertyNames.SQL_NBR_ROWS_TO_SHOW))
		{
         _resultLimitAndReadOnPanelSmallPanel.propsChanged(props);
		}

		if (propName == null || propName.equals(SessionProperties.IPropertyNames.SQL_READ_ON))
		{
         _resultLimitAndReadOnPanelSmallPanel.propsChanged(props);
		}

		if (propName == null || propName.equals(SessionProperties.IPropertyNames.SQL_READ_ON_BLOCK_SIZE))
		{
         _resultLimitAndReadOnPanelSmallPanel.propsChanged(props);
		}

		if (propName == null || propName.equals(SessionProperties.IPropertyNames.FONT_INFO))
		{
			FontInfo fi = props.getFontInfo();
			if (fi != null)
			{
				_sqlEntry.setFont(fi.createFont());
			}
		}

		if (propName == null || propName.equals(SessionProperties.IPropertyNames.SQL_ENTRY_HISTORY_SIZE)
							|| propName.equals(SessionProperties.IPropertyNames.LIMIT_SQL_ENTRY_HISTORY_SIZE))
		{
			if (props.getLimitSQLEntryHistorySize())
			{
				_sqlHistoryComboBox.setMaxMemoryCount(props.getSQLEntryHistorySize());
			}
			else
			{
				_sqlHistoryComboBox.setMaxMemoryCount(MemoryComboBox.NO_MAX);
			}
		}

	}

   public ArrayList<SQLHistoryItem> getSQLHistoryItems()
   {
      return ((SQLHistoryComboBoxModel) _sqlHistoryComboBox.getModel()).getItems();
   }

	public void toggleMinimizeResults()
	{
		_toggleResultMinimizeHandler.toggleMinimizeResults();
	}

	private void createGUI()
	{
		final IApplication app = _session.getApplication();
		synchronized (getClass())
		{
			if (!s_loadedSQLHistory)
			{
				final SQLHistory sqlHistory = app.getSQLHistory();
				SQLHistoryComboBoxModel.initializeSharedInstance(sqlHistory.getSQLHistoryItems());
				s_loadedSQLHistory = true;
			}
		}

//		_tabbedResultsPanel = UIFactory.getInstance().createTabbedPane();
		_tabbedExecuterPanel = UIFactory.getInstance().createTabbedPane();
		_tabbedExecuterPanel.addChangeListener(new MyExecuterPaneListener());

		setLayout(new BorderLayout());


		final SessionProperties props = _session.getProperties();
		_sqlHistoryComboBox = new SQLHistoryComboBox(props.getSQLShareHistory());
		_sqlHistoryComboBox.setEditable(false);
		if (_sqlHistoryComboBox.getItemCount() > 0)
		{
			_sqlHistoryComboBox.setSelectedIndex(0);
		}

		{
			JPanel pnl = new JPanel();
			pnl.setLayout(new BorderLayout());
			pnl.add(_sqlHistoryComboBox, BorderLayout.CENTER);

			Box box = Box.createHorizontalBox();
			box.add(new CopyLastButton(app));
			box.add(new ShowHistoryButton(app));
			box.add(Box.createHorizontalStrut(10));
         box.add(_resultLimitAndReadOnPanelSmallPanel);
			pnl.add(box, BorderLayout.EAST);
			add(pnl, BorderLayout.NORTH);
		}

		_splitPane = SQLPanelSplitPaneFactory.createSplitPane(getSession());
	  
		_splitPane.setOneTouchExpandable(true);

		installSQLEntryPanel(app.getSQLEntryPanelFactory().createSQLEntryPanel(_session,new HashMap<>()));

      _executerPanleHolder = new JPanel(new GridLayout(1,1));
      _executerPanleHolder.setMinimumSize(new Dimension(50,50));


      _simpleExecuterPanel = new JPanel(new GridLayout(1,1));
      _executerPanleHolder.add(_simpleExecuterPanel);
      _splitPane.add(_executerPanleHolder, JSplitPane.RIGHT);

		add(_splitPane, BorderLayout.CENTER);

		_sqlHistoryComboBox.addActionListener(_sqlComboListener);

		SwingUtilities.invokeLater(() -> _sqlEntry.getTextComponent().requestFocus());
	}


	public Action getUndoAction()
   {
      return _undoHandler.getUndoAction();
   }

   public Action getRedoAction()
   {
      return _undoHandler.getRedoAction();
   }

	public UndoHandlerImpl getUndoHandlerImpl()
	{
		return _undoHandler;
	}

	public SQLPanelPosition getSQLPanelPosition()
	{
		return _sqlPanelPosition;
	}

	/**
	 * Listens for changes in the execution jtabbedpane and then fires
	 * activation events
	 */
	private class MyExecuterPaneListener implements ChangeListener
	{
		public void stateChanged(ChangeEvent e)
		{
			JTabbedPane pane = (JTabbedPane)e.getSource();
			int index = pane.getSelectedIndex();
			if (index != -1)
			{
				_sqlPanelListenerManager.fireExecuterTabActivated(_executors.get(index));
			}
		}
	}

	private class MyPropertiesListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			propertiesHaveChanged(evt.getPropertyName());
		}
	}

	private class SqlComboListener implements ActionListener
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
				copySelectedItemToEntryArea();
			}
		}

	}


   private class CopyLastButton extends JButton
	{
		CopyLastButton(IApplication app)
		{
			super();
			final SquirrelResources rsrc = app.getResources();
			final ImageIcon icon = rsrc.getIcon(SquirrelResources.IImageNames.COPY_SELECTED);
			setIcon(icon);
			// i18n[SQLPanel.copylastbutton.hint=Copy current SQL history to entry area]
			String hint = s_stringMgr.getString("SQLPanel.copylastbutton.hint");
			setToolTipText(hint);
			Dimension dm = getPreferredSize();
			dm.setSize(dm.height, dm.height);
			setPreferredSize(dm);
			addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					copySelectedItemToEntryArea();
				}
			});
		}
	}

	private class ShowHistoryButton extends JButton
	{
		ShowHistoryButton(IApplication app)
		{
			final SquirrelResources rsrc = app.getResources();
			final ImageIcon icon = rsrc.getIcon(SquirrelResources.IImageNames.SQL_HISTORY);
			setIcon(icon);
			// i18n[SQLPanel.openSqlHistory.hint=Open SQL History]
			String hint = s_stringMgr.getString("SQLPanel.openSqlHistory.hint");
			setToolTipText(hint);
			Dimension dm = getPreferredSize();
			dm.setSize(dm.height, dm.height);
			setPreferredSize(dm);
			addActionListener(_session.getApplication().getActionCollection().get(OpenSqlHistoryAction.class));
		}
	}

}
