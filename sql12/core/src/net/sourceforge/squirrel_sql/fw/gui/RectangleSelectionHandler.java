package net.sourceforge.squirrel_sql.fw.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class RectangleSelectionHandler
{
   private JComponent _comp;
   private Point _dragBeginPoint = null;
   private Point _dragEndPoint = null;
   private RectangleSelectionListener _rectangleSelectionListener;
   private boolean _cancelCurrentSelection;


   public RectangleSelectionHandler(JComponent comp)
   {
      setComponent(comp);
   }

   public RectangleSelectionHandler()
   {
   }

   private void onMouseDragged(MouseEvent e)
   {
      if(null == _dragBeginPoint )
      {
         initBeginPoint(e);
      }

      _dragEndPoint = e.getPoint();

      _comp.repaint();

   }

   /**
    * Call this method from your component's paint method.
    */
   public void paintRectWhenNeeded(Graphics g)
   {
      if(false == _cancelCurrentSelection && null != _dragBeginPoint && null != _dragEndPoint && false == _dragBeginPoint.equals(_dragEndPoint))
      {
         int x = Math.min(_dragBeginPoint.x,  _dragEndPoint.x);
         int y = Math.min(_dragBeginPoint.y,  _dragEndPoint.y);
         int width = Math.abs(_dragBeginPoint.x - _dragEndPoint.x);
         int heigh = Math.abs(_dragBeginPoint.y - _dragEndPoint.y);

         Color colBuf = g.getColor();
         g.setColor(_comp.getForeground());
         g.drawRect(x,y,width,heigh);
         g.setColor(colBuf);
      }
   }

   public void setComponent(JComponent comp)
   {
      _comp = comp;

      _comp.addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent e)
         {
            initBeginPoint(e);
         }

         public void mouseReleased(MouseEvent e)
         {
            onMouseReleased();
         }
      });

      _comp.addMouseMotionListener(new MouseMotionAdapter()
      {
         public void mouseDragged(MouseEvent e)
         {
            onMouseDragged(e);
         }
      });
   }

   private void onMouseReleased()
   {
      if (false == _cancelCurrentSelection && null != _rectangleSelectionListener && null != _dragBeginPoint && null != _dragEndPoint)
      {
         _rectangleSelectionListener.rectSelected(_dragBeginPoint, _dragEndPoint);
      }
      _dragBeginPoint = null;
      _dragEndPoint = null;
      _cancelCurrentSelection = false;
      _comp.repaint();
   }

   private void initBeginPoint(MouseEvent e)
   {
      if (SwingUtilities.isLeftMouseButton(e) && 0 == (e.getModifiers() & MouseEvent.CTRL_MASK))
      {
         _dragBeginPoint = e.getPoint();
      }
   }

   public void setRectangleSelectionListener(RectangleSelectionListener rectangleSelectionListener)
   {
      _rectangleSelectionListener = rectangleSelectionListener;
   }

   public static boolean rectHit(Rectangle bounds, Point p1, Point p2)
   {
      Rectangle selRect = new Rectangle(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y));
      return bounds.intersects(selRect);
   }

   public void cancelCurrentSelection()
   {
      _cancelCurrentSelection = true;
   }
}
