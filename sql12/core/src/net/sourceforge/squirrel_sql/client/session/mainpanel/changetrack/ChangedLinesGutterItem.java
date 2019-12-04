package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import com.github.difflib.patch.ChangeDelta;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.fw.gui.CopyToClipboardUtil;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.text.BadLocationException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class ChangedLinesGutterItem implements GutterItem
{
   private ChangeTrackPanel _changeTrackPanel;
   private final ISQLEntryPanel _sqlEntry;
   private ChangeDelta<String> _delta;

   public ChangedLinesGutterItem(ChangeTrackPanel changeTrackPanel, ISQLEntryPanel sqlEntry, ChangeDelta<String> delta)
   {
      _changeTrackPanel = changeTrackPanel;
      _sqlEntry = sqlEntry;
      _delta = delta;
   }

   public void leftPaint(Graphics g)
   {
      int beginLine = getBeginLine();
      int changedLinesCount = getChangedLinesCount();

      Rectangle rect = GutterItemUtil.getLeftGutterBoundsForLines(_sqlEntry, beginLine, changedLinesCount);

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
      Rectangle mark =  GutterItemUtil.getRightGutterMarkBoundsForLines(_changeTrackPanel, _sqlEntry, getBeginLine(), getChangedLinesCount());

      GutterItemUtil.paintRightGutterMark(g, mark, getColor());
   }

   @Override
   public void leftGutterMouseMoved(MouseEvent e, CursorHandler cursorHandler)
   {
      Rectangle rect = GutterItemUtil.getLeftGutterBoundsForLines(_sqlEntry, getBeginLine(), getChangedLinesCount());

      if(null == rect)
      {
         return;
      }

      cursorHandler.setClickable(rect.intersects(new Rectangle(e.getPoint(), new Dimension(1,1))));
   }

   @Override
   public void rightMoveCursorWhenHit(MouseEvent e)
   {
      Rectangle mark =  GutterItemUtil.getRightGutterMarkBoundsForLines(_changeTrackPanel, _sqlEntry, getBeginLine(), getChangedLinesCount());

      if(null == mark)
      {
         return;
      }


      if(mark.intersects(new Rectangle(e.getPoint(), new Dimension(1,1))))
      {
         try
         {
            int lineStartPosition = _sqlEntry.getTextComponent().getLineStartOffset(getBeginLine() - 1);
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
      Rectangle mark =  GutterItemUtil.getRightGutterMarkBoundsForLines(_changeTrackPanel, _sqlEntry, getBeginLine(), getChangedLinesCount());

      if(null == mark)
      {
         return;
      }


      cursorHandler.setClickable(mark.intersects(new Rectangle(e.getPoint(), new Dimension(1,1))));
   }


   @Override
   public void leftShowPopupIfHit(MouseEvent me, JPanel trackingGutterLeft)
   {
      Rectangle rect = GutterItemUtil.getLeftGutterBoundsForLines(_sqlEntry, getBeginLine(), getChangedLinesCount());

      if(null == rect)
      {
         return;
      }

      if(rect.intersects(new Rectangle(me.getPoint(), new Dimension(1,1))))
      {
         String displayText = String.join("\n", _delta.getSource().getLines());

         JPopupMenu popupMenu = new JPopupMenu();
         RevertablePopupPanel revertablePopupPanel = new RevertablePopupPanel(displayText, _sqlEntry.getTextComponent().getFont());
         revertablePopupPanel.btnCopy.addActionListener(ae -> CopyToClipboardUtil.copyToClip(displayText));

         revertablePopupPanel.btnRevert.addActionListener(ae -> onRevert(popupMenu));

         popupMenu.add(revertablePopupPanel);
         popupMenu.show(trackingGutterLeft, ChangeTrackPanel.LEFT_GUTTER_WIDTH, me.getY());

      }

   }

   private void onRevert(JPopupMenu popupMenu)
   {
      try
      {
         int beginPos = _sqlEntry.getTextComponent().getLineStartOffset(getBeginLine() - 1);
         int endPos = _sqlEntry.getTextComponent().getLineEndOffset(getBeginLine() + getChangedLinesCount()- 2);

         _sqlEntry.setSelectionStart(beginPos);
         _sqlEntry.setSelectionEnd(endPos);

         String revertText = String.join("\n", _delta.getSource().getLines())  + "\n";

         _sqlEntry.replaceSelection(revertText);

         popupMenu.setVisible(false);
      }
      catch (BadLocationException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }


   private int getChangedLinesCount()
   {
      return _delta.getTarget().getLines().size();
   }

   private int getBeginLine()
   {
      return _delta.getTarget().getPosition() + 1;
   }

}
