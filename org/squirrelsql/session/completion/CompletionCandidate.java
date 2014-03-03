package org.squirrelsql.session.completion;

public abstract class CompletionCandidate
{
   public abstract String getReplacement();

   public abstract String getObjectTypeName();

   @Override
   public String toString()
   {
      return getReplacement() + " [" + getObjectTypeName() + "]";
   }
}
