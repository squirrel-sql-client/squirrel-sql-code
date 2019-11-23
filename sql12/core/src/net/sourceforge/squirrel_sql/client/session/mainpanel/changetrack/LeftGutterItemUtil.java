package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import javax.swing.text.BadLocationException;
import java.awt.Rectangle;

public class LeftGutterItemUtil
{
   static Rectangle getLeftGutterBoundsForLines(ISQLEntryPanel sqlEntry, int beginLine, int numberOfLines)
   {
      try
      {
         int beginLineTransformed = beginLine - 1;
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
}
