package org.squirrelsql.session.completion.joingenerator;

public class LeftJoinCompletionCandidate extends JoinCompletionCandidateBase
{
   public LeftJoinCompletionCandidate(String replacement)
   {
      super(replacement);
   }

   protected String getNonGeneratedTypeName()
   {
      return "left join statement generation";
   }

   protected String getNonGeneratedPopupDisplay()
   {
      return "#l,table1,table2,...,tableN,";
   }
}
