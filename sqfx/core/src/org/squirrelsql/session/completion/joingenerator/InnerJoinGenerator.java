package org.squirrelsql.session.completion.joingenerator;

import org.squirrelsql.session.Session;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.completion.CaretVicinity;

public class InnerJoinGenerator extends JoinGeneratorBase
{
   public InnerJoinGenerator(CaretVicinity caretVicinity, Session session)
   {
      super(caretVicinity, session);
   }

   protected String createJoinClause(TableInfo table, String fkColumnName)
   {
      return "INNER JOIN";
   }

   protected String getGeneratorName()
   {
      return JoinGeneratorProvider.GENERATOR_START + "i";
   }


}
