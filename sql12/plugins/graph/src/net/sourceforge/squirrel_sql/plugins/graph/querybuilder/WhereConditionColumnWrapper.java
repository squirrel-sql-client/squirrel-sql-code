package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.plugins.graph.ColumnInfo;
import net.sourceforge.squirrel_sql.plugins.graph.TableFrameController;

public class WhereConditionColumnWrapper
{
   private TableFrameController _tfc;
   private ColumnInfo _ci;
   private String _definition;
   private String _param;

   public WhereConditionColumnWrapper(TableFrameController tfc, ColumnInfo ci)
   {
      _tfc = tfc;
      _ci = ci;

      _param = "";

      if (false == ci.getQueryData().getOperator().isNoArgOperator())
      {
         _param = " " + ci.getQueryData().getFilterValue();
      }

      _definition =
            _tfc.getTableInfo().getQualifiedName() + "." +
                  ci.getColumnName() + " " +
                  ci.getQueryData().getOperator().getSQL() + _param;

   }

   @Override
   public String toString()
   {
      return getDisplay();
   }

   public String getDisplay()
   {
      return _tfc.getDisplayName() + "." +
            _ci.getColumnName() + " " +
            _ci.getQueryData().getOperator().getSQL() + _param;
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
