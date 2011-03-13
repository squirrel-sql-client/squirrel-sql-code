package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public enum QueryFilterOperators
{

   EQUAL(0, "="),
   LIKE(1, "LIKE"),
   IN(2, "IN"),
   IS_NULL(3, "IS NULL"),
   LESS(4, "<"),
   LESS_EQUAL(5, "<="),
   MORE(6, ">"),
   MORE_EQUAL(7, ">="),
   NOT_EQUAL(8, "<>"),
   NOT_IN(9, "NOT IN"),
   IS_NOT_NULL(10, "IS NOT NULL");

   private int _index;
   private String _toString;

   QueryFilterOperators(int index, String toString)
   {
      _index = index;
      _toString = toString;
   }

   public int getIndex()
   {
      return _index;
   }

   @Override
   public String toString()
   {
      return _toString;
   }

   public static boolean isNoArgOperator(QueryFilterOperators operator)
   {
      return QueryFilterOperators.IS_NOT_NULL == operator || QueryFilterOperators.IS_NULL == operator;
   }

   public static QueryFilterOperators getForIndex(int operatorIndex)
   {
      for (QueryFilterOperators queryFilterOperator : values())
      {
         if(queryFilterOperator._index == operatorIndex)
         {
            return queryFilterOperator;
         }
      }

      throw new IllegalArgumentException("Unkonwn operator index " + operatorIndex);
   }

   public String getSQL()
   {
      return _toString;
   }
}
