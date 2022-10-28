package net.sourceforge.squirrel_sql.fw.gui;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 * Indicates a JComponent by switching a red border on and off for a limited amount of time.
 */
public class ComponentIndicator
{
   public static final int DEFAULT_MAX_INDICATE_COUNT = 10;
   private Timer _timer;
   private int _indicateCount;
   private int _maxIndicateCount;


   public void init(Component componentToIndicate)
   {
      init(componentToIndicate, DEFAULT_MAX_INDICATE_COUNT);
   }

   public void init(Component componentToIndicate, int maxIndicateCount)
   {
      _maxIndicateCount = maxIndicateCount;

      _timer = new Timer(500, e -> onIndicateComponent(componentToIndicate));
      _timer.setRepeats(true);
      _timer.start();
      SwingUtilities.invokeLater(() -> onIndicateComponent(componentToIndicate));
   }

   private void onIndicateComponent(Component component)
   {
      Graphics graphics = component.getGraphics();

      if(null == graphics)
      {
         // Happens when prefs sheet is closed while blinking is active.
         return;
      }

      if(_indicateCount++ % 2 == 0)
      {
         Color formerColor = graphics.getColor();
         graphics.setColor(Color.red);

         Stroke formerStroke = null;
         int strokeWidth = 4;
         if (graphics instanceof Graphics2D)
         {
            formerStroke = ((Graphics2D)graphics).getStroke();
            ((Graphics2D)graphics).setStroke(new BasicStroke(strokeWidth));
         }

         graphics.drawRect(strokeWidth, strokeWidth, component.getBounds().width - 2 * strokeWidth, component.getBounds().height - 2 * strokeWidth);

         graphics.setColor(formerColor);

         if (graphics instanceof Graphics2D)
         {
            ((Graphics2D)graphics).setStroke(formerStroke);
         }
      }
      else
      {
         component.repaint();
      }


      if(_indicateCount > _maxIndicateCount)
      {
         _timer.stop();

         component.repaint();
      }
   }

}
