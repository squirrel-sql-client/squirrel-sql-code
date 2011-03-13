package net.sourceforge.squirrel_sql.plugins.graph.sqlgen;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.fw.codereformat.CommentSpec;
import net.sourceforge.squirrel_sql.plugins.graph.TableFramesModel;

public class QueryBuilderSQLGenerator
{
   private ISession _session;

   public QueryBuilderSQLGenerator(ISession session)
   {
      _session = session;
   }

   public String generateSQL(TableFramesModel tableFramesModel)
   {
      FromClauseRes fromClause = new FromClauseGenerator().createFrom(tableFramesModel);

      if(null == fromClause)
      {
         return "";
      }


      SelectClauseRes selectClause = new SelectClauseGenerator().createSelectClause(fromClause);

      if(null == selectClause)
      {
         return null;
      }

      StringBuffer where = new WhereClauseGenerator().createWhereClause(fromClause);

      return format(selectClause.getSelectClause() + " " + fromClause.getFromClause() + "  " + where + " " + selectClause.getGroupByClause()).trim();
   }



   private String format(String sqls)
   {
      CommentSpec[] commentSpecs =
         new CommentSpec[]
            {
               new CommentSpec("/*", "*/"),
               new CommentSpec("--", "\n")
            };

      String statementSep = _session.getQueryTokenizer().getSQLStatementSeparator();

      CodeReformator cr = new CodeReformator(statementSep, commentSpecs);

      sqls = cr.reformat(sqls) + "\n";
      return sqls;
   }
}
