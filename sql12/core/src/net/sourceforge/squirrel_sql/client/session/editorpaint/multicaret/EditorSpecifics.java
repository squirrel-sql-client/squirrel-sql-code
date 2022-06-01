package net.sourceforge.squirrel_sql.client.session.editorpaint.multicaret;

import net.sourceforge.squirrel_sql.client.session.SquirrelDefaultTextArea;

import javax.swing.JTextArea;

public class EditorSpecifics
{
   public static EditorSpecificCaretData getEditorSpecificCaretData(JTextArea textArea)
   {
      Integer caretWidthProperty = (Integer) textArea.getClientProperty("caretWidth");

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

      return new EditorSpecificCaretData(caretWidth, caretXDisplacement);
   }

   public static int adjustHiglightEndPos(JTextArea textArea, int endPos)
   {
      if(textArea instanceof SquirrelDefaultTextArea || textArea instanceof Test_MultiCaretTextArea)
      {
         return endPos;
      }

      // RSyntax
      return endPos - 1;
   }
}
