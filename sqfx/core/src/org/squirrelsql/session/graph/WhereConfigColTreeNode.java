package org.squirrelsql.session.graph;

import org.squirrelsql.services.dndpositionmarker.DropIntoInfo;

import java.util.UUID;

public class WhereConfigColTreeNode implements DropIntoInfo
{
   private ColumnPersistence _columnPersistence;
   private WhereConfigColEnum _whereConfigColEnum;
   private String _whereConfigColEnumId;


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
      _whereConfigColEnumId = UUID.randomUUID().toString();
   }

   @Override
   public String toString()
   {
      return _describe(false);
   }


   public WhereConfigColEnum getWhereConfigColEnum()
   {
      return _whereConfigColEnum;
   }

   @Override
   public boolean allowsDropInto()
   {
      return isFolder();
   }

   public String getId()
   {
      return _describe(true);
   }

   private String _describe(boolean asId)
   {
      if (null != _columnPersistence)
      {
         String filterDesc = ColumnPersistenceId.createId(_columnPersistence);

         if (false == asId)
         {
            FilterPersistence filterPersistence = _columnPersistence.getColumnConfigurationPersistence().getFilterPersistence();

            filterDesc +=  " " + Operator.valueOf(filterPersistence.getOperatorAsString()).toString() + " " + filterPersistence.getFilter();
         }

         return filterDesc;
      }
      else if (null != _whereConfigColEnum)
      {
         String ret = _whereConfigColEnum.name();

         if(asId)
         {
            ret += "_" + _whereConfigColEnumId;
         }

         return ret;
      }
      else
      {
         return "WHERE (like AND)";
      }
   }

   public boolean isRoot()
   {
      return null == _whereConfigColEnum && null == _columnPersistence;
   }

   public boolean isFolder()
   {
      return null != _whereConfigColEnum || isRoot();
   }

   public boolean isFilter()
   {
      return null != _columnPersistence;
   }

}
