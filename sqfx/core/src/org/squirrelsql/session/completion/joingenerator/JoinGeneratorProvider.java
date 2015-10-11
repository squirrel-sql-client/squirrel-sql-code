package org.squirrelsql.session.completion.joingenerator;

import org.squirrelsql.session.Session;
import org.squirrelsql.session.completion.CompletionCandidate;
import org.squirrelsql.session.completion.CaretVicinity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JoinGeneratorProvider
{
   public static final char FUNCTION_START = '#';

   public static List<CompletionCandidate> getCandidates(Session session, CaretVicinity caretVicinity)
   {
      InnerJoinGenerator innerJoinGenerator = new InnerJoinGenerator(caretVicinity, session);

      return innerJoinGenerator.getCompletionCandidates();

   }
}
