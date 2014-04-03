package org.squirrelsql.session.completion;

import org.squirrelsql.services.Utils;

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
