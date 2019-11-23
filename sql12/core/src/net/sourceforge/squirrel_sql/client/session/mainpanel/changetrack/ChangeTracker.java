package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

public class ChangeTracker
{
   private ISQLEntryPanel _sqlEntry;
   private ChangeTrackPanel _changeTrackPanel;

   public ChangeTracker(ISQLEntryPanel sqlEntry)
   {
      _sqlEntry = sqlEntry;

      JScrollPane scrollPane = _sqlEntry.getTextAreaEmbeddedInScrollPane();
      _changeTrackPanel = new ChangeTrackPanel(scrollPane, g -> onPaintLeftGutter(g), g -> onPaintRightGutter(g));

      _sqlEntry.setTextAreaPaintListener(() -> _changeTrackPanel.requestGutterRepaint());
   }

   private void onPaintRightGutter(Graphics g)
   {

   }

   private void onPaintLeftGutter(Graphics g)
   {
      Rectangle rect = getLeftGutterBoundsForLines(23, 1);

      if(null == rect)
      {
         return;
      }

      //System.out.println("rect = " + rect);

      Color buf = g.getColor();
      g.setColor(new Color(100, 180, 100));
      g.fillRect(rect.x, rect.y, rect.width, rect.height);
      g.setColor(buf);
   }


   private Rectangle getLeftGutterBoundsForLines(int beginLine, int numberOfLines)
   {
      try
      {
         int beginLineTransformed = beginLine - 1;
         int numberOfLinesTransformed = numberOfLines - 1;

         int lastLine = Math.min(beginLineTransformed + numberOfLinesTransformed, _sqlEntry.getTextComponent().getLineCount() -1);

         Rectangle beginRect = _sqlEntry.getTextComponent().modelToView(_sqlEntry.getTextComponent().getLineStartOffset(beginLineTransformed));
         Rectangle endRect = _sqlEntry.getTextComponent().modelToView(_sqlEntry.getTextComponent().getLineStartOffset(lastLine));

         Rectangle visibleRect = _sqlEntry.getTextComponent().getVisibleRect();

         beginRect.y -= visibleRect.y;
         endRect.y -= visibleRect.y;


         Rectangle ret = new Rectangle();

         ret.y = beginRect.y;
         ret.height = endRect.y + endRect.height - beginRect.y;

         ret.x = 0;
         ret.width = _changeTrackPanel.trackingGutterLeft.getWidth();

         return ret;
      }
      catch (BadLocationException e)
      {
         return null;
      }
   }

   private Rectangle first(int beginLine, int numberOfLines)
   {
      try
      {
         Rectangle visibleRect = _sqlEntry.getTextComponent().getVisibleRect();

         int posBeg = _sqlEntry.getTextComponent().viewToModel(new Point(0, visibleRect.y));
         int posEnd = _sqlEntry.getTextComponent().viewToModel(new Point(0, visibleRect.y + visibleRect.height));

         int offset = _sqlEntry.getTextComponent().modelToView(posBeg).y - visibleRect.y;

         System.out.println("offset = " + offset);

         int firstVisibleLine = _sqlEntry.getLineOfPosition(posBeg);
         int lastVisibleLine = _sqlEntry.getLineOfPosition(posEnd);

         lastVisibleLine = Math.min(lastVisibleLine, _sqlEntry.getTextComponent().getLineCount());


         if(beginLine + numberOfLines < firstVisibleLine || beginLine > lastVisibleLine || beginLine > _sqlEntry.getTextComponent().getLineCount())
         {
            return null;
         }


         int height = Math.min(_changeTrackPanel.trackingGutterLeft.getHeight(), _sqlEntry.getTextComponent().modelToView(lastVisibleLine).y);


         double heightOfOneLine = ((double) height) / ((double) (lastVisibleLine - firstVisibleLine));

         Rectangle ret = new Rectangle();

         ret.y = (int) ((beginLine - firstVisibleLine) * heightOfOneLine) + offset;
         ret.height = (int) (numberOfLines * heightOfOneLine);

         ret.x = 0;
         ret.width = _changeTrackPanel.trackingGutterLeft.getWidth();

         return ret;
      }
      catch (BadLocationException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   public ChangeTrackPanel embedInTracking()
   {
      return _changeTrackPanel;
   }
}
