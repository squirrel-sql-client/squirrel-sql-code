package net.sourceforge.squirrel_sql.client.session.editorpaint.multicaret;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.JTextArea;
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

   public MultiCaretPainter(JTextArea textArea, MultiEdits multiEdits)
   {
      _textArea = textArea;
      _multiEdits = multiEdits;
   }

   /**
    * See {@link DefaultCaret#paint(Graphics)}.
    * Astonishingly works without a {@link DefaultCaret#flasher}.
    * Probably because of the flashing done by the DefaultCaret.
    */
   public void paintAdditionalCarets(Graphics g)
   {
      if(false == _textArea.getCaret().isVisible() || false == _multiEdits.requiresMultipleCarets())
      {
         if(false == _emptyRepaintDone)
         {
            _textArea.repaint();
            _emptyRepaintDone = true;
         }
         return;
      }
      _emptyRepaintDone = false;

      int relativeCaretPosition = _multiEdits.relativeCaretPosition();

      final DefaultCaret defaultCaret = (DefaultCaret) _textArea.getCaret();
      final Position.Bias dotBias = defaultCaret.getDotBias();

      Integer caretWidthProperty = (Integer) _textArea.getClientProperty("caretWidth");

      int caretWidth;
      int caretXDisplacement;

      if(null == caretWidthProperty)
      {
         // This is the case when _textArea.getClass() == SquirrelRSyntaxTextArea

         caretWidth = 2;
         caretXDisplacement = 0;
      }
      else
      {
         // This is the case when _textArea.getClass() == SquirrelDefaultTextArea

         caretWidth = caretWidthProperty;

         // See {@link DefaultCaret#paint(Graphics)}.
         caretXDisplacement = caretWidth  >> 1;
      }

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
            r.x -= caretXDisplacement;
            g.fillRect(r.x, r.y, caretWidth, r.height);

            //g.fillRect(r.x, r.y, 20, r.height);
            //System.out.println("r = " + r);
         }
         finally
         {
            g.setColor(previousColor);
         }
      }

      _textArea.repaint();
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
