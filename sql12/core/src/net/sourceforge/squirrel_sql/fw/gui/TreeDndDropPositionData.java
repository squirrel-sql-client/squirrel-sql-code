package net.sourceforge.squirrel_sql.fw.gui;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class TreeDndDropPositionData
{
   private final DefaultMutableTreeNode _node;
   private final TreeDndDropPosition _pos;

   public TreeDndDropPositionData(DefaultMutableTreeNode node, TreeDndDropPosition pos)
   {
      _node = node;
      _pos = pos;
   }

   public DefaultMutableTreeNode getNode()
   {
      return _node;
   }

   public TreeDndDropPosition getPos()
   {
      return _pos;
   }

   public static TreeDndDropPositionData empty()
   {
      return new TreeDndDropPositionData(null, TreeDndDropPosition.NONE);
   }

   public TreePath getTreePath()
   {
      if (null != _node)
      {
         return new TreePath(_node.getPath());
      }
      else
      {
         return null;
      }
   }

   public boolean same(TreeDndDropPositionData other)
   {
      return _pos == other._pos && _node == other._node;
   }
}
