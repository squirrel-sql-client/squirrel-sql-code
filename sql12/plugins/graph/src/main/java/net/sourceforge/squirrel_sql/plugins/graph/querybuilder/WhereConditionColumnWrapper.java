package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.plugins.graph.ColumnInfo;

public class WhereConditionColumnWrapper
{
   private ITableInfo _tableInfo;
   private ColumnInfo _ci;
   private String _definition;

   public WhereConditionColumnWrapper(ITableInfo tableInfo, ColumnInfo ci)
   {
      _tableInfo = tableInfo;
      _ci = ci;

      String param = "";

      if (false == ci.getQueryData().getOperator().isNoArgOperator())
      {
         param = " " + ci.getQueryData().getFilterValue();
      }


      _definition =
            _tableInfo.getSimpleName() + "." +
                  ci.getColumnName() + " " +
                  ci.getQueryData().getOperator().getSQL() + param;
   }

   @Override
   public String toString()
   {
      return _definition;
   }


   @Override
   public int hashCode()
   {
      return _definition.toUpperCase().hashCode();
   }

   @Override
   public boolean equals(Object obj)
   {
      if(false == obj instanceof WhereConditionColumnWrapper)
      {
         return false;
      }

      WhereConditionColumnWrapper other = (WhereConditionColumnWrapper) obj;

      return _definition.equalsIgnoreCase(other._definition);
   }


   public String getDefinition()
   {
      return _definition;
   }
}
