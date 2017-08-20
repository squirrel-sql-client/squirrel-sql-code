package org.squirrelsql.session.graph;

import javafx.scene.control.TreeItem;

public class WhereClauseCreator
{

   public static String generateWhereClause(TreeItem<WhereConfigColTreeNode> root)
   {

      String ret = null;

      for (TreeItem<WhereConfigColTreeNode> kid : root.getChildren())
      {
         String kidClause = getClause(kid);

         if (null != kidClause)
         {
            if (null == ret)
            {
               ret = kidClause;
            }
            else
            {
               ret += " " + WhereConfigColEnum.AND.name() + " " + kidClause;
            }
         }
      }

      if (null != ret)
      {
         return "WHERE " + ret;
      }
      else
      {
         return "";
      }

   }

   private static String getClause(TreeItem<WhereConfigColTreeNode> node)
   {
      WhereConfigColTreeNode whereConfigColTreeNode = node.getValue();

      if(null == whereConfigColTreeNode.getWhereConfigColEnum())
      {
         return  whereConfigColTreeNode.toString();
      }
      else
      {
         String ret = null;

         for (TreeItem<WhereConfigColTreeNode> kid : node.getChildren())
         {
            String clause = getClause(kid);

            if(null != clause)
            {
               if(null == ret)
               {
                  ret = "(" + clause;
               }
               else
               {
                  ret += " " + whereConfigColTreeNode.getWhereConfigColEnum().name() + " " + clause;
               }
            }
         }

         if(null != ret)
         {
            ret += ")";
         }

         return ret;
      }
   }
}
