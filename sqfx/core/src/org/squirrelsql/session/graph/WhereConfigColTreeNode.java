package org.squirrelsql.session.graph;

public class WhereConfigColTreeNode
{
   private ColumnPersistence _columnPersistence;
   private WhereConfigColEnum _whereConfigColEnum;

   public WhereConfigColTreeNode()
   {
   }

   public WhereConfigColTreeNode(ColumnPersistence columnPersistence)
   {
      _columnPersistence = columnPersistence;
   }

   public WhereConfigColTreeNode(WhereConfigColEnum whereConfigColEnum)
   {
      _whereConfigColEnum = whereConfigColEnum;
   }

   @Override
   public String toString()
   {
      if (null != _columnPersistence)
      {
         FilterPersistence filterPersistence = _columnPersistence.getColumnConfigurationPersistence().getFilterPersistence();
         String filterDesc = _columnPersistence.getTableName() + "." + _columnPersistence.getColName() + " " + Operator.valueOf(filterPersistence.getOperatorAsString()).toString() + " " + filterPersistence.getFilter();
         return filterDesc;
      }
      if (null != _whereConfigColEnum)
      {
         return _whereConfigColEnum.name();
      }
      else
      {
         return "WHERE (like AND)";
      }
   }

   public boolean isFolder()
   {
      return null != _whereConfigColEnum;
   }
}
