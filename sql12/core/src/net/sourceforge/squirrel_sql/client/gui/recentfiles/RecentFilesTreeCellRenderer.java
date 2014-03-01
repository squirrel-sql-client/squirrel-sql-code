package net.sourceforge.squirrel_sql.client.gui.recentfiles;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.File;

public class RecentFilesTreeCellRenderer extends DefaultTreeCellRenderer
{
   private IApplication _app;

   public RecentFilesTreeCellRenderer(IApplication app)
   {
      _app = app;
   }

   @Override
   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
   {
      JLabel treeCellRendererComponent = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

      DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) value;

      if(false == dmtn.getUserObject() instanceof File)
      {
         return treeCellRendererComponent;
      }

      File file = (File) dmtn.getUserObject();

      if(file.isDirectory())
      {
         treeCellRendererComponent.setIcon(_app.getResources().getIcon(SquirrelResources.IImageNames.DIR_GIF));
      }
      else
      {
         treeCellRendererComponent.setIcon(_app.getResources().getIcon(SquirrelResources.IImageNames.FILE_GIF));
      }

      return treeCellRendererComponent;




   }
}
