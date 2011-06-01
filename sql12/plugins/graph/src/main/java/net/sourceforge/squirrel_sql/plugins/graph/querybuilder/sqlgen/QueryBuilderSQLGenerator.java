package net.sourceforge.squirrel_sql.plugins.graph.querybuilder.sqlgen;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.fw.codereformat.CommentSpec;
import net.sourceforge.squirrel_sql.plugins.graph.TableFramesModel;
import net.sourceforge.squirrel_sql.plugins.graph.querybuilder.OrderCol;
import net.sourceforge.squirrel_sql.plugins.graph.querybuilder.OrderStructure;
import net.sourceforge.squirrel_sql.plugins.graph.querybuilder.SelectStructure;
import net.sourceforge.squirrel_sql.plugins.graph.querybuilder.WhereTreeNodeStructure;

public class QueryBuilderSQLGenerator
{
   private ISession _session;

   public QueryBuilderSQLGenerator(ISession session)
   {
      _session = session;
   }

   public String generateSQL(TableFramesModel tableFramesModel, WhereTreeNodeStructure wts, OrderStructure orderStructure, SelectStructure selS)
   {
      FromClauseRes fromClause = new FromClauseGenerator().createFrom(tableFramesModel);

      if(null == fromClause)
      {
         return "";
      }


      SelectClauseRes selectClause = new SelectClauseGenerator().createSelectClause(fromClause, selS);

      if(null == selectClause)
      {
         return null;
      }

      String rawSql =
               selectClause.getSelectClause() + " " +
               fromClause.getFromClause() + "  " +
               wts.generateWhereClause() + " " +
               selectClause.getGroupByClause() + " " +
               orderStructure.generateOrderBy(selectClause);

      return format(rawSql, _session).trim();
   }



   public static String format(String sqls, ISession session)
   {
      CommentSpec[] commentSpecs =
         new CommentSpec[]
            {
               new CommentSpec("/*", "*/"),
               new CommentSpec("--", "\n")
            };

      String statementSep = session.getQueryTokenizer().getSQLStatementSeparator();

      CodeReformator cr = new CodeReformator(statementSep, commentSpecs);

      sqls = cr.reformat(sqls) + "\n";
      return sqls;
   }
}
