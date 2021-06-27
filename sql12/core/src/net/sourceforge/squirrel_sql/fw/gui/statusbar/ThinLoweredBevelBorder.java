package net.sourceforge.squirrel_sql.fw.gui.statusbar;

import javax.swing.border.AbstractBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

public class ThinLoweredBevelBorder extends AbstractBorder
{
   @Override public boolean isBorderOpaque() { return true; }

   @Override public Insets getBorderInsets(Component c, Insets insets)
   {
      insets.top = insets.bottom = insets.left = insets.right = 1;
      return insets;
   }

   @Override public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
   {
      Color oldColor = g.getColor();
      int right = x + width - 1;
      int bottom = y + height - 1;

      g.translate(x, y);

      Color darker = c.getBackground().darker();
      Color brighter = c.getBackground().brighter();

      g.setColor(darker);
      g.drawLine(x, y, right, y);
      g.setColor(brighter);
      g.drawLine(x, bottom, right, bottom);

      g.setColor(darker);
      g.drawLine(x, y, x, bottom);
      g.setColor(brighter);
      g.drawLine(right, y, right, bottom);

      g.translate(-x, -y);
      g.setColor(oldColor);
   }

}
