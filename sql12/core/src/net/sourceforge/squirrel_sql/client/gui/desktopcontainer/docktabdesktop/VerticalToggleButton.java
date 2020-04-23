package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.swing.*;

public class VerticalToggleButton extends JToggleButton
{
   private static final int WIDHT_MARGIN = 20;
   private static final int HEIGHT_MARGIN = 4;

   public VerticalToggleButton(String caption)
   {
      Icon icon = new VerticalLabelIcon(caption, this,
            new Insets(HEIGHT_MARGIN, WIDHT_MARGIN, HEIGHT_MARGIN, WIDHT_MARGIN));
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


class VerticalLabelIcon implements Icon, Serializable
{
   private static Map<String, Map<?, ?>> deviceHints = new HashMap<>();

   private String caption;
   private Insets insets;
   private int iconWidth;
   private int iconHeight;

   public VerticalLabelIcon(String caption, Component c, Insets insets)
   {
      this.caption = caption;
      this.insets = (Insets) insets.clone();
      initMetrics(c);
   }

   private FontMetrics initMetrics(Component c)
   {
      FontMetrics fm = c.getFontMetrics(c.getFont());
      this.iconWidth = fm.getHeight() + insets.top + insets.bottom;
      this.iconHeight = fm.stringWidth(caption) + insets.left + insets.right;
      return fm;
   }

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
      FontMetrics fm = initMetrics(c);
      Graphics2D g = (Graphics2D) g0.create(x, y, iconWidth, iconHeight);

      g.setColor(c.getForeground());
      g.setFont(c.getFont());
      setTextHints(g);

      g.rotate(-Math.PI / 2);
      g.translate(-iconHeight, iconWidth);
      g.drawString(caption, insets.left, -insets.bottom - fm.getDescent());
      g.dispose();
   }

   private static void setTextHints(Graphics2D g)
   {
      String key = g.getDeviceConfiguration().getDevice().getIDstring();
      Map<?, ?> hints = deviceHints.computeIfAbsent(key, deviceID ->
      {
         Map<Object, Object> textHints = Optional.ofNullable(getTextHints(deviceID))
                                                 .orElseGet(() -> getTextHints(null));
         return Optional.ofNullable(textHints)
                        .orElseGet(() -> Collections.singletonMap(RenderingHints.KEY_TEXT_ANTIALIASING,
                                                                  RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
      });
      g.addRenderingHints(hints);
   }

   /*
    * See https://docs.oracle.com/javase/8/docs/api/java/awt/doc-files/DesktopProperties.html
    */
   @SuppressWarnings("unchecked")
   private static Map<Object, Object> getTextHints(String deviceID)
   {
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      // REVISIT: Listen for property changes to invalidate the cache.
      String name = (deviceID == null)
                    ? "awt.font.desktophints"
                    : "awt.font.desktophints." + deviceID;
      return (Map<Object, Object>) toolkit.getDesktopProperty(name);
   }
}
