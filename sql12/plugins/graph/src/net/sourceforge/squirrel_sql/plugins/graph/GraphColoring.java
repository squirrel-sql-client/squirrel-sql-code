package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.preferences.themes.ThemesEnum;

import javax.swing.JComponent;
import java.awt.Color;

public class GraphColoring
{
   static void setDesktopBackground(GraphDesktopPane desktopPane)
   {
      if(ThemesEnum.getCurrentTheme() == ThemesEnum.LIGH)
      {
         desktopPane.setBackground(Color.white);
      }
   }

   static void setTableFrameBackground(JComponent compToColor)
   {
      if(ThemesEnum.getCurrentTheme() == ThemesEnum.LIGH)
      {
         compToColor.setBackground(new Color(255, 255, 204));
      }
   }

   public static Color getGroupTitleColor()
   {
      if(ThemesEnum.getCurrentTheme() == ThemesEnum.LIGH)
      {
         return new Color(200,200,240);
      }
      else
      {
         return Color.gray;
      }
   }

   public static Color getInternalFrameTitleSelectedColor(Color defaultSelectedTitleColor)
   {
      if(ThemesEnum.getCurrentTheme() == ThemesEnum.LIGH)
      {
         return new Color(255,255,220);
      }
      else
      {
         return defaultSelectedTitleColor;
      }
   }

   public static Color getInternalFrameTitleNotSelectedTitleColor(Color defaultNotSelectedTitleColor)
   {
      if(ThemesEnum.getCurrentTheme() == ThemesEnum.LIGH)
      {
         return new Color(255,255,220);
      }
      else
      {
         return defaultNotSelectedTitleColor;
      }
   }

   public static Color getInternalFrameTitleSelectedTextColor(Color defaultSelectedTextColor)
   {
      if(ThemesEnum.getCurrentTheme() == ThemesEnum.LIGH)
      {
         return Color.black;
      }
      else
      {
         return defaultSelectedTextColor;
      }
   }

   public static Color getInternalFrameTitleNotSelectedTextColor(Color defaultNotSelectedTextColor)
   {
      if(ThemesEnum.getCurrentTheme() == ThemesEnum.LIGH)
      {
         return Color.black;
      }
      else
      {
         return defaultNotSelectedTextColor;
      }
   }
}
