package net.sourceforge.squirrel_sql.fw.completion;


public class CompletionCandidates
{
   private CompletionInfo[] _candidates;
   private int _replacementStart;
   private String _stringToReplace;

   public CompletionCandidates(CompletionInfo[] candidates, int replacementStart, String stringToReplace)
   {
      _candidates = candidates;
      _replacementStart = replacementStart;
      _stringToReplace = stringToReplace;
   }

   public CompletionInfo[] getCandidates()
   {
      return _candidates;
   }

   public int getReplacementStart()
   {
      return _replacementStart;
   }

   public String getStringToReplace()
   {
      return _stringToReplace;
   }
}
