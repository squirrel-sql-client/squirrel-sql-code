package org.squirrelsql.session.completion.joingenerator;

public class SmartJoinCompletionCandidate extends JoinCompletionCandidateBase
{
   public SmartJoinCompletionCandidate(String replacement)
   {
      super(replacement);
   }

   protected String getNonGeneratedTypeName()
   {
      return "left/inner join statement generation";
   }

   protected String getNonGeneratedPopupDisplay()
   {
      return "#j,table1,table2,...,tableN,";
   }
}
