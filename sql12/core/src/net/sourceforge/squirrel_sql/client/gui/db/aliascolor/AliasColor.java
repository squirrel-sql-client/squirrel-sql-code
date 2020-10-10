package net.sourceforge.squirrel_sql.client.gui.db.aliascolor;

import net.sourceforge.squirrel_sql.client.gui.db.AliasFolder;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.Color;

public class AliasColor
{
   public static final double FACTOR = 0.7;

   public static Color colorOf(int argb)
   {
      // Could cache values, also.
      return new Color(argb, true);
   }

   public static Color getItemColor(Object value)
   {
      Object item = (value instanceof DefaultMutableTreeNode)
                    ? ((DefaultMutableTreeNode) value).getUserObject()
                    : value;

      if (item instanceof SQLAlias)
      {
         SQLAlias sqlAlias = (SQLAlias) item;

         if (sqlAlias.getColorProperties().isOverrideAliasBackgroundColor())
         {
            return colorOf(sqlAlias.getColorProperties().getAliasBackgroundColorRgbValue());
         }
      }
      else if (item instanceof AliasFolder)
      {
         AliasFolder aliasFolder = (AliasFolder) item;

         if (AliasFolder.NO_COLOR_RGB != aliasFolder.getColorRGB())
         {
            return colorOf(aliasFolder.getColorRGB());
         }
      }
      return null;
   }
}
