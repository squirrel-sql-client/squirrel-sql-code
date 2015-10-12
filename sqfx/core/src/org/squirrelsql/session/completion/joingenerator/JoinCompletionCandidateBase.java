package org.squirrelsql.session.completion.joingenerator;

import org.squirrelsql.session.completion.CompletionCandidate;

public abstract class JoinCompletionCandidateBase extends CompletionCandidate
{
   private String _replacement;

   public JoinCompletionCandidateBase(String replacement)
   {
      _replacement = replacement;
   }


   @Override
   public String getPopupDisplayString()
   {
      if(null == _replacement)
      {
         return getNonGeneratedPopupDisplay();
      }

      return _replacement.replace('\n', ' ');
   }

   @Override
   public String getReplacement()
   {
      if(null == _replacement)
      {
         return getNonGeneratedPopupDisplay();
      }

      return _replacement;
   }

   @Override
   public String getObjectTypeName()
   {
      if(null == _replacement)
      {
         return getNonGeneratedTypeName();
      }
      return "";
   }


   @Override
   public boolean isGeneratedJoin()
   {
      return null != _replacement;
   }


   protected abstract String getNonGeneratedTypeName();

   protected abstract String getNonGeneratedPopupDisplay();
}
