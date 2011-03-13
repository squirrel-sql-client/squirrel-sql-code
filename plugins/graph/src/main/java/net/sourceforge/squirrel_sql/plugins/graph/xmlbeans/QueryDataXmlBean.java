package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

public class QueryDataXmlBean
{
   private String _filterValue;
   private int _aggregateFunctionIndex;
   private int _operatorIndex;
   private boolean _inSelectClause;

   public void setOperatorIndex(int operatorIndex)
   {
      _operatorIndex = operatorIndex;
   }

   public void setAggregateFunctionIndex(int aggregateFunctionIndex)
   {
      _aggregateFunctionIndex = aggregateFunctionIndex;
   }

   public void setFilterValue(String filterValue)
   {
      _filterValue = filterValue;
   }

   public String getFilterValue()
   {
      return _filterValue;
   }

   public int getAggregateFunctionIndex()
   {
      return _aggregateFunctionIndex;
   }

   public int getOperatorIndex()
   {
      return _operatorIndex;
   }

   public void setInSelectClause(boolean inSelectClause)
   {
      _inSelectClause = inSelectClause;
   }

   public boolean isInSelectClause()
   {
      return _inSelectClause;
   }
}
