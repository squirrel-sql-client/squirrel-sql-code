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
      // Will be added to the completion list whenever ON-clauses make sense,
      // which is after the table join or table alias in a JOIN-clause.
      // That's why there is no compare string.
      return "";
   }

   @Override
   public String getCompletionString()
   {
      return _completionOnClause;
   }

   @Override
   public String toString()
   {
      return _completionOnClause + " (ON clause of JOIN)";
   }
}
