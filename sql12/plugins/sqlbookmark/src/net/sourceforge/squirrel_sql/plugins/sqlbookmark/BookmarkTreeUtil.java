package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class BookmarkTreeUtil
{
   public static void selectNode(JTree treBookmarks, DefaultMutableTreeNode toSel)
   {
      TreeNode[] pathToRoot = ((DefaultTreeModel) treBookmarks.getModel()).getPathToRoot(toSel);
      treBookmarks.setSelectionPath(new TreePath(pathToRoot));
   }

   public static List<DefaultMutableTreeNode> getAllBookmarkNodes(DefaultMutableTreeNode parent)
   {
      return getAllSubNodes(parent).stream().filter(n -> n.getUserObject() instanceof Bookmark).toList();
   }

   private static List<DefaultMutableTreeNode> getAllSubNodes(DefaultMutableTreeNode parent)
   {
      List<DefaultMutableTreeNode> subNodes = new ArrayList<>();
      int childCount = parent.getChildCount();

      for(int i = 0; i < childCount; i++)
      {
         DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) parent.getChildAt(i);
         subNodes.add(childNode);
         // Recursively get sub-nodes of the child node
         subNodes.addAll(getAllSubNodes(childNode));
      }

      return subNodes;
   }

   public static boolean isMyPredecessor(DefaultMutableTreeNode nodeToCheck, DefaultMutableTreeNode predecessorCandidate)
   {
      for(TreeNode node : nodeToCheck.getPath())
      {
         if(node == predecessorCandidate)
         {
            return true;
         }
      }

      return false;
   }

   public static List<DefaultMutableTreeNode> getAllChildFoldersShallow(DefaultMutableTreeNode parent)
   {
      ArrayList<DefaultMutableTreeNode> ret = new ArrayList<>();

      for(int i = 0; i < parent.getChildCount(); i++)
      {
         DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(i);
         if(child.getUserObject() instanceof UserBookmarkFolder)
         {
            ret.add(child);
         }
      }

      return ret;
   }

   public static boolean areAllPathsChildrenOf(TreePath[] selectionPaths, DefaultMutableTreeNode parent)
   {
      for(TreePath selectionPath : selectionPaths)
      {
         boolean parentFoundInParentPath = false;
         for(int i = 0; i < selectionPath.getPath().length - 1; i++)
         {
            if(selectionPath.getPath()[i] == parent)
            {
               parentFoundInParentPath = true;
               break;
            }
         }

         if(false == parentFoundInParentPath)
         {
            return false;
         }
      }

      return true;
   }
}
