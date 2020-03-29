package net.sourceforge.squirrel_sql.fw.sql.commentandliteral;

public class NextPositionResult
{
   private NextPositionAction _nextPositionAction;
   private int _nextPosition;

   public NextPositionResult(int posInScript)
   {
      _nextPosition = posInScript + 1;
   }


   public NextPositionAction getNextPositionAction()
   {
      return _nextPositionAction;
   }

   public int getNextPosition()
   {
      return _nextPosition;
   }

   public NextPositionResult setNextPositionAction(NextPositionAction nextPositionAction)
   {
      _nextPositionAction = nextPositionAction;
      return this;
   }

   public NextPositionResult setNextPosition(int nextPosition)
   {
      _nextPosition = nextPosition;
      return this;
   }
}
