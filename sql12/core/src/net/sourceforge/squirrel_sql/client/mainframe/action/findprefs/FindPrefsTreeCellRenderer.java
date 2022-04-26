package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class FindPrefsTreeCellRenderer extends DefaultTreeCellRenderer
{
   @Override
   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
   {
      Component renderer = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

      if(value instanceof DefaultMutableTreeNode
         && ((DefaultMutableTreeNode)value).getUserObject() instanceof PathEntry)
      {
         PathEntry pathEntry = (PathEntry) ((DefaultMutableTreeNode)value).getUserObject();

         if(pathEntry.isThisEntryMatchesFilter())
         {
            if(selected)
            {
               renderer.setBackground(Color.green.darker());
            }
            else
            {
               renderer.setBackground(Color.green);
            }

            ((JComponent) renderer).setOpaque(true);
         }
         else
         {
            renderer.setBackground(null);
            ((JComponent) renderer).setOpaque(false);
         }
      }

      return renderer;
   }
}
