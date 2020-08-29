package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import com.github.difflib.patch.DeleteDelta;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
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
   private int _scriptLineCount;
   private DeleteDelta<String> _delta;

   public DeletedLinesGutterItem(ChangeTrackPanel changeTrackPanel, ISQLEntryPanel sqlEntry, int scriptLineCount, DeleteDelta<String> delta)
   {
      _changeTrackPanel = changeTrackPanel;
      _sqlEntry = sqlEntry;
      _scriptLineCount = scriptLineCount;
      _delta = delta;
   }

   @Override
   public void leftPaint(Graphics g)
   {
      int y = getYOfLine(_sqlEntry, getDeletePosition());

      paintArrow(g, 0, y, ChangeTrackPanel.LEFT_GUTTER_WIDTH, y);
   }

   private int getYOfLine(ISQLEntryPanel sqlEntry, int deletePosition)
   {
      Rectangle rect;

      Rectangle visibleRect = sqlEntry.getTextComponent().getVisibleRect();

      try
      {
         rect = sqlEntry.getTextComponent().modelToView(sqlEntry.getTextComponent().getLineStartOffset(deletePosition));
         return correctYForEditorBegin(rect.y - visibleRect.y);
      }
      catch (BadLocationException e)
      {
         try
         {
            // We were below the end.
            if (0 < sqlEntry.getTextComponent().getLineCount())
            {
               rect = sqlEntry.getTextComponent().modelToView(sqlEntry.getTextComponent().getLineStartOffset(sqlEntry.getTextComponent().getLineCount() - 1));

               return correctYForEditorBegin(rect.y - visibleRect.y + rect.height);
            }
            else
            {
               return correctYForEditorBegin(0);
            }
         }
         catch (BadLocationException ex)
         {
            throw Utilities.wrapRuntime(ex);
         }
      }
   }

   private int correctYForEditorBegin(int y)
   {
      if (0 <= y && y < 2)
      {
         return 2;
      }
      else
      {
         return y;
      }
   }

   @Override
   public void leftShowPopupIfHit(MouseEvent me, JPanel trackingGutterLeft)
   {
      if(intersects(me))
      {
         String displayText = String.join("\n", _delta.getSource().getLines());


         JPopupMenu popupMenu = new JPopupMenu();
         RevertablePopupPanel revertablePopupPanel = new RevertablePopupPanel(displayText, _sqlEntry.getTextComponent().getFont());

         revertablePopupPanel.btnCopy.addActionListener(ae -> ClipboardUtil.copyToClip(displayText));

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

         if( _scriptLineCount <= getDeletePosition())
         {
            insertPos = _sqlEntry.getTextComponent().getLineEndOffset(_scriptLineCount - 1);
         }
         else
         {
            insertPos = _sqlEntry.getTextComponent().getLineStartOffset(getDeletePosition());
         }


         _sqlEntry.setCaretPosition(insertPos);

         String revertText = String.join("\n", _delta.getSource().getLines()) + "\n";
         _sqlEntry.replaceSelection(revertText);

         popupMenu.setVisible(false);
      }
      catch (BadLocationException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private int getDeletePosition()
   {
      return _delta.getTarget().getPosition();
   }

   private boolean intersects(MouseEvent e)
   {
      int y = getYOfLine(_sqlEntry, getDeletePosition());


      Polygon triangle = getTriangle(0, y, ChangeTrackPanel.LEFT_GUTTER_WIDTH, y);

      return triangle.getBounds().intersects(new Rectangle(e.getPoint(), new Dimension(1, 1)));
   }

   @Override
   public void rightPaint(Graphics g)
   {
      Rectangle mark =  GutterItemUtil.getRightGutterMarkBoundsForLines(_changeTrackPanel, _sqlEntry, getDeletePosition(), 1);

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
      Rectangle mark =  GutterItemUtil.getRightGutterMarkBoundsForLines(_changeTrackPanel, _sqlEntry, getDeletePosition(), 1);

      if(null == mark)
      {
         return;
      }

      if(mark.intersects(new Rectangle(e.getPoint(), new Dimension(1,1))))
      {
         try
         {
            int lineStartPosition = _sqlEntry.getTextComponent().getLineStartOffset(Math.max(getDeletePosition() -1, 0));
            GutterItemUtil.positionCaretAndScroll(lineStartPosition, _sqlEntry);
         }
         catch (BadLocationException ex)
         {
         }
      }
   }

   @Override
   public void rightGutterMouseMoved(MouseEvent e, CursorHandler cursorHandler)
   {
      Rectangle mark =  GutterItemUtil.getRightGutterMarkBoundsForLines(_changeTrackPanel, _sqlEntry, getDeletePosition(), 1);

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
