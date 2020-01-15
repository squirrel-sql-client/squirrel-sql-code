package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;

public class WhereTreeNodeStructure
{
   private boolean _expanded;
   private WhereClauseOperator _whereOp;
   private String _whereCondDef;
   private String _whereCondDisplay;
   private WhereTreeNodeStructure[] _kids;

   public WhereTreeNodeStructure(DefaultMutableTreeNode node, JTree tree)
   {
      _expanded = tree.isExpanded(new TreePath(node.getPath()));

      if(node.getUserObject() instanceof WhereClauseOperator)
      {
         _whereOp = (WhereClauseOperator)node.getUserObject();
      }
      else // if(node.getUserObject() instanceof WhereConditionColumnWrapper)
      {
         _whereCondDef = ((WhereConditionColumnWrapper)node.getUserObject()).getDefinition();
         _whereCondDisplay = ((WhereConditionColumnWrapper)node.getUserObject()).getDisplay();
      }

      _kids = new WhereTreeNodeStructure[node.getChildCount()];
      for (int i = 0; i < node.getChildCount(); i++)
      {
         _kids[i] = new WhereTreeNodeStructure((DefaultMutableTreeNode)node.getChildAt(i), tree);
      }
   }

   public void applyExpansion(DefaultMutableTreeNode node, JTree tree)
   {
      if(equals(new WhereTreeNodeStructure(node, tree)))
      {
         if (_expanded)
         {
            tree.expandPath(new TreePath(node.getPath()));
         }
      }

      for (int i = 0; i < node.getChildCount() && i < _kids.length; i++)
      {
         DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
         if(_kids[i].equals(new WhereTreeNodeStructure(child, tree)))
         {
            _kids[i].applyExpansion(child, tree);
         }
      }
   }


   @Override
   public boolean equals(Object obj)
   {
      if(false == obj instanceof WhereTreeNodeStructure)
      {
         return false;
      }

      WhereTreeNodeStructure other = (WhereTreeNodeStructure) obj;

      return getKey().equals(other.getKey());
   }

   @Override
   public int hashCode()
   {
      return getKey().hashCode();
   }

   private String getKey()
   {
      return _whereOp + "#" + _whereCondDef;
   }

   public void initTree(DefaultMutableTreeNode root, ArrayList<WhereConditionColumnWrapper> filteredCols, JTree tree)
   {
      _initTree(root, filteredCols);

      for (WhereConditionColumnWrapper filteredCol : filteredCols)
      {
         root.add(new DefaultMutableTreeNode(filteredCol));
      }

      applyExpansion(root, tree);
   }

   private void _initTree(DefaultMutableTreeNode node, ArrayList<WhereConditionColumnWrapper> filteredCols)
   {
      for (int i = 0; i < _kids.length; i++)
      {
         if(null == _kids[i]._whereCondDef)
         {
            DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(_kids[i]._whereOp);
            node.add(newChild);
            _kids[i]._initTree(newChild, filteredCols);
         }
         else
         {
            WhereConditionColumnWrapper wrapper = getAndRemove(_kids[i]._whereCondDef, filteredCols);
            if(null != wrapper)
            {
               DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(wrapper);
               node.add(newChild);
            }
         }
      }
   }

   private WhereConditionColumnWrapper getAndRemove(String whereCondDef, ArrayList<WhereConditionColumnWrapper> filteredCols)
   {
      for (WhereConditionColumnWrapper filteredCol : filteredCols)
      {
         if(filteredCol.getDefinition().equalsIgnoreCase(whereCondDef))
         {
            filteredCols.remove(filteredCol);
            return filteredCol;
         }
      }
      return null;
   }

   public String generateWhereClause()
   {
      if(WhereClauseOperator.WHERE != _whereOp)
      {
         throw new UnsupportedOperationException("This method should only be called on the where node");
      }

      String ret = null;

      for (int i = 0; i < _kids.length; i++)
      {
         String kidClause = _kids[i]._getWhereClause();

         if (null != kidClause)
         {
            if(null == ret)
            {
               ret = kidClause;
            }
            else
            {
               ret += " " + WhereClauseOperator.AND.getOpSQL() + " " + kidClause;
            }
         }
      }

      if (null != ret)
      {
         return WhereClauseOperator.WHERE.getOpSQL() + " " +ret;
      }
      else
      {
         return "";
      }

   }

   private String _getWhereClause()
   {
      if(null == _whereOp)
      {
         return _whereCondDisplay;
      }
      else
      {
         String ret = null;

         for (int i = 0; i < _kids.length; i++)
         {
            String kidClause = _kids[i]._getWhereClause();
            if (null != kidClause)
            {
               if(null == ret)
               {
                  ret = kidClause;
               }
               else
               {
                  ret += " " + _whereOp.getOpSQL() + " " + kidClause;
               }
            }
         }

         if(null != ret)
         {
            ret = "(" + ret + ")";
         }

         return ret;
      }
   }


   /////////////////////////////////////////////////////////////////////////
   // Needed for XML Serialization
   public WhereTreeNodeStructure()
   {
   }

   public boolean isExpanded()
   {
      return _expanded;
   }

   public void setExpanded(boolean expanded)
   {
      _expanded = expanded;
   }

   public int getWhereOpIdx()
   {
      if (null == _whereOp)
      {
         return -1;
      }
      else
      {
         return _whereOp.getIdx();
      }
   }

   public void setWhereOpIdx(int whereOpIdx)
   {
      if (-1 == whereOpIdx)
      {
         _whereOp = null;
      }
      else
      {
         _whereOp = WhereClauseOperator.getByIx(whereOpIdx);
      }
   }

   public String getWhereCondDef()
   {
      return _whereCondDef;
   }

   public void setWhereCondDef(String whereCondDef)
   {
      _whereCondDef = whereCondDef;
   }

   public WhereTreeNodeStructure[] getKids()
   {
      return _kids;
   }

   public void setKids(WhereTreeNodeStructure[] kids)
   {
      _kids = kids;
   }
   //
   /////////////////////////////////////////////////////////////////////////

}
