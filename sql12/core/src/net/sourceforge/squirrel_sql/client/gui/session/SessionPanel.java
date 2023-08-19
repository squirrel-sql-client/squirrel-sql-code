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
import net.sourceforge.squirrel_sql.client.gui.session.rowcolumnlabel.RowColumnLabel;
import net.sourceforge.squirrel_sql.client.gui.titlefilepath.TitleFilePathHandler;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.connectionpool.SessionConnectionPoolStatusBarCtrl;
import net.sourceforge.squirrel_sql.client.session.filemanager.IFileEditorAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.SQLTab;
import net.sourceforge.squirrel_sql.client.session.objecttreesearch.ObjectTreeSearch;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.statusbar.SessionStatusBar;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class SessionPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SessionPanel.class);

   private final IApplication _app;

   private IIdentifier _sessionId;

   private PropertyChangeListener _propsListener;

   private MainPanel _mainPanel;

   /** Toolbar for window. */
   private SessionPanelToolBar _sessionPanelToolBar;

   private ArrayList<ToolbarItem> _externallyAddedToolbarActionsAndSeparators = new ArrayList<>();

   private SessionStatusBar _sessionStatusBar = new SessionStatusBar();

   private TitleFilePathHandler _titleFileHandler = null;

   public SessionPanel(ISession session, TitleFilePathHandler titleFileHandler)
   {
      super(new BorderLayout());

      _titleFileHandler = titleFileHandler;

      _app = session.getApplication();
      _sessionId = session.getIdentifier();

      SessionColoringUtil.colorStatusbar(session, _sessionStatusBar);

      _sessionStatusBar.setHrefListener((linkDescription, hrefReferenceObject) -> onHrefClicked(linkDescription, hrefReferenceObject));


      JMenuItem mnuCopyLast = new JMenuItem(s_stringMgr.getString("SessionPanel.statusbar.rightMouseMenu.copyLast"));
      mnuCopyLast.addActionListener(e -> onStatusBarRightMouseMenuCopyLast());
      _sessionStatusBar.setAdditionalRightMouseMenuItem(mnuCopyLast);
   }

   private void onStatusBarRightMouseMenuCopyLast()
   {
      final TreePath refTreePath = (TreePath) _sessionStatusBar.getHrefReferenceObject();

      if(null == refTreePath)
      {
         return;
      }

      ObjectTreeNode node = (ObjectTreeNode) refTreePath.getLastPathComponent();

      ClipboardUtil.copyToClip(node.getDatabaseObjectInfo().getSimpleName(), true);
   }

   private void onHrefClicked(String linkDescription, Object hrefReferenceObject)
   {
      final TreePath treePathForLink = StatusBarHtml.getTreePathForLink(linkDescription, (TreePath) hrefReferenceObject);

      new ObjectTreeSearch().viewInObjectTree(treePathForLink, getSession().getObjectTreeAPIOfActiveSessionWindow());
   }

   protected void initialize(ISession session)
   {
      createGUI(session);
      propertiesHaveChanged(null);

      _propsListener = evt -> propertiesHaveChanged(evt.getPropertyName());

      getObjectTreePanel().addTreeSelectionListener(e -> onObjectTreeSelectionChanged(e));

      session.getProperties().addPropertyChangeListener(_propsListener);
   }

   public void addToToolsPopUp(String selectionString, Action action)
   {
      getMainSQLPaneAPI().addToToolsPopUp(selectionString, action);
   }

   public ISession getSession()
   {
      return _app.getSessionManager().getSession(_sessionId);
   }

   public void sessionHasClosed()
   {

      final ISession session = getSession();
      if (session != null)
      {
         if (_propsListener != null)
         {
            session.getProperties().removePropertyChangeListener(_propsListener);
            _propsListener = null;
         }
         _mainPanel.sessionClosing(session);
         _sessionId = null;
      }
   }


   public void setStatusBarProgress(final String msg, final int minimum, final int maximum, final int value)
   {
      setStatusBarProgress(msg, minimum, maximum, value, null);
   }

   /**
    * @param stopAction On first call when the progress bar is created (and until setStatusBarProgressFinished() is called)
    *                   if stopAction is not null a stop button is created on the left and the listener is attached to it.
    */
   public void setStatusBarProgress(final String msg, final int minimum, final int maximum, final int value, ActionListener stopAction)
   {
      GUIUtils.processOnSwingEventThread(() -> _sessionStatusBar.setStatusBarProgress(msg, minimum, maximum, value, stopAction));
   }

   public void setStatusBarProgressFinished()
   {
      GUIUtils.processOnSwingEventThread(() -> _sessionStatusBar.setStatusBarProgressFinished());
   }

   /**
    * Add the passed action to the session toolbar.
    *
    * @param   action   Action to be added.
    */
   public synchronized void addToToolbar(Action action)
   {
      _externallyAddedToolbarActionsAndSeparators.add(new ToolbarItem(action));
      if (null != _sessionPanelToolBar)
      {
         _sessionPanelToolBar.add(action);
      }
   }

   public synchronized void addSeparatorToToolbar()
   {
      _externallyAddedToolbarActionsAndSeparators.add(new ToolbarItem());
      if (null != _sessionPanelToolBar)
      {
         _sessionPanelToolBar.addSeparator();
      }
   }


   private void propertiesHaveChanged(String propertyName)
   {
      final ISession session = getSession();
      final SessionProperties props = session.getProperties();

      if (propertyName == null || propertyName.equals(SessionProperties.IPropertyNames.COMMIT_ON_CLOSING_CONNECTION))
      {
         _app.getThreadPool().addTask(() -> session.getConnectionPool().setSessionCommitOnClose(props.getCommitOnClosingConnection()));
      }

      if (propertyName == null || propertyName.equals(SessionProperties.IPropertyNames.SHOW_TOOL_BAR))
      {
         synchronized (this)
         {
            boolean show = props.getShowToolBar();
            if (show != (_sessionPanelToolBar != null))
            {
               if (show)
               {
                  if (_sessionPanelToolBar == null)
                  {
                     _sessionPanelToolBar = new SessionPanelToolBar(session, getObjectTreePanel());
                     for (int i = 0; i < _externallyAddedToolbarActionsAndSeparators.size(); i++)
                     {
                        ToolbarItem toolbarItem = _externallyAddedToolbarActionsAndSeparators.get(i);

                        if (toolbarItem.isSeparator())
                        {
                           _sessionPanelToolBar.addSeparator();
                        }
                        else
                        {
                           _sessionPanelToolBar.add(toolbarItem.getAction());
                        }
                     }
                     add(_sessionPanelToolBar, BorderLayout.NORTH);
                  }
               }
               else
               {
                  if (_sessionPanelToolBar != null)
                  {
                     remove(_sessionPanelToolBar);
                     _sessionPanelToolBar = null;
                  }
               }
            }
         }
      }
   }

   private void createGUI(ISession session)
   {
      final IApplication app = session.getApplication();

      _mainPanel = new MainPanel(session, _titleFileHandler);

      add(_mainPanel, BorderLayout.CENTER);

      app.getFontInfoStore().setUpStatusBarFont(_sessionStatusBar);
      add(_sessionStatusBar, BorderLayout.SOUTH);
      _sessionStatusBar.addJComponent(new SessionConnectionPoolStatusBarCtrl(session, _sessionStatusBar).getStatusBarPanel());
      _sessionStatusBar.addJComponent(new SchemaPanel(session));
      _sessionStatusBar.addJComponent(new RowColumnLabel(_mainPanel));

      validate();
   }


   public boolean isObjectTreeTabSelected()
   {
      return MainPanel.ITabIndexes.OBJECT_TREE_TAB == _mainPanel.getSelectedMainTabIndex();
   }


   public int getTabCount()
   {
      return _mainPanel.getMainTabCount();
   }

   public int getMainPanelTabIndex(IMainPanelTab mainPanelTab)
   {
      return _mainPanel.getTabIndex(mainPanelTab);
   }

   public String getSelectedCatalogFromCatalogsComboBox()
   {
      if(null == _sessionPanelToolBar)
      {
         return null;
      }

      return _sessionPanelToolBar.getCatalogsPanelController().getSelectedCatalog();
   }

   public IMainPanelTab getMainPanelTabAt(int tabIndex)
   {
      return _mainPanel.getMainPanelTabAt(tabIndex);
   }

   public ISQLPanelAPI getMainSQLPaneAPI()
   {
      return _mainPanel.getMainSQLPanel().getSQLPanelAPI();
   }

   public ISQLPanelAPI getSelectedOrMainSQLPanelAPI()
   {
      return _mainPanel.getSelectedOrMainSQLPanel().getSQLPanelAPI();
   }


   public ISQLEntryPanel getMainSQLEntryPanel()
   {
      return getMainSQLPanel().getSQLEntryPanel();
   }

   public SQLPanel getMainSQLPanel()
   {
      return _mainPanel.getMainSQLPanel();
   }

   public java.util.List<SQLPanel> getAllSQLPanels()
   {
      return _mainPanel.getAllSQLPanels();
   }

   public List<AdditionalSQLTab> getAdditionalSQLTabs()
   {
      return _mainPanel.getAdditionalSQLTabs();
   }

   /**
    * @return null if the selected panel is not an SQL panel.
    */
   public SQLPanel getSelectedSQLPanel()
   {
      return _mainPanel.getSelectedSQLPanel();
   }

   public SQLPanel getSelectedOrMainSQLPanel()
   {
      return _mainPanel.getSelectedOrMainSQLPanel();
   }


   public boolean isAnSQLTabSelected()
   {
      return _mainPanel.getSelectedMainTab() instanceof SQLTab || _mainPanel.getSelectedMainTab() instanceof AdditionalSQLTab;
   }

   public void sessionWindowClosing()
   {
      _mainPanel.sessionWindowClosing();
   }


   public ObjectTreePanel getObjectTreePanel()
   {
      return _mainPanel.getObjectTreePanel();
   }

   public void selectMainTab(int tabIndex)
   {
      if (tabIndex >= _mainPanel.getMainTabCount())
      {
         throw new IllegalArgumentException("" + tabIndex + " is not a valid index into the main tabbed pane.");
      }
      if (_mainPanel.getSelectedMainTabIndex() != tabIndex)
      {
         _mainPanel.selectMainTab(tabIndex);
      }
   }

   public void selectMainTab(IMainPanelTab mainPanelTab)
   {
      int mainTabIndex = getMainPanelTabIndex(mainPanelTab);

      if(-1 == mainTabIndex)
      {
         throw new IllegalStateException("Couldn't find index for IMainPanelTab: " + mainPanelTab);
      }


      selectMainTab(mainTabIndex);
   }


   public int getSelectedMainTabIndex()
   {
      return _mainPanel.getSelectedMainTabIndex();
   }

   public IMainPanelTab getSelectedMainTab()
   {
      return _mainPanel.getSelectedMainTab();
   }



   public int addMainTab(IMainPanelTab tab)
   {
      if (tab == null)
      {
         throw new IllegalArgumentException("IMainPanelTab == null");
      }
      return _mainPanel.addMainPanelTab(tab);
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

      _mainPanel.insertMainPanelTab(tab, idx, selectInsertedTab);
   }

   public int removeMainTab(IMainPanelTab tab)
   {
      if (tab == null)
      {
         throw new IllegalArgumentException("Null IMainPanelTab passed");
      }
      return _mainPanel.removeMainPanelTab(tab);
   }

   public IFileEditorAPI getActiveIFileEditorAPIOrNull()
   {
      return getSelectedMainTab().getActiveFileEditorAPIOrNull();
   }

   public void performStateChanged()
   {
      _mainPanel.performStateChanged();
   }

   public void onObjectTreeSelectionChanged(TreeSelectionEvent evt)
   {
      final TreePath selPath = evt.getNewLeadSelectionPath();

      if (selPath == null)
      {
         GUIUtils.processOnSwingEventThread(() -> _sessionStatusBar.clearText());
         return;
      }

      TreePath newBreadCrumbPath = selPath;
      final TreePath currentBreadCrumbPath = (TreePath) _sessionStatusBar.getHrefReferenceObject();

      // If the new selected path is a parent of the current bread crumb path
      // we leave the bread crumbs as they are to allow a bit of backward navigation.
      if (   null != currentBreadCrumbPath
          && currentBreadCrumbPath.getPathCount() > selPath.getPathCount()
          && selPath.getLastPathComponent() == currentBreadCrumbPath.getPathComponent(selPath.getPathCount() - 1))
      {
         newBreadCrumbPath = currentBreadCrumbPath;
      }

      final String text = StatusBarHtml.createStatusBarHtml(newBreadCrumbPath);
      TreePath finalNewBreadCrumbPath = newBreadCrumbPath;
      GUIUtils.processOnSwingEventThread(() -> _sessionStatusBar.setText(text, finalNewBreadCrumbPath));
   }
}
