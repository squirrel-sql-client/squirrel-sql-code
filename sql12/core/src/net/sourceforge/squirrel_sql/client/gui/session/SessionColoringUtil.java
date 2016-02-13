package net.sourceforge.squirrel_sql.client.gui.session;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasColorProperties;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.StatusBar;

import javax.swing.*;
import java.awt.*;

public class SessionColoringUtil
{
   public static void colorToolbar(ISession session, JToolBar toolBar)
   {
      SQLAliasColorProperties colorProps = session.getAlias().getColorProperties();
      if (colorProps.isOverrideToolbarBackgroundColor()) {
         int rgbValue = colorProps.getToolbarBackgroundColorRgbValue();
         toolBar.setBackground(new Color(rgbValue));
      }
   }

   public static void colorStatusbar(ISession session, StatusBar statusBar)
   {
      SQLAliasColorProperties colorProps = session.getAlias().getColorProperties();
      if (colorProps.isOverrideStatusBarBackgroundColor()) {
         int rgbValue = colorProps.getStatusBarBackgroundColorRgbValue();
         statusBar.setBackground(new Color(rgbValue));
      }


   }

   public static void colorTree(ISession session, JTree tree)
   {
      SQLAliasColorProperties colorProps = session.getAlias().getColorProperties();
      if (colorProps.isOverrideObjectTreeBackgroundColor()) {
         int rgbValue = colorProps.getObjectTreeBackgroundColorRgbValue();
         tree.setBackground(new Color(rgbValue));
      }
   }
}
