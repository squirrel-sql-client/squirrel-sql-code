package org.squirrelsql.session.completion.joingenerator;

public class InnerJoinCompletionCandidate extends JoinCompletionCandidateBase
{
   public InnerJoinCompletionCandidate(String replacement)
   {
      super(replacement);
   }

   public InnerJoinCompletionCandidate()
   {
      super(null);
   }

   protected String getNonGeneratedTypeName()
   {
      return "inner join statement generation";
   }

   protected String getNonGeneratedPopupDisplay()
   {
      return "#i,table1,table2,...,tableN,";
   }
}
