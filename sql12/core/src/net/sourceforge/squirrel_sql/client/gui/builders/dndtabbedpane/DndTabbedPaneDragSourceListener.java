package net.sourceforge.squirrel_sql.client.gui.builders.dndtabbedpane;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;

class DndTabbedPaneDragSourceListener implements DragSourceListener
{
   private final GhostGlassPane _glassPane;
   private final DnDTabbedPaneData _dnDTabbedPaneData;
   private OutwardDndTabbedPaneChanel _outwardDndTabbedPaneChanel;

   public DndTabbedPaneDragSourceListener(GhostGlassPane glassPane, DnDTabbedPaneData dnDTabbedPaneData, OutwardDndTabbedPaneChanel outwardDndTabbedPaneChanel)
   {
      _glassPane = glassPane;
      _dnDTabbedPaneData = dnDTabbedPaneData;
      _outwardDndTabbedPaneChanel = outwardDndTabbedPaneChanel;
   }

   @Override
   public void dragEnter(DragSourceDragEvent e)
   {
      if (null != _outwardDndTabbedPaneChanel)
      {
         _outwardDndTabbedPaneChanel.setDndTabbedPaneData(_dnDTabbedPaneData);
      }
      e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
   }

   @Override
   public void dragExit(DragSourceEvent e)
   {
      e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
      _dnDTabbedPaneData.getTargetLineRectBuffer().setRect(0, 0, 0, 0);
      _glassPane.setPoint(new Point(-1000, -1000));
      _glassPane.repaint();
   }

   @Override
   public void dragOver(DragSourceDragEvent e)
   {
//      Point glassPt = e.getLocation();
//      SwingUtilities.convertPointFromScreen(glassPt, _glassPane);
//      int targetIdx = DndTabUtils.getTargetTabIndex(glassPt, _glassPane, _dnDTabbedPaneData.getTabbedPane());
//      //if(getTabAreaBounds().contains(tabPt) && targetIdx>=0 &&
////      if (DndTabUtils.getTabAreaBounds(_dnDTabbedPaneData.getTabbedPane()).contains(glassPt) && targetIdx >= 0 &&
////            targetIdx != _dnDTabbedPaneData.getDragTabIndex() && targetIdx != _dnDTabbedPaneData.getDragTabIndex() + 1)
//
//      if ( null != _outwardDndTabbedPaneChanel && null != _outwardDndTabbedPaneChanel.getDndTabbedPaneData())
//      {
//         e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
//         _glassPane.setCursor(DragSource.DefaultMoveDrop);
//      }
////      else if (DndTabUtils.getTabAreaBounds(_dnDTabbedPaneData.getTabbedPane()).contains(glassPt) && targetIdx >= 0 &&
////            targetIdx != _dnDTabbedPaneData.getDragTabIndex() && targetIdx != _dnDTabbedPaneData.getDragTabIndex() + 1)
//      else if (_glassPane.getBounds().contains(glassPt))
//      {
//         e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
//         _glassPane.setCursor(DragSource.DefaultMoveDrop);
//      }
//      else
//      {
//         e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
//         _glassPane.setCursor(DragSource.DefaultMoveNoDrop);
//      }

      e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
      _glassPane.setCursor(DragSource.DefaultMoveDrop);

   }

   @Override
   public void dragDropEnd(DragSourceDropEvent e)
   {
      _dnDTabbedPaneData.getTargetLineRectBuffer().setRect(0, 0, 0, 0);
      _dnDTabbedPaneData.setDragTabIndex(-1);
      DndTabUtils.hideGlassPane(_glassPane, _dnDTabbedPaneData);

      if(null != _outwardDndTabbedPaneChanel)
      {
         _outwardDndTabbedPaneChanel.dragDropEnd();
      }
   }

   @Override
   public void dropActionChanged(DragSourceDragEvent e)
   {
   }
}
