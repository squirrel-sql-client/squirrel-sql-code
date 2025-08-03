package net.sourceforge.squirrel_sql.fw.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TooManyListenersException;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

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
            @Override
            public void drop(DropTargetDropEvent dtde)
            {
               _treeDnDHandlerCallback.updateDragPosition(TreeDndDropPositionData.empty());
               onDrop(dtde);
            }

            @Override
            public void dragEnter(DropTargetDragEvent dtde)
            {
               _treeDnDHandlerCallback.updateDragPosition(getTreeDndDropPositionData(dtde.getLocation()));
            }
            @Override
            public void dragOver(DropTargetDragEvent dtde)
            {
               _treeDnDHandlerCallback.updateDragPosition(getTreeDndDropPositionData(dtde.getLocation()));
            }

            @Override
            public void dragExit(DropTargetEvent dte)
            {
               _treeDnDHandlerCallback.updateDragPosition(TreeDndDropPositionData.empty());
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

         TreeDndDropPositionData treeDndDropPositionData = getTreeDndDropPositionData(dtde.getLocation());

         TreePath targetPath = treeDndDropPositionData.getTreePath();

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
            execCopyOrMove(toPaste, treeDndDropPositionData);
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
      return isPlaceAbove(dtde.getLocation());
   }

   private boolean isPlaceAbove(Point location)
   {
      int row = _tree.getRowForLocation(location.x, location.y);

      if(-1 == row)
      {
         return false;
      }

      Rectangle rowBounds = _tree.getRowBounds(row);

      int distToTopOfRow = Math.abs(rowBounds.y - location.y);

      if(distToTopOfRow * 4 < rowBounds.height)
      {
         return true;
      }

      return false;
   }


   public void execCopyOrMove(TreePath[] pathsToPaste, TreeDndDropPositionData treeDndDropPositionData)
   {
      DefaultTreeModel dtm = (DefaultTreeModel) _tree.getModel();

      ArrayList<DefaultMutableTreeNode> cutNodes = new ArrayList<>();

      if (null == treeDndDropPositionData.getTreePath())
      {
         DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();

         if (_treeDnDHandlerCallback.nodeAcceptsKids(root))
         {
            cutNodes = cutDraggedNodes(pathsToPaste, null, dtm);

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
         cutNodes = cutDraggedNodes(pathsToPaste, treeDndDropPositionData.getTreePath(), dtm);

         DefaultMutableTreeNode targetNode = treeDndDropPositionData.getNode();

         switch (treeDndDropPositionData.getPos())
         {
            case INTO_ROOT:
               for (int i = 0; i < cutNodes.size(); i++)
               {
                  targetNode.add(cutNodes.get(i));
                  dtm.nodesWereInserted(targetNode, new int[]{targetNode.getChildCount() - 1});
               }
               break;
            case INTO:
               for (int i = 0; i < cutNodes.size(); i++)
               {
                  targetNode.insert(cutNodes.get(i), 0);
                  dtm.nodesWereInserted(targetNode, new int[]{0});
               }
               break;
            case ABOVE:
               DefaultMutableTreeNode parent1 = (DefaultMutableTreeNode) targetNode.getParent();
               int index1 = parent1.getIndex(targetNode);

               ArrayList<DefaultMutableTreeNode> reverted1 = new ArrayList<>(cutNodes);
               Collections.reverse(reverted1);
               for (int i = 0; i < reverted1.size(); i++)
               {
                  parent1.insert(cutNodes.get(i), index1);
                  dtm.nodesWereInserted(parent1, new int[]{index1});
               }
               break;
            case BELOW:
               DefaultMutableTreeNode parent2 = (DefaultMutableTreeNode) targetNode.getParent();
               int index2 = parent2.getIndex(targetNode) + 1;

               ArrayList<DefaultMutableTreeNode> reverted2 = new ArrayList<>(cutNodes);
               Collections.reverse(reverted2);
               for (int i = 0; i < reverted2.size(); i++)
               {
                  parent2.insert(cutNodes.get(i), index2);
                  dtm.nodesWereInserted(parent2, new int[]{index2});
               }
               break;

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


   public TreeDndDropPositionData getTreeDndDropPositionData(Point pos)
   {
      TreePath targetPath = _tree.getClosestPathForLocation(pos.x, pos.y);

      if (null == targetPath || ((DefaultMutableTreeNode)targetPath.getLastPathComponent()).isRoot())
      {
         DefaultTreeModel dtm = (DefaultTreeModel) _tree.getModel();
         DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();

         if (_treeDnDHandlerCallback.nodeAcceptsKids(root))
         {
            return new TreeDndDropPositionData(root, TreeDndDropPosition.INTO_ROOT);
         }
         else
         {
            throw new IllegalStateException("Root does not allow kids.");
         }
      }

      DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) targetPath.getLastPathComponent();

      int row = _tree.getRowForPath(targetPath);

      Rectangle rowBounds = _tree.getRowBounds(row);

      int distToTopOfRow = Math.abs(rowBounds.y - pos.y);
      int distToBottomOfRow = Math.abs(rowBounds.y + rowBounds.height - pos.y);

      if (_treeDnDHandlerCallback.nodeAcceptsKids(targetNode))
      {
         TreeDndDropPosition treeDndDropPosition = TreeDndDropPosition.INTO;
         if(distToTopOfRow * 4 < rowBounds.height)
         {
            treeDndDropPosition = TreeDndDropPosition.ABOVE;
         }
         else if(distToBottomOfRow * 4 < rowBounds.height)
         {
            treeDndDropPosition = TreeDndDropPosition.BELOW;
         }

         return new TreeDndDropPositionData(targetNode, treeDndDropPosition);
      }
      else
      {
         TreeDndDropPosition treeDndDropPosition = TreeDndDropPosition.ABOVE;
         if(distToTopOfRow > distToBottomOfRow)
         {
            treeDndDropPosition = TreeDndDropPosition.BELOW;
         }
         return new TreeDndDropPositionData(targetNode, treeDndDropPosition);
      }
   }
}
