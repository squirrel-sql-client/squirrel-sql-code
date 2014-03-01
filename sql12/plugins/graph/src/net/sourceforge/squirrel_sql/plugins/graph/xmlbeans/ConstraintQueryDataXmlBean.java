package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

public class ConstraintQueryDataXmlBean
{
   private int _queryJoinTypeIndex;
   private String _outerTableName;

   public void setQueryJoinTypeIndex(int queryJoinTypeIndex)
   {
      _queryJoinTypeIndex = queryJoinTypeIndex;
   }

   public int getQueryJoinTypeIndex()
   {
      return _queryJoinTypeIndex;
   }

   public void setOuterTableName(String outerTableName)
   {
      _outerTableName = outerTableName;
   }

   public String getOuterTableName()
   {
      return _outerTableName;
   }
}
