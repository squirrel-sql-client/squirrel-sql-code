package org.squirrelsql.session.completion;

/**
 * Created by gerd on 16.02.14.
 */
public class FunctionCompletionCandidate extends CompletionCandidate
{
   private String _function;

   public FunctionCompletionCandidate(String function)
   {
      _function = function;
   }

   @Override
   public String getReplacement()
   {
      return _function;
   }
}
