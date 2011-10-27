package net.sourceforge.squirrel_sql.plugins.hibernate.configuration;

import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePluginResources;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ClassPathItem;

import javax.swing.*;
import java.awt.*;

class ClassPathListCellRenderer extends DefaultListCellRenderer
{
   private HibernatePluginResources _resources;

   ClassPathListCellRenderer(HibernatePluginResources resources)
   {
      _resources = resources;
   }

   @Override
   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
   {
      ClassPathItem item = (ClassPathItem) value;
      Component listCellRendererComponent = super.getListCellRendererComponent(list, item.getPath(), index, isSelected, cellHasFocus);

      if(listCellRendererComponent instanceof JLabel)
      {
         ((JLabel)listCellRendererComponent).setIcon(_getIcon(item));

      }
      return listCellRendererComponent;
   }

   public Icon _getIcon(ClassPathItem item)
   {
      if(item.isJarDir())
      {
         return _resources.getIcon(HibernatePluginResources.IKeys.JAR_DIRECTORY_IMAGE);
      }
      else
      {
         return _resources.getIcon(HibernatePluginResources.IKeys.JAR_IMAGE);
      }
   }

}
