package net.sourceforge.squirrel_sql.plugins.graph.graphtofiles;

import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.FormatXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.PixelCalculater;

import java.awt.print.PageFormat;
import java.awt.*;

public class SaveToFilePageFormat extends PageFormat
{
   private PixelCalculater _pc;
   private Dimension _graphPixelSize;

   public SaveToFilePageFormat(FormatXmlBean format)
   {
      _pc = new PixelCalculater(format);

   }

   public SaveToFilePageFormat(Dimension graphPixelSize)
   {
      _graphPixelSize = graphPixelSize;
   }

   public double getImageableHeight()
   {
      if(null == _pc)
      {
         return _graphPixelSize.height;
      }
      else
      {
         return _pc.getPixelHeight();
      }
   }

   public double getImageableWidth()
   {
      if(null == _pc)
      {
         return _graphPixelSize.width;
      }
      else
      {
         return _pc.getPixelWidth();
      }
   }

   public double getImageableX()
   {
      return 0;
   }

   public double getImageableY()
   {
      return 0;
   }
}
