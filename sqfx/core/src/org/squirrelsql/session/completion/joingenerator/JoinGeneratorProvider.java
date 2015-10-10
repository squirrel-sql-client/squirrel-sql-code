package org.squirrelsql.session.completion.joingenerator;

import org.squirrelsql.session.completion.CompletionCandidate;
import org.squirrelsql.session.completion.CaretVicinity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JoinGeneratorProvider
{

   public static List<CompletionCandidate> getCandidates(CaretVicinity caretVicinity)
   {
      InnerJoinGenerator innerJoinGenerator = new InnerJoinGenerator(caretVicinity);

      return innerJoinGenerator.getCompletionCandidates();

   }
}
