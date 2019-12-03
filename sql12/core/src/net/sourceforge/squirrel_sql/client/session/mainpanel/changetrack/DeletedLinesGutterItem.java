package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.fw.gui.CopyToClipboardUtil;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.text.BadLocationException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class DeletedLinesGutterItem implements GutterItem
{
   private ChangeTrackPanel _changeTrackPanel;
   private final ISQLEntryPanel _sqlEntry;
   private int _lineCount;
   private final int _lineBefore;
   private final String _deletedText;

   public DeletedLinesGutterItem(ChangeTrackPanel changeTrackPanel, ISQLEntryPanel sqlEntry, int lineCount, int lineBefore, String deletedText)
   {
      _changeTrackPanel = changeTrackPanel;
      _sqlEntry = sqlEntry;
      _lineCount = lineCount;
      _lineBefore = lineBefore;
      _deletedText = deletedText;
   }

   @Override
   public void leftPaint(Graphics g)
   {
      Rectangle rectBefore = GutterItemUtil.getLeftGutterBoundsForLines(_sqlEntry, _lineBefore, 1);

      if(null == rectBefore)
      {
         return;
      }

      int y = calculateY(rectBefore);

      paintArrow(g, rectBefore.x, y, rectBefore.x + rectBefore.width, y);
   }

   private int calculateY(Rectangle rectBefore)
   {
      int y;
      if (rectBefore.y < 2)
      {
         y = 2;
      }
      else
      {
         y = rectBefore.y + rectBefore.height;
      }
      return y;
   }

   @Override
   public void leftShowPopupIfHit(MouseEvent me, JPanel trackingGutterLeft)
   {
      if(intersects(me))
      {
         JPopupMenu popupMenu = new JPopupMenu();
         RevertablePopupPanel revertablePopupPanel = new RevertablePopupPanel(_deletedText, _sqlEntry.getTextComponent().getFont());

         revertablePopupPanel.btnCopy.addActionListener(ae -> CopyToClipboardUtil.copyToClip(_deletedText));

         revertablePopupPanel.btnRevert.addActionListener(ae -> onRevert(popupMenu));

         popupMenu.add(revertablePopupPanel);
         popupMenu.show(trackingGutterLeft, ChangeTrackPanel.LEFT_GUTTER_WIDTH, me.getY());
      }
   }

   private void onRevert(JPopupMenu popupMenu)
   {
      try
      {
         int insertPos;

         if( _lineCount <= _lineBefore)
         {
            insertPos = _sqlEntry.getTextComponent().getLineEndOffset(_lineCount - 1);
         }
         else
         {
            insertPos = _sqlEntry.getTextComponent().getLineStartOffset(_lineBefore);
         }


         _sqlEntry.setCaretPosition(insertPos);

         _sqlEntry.replaceSelection(_deletedText);

         popupMenu.setVisible(false);
      }
      catch (BadLocationException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private boolean intersects(MouseEvent e)
   {
      Rectangle rectBefore = GutterItemUtil.getLeftGutterBoundsForLines(_sqlEntry, _lineBefore, 1);

      if(null == rectBefore)
      {
         return false;
      }

      int y = calculateY(rectBefore);

      Polygon triangle = getTriangle(rectBefore.x, y, rectBefore.x + rectBefore.width, y);

      return triangle.getBounds().intersects(new Rectangle(e.getPoint(), new Dimension(1, 1)));
   }

   @Override
   public void rightPaint(Graphics g)
   {
      Rectangle mark =  GutterItemUtil.getRightGutterMarkBoundsForLines(_changeTrackPanel, _sqlEntry, _lineBefore, 1);

      GutterItemUtil.paintRightGutterMark(g, mark, getColor());
   }

   @Override
   public void leftGutterMouseMoved(MouseEvent e, CursorHandler cursorHandler)
   {
      cursorHandler.setClickable(intersects(e));
   }

   @Override
   public void rightMoveCursorWhenHit(MouseEvent e)
   {
      Rectangle mark =  GutterItemUtil.getRightGutterMarkBoundsForLines(_changeTrackPanel, _sqlEntry, _lineBefore, 1);

      if(null == mark)
      {
         return;
      }

      if(mark.intersects(new Rectangle(e.getPoint(), new Dimension(1,1))))
      {
         try
         {
            int lineStartPosition = _sqlEntry.getTextComponent().getLineStartOffset(Math.max(_lineBefore -1, 0));
            _sqlEntry.setCaretPosition(lineStartPosition);
         }
         catch (BadLocationException ex)
         {
         }
      }
   }

   @Override
   public void rightGutterMouseMoved(MouseEvent e, CursorHandler cursorHandler)
   {
      Rectangle mark =  GutterItemUtil.getRightGutterMarkBoundsForLines(_changeTrackPanel, _sqlEntry, _lineBefore, 1);

      if(null == mark)
      {
         return;
      }

      cursorHandler.setClickable(mark.intersects(new Rectangle(e.getPoint(), new Dimension(1,1))));
   }

   private Color getColor()
   {
      return Color.black;
   }

   private void paintArrow(Graphics g, int x1, int y1, int x2, int y2)
   {
      Color buf = g.getColor();
      g.setColor(getColor());

      Polygon pg = getTriangle(x1, y1, x2, y2);
      g.fillPolygon(pg);

      g.setColor(buf);
   }

   private Polygon getTriangle(int x1, int y1, int x2, int y2)
   {
      // defines the opening angle of the arrow (not rad or so but something fancy)
      double sAng = 0.5;

      Point c = new Point(x2, y2);
      Point a = new Point((int) (x1 + sAng * (y2 - y1)), (int) (y1 - sAng * (x2 - x1)));
      Point b = new Point((int) (x1 - sAng * (y2 - y1)), (int) (y1 + sAng * (x2 - x1)));

      // defines the size of the arrow
      double sLen = 10 / Math.sqrt((a.x - c.x) * (a.x - c.x) + (a.y - c.y) * (a.y - c.y));

      Point arrPa = new Point((int) (c.x + sLen * (a.x - c.x)), (int) (c.y + sLen * (a.y - c.y)));
      Point arrPb = new Point((int) (c.x + sLen * (b.x - c.x)), (int) (c.y + sLen * (b.y - c.y)));


      Polygon pg = new Polygon();
      pg.addPoint(arrPa.x, arrPa.y);
      pg.addPoint(arrPb.x, arrPb.y);
      pg.addPoint(c.x, c.y);
      return pg;
   }

}
