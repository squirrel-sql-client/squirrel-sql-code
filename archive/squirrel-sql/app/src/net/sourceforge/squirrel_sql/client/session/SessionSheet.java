package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.sql.SQLException;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
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
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.BaseSheet;
import net.sourceforge.squirrel_sql.client.session.action.CommitAction;
import net.sourceforge.squirrel_sql.client.session.action.ExecuteSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshObjectTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.RollbackAction;
import net.sourceforge.squirrel_sql.client.session.action.SessionPropertiesAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class SessionSheet extends BaseSheet
{
	/** Logger for this class. */
	private static ILogger s_log =
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
	 * Close this window.
	 */
	public void dispose()
	{
		if (_propsListener != null)
		{
			_session.getProperties().removePropertyChangeListener(
				_propsListener);
			_propsListener = null;
		}
		_mainTabPane.sessionClosing(_session);
		_session.getApplication().getPluginManager().sessionEnding(_session);
		closeConnection();
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

	public void replaceSQLEntryPanel(ISQLEntryPanel pnl)
	{
		_mainTabPane.getSQLPanel().replaceSQLEntryPanel(pnl);
	}

	ObjectTreePanel getObjectTreePanel()
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
	 * @param	tabIndex   The tab to select. @see #IMainTabIndexes
	 *
	 * @throws  llegalArgumentException
	 *		  Thrown if an invalid <TT>tabIndex</TT> passed.
	 */
	public void selectMainTab(int tabIndex)
	{
		if (tabIndex >= _mainTabPane.getTabCount())
		{
			throw new IllegalArgumentException("" + tabIndex
					+ " is not a valid index into the main tabbed pane.");
		}
		if (_mainTabPane.getSelectedIndex() != tabIndex)
		{
			_mainTabPane.setSelectedIndex(tabIndex);
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

	ISQLEntryPanel getSQLEntryPanel()
	{
		return getSQLPanel().getSQLEntryPanel();
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
						_toolBar = new MyToolBar(_session, this);
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

	private void createUserInterface()
	{
		setVisible(false);
		SessionProperties props = _session.getProperties();
		final IApplication app = _session.getApplication();
		Icon icon = app.getResources().getIcon(getClass(), "frameIcon"); //i18n
		if (icon != null)
		{
			setFrameIcon(icon);
		}
		_mainTabPane = new MainPanel(_session);
		Container content = getContentPane();
		content.setLayout(new BorderLayout());
		if (props.getShowToolBar())
		{
			_toolBar = new MyToolBar(_session, this);
			content.add(_toolBar, BorderLayout.NORTH);
		}
		MessagePanel msgPnl = new MessagePanel(app);
		_session.setMessageHandler(msgPnl);
		msgPnl.setEditable(false);
		//		msgPnl.setRows(4);
		_msgSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		_msgSplit.setOneTouchExpandable(true);
		_msgSplit.add(_mainTabPane, JSplitPane.LEFT);
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
				Window window =
					SwingUtilities.windowForComponent(
						SessionSheet.this.getSQLPanel());
				Component focusOwner =
					(window != null) ? window.getFocusOwner() : null;
				if (focusOwner != null)
				{
					FocusEvent lost =
						new FocusEvent(focusOwner, FocusEvent.FOCUS_LOST);
					FocusEvent gained =
						new FocusEvent(focusOwner, FocusEvent.FOCUS_GAINED);
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
		MyToolBar(IClientSession session, SessionSheet frame)
		{
			super();
			// If DBMS supports catalogs then place combo box of catalogs
			// in toolbar.
			SQLConnection conn = session.getSQLConnection();
			SQLCatalogsComboBox catalogsCmb = null;
			try
			{
				if (conn.getSQLMetaData().supportsCatalogs())
				{
					catalogsCmb = new SQLCatalogsComboBox();
					catalogsCmb.setConnection(conn);
					catalogsCmb.setSelectedCatalog(conn.getCatalog());
					catalogsCmb.addActionListener(new CatalogsComboListener());
					add(new JLabel(" Catalog: "));
					add(catalogsCmb);
					addSeparator();
				}
			}
			catch (SQLException ex)
			{
				s_log.error("Unable to retrieve catalog info", ex);
			}

			// Listener for changes in the connection status.
			conn.addPropertyChangeListener(new SQLConnectionListener(catalogsCmb));
			ActionCollection actions =
				session.getApplication().getActionCollection();
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
		}
	}

	private final class CatalogsComboListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
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
			final String propName = evt.getPropertyName();

			if (propName == null ||
					propName.equals(SQLConnection.IPropertyNames.CATALOG))
			{
				if (_cmb != null)
				{
					final SQLConnection conn = _session.getSQLConnection();
					try
					{
						_cmb.setSelectedCatalog(conn.getCatalog());
					}
					catch (SQLException ex)
					{
						_session.getMessageHandler().showErrorMessage(ex);
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
