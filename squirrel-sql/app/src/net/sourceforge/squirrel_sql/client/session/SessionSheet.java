package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.Icon;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.action.CommitAction;
import net.sourceforge.squirrel_sql.client.session.action.ExecuteSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.RollbackAction;
import net.sourceforge.squirrel_sql.client.session.action.SessionPropertiesAction;
import net.sourceforge.squirrel_sql.client.session.objectstree.DatabasePanel;
import net.sourceforge.squirrel_sql.client.session.objectstree.ProcedurePanel;
import net.sourceforge.squirrel_sql.client.session.objectstree.TablePanel;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class SessionSheet extends JInternalFrame {
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n {
		String SQL_TAB_TITLE = "SQL";
		String SQL_TAB_DESC = "Execute SQL statements";
		String OBJ_TAB_TITLE = "Objects";
		String OBJ_TAB_DESC = "Show database objects";
		String IMPORT_TAB_TITLE = "Import Data";
		String IMPORT_TAB_DESC = "Import csv files into database";
	}

	/**
	 * IDs of tabs in the main tabbed pane.
	 */
	public interface IMainTabIndexes {
		int OBJECT_TREE_TAB = 0;
		int SQL_TAB = 1;
	}

	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(SessionSheet.class);

	private ISession _session;

	/** Listener to the sessions properties. */
	PropertyChangeListener _propsListener;

	private JTabbedPane _tabPane = new JTabbedPane();
	private SQLPanel _sqlPnl;
	private ObjectsPanel _objectsPnl;
	private JSplitPane _msgSplit;

	private boolean _hasBeenVisible = false;

	public SessionSheet(ISession session) {
		super(createTitle(session), true, true, true, true);
		_session = session;
		setVisible(false);

		createUserInterface();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		propertiesHaveChanged(null);

		session.getProperties().addPropertyChangeListener(_propsListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				propertiesHaveChanged(evt.getPropertyName());
			}
		});
	}

	/**
	 * Close this window.
	 */
	public void dispose() {
		if (_propsListener != null) {
			_session.getProperties().removePropertyChangeListener(_propsListener);
			_propsListener = null;
		}
		_objectsPnl.sessionEnding();
		_sqlPnl.sessionEnding();
		_session.getApplication().getPluginManager().sessionEnding(_session);
		closeConnection();
		super.dispose();
	}

	public boolean hasConnection() {
		return _session.getSQLConnection() != null;
	}

	public ISession getSession() {
		return _session;
	}

	public void refreshTree() throws BaseSQLException {
		_objectsPnl.refreshTree();
	}

	public void updateState() {
		ActionCollection actions = _session.getApplication().getActionCollection();
		final String tabTitle = _tabPane.getTitleAt(_tabPane.getSelectedIndex());
		if (tabTitle.equals(i18n.SQL_TAB_TITLE)) {
			actions.get(ExecuteSqlAction.class).setEnabled(true);
			boolean isAutoCommit = _session.getProperties().getAutoCommit();
			actions.get(CommitAction.class).setEnabled(!isAutoCommit);
			actions.get(RollbackAction.class).setEnabled(!isAutoCommit);
			actions.get(RefreshTreeAction.class).setEnabled(false);
		} else {
			actions.get(ExecuteSqlAction.class).setEnabled(false);
			actions.get(CommitAction.class).setEnabled(false);
			actions.get(RollbackAction.class).setEnabled(false);
			actions.get(RefreshTreeAction.class).setEnabled(true);
		}
	}

	public void replaceSQLEntryPanel(ISQLEntryPanel pnl) {
		_sqlPnl.replaceSQLEntryPanel(pnl);
	}

	/**
	 * Close all the Results frames.
	 */
	public void closeAllSQLResultFrames() {
		_sqlPnl.closeAllSQLResultFrames();
	}

	/**
	 * Close all the Results tabs.
	 */
	public void closeAllSQLResultTabs() {
		_sqlPnl.closeAllSQLResultTabs();
	}

	ObjectsPanel getObjectPanel() {
		return _objectsPnl;
	}

	DatabasePanel getDatabasePanel() {
		final IPlugin plugin = _session.getApplication().getDummyAppPlugin();
		return (DatabasePanel)_session.getPluginObject(plugin, ISession.ISessionKeys.DATABASE_DETAIL_PANEL_KEY);
	}

	TablePanel getTablePanel() {
		final IPlugin plugin = _session.getApplication().getDummyAppPlugin();
		return (TablePanel)_session.getPluginObject(plugin, ISession.ISessionKeys.TABLE_DETAIL_PANEL_KEY);
	}

	ProcedurePanel getProcedurePanel() {
		final IPlugin plugin = _session.getApplication().getDummyAppPlugin();
		return (ProcedurePanel)_session.getPluginObject(plugin, ISession.ISessionKeys.PROCEDURE_DETAIL_PANEL_KEY);
	}

	void closeConnection() {
		try {
			_session.closeSQLConnection();
		} catch (SQLException ex) {
			showError(ex);
		}
	}

	public void setVisible(boolean value) {
		super.setVisible(value);
		// Required under JDK1.2. Without it the divider location is reset to 0.
		if (!_hasBeenVisible && value == true) {
			_objectsPnl.fixDividerLocation();
			_msgSplit.setDividerLocation(0.9d);
			_hasBeenVisible = true;
		}
	}

	/**
	 * Select a tab in the main tabbed pane.
	 *
	 * @param	tabIndex   The tab to select. @see #IMainTabIndexes
	 *
	 * @throws  llegalArgumentException
	 *		  Thrown if an invalid <TT>tabIndex</TT> passed.
	 */
	public void selectMainTab(int tabIndex) throws IllegalArgumentException {
		if (tabIndex >= _tabPane.getTabCount()) {
			throw new IllegalArgumentException(
				"" + tabIndex + " is not a valid index into the main tabbed pane.");
		}

		if (_tabPane.getSelectedIndex() != tabIndex) {
			_tabPane.setSelectedIndex(tabIndex);
		}
	}

	/**
	 * Add a tab to the main tabbed panel.
	 *
	 * title	The title to display in the tab.
	 * icon	 The icon to display in the tab. If <TT>null</TT> then no icon displayed.
	 * comp	 The component to be shown when the tab is active.
	 * tip	  The tooltip to be displayed for the tab. Can be <TT>null</TT>.
	 *
	 * @throws  IllegalArgumentException
	 *		  If <TT>title</TT> or <TT>comp</TT> is <TT>null</TT>.
	 */
	public void addMainTab(String title, Icon icon, Component comp, String tip)
			throws IllegalArgumentException {
		if (title == null) {
			throw new IllegalArgumentException("Null title passed");
		}
		if (comp == null) {
			throw new IllegalArgumentException("Null Component passed");
		}
		_tabPane.addTab(title, icon, comp, tip);
	}

	String getEntireSQLScript() {
		return _sqlPnl.getEntireSQLScript();
	}

	void setEntireSQLScript(String sqlScript) {
		_sqlPnl.setEntireSQLScript(sqlScript);
	}

	void appendSQLScript(String sqlScript) {
		_sqlPnl.appendSQLScript(sqlScript);
	}

	SQLPanel getSQLPanel() {
		return _sqlPnl;
	}

	void executeCurrentSQL() {
		_sqlPnl.executeCurrentSQL();
	}

	private static String createTitle(ISession session) {
		StringBuffer title = new StringBuffer();//"Session: ");
		title.append(session.getAlias().getName());
		String user = null;
		try {
			user = session.getSQLConnection().getUserName();
		} catch (BaseSQLException ex) {
			s_log.error("Error occured retrieving user name from Connection", ex);
		}
		if (user != null && user.length() > 0) {
			title.append(" as ").append(user); // i18n
		}
		return title.toString();
	}

	private void showError(Exception ex) {
		new ErrorDialog(_session.getApplication().getMainFrame(), ex).show();
	}

	private void propertiesHaveChanged(String propertyName) {
		SessionProperties props = _session.getProperties();
		if (propertyName == null
			|| propertyName.equals(
				SessionProperties.IPropertyNames.COMMIT_ON_CLOSING_CONNECTION)) {
			_session.getSQLConnection().setCommitOnClose(
				props.getCommitOnClosingConnection());
		}
		updateState();
	}

	private void createUserInterface() {
		setVisible(false);
		Icon icon =
			_session.getApplication().getResources().getIcon(getClass(), "frameIcon");
		//i18n
		if (icon != null) {
			setFrameIcon(icon);
		}

		_sqlPnl = new SQLPanel(_session);
		_objectsPnl = new ObjectsPanel(_session);

		_tabPane.addTab(i18n.OBJ_TAB_TITLE, null, _objectsPnl, i18n.OBJ_TAB_DESC);
		_tabPane.addTab(i18n.SQL_TAB_TITLE, null, _sqlPnl, i18n.SQL_TAB_DESC);

		_tabPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				updateState();
			}
		});

		Container content = getContentPane();
		content.setLayout(new BorderLayout());
		content.add(new MyToolBar(this), BorderLayout.NORTH);

		MessagePanel msgPnl = new MessagePanel(_session.getApplication());
		_session.setMessageHandler(msgPnl);
		msgPnl.setEditable(false);
		msgPnl.setRows(4);

		_msgSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		_msgSplit.setOneTouchExpandable(true);
		_msgSplit.add(_tabPane, JSplitPane.LEFT);
		_msgSplit.add(new JScrollPane(msgPnl), JSplitPane.RIGHT);
		content.add(_msgSplit, BorderLayout.CENTER);

		// This is to fix a problem with the JDK (up to version 1.3)
		// where focus events were not generated correctly. The sympton
		// is being unable to key into the text entry field unless you click
		// elsewhere after focus is gained by the internal frame.
		// See bug ID 4309079 on the JavaSoft bug parade (plus others).
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameActivated(InternalFrameEvent evt) {
				Window window = SwingUtilities.windowForComponent(SessionSheet.this._sqlPnl);
				Component focusOwner = (window != null) ? window.getFocusOwner() : null;
				if (focusOwner != null) {
					FocusEvent lost = new FocusEvent(focusOwner, FocusEvent.FOCUS_LOST);
					FocusEvent gained = new FocusEvent(focusOwner, FocusEvent.FOCUS_GAINED);
					window.dispatchEvent(lost);
					window.dispatchEvent(gained);
					window.dispatchEvent(lost);
					focusOwner.requestFocus();
				}
			}
		});

		validate();
	}

	private class MyToolBar extends ToolBar {
		MyToolBar(SessionSheet frame) {
			super();
			ActionCollection actions = _session.getApplication().getActionCollection();
			setUseRolloverButtons(true);
			setFloatable(false);
			add(actions.get(SessionPropertiesAction.class));
			add(actions.get(RefreshTreeAction.class));
			addSeparator();
			add(actions.get(ExecuteSqlAction.class));
			addSeparator();
			add(actions.get(CommitAction.class));
			add(actions.get(RollbackAction.class));

			actions.get(ExecuteSqlAction.class).setEnabled(false);
			actions.get(CommitAction.class).setEnabled(false);
			actions.get(RollbackAction.class).setEnabled(false);
		}
	}
}
