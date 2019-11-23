package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class ChangedLinesLeftGutterItem implements LeftGutterItem
{
   private final ISQLEntryPanel _sqlEntry;
   private final int _beginLine;
   private final int _numberOfChangedLines;
   private final String _formerText;

   public ChangedLinesLeftGutterItem(ISQLEntryPanel sqlEntry, int beginLine, int numberOfChangedLines, String formerText)
   {
      _sqlEntry = sqlEntry;
      _beginLine = beginLine;
      _numberOfChangedLines = numberOfChangedLines;
      _formerText = formerText;
   }

   public void paint(Graphics g)
   {
      Rectangle rect = LeftGutterItemUtil.getLeftGutterBoundsForLines(_sqlEntry, _beginLine, _numberOfChangedLines);

      if(null == rect)
      {
         return;
      }

      //System.out.println("rect = " + rect);

      Color buf = g.getColor();
      g.setColor(new Color(200, 180, 230));
      g.fillRect(rect.x, rect.y, rect.width, rect.height);
      g.setColor(buf);

   }

}
