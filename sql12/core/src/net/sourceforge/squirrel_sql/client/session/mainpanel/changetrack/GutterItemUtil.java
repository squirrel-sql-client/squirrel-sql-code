package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import javax.swing.text.BadLocationException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class GutterItemUtil
{
   static Rectangle getLeftGutterBoundsForLines(ISQLEntryPanel sqlEntry, int beginLine, int numberOfLines)
   {
      try
      {
         int beginLineTransformed = Math.max(beginLine - 1, 0);
         int numberOfLinesTransformed = numberOfLines - 1;

         int lastLine = Math.min(beginLineTransformed + numberOfLinesTransformed, sqlEntry.getTextComponent().getLineCount() -1);

         Rectangle beginRect = sqlEntry.getTextComponent().modelToView(sqlEntry.getTextComponent().getLineStartOffset(beginLineTransformed));
         Rectangle endRect = sqlEntry.getTextComponent().modelToView(sqlEntry.getTextComponent().getLineStartOffset(lastLine));

         Rectangle visibleRect = sqlEntry.getTextComponent().getVisibleRect();

         beginRect.y -= visibleRect.y;
         endRect.y -= visibleRect.y;


         Rectangle ret = new Rectangle();

         ret.y = beginRect.y;
         ret.height = endRect.y + endRect.height - beginRect.y;

         ret.x = 0;
         ret.width = ChangeTrackPanel.LEFT_GUTTER_WIDTH;

         return ret;
      }
      catch (BadLocationException e)
      {
         return null;
      }
   }

   public static Rectangle getRightGutterMarkBoundsForLines(ChangeTrackPanel changeTrackPanel, ISQLEntryPanel sqlEntry, int beginLine, int numberOfLines)
   {
      try
      {
         int beginLineTransformed = beginLine - 1;
         int numberOfLinesTransformed = numberOfLines - 1;

         int lastLine = Math.min(beginLineTransformed + numberOfLinesTransformed, sqlEntry.getTextComponent().getLineCount() -1);

         Rectangle beginRect = sqlEntry.getTextComponent().modelToView(sqlEntry.getTextComponent().getLineStartOffset(beginLineTransformed));
         Rectangle endRect = sqlEntry.getTextComponent().modelToView(sqlEntry.getTextComponent().getLineStartOffset(lastLine));

         Rectangle ret = new Rectangle();

         ret.y = beginRect.y;
         ret.height = endRect.y + endRect.height - beginRect.y;

         ret.x = 0;
         ret.width = ChangeTrackPanel.RIGHT_GUTTER_WIDTH;


         double doubleY = (double) changeTrackPanel.trackingGutterRight.getHeight() * ((double) ret.y / (double) sqlEntry.getTextComponent().getHeight());
         double doubleHeight = (double) changeTrackPanel.trackingGutterRight.getHeight() * ((double) ret.height / (double) sqlEntry.getTextComponent().getHeight());

         ret.y = (int) doubleY;
         ret.height = Math.max((int) doubleHeight, 2);

         return ret;
      }
      catch (BadLocationException e)
      {
         return null;
      }
   }

   static void paintRightGutterMark(Graphics g, Rectangle mark, Color color)
   {
      if(null == mark)
      {
         return;
      }

      Color buf = g.getColor();
      g.setColor(color);

      g.fillRect(mark.x, mark.y, mark.width, mark.height);

      g.setColor(buf);
   }
}
