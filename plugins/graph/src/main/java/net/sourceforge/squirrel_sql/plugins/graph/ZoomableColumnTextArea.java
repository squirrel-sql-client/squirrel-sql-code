package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.graph.nondbconst.DndCallback;
import net.sourceforge.squirrel_sql.plugins.graph.nondbconst.DndColumn;
import net.sourceforge.squirrel_sql.plugins.graph.nondbconst.DndEvent;
import net.sourceforge.squirrel_sql.plugins.graph.nondbconst.DndHandler;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.geom.AffineTransform;


public class ZoomableColumnTextArea extends JPanel implements DndColumn, IColumnTextArea
{
   private TableToolTipProvider _toolTipProvider;
   private ColumnInfoModel _columnInfoModel;
   private Zoomer _zoomer;
   private DndHandler _dndHandler;

   public ZoomableColumnTextArea(TableToolTipProvider toolTipProvider, Zoomer zoomer, DndCallback dndCallback, ISession session)
   {
      _toolTipProvider = toolTipProvider;
      _zoomer = zoomer;
      setToolTipText("Just to make getToolTiptext() to be called");
      _dndHandler = new DndHandler(dndCallback, this, session);
   }

   public String getToolTipText(MouseEvent event)
   {
      return _toolTipProvider.getToolTipText(event);
   }

   public void setColumnInfoModel(ColumnInfoModel columnInfoModel)
   {
      _columnInfoModel = columnInfoModel;
   }


   public void paint(Graphics g)
   {
      super.paint(g);

      Graphics2D g2d = (Graphics2D) g;

      AffineTransform origTrans = g2d.getTransform();

      try
      {
         AffineTransform at = new AffineTransform(origTrans);
         at.scale(_zoomer.getZoom(), _zoomer.getZoom());
         g2d.setTransform(at);

         int textHeight = getTextHeight();

         int curBaseLine = textHeight - 3;
         for (int i = 0; i < _columnInfoModel.getColCount(); i++)
         {
            g2d.drawString(_columnInfoModel.getOrderedColAt(i).toString(), 0, curBaseLine);
            curBaseLine += textHeight;
         }
      }
      finally
      {
         g2d.setTransform(origTrans);
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

      int maxTextWidht = fm.stringWidth(_columnInfoModel.getColAt(0).toString());
      for (int i = 1; i < _columnInfoModel.getColCount(); i++)
      {
         maxTextWidht = Math.max(maxTextWidht, fm.stringWidth(_columnInfoModel.getColAt(0).toString()));
      }

      return (int)(maxTextWidht * _zoomer.getZoom() + 0.5);

   }


   public Dimension getPreferredSize()
   {
      Dimension ret = new Dimension();
      ret.width = getMaxTextWidht();
      ret.height = (int)(_zoomer.getZoom() * _columnInfoModel.getColCount() * getTextHeight() + 0.5);
      return ret;
   }


   public DndEvent getDndEvent()
   {
      return _dndHandler.getDndEvent();
   }

   public void setDndEvent(DndEvent dndEvent)
   {
      _dndHandler.setDndEvent(dndEvent);
   }

   @Override
   public Point getLocationInColumnTextArea()
   {
      return new Point(0,0);
   }

   @Override
   public int getColumnHeight()
   {
      FontMetrics fm = getGraphics().getFontMetrics(getFont());
      return fm.getHeight();
   }

   @Override
   public int getMaxWidth()
   {
      int maxSize = 0;
      FontMetrics fm = getFontMetrics(getFont());

      for (int i = 0; i < _columnInfoModel.getColCount(); i++)
      {
         int buf = fm.stringWidth(_columnInfoModel.getColAt(i).toString());
         if(maxSize < buf)
         {
            maxSize = buf;
         }
      }

      return maxSize;
   }
}
