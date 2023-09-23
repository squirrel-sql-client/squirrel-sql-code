package net.sourceforge.squirrel_sql.client.gui.session.rowcolumnlabel;

public class CaretPositionInfo
{
   private final int _caretLineNumber;
   private final int _caretLinePosition;
   private final int _caretPosition;
   private Integer _positionInCurrentSQL = null;

   public CaretPositionInfo(int caretLineNumber, int caretLinePosition, int caretPosition, Integer positionInCurrentSQL)
   {
      _caretLineNumber = caretLineNumber;
      _caretLinePosition = caretLinePosition;
      _caretPosition = caretPosition;
      _positionInCurrentSQL = positionInCurrentSQL;
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

   public Integer getPositionInCurrentSQL()
   {
      return _positionInCurrentSQL;
   }
}
