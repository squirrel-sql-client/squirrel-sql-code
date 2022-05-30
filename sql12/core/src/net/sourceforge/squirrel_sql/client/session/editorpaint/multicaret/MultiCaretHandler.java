package net.sourceforge.squirrel_sql.client.session.editorpaint.multicaret;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * https://stackoverflow.com/questions/40955172/java-how-to-select-multiple-non-consecutive-lines-of-text-in-a-text-area-the-s
 */
public class MultiCaretHandler
{
   private MultiEdits _multiEdits;
   private MultiCaretPainter _multiCaretPainter;
   private JTextArea _textArea;
   private DocumentMultiEditor _documentMultiEditor;


   public MultiCaretHandler(JTextArea textArea)
   {
      _textArea = textArea;
      _multiEdits = new MultiEdits(_textArea);
      _multiCaretPainter = new MultiCaretPainter(_textArea, _multiEdits);

      _textArea.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e)
         {
            onKeyPressed(e);
         }
      });

      _documentMultiEditor = new DocumentMultiEditor(_textArea, _multiEdits);

      _textArea.getDocument().addDocumentListener(new DocumentListener()
      {
         /**
          * From {@link DocumentListener#changedUpdate(DocumentEvent)}:
          * Gives notification that an attribute or set of attributes changed.
          * Hence implemented empty.
          */
         public void changedUpdate(DocumentEvent e)
         {
         }

         public void insertUpdate(DocumentEvent e)
         {
            onInsertUpdate(e);
         }

         public void removeUpdate(DocumentEvent e)
         {
            onRemoveUpdate(e);
         }
      });

      _textArea.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e)
         {
            clearMultiEdits();
         }
      });
   }

   /**
    * From {@link DocumentListener#removeUpdate(DocumentEvent)}:
    * Gives notification that a portion of the document has been
    * removed.  The range is given in terms of what the view last
    * saw (that is, before updating sticky positions).
    *
    */
   private void onRemoveUpdate(DocumentEvent e)
   {
      if(_multiEdits.isEmpty())
      {
         return;
      }

      if(_documentMultiEditor.isUpdating())
      {
         return;
      }

      if(0 >= e.getLength())
      {
         return;
      }

      _documentMultiEditor.executeRemove(e);
   }


   private void onInsertUpdate(DocumentEvent e)
   {
      if(_multiEdits.isEmpty())
      {
         return;
      }

      if(_documentMultiEditor.isUpdating())
      {
         return;
      }

      if(0 >= e.getLength())
      {
         return;
      }

      _documentMultiEditor.executeInsert(e);
   }


   public void onKeyPressed(KeyEvent e)
   {
      if(isAddNextTrigger(e))
      {
         createNextHighlight();
      }
      else if(isRemovePreviousTrigger(e))
      {
         deleteLastHighlight();
      }
      else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
      {
         clearMultiEdits();
      }
      else
      {
         if(_multiEdits.isEmpty())
         {
            return;
         }

         SwingUtilities.invokeLater(() -> adjustHighlights());
      }

   }

   private void adjustHighlights()
   {
      if(_multiEdits.isEmpty())
      {
         return;
      }

      int relativeSelectionStart = _textArea.getSelectionStart() - _textArea.getCaretPosition();
      int relativeSelectionEnd = _textArea.getSelectionEnd() - _textArea.getCaretPosition();

      final int relativeCaretPosition = _multiEdits.relativeCaretPosition();

      for (SingleEdit singleEdit : _multiEdits.all())
      {
         HighlightUtil.removeHighlight(_textArea, singleEdit.getHighLightTag());

         final int thisEditsCaretPos = singleEdit.getStart() + relativeCaretPosition;
         final int thisEditsSelectionStart = thisEditsCaretPos + relativeSelectionStart;
         final int thisEditsSelectionEnd = thisEditsCaretPos + relativeSelectionEnd;

         singleEdit.setHighLightTag(HighlightUtil.highlightRange(_textArea, thisEditsSelectionStart, thisEditsSelectionEnd));
      }

      SwingUtilities.invokeLater(() -> _multiEdits.scrollToLastCaret());
   }

   private void deleteLastHighlight()
   {
      if(_multiEdits.isEmpty())
      {
         return;
      }

      HighlightUtil.removeHighlight(_textArea, _multiEdits.removeLast().getHighLightTag());


      if(_multiEdits.isEmpty())
      {
         _textArea.setSelectionStart(_textArea.getCaretPosition());
         _textArea.setSelectionEnd(_textArea.getCaretPosition());
      }
      else
      {
         _textArea.scrollRectToVisible(GUIUtils.getRectangleOfPosition(_textArea, _multiEdits.last().getStart()));
      }
   }

   private void createNextHighlight()
   {
      SingleEdit buf = getNextTextToFind();

      if(buf.isEmpty())
      {
         return;
      }

      _multiEdits.add(buf);
      newHighLight(_multiEdits.last());

      if(false == _multiEdits.isEmpty() && _multiEdits.last().isFromSelection())
      {
         createNextHighlight();
      }
   }

   private void newHighLight(SingleEdit singleEdit)
   {
      final Object highlightTag = HighlightUtil.highlightRange(_textArea, singleEdit.getStart(), singleEdit.getEnd());
      singleEdit.setHighLightTag(highlightTag);

      _textArea.scrollRectToVisible(GUIUtils.getRectangleOfPosition(_textArea, singleEdit.getStart()));
   }

   private void clearMultiEdits()
   {
      if(_multiEdits.isEmpty())
      {
         return;
      }

      for (SingleEdit singleEdit : _multiEdits.all())
      {
         HighlightUtil.removeHighlight(_textArea, singleEdit.getHighLightTag());
      }

      _multiEdits.clear();
   }

   private boolean isAddNextTrigger(KeyEvent e)
   {
      return e.getModifiersEx() == KeyEvent.ALT_DOWN_MASK && e.getKeyCode() == KeyEvent.VK_J;
   }

   private boolean isRemovePreviousTrigger(KeyEvent e)
   {
      return    (e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) == KeyEvent.ALT_DOWN_MASK
             && (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) == KeyEvent.SHIFT_DOWN_MASK
             && e.getKeyCode() == KeyEvent.VK_J;
   }


   private SingleEdit getNextTextToFind()
   {
      if(_multiEdits.isEmpty())
      {
         final SingleEdit ret = new SingleEdit(_textArea.getSelectedText(), _textArea.getSelectionStart(), _textArea.getSelectionEnd());
         ret.setFromSelection(true);
         return ret;
      }
      else
      {
         final int nextStart =
               _textArea.getText().indexOf(_multiEdits.last().getText(), _multiEdits.last().getEnd());

         if(-1 == nextStart)
         {
            return SingleEdit.EMPTY;
         }
         else
         {
            return new SingleEdit(_multiEdits.last().getText(), nextStart, nextStart + _multiEdits.last().getText().length());
         }
      }
   }

   public void onPaintComponent(Graphics g)
   {
      _multiCaretPainter.paintAdditionalCarets(g);
   }
}
