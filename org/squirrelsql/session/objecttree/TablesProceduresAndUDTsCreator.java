package org.squirrelsql.session.objecttree;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.squirrelsql.session.*;

import java.util.ArrayList;

public class TablesProceduresAndUDTsCreator
{
   public static void createNodes(TreeView<ObjectTreeNode> objectsTree, Session session)
   {
      doTables(objectsTree, session);
      doProcedures(objectsTree, session);
      doTypes(objectsTree, session);


   }

   private static void doTypes(TreeView<ObjectTreeNode> objectsTree, Session session)
   {
      ArrayList<TreeItem<ObjectTreeNode>> udtTypeItems = ObjectTreeUtil.findTreeItems(objectsTree, ObjectTreeNodeTypeKey.UDT_TYPE_KEY);

      for (TreeItem<ObjectTreeNode> udtTypeItem : udtTypeItems)
      {
         ArrayList<UDTInfo>  udtInfos = session.getSchemaCache().getUDTInfos(udtTypeItem.getValue().getCatalog(), udtTypeItem.getValue().getSchema());

         for (UDTInfo udtInfo : udtInfos)
         {
            udtTypeItem.getChildren().add(ObjectTreeItemFactory.createUDT(udtInfo));
         }
      }
   }

   private static void doProcedures(TreeView<ObjectTreeNode> objectsTree, Session session)
   {
      ArrayList<TreeItem<ObjectTreeNode>> procedureTypeItems = ObjectTreeUtil.findTreeItems(objectsTree, ObjectTreeNodeTypeKey.PROCEDURE_TYPE_KEY);

      for (TreeItem<ObjectTreeNode> procedureTypeItem : procedureTypeItems)
      {
         ArrayList<ProcedureInfo>  procedureInfos = session.getSchemaCache().getProcedureInfos(procedureTypeItem.getValue().getCatalog(), procedureTypeItem.getValue().getSchema());

         for (ProcedureInfo procedureInfo : procedureInfos)
         {
            procedureTypeItem.getChildren().add(ObjectTreeItemFactory.createProcedure(procedureInfo));
         }
      }
   }

   private static void doTables(TreeView<ObjectTreeNode> objectsTree, Session session)
   {
      ArrayList<TreeItem<ObjectTreeNode>> tableTypeItems = ObjectTreeUtil.findTreeItems(objectsTree, ObjectTreeNodeTypeKey.TABLE_TYPE_TYPE_KEY);

      for (TreeItem<ObjectTreeNode> tableTypeItem : tableTypeItems)
      {
         TableTypeObjectTreeNode value = tableTypeItem.getValue();
         ArrayList<TableInfo> tableInfos = session.getSchemaCache().getTableInfos(value.getCatalog(), value.getSchema(), value.getTableType());

         for (TableInfo tableInfo : tableInfos)
         {
            tableTypeItem.getChildren().add(ObjectTreeItemFactory.createTable(tableInfo));
         }
      }
   }
}
