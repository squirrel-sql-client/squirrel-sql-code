package net.sourceforge.squirrel_sql.client.session.editorpaint.multicaret;

public class EditorSpecificCaretData
{
   private final int _caretWidth;
   private final int _caretXDisplacement;

   public EditorSpecificCaretData(int caretWidth, int caretXDisplacement)
   {
      _caretWidth = caretWidth;
      _caretXDisplacement = caretXDisplacement;
   }

   public int getCaretWidth()
   {
      return _caretWidth;
   }

   public int getCaretXDisplacement()
   {
      return _caretXDisplacement;
   }
}
