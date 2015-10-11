package org.squirrelsql.session.completion.joingenerator;

import org.squirrelsql.session.completion.CompletionCandidate;

public class InnerJoinCompletionCandidate extends CompletionCandidate
{
   public static final String INFO_STRING = "#i,table1,table2,...,tableN,";
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
         return INFO_STRING;
      }

      return _replacement.replace('\n', ' ');
   }

   @Override
   public String getReplacement()
   {
      if(null == _replacement)
      {
         return INFO_STRING;
      }

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
