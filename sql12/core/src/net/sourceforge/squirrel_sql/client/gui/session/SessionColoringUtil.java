package net.sourceforge.squirrel_sql.client.gui.session;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasColorProperties;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.statusbar.SessionStatusBar;

import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.Color;

public class SessionColoringUtil
{
   public static void colorToolbar(ISession session, JToolBar toolBar)
   {
      SQLAliasColorProperties colorProps = session.getAlias().getColorProperties();
      if (colorProps.isOverrideToolbarBackgroundColor()) {
         int rgbValue = colorProps.getToolbarBackgroundColorRgbValue();
         toolBar.setBackground(new Color(rgbValue));
         toolBar.setOpaque(true);
      }
   }

   public static void colorStatusbar(ISession session, SessionStatusBar sessionStatusBar)
   {
      SQLAliasColorProperties colorProps = session.getAlias().getColorProperties();
      if (colorProps.isOverrideStatusBarBackgroundColor()) {
         int rgbValue = colorProps.getStatusBarBackgroundColorRgbValue();
         sessionStatusBar.setBackground(new Color(rgbValue));
      }


   }

   public static void colorTree(ISession session, JTree tree)
   {
      SQLAliasColorProperties colorProps = session.getAlias().getColorProperties();
      if (colorProps.isOverrideObjectTreeBackgroundColor()) {
         int rgbValue = colorProps.getObjectTreeBackgroundColorRgbValue();
         tree.setBackground(new Color(rgbValue));
         Runnable updateNonSelectionColor = () ->
         {
            TreeCellRenderer cellRenderer = tree.getCellRenderer();
            if (cellRenderer instanceof DefaultTreeCellRenderer)
               ((DefaultTreeCellRenderer) cellRenderer).setBackgroundNonSelectionColor(new Color(rgbValue));
         };
         updateNonSelectionColor.run();
         tree.addPropertyChangeListener("cellRenderer", evt -> updateNonSelectionColor.run());
      }
   }
}
