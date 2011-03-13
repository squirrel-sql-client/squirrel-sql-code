package net.sourceforge.squirrel_sql.plugins.graph.sqlgen;

public class SelectClauseRes
{
   private String _select;
   private String _groupBy;

   public SelectClauseRes(StringBuffer select)
   {
      this(select, new StringBuffer(""));
   }

   public SelectClauseRes(StringBuffer select, StringBuffer groupBy)
   {
      _select = select.toString();
      _groupBy = groupBy.toString();
   }


   public String getSelectClause()
   {
      return _select;
   }

   public String getGroupByClause()
   {
      return _groupBy;
   }
}
