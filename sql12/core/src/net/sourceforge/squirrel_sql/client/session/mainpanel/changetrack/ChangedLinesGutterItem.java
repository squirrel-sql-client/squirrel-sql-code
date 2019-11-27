package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class ChangedLinesGutterItem implements GutterItem
{
   private ChangeTrackPanel _changeTrackPanel;
   private final ISQLEntryPanel _sqlEntry;
   private final int _beginLine;
   private final int _changedLinesCount;
   private final String _formerText;

   public ChangedLinesGutterItem(ChangeTrackPanel changeTrackPanel, ISQLEntryPanel sqlEntry, int beginLine, int changedLinesCount, String formerText)
   {
      _changeTrackPanel = changeTrackPanel;
      _sqlEntry = sqlEntry;
      _beginLine = beginLine;
      _changedLinesCount = changedLinesCount;
      _formerText = formerText;
   }

   public void leftPaint(Graphics g)
   {
      Rectangle rect = GutterItemUtil.getLeftGutterBoundsForLines(_sqlEntry, _beginLine, _changedLinesCount);

      if(null == rect)
      {
         return;
      }

      Color buf = g.getColor();
      g.setColor(getColor());
      g.fillRect(rect.x, rect.y, rect.width, rect.height);
      g.setColor(buf);

   }

   private Color getColor()
   {
      return new Color(200, 180, 230);
   }

   @Override
   public void rightPaint(Graphics g)
   {
      Rectangle mark =  GutterItemUtil.getRightGutterMarkBoundsForLines(_changeTrackPanel, _sqlEntry, _beginLine, _changedLinesCount);

      GutterItemUtil.paintRightGutterMark(g, mark, getColor());
   }


   @Override
   public void leftShowPopupIfHit(MouseEvent e, JPanel trackingGutterLeft)
   {
      Rectangle rect = GutterItemUtil.getLeftGutterBoundsForLines(_sqlEntry, _beginLine, _changedLinesCount);

      if(rect.intersects(new Rectangle(e.getPoint(), new Dimension(1,1))))
      {
         JPopupMenu popupMenu = new JPopupMenu();
         popupMenu.add(new RevertablePopupPanel(_formerText, _sqlEntry.getTextComponent().getFont()));
         popupMenu.show(trackingGutterLeft, ChangeTrackPanel.LEFT_GUTTER_WIDTH, e.getY());

      }

   }
}
