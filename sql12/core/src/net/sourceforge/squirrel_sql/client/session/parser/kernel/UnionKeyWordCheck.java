package net.sourceforge.squirrel_sql.client.session.parser.kernel;

public class UnionKeyWordCheck
{
   private boolean _inUnion;

   public boolean previousWasUnionOrUnionAll()
   {
      return _inUnion ;
   }

   public void reset()
   {
      _inUnion = false;
   }

   public void check(String sqlEditorText, int beginPos)
   {
      if(false == _inUnion)
      {
         _inUnion = StatementBeginPredictionUtil.startsWithIgnoreCase(sqlEditorText, beginPos, "UNION");
      }
   }
}
