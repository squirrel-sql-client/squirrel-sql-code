package org.squirrelsql.session.completion.joingenerator;

import org.squirrelsql.services.CollectionUtil;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.completion.CompletionCandidate;
import org.squirrelsql.session.completion.CaretVicinity;

import java.util.ArrayList;
import java.util.List;

public class JoinGeneratorProvider
{
   public static final char GENERATOR_START = '#';

   public static List<CompletionCandidate> getCandidates(Session session, CaretVicinity caretVicinity)
   {
      ArrayList<CompletionCandidate> ret = new ArrayList<>();

      List<CompletionCandidate> completionCandidates;

      completionCandidates = new InnerJoinGenerator(caretVicinity, session).getCompletionCandidates();
      if(CollectionUtil.contains(completionCandidates, c -> c.isGeneratedJoin()))
      {
         return completionCandidates;
      }
      ret.addAll(completionCandidates);

      completionCandidates = new LeftJoinGenerator(caretVicinity, session).getCompletionCandidates();
      if(CollectionUtil.contains(completionCandidates, c -> c.isGeneratedJoin()))
      {
         return completionCandidates;
      }
      ret.addAll(completionCandidates);


      return ret;

   }
}
