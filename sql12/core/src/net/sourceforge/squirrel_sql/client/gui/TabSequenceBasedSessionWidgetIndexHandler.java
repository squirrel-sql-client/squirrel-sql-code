package net.sourceforge.squirrel_sql.client.gui;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.DockTabDesktopPane;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.TabHandle;

public class TabSequenceBasedSessionWidgetIndexHandler implements ISessionWidgetIndexHandler
{

   private IApplication _app;

   public TabSequenceBasedSessionWidgetIndexHandler(IApplication app)
   {
      _app = app;
   }

   @Override
   public ISessionWidget getPreviousWidget(ISessionWidget sessionWindow)
   {
      TabHandle tabHandle = sessionWindow.getTabHandle();

      TabHandle ret = tabHandle.getPreviousTabHandle();

      if(null == ret)
      {
         return null;
      }

      return (ISessionWidget) ret.getWidget();
   }

   @Override
   public ISessionWidget getNextWidget(ISessionWidget sessionWindow)
   {
      TabHandle tabHandle = sessionWindow.getTabHandle();

      TabHandle ret = tabHandle.getNextTabHandle();

      if(null == ret)
      {
         return null;
      }

      return (ISessionWidget) ret.getWidget();
   }

   @Override
   public int size()
   {
      return getDockTabDesktopPane().getTabCount();
   }


   @Override
   public ISessionWidget getLastSessionWidget()
   {
      int tabIndex = size() - 1;

      if(0 > tabIndex)
      {
         return null;
      }

      return (ISessionWidget) getDockTabDesktopPane().getHandleAtTabIndex(tabIndex).getWidget();
   }

   @Override
   public ISessionWidget getFirstSessionWidget()
   {
      if(0 == size())
      {
         return null;
      }

      return (ISessionWidget) getDockTabDesktopPane().getHandleAtTabIndex(0).getWidget();
   }

   private DockTabDesktopPane getDockTabDesktopPane()
   {
      return (DockTabDesktopPane)_app.getMainFrame().getDesktopContainer();
   }

}
