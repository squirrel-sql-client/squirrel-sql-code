package net.sourceforge.squirrel_sql.client.session.parser.kernel;

public class StatementBounds
{
   private final String _statement;
   private final int _beginPos;
   private final int _endPos;

   public StatementBounds(String statement, int beginPos, int endPos)
   {
      _statement = statement;
      _beginPos = beginPos;
      _endPos = endPos;
   }


   public String getStatement()
   {
      return _statement;
   }

   public int getBeginPos()
   {
      return _beginPos;
   }

   public int getEndPos()
   {
      return _endPos;
   }
}
