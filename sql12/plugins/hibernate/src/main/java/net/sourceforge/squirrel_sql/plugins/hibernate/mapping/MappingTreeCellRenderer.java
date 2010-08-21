package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePluginResources;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.*;
import java.awt.*;

public class MappingTreeCellRenderer extends DefaultTreeCellRenderer
{
   private ImageIcon _propertyIcon;

   public MappingTreeCellRenderer(HibernatePluginResources resource)
   {
      _propertyIcon = resource.getIcon(HibernatePluginResources.IKeys.PROPERTY_IMAGE);
   }


   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
   {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

      JLabel rendererComponent = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

      if(node.getUserObject() instanceof PropertyInfoTreeWrapper)
      {
         rendererComponent.setIcon(_propertyIcon);
      }

      return rendererComponent;
   }
}
