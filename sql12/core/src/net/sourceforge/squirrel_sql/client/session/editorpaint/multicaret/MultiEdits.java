package net.sourceforge.squirrel_sql.client.session.editorpaint.multicaret;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.JTextArea;
import java.util.List;
import java.util.Stack;

public class MultiEdits
{
   private Stack<SingleEdit> _singleEditStack = new Stack<>();
   private JTextArea _textArea;

   public MultiEdits(JTextArea textArea)
   {
      _textArea = textArea;
   }

   public void add(SingleEdit singleEdit)
   {
      _singleEditStack.push(singleEdit);
   }

   public SingleEdit last()
   {
      return _singleEditStack.peek();
   }

   public boolean isEmpty()
   {
      return _singleEditStack.isEmpty();
   }

   public List<SingleEdit> all()
   {
      return _singleEditStack;
   }

   public List<SingleEdit> allButInitial()
   {
      return _singleEditStack.subList(1, _singleEditStack.size());
   }

   public void clear()
   {
      _singleEditStack.clear();
   }

   public SingleEdit removeLast()
   {
      return _singleEditStack.pop();
   }

   public boolean requiresMultipleCarets()
   {
      return 1 < _singleEditStack.size();
   }

   /**
    *
    * @return The original edit of the text area's real caret.
    */
   public SingleEdit initial()
   {
      return _singleEditStack.firstElement();
   }

   public int relativeCaretPosition()
   {
      final int caretPosition = _textArea.getCaretPosition();
      SingleEdit realCaretAnchor = initial();
      int relativeCaretPosition = caretPosition - realCaretAnchor.getStart();
      return relativeCaretPosition;
   }

   public void scrollToLastCaret()
   {
      if(false == requiresMultipleCarets())
      {
         return;
      }

      _textArea.scrollRectToVisible(GUIUtils.getRectangleOfPosition(_textArea, last().getStart() + relativeCaretPosition()));
   }
}
