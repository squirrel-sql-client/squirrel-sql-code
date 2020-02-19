package net.sourceforge.squirrel_sql.fw.gui;

import net.sourceforge.squirrel_sql.client.gui.db.aliastransfer.AliasDndExport;
import net.sourceforge.squirrel_sql.client.gui.db.aliastransfer.AliasDndImport;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.util.ArrayList;
import java.util.TooManyListenersException;

/**
 * If a tree is based upon DefaultTreeModel and DefaultMutableTreeNodes this class
 * can handle default (Windows Explorer like) drag and drop.
 *
 * All one needs to do is to pass this class the tree itself and a callback that decides
 * whether dropped nodes should be children or siblings of the node they where dropped at.
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
      try
      {
         TreePath[] toPaste;

         TreePath targetPath = _tree.getClosestPathForLocation(dtde.getLocation().x, dtde.getLocation().y);

         if(false == dtde.isLocalTransfer())
         {
            if (false == _allowExternalDrop)
            {
               return;
            }

            ArrayList<DefaultMutableTreeNode> nodes = _treeDnDHandlerCallback.getPasteTreeNodesFromExternalTransfer(dtde, targetPath);

            ArrayList<TreePath> buf = new ArrayList<>();
            for (DefaultMutableTreeNode node : nodes)
            {
               buf.add(new TreePath(node));
            }

            toPaste = buf.toArray(new TreePath[0]);
         }
         else
         {
            toPaste = _treeDnDHandlerCallback.getPasteTreeNodesFromInternalTransfer(dtde, targetPath, _tree.getSelectionPaths());
         }

         if(0 != (DnDConstants.ACTION_COPY_OR_MOVE & dtde.getDropAction()))
         {
            execCopyOrMove(toPaste, targetPath, isPlaceAbove(dtde));
            _treeDnDHandlerCallback.dndExecuted();
         }
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private boolean isPlaceAbove(DropTargetDropEvent dtde)
   {
      int row = _tree.getRowForLocation(dtde.getLocation().x, dtde.getLocation().y);

      if(-1 == row)
      {
         return false;
      }

      Rectangle rowBounds = _tree.getRowBounds(row);

      int distToTopOfRow = Math.abs(rowBounds.y - dtde.getLocation().y);

      if(distToTopOfRow * 4 < rowBounds.height)
      {
         return true;
      }

      return false;
   }


   /**
    * - pathsToPaste will be appended to the target last path component of the targetPath or its previous sibling when placeAbove iss true.
    * - The last path components of pathsToPaste will be removed from their previous parents only if those parents exist in the current tree.
    */
   public void execCopyOrMove(TreePath[] pathsToPaste, TreePath targetPath, boolean placeAbove)
   {
      DefaultTreeModel dtm = (DefaultTreeModel) _tree.getModel();

      ArrayList<DefaultMutableTreeNode> cutNodes = new ArrayList<>();

      if (null == targetPath)
      {
         DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();

         if (_treeDnDHandlerCallback.nodeAcceptsKids(root))
         {
            cutNodes = cutDraggedNodes(pathsToPaste, targetPath, dtm);

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

         if (false == placeAbove && _treeDnDHandlerCallback.nodeAcceptsKids(selNode))
         {
            cutNodes = cutDraggedNodes(pathsToPaste, targetPath, dtm);
            for (int i = 0; i < cutNodes.size(); i++)
            {
               selNode.insert(cutNodes.get(i), 0);
               dtm.nodesWereInserted(selNode, new int[]{0});
            }
         }
         else
         {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selNode.getParent();
            if (_treeDnDHandlerCallback.nodeAcceptsKids(parent))
            {
               cutNodes = cutDraggedNodes(pathsToPaste, targetPath, dtm);
               for (int i = 0; i < cutNodes.size(); i++)
               {
                  if (null == selNode.getPreviousSibling())
                  {
                     parent.insert(cutNodes.get(i), 0);
                     dtm.nodesWereInserted(parent, new int[]{0});
                  }
                  else
                  {
                     int childIndex = parent.getIndex(selNode) + 1;
                     parent.insert(cutNodes.get(i), childIndex);
                     dtm.nodesWereInserted(parent, new int[]{childIndex});
                  }
               }
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

   private ArrayList<DefaultMutableTreeNode> cutDraggedNodes(TreePath[] pathsToPaste, TreePath targetPath, DefaultTreeModel dtm)
   {
      ArrayList<DefaultMutableTreeNode> cutNodes = new ArrayList<>();

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
