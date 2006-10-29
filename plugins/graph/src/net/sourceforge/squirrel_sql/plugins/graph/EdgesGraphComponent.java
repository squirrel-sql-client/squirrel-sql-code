package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.FormatXmlBean;

import java.awt.*;


public class EdgesGraphComponent implements GraphComponent
{
   private FormatXmlBean _format;
   private double _sldValue;
   private boolean _valueIsAdjusting;
   private int _widht;
   private int _height;
   public static final double CM_BY_INCH = 1/2.54;

   public void init(FormatXmlBean format, double sldValue, boolean valueIsAdjusting)
   {
      _format = format;
      _sldValue = sldValue;
      _valueIsAdjusting = valueIsAdjusting;
   }

   public void setBounds(int widht, int height)
   {
      _widht = widht;
      _height = height;
   }

   public void paint(Graphics g, boolean isPrinting)
   {
      if(isPrinting)
      {
         return;
      }

      PixelCalculater pc = new PixelCalculater(_format, _sldValue);

      int stepWitdth = pc.getPixelWidth();
      int stepHeight = pc.getPixelHeight();

      Color oldColor = g.getColor();
      try
      {
         g.setColor(Color.GREEN);

         for(int i=stepHeight; i < _height; i+= stepHeight)
         {
            g.drawLine(0, i, _widht, i);
         }

         for(int i=0; i < _widht; i+= stepWitdth)
         {
            g.drawLine(i, 0, i, _height);
         }
      }
      finally
      {
         g.setColor(oldColor);
      }

   }

   public Dimension getRequiredSize()
   {
      return new Dimension(0,0);
   }
}
