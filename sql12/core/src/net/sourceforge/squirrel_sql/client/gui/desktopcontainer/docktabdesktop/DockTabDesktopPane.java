package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import net.sourceforge.squirrel_sql.client.ApplicationListener;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DockDelegate;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DockWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IDesktopContainer;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.TabDelegate;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.TabWidget;
import net.sourceforge.squirrel_sql.client.gui.mainframe.SquirrelDesktopManager;
import net.sourceforge.squirrel_sql.client.mainframe.action.CloseAllButCurrentSessionsAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CloseAllSessionsAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseSessionAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseSessionWindowAction;
import net.sourceforge.squirrel_sql.client.session.action.RenameSessionAction;
import net.sourceforge.squirrel_sql.fw.resources.Resources;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;

//
public class DockTabDesktopPane extends JComponent implements IDesktopContainer
{
   public enum TabClosingMode
   {
      CLOSE_BUTTON,
      DISPOSE
   }

   private IApplication _app;
   private boolean _belongsToMainApplicationWindow;
   private DockTabDesktopPaneListener _dockTabDesktopPaneListener;

   private JPanel _pnlButtons = new JPanel();

   private DockPanel _pnlDock = new DockPanel();

   private DesktopTabbedPane _tabbedPane;

   private JSplitPane _split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
   private boolean _inOnToggleOpenDock;
   private ArrayList<DockHandle> _dockHandles = new ArrayList<>();
   private ArrayList<TabHandle> _tabHandles = new ArrayList<>();
   private int _standardDividerSize;

   private HashSet<TabHandle> _handlesInRemoveTab_CloseButton = new HashSet<TabHandle>();
   private HashSet<TabHandle> _handlesInRemoveTab_Dispose = new HashSet<TabHandle>();

   private DockTabDesktopManager _dockTabDesktopManager = new DockTabDesktopManager();

   private JPopupMenu _tabRightMouseMenu;

   private ScrollableTabHandler _scrollableTabHandler;

