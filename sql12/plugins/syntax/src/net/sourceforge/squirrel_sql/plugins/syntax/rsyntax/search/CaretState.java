package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.search;

import net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.SquirrelRSyntaxTextArea;

public class CaretState
{
   private int _selectionStart;
   private int _selectionEnd;
   private int _caretPosition;


   public CaretState(SquirrelRSyntaxTextArea squirrelRSyntaxTextArea)
   {
      _selectionStart = squirrelRSyntaxTextArea.getSelectionStart();
      _selectionEnd = squirrelRSyntaxTextArea.getSelectionEnd();
      _caretPosition = squirrelRSyntaxTextArea.getCaretPosition();
   }

   void restoreCaretState(SquirrelRSyntaxTextArea squirrelRSyntaxTextArea)
   {
      if (_selectionStart  < squirrelRSyntaxTextArea.getText().length())
      {
         squirrelRSyntaxTextArea.setSelectionStart(_selectionStart);
      }
      if (_selectionEnd  < squirrelRSyntaxTextArea.getText().length())
      {
         squirrelRSyntaxTextArea.setSelectionEnd(_selectionEnd);
      }
      if (_caretPosition < squirrelRSyntaxTextArea.getText().length())
      {
         squirrelRSyntaxTextArea.setSelectionEnd(_caretPosition);
      }
   }
}
