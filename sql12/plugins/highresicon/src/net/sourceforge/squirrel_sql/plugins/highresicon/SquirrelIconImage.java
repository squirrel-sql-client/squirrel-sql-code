package net.sourceforge.squirrel_sql.plugins.highresicon;

import net.sourceforge.squirrel_sql.fw.resources.IconScale;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.MultiResolutionImage;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements strategy for looking up and producing hires image variants.
 * Uses a single source image from which variants get interpolated.  The
 * source may be an already hires variant that would get scaled down as
 * necessary.  The scaling factor of the source is determined from the file
 * name {@code myicon@2x.png}, and the logical image size (user-space
 * dimensions) get adjusted accordingly.
 * <p>
 * The base image size is also adjusted according to {@code IconScale.factor}.</p>
 *
 * @see  IconScale
 */
public class SquirrelIconImage extends MultiResolutionCachedImage
{
   private static final Pattern SCALE_FACTOR_IN_PATH =
         Pattern.compile("(?x) [^/] @([1-6])x (?:\\.[^./]+)? $");

   private static ILogger log = LoggerController.createLogger(SquirrelIconImage.class);

   private static ImageScaler imageScaler = ImageScaler.getInstance();

   private final URL sourceLocation;
   private final Image sourceImage;
   private final boolean iconImage;

   protected SquirrelIconImage(URL sourceLocation, Image sourceImage,
                               int baseWidth, int baseHeight)
   {
      super(baseWidth, baseHeight);
      this.sourceLocation = sourceLocation;
      this.sourceImage = sourceImage;
      // ImageIcon is sometimes used just as an image source, so
      // don't scale adjust larger images which might not be icons.
      this.iconImage = baseWidth > 0 && baseWidth <= 64
                       && baseHeight > 0 && baseHeight <= 64;
   }

   public static SquirrelIconImage of(URL sourceLocation)
   {
      Image sourceImage = loadImage(sourceLocation);
      Matcher m = SCALE_FACTOR_IN_PATH.matcher(sourceLocation.getPath());
      double sourceScale = m.find() ? Double.parseDouble(m.group(1)) : 1.0;
      int baseWidth = (int) Math.round(sourceImage.getWidth(null) / sourceScale);
      int baseHeight = (int) Math.round(sourceImage.getHeight(null) / sourceScale);
      return new SquirrelIconImage(sourceLocation, sourceImage, baseWidth, baseHeight);
   }

   public SquirrelIconImage withSource(Image anotherSource)
   {
      return new SquirrelIconImage(sourceLocation, anotherSource, baseWidth, baseHeight);
   }

   protected URL getSourceLocation()
   {
      return sourceLocation;
   }

   protected Image getSourceImage()
   {
      return sourceImage;
   }

   @Override
   public int getWidth(ImageObserver observer)
   {
      return iconImage ? IconScale.ceil(baseWidth) : baseWidth;
   }

   @Override
   public int getHeight(ImageObserver observer)
   {
      return iconImage ? IconScale.ceil(baseHeight) : baseHeight;
   }

   @Override
   public ImageProducer getSource()
   {
      return getResolutionVariant(getWidth(null), getHeight(null)).getSource();
   }

   @Override
   public Object getProperty(String name, ImageObserver observer)
   {
      URL location = getSourceLocation();
      if ("comment".equals(name) && location != null)
      {
         return location.toString();
      }
      return super.getProperty(name, observer);
   }

   @Override
   public List<Image> getResolutionVariants()
   {
      return Collections.singletonList(getSourceImage());
   }

   @Override
   protected Image createResolutionVariant(int width, int height)
   {
      Image base = getSourceImage();
      if (base instanceof MultiResolutionImage)
      {
         base = ((MultiResolutionImage) base).getResolutionVariant(width, height);
      }
      return imageScaler.scale(base, width, height);
   }

   private static Image loadImage(URL url)
   {
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      return loadImage(toolkit.getImage(url));
   }

   private static Image loadImage(Image image)
   {
      ImageIcon loader = new ImageIcon(image);
      if (log.isDebugEnabled())
      {
         log.debug("Image load status="
               + loader.getImageLoadStatus() + ": " + loader.getDescription());
      }
      return loader.getImage();
   }

}
