package net.sourceforge.squirrel_sql.client.gui.builders.dndtabbedpane;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;

class DndTabbedPaneDropTargetListener implements DropTargetListener
{
   private DnDTabbedPaneData _dnDTabbedPaneData;
   private GhostGlassPane _glassPane;
   private OutwardDndTabbedPaneChanel _outwardDndTabbedPaneChanel;
   private Point _glassPt = new Point();

   public DndTabbedPaneDropTargetListener(DnDTabbedPaneData dnDTabbedPaneData, GhostGlassPane glassPane, OutwardDndTabbedPaneChanel outwardDndTabbedPaneChanel)
   {
      _dnDTabbedPaneData = dnDTabbedPaneData;
      _glassPane = glassPane;
      _outwardDndTabbedPaneChanel = outwardDndTabbedPaneChanel;
   }

   @Override
   public void dragEnter(DropTargetDragEvent e)
   {
      if (isDragAcceptable(e))
      {
         e.acceptDrag(e.getDropAction());
      }
      else
      {
         e.rejectDrag();
      }
   }

   @Override
   public void dragExit(DropTargetEvent e)
   {
   }

   @Override
   public void dropActionChanged(DropTargetDragEvent e)
   {
   }


   @Override
   public void dragOver(final DropTargetDragEvent e)
   {
      Point glassPt = e.getLocation();

      if (_dnDTabbedPaneData.getTabbedPane().getTabPlacement() == JTabbedPane.TOP || _dnDTabbedPaneData.getTabbedPane().getTabPlacement() == JTabbedPane.BOTTOM)
      {
         initTargetLeftRightLine(DndTabUtils.getTargetTabIndex(glassPt, _glassPane, _dnDTabbedPaneData.getTabbedPane()));
      }
      else
      {
         initTargetTopBottomLine(DndTabUtils.getTargetTabIndex(glassPt, _glassPane, _dnDTabbedPaneData.getTabbedPane()));
      }
      if (_dnDTabbedPaneData.isHasGhost())
      {
         _glassPane.setPoint(glassPt);
      }
      if (!_glassPt.equals(glassPt)) _glassPane.repaint();
      _glassPt = glassPt;
      autoScrollTest(glassPt);
   }

   private void autoScrollTest(Point glassPt)
   {
      Rectangle r = DndTabUtils.getTabAreaBounds(_dnDTabbedPaneData.getTabbedPane());
      int tabPlacement = _dnDTabbedPaneData.getTabbedPane().getTabPlacement();
      if (tabPlacement == SwingConstants.TOP || tabPlacement == SwingConstants.BOTTOM)
      {
         _dnDTabbedPaneData.getrBackward().setBounds(r.x, r.y, DndTabUtils.RWH, r.height);
         _dnDTabbedPaneData.getrForward().setBounds(r.x + r.width - DndTabUtils.RWH - DndTabUtils.BUTTON_SIZE, r.y, DndTabUtils.RWH + DndTabUtils.BUTTON_SIZE, r.height);
      }
      else if (tabPlacement == SwingConstants.LEFT || tabPlacement == SwingConstants.RIGHT)
      {
         _dnDTabbedPaneData.getrBackward().setBounds(r.x, r.y, r.width, DndTabUtils.RWH);
         _dnDTabbedPaneData.getrForward().setBounds(r.x, r.y + r.height - DndTabUtils.RWH - DndTabUtils.BUTTON_SIZE, r.width, DndTabUtils.RWH + DndTabUtils.BUTTON_SIZE);
      }
      _dnDTabbedPaneData.setrBackward(SwingUtilities.convertRectangle(_dnDTabbedPaneData.getTabbedPane().getParent(), _dnDTabbedPaneData.getrBackward(), _glassPane));
      _dnDTabbedPaneData.setrForward(SwingUtilities.convertRectangle(_dnDTabbedPaneData.getTabbedPane().getParent(), _dnDTabbedPaneData.getrForward(), _glassPane));
      if (_dnDTabbedPaneData.getrBackward().contains(glassPt))
      {
         //System.out.println(new java.util.Date() + "Backward");
         clickArrowButton("scrollTabsBackwardAction");
      }
      else if (_dnDTabbedPaneData.getrForward().contains(glassPt))
      {
         //System.out.println(new java.util.Date() + "Forward");
         clickArrowButton("scrollTabsForwardAction");
      }
   }

   private void clickArrowButton(String actionKey)
   {
      ActionMap map = _dnDTabbedPaneData.getTabbedPane().getActionMap();
      if (map != null)
      {
         Action action = map.get(actionKey);
         if (action != null && action.isEnabled())
         {
            action.actionPerformed(new ActionEvent(_dnDTabbedPaneData.getTabbedPane(), ActionEvent.ACTION_PERFORMED, null, 0, 0));
         }
      }
   }


   private void initTargetLeftRightLine(int next)
   {

//      if (next < 0 || _dnDTabbedPaneData.getDragTabIndex() == next || next - _dnDTabbedPaneData.getDragTabIndex() == 1)
      if (next < 0 || _dnDTabbedPaneData.getTabbedPane().getTabCount() < next || 0 == _dnDTabbedPaneData.getTabbedPane().getTabCount())
      {
         _dnDTabbedPaneData.getTargetLineRectBuffer().setRect(0, 0, 0, 0);
      }
      else if (next == 0)
      {
         Rectangle r = SwingUtilities.convertRectangle(_dnDTabbedPaneData.getTabbedPane(), _dnDTabbedPaneData.getTabbedPane().getBoundsAt(0), _glassPane);
         _dnDTabbedPaneData.getTargetLineRectBuffer().setRect(r.x - DndTabUtils.LINEWIDTH / 2, r.y, DndTabUtils.LINEWIDTH, r.height);
      }
      else
      {
         Rectangle r = SwingUtilities.convertRectangle(_dnDTabbedPaneData.getTabbedPane(), _dnDTabbedPaneData.getTabbedPane().getBoundsAt(next - 1), _glassPane);
         _dnDTabbedPaneData.getTargetLineRectBuffer().setRect(r.x + r.width - DndTabUtils.LINEWIDTH / 2, r.y, DndTabUtils.LINEWIDTH, r.height);
      }
   }

