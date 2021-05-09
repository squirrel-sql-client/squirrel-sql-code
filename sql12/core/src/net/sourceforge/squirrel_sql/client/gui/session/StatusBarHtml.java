package net.sourceforge.squirrel_sql.client.gui.session;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;

import javax.swing.tree.TreePath;
import java.util.ArrayList;

public class StatusBarHtml
{
   static String createStatusBarHtml(TreePath selPath)
   {
      StringBuffer buf = new StringBuffer();
      buf.append("<html>");
      Object[] fullPath = selPath.getPath();
      for (int i = 0; i < fullPath.length; ++i)
      {
         if (fullPath[i] instanceof ObjectTreeNode)
         {
            ObjectTreeNode node = (ObjectTreeNode)fullPath[i];

            // See linkDescription in getTreePathForLink(...) below.
            String linkDescription = "" + i;
            buf.append('/').append("<a href=\"" + linkDescription + "\">" + node.toString() + "</a>");
         }
      }
      buf.append("</html>");
      final String text = buf.toString();
      return text;
   }

   public static TreePath getTreePathForLink(String linkDescription, TreePath treePathForLink)
   {
      final int pathIndex = Integer.parseInt(linkDescription);

      ArrayList path = new ArrayList();

      for (int i = 0; i <= pathIndex; i++)
      {
         path.add(treePathForLink.getPath()[i]);
      }

      return new TreePath(path.toArray(new Object[0]));
   }
}
