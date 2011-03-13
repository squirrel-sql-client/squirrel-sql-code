package net.sourceforge.squirrel_sql.plugins.graph.sqlgen;

import net.sourceforge.squirrel_sql.plugins.graph.ColumnInfo;
import net.sourceforge.squirrel_sql.plugins.graph.QueryData;
import net.sourceforge.squirrel_sql.plugins.graph.QueryFilterOperators;
import net.sourceforge.squirrel_sql.plugins.graph.TableFrameController;

public class WhereClauseGenerator
{
   public static final String AND = " AND ";

   StringBuffer createWhereClause(FromClauseRes fromClause)
   {
      StringBuffer where = new StringBuffer("WHERE ");


      boolean hasFilter = false;
      for (TableFrameController tfc : fromClause.getTables())
      {
         for (ColumnInfo columnInfo : tfc.getColumnInfos())
         {
            QueryData qd = columnInfo.getQueryData();
            if(qd.isFiltered())
            {
               hasFilter = true;

               if (QueryFilterOperators.isNoArgOperator(qd.getOperator()))
               {
                  where.append(tfc.getTableInfo().getSimpleName() + "." + columnInfo.getName() + " " + qd.getOperator().getSQL());
                  where.append(AND);
               }
               else
               {
                  where.append(tfc.getTableInfo().getSimpleName() + "." + columnInfo.getName() + " " + qd.getOperator().getSQL() + " " + qd.getFilterValue());
                  where.append(AND);
               }
            }
         }
      }

      if(hasFilter)
      {
         where.setLength(where.length() - AND.length());
         return where;
      }
      else
      {
         return new StringBuffer("");
      }
   }

}
