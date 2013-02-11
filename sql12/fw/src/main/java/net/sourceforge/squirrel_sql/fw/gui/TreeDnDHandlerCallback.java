package net.sourceforge.squirrel_sql.fw.gui;


import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.dnd.DropTargetDropEvent;
import java.util.ArrayList;

public interface TreeDnDHandlerCallback
{
   public boolean nodeAcceptsKids(DefaultMutableTreeNode selNode);

   void dndExecuted();

   /**
    * Will only be called when external drop is allowed.
    */
   ArrayList<DefaultMutableTreeNode> createPasteTreeNodesFromExternalTransfer(DropTargetDropEvent dtde, TreePath targetPath);
}
