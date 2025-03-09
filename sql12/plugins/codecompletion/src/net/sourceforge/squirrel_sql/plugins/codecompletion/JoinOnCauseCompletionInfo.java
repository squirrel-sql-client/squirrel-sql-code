package net.sourceforge.squirrel_sql.plugins.codecompletion;

public class JoinOnCauseCompletionInfo extends CodeCompletionInfo
{
   private final String _completionOnClause;

   public JoinOnCauseCompletionInfo(String completionOnClause)
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
