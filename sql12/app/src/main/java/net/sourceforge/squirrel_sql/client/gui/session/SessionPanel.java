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
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasColorProperties;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.*;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.IObjectTreeListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.schemainfo.FilterMatcher;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.StatusBar;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
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
    private static final long serialVersionUID = 1L;

    /** Logger for this class. */
    @SuppressWarnings("unused")
	private static final ILogger s_log =
		LoggerController.createLogger(SessionPanel.class);

	/** Internationalized strings for this class. */
	@SuppressWarnings("unused")
    private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SessionPanel.class);

	/** Application API. */
	private final transient IApplication _app;

	/** ID of the session for this window. */
	private transient IIdentifier _sessionId;

	/** Listener to the sessions properties. */
	private transient PropertyChangeListener _propsListener;

	private transient MainPanel _mainTabPane;

	private transient IMainPanelFactory _mainPanelFactory;

	/** Toolbar for window. */
	private MyToolBar _toolBar;

	private Vector<ToolbarItem> _externallyAddedToolbarActionsAndSeparators = new Vector<ToolbarItem>();

	private StatusBar _statusBar = new StatusBar();
	private boolean _hasBeenVisible;

	private transient ObjectTreeSelectionListener _objTreeSelectionLis = null;

   public SessionPanel(ISession session)
	{
		super(new BorderLayout());

		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
        
		_app = session.getApplication();
		_sessionId = session.getIdentifier();
        
		SQLAliasColorProperties colorProps = session.getAlias().getColorProperties();
		if (colorProps.isOverrideStatusBarBackgroundColor()) {
			int rgbValue = colorProps.getStatusBarBackgroundColorRgbValue();
			_statusBar.setBackground(new Color(rgbValue));
		}
	}

   protected void initialize(ISession session) {
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

   public int getSelectedMainTabIndex()
   {
      return _mainTabPane.getTabbedPane().getSelectedIndex();
   }

   public IMainPanelTab getSelectedMainTab()
   {
      return _mainTabPane.getSelectedMainTab();
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
      insertMainTab(tab, idx, true);
   }

	public void insertMainTab(IMainPanelTab tab, int idx, boolean selectInsertedTab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null IMainPanelTab passed");
		}
		if(idx == MainPanel.ITabIndexes.SQL_TAB || idx == MainPanel.ITabIndexes.OBJECT_TREE_TAB)
		{
			throw new IllegalArgumentException("Index " + idx + "conflicts with standard tabs");
		}

		_mainTabPane.insertMainPanelTab(tab, idx, selectInsertedTab);
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

   public void setStatusBarProgress(final String msg, final int minimum, final int maximum, final int value)
   {
      GUIUtils.processOnSwingEventThread(new Runnable()
      {
         public void run()
         {
            _statusBar.setStatusBarProgress(msg, minimum, maximum, value);
         }
      });
   }

   public void setStatusBarProgressFinished()
   {
      GUIUtils.processOnSwingEventThread(new Runnable()
      {
         public void run()
         {
            _statusBar.setStatusBarProgressFinished();
         }
      });

   }



    public String getStatusBarMessage() {
        return _statusBar.getText();
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
		_externallyAddedToolbarActionsAndSeparators.add(new ToolbarItem(action));
		if (null != _toolBar)
		{
			_toolBar.add(action);
		}
	}

   public synchronized void addSeparatorToToolbar()
   {
      _externallyAddedToolbarActionsAndSeparators.add(new ToolbarItem());
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
								ToolbarItem toolbarItem =  _externallyAddedToolbarActionsAndSeparators.get(i);
								
								if (toolbarItem.isSeparator()) {
									_toolBar.addSeparator();
								} else {
									 _toolBar.add(toolbarItem.getAction());
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

	private void createGUI(ISession session)
	{
		final IApplication app = session.getApplication();

		_mainTabPane = _mainPanelFactory.createMainPanel(session);

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

	/**
	 * @param panelFactory the _mainPanelFactory to set
	 */
	public void setMainPanelFactory(IMainPanelFactory panelFactory)
	{
		_mainPanelFactory = panelFactory;
	}

   public int getTabCount()
   {
      return _mainTabPane.getTabbedPane().getTabCount();
   }

   public int getMainTabIndex(IMainPanelTab mainPanelTab)
   {
      return _mainTabPane.getTabIndex(mainPanelTab);
   }


   private class MyToolBar extends ToolBar
   {
      private static final long serialVersionUID = 1L;
      private IObjectTreeListener _lis;
      private CatalogsPanel _catalogsPanel;

      MyToolBar(final ISession session)
      {
         super();
         createGUI(session);
         SQLAliasColorProperties colorProps = session.getAlias().getColorProperties();
         if (colorProps.isOverrideToolbarBackgroundColor()) {
         	int rgbValue = colorProps.getToolbarBackgroundColorRgbValue();
         	super.setBackground(new Color(rgbValue));
         }
         
      }

      public void addNotify()
      {
         super.addNotify();
         if (!_hasBeenVisible)
         {
            _hasBeenVisible = true;
            _mainTabPane.getObjectTreePanel().refreshTree();
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
         _catalogsPanel = new CatalogsPanel(session, this);
         _catalogsPanel.addActionListener(new CatalogsComboListener());


         add(_catalogsPanel);
         ActionCollection actions = session.getApplication().getActionCollection();
         setUseRolloverButtons(true);
         setFloatable(false);
         add(actions.get(SessionPropertiesAction.class));
         add(actions.get(RefreshSchemaInfoAction.class));
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
         add(actions.get(FilePrintAction.class));
         add(actions.get(FileCloseAction.class));
         addSeparator();
         add(actions.get(PreviousSqlAction.class));
         add(actions.get(NextSqlAction.class));
         add(actions.get(SelectSqlAction.class));
      }
   }

	private final class CatalogsComboListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			String selectedCatalog = SessionPanel.this._toolBar._catalogsPanel.getSelectedCatalog();
			if (selectedCatalog != null)
			{
				try
				{
               ISession session = getSession();
					session.getSQLConnection().setCatalog(selectedCatalog);
					refreshSchemaInBackground();
				}
				catch (SQLException ex)
				{
					getSession().showErrorMessage(ex);
					SessionPanel.this._toolBar._catalogsPanel.refreshCatalogs();
				}
			}
		}
        
		private void refreshSchemaInBackground()
		{
			final ISession session = getSession();
			session.getApplication().getThreadPool().addTask(new Runnable()
			{
				public void run()
				{
					session.getSchemaInfo().reloadAll();
					expandTreeInForeground();
				}
			});
		}
		
		private void expandTreeInForeground() {
			
			final ISession session = getSession();
			final String selectedCatalog = SessionPanel.this._toolBar._catalogsPanel.getSelectedCatalog();
			
			GUIUtils.processOnSwingEventThread(new Runnable() {
				public void run() {
					expandTablesForCatalog(session, selectedCatalog);
				}
			});
		}
		
		
      /**
		 * Since the catalog has changed, it is necessary to reload the schema info and expand the tables node
		 * in the tree. Saves the user a few clicks.
		 * 
		 * @param session
		 *           the session whose ObjectTreePanel should be updated
		 * @param selectedCatalog
		 *           the catalog that was selected.
		 */
		private void expandTablesForCatalog(ISession session, String selectedCatalog)
		{
			IObjectTreeAPI api = session.getObjectTreeAPIOfActiveSessionWindow();
			api.refreshTree(true);
			if (api.selectInObjectTree(selectedCatalog, null, new FilterMatcher("TABLE", null)))
			{
				ObjectTreeNode[] nodes = api.getSelectedNodes();

				if (nodes.length > 0)
				{
					ObjectTreeNode tableNode = nodes[0];

					// send a tree expansion event to the object tree
					api.expandNode(tableNode);
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
