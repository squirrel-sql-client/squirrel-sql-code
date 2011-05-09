package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;

public class WhereTreeSync
{
   public void sync(DefaultMutableTreeNode root, ArrayList<WhereConditionColumnWrapper> newCols)
   {
      ArrayList<WhereConditionColumnWrapper> curCols = getAllChildCols(root);

      for (WhereConditionColumnWrapper newCol : newCols)
      {
         if(false == curCols.contains(newCol))
         {
            root.add(new DefaultMutableTreeNode(newCol));
         }
      }

      for (WhereConditionColumnWrapper curCol : curCols)
      {
         if(false == newCols.contains(curCol))
         {
            remove(root, curCol);
         }
      }
   }

   private boolean remove(DefaultMutableTreeNode node, WhereConditionColumnWrapper curCol)
   {
      for (int i = 0; i < node.getChildCount(); i++)
      {
         DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);

         if(child.getUserObject().equals(curCol))
         {
            node.remove(i);
            return true;
         }
         else
         {
            if(remove(child, curCol))
            {
               return true;
            }
         }
      }

      return false;
   }


   private ArrayList<WhereConditionColumnWrapper> getAllChildCols(DefaultMutableTreeNode node)
   {
      ArrayList<WhereConditionColumnWrapper> ret = new ArrayList<WhereConditionColumnWrapper>();

      if (node.getUserObject() instanceof WhereClauseOperator)
      {
         for (int i = 0; i < node.getChildCount(); i++)
         {
            ret.addAll(getAllChildCols((DefaultMutableTreeNode) node.getChildAt(i)));
         }
      }
      else
      {
         ret.add((WhereConditionColumnWrapper) node.getUserObject());
      }

      return ret;

   }

   public void removeFolder(DefaultMutableTreeNode node)
   {
      ArrayList<WhereConditionColumnWrapper> allChildCols = getAllChildCols(node);

      DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();

      parent.remove(node);

      for (WhereConditionColumnWrapper col : allChildCols)
      {
         parent.add(new DefaultMutableTreeNode(col));
      }
   }
}
