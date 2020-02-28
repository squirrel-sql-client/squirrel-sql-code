package net.sourceforge.squirrel_sql.fw.gui;


import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.dnd.DropTargetDropEvent;
import java.util.ArrayList;

public interface TreeDnDHandlerCallback
{
   boolean nodeAcceptsKids(DefaultMutableTreeNode selNode);

   default void dndExecuted() {}

   /**
    * Will only be called when external drop is allowed.
    */
   ArrayList<DefaultMutableTreeNode> getPasteTreeNodesFromExternalTransfer(DropTargetDropEvent dtde, TreePath targetPath);

   default TreePath[] getPasteTreeNodesFromInternalTransfer(DropTargetDropEvent dtde, TreePath targetPath, TreePath[] selectionPaths)
   {
      return selectionPaths;
   }

   default void updateDragPosition(TreeDndDropPositionData treeDndDropPositionData) {}
}
