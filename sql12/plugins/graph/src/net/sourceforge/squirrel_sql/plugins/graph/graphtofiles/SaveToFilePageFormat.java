package net.sourceforge.squirrel_sql.plugins.graph.graphtofiles;

import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.FormatXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.PixelCalculater;

import java.awt.print.PageFormat;

public class SaveToFilePageFormat extends PageFormat
{
   private FormatXmlBean _format;
   private PixelCalculater _pc;

   public SaveToFilePageFormat(FormatXmlBean format)
   {
      _format = format;
      _pc = new PixelCalculater(format);

   }

   public double getImageableHeight()
   {
      return _pc.getPixelHeight();
   }

   public double getImageableWidth()
   {
      return _pc.getPixelWidth();
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
