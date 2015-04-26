package org.squirrelsql.session.objecttree;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import org.squirrelsql.session.ProcedureInfo;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.UDTInfo;

import java.util.Comparator;
import java.util.List;


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
      List<TreeItem<ObjectTreeNode>> udtTypeItems = ObjectTreeUtil.findTreeItems(objectsTree, ObjectTreeNodeTypeKey.UDT_TYPE_KEY);

      for (TreeItem<ObjectTreeNode> udtTypeItem : udtTypeItems)
      {
         List<UDTInfo>  udtInfos = session.getSchemaCacheValue().get().getUDTInfosExact(udtTypeItem.getValue().getCatalog(), udtTypeItem.getValue().getSchema());

         // The schema cache gets changed here :-/
         udtInfos.sort((ui1, ui2) -> ui1.getName().compareTo(ui2.getName()));

         for (UDTInfo udtInfo : udtInfos)
         {
            udtTypeItem.getChildren().add(ObjectTreeItemFactory.createUDT(udtInfo));
         }
      }
   }

   private static void doProcedures(TreeView<ObjectTreeNode> objectsTree, Session session)
   {
      List<TreeItem<ObjectTreeNode>> procedureTypeItems = ObjectTreeUtil.findTreeItems(objectsTree, ObjectTreeNodeTypeKey.PROCEDURE_TYPE_KEY);

      for (TreeItem<ObjectTreeNode> procedureTypeItem : procedureTypeItems)
      {
         List<ProcedureInfo>  procedureInfos = session.getSchemaCacheValue().get().getProcedureInfosExact(procedureTypeItem.getValue().getCatalog(), procedureTypeItem.getValue().getSchema());

         // The schema cache gets changed here :-/
         procedureInfos.sort((pi1, pi2) -> pi1.getName().compareTo(pi2.getName()));

         for (ProcedureInfo procedureInfo : procedureInfos)
         {
            procedureTypeItem.getChildren().add(ObjectTreeItemFactory.createProcedure(procedureInfo));
         }
      }
   }

   private static void doTables(TreeView<ObjectTreeNode> objectsTree, Session session)
   {
      List<TreeItem<ObjectTreeNode>> tableTypeItems = ObjectTreeUtil.findTreeItems(objectsTree, ObjectTreeNodeTypeKey.TABLE_TYPE_TYPE_KEY);

      for (TreeItem<ObjectTreeNode> tableTypeItem : tableTypeItems)
      {
         ObjectTreeNode value = tableTypeItem.getValue();
         List<TableInfo> tableInfos = session.getSchemaCacheValue().get().getTableInfosExact(value.getCatalog(), value.getSchema(), value.getTableType());

         // The schema cache gets changed here :-/
         tableInfos.sort((ti1, ti2) -> ti1.getName().compareTo(ti2.getName()));


         for (TableInfo tableInfo : tableInfos)
         {
            tableTypeItem.getChildren().add(ObjectTreeItemFactory.createTable(tableInfo));
         }
      }
   }
}
