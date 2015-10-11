package org.squirrelsql.session.completion;


import org.squirrelsql.services.Utils;

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
      if (Utils.isEmptyString(getObjectTypeName()))
      {
         return getPopupDisplayString();
      }

      return getPopupDisplayString() + " [" + getObjectTypeName() + "]";

   }

   public boolean isGeneratedJoin()
   {
      return false;
   }
}
