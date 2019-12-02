package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.text.BadLocationException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class AddedLinesGutterItem implements GutterItem
{
   private ChangeTrackPanel _changeTrackPanel;
   private final ISQLEntryPanel _sqlEntry;
   private final int _beginLine;
   private final int _newLinesCount;

   public AddedLinesGutterItem(ChangeTrackPanel changeTrackPanel, ISQLEntryPanel sqlEntry, int beginLine, int newLinesCount)
   {
      _changeTrackPanel = changeTrackPanel;
      _sqlEntry = sqlEntry;
      _beginLine = beginLine;
      _newLinesCount = newLinesCount;
   }

   public void leftPaint(Graphics g)
   {
      Rectangle rect = GutterItemUtil.getLeftGutterBoundsForLines(_sqlEntry, _beginLine, _newLinesCount);

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
      return new Color(150, 180, 150);
   }

   @Override
   public void rightPaint(Graphics g)
   {
      Rectangle mark =  GutterItemUtil.getRightGutterMarkBoundsForLines(_changeTrackPanel, _sqlEntry,_beginLine, _newLinesCount);

      GutterItemUtil.paintRightGutterMark(g, mark, getColor());
   }

   @Override
   public void leftGutterMouseMoved(MouseEvent e, CursorHandler cursorHandler)
   {
      Rectangle rect = GutterItemUtil.getLeftGutterBoundsForLines(_sqlEntry, _beginLine, _newLinesCount);

      if(null == rect)
      {
         return;
      }


      cursorHandler.setClickable(rect.intersects(new Rectangle(e.getPoint(), new Dimension(1,1))));
   }

   @Override
   public void rightMoveCursorWhenHit(MouseEvent e)
   {
      Rectangle mark =  GutterItemUtil.getRightGutterMarkBoundsForLines(_changeTrackPanel, _sqlEntry, _beginLine, _newLinesCount);

      if(null == mark)
      {
         return;
      }


      if(mark.intersects(new Rectangle(e.getPoint(), new Dimension(1,1))))
      {
         try
         {
            int lineStartPosition = _sqlEntry.getTextComponent().getLineStartOffset(_beginLine - 1);
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
      Rectangle mark =  GutterItemUtil.getRightGutterMarkBoundsForLines(_changeTrackPanel, _sqlEntry, _beginLine, _newLinesCount);

      if(null == mark)
      {
         return;
      }


      cursorHandler.setClickable(mark.intersects(new Rectangle(e.getPoint(), new Dimension(1,1))));
   }


   @Override
   public void leftShowPopupIfHit(MouseEvent e, JPanel trackingGutterLeft)
   {
      Rectangle rect = GutterItemUtil.getLeftGutterBoundsForLines(_sqlEntry, _beginLine, _newLinesCount);

      if(null == rect)
      {
         return;
      }


      if(rect.intersects(new Rectangle(e.getPoint(), new Dimension(1,1))))
      {
         JPopupMenu popupMenu = new JPopupMenu();
         AddedLinesPopupPanel addedLinesPopupPanel = new AddedLinesPopupPanel();

         addedLinesPopupPanel.btnRevert.addActionListener(ae -> onRevert(popupMenu));

         popupMenu.add(addedLinesPopupPanel);
         popupMenu.show(trackingGutterLeft, ChangeTrackPanel.LEFT_GUTTER_WIDTH, e.getY());
      }
   }

   private void onRevert(JPopupMenu popupMenu)
   {
      try
      {
         int beginPos = _sqlEntry.getTextComponent().getLineStartOffset(_beginLine -1);
         int endPos = _sqlEntry.getTextComponent().getLineEndOffset(_beginLine + _newLinesCount -2);
         _sqlEntry.setSelectionStart(beginPos);
         _sqlEntry.setSelectionEnd(endPos);

         _sqlEntry.replaceSelection("");

         popupMenu.setVisible(false);
      }
      catch (BadLocationException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

}
