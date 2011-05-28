package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.plugins.graph.querybuilder.sqlgen.SelectClauseRes;

public class OrderStructure
{
   private OrderCol[] _orderCols;

   public OrderStructure(OrderCol[] orderCols)
   {
      _orderCols = orderCols;
   }

   public String generateOrderBy(SelectClauseRes selectClause)
   {
      String ret = "";

      for (int i = 0; i < _orderCols.length; i++)
      {
         if (0 == i)
         {
            ret += "ORDER BY " + getOrderExpression(_orderCols[i], selectClause);
         }
         else
         {
            ret += ", " + getOrderExpression(_orderCols[i], selectClause);
         }
      }

      return ret;

   }

   private String getOrderExpression(OrderCol orderCol, SelectClauseRes selectClause)
   {
      String direction = orderCol.isAscending() ? " ASC" : "";

      if (orderCol.isAggregated())
      {
         // For aggregate functions we append the position of the column in the where clause.
         // This should word acording to C. J. Date The SQL standard.
         return selectClause.getSQLSelectPositionForCol(orderCol.getQualifiedCol()) + direction;
      }
      else
      {
         return orderCol.getQualifiedCol() + direction;
      }
   }
}