   private void initTargetTopBottomLine(int next)
   {
//             if (next < 0 || _dnDTabbedPaneData.getDragTabIndex() == next || next - _dnDTabbedPaneData.getDragTabIndex() == 1)
      if (next < 0 || _dnDTabbedPaneData.getTabbedPane().getTabCount() < next)
      {
         _dnDTabbedPaneData.getTargetLineRectBuffer().setRect(0, 0, 0, 0);
      }
      else if (next == 0)
      {
         Rectangle r = SwingUtilities.convertRectangle(_dnDTabbedPaneData.getTabbedPane(), _dnDTabbedPaneData.getTabbedPane().getBoundsAt(0), _glassPane);
         _dnDTabbedPaneData.getTargetLineRectBuffer().setRect(r.x, r.y - DndTabUtils.LINEWIDTH / 2, r.width, DndTabUtils.LINEWIDTH);
      }
      else
      {
         Rectangle r = SwingUtilities.convertRectangle(_dnDTabbedPaneData.getTabbedPane(), _dnDTabbedPaneData.getTabbedPane().getBoundsAt(next - 1), _glassPane);
         _dnDTabbedPaneData.getTargetLineRectBuffer().setRect(r.x, r.y + r.height - DndTabUtils.LINEWIDTH / 2, r.width, DndTabUtils.LINEWIDTH);
      }
   }



   @Override
   public void drop(DropTargetDropEvent e)
   {
      if (isDropAcceptable(e))
      {
         if (null != _outwardDndTabbedPaneChanel && null != _outwardDndTabbedPaneChanel.getDndTabbedPaneData() && _outwardDndTabbedPaneChanel.getDndTabbedPaneData() != _dnDTabbedPaneData)
         {
            int targetTabIndex = DndTabUtils.getTargetTabIndex(e.getLocation(), _glassPane, _dnDTabbedPaneData.getTabbedPane());
            if(-1 == targetTabIndex)
            {
               targetTabIndex = _dnDTabbedPaneData.getTabbedPane().getTabCount();
            }

            _outwardDndTabbedPaneChanel.moveDraggedTabTo(_dnDTabbedPaneData.getTabbedPane(), targetTabIndex);
         }
         else
         {
            convertTab(_dnDTabbedPaneData.getDragTabIndex(), DndTabUtils.getTargetTabIndex(e.getLocation(), _glassPane, _dnDTabbedPaneData.getTabbedPane()));
         }
         e.dropComplete(true);
      }
      else
      {
         e.dropComplete(false);
      }
      _dnDTabbedPaneData.getTabbedPane().repaint();
   }

   private void convertTab(int prev, int next)
   {
      if (next < 0 || prev == next)
      {
         return;
      }
      Component cmp = _dnDTabbedPaneData.getTabbedPane().getComponentAt(prev);
      Component tab = _dnDTabbedPaneData.getTabbedPane().getTabComponentAt(prev);
      String str = _dnDTabbedPaneData.getTabbedPane().getTitleAt(prev);
      Icon icon = _dnDTabbedPaneData.getTabbedPane().getIconAt(prev);
      String tip = _dnDTabbedPaneData.getTabbedPane().getToolTipTextAt(prev);
      boolean flg = _dnDTabbedPaneData.getTabbedPane().isEnabledAt(prev);
      int tgtindex = prev > next ? next : next - 1;
      _dnDTabbedPaneData.getTabbedPane().remove(prev);
      _dnDTabbedPaneData.getTabbedPane().insertTab(str, icon, cmp, tip, tgtindex);
      _dnDTabbedPaneData.getTabbedPane().setEnabledAt(tgtindex, flg);
      //When you drag'n'drop a disabled tab, it finishes enabled and selected.
      //pointed out by dlorde
      if (flg) _dnDTabbedPaneData.getTabbedPane().setSelectedIndex(tgtindex);

      //I have a component in all tabs (jlabel with an X to close the tab) and when i move a tab the component disappear.
      //pointed out by Daniel Dario Morales Salas
      _dnDTabbedPaneData.getTabbedPane().setTabComponentAt(tgtindex, tab);
   }


   private boolean isDragAcceptable(DropTargetDragEvent e)
   {
      return dragAndDropAllowed(e.getTransferable());
   }

   private boolean isDropAcceptable(DropTargetDropEvent e)
   {
      return dragAndDropAllowed(e.getTransferable());
   }

   private boolean dragAndDropAllowed(Transferable transferable)
   {
      if (transferable == null)
      {
         return false;
      }

      DataFlavor[] f = transferable.getTransferDataFlavors();

      if(false == transferable.isDataFlavorSupported(f[0]))
      {
         return false;
      }

      if (null != _outwardDndTabbedPaneChanel && null != _outwardDndTabbedPaneChanel.getDndTabbedPaneData())
      {
         return true;
      }

      if (_dnDTabbedPaneData.getDragTabIndex() >= 0)
      {
         return true;
      }
      return false;
   }
}
