package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

class TableToAddWrapper
{

   private ObjectTreeNode _objectTreeNode;
   private ITableInfo _tableInfo;

   public TableToAddWrapper(ObjectTreeNode objectTreeNode)
   {
      _objectTreeNode = objectTreeNode;
   }

   public TableToAddWrapper(ITableInfo tableInfo)
   {
      _tableInfo = tableInfo;
   }

   public static TableToAddWrapper[] wrap(ObjectTreeNode[] objectTreeNodes)
   {
      TableToAddWrapper[] ret = new TableToAddWrapper[objectTreeNodes.length];

      for (int i = 0; i < objectTreeNodes.length; i++)
      {
         ret[i] = new TableToAddWrapper(objectTreeNodes[i]);
         
      }
      
      return ret;
   }

   public boolean isTable()
   {
      if (null != _objectTreeNode)
      {
         return _objectTreeNode.getDatabaseObjectType() == DatabaseObjectType.TABLE;
      }
      else
      {
         return true;
      }
   }

   public String getCatalogName()
   {
      if (null != _objectTreeNode)
      {
         return _objectTreeNode.getDatabaseObjectInfo().getCatalogName();
      }
      else
      {
         return _tableInfo.getCatalogName();
      }
   }

   public String getSchemaName()
   {
      if (null != _objectTreeNode)
      {
         return _objectTreeNode.getDatabaseObjectInfo().getSchemaName();
      }
      else
      {
         return _tableInfo.getSchemaName();
      }
   }

   public String getSimpleName()
   {
      if (null != _objectTreeNode)
      {
         return _objectTreeNode.getDatabaseObjectInfo().getSimpleName();
      }
      else
      {
         return _tableInfo.getSimpleName();
      }
   }

}
