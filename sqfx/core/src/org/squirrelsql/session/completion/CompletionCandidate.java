package org.squirrelsql.session.completion;


public abstract class CompletionCandidate
{
   public abstract String getReplacement();

   public abstract String getObjectTypeName();

   public String getPopupDisplayString()
   {
      return getReplacement();
   }

   @Override
   public String toString()
   {
      return getPopupDisplayString() + " [" + getObjectTypeName() + "]";
   }
}
