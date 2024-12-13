package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.aliascolor.AliasColor;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
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
   private AliasDragState _aliasDragState;

   private final Icon _folderClosedIcon;
   private final Icon _folderOpenIcon;
   private final ImageIcon _startAliasIcon;

   public AliasTreeCellRenderer(AliasTreePasteState aliasPasteState, AliasDragState aliasDragState)
   {
      _aliasPasteState = aliasPasteState;
      _aliasDragState = aliasDragState;

      _folderClosedIcon = Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.FOLDER_CLOSED);
      _folderOpenIcon = Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.FOLDER_OPEN);
      _startAliasIcon = Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.START_ALIAS);
   }

   @Override
   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
   {
      Component renderer = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

      Color itemColor = AliasColor.getItemColor(value);
      if (itemColor == null)
      {
         renderer.setBackground(null);
         ((JComponent) renderer).setOpaque(false);
      }
      else
      {
         if (sel)
         {
            itemColor = colorWithAlpha(itemColor, (int) (255 * AliasColor.FACTOR));
         }
         renderer.setBackground(itemColor);
         ((JComponent) renderer).setOpaque(true);
      }

      if(value instanceof DefaultMutableTreeNode)
      {
         DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
         if(node.getUserObject() instanceof AliasFolder)
         {
            setIcon(expanded ? _folderOpenIcon : _folderClosedIcon);
         }
         else
         {
            setIcon(_startAliasIcon); // No icon for leaf nodes
         }
      }


      return modifyRenderer(renderer, value);
   }

   static Color colorWithAlpha(Color color, int alpha)
   {
      return AliasColor.colorOf(color.getRGB() & 0x00FFFFFF | (alpha << 24));
   }

   private Component modifyRenderer(Component component, Object node)
   {
      JLabel ret = (JLabel) component;
      ret.setEnabled(true);

      DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) node;

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
