package net.sourceforge.squirrel_sql.plugins.graph.querybuilder.sqlgen;

import net.sourceforge.squirrel_sql.plugins.graph.AggregateFunctions;
import net.sourceforge.squirrel_sql.plugins.graph.ColumnInfo;
import net.sourceforge.squirrel_sql.plugins.graph.TableFrameController;

import java.util.ArrayList;

public class SelectClauseGenerator
{
   public SelectClauseRes createSelectClause(FromClauseRes fromClause)
   {
      StringBuffer select = new StringBuffer("SELECT ");
      StringBuffer groupBy = new StringBuffer("GROUP BY ");

      boolean colAdded = false;
      boolean hasAggFct = false;
      boolean groupByHasCols = false;

      ArrayList<String> qualifiedColsOrderedAsTheyAppearInSelect = new ArrayList<String>();

      for (TableFrameController tfc : fromClause.getTables())
      {
         for (ColumnInfo columnInfo : tfc.getColumnInfos())
         {
            if (columnInfo.getQueryData().isInSelectClause())
            {
               AggregateFunctions af = columnInfo.getQueryData().getAggregateFunction();

               String qualifiedCol = tfc.getTableInfo().getSimpleName() + "." + columnInfo.getColumnName();

               if (AggregateFunctions.NONE == af)
               {
                  String s = qualifiedCol + ",";
                  select.append(s);
                  groupBy.append(s);
                  groupByHasCols = true;
               }
               else
               {
                  select.append(af.getSQL() + "(" + qualifiedCol + "),");
                  hasAggFct = true;
               }
               qualifiedColsOrderedAsTheyAppearInSelect.add(qualifiedCol);
               colAdded = true;
            }
         }
      }

      if(false == colAdded)
      {
         return null;
      }

      select.setLength(select.length() - 1); // cut off the last comma.

      if(hasAggFct && groupByHasCols)
      {
         groupBy.setLength(groupBy.length() - 1); // cut off the last comma.
         return new SelectClauseRes(select, groupBy, qualifiedColsOrderedAsTheyAppearInSelect);
      }
      else
      {
         return new SelectClauseRes(select, qualifiedColsOrderedAsTheyAppearInSelect);
      }
   }
}
