package net.sourceforge.squirrel_sql.client;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.DockTabDesktopPane;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.DockTabDesktopPaneHolder;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.HashSet;

public class MultipleWindowsHandler
{
   public static final boolean DETACH_SESSION_WINDOW = false;

   private HashSet<DockTabDesktopPaneHolder> _dockTabDesktopPaneHolders = new HashSet<DockTabDesktopPaneHolder>();
   private DockTabDesktopPaneHolder _curSelectedDockTabDesktopPaneHolder;


   public void registerDesktop(DockTabDesktopPaneHolder dockTabDesktopPaneHolder)
   {
      _dockTabDesktopPaneHolders.add(dockTabDesktopPaneHolder);
   }

   public void selectDesktop(DockTabDesktopPaneHolder dockTabDesktopPaneHolder)
   {
      if(false == DETACH_SESSION_WINDOW)
      {
         return;
      }

      if(dockTabDesktopPaneHolder == _curSelectedDockTabDesktopPaneHolder)
      {
         return;
      }

      _curSelectedDockTabDesktopPaneHolder.getDockTabDesktopPane().setSelected(false);

      _curSelectedDockTabDesktopPaneHolder = dockTabDesktopPaneHolder;

      _curSelectedDockTabDesktopPaneHolder.getDockTabDesktopPane().setSelected(true);
   }

   public void registerMainFrame(IApplication app, final DockTabDesktopPane mainDesktop)
   {

      final DockTabDesktopPaneHolder dockTabDesktopPaneHolder = new DockTabDesktopPaneHolder()
      {
         @Override
         public DockTabDesktopPane getDockTabDesktopPane()
         {
            return mainDesktop;
         }
      };

      _dockTabDesktopPaneHolders.add(dockTabDesktopPaneHolder);

      _curSelectedDockTabDesktopPaneHolder = dockTabDesktopPaneHolder;


      app.getMainFrame().addWindowFocusListener(new WindowFocusListener()
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

}
