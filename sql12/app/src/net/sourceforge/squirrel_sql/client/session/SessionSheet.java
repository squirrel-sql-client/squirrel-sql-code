package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.Container;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.sql.SQLException;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import net.sourceforge.squirrel_sql.fw.gui.SQLCatalogsComboBox;
import net.sourceforge.squirrel_sql.fw.gui.StatusBar;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.BaseSheet;
import net.sourceforge.squirrel_sql.client.session.action.CommitAction;
import net.sourceforge.squirrel_sql.client.session.action.ExecuteSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshObjectTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.RollbackAction;
import net.sourceforge.squirrel_sql.client.session.action.SQLFilterAction;
import net.sourceforge.squirrel_sql.client.session.action.SessionPropertiesAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.IObjectTreeListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeAdapter;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeListenerEvent;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class SessionSheet extends BaseSheet
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(SessionSheet.class);

	/** Session for this window. */
	private IClientSession _session;

	/** Listener to the sessions properties. */
	private PropertyChangeListener _propsListener;

	private MainPanel _mainTabPane;
	private JSplitPane _msgSplit;

	/** Toolbar for window. */
	private MyToolBar _toolBar;

	private StatusBar _statusBar = new StatusBar();
	private boolean _hasBeenVisible;

	private boolean _buildingListOfCatalogs = false;

	public SessionSheet(IClientSession session)
	{
		super(createTitle(session), true, true, true, true);
		_session = session;
		setVisible(false);
		createUserInterface();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		propertiesHaveChanged(null);

		_propsListener = new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				propertiesHaveChanged(evt.getPropertyName());
			}
		};
		session.getProperties().addPropertyChangeListener(_propsListener);
	}

	/**
	 * Dispose of this window.
	 */
	public void dispose()
	{
		final IApplication app = _session.getApplication();
		if (_propsListener != null)
		{
			_session.getProperties().removePropertyChangeListener(_propsListener);
			_propsListener = null;
		}
		_mainTabPane.sessionClosing(_session);
		try
		{
			app.getSessionManager().closeSession(_session);
		}
		catch (SQLException ex)
		{
			final String msg = "Error closing session";
			app.showErrorDialog(msg, ex);
			s_log.error(msg, ex);
		}
		_session.setSessionSheet(null);
		super.dispose();
	}

	public void setVisible(boolean value)
	{
		super.setVisible(value);
		if (!_hasBeenVisible && value == true)
		{
			_hasBeenVisible = true;
			_msgSplit.setDividerLocation(0.9d);
			_msgSplit.setResizeWeight(1.0);

			// Done this late so that plugins have time to register expanders
			// with the object tree prior to it being built.
			_session.getObjectTreeAPI(_session.getApplication().getDummyAppPlugin()).refreshTree();
		}
	}

	public void setSelected(boolean selected)
			throws PropertyVetoException
	{
		super.setSelected(selected);

		// Without this when using alt left/right to move
		// between sessions the focus is left in the SQL
		// entry area of the previous session.
		if (selected)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					getSQLEntryPanel().requestFocus();
				}
			});
		}
	}

	public boolean hasConnection()
	{
		return _session.getSQLConnection() != null;
	}

	public IClientSession getSession()
	{
		return _session;
	}

	public void updateState()
	{
		_mainTabPane.updateState();
	}

	void installSQLEntryPanel(ISQLEntryPanel pnl)
	{
		_mainTabPane.getSQLPanel().installSQLEntryPanel(pnl);
	}

	/*
	 * TODO: This should not be public. Check all usages of it
	 * and put appropriate methods in an API object.
	 */
	public ObjectTreePanel getObjectTreePanel()
	{
		return _mainTabPane.getObjectTreePanel();
	}

	void closeConnection()
	{
		try
		{
			_session.closeSQLConnection();
		}
		catch (SQLException ex)
		{
			showError(ex);
		}
	}

	/**
	 * Select a tab in the main tabbed pane.
	 *
	 * @param	tabIndex	The tab to select. @see #IMainTabIndexes
	 *
	 * @throws	llegalArgumentException
	 *			Thrown if an invalid <TT>tabIndex</TT> passed.
	 */
	public void selectMainTab(int tabIndex)
	{
		final JTabbedPane tabPnl = _mainTabPane.getTabbedPane();
		if (tabIndex >= tabPnl.getTabCount())
		{
			throw new IllegalArgumentException("" + tabIndex
					+ " is not a valid index into the main tabbed pane.");
		}
		if (tabPnl.getSelectedIndex() != tabIndex)
		{
			tabPnl.setSelectedIndex(tabIndex);
		}
	}

	/**
	 * Add a tab to the main tabbed panel.
	 *
	 * tab	Describes the tab to be added.
	 *
	 * @throws	IllegalArgumentException
	 *			If <TT>tab</TT> is <TT>null</TT>.
	 */
	public void addMainTab(IMainPanelTab tab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("IMainPanelTab == null");
		}
		_mainTabPane.addMainPanelTab(tab);
	}

	public void setStatusBarMessage(String msg)
	{
		_statusBar.setText(msg);
	}

	SQLPanel getSQLPanel()
	{
		return _mainTabPane.getSQLPanel();
	}

	/**
	 * TODO: This shouldn't be public. Its only been done for the JComplete
	 * plugin. At some stage this method will be returned to package visibility.
	 */
	public ISQLEntryPanel getSQLEntryPanel()
	{
		return getSQLPanel().getSQLEntryPanel();
	}

	/**
	 * Add component to the session sheets status bar.
	 *
	 * @param	comp	Component to add.
	 */
	public void addToStatusBar(JComponent comp)
	{
		_statusBar.addJComponent(comp);
	}

	/**
	 * Remove component from the session sheets status bar.
	 *
	 * @param	comp	Component to remove.
	 */
	public void removeFromStatusBar(JComponent comp)
	{
		_statusBar.remove(comp);
	}

	private static String createTitle(IClientSession session)
	{
		StringBuffer title = new StringBuffer();
		title.append(session.getAlias().getName());
		String user = null;
		try
		{
			user = session.getSQLConnection().getSQLMetaData().getUserName();
		}
		catch (SQLException ex)
		{
			s_log.error("Error occured retrieving user name from Connection", ex);
		}
		if (user != null && user.length() > 0)
		{
			title.append(" as ").append(user); // i18n
		}
		return title.toString();
	}

	private void showError(Exception ex)
	{
		_session.getApplication().showErrorDialog(ex);
	}

	private void propertiesHaveChanged(String propertyName)
	{
		SessionProperties props = _session.getProperties();
		if (propertyName == null
			|| propertyName.equals(
				SessionProperties.IPropertyNames.COMMIT_ON_CLOSING_CONNECTION))
		{
			_session.getSQLConnection().setCommitOnClose(
				props.getCommitOnClosingConnection());
		}
		if (propertyName == null
			|| propertyName.equals(
				SessionProperties.IPropertyNames.SHOW_TOOL_BAR))
		{
			boolean show = props.getShowToolBar();
			if (show != (_toolBar != null))
			{
				if (show)
				{
					if (_toolBar == null)
					{
						_toolBar = new MyToolBar(_session);
						getContentPane().add(_toolBar, BorderLayout.NORTH);
					}
				}
				else
				{
					if (_toolBar != null)
					{
						getContentPane().remove(_toolBar);
						_toolBar = null;
					}
				}
			}
		}
		updateState();
	}

	private void setupCatalogsCombo()
	{
		if (_toolBar != null)
		{
			_toolBar.setupCatalogsCombo();
		}
	}

	private void createUserInterface()
	{
		setVisible(false);
//		SessionProperties props = _session.getProperties();
		final IApplication app = _session.getApplication();
		Icon icon = app.getResources().getIcon(getClass(), "frameIcon"); //i18n
		if (icon != null)
		{
			setFrameIcon(icon);
		}

		_mainTabPane = new MainPanel(_session);
		Container content = getContentPane();
		content.setLayout(new BorderLayout());

		MessagePanel msgPnl = new MessagePanel(app);
		_session.setMessageHandler(msgPnl);
		msgPnl.setEditable(false);
		_msgSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		_msgSplit.setOneTouchExpandable(true);
		_msgSplit.add(_mainTabPane.getTabbedPane(), JSplitPane.LEFT);
		_msgSplit.add(new JScrollPane(msgPnl), JSplitPane.RIGHT);
		content.add(_msgSplit, BorderLayout.CENTER);

		// This is to fix a problem with the JDK (up to version 1.3)
		// where focus events were not generated correctly. The sympton
		// is being unable to key into the text entry field unless you click
		// elsewhere after focus is gained by the internal frame.
		// See bug ID 4309079 on the JavaSoft bug parade (plus others).
		addInternalFrameListener(new InternalFrameAdapter()
		{
			public void internalFrameActivated(InternalFrameEvent evt)
			{
				Window window = SwingUtilities.windowForComponent(
										SessionSheet.this.getSQLPanel());
				Component focusOwner = (window != null)
											? window.getFocusOwner() : null;
				if (focusOwner != null)
				{
					FocusEvent lost = new FocusEvent(focusOwner, FocusEvent.FOCUS_LOST);
					FocusEvent gained = new FocusEvent(focusOwner, FocusEvent.FOCUS_GAINED);
					window.dispatchEvent(lost);
					window.dispatchEvent(gained);
					window.dispatchEvent(lost);
					focusOwner.requestFocus();
				}
			}
		});
		Font fn = app.getFontInfoStore().getStatusBarFontInfo().createFont();
		_statusBar.setFont(fn);
		content.add(_statusBar, BorderLayout.SOUTH);

		getObjectTreePanel().addTreeSelectionListener(new ObjectTreeSelectionListener());

		validate();
	}

	private class MyToolBar extends ToolBar
	{
		private SQLCatalogsComboBox _catalogsCmb;
		private IObjectTreeListener _lis;

		MyToolBar(IClientSession session)
		{
			super();
			createGUI(session);
		}

		public void addNotify()
		{
			super.addNotify();

			// Whenever object tree refreshed refresh list of catalogs.
			if (_catalogsCmb != null && _lis == null)
			{
				_lis = new ObjectTreeAdapter()
				{
					public void objectTreeRefreshed(ObjectTreeListenerEvent evt)
					{
						setupCatalogsCombo();
					}
				};
				getObjectTreePanel().addObjectTreeListener(_lis);
			}
		}

		public void removeNotify()
		{
			super.removeNotify();
			if (_lis != null)
			{
				getObjectTreePanel().removeObjectTreeListener(_lis);
				_lis = null;
			}
		}

		private void createGUI(ISession session)
		{
			// If DBMS supports catalogs then place combo box of catalogs
			// in toolbar.
			try
			{
				SQLConnection conn = _session.getSQLConnection();
				if (conn.getSQLMetaData().supportsCatalogs())
				{
					_catalogsCmb = new SQLCatalogsComboBox();
					add(new JLabel(" Catalog: "));
					add(_catalogsCmb);
					addSeparator();

					// Listener for changes in the connection status.
					conn.addPropertyChangeListener(new SQLConnectionListener(_catalogsCmb));

					_catalogsCmb.addActionListener(new CatalogsComboListener());
				}
			}
			catch (SQLException ex)
			{
				s_log.error("Unable to retrieve catalog info", ex);
			}

			ActionCollection actions = session.getApplication().getActionCollection();
			setUseRolloverButtons(true);
			setFloatable(false);
			add(actions.get(SessionPropertiesAction.class));
			add(actions.get(RefreshObjectTreeAction.class));
			addSeparator();
			add(actions.get(ExecuteSqlAction.class));
			addSeparator();
			add(actions.get(CommitAction.class));
			add(actions.get(RollbackAction.class));
			actions.get(ExecuteSqlAction.class).setEnabled(false);
			actions.get(CommitAction.class).setEnabled(false);
			actions.get(RollbackAction.class).setEnabled(false);
			addSeparator();
			add(actions.get(SQLFilterAction.class));
			actions.get(SQLFilterAction.class).setEnabled(false);
		}

		private void setupCatalogsCombo()
		{
			try
			{
				SQLConnection conn = _session.getSQLConnection();
				try
				{
					_buildingListOfCatalogs = true;
					_catalogsCmb.setConnection(conn);
				}
				finally
				{
					_buildingListOfCatalogs = false;
				}
			}
			catch (SQLException ex)
			{
				s_log.error("Unable to retrieve catalog info", ex);
			}
		}
	}

	private final class CatalogsComboListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			if (!_buildingListOfCatalogs)
			{
				Object src = evt.getSource();
				if (src instanceof SQLCatalogsComboBox)
				{
					SQLCatalogsComboBox cmb = (SQLCatalogsComboBox)src;
					String catalog = cmb.getSelectedCatalog();
					if (catalog != null)
					{
						try
						{
							_session.getSQLConnection().setCatalog(catalog);
						}
						catch (SQLException ex)
						{
							_session.getMessageHandler().showErrorMessage(ex);
							SessionSheet.this.setupCatalogsCombo();
						}
					}
				}
			}
		}
	}

	private final class SQLConnectionListener implements PropertyChangeListener
	{
		private SQLCatalogsComboBox _cmb;

		SQLConnectionListener(SQLCatalogsComboBox cmb)
		{
			super();
			_cmb = cmb;
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			if (!_buildingListOfCatalogs)
			{
				final String propName = evt.getPropertyName();
				if (propName == null ||
						propName.equals(SQLConnection.IPropertyNames.CATALOG))
				{
					if (_cmb != null)
					{
						final SQLConnection conn = _session.getSQLConnection();
						try
						{
							if (!Utilities.areStringsEqual(conn.getCatalog(),
														_cmb.getSelectedCatalog()))
							{
								_cmb.setSelectedCatalog(conn.getCatalog());
							}
						}
						catch (SQLException ex)
						{
							_session.getMessageHandler().showErrorMessage(ex);
						}
					}
				}
			}
		}
	}

	private final class ObjectTreeSelectionListener implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent evt)
		{
			final TreePath selPath = evt.getNewLeadSelectionPath();
			if (selPath != null)
			{
				StringBuffer buf = new StringBuffer();
				Object[] fullPath = selPath.getPath();
				for (int i = 0; i < fullPath.length; ++i)
				{
					if (fullPath[i] instanceof ObjectTreeNode)
					{
						ObjectTreeNode node = (ObjectTreeNode)fullPath[i];
						buf.append('/').append(node.toString());
					}
				}
				setStatusBarMessage(buf.toString());
			}
		}
	}
}
