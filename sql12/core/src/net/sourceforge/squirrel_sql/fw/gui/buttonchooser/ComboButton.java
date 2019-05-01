package net.sourceforge.squirrel_sql.fw.gui.buttonchooser;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;

public class ComboButton extends JButton
{
   @Override
   protected void paintComponent(Graphics g)
   {
      super.paintComponent(g);

      Dimension size = getSize();

      if (isEnabled())
      {
         g.setColor(Color.black);
      }
      else
      {
         g.setColor(Color.gray);
      }

      int triHeight = 3;
      int dist = 3;

      Polygon pg = new Polygon();
      pg.addPoint(dist, size.height / 2 - triHeight);
      pg.addPoint(size.width - dist, size.height / 2 - triHeight);
      pg.addPoint(size.width / 2, size.height / 2 + triHeight);
      g.fillPolygon(pg);
   }
}
