package net.sourceforge.squirrel_sql.client.gui.builders.dndtabbedpane;

import javax.swing.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;

public class DnDTabbedPane extends JTabbedPane
{
   private final GhostGlassPane _glassPane;

   private DnDTabbedPaneData _dnDTabbedPaneData;

   public DnDTabbedPane()
   {
      this(null);
   }

   public DnDTabbedPane(OutwardDndTabbedPaneChanel outwardDndTabbedPaneChanel)
   {
      _dnDTabbedPaneData = new DnDTabbedPaneData(this);
      _glassPane = new GhostGlassPane(_dnDTabbedPaneData);

      DragSourceListener dsl = new DndTabbedPaneDragSourceListener(_glassPane, _dnDTabbedPaneData, outwardDndTabbedPaneChanel);
      Transferable t = new DndTabbedPaneTransferable(_dnDTabbedPaneData);
      DragGestureListener dgl = new DndTabbedPaneDragGestureListener(t, dsl, _dnDTabbedPaneData, _glassPane);

      new DropTarget(_glassPane, DnDConstants.ACTION_COPY_OR_MOVE, new DndTabbedPaneDropTargetListener(_dnDTabbedPaneData, _glassPane, outwardDndTabbedPaneChanel), true);

      if (null != outwardDndTabbedPaneChanel)
      {
         new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new OutwardDndTabbedPaneDropTargetListener(_dnDTabbedPaneData, _glassPane, outwardDndTabbedPaneChanel), true);
      }

      new DragSource().createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, dgl);
   }


   public void setPaintGhost(boolean flag)
   {
      _dnDTabbedPaneData.setHasGhost(flag);
   }


   public void setPaintScrollArea(boolean flag)
   {
      _dnDTabbedPaneData.setPaintScrollArea(flag);
   }


}
