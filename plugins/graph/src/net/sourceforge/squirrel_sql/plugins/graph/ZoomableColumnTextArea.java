package net.sourceforge.squirrel_sql.plugins.graph;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.geom.AffineTransform;


public class ZoomableColumnTextArea extends JPanel implements IColumnTextArea
{
   private TableToolTipProvider _toolTipProvider;
   private ColumnInfo[] _columnInfos;
   private Zoomer _zoomer;

   public ZoomableColumnTextArea(TableToolTipProvider toolTipProvider, Zoomer zoomer, Font zoomColumsFont)
   {
      _toolTipProvider = toolTipProvider;
      _zoomer = zoomer;
      setToolTipText("Just to make getToolTiptext() to be called");

      setFont(zoomColumsFont);
   }

   public String getToolTipText(MouseEvent event)
   {
      return _toolTipProvider.getToolTipText(event);
   }

   public void setGraphColumns(ColumnInfo[] columnInfos)
   {
      _columnInfos = columnInfos;
   }


   public void paint(Graphics g)
   {
      super.paint(g);

      Graphics2D g2d = (Graphics2D) g;

      AffineTransform origTrans = g2d.getTransform();
      Font origFont = g2d.getFont();

      try
      {
         g2d.setFont(getFont());
         AffineTransform at = new AffineTransform(origTrans);
         at.scale(_zoomer.getZoom(), _zoomer.getZoom());
         g2d.setTransform(at);

         int textHeight = getTextHeight();

         int curBaseLine = textHeight - 3;
         for (int i = 0; i < _columnInfos.length; i++)
         {
            g2d.drawString(_columnInfos[i].toString(), 0, curBaseLine);
            curBaseLine += textHeight;
         }
      }
      finally
      {
         g2d.setTransform(origTrans);
         g2d.setFont(origFont);

      }
   }

   private int getTextHeight()
   {
      FontMetrics fm = getFontMetrics(getFont());
      int textHeight = (fm.getAscent() - fm.getLeading() - fm.getDescent()) + 6;
      return textHeight;
   }


   private int getMaxTextWidht()
   {
      FontMetrics fm = getFontMetrics(getFont());

      int maxTextWidht = fm.stringWidth(_columnInfos[0].toString());
      for (int i = 1; i < _columnInfos.length; i++)
      {
         maxTextWidht = Math.max(maxTextWidht, fm.stringWidth(_columnInfos[0].toString()));
      }

      return (int)(maxTextWidht * _zoomer.getZoom() + 0.5);

   }


   public Dimension getPreferredSize()
   {
      Dimension ret = new Dimension();
      ret.width = getMaxTextWidht();
      ret.height = (int)(_zoomer.getZoom() * _columnInfos.length * getTextHeight() + 0.5);
      return ret;
   }


}
