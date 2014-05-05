package org.squirrelsql;

import java.util.ArrayList;
import java.util.List;

public class DockPaneChanel
{
   private List<DockPaneChanelAdapter> _listeners = new ArrayList<>();

   public void driversChanged(boolean selected)
   {
      DockPaneChanelAdapter[] dockPaneChanelAdapters = _listeners.toArray(new DockPaneChanelAdapter[_listeners.size()]);

      for (DockPaneChanelAdapter dockPaneChanelAdapter : dockPaneChanelAdapters)
      {
         dockPaneChanelAdapter.showDrivers(selected);
      }
   }

   public void aliasesChanged(boolean selected)
   {
      DockPaneChanelAdapter[] dockPaneChanelAdapters = _listeners.toArray(new DockPaneChanelAdapter[_listeners.size()]);

      for (DockPaneChanelAdapter dockPaneChanelAdapter : dockPaneChanelAdapters)
      {
         dockPaneChanelAdapter.showAliases(selected);
      }
   }

   public void closeDriver()
   {
      DockPaneChanelAdapter[] dockPaneChanelAdapters = _listeners.toArray(new DockPaneChanelAdapter[_listeners.size()]);

      for (DockPaneChanelAdapter dockPaneChanelAdapter : dockPaneChanelAdapters)
      {
         dockPaneChanelAdapter.closeDriver();
      }
   }

   public void addListener(DockPaneChanelAdapter dockPaneChanelAdapter)
   {
      _listeners.add(dockPaneChanelAdapter) ;
   }

   public void closeAliases()
   {
      DockPaneChanelAdapter[] dockPaneChanelAdapters = _listeners.toArray(new DockPaneChanelAdapter[_listeners.size()]);

      for (DockPaneChanelAdapter dockPaneChanelAdapter : dockPaneChanelAdapters)
      {
         dockPaneChanelAdapter.closeAliases();
      }
   }
}
