package net.sourceforge.squirrel_sql.client.gui.session.rowcolumnlabel;

public class CaretPositionInfo
{
   private final int _caretLineNumber;
   private final int _caretLinePosition;
   private final int _caretPosition;

   public CaretPositionInfo(int caretLineNumber, int caretLinePosition, int caretPosition)
   {
      _caretLineNumber = caretLineNumber;
      _caretLinePosition = caretLinePosition;
      _caretPosition = caretPosition;
   }

   public int getCaretLineNumber()
   {
      return _caretLineNumber;
   }

   public int getCaretLinePosition()
   {
      return _caretLinePosition;
   }

   public int getCaretPosition()
   {
      return _caretPosition;
   }
}
