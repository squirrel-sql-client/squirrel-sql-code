package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.apache.commons.lang3.StringUtils;

public class BookmarkTreeState
{
   private final JTree _treBookmarks;
   private final DefaultMutableTreeNode _nodeSquirrelMarks;
   private final List<DefaultMutableTreeNode> _expandedNodes = new ArrayList<>();


   public BookmarkTreeState(JTree treBookmarks, DefaultMutableTreeNode nodeSquirrelMarks)
   {
      _treBookmarks = treBookmarks;
      _nodeSquirrelMarks = nodeSquirrelMarks;

      DefaultMutableTreeNode root = (DefaultMutableTreeNode) treBookmarks.getModel().getRoot();

      fillExpanded(root);
   }

   private void fillExpanded(DefaultMutableTreeNode node)
   {
      if(_treBookmarks.isExpanded(new TreePath(node.getPath())))
      {
         _expandedNodes.add(node);
         for(int i = 0; i < node.getChildCount(); i++)
         {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            fillExpanded(child);
         }
      }
   }

   /**
    *
    * @param selNode null, means no selection
    */
   void applyState(DefaultMutableTreeNode selNode)
   {
      boolean squirrelPathExpanded = _treBookmarks.isExpanded(new TreePath(_nodeSquirrelMarks.getPath()));

      DefaultTreeModel model = (DefaultTreeModel) _treBookmarks.getModel();
      TreeNode root = (TreeNode) model.getRoot();
      model.setRoot(null);

      model.setRoot(root);

      applyExpanded((DefaultMutableTreeNode) root);

      if(null != selNode)
      {
         _treBookmarks.setSelectionPath(new TreePath(selNode.getPath()));
      }

      if(squirrelPathExpanded)
      {
         _treBookmarks.expandPath(new TreePath(_nodeSquirrelMarks.getPath()));
      }
   }

   private void applyExpanded(DefaultMutableTreeNode root)
   {
      for(DefaultMutableTreeNode formerExpandedNode : _expandedNodes)
      {
         DefaultMutableTreeNode res = findByUserObject(formerExpandedNode, root);
         if(null != res)
         {
            _treBookmarks.expandPath(new TreePath(res.getPath()));
         }
      }
   }

   private DefaultMutableTreeNode findByUserObject(DefaultMutableTreeNode formerExpandedNode, DefaultMutableTreeNode parent)
   {
      if(Objects.equals(formerExpandedNode.getUserObject(), parent.getUserObject())
         ||
         (
               formerExpandedNode.getUserObject() instanceof UserBookmarkFolder userBookmarkFolder1
            && parent.getUserObject() instanceof UserBookmarkFolder userBookmarkFolder2
            && StringUtils.equals(userBookmarkFolder1.getFolderName(), userBookmarkFolder2.getFolderName())
         )
      )
      {
         return parent;
      }

      for(int i = 0; i < parent.getChildCount(); i++)
      {
         DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(i);
         DefaultMutableTreeNode res = findByUserObject(formerExpandedNode, child);
         if(null != res)
         {
            return res;
         }
      }

      return null;
   }

}
