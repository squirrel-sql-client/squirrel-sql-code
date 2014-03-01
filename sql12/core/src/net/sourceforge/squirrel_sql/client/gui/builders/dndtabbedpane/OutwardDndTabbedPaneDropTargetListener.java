package net.sourceforge.squirrel_sql.client.gui.builders.dndtabbedpane;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

public class OutwardDndTabbedPaneDropTargetListener implements DropTargetListener
{

   private final DnDTabbedPaneData _dnDTabbedPaneData;
   private final GhostGlassPane _glassPane;
   private OutwardDndTabbedPaneChanel _outwardDndTabbedPaneChanel;

   public OutwardDndTabbedPaneDropTargetListener(DnDTabbedPaneData dnDTabbedPaneData, GhostGlassPane glassPane, OutwardDndTabbedPaneChanel outwardDndTabbedPaneChanel)
   {
      _dnDTabbedPaneData = dnDTabbedPaneData;
      _glassPane = glassPane;
      _outwardDndTabbedPaneChanel = outwardDndTabbedPaneChanel;

      // Memory Leak
      _outwardDndTabbedPaneChanel.putListener(_dnDTabbedPaneData.getTabbedPane(), new OutwardDndTabbedPaneChanelListener()
      {
         @Override
         public void hideGlassPane()
         {
            DndTabUtils.hideGlassPane(_glassPane, _dnDTabbedPaneData);
         }
      });
   }

   @Override
   public void dragEnter(final DropTargetDragEvent dtde)
   {
//      Timer t = new Timer(100, new ActionListener() {
//         @Override
//         public void actionPerformed(ActionEvent e)
//         {
//            if (null != _outwardDndTabbedPaneChanel.getDndTabbedPaneData())
//            {
//               DndTabUtils.initGlassPane(dtde.getLocation(), _dnDTabbedPaneData, _outwardDndTabbedPaneChanel.getDndTabbedPaneData(), _glassPane);
//            }
//         }
//      });
//      t.setRepeats(false);
//      t.start();

      if (null != _outwardDndTabbedPaneChanel.getDndTabbedPaneData())
      {
         DndTabUtils.initGlassPane(dtde.getLocation(), _dnDTabbedPaneData, _outwardDndTabbedPaneChanel.getDndTabbedPaneData(), _glassPane);
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
   public void dragOver(DropTargetDragEvent e)
   {
   }

   @Override
   public void drop(DropTargetDropEvent e)
   {
   }
}
