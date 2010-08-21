package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.FormatXmlBean;

import java.awt.*;

public class PixelCalculater
{
   public static final double CM_BY_INCH = 1/2.54;

   private FormatXmlBean _format;
   private double _sldValue = 1;

   public PixelCalculater(FormatXmlBean format, double sldValue)
   {
      _format = format;
      _sldValue = sldValue;
   }

   public PixelCalculater(FormatXmlBean format)
   {
      _format = format;
   }


//   public int getPixelWidth()
//   {
//      int pixelByCm = (int) (Toolkit.getDefaultToolkit().getScreenResolution() * CM_BY_INCH + 0.5);
//      return (int)(_format.getWidth() * pixelByCm + 0.5);
//   }
//
//   public int getPixelHeight()
//   {
//      int pixelByCm = (int) (Toolkit.getDefaultToolkit().getScreenResolution() * CM_BY_INCH + 0.5);
//      return (int)(_format.getHeight() * pixelByCm + 0.5);
//   }



   public int getPixelWidth()
   {
      int pixelByCm = (int) (Toolkit.getDefaultToolkit().getScreenResolution() * CM_BY_INCH + 0.5);
      return (int)(_format.getWidth() * pixelByCm * _sldValue + 0.5);
   }

   public int getPixelHeight()
   {
      int pixelByCm = (int) (Toolkit.getDefaultToolkit().getScreenResolution() * CM_BY_INCH + 0.5);
      return (int)(_format.getHeight() * pixelByCm * _sldValue + 0.5);
   }
}
