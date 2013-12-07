package org.squirrelsql.session.objecttree;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.DBSchema;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.schemainfo.*;

import java.sql.SQLException;
import java.util.ArrayList;

public class AliasCatalogsSchemasAndTypesCreator
{

   public static void createNodes(TreeView<ObjectTreeNode> objectsTree, Session session)
   {
      DatabaseStructure dataBaseStructure = session.getSchemaCache().getDataBaseStructure();

      TreeItem<ObjectTreeNode> aliasRoot = dataBaseStructure.visitTopToBottom((parent, structItem) -> createTreeItem(session, parent, structItem));

      objectsTree.setRoot(aliasRoot);
   }

   private static TreeItem<ObjectTreeNode> createTreeItem(Session session, TreeItem<ObjectTreeNode> parent, StructItem structItem)
   {
      if (structItem instanceof DatabaseStructure)
      {
         return ObjectTreeItemFactory.createAlias(session.getDbConnectorResult());
      }
      else if(structItem instanceof StructItemCatalog)
      {
         StructItemCatalog buf = (StructItemCatalog) structItem;
         TreeItem<ObjectTreeNode> catalog = ObjectTreeItemFactory.createCatalog(buf.getCatalog());
         parent.getChildren().add(catalog);
         return catalog;
      }
      else if(structItem instanceof StructItemSchema)
      {
         StructItemSchema buf = (StructItemSchema) structItem;
         TreeItem<ObjectTreeNode> schema = ObjectTreeItemFactory.createSchema(new DBSchema(buf.getSchema(), buf.getCatalog()));
         parent.getChildren().add(schema);
         return schema;
      }
      else if(structItem instanceof StructItemTableType)
      {
         StructItemTableType buf = (StructItemTableType) structItem;
         TreeItem<ObjectTreeNode> tableType = ObjectTreeItemFactory.createTableType(buf.getType(), buf.getCatalog(), buf.getSchema());
         parent.getChildren().add(tableType);
         return tableType;
      }
      else if(structItem instanceof StructItemProcedureType)
      {
         StructItemProcedureType buf = (StructItemProcedureType) structItem;
         TreeItem<ObjectTreeNode> procedureType = ObjectTreeItemFactory.createProcedureType(buf.getCatalog(), buf.getSchema());
         parent.getChildren().add(procedureType);
         return procedureType;
      }
      else if(structItem instanceof StructItemUDTType)
      {
         StructItemUDTType buf = (StructItemUDTType) structItem;
         TreeItem<ObjectTreeNode> udtType = ObjectTreeItemFactory.createUDTType(buf.getCatalog(), buf.getSchema());
         parent.getChildren().add(udtType);
         return udtType;
      }

      throw new IllegalArgumentException("Unknown StructItem subclass: " + structItem.getClass().getName());
   }
}
