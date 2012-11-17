package net.sourceforge.squirrel_sql.client;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.TabWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.DockTabDesktopPane;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.DockTabDesktopPaneHolder;
import net.sourceforge.squirrel_sql.client.gui.builders.dndtabbedpane.OutwardDndTabbedPaneChanel;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.RemoveTabHandelResult;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.SmallTabButton;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.HashSet;

public class MultipleWindowsHandler
{
   private HashSet<DockTabDesktopPaneHolder> _dockTabDesktopPaneHolders = new HashSet<DockTabDesktopPaneHolder>();
   private DockTabDesktopPaneHolder _curSelectedDockTabDesktopPaneHolder;

   private DesktopTabbedPaneOutwardDndChanel _outwardDndTabbedPaneChanel;
   private Application _app;
   private DockTabDesktopPaneHolder _mainDockTabDesktopPaneHolder;

   public MultipleWindowsHandler(Application app)
   {
      _app = app;
      _outwardDndTabbedPaneChanel = new DesktopTabbedPaneOutwardDndChanel(_app);
   }


   public void registerDesktop(DockTabDesktopPaneHolder dockTabDesktopPaneHolder)
   {
      _dockTabDesktopPaneHolders.add(dockTabDesktopPaneHolder);
   }

   public void selectDesktop(DockTabDesktopPaneHolder dockTabDesktopPaneHolder)
   {
      if(dockTabDesktopPaneHolder == _curSelectedDockTabDesktopPaneHolder)
      {
         return;
      }

      _curSelectedDockTabDesktopPaneHolder.setSelected(false);

      _curSelectedDockTabDesktopPaneHolder = dockTabDesktopPaneHolder;

      _curSelectedDockTabDesktopPaneHolder.setSelected(true);
   }

   public void registerMainFrame(final DockTabDesktopPane mainDesktop)
   {
      _mainDockTabDesktopPaneHolder = new DockTabDesktopPaneHolder()
      {

         @Override
         public void setSelected(boolean b)
         {
            onSelectMainDesktop(b, mainDesktop);
         }

         @Override
         public void tabDragedAndDroped()
         {
            onTabDragedAndDroped(mainDesktop);
         }

         @Override
         public void addTabWidgetAt(TabWidget widget, int tabIndex, ArrayList<SmallTabButton> externalButtons)
         {
            mainDesktop.addTabWidgetAt(widget, tabIndex, externalButtons);
         }

         @Override
         public RemoveTabHandelResult removeTabHandel(int tabIndex)
         {
            return mainDesktop.removeTabHandel(tabIndex);
         }

         @Override
         public boolean isMyTabbedPane(JTabbedPane tabbedPane)
         {
            return mainDesktop.isMyTabbedPane(tabbedPane);
         }

         @Override
         public void addTabWidget(TabWidget widget, ArrayList<SmallTabButton> externalButtons)
         {
            mainDesktop.addTabWidgetAt(widget, mainDesktop.getTabCount() , externalButtons);
         }
      };

      _dockTabDesktopPaneHolders.add(_mainDockTabDesktopPaneHolder);

      _curSelectedDockTabDesktopPaneHolder = _mainDockTabDesktopPaneHolder;


      _app.getMainFrame().addWindowFocusListener(new WindowFocusListener()
      {
         @Override
         public void windowGainedFocus(WindowEvent e)
         {
            selectDesktop(_mainDockTabDesktopPaneHolder);
         }

         @Override
         public void windowLostFocus(WindowEvent e)
         {
         }
      });
   }

   private void onTabDragedAndDroped(DockTabDesktopPane mainDesktop)
   {
      adjustSessionMenu(mainDesktop);
   }

   private void onSelectMainDesktop(boolean b, DockTabDesktopPane mainDesktop)
   {
      mainDesktop.setSelected(b);
      adjustSessionMenu(mainDesktop);
   }

   private void adjustSessionMenu(DockTabDesktopPane mainDesktop)
   {
      if(null == mainDesktop.getSelectedWidget())
      {
         _app.getWindowManager().setEnabledSessionMenu(false);
      }
      else
      {
         _app.getWindowManager().setEnabledSessionMenu(true);
      }
   }

   public void unregisterDesktop(DockTabDesktopPaneHolder dockTabDesktopPaneHolder)
   {
      _dockTabDesktopPaneHolders.remove(dockTabDesktopPaneHolder);
      _outwardDndTabbedPaneChanel.removeListener(dockTabDesktopPaneHolder);
   }

   public OutwardDndTabbedPaneChanel getOutwardDndTabbedPaneChanel()
   {
      return _outwardDndTabbedPaneChanel;
   }

   public DockTabDesktopPaneHolder getDockTabDesktopPaneOfTabbedPane(JTabbedPane tabbedPane)
   {
      for (DockTabDesktopPaneHolder dockTabDesktopPaneHolder : _dockTabDesktopPaneHolders)
      {
         if(dockTabDesktopPaneHolder.isMyTabbedPane(tabbedPane))
         {
            return dockTabDesktopPaneHolder;
         }
      }

      throw new IllegalArgumentException("Could not find DockTabDesktopPane for TabbedPane " + tabbedPane);
   }

   public DockTabDesktopPaneHolder getMainDockTabDesktopHolder()
   {
      return _mainDockTabDesktopPaneHolder;
   }
}
