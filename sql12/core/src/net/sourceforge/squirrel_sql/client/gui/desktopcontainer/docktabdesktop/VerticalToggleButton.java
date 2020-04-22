package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.Collections;
import java.util.Map;
import javax.swing.*;

public class VerticalToggleButton extends JToggleButton
{
   private static final int WIDHT_MARGIN = 20;
   private static final int HEIGHT_MARGIN = 4;

   public VerticalToggleButton(String caption)
   {
      Font f = getFont();
      FontMetrics fm = getFontMetrics(f);
      int iconWidth = fm.getHeight() + 2 * HEIGHT_MARGIN;
      int iconHeight = fm.stringWidth(caption) + 2 * WIDHT_MARGIN;

      Icon icon = new Icon()
      {
         @Override
         public int getIconWidth()
         {
            return iconWidth;
         }

         @Override
         public int getIconHeight()
         {
            return iconHeight;
         }

         @Override
         public synchronized void paintIcon(Component c, Graphics g0, int x, int y)
         {
            Graphics2D g = (Graphics2D) g0.create(x, y, iconWidth, iconHeight);

            g.setColor(new Color(0, 0, 0, 0)); // transparent
            g.fillRect(0, 0, iconWidth, iconHeight);

            g.setColor(c.getForeground());
            g.setFont(f);
            Map<?, ?> textHints = (Map<?, ?>) Toolkit
                  .getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
            if (textHints == null)
            {
               textHints = Collections.singletonMap(RenderingHints.KEY_TEXT_ANTIALIASING,
                                                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            }
            g.addRenderingHints(textHints);

            g.rotate(-Math.PI / 2);
            g.translate(-iconHeight, iconWidth);
            g.drawString(caption, WIDHT_MARGIN , -HEIGHT_MARGIN - fm.getDescent());
            g.dispose();
         }
      };
      super.setIcon(icon);
      super.setActionCommand(caption);
   }

   public static void main(String[] args)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            JFrame frame = new JFrame("Vertical Button Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new FlowLayout());
            frame.add(new VerticalToggleButton("Vertical Up"));
            frame.add(new VerticalToggleButton("Vertical Down"));
            frame.pack();
            frame.setVisible(true);
         }
      });
   }
}
