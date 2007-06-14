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

   /**
    * This ctor can be used when the completion uses its own filter text field.
    * @param candidates
    */
   public CompletionCandidates(CompletionInfo[] candidates)
   {
      _candidates = candidates;
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

   public String getAllCandidatesPrefix(boolean caseSensitive)
   {
      if(0 == _candidates.length)
      {
         return "";
      }


      String prefix = null;
      for (CompletionInfo _candidate : _candidates)
      {
         String completionString = _candidate.getCompletionString();
         if (null == prefix)
         {
            prefix = completionString;
         }
         else
         {
            int ix;
            int minLen = Math.min(prefix.length(), completionString.length());

            prefix = prefix.substring(0, minLen);

            for(ix =0; ix < minLen; ++ix)
            {
               if(getCharAt(prefix, ix, caseSensitive) != getCharAt(completionString, ix, caseSensitive))
               {
                  prefix = prefix.substring(0, ix);
                  break;
               }
            }
         }
      }

      return prefix;
   }

   private char getCharAt(String prefix, int ix, boolean caseSensitive)
   {
      char c = prefix.charAt(ix);
      return caseSensitive ? c : Character.toUpperCase(c);
   }
}
