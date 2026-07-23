package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import java.util.ArrayList;
import java.util.List;

public class ParenthesedSelectParseResult
{
   private List<ParenthesedSelectInfo> _parenthesedSelectInfos = new ArrayList<>();

   public ParenthesedSelectParseResult()
   {
   }

   public List<ParenthesedSelectInfo> getParenthesedSelectInfos()
   {
      return _parenthesedSelectInfos;
   }

   public void addParenthesedSelectInfos(List<ParenthesedSelectInfo> parenthesedSelectInfos)
   {
      _parenthesedSelectInfos.addAll(parenthesedSelectInfos);
   }
}
