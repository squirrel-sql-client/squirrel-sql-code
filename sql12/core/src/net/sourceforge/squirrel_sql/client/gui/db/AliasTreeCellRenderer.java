package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.gui.db.aliascolor.AliasTreeColorer;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.Component;

public class AliasTreeCellRenderer extends DefaultTreeCellRenderer
{
   private final AliasTreePasteState _aliasPasteState;
   private final AliasTreeColorer _aliasColorer;

   public AliasTreeCellRenderer(AliasTreePasteState aliasPasteState, AliasTreeColorer aliasColorer)
   {
      _aliasPasteState = aliasPasteState;
      _aliasColorer = aliasColorer;
   }

   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
   {
      return modifyRenderer(this, super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus), value);
   }

   private Component modifyRenderer(DefaultTreeCellRenderer defaultTreeCellRenderer, Component component, Object node)
   {
      JLabel ret = (JLabel) component;
      ret.setEnabled(true);

      DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) node;
      _aliasColorer.colorAliasRendererComponent(defaultTreeCellRenderer, dmtn, ret);

      if (null != _aliasPasteState.getPathsToPaste() && AliasTreePasteMode.CUT.equals(_aliasPasteState.getPasteMode()))
      {

         boolean found = false;
         for (TreePath treePath : _aliasPasteState.getPathsToPaste())
         {
            if(treePath.getLastPathComponent() == dmtn)
            {
               found = true;
               break;
            }
         }
         ret.setEnabled(!found);
         ret.setDisabledIcon(ret.getIcon());
      }

      return component;
   }


}
