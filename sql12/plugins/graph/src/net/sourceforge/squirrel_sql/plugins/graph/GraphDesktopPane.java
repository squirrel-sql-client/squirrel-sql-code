package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ScrollableDesktopPane;
import net.sourceforge.squirrel_sql.client.IApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.util.Vector;
import java.util.Arrays;


public class GraphDesktopPane extends ScrollableDesktopPane implements GraphPrintable
{
   private static final long serialVersionUID = 1L;
   private Vector<GraphComponent> _graphComponents = new Vector<GraphComponent>();
   private transient ConstraintViewListener _constraintViewListener;

   /////////////////////////////////////////////////////////
   // Printing
   private double _formatWidthInPixel;
   private double _formatHeightInPixel;
   private double _formatScale;
   private boolean _isPrinting;
   //
   /////////////////////////////////////////////////////////

   public GraphDesktopPane(IApplication app)
   {
      super(app);
      _constraintViewListener = new ConstraintViewAdapter()
      {
         public void foldingPointMoved(ConstraintView source)
         {
            revalidate();
         }
      };
   }


   public void paint(Graphics g)
   {
      super.paintComponent(g);
      super.paintBorder(g);

      paintGraphComponents(g);

      super.paintChildren(g);
   }

   private void paintGraphComponents(Graphics g)
   {
      for (int i = 0; i < _graphComponents.size(); i++)
      {
         GraphComponent comp = _graphComponents.elementAt(i);
         if(comp instanceof EdgesGraphComponent)
         {
            ((EdgesGraphComponent)comp).setBounds(getWidth(), getHeight());
         }

         comp.paint(g, _isPrinting);
      }
   }

   public void putGraphComponents(GraphComponent[] graphComponents)
   {
      for (int i = 0; i < graphComponents.length; i++)
      {
         if(false == _graphComponents.contains(graphComponents[i]))
         {
            if(graphComponents[i] instanceof ConstraintView)
            {
               ((ConstraintView)graphComponents[i]).addConstraintViewListener(_constraintViewListener);
            }

            _graphComponents.add(graphComponents[i]);
         }
      }
   }

   public void removeGraphComponents(GraphComponent[] graphComponents)
   {
      _graphComponents.removeAll(Arrays.asList(graphComponents));
   }

   public Vector<GraphComponent> getGraphComponents()
   {
      return _graphComponents;
   }


   public Dimension getRequiredSize()
   {
      Dimension reqSize = super.getRequiredSize();
      for (int i = 0; i < _graphComponents.size(); i++)
      {
         GraphComponent graphComponent = _graphComponents.elementAt(i);
         Dimension buf = graphComponent.getRequiredSize();

         if(buf.width > reqSize.width)
         {
            reqSize.width = buf.width;
         }

         if(buf.height > reqSize.height)
         {
            reqSize.height = buf.height;
         }
      }

      return reqSize;

   }

   ////////////////////////////////////////////////////////////////////////////////////////
   // Printing
   public void initPrint(double formatWidthInCm, double formatHeightInCm, double formatScale)
   {
      int pixelByCm = (int) (Toolkit.getDefaultToolkit().getScreenResolution() * EdgesGraphComponent.CM_BY_INCH + 0.5);
      _formatWidthInPixel = formatWidthInCm * pixelByCm;
      _formatHeightInPixel = formatHeightInCm * pixelByCm;
      _formatScale = formatScale;
   }

   public Dimension initPrintNoScaleSinglePage()
   {
      Dimension size = getSize();
      _formatWidthInPixel = size.width;
      _formatHeightInPixel = size.height;
      _formatScale = 1;

      return size;

   }


   public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException
   {
      double edgesWitdthInPixel = _formatWidthInPixel * _formatScale;
      double edgesHeightInPixel = _formatHeightInPixel * _formatScale;


      int pageCountHorizontal = getPageCountHorizontal(edgesWitdthInPixel);
      int pageCountVertical = getPageCountVertical(edgesHeightInPixel);


      if(pageIndex >= pageCountHorizontal * pageCountVertical)
      {
         return Printable.NO_SUCH_PAGE;
      }

      Graphics2D g2d = (Graphics2D) graphics;

      AffineTransform oldTransform = g2d.getTransform();

      boolean origDoubleBufferingEnabled = RepaintManager.currentManager(this).isDoubleBufferingEnabled();

      try
      {
         _isPrinting = true;
         RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);

         double tx = -getPageWidthInPixel(pageFormat) * (pageIndex % pageCountHorizontal) + pageFormat.getImageableX();
         double ty = -getPageHeightInPixel(pageFormat) * (pageIndex / pageCountHorizontal) + pageFormat.getImageableY();

         g2d.translate(tx, ty);

         double sx = getPageWidthInPixel(pageFormat) / edgesWitdthInPixel;
         double sy = getPageHeightInPixel(pageFormat) / edgesHeightInPixel;

         g2d.scale(sx, sy);

         paintGraphComponents(g2d);
         super.paintChildren(g2d);

      }
      finally
      {
         g2d.setTransform(oldTransform);
         RepaintManager.currentManager(this).setDoubleBufferingEnabled(origDoubleBufferingEnabled);
         _isPrinting = false;
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



   public int getPageCountHorizontal(double pageWidthInPixel)
   {
      return roundPageCount(getRequiredSize().width / pageWidthInPixel);
   }

   public int getPageCountVertical(double pageHeightInPixel)
   {
      return roundPageCount(getRequiredSize().height / pageHeightInPixel);
   }

   private int roundPageCount(double d)
   {
      return 0 < d - (int)d ? (int)(d+1) : (int)d;
   }
   //
   ///////////////////////////////////////////////////////////////////////////////////////


}
