package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.ColorPropertiesPanel;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JColorChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;

public class AliasColorSelectionHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasColorSelectionHandler.class);

   public static void selectColor(AliasesListModel model)
   {

   }

   public static void selectColor(JTree aliasTree)
   {
      TreePath[] selectionPaths = aliasTree.getSelectionPaths();

      if(0 == selectionPaths.length)
      {
         Dialogs.showOk(Main.getApplication().getMainFrame(), s_stringMgr.getString("select.alias.to.color"));
         return;
      }

      Color startColor = getStartColor(selectionPaths);


      if(null != startColor)
      {
         JPopupMenu popupMenu = new JPopupMenu();

         popupMenu.add(createChooseColorItem(aliasTree, startColor));
         popupMenu.add(createRemoveColorItem(aliasTree));

         Point p = MouseInfo.getPointerInfo().getLocation();

         SwingUtilities.convertPointFromScreen(p, aliasTree);

         popupMenu.show(aliasTree, p.x, p.y);
      }
      else
      {
         colorSelection(aliasTree, startColor, false);
      }
   }

   private static void colorSelection(JTree aliasTree, Color startColor, boolean remove)
   {

      Color newColor = null;

      if (false == remove)
      {
         newColor = JColorChooser.showDialog(aliasTree, ColorPropertiesPanel.i18n.ALIAS_BACKGROUND_COLOR_CHOOSER_DIALOG_TITLE, startColor);

         if(null == newColor)
         {
            return;
         }
      }

      TreePath[] selectionPaths = aliasTree.getSelectionPaths();

      for (TreePath selectionPath : selectionPaths)
      {
         DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();

         if(selNode.getUserObject() instanceof SQLAlias)
         {
            if (remove)
            {
               ((SQLAlias)selNode.getUserObject()).getColorProperties().setOverrideObjectTreeBackgroundColor(false);
            }
            else
            {
               ((SQLAlias)selNode.getUserObject()).getColorProperties().setOverrideObjectTreeBackgroundColor(true);
               ((SQLAlias)selNode.getUserObject()).getColorProperties().setObjectTreeBackgroundColorRgbValue(newColor.getRGB());
            }
         }
         else if(selNode.getUserObject() instanceof AliasFolder)
         {
            if (remove)
            {
               ((AliasFolder)selNode.getUserObject()).setColorRGB(AliasFolder.NO_COLOR_RGB);
            }
            else
            {
               ((AliasFolder)selNode.getUserObject()).setColorRGB(newColor.getRGB());
            }
         }
         else
         {
            AliasTreeUtil.throwUnknownUserObjectException(selNode);
         }
      }

      aliasTree.repaint();
   }

   private static JMenuItem createRemoveColorItem(JTree aliasTree)
   {
      JMenuItem ret = new JMenuItem(s_stringMgr.getString("remove.color"));
      ret.addActionListener(e -> colorSelection(aliasTree, null, true));
      return ret;
   }

   private static JMenuItem createChooseColorItem(JTree aliasTree, Color startColor)
   {
      JMenuItem ret = new JMenuItem(s_stringMgr.getString("choose.color"));
      ret.addActionListener(e -> colorSelection(aliasTree, startColor, false));
      return ret;
   }




   private static Color getStartColor(TreePath[] selectionPaths)
   {
      Color ret = null;

      DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selectionPaths[0].getLastPathComponent();

      if (selNode.getUserObject() instanceof SQLAlias)
      {
         SQLAlias sel = (SQLAlias) selNode.getUserObject();

         if(sel.getColorProperties().isOverrideObjectTreeBackgroundColor())
         {
            ret = new Color(sel.getColorProperties().getObjectTreeBackgroundColorRgbValue());
         }
      }
      else if(selNode.getUserObject() instanceof AliasFolder)
      {
         Integer colorRGB = ((AliasFolder) selNode.getUserObject()).getColorRGB();

         if (AliasFolder.NO_COLOR_RGB == colorRGB)
         {
            ret = null;
         }
         else
         {
            ret = new Color(colorRGB);
         }
      }
      else
      {
         AliasTreeUtil.throwUnknownUserObjectException(selNode);
      }

      return ret;
   }
}
