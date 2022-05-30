package net.sourceforge.squirrel_sql.client.session.editorpaint;

import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.sqlbounds.BoundsOfSqlHandler;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class MarkCurrentSqlHandler
{
   private static final Rectangle NULL_RECT = new Rectangle(-1,-1,-1,-1);

   private boolean _active = true;

   private BoundsOfSqlHandler _boundsOfSqlHandler;
   private JTextComponent _textComponent;


   private Rectangle _currentRect = (Rectangle) NULL_RECT.clone();
   private Color _markColor;

   public MarkCurrentSqlHandler(JTextComponent textComponent, ISession session)
   {
      SquirrelPreferences squirrelPreferences = session.getApplication().getSquirrelPreferences();

      if(false == squirrelPreferences.isMarkCurrentSql())
      {
         _active = false;
         return;
      }

      _markColor = squirrelPreferences.getCurrentSqlMarkColor();



      _textComponent = textComponent;
      _boundsOfSqlHandler = new BoundsOfSqlHandler(_textComponent, session);
   }

   public void paintMark(final Graphics g)
   {
      if(_active == false)
      {
         return;
      }

      Color buf = g.getColor();

      int x = NULL_RECT.x;
      int y = NULL_RECT.y;
      int width = NULL_RECT.width;
      int height = NULL_RECT.height;

      try
      {

         if (null != _textComponent.getSelectedText())
         {
            return;
         }

         final int[] bounds = _boundsOfSqlHandler.getSqlBoundsBySeparatorRule(_textComponent.getCaretPosition());

         if (bounds[0] + 1 >= bounds[1])
         {
            return;
         }

         g.setColor(_markColor);

         Rectangle2D beg = _textComponent.modelToView2D(bounds[0]);
         Rectangle2D end = _textComponent.modelToView2D(bounds[1]);


         int maxSqlX = getMaxSqlX(bounds, end);

         ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
         // With the new option(*) to use statement separators instead of empty lines as bounds of SQL to execute
         // it becomes possible to e.g. have two SQLs in the same line. In this case setting x = 0 may not be considered right.
         //
         // Before changing this be aware that even a rectangle might be inadequate in case of the option(*).
         // To avoid this complexity for now we refrain from adjusting this and wait for the time when many ;) users complain.
         //
         // Note: Central class for option(*) is SQLStatementSeparatorBasedBoundsHandler
         x = 0;
         //
         ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

         y = (int) beg.getY();
         width = maxSqlX - x;
         height = (int) (end.getHeight() + end.getY() - beg.getY());

         g.drawRect(x, y, width, height);


//         System.out.print(new Date());
//         System.out.print(", x = " + x);
//         System.out.print(", y = " + y);
//         System.out.print(", width = " + width);
//         System.out.print(", height = " + height);
//         System.out.println(", color = " + color);
      }
      catch (BadLocationException e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         g.setColor(buf);
         triggerRepaintIfChanged(x, y, width, height);
      }

   }

   private int getMaxSqlX(int[] bounds, Rectangle2D end) throws BadLocationException
   {
      String text = _textComponent.getText();

      int maxSqlX = 0;
      for (int i = bounds[0]; i < bounds[1]; i++)
      {
         if('\n' == text.charAt(i))
         {
            Rectangle2D rectangle = _textComponent.modelToView2D(i);
            maxSqlX = (int) Math.max(maxSqlX, rectangle.getX() + rectangle.getWidth());
         }
      }
      maxSqlX = (int) Math.max(maxSqlX, end.getX() + end.getWidth());
      return maxSqlX;
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
               _textComponent.repaint();
            }
         });
      }
   }

   public void setActive(boolean b)
   {
      _active = false;
   }
}
