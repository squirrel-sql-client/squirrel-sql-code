package net.sourceforge.squirrel_sql.plugins.codecompletion;

import org.apache.xml.utils.XMLString;


public class CodeCompletionCandidates
{
   private CodeCompletionInfo[] _candidates;
   private int _replacementStart;
   private String _stringToReplace;

   public CodeCompletionCandidates(CodeCompletionInfo[] candidates, int replacementStart, String stringToReplace)
   {
      _candidates = candidates;
      _replacementStart = replacementStart;
      _stringToReplace = stringToReplace;
   }

   public CodeCompletionInfo[] getCandidates()
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
