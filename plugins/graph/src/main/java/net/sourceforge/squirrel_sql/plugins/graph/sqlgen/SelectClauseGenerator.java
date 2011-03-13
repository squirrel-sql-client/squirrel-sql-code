package net.sourceforge.squirrel_sql.plugins.graph.sqlgen;

import net.sourceforge.squirrel_sql.plugins.graph.AggregateFunctions;
import net.sourceforge.squirrel_sql.plugins.graph.ColumnInfo;
import net.sourceforge.squirrel_sql.plugins.graph.TableFrameController;

public class SelectClauseGenerator
{
   public SelectClauseRes createSelectClause(FromClauseRes fromClause)
   {
      StringBuffer select = new StringBuffer("SELECT ");
      StringBuffer groupBy = new StringBuffer("GROUP BY ");

      boolean colAdded = false;
      boolean hasAggFct = false;

      for (TableFrameController tfc : fromClause.getTables())
      {
         for (ColumnInfo columnInfo : tfc.getColumnInfos())
         {
            if (columnInfo.getQueryData().isInSelectClause())
            {
               AggregateFunctions af = columnInfo.getQueryData().getAggregateFunction();

               if (AggregateFunctions.NONE == af)
               {
                  String s = tfc.getTableInfo().getSimpleName() + "." + columnInfo.getColumnName() + ",";
                  select.append(s);
                  groupBy.append(s);
               }
               else
               {
                  select.append(af.getSQL() + "(" + tfc.getTableInfo().getSimpleName() + "." + columnInfo.getColumnName() + "),");
                  hasAggFct = true;
               }
               colAdded = true;
            }
         }
      }

      if(false == colAdded)
      {
         return null;
      }

      select.setLength(select.length() - 1); // cut off the last comma.

      if(hasAggFct)
      {
         groupBy.setLength(groupBy.length() - 1); // cut off the last comma.
         return new SelectClauseRes(select, groupBy);
      }
      else
      {
         return new SelectClauseRes(select);
      }
   }
}
