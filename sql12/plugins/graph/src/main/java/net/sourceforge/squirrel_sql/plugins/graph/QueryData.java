package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.plugins.graph.querybuilder.QueryFilterOperators;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.QueryDataXmlBean;

public class QueryData
{
   private String _filterValue;
   private QueryFilterOperators _operator = QueryFilterOperators.EQUAL;
   private AggregateFunctions _aggregateFunction = AggregateFunctions.NONE;
   private boolean _inSelectClause;
   private Sorting _sorting = Sorting.NONE;

   public QueryData(QueryDataXmlBean queryDataXmlBean)
   {
      if (null != queryDataXmlBean)
      {
         _inSelectClause = queryDataXmlBean.isInSelectClause();
         _filterValue = queryDataXmlBean.getFilterValue();
         _operator = QueryFilterOperators.getForIndex(queryDataXmlBean.getOperatorIndex());
         _aggregateFunction = AggregateFunctions.getForIndex(queryDataXmlBean.getAggregateFunctionIndex());
         _sorting = Sorting.getByIndex(queryDataXmlBean.getSortingIndex());
      }
   }

   public QueryData()
   {
   }

   public void setFilterValue(String filterValue)
   {
      _filterValue = filterValue;
   }

   public String getFilterValue()
   {
      return _filterValue;
   }

   public void setOperator(QueryFilterOperators operator)
   {
      _operator = operator;
   }

   public QueryFilterOperators getOperator()
   {
      return _operator;
   }

   public boolean isFiltered()
   {
      if(QueryFilterOperators.isNoArgOperator(_operator))
      {
         return true;
      }

      if(null == _filterValue || 0 == _filterValue.trim().length())
      {
         return false;
      }
      else
      {
         return true;
      }
   }

   public void setAggregateFunction(AggregateFunctions aggregateFunction)
   {
      _aggregateFunction = aggregateFunction;
   }

   public AggregateFunctions getAggregateFunction()
   {
      return _aggregateFunction;
   }

   public boolean isInSelectClause()
   {
      return _inSelectClause;
   }

   public void setInSelectClause(boolean b)
   {
      _inSelectClause = b;

      if(false == b)
      {
         _aggregateFunction = AggregateFunctions.NONE;
      }
   }

   public void clearFilter()
   {
      _filterValue = null;
      _operator = QueryFilterOperators.EQUAL;
   }

   public Sorting getSorting()
   {
      return _sorting;
   }

   public void setSorting(Sorting sorting)
   {
      _sorting = sorting;
   }

   public boolean isSorted()
   {
      return _sorting != Sorting.NONE;
   }

   public boolean isSortedAsc()
   {
      return _sorting == Sorting.ASC;
   }
}
