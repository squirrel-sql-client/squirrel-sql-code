package org.squirrelsql.session.completion.joingenerator;

import org.squirrelsql.session.completion.CompletionCandidate;

public class InnerJoinCompletionCandidate extends CompletionCandidate
{
   private String _replacement;

   public InnerJoinCompletionCandidate(String replacement)
   {
      _replacement = replacement;
   }

   public InnerJoinCompletionCandidate()
   {
      this(null);
   }


   @Override
   public String getPopupDisplayString()
   {
      if(null == _replacement)
      {
         return "#i,table1,table2,...,tableN,";
      }

      return _replacement;
   }

   @Override
   public String getReplacement()
   {
      return _replacement;
   }

   @Override
   public String getObjectTypeName()
   {
      if(null == _replacement)
      {
         return "inner join statement generation";
      }
      return "";
   }

   @Override
   public boolean isGeneratedJoin()
   {
      return null != _replacement;
   }
}
