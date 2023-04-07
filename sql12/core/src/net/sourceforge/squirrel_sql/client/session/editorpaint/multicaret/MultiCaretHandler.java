package net.sourceforge.squirrel_sql.client.session.editorpaint.multicaret;

import net.sourceforge.squirrel_sql.client.session.editorpaint.EditorPaintService;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import org.apache.commons.lang3.StringUtils;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
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
   private final EditorPaintService _editorPaintService;
   private DocumentMultiEditor _documentMultiEditor;


   public MultiCaretHandler(JTextArea textArea, EditorPaintService editorPaintService)
   {
      _textArea = textArea;
      _editorPaintService = editorPaintService;
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

      if(isUndoRedoDocumentEvent(e))
      {
         clearMultiEdits();
         return;
      }

      _documentMultiEditor.executeRemove(e);
   }

   private boolean isUndoRedoDocumentEvent(DocumentEvent e)
   {
      return "javax.swing.text.AbstractDocument$UndoRedoDocumentEvent".equals(e.getClass().getName());
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

      if(isUndoRedoDocumentEvent(e))
      {
         clearMultiEdits();
         return;
      }


      _documentMultiEditor.executeInsert(e);
   }


   public void onKeyPressed(KeyEvent e)
   {
//      if(isAddNextTrigger(e))
//      {
//         createNextCaret();
//      }
//      else if(isRemovePreviousTrigger(e))
//      {
//         removeLastCaret();
//      }


      if(_multiEdits.isEmpty())
      {
         return;
      }

      if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
      {
         clearMultiEdits();
      }
      else
      {
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
      SwingUtilities.invokeLater(() -> _textArea.repaint());
   }

   public void removeLastCaret()
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

      if(false == _multiEdits.requiresMultipleCarets())
      {
         _editorPaintService.setPauseInsertPairedCharacters(false);
      }

      SwingUtilities.invokeLater(() -> _textArea.repaint());
   }

   public void createNextCaret()
   {
      SingleEdit buf = getNextEdit();

      if(null == buf)
      {
         return;
      }

      boolean didRequireMultipleCarets = _multiEdits.requiresMultipleCarets();

      _multiEdits.add(buf);
      newCaret(_multiEdits.last());

      if(false == _multiEdits.isEmpty() && _multiEdits.last().isInitialEdit())
      {
         createNextCaret();
      }

      if(false == didRequireMultipleCarets && _multiEdits.requiresMultipleCarets())
      {
         _editorPaintService.setPauseInsertPairedCharacters(true);
      }

      SwingUtilities.invokeLater(() -> _textArea.repaint());
   }

   private void newCaret(SingleEdit singleEdit)
   {
      final Object highlightTag = HighlightUtil.highlightRange(_textArea, singleEdit.getStart(), singleEdit.getEnd());
      singleEdit.setHighLightTag(highlightTag);

      _textArea.scrollRectToVisible(GUIUtils.getRectangleOfPosition(_textArea, singleEdit.getStart()));
   }

   private void clearMultiEdits()
   {
      for (SingleEdit singleEdit : _multiEdits.all())
      {
         HighlightUtil.removeHighlight(_textArea, singleEdit.getHighLightTag());
      }

      _multiEdits.clear();

      if(false == _multiEdits.requiresMultipleCarets())
      {
         _editorPaintService.setPauseInsertPairedCharacters(false);
      }
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


   private SingleEdit getNextEdit()
   {
      if(_multiEdits.isEmpty())
      {
         final SingleEdit ret = new SingleEdit(_textArea.getSelectedText(), _textArea.getSelectionStart(), _textArea.getSelectionEnd());
         ret.setInitialEdit(true);
         return ret;
      }
      else
      {
         final int nextEditsStart;
         if( _multiEdits.last().isEmptySelection() )
         {
            nextEditsStart = getNextEditsStartFromPositionInLine();
         }
         else
         {
            nextEditsStart = StringUtils.indexOfIgnoreCase(_textArea.getText(), _multiEdits.last().getText(), _multiEdits.last().getEnd());
            //nextEditsStart = _textArea.getText().indexOf(_multiEdits.last().getText(), _multiEdits.last().getEnd());
         }

         if(-1 == nextEditsStart)
         {
            return null;
         }
         else
         {
            return new SingleEdit(_multiEdits.last().getText(), nextEditsStart, nextEditsStart + _multiEdits.last().getLength());
         }
      }
   }

   private int getNextEditsStartFromPositionInLine()
   {
      try
      {
         int previousEditsLine = _textArea.getLineOfOffset(_multiEdits.last().getStart());

         int previousEditsPositionInLine = _multiEdits.last().getStart() - _textArea.getLineStartOffset(previousEditsLine);

         int lineCount = _textArea.getLineCount();
         for( int line = previousEditsLine + 1; line < lineCount; line++ )
         {
            // _textArea.getLineEndOffset(line) returns the position of \n
            if( _textArea.getLineEndOffset(line) - 1 - _textArea.getLineStartOffset(line) >= previousEditsPositionInLine )
            {
               return _textArea.getLineStartOffset(line) + previousEditsPositionInLine;
            }
         }

         return -1;
      }
      catch(BadLocationException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   public void onPaintComponent(Graphics g)
   {
      _multiCaretPainter.paintAdditionalCarets(g);
   }
}
