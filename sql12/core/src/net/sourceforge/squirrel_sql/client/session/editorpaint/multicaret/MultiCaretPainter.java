package net.sourceforge.squirrel_sql.client.session.editorpaint.multicaret;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Position;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class MultiCaretPainter
{
   private final JTextArea _textArea;
   private final MultiEdits _multiEdits;
   private boolean _emptyRepaintDone;

   private Timer _repaintWithDelayBeyondPerceptiblyTimer;

   public MultiCaretPainter(JTextArea textArea, MultiEdits multiEdits)
   {
      _textArea = textArea;
      _multiEdits = multiEdits;

      _repaintWithDelayBeyondPerceptiblyTimer = new Timer(100, e -> _textArea.repaint());
      _repaintWithDelayBeyondPerceptiblyTimer.setRepeats(false);
   }

   /**
    * See {@link DefaultCaret#paint(Graphics)}.
    * Astonishingly works without a {@link DefaultCaret#flasher}.
    * Probably because of the flashing done by the DefaultCaret.
    */
   public void paintAdditionalCarets(Graphics g)
   {
      if(false == _multiEdits.requiresMultipleCarets())
      {
         if(false == _emptyRepaintDone)
         {
            repaintWithDelayBeyondPerceptibly();
            _emptyRepaintDone = true;
         }
         return;
      }
      _emptyRepaintDone = false;


      if(_textArea.getCaret().isVisible())
      {
         // For a blinking caret _textArea.getCaret() changes between visible=true and visible=false
         // When it's not visible we just don't paint it.

         int relativeCaretPosition = _multiEdits.relativeCaretPosition();

         final DefaultCaret defaultCaret = (DefaultCaret) _textArea.getCaret();
         final Position.Bias dotBias = defaultCaret.getDotBias();

         EditorSpecificCaretData editorSpecificCaretData = EditorSpecifics.getEditorSpecificCaretData(_textArea);

         for (SingleEdit singleEdit : _multiEdits.allButInitial())
         {
            final int additionalCaretsPosition = singleEdit.getStart() + relativeCaretPosition;

            if(additionalCaretsPosition < 0 || additionalCaretsPosition >= _textArea.getText().length())
            {
               continue;
            }

            Rectangle r = GUIUtils.getRectangleOfPosition(_textArea, additionalCaretsPosition, dotBias);

            Color previousColor = g.getColor();
            try
            {
               g.setColor(_textArea.getCaretColor());
               r.x -= editorSpecificCaretData.getCaretXDisplacement();
               g.fillRect(r.x, r.y, editorSpecificCaretData.getCaretWidth(), r.height);

               //g.fillRect(r.x, r.y, 20, r.height);
               //System.out.println("r = " + r);
            }
            finally
            {
               g.setColor(previousColor);
            }
         }
      }

      repaintWithDelayBeyondPerceptibly();
   }

   /**
    * This method is called from within {@link #paintAdditionalCarets(Graphics)} which is
    * called by {@link #_textArea}'s paintComponent(...) method.
    * To prevent paintComponent(...) being called quasi always when additional carets exist
    * we introduced this delayed call.
    *
    * Calling this method ensures additional carets blink alright even if the original caret
    * is scrolled out of sight.
    */
   private void repaintWithDelayBeyondPerceptibly()
   {
      if(false == _repaintWithDelayBeyondPerceptiblyTimer.isRunning())
      {
         _repaintWithDelayBeyondPerceptiblyTimer.start();
      }
   }

//   private int getCaretWidth()
//   {
//      Integer caretWidth = (Integer) _textArea.getClientProperty("caretWidth");
//
//      if(null == caretWidth)
//      {
//         caretWidth = 1;
//      }
//
//      return caretWidth;
//   }
}
