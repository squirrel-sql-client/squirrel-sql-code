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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import net.sourceforge.squirrel_sql.fw.gui.SQLCatalogsComboBox;
import net.sourceforge.squirrel_sql.fw.gui.StatusBar;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
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

public class SessionSheet extends JPanel
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(SessionSheet.class);

	/** Application API. */
	private final IApplication _app;

	/** Session for this window. */
//	private ISession _session;

	/** ID of the session for this window. */
	private IIdentifier _sessionId;

	/** Listener to the sessions properties. */
	private PropertyChangeListener _propsListener;

	private MainPanel _mainTabPane;
	private JSplitPane _msgSplit;

	/** Toolbar for window. */
	private MyToolBar _toolBar;

	private StatusBar _statusBar = new StatusBar();
	private boolean _hasBeenVisible;

	private boolean _buildingListOfCatalogs = false;

	public SessionSheet(ISession session)
	{
		super(new BorderLayout());

		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		_app = session.getApplication();
		_sessionId = session.getIdentifier();
		createGUI(session);
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
			getSession().getObjectTreeAPI(_app.getDummyAppPlugin()).refreshTree();
		}
	}

	public boolean hasConnection()
	{
		return getSession().getSQLConnection() != null;
	}

	/**
	 * Retrieve the session attached to this window.
	 *
	 * @return	the session attached to this window.
	 */
	public ISession getSession()
	{
		return (ISession)_app.getSessionManager().getSession(_sessionId);
	}

	public void updateState()
	{
		_mainTabPane.updateState();
	}

	void sessionHasClosed()
	{
		final ISession session = getSession();
		if (session != null)
		{
			if (_propsListener != null)
			{
				session.getProperties().removePropertyChangeListener(_propsListener);
				_propsListener = null;
			}
			_mainTabPane.sessionClosing(session);
//			try
//			{
//				_app.getSessionManager().closeSession(session);
//			}
//			catch (SQLException ex)
//			{
//				final String msg = "Error closing session";
//				_app.showErrorDialog(msg, ex);
//				s_log.error(msg, ex);
//			}

//			session.setSessionSheet(null);

			_sessionId = null;
		}
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
			getSession().closeSQLConnection();
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

	private void showError(Exception ex)
	{
		_app.showErrorDialog(ex);
	}

	private void propertiesHaveChanged(String propertyName)
	{
		final ISession session = getSession();
		final SessionProperties props = session.getProperties();
		if (propertyName == null
			|| propertyName.equals(
				SessionProperties.IPropertyNames.COMMIT_ON_CLOSING_CONNECTION))
		{
			session.getSQLConnection().setCommitOnClose(
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
						_toolBar = new MyToolBar(session);
						add(_toolBar, BorderLayout.NORTH);
					}
				}
				else
				{
					if (_toolBar != null)
					{
						remove(_toolBar);
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

	private void createGUI(ISession session)
	{
//		setVisible(false);
//		SessionProperties props = _session.getProperties();
		final IApplication app = session.getApplication();

		_mainTabPane = new MainPanel(session);

		MessagePanel msgPnl = new MessagePanel(app);
		session.setMessageHandler(msgPnl);
		msgPnl.setEditable(false);
		_msgSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		_msgSplit.setOneTouchExpandable(true);
		_msgSplit.add(_mainTabPane, JSplitPane.LEFT);
		_msgSplit.add(new JScrollPane(msgPnl), JSplitPane.RIGHT);
		add(_msgSplit, BorderLayout.CENTER);

		Font fn = app.getFontInfoStore().getStatusBarFontInfo().createFont();
		_statusBar.setFont(fn);
		add(_statusBar, BorderLayout.SOUTH);

		getObjectTreePanel().addTreeSelectionListener(new ObjectTreeSelectionListener());

		validate();
	}

	private class MyToolBar extends ToolBar
	{
		private SQLCatalogsComboBox _catalogsCmb;
		private IObjectTreeListener _lis;

		MyToolBar(ISession session)
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
				SQLConnection conn = getSession().getSQLConnection();
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
				SQLConnection conn = getSession().getSQLConnection();
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
							getSession().getSQLConnection().setCatalog(catalog);
						}
						catch (SQLException ex)
						{
							getSession().getMessageHandler().showErrorMessage(ex);
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
						final SQLConnection conn = getSession().getSQLConnection();
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
							getSession().getMessageHandler().showErrorMessage(ex);
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
