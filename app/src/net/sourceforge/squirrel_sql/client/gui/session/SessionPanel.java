package net.sourceforge.squirrel_sql.client.gui.session;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
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
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.*;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.*;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.SQLCatalogsComboBox;
import net.sourceforge.squirrel_sql.fw.gui.StatusBar;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.Vector;

public class SessionPanel extends JPanel
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(SessionPanel.class);

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SessionPanel.class);

	/** Application API. */
	private final IApplication _app;

	/** ID of the session for this window. */
	private IIdentifier _sessionId;

	/** Listener to the sessions properties. */
	private PropertyChangeListener _propsListener;

	private MainPanel _mainTabPane;
//	private JSplitPane _msgSplit;

	/** Toolbar for window. */
	private MyToolBar _toolBar;

	private Vector _externallyAddedToolbarActionsAndSeparators = new Vector();

	private StatusBar _statusBar = new StatusBar();
	private boolean _hasBeenVisible;

	private boolean _buildingListOfCatalogs = false;

	private ObjectTreeSelectionListener _objTreeSelectionLis = null;

   public SessionPanel(ISession session)
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

   public void addToToolsPopUp(String selectionString, Action action)
   {
      getSQLPaneAPI().addToToolsPopUp(selectionString, action);
   }


//	public void setVisible(boolean value)
//	{
	public void addNotify()
	{
//		super.setVisible(value);
		super.addNotify();
//		if (!_hasBeenVisible && value == true)
		if (!_hasBeenVisible)
		{
			_hasBeenVisible = true;
//			_msgSplit.setDividerLocation(0.9d);
//			_msgSplit.setResizeWeight(1.0);

			// Done this late so that plugins have time to register expanders
			// with the object tree prior to it being built.
//			getSession().getObjectTreeAPI(_app.getDummyAppPlugin()).refreshTree();
			_mainTabPane.getObjectTreePanel().refreshTree();
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
		return _app.getSessionManager().getSession(_sessionId);
	}

	public void sessionHasClosed()
	{
		if (_objTreeSelectionLis != null)
		{
			getObjectTreePanel().removeTreeSelectionListener(_objTreeSelectionLis);
			_objTreeSelectionLis = null;
		}

		final ISession session = getSession();
		if (session != null)
		{
			if (_propsListener != null)
			{
				session.getProperties().removePropertyChangeListener(_propsListener);
				_propsListener = null;
			}
			_mainTabPane.sessionClosing(session);
			_sessionId = null;
		}
	}

   public void sessionWindowClosing()
   {
      _mainTabPane.sessionWindowClosing();
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
    * @return The index of th added tab.
	 *
	 * @throws	IllegalArgumentException
	 *			If <TT>tab</TT> is <TT>null</TT>.
	 */
	public int addMainTab(IMainPanelTab tab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("IMainPanelTab == null");
		}
		return _mainTabPane.addMainPanelTab(tab);
	}

	public void insertMainTab(IMainPanelTab tab, int idx)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null IMainPanelTab passed");
		}
		if(idx == MainPanel.ITabIndexes.SQL_TAB || idx == MainPanel.ITabIndexes.OBJECT_TREE_TAB)
		{
			throw new IllegalArgumentException("Index " + idx + "conflicts with standard tabs");
		}

		_mainTabPane.insertMainPanelTab(tab, idx);
	}

	public int removeMainTab(IMainPanelTab tab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null IMainPanelTab passed");
		}
		return _mainTabPane.removeMainPanelTab(tab);
	}

	public void setStatusBarMessage(final String msg)
	{
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				_statusBar.setText(msg);
			}
		});
	}

	SQLPanel getSQLPanel()
	{
		return _mainTabPane.getSQLPanel();
	}

	public ISQLPanelAPI getSQLPaneAPI()
	{
		return _mainTabPane.getSQLPanel().getSQLPanelAPI();
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
	 * Add the passed action to the session toolbar.
	 *
	 * @param	action	Action to be added.
	 */
	public synchronized void addToToolbar(Action action)
	{
		_externallyAddedToolbarActionsAndSeparators.add(action);
		if (null != _toolBar)
		{
			_toolBar.add(action);
		}
	}

   public synchronized void addSeparatorToToolbar()
   {
      _externallyAddedToolbarActionsAndSeparators.add(new SeparatorMarker());
      if (null != _toolBar)
      {
         _toolBar.addSeparator();
      }
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
            _app.getThreadPool().addTask(new Runnable() {
                public void run() {
                    session.getSQLConnection().setCommitOnClose(
                            props.getCommitOnClosingConnection());                    
                }
            });
		}
		if (propertyName == null
			|| propertyName.equals(
				SessionProperties.IPropertyNames.SHOW_TOOL_BAR))
		{
			synchronized(this)
			{
				boolean show = props.getShowToolBar();
				if (show != (_toolBar != null))
				{
					if (show)
					{
						if (_toolBar == null)
						{
							_toolBar = new MyToolBar(session);
							for (int i = 0; i < _externallyAddedToolbarActionsAndSeparators.size(); i++)
							{
                        if(_externallyAddedToolbarActionsAndSeparators.get(i) instanceof Action)
                        {
								   _toolBar.add((Action)_externallyAddedToolbarActionsAndSeparators.get(i));
                        }
                        else
                        {
                           _toolBar.addSeparator();
                        }
							}

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
		}
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
		final IApplication app = session.getApplication();

		_mainTabPane = new MainPanel(session);

		add(_mainTabPane, BorderLayout.CENTER);

		Font fn = app.getFontInfoStore().getStatusBarFontInfo().createFont();
		_statusBar.setFont(fn);
		add(_statusBar, BorderLayout.SOUTH);

		_objTreeSelectionLis = new ObjectTreeSelectionListener();
		getObjectTreePanel().addTreeSelectionListener(_objTreeSelectionLis);

		RowColumnLabel lblRowCol = new RowColumnLabel(_mainTabPane.getSQLPanel().getSQLEntryPanel());
		addToStatusBar(lblRowCol);
		validate();
	}

   public boolean isSQLTabSelected()
   {
      return MainPanel.ITabIndexes.SQL_TAB ==_mainTabPane.getTabbedPane().getSelectedIndex();
   }

   public boolean isObjectTreeTabSelected()
   {
      return MainPanel.ITabIndexes.OBJECT_TREE_TAB ==_mainTabPane.getTabbedPane().getSelectedIndex();
   }

   private class MyToolBar extends ToolBar
	{
		private SQLCatalogsComboBox _catalogsCmb;
		private IObjectTreeListener _lis;

		MyToolBar(final ISession session)
		{
			super();
            session.getApplication().getThreadPool().addTask(new Runnable() {                
                public void run() {
                    final String[] catalogs = getCatalogs(session);
                    final String selected = getCatalog(session);
                    final SQLConnectionListener listener = new SQLConnectionListener();
                    
                    // Listener for changes in the connection status.
                    session.getSQLConnection().addPropertyChangeListener(listener);                

                    GUIUtils.processOnSwingEventThread(new Runnable() {
                        public void run() {
                            createGUI(session, listener, catalogs, selected);
                        }
                    });
                }
            }); 
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

		private void createGUI(ISession session,
                               SQLConnectionListener listener,
                               String[] catalogs, 
                               String selected)
		{
			if (catalogs != null)
			{
				_catalogsCmb = new SQLCatalogsComboBox();
				add(new JLabel(s_stringMgr.getString("SessionPanel.catalog")));
				add(_catalogsCmb);
				addSeparator();
				_catalogsCmb.setCatalogs(catalogs, selected);
                _catalogsCmb.addActionListener(new CatalogsComboListener());
                listener.setSQLCatalogsComboBox(_catalogsCmb);
			}

			ActionCollection actions = session.getApplication().getActionCollection();
			setUseRolloverButtons(true);
			setFloatable(false);
			add(actions.get(SessionPropertiesAction.class));
			add(actions.get(RefreshObjectTreeAction.class));
			addSeparator();
			add(actions.get(ExecuteSqlAction.class));
			addSeparator();
//			actions.get(ExecuteSqlAction.class).setEnabled(false);
			add(actions.get(SQLFilterAction.class));
//			actions.get(SQLFilterAction.class).setEnabled(false);
            addSeparator();
            add(actions.get(FileNewAction.class));
            add(actions.get(FileOpenAction.class));
            add(actions.get(FileAppendAction.class));
            add(actions.get(FileSaveAction.class));
            add(actions.get(FileSaveAsAction.class));
            add(actions.get(FileCloseAction.class));
		}

        private boolean supportsCatalogs(ISession session) {
            boolean result = false;
            try {
                result = 
                    session.getSQLConnection().getSQLMetaData().supportsCatalogs();
            } catch (SQLException ex) {
                // i18n[SessionPanel.error.retrievecatalog=Unable to retrieve catalog info]
                s_log.error(s_stringMgr.getString("SessionPanel.error.retrievecatalog"), ex);
            }
            return result;
        }
        
        private String[] getCatalogs(ISession session) {
            String[] result = null;
            if (!supportsCatalogs(session)) {
                return null;
            }
            try {
                result = session.getSQLConnection().getSQLMetaData().getCatalogs();
            } catch (SQLException ex) {
                // i18n[SessionPanel.error.retrievecatalog=Unable to retrieve catalog info]
                s_log.error(s_stringMgr.getString("SessionPanel.error.retrievecatalog"), ex);                
            }
            return result;
        }
        
        private String getCatalog(ISession session) {
            String result = null;
            if (!supportsCatalogs(session)) {
                return null;
            }
            try {
                result = session.getSQLConnection().getCatalog();
            } catch (SQLException ex) {
                // i18n[SessionPanel.error.retrievecatalog=Unable to retrieve catalog info]
                s_log.error(s_stringMgr.getString("SessionPanel.error.retrievecatalog"), ex);                                
            }
            return result;
        }
        
		private void setupCatalogsCombo()
		{
			try
			{
                final ISession session = getSession();
                session.getApplication().getThreadPool().addTask(new Runnable() {
                    public void run() {
                        final String[] catalogs = getCatalogs(session);
                        final String selected = getCatalog(session);
                        GUIUtils.processOnSwingEventThread(new Runnable() {
                            public void run() {
                                try
                                {
                                    _buildingListOfCatalogs = true;
                                    _catalogsCmb.setCatalogs(catalogs, selected);
                                }
                                finally
                                {
                                    _buildingListOfCatalogs = false;
                                }                        
                            }
                        });                        
                    }
                });
			}
			catch (Exception ex)
			{
                // i18n[SessionPanel.error.retrievecatalog=Unable to retrieve catalog info]
				s_log.error(s_stringMgr.getString("SessionPanel.error.retrievecatalog"), ex);
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
							SessionPanel.this.setupCatalogsCombo();
						}
					}
				}
			}
		}
	}

	private final class SQLConnectionListener implements PropertyChangeListener
	{
		private SQLCatalogsComboBox _cmb;

        SQLConnectionListener()
        {
            super();
        }        
        
		SQLConnectionListener(SQLCatalogsComboBox cmb)
		{
			super();
			_cmb = cmb;
		}

        public void setSQLCatalogsComboBox(SQLCatalogsComboBox cmb) {
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
							if (!StringUtilities.areStringsEqual(
									conn.getCatalog(), _cmb.getSelectedCatalog()))
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

   private static class SeparatorMarker
   {

   }
}
