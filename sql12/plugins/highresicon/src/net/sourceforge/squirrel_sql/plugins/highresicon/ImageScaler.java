package net.sourceforge.squirrel_sql.plugins.highresicon;

import net.sourceforge.xbrz.tool.AwtXbrz;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

class ImageScaler
{

   public static ImageScaler getInstance()
   {
      return new ImageScaler();
   }

   public Image scale(Image source, int destWidth, int destHeight)
   {
      return incrementalDownscale(AwtXbrz.scaleImage(source, destWidth, destHeight), destWidth, destHeight);
   }

   /**
    * Smoother downscale result for factors > 2x.
    *
    * @param   source  source image to scale;
    * @param   destWidth  destination width;
    * @param   destHeight  destination height.
    * @return  Scaled image as necessary, or the original source.
    * @see  <a href="https://web.archive.org/web/20070427021208/http://today.java.net:80/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html"
    *          >The Perils of Image.getScaledInstance()</a>
    */
   private static Image incrementalDownscale(Image source, int destWidth, int destHeight)
   {
      Image current = source;
      int currentWidth = source.getWidth(null);
      int currentHeight = source.getHeight(null);
      while (currentWidth > destWidth * 2 || currentHeight > destHeight * 2)
      {
         if (currentWidth > destWidth * 2) currentWidth /= 2;
         if (currentHeight > destHeight * 2) currentHeight /= 2;

         Image previous = current;
         current = scaleSmooth(current, currentWidth, currentHeight);
         if (previous == current) // animated, avoid infinite loop
         {
            return current;
         }
      }
      return scaleSmooth(current, destWidth, destHeight);
   }

   private static Image scaleSmooth(Image source, int destWidth, int destHeight)
   {
      BufferedImage variant = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = variant.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                         RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                         RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
      try
      {
         // drawImage() returning false means "image pixels are still changing",
         // that is the base image appears animated.
         return g.drawImage(source, 0, 0, destWidth, destHeight, null) ? variant : source;
      }
      finally
      {
         g.dispose();
      }
   }

}
