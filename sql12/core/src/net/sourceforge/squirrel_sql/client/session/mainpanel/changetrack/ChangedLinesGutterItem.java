package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import com.github.difflib.patch.ChangeDelta;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.matchpatch.ChangeRenderer;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.matchpatch.ChangeRendererStyle;
import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class ChangedLinesGutterItem implements GutterItem
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ChangedLinesGutterItem.class);

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
         String sourceText = String.join("\n", _delta.getSource().getLines());
         String targetText = String.join("\n", _delta.getTarget().getLines());

         JPopupMenu popupMenu = new JPopupMenu();
         RevertablePopupPanel revertablePopupPanel = new RevertablePopupPanel(sourceText, _sqlEntry.getTextComponent().getFont());
         revertablePopupPanel.btnCopy.addActionListener(ae -> ClipboardUtil.copyToClip(sourceText));
         revertablePopupPanel.btnRevert.addActionListener(ae -> onRevert(popupMenu));

         revertablePopupPanel.txtFormerText.setText("");
         ChangeRenderer.renderChangeInTextPane(revertablePopupPanel.txtFormerText, sourceText, targetText, ChangeRendererStyle.createFromPrefs());


         // The ScrollPane prevents NollPointer exception deep inside Swing code
         // when the popup is too wide to fit on the screen.
         final JScrollPane scrollPane = new JScrollPane(revertablePopupPanel);
         scrollPane.setBorder(BorderFactory.createEmptyBorder());
         popupMenu.add(scrollPane);
         popupMenu.show(trackingGutterLeft, ChangeTrackPanel.LEFT_GUTTER_WIDTH, me.getY());
      }
   }

   private void onRevert(JPopupMenu popupMenu)
   {
      try
      {
         int beginPos = _sqlEntry.getTextComponent().getLineStartOffset(getBeginLine() - 1);
         int endPos = _sqlEntry.getTextComponent().getLineEndOffset(getBeginLine() -1 + getChangedLinesCount() - 1);

         _sqlEntry.setSelectionStart(beginPos);
         _sqlEntry.setSelectionEnd(endPos);

         String revertText;

         if (endPos == _sqlEntry.getText().length() && false == endsWithNewLine(_sqlEntry.getText()))
         {
            revertText = String.join("\n", _delta.getSource().getLines());
         }
         else
         {
            revertText = String.join("\n", _delta.getSource().getLines())  + "\n";
         }

         _sqlEntry.replaceSelection(revertText);

         _sqlEntry.setCaretPosition(beginPos);

         popupMenu.setVisible(false);
      }
      catch (BadLocationException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private boolean endsWithNewLine(String text)
   {
      if(0 == text.length())
      {
         return false;
      }

      return '\n' == text.charAt(text.length() - 1);
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
