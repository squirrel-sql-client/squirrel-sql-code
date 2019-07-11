package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import java.util.ArrayList;
import java.util.List;

public class StatementBoundsPrediction
{

   public static List<StatementBounds> getStatementBoundsList(String text, ParseTerminateRequestCheck check)
   {
      ArrayList<StatementBounds> ret = new ArrayList<>();

      int beg = StatementBeginPrediction.predictNextStatementBegin(text, 0, check);
      int end = StatementBeginPrediction.predictNextStatementBegin(text, beg + 1, check);

      while (beg < end && end-1 < text.length())
      {
         ret.add(new StatementBounds(text.substring(beg, end), beg, end));

         beg = StatementBeginPrediction.predictNextStatementBegin(text, end, check);
         end = StatementBeginPrediction.predictNextStatementBegin(text, beg + 1, check);
      }

      return ret;
   }


   public static void main(String[] args)
   {
      String sql = "Select * from WArt; \nSelect * from wkv   hhfggg  j insert into wbestellung ";

      List<StatementBounds> statementBoundsList = getStatementBoundsList(sql, () ->{});

      for (StatementBounds statementBounds : statementBoundsList)
      {
         System.out.println(statementBounds.getBeginPos() + " - " + statementBounds.getEndPos() + " :: >" + statementBounds.getStatement() + "<");
      }


   }


}
