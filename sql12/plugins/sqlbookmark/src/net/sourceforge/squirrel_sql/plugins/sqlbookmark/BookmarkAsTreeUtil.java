package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import net.sourceforge.squirrel_sql.fw.props.Props;
import org.apache.commons.lang3.StringUtils;

public class BookmarkAsTreeUtil
{
   private static final List<Character> TREE_PATH_SEPARATORS = List.of('.', '/', '\\', '|', ',', ';', ':', '-', '_', '+', '#', '%', '&');
   private static final String PREF_DISPLAY_BOOKMARKS_AS_TREE = "BookmarkPlugin.DisplayBookmarksAsTree";
   private static final String PREF_TREE_PATH_SEPARATOR = "BookmarkPlugin.TreePathSeparator";

   static void displayUserBookMarksAsTree(JTree treBookmarks, DefaultMutableTreeNode nodeUserMarks, char separatorChar)
   {
      List<DefaultMutableTreeNode> leaves = getLeaves(nodeUserMarks);
      nodeUserMarks.removeAllChildren();


      leaves.forEach(l -> getOrCreateParent(nodeUserMarks, l, separatorChar).add(l));

      ((DefaultTreeModel) treBookmarks.getModel()).nodeStructureChanged(nodeUserMarks);
      treBookmarks.expandPath(new TreePath(nodeUserMarks.getPath()));
   }

   private static DefaultMutableTreeNode getOrCreateParent(DefaultMutableTreeNode nodeUserMarks, DefaultMutableTreeNode leave, char separatorChar)
   {
      String[] folders = StringUtils.split(((Bookmark) leave.getUserObject()).getName(), separatorChar);

      if(1 == folders.length)
      {
         return nodeUserMarks;
      }

      DefaultMutableTreeNode parent = nodeUserMarks;

      for(int j = 0; j < folders.length - 1; j++)
      {
         String folder = folders[j];

         boolean found = false;
         for(int i = 0; i < parent.getChildCount(); i++)
         {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(i);
            if(
                  child.getUserObject() instanceof UserBookmarkFolder
                        && StringUtils.equals(folder, ((UserBookmarkFolder) child.getUserObject()).getFolderName())
            )
            {
               parent = child;
               found = true;
               break;
            }
         }

         if(false == found)
         {
            DefaultMutableTreeNode folderNode = new DefaultMutableTreeNode(new UserBookmarkFolder(folder));
            parent.add(folderNode);
            parent = folderNode;
         }
      }
      return parent;
   }


   static void undoDisplayUserBookMarksAsTree(JTree treBookmarks, DefaultMutableTreeNode nodeUserMarks)
   {
      List<DefaultMutableTreeNode> leaves = getLeaves(nodeUserMarks);
      nodeUserMarks.removeAllChildren();

      leaves.forEach(l -> nodeUserMarks.add(l));

      ((DefaultTreeModel) treBookmarks.getModel()).nodeStructureChanged(nodeUserMarks);
      treBookmarks.expandPath(new TreePath(nodeUserMarks.getPath()));
   }

   public static List<DefaultMutableTreeNode> getLeaves(DefaultMutableTreeNode parent)
   {
      List<DefaultMutableTreeNode> leaves = new ArrayList<>();

      fillLeaves(parent, leaves);

      return leaves;
   }

   private static void fillLeaves(DefaultMutableTreeNode parent, List<DefaultMutableTreeNode> leavesToFill)
   {
      if(parent.getUserObject() instanceof Bookmark)
      {
         leavesToFill.add(parent);
      }
      else
      {
         for(int i = 0; i < parent.getChildCount(); i++)
         {
            fillLeaves((DefaultMutableTreeNode) parent.getChildAt(i), leavesToFill);
         }
      }
   }

   static void savePrefs(boolean displayUserBookmarksAsTree, char treePathSeparator)
   {
      Props.putBoolean(PREF_DISPLAY_BOOKMARKS_AS_TREE, displayUserBookmarksAsTree);
      Props.putString(PREF_TREE_PATH_SEPARATOR, "" + treePathSeparator);
   }

   static boolean isDisplayUserBookmarksAsTree()
   {
      return Props.getBoolean(PREF_DISPLAY_BOOKMARKS_AS_TREE, false);
   }

   static List<Character> getTreePathSeparators()
   {
      return TREE_PATH_SEPARATORS;
   }

   static char getSelectedTreePathSeparator()
   {
      return Props.getString(PREF_TREE_PATH_SEPARATOR,  "" + TREE_PATH_SEPARATORS.get(0)).charAt(0);
   }

   static boolean containsLeavesOnly(TreePath[] selectionPaths)
   {
      for(TreePath selectionPath : selectionPaths)
      {
         if(selectionPath.getLastPathComponent() instanceof DefaultMutableTreeNode node)
         {
            if( false == node.getUserObject() instanceof Bookmark )
            {
               return false;
            }

         }
         else
         {
            return false;
         }
      }
      return true;
   }

   static boolean isUserBookmarkChild(DefaultMutableTreeNode selNode, DefaultMutableTreeNode nodeUserMarks)
   {
      for(TreeNode node : selNode.getPath())
      {
         if(node == nodeUserMarks)
         {
            return true;
         }
      }

      return false;
   }

   public static void main(String[] args)
   {
      String[] split = StringUtils.split("...dd.d..ddd.", ".");
      System.out.println("split = " + split);
   }

}
