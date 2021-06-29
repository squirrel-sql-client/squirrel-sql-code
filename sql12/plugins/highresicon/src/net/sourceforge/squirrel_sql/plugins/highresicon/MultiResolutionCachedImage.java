package net.sourceforge.squirrel_sql.plugins.highresicon;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.AbstractMultiResolutionImage;
import java.awt.image.ImageObserver;
import java.awt.image.MultiResolutionImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Implements caching strategy for image resolution variants.
 */
abstract class MultiResolutionCachedImage extends AbstractMultiResolutionImage
{
   protected final int baseWidth;
   protected final int baseHeight;

   @SuppressWarnings("serial")
   private Map<Dimension, Image> resolutionVariants = new LinkedHashMap<Dimension, Image>(2, 1, true)
   {
      protected boolean removeEldestEntry(Map.Entry<Dimension, Image> eldest)
      {
         return size() > 2;
      }
   };

   protected MultiResolutionCachedImage(int baseWidth, int baseHeight)
   {
      this.baseWidth = baseWidth;
      this.baseHeight = baseHeight;
   }

   public static Image map(Image image, Function<Image, Image> mapper)
   {
      if (image instanceof MultiResolutionImage)
      {
         return new MultiResolutionCachedImage(image.getWidth(null), image.getHeight(null))
         {
            @Override
            protected Image createResolutionVariant(int destWidth, int destHeight)
            {
               return mapper.apply(((MultiResolutionImage) image).getResolutionVariant(destWidth, destHeight));
            }
         };
      }
      return mapper.apply(image);
   }

   @Override
   public int getWidth(ImageObserver observer)
   {
      return baseWidth;
   }

   @Override
   public int getHeight(ImageObserver observer)
   {
      return baseHeight;
   }

   @Override
   protected Image getBaseImage()
   {
      return getResolutionVariant(baseWidth, baseHeight);
   }

   @Override
   public Image getScaledInstance(int width, int height, int hints)
   {
      return getResolutionVariant(width, height);
   }

   @Override
   public Image getResolutionVariant(double destImageWidth, double destImageHeight)
   {
      int width = (int) Math.ceil(destImageWidth);
      int height = (int) Math.ceil(destImageHeight);
      Dimension key = new Dimension(width, height);
      Image variant = resolutionVariants.get(key);
      if (variant == null)
      {
         variant = createResolutionVariant(width, height);
         resolutionVariants.put(key, variant);
      }
      return variant;
   }

   @Override
   public List<Image> getResolutionVariants()
   {
      List<Image> variants = new ArrayList<>(resolutionVariants.values());
      if (variants.isEmpty())
      {
         variants.add(getBaseImage());
      };
      return variants;
   }

   /**
    * Creates a specific image that is the best variant to represent
    * this logical image at the indicated size.  It is called by
    * {@code getResolutionVariant(width, height)} whenever a new
    * variant needs to be created (not cached).
    *
    * @param   destWidth  the width of the destination image, in pixels.
    * @param   destHeight  the height of the destination image, in pixels.
    * @return  Image resolution variant.
    * @see     #getResolutionVariant(double, double)
    */
   protected abstract Image createResolutionVariant(int destWidth, int destHeight);

}
