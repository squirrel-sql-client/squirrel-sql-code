package net.sourceforge.squirrel_sql.client.gui.db.aliascolor;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.ColorPropertiesPanel;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JColorChooser;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;

public class ListAliasColorSelectionHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ListAliasColorSelectionHandler.class);

   public static void selectColor(JList list)
   {
      int[] selectedIndices = list.getSelectedIndices();

      if(0 == selectedIndices.length)
      {
         Dialogs.showOk(Main.getApplication().getMainFrame(), s_stringMgr.getString("select.alias.to.color"));
         return;
      }


      SQLAlias sqlAlias = ((SQLAlias)list.getModel().getElementAt(selectedIndices[0]));

      Color startColor = null;

      if(sqlAlias.getColorProperties().isOverrideAliasBackgroundColor())
      {
         startColor = new Color(sqlAlias.getColorProperties().getAliasBackgroundColorRgbValue());
      }


      if(null != startColor)
      {
         JPopupMenu popupMenu = new JPopupMenu();

         popupMenu.add(createChooseColorItem(list, startColor));
         popupMenu.add(createRemoveColorItem(list));

         Point p = MouseInfo.getPointerInfo().getLocation();

         SwingUtilities.convertPointFromScreen(p, list);

         popupMenu.show(list, p.x, p.y);
      }
      else
      {
         colorSelection(list, null, false);
      }
   }

   private static JMenuItem createRemoveColorItem(JList list)
   {
      JMenuItem ret = new JMenuItem(s_stringMgr.getString("remove.color"));
      ret.addActionListener(e -> colorSelection(list, null, true));
      return ret;
   }

   private static JMenuItem createChooseColorItem(JList list, Color startColor)
   {
      JMenuItem ret = new JMenuItem(s_stringMgr.getString("choose.color"));
      ret.addActionListener(e -> colorSelection(list, startColor, false));
      return ret;
   }

   private static void colorSelection(JList list, Color startColor, boolean remove)
   {

      Color newColor = null;

      if (false == remove)
      {
         if(null == startColor)
         {
            if(-1 != Props.getInt(AliasColor.PREF_KEY_LAST_ALIAS_COLOR, -1))
            {
               startColor = new Color(Props.getInt(AliasColor.PREF_KEY_LAST_ALIAS_COLOR, -1));
            }
         }

         newColor = JColorChooser.showDialog(Main.getApplication().getMainFrame(), ColorPropertiesPanel.i18n.ALIAS_BACKGROUND_COLOR_CHOOSER_DIALOG_TITLE, startColor);

         if(null == newColor)
         {
            return;
         }
         Props.putInt(AliasColor.PREF_KEY_LAST_ALIAS_COLOR, newColor.getRGB());
      }

      int[] selectedIndices = list.getSelectedIndices();

      for (int selectedIndex : selectedIndices)
      {
         SQLAlias sqlAlias = (SQLAlias) list.getModel().getElementAt(selectedIndex);

         if (remove)
         {
            sqlAlias.getColorProperties().setOverrideAliasBackgroundColor(false);
         }
         else
         {
            sqlAlias.getColorProperties().setOverrideAliasBackgroundColor(true);
            sqlAlias.getColorProperties().setAliasBackgroundColorRgbValue(newColor.getRGB());
         }

      }

      list.repaint();
   }
}
