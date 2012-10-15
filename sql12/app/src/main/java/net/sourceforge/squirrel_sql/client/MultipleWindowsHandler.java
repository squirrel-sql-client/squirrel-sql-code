package net.sourceforge.squirrel_sql.client;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.DockTabDesktopPane;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.DockTabDesktopPaneHolder;
import net.sourceforge.squirrel_sql.client.gui.builders.dndtabbedpane.OutwardDndTabbedPaneChanel;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.HashSet;

public class MultipleWindowsHandler
{
   private HashSet<DockTabDesktopPaneHolder> _dockTabDesktopPaneHolders = new HashSet<DockTabDesktopPaneHolder>();
   private DockTabDesktopPaneHolder _curSelectedDockTabDesktopPaneHolder;

   private DesktopTabbedPaneOutwardDndChanel _outwardDndTabbedPaneChanel;
   private Application _app;

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

      final DockTabDesktopPaneHolder dockTabDesktopPaneHolder = new DockTabDesktopPaneHolder()
      {
         @Override
         public DockTabDesktopPane getDockTabDesktopPane()
         {
            return mainDesktop;
         }

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
      };

      _dockTabDesktopPaneHolders.add(dockTabDesktopPaneHolder);

      _curSelectedDockTabDesktopPaneHolder = dockTabDesktopPaneHolder;


      _app.getMainFrame().addWindowFocusListener(new WindowFocusListener()
      {
         @Override
         public void windowGainedFocus(WindowEvent e)
         {
            selectDesktop(dockTabDesktopPaneHolder);
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
         if(dockTabDesktopPaneHolder.getDockTabDesktopPane().isMyTabbedPane(tabbedPane))
         {
            return dockTabDesktopPaneHolder;
         }
      }

      throw new IllegalArgumentException("Could not find DockTabDesktopPane for TabbedPane " + tabbedPane);
   }
}