   public DockTabDesktopPane(IApplication app, boolean belongsToMainApplicationWindow, DockTabDesktopPaneListener dockTabDesktopPaneListener)
   {
      _app = app;
      _belongsToMainApplicationWindow = belongsToMainApplicationWindow;
      _dockTabDesktopPaneListener = dockTabDesktopPaneListener;

      _tabbedPane = new DesktopTabbedPane(_app);

      setLayout(new BorderLayout());

      _pnlButtons = new JPanel();
      _pnlButtons.setLayout(new BoxLayout(_pnlButtons, BoxLayout.Y_AXIS));
      add(_pnlButtons, BorderLayout.WEST);

      _split.setLeftComponent(_pnlDock);

      _scrollableTabHandler = new ScrollableTabHandler(_app, _tabbedPane);

      _tabbedPane.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent e)
         {
            onTabStateChanged(e);
         }
      });

      _split.setRightComponent(_tabbedPane);


      add(_split, BorderLayout.CENTER);
      _standardDividerSize = _split.getDividerSize();

      _pnlDock.addComponentListener(new ComponentAdapter()
      {
         public void componentResized(ComponentEvent e)
         {
            onDockPanelResized();
         }
      });

      closeDock();

      if (_belongsToMainApplicationWindow)  // prevents memory leaks concerning detached windows
      {
         _app.addApplicationListener(new ApplicationListener()
         {
            public void saveApplicationState()
            {
               onSaveApplicationState();
            }
         });
      }

      initTabRightMouseMenu();
   }

   private void onSaveApplicationState()
   {
      for (DockHandle dockHandle : _dockHandles)
      {
         if(false == dockHandle.isClosed())
         {
            dockHandle.storeDividerLocation(_split.getDividerLocation());
         }
      }
   }

   private void onTabStateChanged(ChangeEvent e)
   {
      tabActivationChanged();
   }

   private void tabActivationChanged()
   {
      TabPanel tabPanel = (TabPanel) _tabbedPane.getSelectedComponent();

      if (null != tabPanel && tabPanel.getTabHandle().isSelected())
      {
         return;
      }

      for (TabHandle tabHandle : _tabHandles)
      {
         if (null == tabPanel || tabHandle.isSelected() && tabHandle != tabPanel.getTabHandle())
         {
            tabHandle.setSelected(false);
         }
      }

      if (tabPanel != null)
      {
         tabPanel.getTabHandle().setSelected(true);
      }
   }


   public void addWidget(DockWidget widget)
   {
      DockHandle dockHandle = addDock(widget.getContentPane(), widget.getTitle());
      ((DockDelegate) widget.getDelegate()).setDockHandle(dockHandle);
   }

   public void addWidget(DialogWidget client)
   {
   }

   public void addWidget(final TabWidget widget)
   {
      addTabWidgetAt(widget, _tabbedPane.getTabCount(), new ArrayList<SmallTabButton>());
   }

   public TabHandle addTabWidgetAt(TabWidget widget, int index, ArrayList<SmallTabButton> externalButtons)
   {
      return addTabWidgetAt(widget, index, externalButtons, false);
   }

   public TabHandle addTabWidgetAt(TabWidget widget, int index, ArrayList<SmallTabButton> externalButtons, boolean widgetMovedButNotCreated)
   {
      final TabHandle tabHandle = new TabHandle(widget, this);
      ((TabDelegate) widget.getDelegate()).setTabHandle(tabHandle);

      tabHandle.addTabHandleListener(_dockTabDesktopManager);

      TabPanel tabPanel = createTabPanel(tabHandle);
      _tabbedPane.insertTab(widget.getTitle(), null, tabPanel, widget.getTip(), index);
      int tabIx = _tabbedPane.indexOfComponent(tabPanel);

      ButtonTabComponent btc = (ButtonTabComponent) _tabbedPane.getTabComponentAt(tabIx);

      btc.getClosebutton().addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            removeTab(tabHandle, e, TabClosingMode.CLOSE_BUTTON);
         }
      });

      btc.getToWindowButton().addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onToWindow(tabHandle);
         }
      });


      for (SmallTabButton externalButton : externalButtons)
      {
         btc.addSmallTabButton(externalButton);
      }


      _tabHandles.add(tabHandle);
      tabHandle.fireAdded(_belongsToMainApplicationWindow, widgetMovedButNotCreated);
      _tabbedPane.setSelectedIndex(tabIx);
      _scrollableTabHandler.tabAdded();

      return tabHandle;
   }



   private void onToWindow(TabHandle tabHandle)
   {

      ///////////////////////////////////////////////////////////////////////////////////
      // The detaching tab might not be selected and thus not showing on screen.
      // Trying to get position and size form that would fail.
      // But we can as well take the selected tab to do this calculation.
      Point locationOnScreen = getSelectedHandle().getWidget().getContentPane().getLocationOnScreen();
      Dimension size = getSelectedHandle().getWidget().getContentPane().getSize();
      //
      ////////////////////////////////////////////////////////////////////////////////////
      TabWindowController tabWindowController = new TabWindowController(locationOnScreen, size, _app);


      _app.getMultipleWindowsHandler().registerDesktop(tabWindowController);

      RemoveTabHandelResult removeTabHandelResult = removeTabHandel(tabHandle);

      tabWindowController.addTabWidgetAt(tabHandle.getWidget(), 0, removeTabHandelResult.getRemovedButtonTabComponent().getExternalButtons(), true);

      if (null != _dockTabDesktopPaneListener)
      {
         _dockTabDesktopPaneListener.tabWasRemoved(tabHandle);
      }
   }

   public RemoveTabHandelResult removeTabHandel(int tabIndex)
   {
      TabHandle tabHandle = ((TabPanel) _tabbedPane.getComponentAt(tabIndex)).getTabHandle();
      return removeTabHandel(tabHandle);
   }

   public RemoveTabHandelResult removeTabHandel(TabHandle tabHandle)
   {
      RemoveTabHandelResult ret = new RemoveTabHandelResult();
      int tabIndex = getTabIndex(tabHandle);
      if (-1 != tabIndex)
      {
         ret.setRemovedButtonTabComponent(removeTabFromTabbedPane(tabIndex));
      }
      _tabHandles.remove(tabHandle);
      tabHandle.removeTabHandleListener(_dockTabDesktopManager);

      if(_belongsToMainApplicationWindow)
      {
         _app.getWindowManager().removeWidgetFromWindowMenu(tabHandle.getWidget());
      }

      ret.setRemovedTabHandle(tabHandle);

      return ret;
   }

   public boolean isMyTabbedPane(JTabbedPane tabbedPane)
   {
      return _tabbedPane == tabbedPane;
   }


   private void onDockPanelResized()
   {
      if (0 == _pnlDock.getComponentCount())
      {
         // Without this the split moves when the window is resized.
         closeDock();
      }
   }

   private void closeDock()
   {
      _split.setDividerLocation(0);
      _split.setDividerSize(0);
   }


   private void onToggleOpenDock(DockHandle handle, JToggleButton btn, ActionEvent e)
   {
      if (_inOnToggleOpenDock)
      {
         return;
      }

      try
      {
         _inOnToggleOpenDock = true;

         for (int i = 0; i < _pnlButtons.getComponents().length; i++)
         {
            JToggleButton btnBuf = (JToggleButton) _pnlButtons.getComponents()[i];

            if (btnBuf != btn)
            {
               btnBuf.setSelected(false);
            }
         }


         if (1 == _pnlDock.getComponentCount())
         {
            DockHandle lastHandle = getDockHandleForComponent(_pnlDock.getComponent(0));
            lastHandle.storeDividerLocation(_split.getDividerLocation());
            _pnlDock.remove(0);
            lastHandle.wasClosedByOtherButton(e);
         }

         if (btn.isSelected())
         {
            _pnlDock.add(handle.getDockFrame());
            _split.setDividerSize(_standardDividerSize);
            _split.setDividerLocation(handle.getDividerLocation());

         }
         else
         {
            closeDock();
         }
      }
      finally
      {
         _inOnToggleOpenDock = false;
      }
   }

   private DockHandle getDockHandleForComponent(Component component)
   {
      for (DockHandle dockHandle : _dockHandles)
      {
         if (dockHandle.getDockFrame() == component)
         {
            return dockHandle;
         }
      }

      return null;
   }

   private DockHandle addDock(Container comp, String title)
   {
      final VerticalToggleButton btn = new VerticalToggleButton(title);
      btn.setBorder(BorderFactory.createEtchedBorder());

      _pnlButtons.add(btn);

      final DockHandle handle = new DockHandle(_app, comp, title, btn);

      btn.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onToggleOpenDock(handle, btn, e);
         }
      });

      _dockHandles.add(handle);

      return handle;
   }


   private TabPanel createTabPanel(final TabHandle tabHandle)
   {
      return new TabPanel(tabHandle);
   }

   public void selectTab(TabHandle tabHandle)
   {
      TabHandle selectedHandle = getSelectedHandle();
      if(selectedHandle == tabHandle)
      {
         return;
      }

      if (null != selectedHandle)
      {
         selectedHandle.fireDeselected(null);
      }

      if (null != tabHandle)
      {
         int tabIx = getTabIndex(tabHandle);
         _tabbedPane.setSelectedIndex(tabIx);
      }
   }


   boolean removeTab(TabHandle tabHandle, ActionEvent e, TabClosingMode tabClosingMode)
   {


      if(TabClosingMode.CLOSE_BUTTON == tabClosingMode)
      {
         try
         {
            if(_handlesInRemoveTab_CloseButton.contains(tabHandle))
            {
               return false;
            }

            _handlesInRemoveTab_CloseButton.add(tabHandle);

            if(false == tabHandle.fireClosing(e))
            {
               return false;
            }

            if (WindowConstants.DO_NOTHING_ON_CLOSE != tabHandle.getWidget().getDefaultCloseOperation())
            {
               int tabIndex = getTabIndex(tabHandle);
               if (-1 != tabIndex)
               {
                  removeTabFromTabbedPane(tabIndex);
               }
               tabHandle.fireClosed(e);
               _tabHandles.remove(tabHandle);
               tabHandle.removeTabHandleListener(_dockTabDesktopManager);
            }
         }
         finally
         {
            _handlesInRemoveTab_CloseButton.remove(tabHandle);
         }
      }
      else if(TabClosingMode.DISPOSE == tabClosingMode)
      {
         try
         {
            if(_handlesInRemoveTab_Dispose.contains(tabHandle))
            {
               return false;
            }

            _handlesInRemoveTab_Dispose.add(tabHandle);

            if(false == tabHandle.isFireClosingProceedingOrDone())
            {
               // Has to be done here e.g. when "Close All Sessions" menu was used.
               if(false == tabHandle.fireClosing(e))
               {
                  return false;
               }
            }

            tabHandle.getWidget().setVisible(false);
            tabHandle.fireDeselected(e);
            removeTabHandel(tabHandle);


            TabHandle newSelectedHandle = getSelectedHandle();
            if(null != newSelectedHandle)
            {
               // Make sure a deselect is followed by a select when a new selected Tab exists. 
               newSelectedHandle.fireSelected(e);
            }
         }
         finally
         {
            _handlesInRemoveTab_Dispose.remove(tabHandle);
         }
      }
      else
      {
         throw new IllegalArgumentException("Unknown TabClosingMode: " + tabClosingMode);
      }

      return true;
   }

   private ButtonTabComponent removeTabFromTabbedPane(int tabIndex)
   {
      ButtonTabComponent ret = (ButtonTabComponent) _tabbedPane.getTabComponentAt(tabIndex);
      _tabbedPane.remove(tabIndex);
      _scrollableTabHandler.tabRemoved();

      return ret;
   }


   public IWidget[] getAllWidgets()
   {
      IWidget[] ret = new IWidget[_tabHandles.size()];

      for (int i = 0; i < ret.length; i++)
      {
         ret[i] = _tabHandles.get(i).getWidget();
      }

      return ret;
   }

   public ArrayList<TabHandle> getAllHandels()
   {
      return _tabHandles;
   }


   public IWidget getSelectedWidget()
   {
      TabHandle selectedHandle = getSelectedHandle();

      if(null == selectedHandle)
      {
         return null;
      }

      return selectedHandle.getWidget();
   }

   private TabHandle getSelectedHandle()
   {
      TabPanel selComp = (TabPanel) _tabbedPane.getSelectedComponent();

      if (null == selComp)
      {
         return null;
      }

      for (TabHandle tabHandle : _tabHandles)
      {
         if (tabHandle == selComp.getTabHandle())
         {
            return tabHandle;
         }
      }

      return null;
   }


   private int getTabIndex(TabHandle tabHandle)
   {
      for (int i = 0; i < _tabbedPane.getTabCount(); ++i)
      {
         TabPanel comp = (TabPanel) _tabbedPane.getComponentAt(i);

         if (comp.getTabHandle() == tabHandle)
         {
            return i;
         }
      }

      return -1;
   }

   public TabHandle getHandleAtTabIndex(int tabIndex)
   {
      TabPanel comp = (TabPanel) _tabbedPane.getComponentAt(tabIndex);
      return comp.getTabHandle();
   }



   public Dimension getRequiredSize()
   {
      return getPreferredSize();
   }

   public JComponent getComponent()
   {
      return this;
   }

   public void initTabRightMouseMenu()
   {
      _tabRightMouseMenu = new JPopupMenu();

      _tabbedPane.addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent e)
         {
            maybeShowTabRightMouseMenu(e);
         }

         public void mouseReleased(MouseEvent e)
         {
            maybeShowTabRightMouseMenu(e);
         }
      });

      _tabRightMouseMenu.add(createMenu(_app.getActionCollection().get(CloseSessionWindowAction.class)));
      _tabRightMouseMenu.add(createMenu(_app.getActionCollection().get(CloseSessionAction.class)));
      _tabRightMouseMenu.add(createMenu(_app.getActionCollection().get(CloseAllButCurrentSessionsAction.class)));
      _tabRightMouseMenu.add(createMenu(_app.getActionCollection().get(CloseAllSessionsAction.class)));
      
      _tabRightMouseMenu.add(createMenu(_app.getActionCollection().get(RenameSessionAction.class)));
      
   }

   private JMenuItem createMenu(Action action)
   {
      JMenuItem ret = new JMenuItem(action);

      String accel = (String) action.getValue(Resources.ACCELERATOR_STRING);
//      if(null != accel && 0 != accel.trim().length())
//      {
         Main.getApplication().getShortcutManager().setAccelerator(ret, KeyStroke.getKeyStroke(accel), action);
//      }

      return ret;
   }

   private void maybeShowTabRightMouseMenu(MouseEvent e)
   {
      if (e.isPopupTrigger())
      {
         int tab = _tabbedPane.getUI().tabForCoordinate(_tabbedPane, e.getX(), e.getY());
         if (-1 != tab)
         {
            _tabRightMouseMenu.show(e.getComponent(), e.getX(), e.getY());
         }
      }
   }


   public void setDesktopManager(SquirrelDesktopManager squirrelDesktopManager)
   {
      _dockTabDesktopManager.setSquirrelDesktopManager(squirrelDesktopManager);
   }

   public void setTabTitle(TabHandle tabHandle, String title)
   {
      _tabbedPane.setTitleAt(getTabIndex(tabHandle), title);
   }

   public void addSmallTabButton(TabHandle tabHandle, SmallTabButton smallTabButton)
   {
      _tabbedPane.addSmallTabButtonAt(getTabIndex(tabHandle), smallTabButton);
   }

   public void removeSmallTabButton(TabHandle tabHandle, SmallTabButton smallTabButton)
   {
      _tabbedPane.removeSmallTabButtonAt(getTabIndex(tabHandle), smallTabButton);
   }



   public String getTabTitle(TabHandle tabHandle)
   {
      int index = getTabIndex(tabHandle);

      if(-1 == index)
      {
         return null;
      }
      return _tabbedPane.getTitleAt(index);
   }

   public void setTabIcon(TabHandle tabHandle, Icon frameIcon)
   {
      _tabbedPane.setIconAt(getTabIndex(tabHandle), frameIcon);
   }


   public void putClientProperty(String key, String value)
   {
   }

   public void setSelected(boolean b)
   {
      TabHandle selectedHandle = getSelectedHandle();
      if (null != selectedHandle)
      {
         selectedHandle._setSelected(b, true);
      }
   }

   public int getTabCount()
   {
      return _tabHandles.size();
   }

   public TabHandle getPreviousTabHandle(TabHandle tabHandle)
   {
      int tabIndex = getTabIndex(tabHandle);

      if(0 < tabIndex)
      {
         return getHandleAtTabIndex(tabIndex - 1);
      }

      return null;
   }

   public TabHandle getNextTabHandle(TabHandle tabHandle)
   {
      int tabIndex = getTabIndex(tabHandle);

      if(tabIndex + 1 < getTabCount())
      {
         return getHandleAtTabIndex(tabIndex + 1);
      }

      return null;
   }
}
