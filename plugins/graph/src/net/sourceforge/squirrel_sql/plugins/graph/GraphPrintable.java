package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.FormatXmlBean;

import javax.swing.*;
import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.*;
import java.awt.geom.AffineTransform;


public class GraphPrintable implements Printable
{
   private FormatXmlBean _format;
   private JComponent _toPrint;
   private int _pageCountHorizontal = -1;
   private int _pageCountVertical = -1;
   private double _sldEdgesValue;
   private PageCountCallBack _pageCountCallBack;

   public GraphPrintable(FormatXmlBean format, JComponent toPrint, double sldEdgesValue, PageCountCallBack pageCountCallBack)
   {
      _format = format;
      _toPrint = toPrint;
      _sldEdgesValue = sldEdgesValue;
      _pageCountCallBack = pageCountCallBack;
   }


   public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException
   {
      int pixelByCm = (int) (Toolkit.getDefaultToolkit().getScreenResolution() * EdgesGraphComponent.CM_BY_INCH + 0.5);
      double edgesWitdthInPixel = _format.getWidth() * pixelByCm * _sldEdgesValue;
      double edgesHeightInPixel = _format.getHeight() * pixelByCm * _sldEdgesValue;


      if(0 > _pageCountHorizontal)
      {
         _pageCountHorizontal = _pageCountCallBack.getPageCountHorizontal(edgesWitdthInPixel);
         _pageCountVertical = _pageCountCallBack.getPageCountVertical(edgesHeightInPixel);
      }


      if(pageIndex >= _pageCountHorizontal *_pageCountVertical)
      {
         return Printable.NO_SUCH_PAGE;
      }

      Graphics2D g2d = (Graphics2D) graphics;

      AffineTransform oldTransform = g2d.getTransform();

      try
      {
         double tx = -getPageWidthInPixel(pageFormat) * (pageIndex % _pageCountHorizontal) + pageFormat.getImageableX();
         double ty = -getPageHeightInPixel(pageFormat) * (pageIndex / _pageCountHorizontal) + pageFormat.getImageableY();

         g2d.translate(tx, ty);

         double sx = getPageWidthInPixel(pageFormat) / edgesWitdthInPixel;
         double sy = getPageHeightInPixel(pageFormat) / edgesHeightInPixel;

         g2d.scale(sx, sy);

         _toPrint.paint(g2d);
      }
      finally
      {
         g2d.setTransform(oldTransform);
      }

      return Printable.PAGE_EXISTS;
   }

   private double getPageHeightInPixel(PageFormat pageFormat)
   {
      return pageFormat.getImageableHeight();
   }

   private double getPageWidthInPixel(PageFormat pageFormat)
   {
      return pageFormat.getImageableWidth();
   }
}
