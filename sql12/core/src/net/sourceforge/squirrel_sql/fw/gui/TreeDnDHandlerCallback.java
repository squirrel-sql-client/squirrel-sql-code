package net.sourceforge.squirrel_sql.fw.gui;


import java.awt.dnd.DropTargetDropEvent;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

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

   default boolean allowDND(DefaultMutableTreeNode targetNode, ArrayList<DefaultMutableTreeNode> draggedNodes)
   {
      return true;
   }
}
