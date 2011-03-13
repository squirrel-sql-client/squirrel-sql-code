package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ConstraintQueryDataXmlBean;

public class ConstraintQueryData
{
   private QueryJoinType _queryJoinType = QueryJoinType.INNER;
   private String _outerTableName;

   public ConstraintQueryData()
   {
   }

   public ConstraintQueryData(ConstraintQueryDataXmlBean constraintQueryDataXmlBean)
   {
      _queryJoinType = QueryJoinType.getByIndex(constraintQueryDataXmlBean.getQueryJoinTypeIndex());
      _outerTableName = constraintQueryDataXmlBean.getOuterTableName();
   }

   public boolean isInnerJoin()
   {
      return _queryJoinType == QueryJoinType.INNER;
   }

   public boolean isOuterJoinFor(String tableName)
   {
      return _queryJoinType == QueryJoinType.OUTER && tableName.equalsIgnoreCase(_outerTableName);
   }


   public boolean isNoJoin()
   {
      return _queryJoinType == QueryJoinType.NONE;
   }

   public void setInnerJoin()
   {
      _queryJoinType = QueryJoinType.INNER;
      _outerTableName = null;
   }

   public void setOuterJoin(String outerTableName)
   {
      _queryJoinType = QueryJoinType.OUTER;
      _outerTableName = outerTableName;
   }

   public void setNoJoin()
   {
      _queryJoinType = QueryJoinType.NONE;
      _outerTableName = null;
   }

   public ConstraintQueryDataXmlBean getXmlBean()
   {
      ConstraintQueryDataXmlBean ret = new ConstraintQueryDataXmlBean();
      ret.setQueryJoinTypeIndex(_queryJoinType.getIndex());
      ret.setOuterTableName(_outerTableName);
      return ret;
   }
}
