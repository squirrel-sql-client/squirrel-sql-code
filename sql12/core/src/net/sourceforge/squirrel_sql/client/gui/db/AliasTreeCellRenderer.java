package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.gui.db.aliascolor.AliasTreeColorer;
import net.sourceforge.squirrel_sql.fw.gui.TreeDndDropPosition;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.Color;
import java.awt.Component;

public class AliasTreeCellRenderer extends DefaultTreeCellRenderer
{
   private final AliasTreePasteState _aliasPasteState;
   private final AliasTreeColorer _aliasColorer;
   private AliasDragState _aliasDragState;

   public AliasTreeCellRenderer(AliasTreePasteState aliasPasteState, AliasTreeColorer aliasColorer, AliasDragState aliasDragState)
   {
      _aliasPasteState = aliasPasteState;
      _aliasColorer = aliasColorer;
      _aliasDragState = aliasDragState;
   }

   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
   {
      return modifyRenderer(super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus), value);
   }

   private Component modifyRenderer(Component component, Object node)
   {
      JLabel ret = (JLabel) component;
      ret.setEnabled(true);

      DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) node;
      _aliasColorer.colorAliasRendererComponent(this, dmtn, ret);

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

      initDropPositionIndicator(ret, dmtn);

      return ret;
   }

   private void initDropPositionIndicator(JLabel ret, DefaultMutableTreeNode dmtn)
   {
      ret.setBorder(BorderFactory.createEmptyBorder());

      if (dmtn != _aliasDragState.getTreeDndDropPositionData().getNode())
      {
         return;
      }

      switch(_aliasDragState.getTreeDndDropPositionData().getPos())
      {
         case ABOVE:
            ret.setBorder(BorderFactory.createMatteBorder(2,0,0,0, Color.blue));
            break;
         case BELOW:
            ret.setBorder(BorderFactory.createMatteBorder(0,0,2,0, Color.blue));
            break;
         case INTO:
            ret.setBorder(BorderFactory.createLineBorder(Color.blue));
            break;
      }
   }
}
