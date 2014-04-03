package org.squirrelsql.session.completion;


import java.util.HashMap;

public class DuplicateSimpleNamesCheck
{
   private HashMap<String, TableCompletionCandidate> _checkHash = new HashMap<>();

   public void check(TableCompletionCandidate tableCompletionCandidate)
   {
      String simpleName = tableCompletionCandidate.getSimpleName();
      TableCompletionCandidate match = _checkHash.get(simpleName);

      if (null != match)
      {
         match.setShowQualifiedHint(true);
         tableCompletionCandidate.setShowQualifiedHint(true);
      }

      _checkHash.put(simpleName, tableCompletionCandidate);

   }
}
