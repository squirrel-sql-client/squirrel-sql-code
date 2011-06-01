package net.sourceforge.squirrel_sql.plugins.graph.querybuilder.sqlgen;

import net.sourceforge.squirrel_sql.plugins.graph.AggregateFunctions;
import net.sourceforge.squirrel_sql.plugins.graph.ColumnInfo;
import net.sourceforge.squirrel_sql.plugins.graph.TableFrameController;
import net.sourceforge.squirrel_sql.plugins.graph.querybuilder.SelectCol;
import net.sourceforge.squirrel_sql.plugins.graph.querybuilder.SelectStructure;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectClauseGenerator
{
   public SelectClauseRes createSelectClause(FromClauseRes fromClause, SelectStructure selS)
   {
      boolean colAdded = false;
      boolean hasAggFct = false;
      boolean groupByHasCols = false;

      HashMap<String, String> selectFieldsByQualifiedCol = new HashMap<String, String>();
      HashMap<String, String> groupByFieldsByQualifiedCol = new HashMap<String, String>();
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
                  selectFieldsByQualifiedCol.put(qualifiedCol, qualifiedCol);
                  groupByFieldsByQualifiedCol.put(qualifiedCol, qualifiedCol);
                  groupByHasCols = true;
               }
               else
               {
                  selectFieldsByQualifiedCol.put(qualifiedCol, af.getSQL() + "(" + qualifiedCol + ")");
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

      ArrayList<String> qualifiedColsOrderedAsTheyAppearInSelect = new ArrayList<String>();

      StringBuffer selectClause = new StringBuffer("SELECT ");
      StringBuffer groupByClause = new StringBuffer("GROUP BY ");

      for (SelectCol selectCol : selS.getSelectCols())
      {
         String selectField = selectFieldsByQualifiedCol.remove(selectCol.getQualifiedCol());

         if (null != selectField)
         {
            selectClause.append(selectField).append(",");
         }
         qualifiedColsOrderedAsTheyAppearInSelect.add(selectCol.getQualifiedCol());

         String groupByField = groupByFieldsByQualifiedCol.remove(selectCol.getQualifiedCol());
         if (null != groupByField)
         {
            groupByClause.append(groupByField).append(",");
         }
      }


      if(0 < selectFieldsByQualifiedCol.size())
      {
         throw new IllegalStateException("Not all select fields appeard in SelectStructure");
      }

      if(0 < groupByFieldsByQualifiedCol.size())
      {
         throw new IllegalStateException("Not all group by fields appeard in SelectStructure");
      }

      selectClause.setLength(selectClause.length() - 1); // cut off the last comma.

      if(hasAggFct && groupByHasCols)
      {
         groupByClause.setLength(groupByClause.length() - 1); // cut off the last comma.
         return new SelectClauseRes(selectClause, groupByClause, qualifiedColsOrderedAsTheyAppearInSelect);
      }
      else
      {
         return new SelectClauseRes(selectClause, qualifiedColsOrderedAsTheyAppearInSelect);
      }
   }
}
