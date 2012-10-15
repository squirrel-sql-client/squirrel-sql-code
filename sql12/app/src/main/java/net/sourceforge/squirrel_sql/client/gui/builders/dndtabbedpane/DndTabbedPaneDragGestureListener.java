package net.sourceforge.squirrel_sql.client.gui.builders.dndtabbedpane;

import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;

class DndTabbedPaneDragGestureListener implements DragGestureListener
{
   private final Transferable _t;
   private final DragSourceListener _dsl;
   private DnDTabbedPaneData _dnDTabbedPaneData;
   private GhostGlassPane _glassPane;

   public DndTabbedPaneDragGestureListener(Transferable t, DragSourceListener dsl, DnDTabbedPaneData dnDTabbedPaneData, GhostGlassPane glassPane)
   {
      _t = t;
      _dsl = dsl;
      _dnDTabbedPaneData = dnDTabbedPaneData;
      _glassPane = glassPane;
   }

   @Override
   public void dragGestureRecognized(DragGestureEvent e)
   {
//      if (_dnDTabbedPaneData.getTabbedPane().getTabCount() <= 1)
//      {
//         return;
//      }

      Point tabPt = e.getDragOrigin();
      _dnDTabbedPaneData.setDragTabIndex(_dnDTabbedPaneData.getTabbedPane().indexAtLocation(tabPt.x, tabPt.y));
      //"disabled tab problem".
      if (_dnDTabbedPaneData.getDragTabIndex() < 0 || !_dnDTabbedPaneData.getTabbedPane().isEnabledAt(_dnDTabbedPaneData.getDragTabIndex())) return;
      DndTabUtils.initGlassPaneLocal(e.getDragOrigin(), _dnDTabbedPaneData, _glassPane);
      try
      {
         e.startDrag(DragSource.DefaultMoveDrop, _t, _dsl);
      }
      catch (InvalidDnDOperationException idoe)
      {
         idoe.printStackTrace();
      }
   }

}
