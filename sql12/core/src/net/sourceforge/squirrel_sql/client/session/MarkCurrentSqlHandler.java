package net.sourceforge.squirrel_sql.client.session;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.Date;

public class MarkCurrentSqlHandler
{
   //public static final int NULL_CARET_POS = -10;
   private static final Rectangle NULL_RECT = new Rectangle(-1,-1,-1,-1);


   private final BoundsOfSqlHandler _boundsOfSqlHandler;
   private JTextComponent _textComponent;


   private Rectangle _currentRect = (Rectangle) NULL_RECT.clone();

   //private int _currentCaretPos = NULL_CARET_POS;

   public MarkCurrentSqlHandler(JTextComponent textComponent)
   {
      _textComponent = textComponent;
      _boundsOfSqlHandler = new BoundsOfSqlHandler(_textComponent);
   }

   public void paintMark(final Graphics g)
   {
      Color buf = g.getColor();

      int x = NULL_RECT.x;
      int y = NULL_RECT.y;
      int width = NULL_RECT.width;
      int height = NULL_RECT.height;

      try
      {

         if (null != _textComponent.getSelectedText())
         {
            //_currentRect = (Rectangle) NULL_RECT.clone();
            return;
         }

         final int[] bounds = _boundsOfSqlHandler.getSqlBoundsBySeparatorRule(_textComponent.getCaretPosition());

         if (bounds[0] + 1 >= bounds[1])
         {
            //_currentRect = (Rectangle) NULL_RECT.clone();
            return;
         }

         //((Graphics2D)g).setStroke(new BasicStroke(1));

         g.setColor(Color.blue);

         Rectangle beg = _textComponent.modelToView(bounds[0]);
         Rectangle end = _textComponent.modelToView(bounds[1]);

         x = beg.x;
         y = beg.y;
         width = end.width + end.x - beg.x;
         height = end.height + end.y - beg.y;

         g.drawRect(x, y, width, height);


//         System.out.print(new Date());
//         System.out.print(", x = " + x);
//         System.out.print(", y = " + y);
//         System.out.print(", width = " + width);
//         System.out.print(", height = " + height);
//         System.out.println(", color = " + color);


         //System.out.println(System.currentTimeMillis() + "MarkCurrentSqlHandler.paintMarkRectangle beg=" + beg + ", end=" + end + ", Color " + color);
      }
      catch (BadLocationException e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         g.setColor(buf);
         triggerRepaintIfChanged(x, y, width, height);
         //((Graphics2D)g).setStroke(strokeBuf);
      }

   }

   private void triggerRepaintIfChanged(int x, int y, int width, int height)
   {
      boolean repaint = false;

      if(null == _currentRect)
      {
         repaint = true;
         _currentRect = new Rectangle();
      }
      else
      {
         if(x == _currentRect.x && y == _currentRect.y && width == _currentRect.width && height == _currentRect.height)
         {

         }
         else
         {
            repaint = true;
         }
      }

//      int caretPos = _textComponent.getCaretPosition();
//
//      if(_currentCaretPos != caretPos)
//      {
//         repaint = true;
//         _currentCaretPos = caretPos;
//      }

      if (repaint)
      {
         _currentRect.x = x;
         _currentRect.y = y;
         _currentRect.width = width;
         _currentRect.height = height;

         SwingUtilities.invokeLater(new Runnable()
         {
            @Override
            public void run()
            {
               System.out.println(new Date() + " _textComponent.repaint()");
               _textComponent.repaint();
            }
         });
      }
   }

}
