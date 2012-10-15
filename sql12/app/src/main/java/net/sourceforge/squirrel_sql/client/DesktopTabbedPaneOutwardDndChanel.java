package net.sourceforge.squirrel_sql.client;

import net.sourceforge.squirrel_sql.client.gui.builders.dndtabbedpane.DnDTabbedPaneData;
import net.sourceforge.squirrel_sql.client.gui.builders.dndtabbedpane.OutwardDndTabbedPaneChanelListener;
import net.sourceforge.squirrel_sql.client.gui.builders.dndtabbedpane.OutwardDndTabbedPaneChanel;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.DockTabDesktopPane;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.DockTabDesktopPaneHolder;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.TabHandle;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;

class DesktopTabbedPaneOutwardDndChanel implements OutwardDndTabbedPaneChanel
{
   HashMap<JTabbedPane, OutwardDndTabbedPaneChanelListener> _listeners = new HashMap<JTabbedPane, OutwardDndTabbedPaneChanelListener>();

   private DnDTabbedPaneData _dnDTabbedPaneData;
   private Application _app;

   public DesktopTabbedPaneOutwardDndChanel(Application app)
   {
      _app = app;
   }

   @Override
   public DnDTabbedPaneData getDndTabbedPaneData()
   {
      return _dnDTabbedPaneData;
   }

   @Override
   public void setDndTabbedPaneData(DnDTabbedPaneData dnDTabbedPaneData)
   {
      _dnDTabbedPaneData = dnDTabbedPaneData;
   }

   private void fireHideGlassPane()
   {
      OutwardDndTabbedPaneChanelListener[] clone = _listeners.values().toArray(new OutwardDndTabbedPaneChanelListener[_listeners.size()]);
      for (OutwardDndTabbedPaneChanelListener outwardDndTabbedPaneChanelListener : clone)
      {
         outwardDndTabbedPaneChanelListener.hideGlassPane();
      }

   }

   @Override
   public void putListener(JTabbedPane tabbedPane, OutwardDndTabbedPaneChanelListener l)
   {
      _listeners.put(tabbedPane, l);
   }

   @Override
   public void dragDropEnd()
   {
      _dnDTabbedPaneData = null;
      fireHideGlassPane();
   }

   @Override
   public void moveDraggedTabTo(JTabbedPane targetTabbedPane, int targetTabIndex)
   {
//      ButtonTabComponent cp = (ButtonTabComponent) _dnDTabbedPaneData.getTabbedPane().getTabComponentAt(_dnDTabbedPaneData.getDragTabIndex());
//      System.out.println("Moving >" + cp.getTitle() + "< to index " + targetTabIndex);


      DockTabDesktopPaneHolder sourceDesktopPaneHolder = _app.getMultipleWindowsHandler().getDockTabDesktopPaneOfTabbedPane(_dnDTabbedPaneData.getTabbedPane());

      TabHandle tabHandleToMove = sourceDesktopPaneHolder.getDockTabDesktopPane().removeTabHandel(_dnDTabbedPaneData.getDragTabIndex());

      DockTabDesktopPaneHolder targetDesktopPaneHolder = _app.getMultipleWindowsHandler().getDockTabDesktopPaneOfTabbedPane(targetTabbedPane);

      targetDesktopPaneHolder.getDockTabDesktopPane().addTabWidgetAt(tabHandleToMove.getWidget(), targetTabIndex);

      sourceDesktopPaneHolder.tabDragedAndDroped();
      targetDesktopPaneHolder.tabDragedAndDroped();
   }

   public void removeListener(DockTabDesktopPaneHolder dockTabDesktopPaneHolder)
   {
      for (JTabbedPane tabbedPane : _listeners.keySet())
      {
         if(dockTabDesktopPaneHolder.getDockTabDesktopPane().isMyTabbedPane(tabbedPane))
         {
            _listeners.remove(tabbedPane);
            break;
         }
      }
   }
}
