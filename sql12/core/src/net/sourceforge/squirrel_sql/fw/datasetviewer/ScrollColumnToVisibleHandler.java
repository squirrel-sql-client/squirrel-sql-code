package net.sourceforge.squirrel_sql.fw.datasetviewer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import javax.swing.JTable;
import javax.swing.Timer;

public class ScrollColumnToVisibleHandler
{
   private int _blinkCount = 0;
   private Timer _timer;


   public ScrollColumnToVisibleHandler(final JTable table, ExtTableColumn toScrollto)
   {
      for (int i = 0; i < table.getColumnModel().getColumnCount(); i++)
      {
         final ExtTableColumn column = (ExtTableColumn) table.getColumnModel().getColumn(i);

         if(column == toScrollto)
         {
            final int columnIxToScrollTo = i;

            Rectangle cellRect = table.getCellRect(0, columnIxToScrollTo, true);
            table.scrollRectToVisible(cellRect);

            _timer = new Timer(500, e -> onBlinkCol(table, columnIxToScrollTo));

            _timer.setRepeats(true);

            onBlinkCol(table, columnIxToScrollTo);

            _timer.start();

            return;
         }
      }
   }

   private void onBlinkCol(JTable table, int columnIxToScrollTo)
   {
      Rectangle headerRect = table.getTableHeader().getHeaderRect(columnIxToScrollTo);

      Graphics graphics = table.getTableHeader().getGraphics();

      if( _blinkCount++ % 2 == 0)
      {
         Color formerColor = graphics.getColor();
         graphics.setColor(Color.red);

         Stroke formerStroke = null;
         if (graphics instanceof Graphics2D)
         {
            formerStroke = ((Graphics2D)graphics).getStroke();
            ((Graphics2D)graphics).setStroke(new BasicStroke(4));
         }

         graphics.drawRect(headerRect.x, headerRect.y, headerRect.width-1, headerRect.height-1);


         graphics.setColor(formerColor);

         if (graphics instanceof Graphics2D)
         {
            ((Graphics2D)graphics).setStroke(formerStroke);
         }
      }
      else
      {
         table.getTableHeader().repaint();
      }


      if( _blinkCount > 10)
      {
         _timer.stop();

         table.getTableHeader().repaint();

      }
   }
}
