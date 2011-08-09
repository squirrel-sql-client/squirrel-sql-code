package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ScrollableDesktopPane;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.DockTabDesktopPane;

import java.awt.*;

public class DesktopContainerFactory
{
   public static IDesktopContainer createDesktopContainer(IApplication app)
   {
      if (app.getDesktopStyle().isDockTabStyle())
      {
         return new DockTabDesktopPane(app);
      }
      else
      {
         return new ScrollableDesktopPane(app);
      }
   }


   public static IDialogDelegate createDialogDelegate(IApplication app, Window parent, String title, boolean resizeable, boolean closeable, boolean maximizeable, boolean iconifiable, DialogWidget dialogClient)
   {
      if(app.getDesktopStyle().isDockTabStyle())
      {
         return new DialogDelegate(title, resizeable, closeable, maximizeable, iconifiable, dialogClient, parent);
      }
      else
      {
         return new InternalFrameDelegate(title, resizeable, closeable, maximizeable, iconifiable, dialogClient);
      }
   }

   public static IDockDelegate createDockDelegate(IApplication app, String title, boolean resizeable, boolean closeable, boolean maximizeable, boolean iconifiable, DockWidget dockWidget)
   {
      if (app.getDesktopStyle().isDockTabStyle())
      {
         return new DockDelegate(app, title, dockWidget);
      }
      else
      {
         return new InternalFrameDelegate(title, resizeable, closeable, maximizeable, iconifiable, dockWidget);
      }
   }


   public static ITabDelegate createTabDelegate(IApplication app, String title, boolean resizeable, boolean closeable, boolean maximizeable, boolean iconifiable, TabWidget tabWidget)
   {
      if (app.getDesktopStyle().isDockTabStyle())
      {
         return new TabDelegate(tabWidget, title);
      }
      else
      {
         return new InternalFrameDelegate(title, resizeable, closeable, maximizeable, iconifiable, tabWidget);
      }
   }
}
