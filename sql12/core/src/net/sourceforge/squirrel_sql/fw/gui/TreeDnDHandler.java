package net.sourceforge.squirrel_sql.fw.gui;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.util.ArrayList;
import java.util.TooManyListenersException;

/**
 * If a tree is bssed upon DefaultTreeModel and DefaultMutableTreeNodes this class
 * can handle default (Windows Explorer like) drag and drop.
 *
 * All one needs to do is to pass this class the tree itself and a callback that decides
 * whether droped nodes should be childeren or siblings of the node they where droped at.
 *
 */
public class TreeDnDHandler
{
   private JTree _tree;
   private TreeDnDHandlerCallback _treeDnDHandlerCallback;
   private boolean _allowExternalDrop;

   public TreeDnDHandler(JTree tree, TreeDnDHandlerCallback treeDnDHandlerCallback)
   {
      this(tree, treeDnDHandlerCallback, false);

   }

   public TreeDnDHandler(JTree tree, TreeDnDHandlerCallback treeDnDHandlerCallback, boolean allowExternalDrop)
   {
      _tree = tree;
      _treeDnDHandlerCallback = treeDnDHandlerCallback;
      _allowExternalDrop = allowExternalDrop;

      initDnD();
   }

   private void initDnD()
   {
      try
      {
         _tree.setDragEnabled(true);
         DropTarget dt = new DropTarget();

         dt.addDropTargetListener(new DropTargetAdapter()
         {
            public void drop(DropTargetDropEvent dtde)
            {
               onDrop(dtde);
            }
         });

         _tree.setDropTarget(dt);
      }
      catch (TooManyListenersException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void onDrop(DropTargetDropEvent dtde)
   {
      TreePath[] toPaste;

      TreePath targetPath = _tree.getClosestPathForLocation(dtde.getLocation().x, dtde.getLocation().y);

      if(false == dtde.isLocalTransfer())
      {
         if (false == _allowExternalDrop)
         {
            return;
         }

         ArrayList<DefaultMutableTreeNode> nodes = _treeDnDHandlerCallback.createPasteTreeNodesFromExternalTransfer(dtde, targetPath);

         ArrayList<TreePath> buf = new ArrayList<TreePath>();
         for (DefaultMutableTreeNode node : nodes)
         {
            buf.add(new TreePath(node));
         }

         toPaste = buf.toArray(new TreePath[buf.size()]);
      }
      else
      {
         toPaste = _tree.getSelectionPaths();
      }



      if(0 != (DnDConstants.ACTION_COPY_OR_MOVE & dtde.getDropAction()))
      {
         execCut(toPaste, targetPath);
         _treeDnDHandlerCallback.dndExecuted();
      }
   }


   public void execCut(TreePath[] pathsToPaste, TreePath targetPath)
   {
      DefaultTreeModel dtm = (DefaultTreeModel) _tree.getModel();

      ArrayList<DefaultMutableTreeNode> cutNodes = new ArrayList<DefaultMutableTreeNode>();

      if (null == targetPath)
      {
         DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();

         if (_treeDnDHandlerCallback.nodeAcceptsKids(root))
         {
            cutNodes = cutDragedNodes(pathsToPaste, targetPath, dtm);

            int[] childIndices = new int[cutNodes.size()];
            for (int i = 0; i < cutNodes.size(); i++)
            {
               childIndices[i] = root.getChildCount();
               root.add(cutNodes.get(i));
            }
            dtm.nodesWereInserted(root, childIndices);
         }
      }
      else
      {
         DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) targetPath.getLastPathComponent();

         if (_treeDnDHandlerCallback.nodeAcceptsKids(selNode))
         {
            cutNodes = cutDragedNodes(pathsToPaste, targetPath, dtm);
            for (int i = 0; i < cutNodes.size(); i++)
            {
               selNode.insert(cutNodes.get(i), 0);
            }
            dtm.nodeStructureChanged(selNode);
         }
         else
         {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selNode.getParent();
            if (_treeDnDHandlerCallback.nodeAcceptsKids(parent))
            {
               cutNodes = cutDragedNodes(pathsToPaste, targetPath, dtm);
               for (int i = 0; i < cutNodes.size(); i++)
               {
                  if (null == selNode.getPreviousSibling())
                  {
                     parent.insert(cutNodes.get(i), 0);
                  }
                  else
                  {
                     parent.insert(cutNodes.get(i), parent.getIndex(selNode) + 1);
                  }
               }
               dtm.nodeStructureChanged(parent);
            }
         }
      }

      TreePath[] newSelPaths = new TreePath[cutNodes.size()];
      for (int i = 0; i < newSelPaths.length; i++)
      {
         newSelPaths[i] = new TreePath(cutNodes.get(i).getPath());
      }
      _tree.setSelectionPaths(newSelPaths);
   }

   private ArrayList<DefaultMutableTreeNode> cutDragedNodes(TreePath[] pathsToPaste, TreePath targetPath, DefaultTreeModel dtm)
   {
      ArrayList<DefaultMutableTreeNode> cutNodes = new ArrayList<DefaultMutableTreeNode>();

      for (int i = 0; i < pathsToPaste.length; i++)
      {
         if(false == pathsToPaste[i].equals(targetPath))
         {
            DefaultMutableTreeNode cutNode = (DefaultMutableTreeNode) pathsToPaste[i].getLastPathComponent();
            cutNodes.add(cutNode);
            if (null != cutNode.getParent())
            {
               dtm.removeNodeFromParent(cutNode);
            }
         }
      }
      return cutNodes;
   }
}
