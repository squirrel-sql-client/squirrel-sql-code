package org.squirrelsql.session.completion;

public abstract class CompletionCandidate
{
   public abstract String getReplacement();

   @Override
   public String toString()
   {
      return getReplacement();
   }
}
