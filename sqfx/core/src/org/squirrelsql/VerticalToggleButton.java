package org.squirrelsql;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.awt.*;
import java.awt.image.BufferedImage;


public class VerticalToggleButton extends ToggleButton
{
   private static final int WIDHT_MARGIN = 15;
   private static final int HEIGHT_MARGIN = 5;

   public VerticalToggleButton(String caption)
   {
      Font f = getFont();
      FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(f);

      int captionHeight = toInt(fm.getXheight());
      int captionWidth = toInt(fm.computeStringWidth(caption));
      BufferedImage bi = new BufferedImage(captionHeight + 2 * HEIGHT_MARGIN, captionWidth + 2 * WIDHT_MARGIN, BufferedImage.TYPE_INT_ARGB);


      Graphics2D g = (Graphics2D) bi.getGraphics();

      g.setColor(new Color(0, 0, 0, 0)); // transparent
      g.fillRect(0, 0, bi.getWidth(), bi.getHeight());

      Paint paint = this.getTextFill();

      if(paint instanceof javafx.scene.paint.Color)
      {
         javafx.scene.paint.Color fxc = (javafx.scene.paint.Color) paint;
         g.setColor(new Color(toInt(fxc.getRed()), toInt(fxc.getGreen()), toInt(fxc.getBlue())));
      }

      g.setFont(new java.awt.Font(f.getName(), java.awt.Font.BOLD, toInt(f.getSize())));
      g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

      g.rotate(-Math.PI / 2);
      g.translate(-bi.getHeight(), bi.getWidth());
      g.drawString(caption, WIDHT_MARGIN, - fm.getDescent());

      WritableImage writableImage = SwingFXUtils.toFXImage(bi, new WritableImage(captionWidth, captionHeight));

      setGraphic(new ImageView(writableImage));

   }

   private int toInt(double d)
   {
      return (int)(d + .5);
   }
}