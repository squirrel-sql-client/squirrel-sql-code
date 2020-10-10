package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.gui.db.aliascolor.AliasColor;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Color;
import java.awt.Component;

public class AliasListCellRenderer extends DefaultListCellRenderer
{
   @Override
   public Component getListCellRendererComponent(JList<?> list,
                                                 Object value,
                                                 int index,
                                                 boolean isSelected,
                                                 boolean cellHasFocus)
   {
      Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

      Color bg = AliasColor.getItemColor(value);
      if (bg != null)
      {
         if (isSelected)
         {
            renderer.setBackground(blendColor(bg, renderer.getBackground(), AliasColor.FACTOR));
         }
         else
         {
            renderer.setBackground(bg);
         }
      }

      return renderer;
   }

   static Color blendColor(Color front, Color back, double factor)
   {
      final double weightFront = front.getAlpha() * factor;
      final double weightBack = back.getAlpha() * (1 - factor);
      final double weightSum = weightFront + weightBack;

      final double a = weightSum;
      final double r = (weightFront * front.getRed() + weightBack * back.getRed()) / weightSum;
      final double g = (weightFront * front.getGreen() + weightBack * back.getGreen()) / weightSum;
      final double b = (weightFront * front.getBlue() + weightBack * back.getBlue()) / weightSum;
      return AliasColor.colorOf(((int) a << 24) | ((int) r << 16) | ((int) g << 8) | (int) b);
   }
}
