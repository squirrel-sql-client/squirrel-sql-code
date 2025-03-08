package net.sourceforge.squirrel_sql.plugins.codecompletion;

public class JoinOnCompletionInfo extends CodeCompletionInfo
{
   private final String _completionOnClause;

   public JoinOnCompletionInfo(String completionOnClause)
   {
      _completionOnClause = completionOnClause;
   }

   @Override
   public String getCompareString()
   {
      // Blank or JOIN-Keyword depending on text till caret
      return "";
   }

   @Override
   public String getCompletionString()
   {
      return _completionOnClause;
   }
}
