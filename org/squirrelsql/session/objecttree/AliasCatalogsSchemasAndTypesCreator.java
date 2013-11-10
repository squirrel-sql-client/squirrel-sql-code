package org.squirrelsql.session.objecttree;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.DBSchema;

import java.sql.SQLException;
import java.util.ArrayList;

public class AliasCatalogsSchemasAndTypesCreator
{

   public static void createNodes(TreeView<ObjectTreeNode> objectsTree, DbConnectorResult dbConnectorResult)
   {
      TreeItem<ObjectTreeNode> aliasRoot = ObjectTreeItemFactory.createAlias(dbConnectorResult);

      recursiveAppendChildren(aliasRoot, dbConnectorResult);

      objectsTree.setRoot(aliasRoot);

   }

   private static void recursiveAppendChildren(TreeItem<ObjectTreeNode> parent, DbConnectorResult dbConnectorResult)
   {
      if(appendChildren(parent, dbConnectorResult))
      {
         for (TreeItem<ObjectTreeNode> child : parent.getChildren())
         {
            recursiveAppendChildren(child, dbConnectorResult);
         }
      }

   }

   private static boolean appendChildren(TreeItem<ObjectTreeNode> parent, DbConnectorResult dbConnectorResult)
   {

      boolean supportsCatalogs = supportsCatalogs(dbConnectorResult);

      boolean supportsSchemas = supportsSchemas(dbConnectorResult);

      boolean addedChildren = false;

      if (parent.getValue().isOfType(ObjectTreeNodeTypeKey.ALIAS_TYPE_KEY))
      {
         // If a driver says it supports schemas/catalogs but doesn't
         // provide schema/catalog nodes, try to get other nodes.
         if (supportsCatalogs)
         {
            addedChildren = appendCatalogs(parent, dbConnectorResult);
         }

         if (false == addedChildren && supportsSchemas)
         {
            addedChildren = appendSchemas(parent, dbConnectorResult, null);
         }

         if (false == addedChildren)
         {
            addedChildren = appendTypes(parent, dbConnectorResult, null, null);
         }
      }
      else if (parent.getValue().isOfType(ObjectTreeNodeTypeKey.CATALOG_TYPE_KEY))
      {
         // If a driver says it supports schemas but doesn't
         // provide schema nodes, try to get other nodes.
         final String catalogName = parent.getValue().getCatalog();
         if (supportsSchemas)
         {
            addedChildren = appendSchemas(parent, dbConnectorResult, catalogName);
         }
         if (false == addedChildren)
         {
            addedChildren = appendTypes(parent, dbConnectorResult, null, catalogName);
         }
      }
      else if (parent.getValue().isOfType(ObjectTreeNodeTypeKey.SCHEMA_TYPE_KEY))
      {
         addedChildren = appendTypes(parent, dbConnectorResult, parent.getValue().getCatalog(), parent.getValue().getSchema());
      }

      return addedChildren;
   }

   private static boolean supportsSchemas(DbConnectorResult dbConnectorResult)
   {
      boolean supportsSchemas = false;
      try
      {
         supportsSchemas = dbConnectorResult.getSQLConnection().supportsSchemas();
      }
      catch (SQLException ex)
      {
      }
      return supportsSchemas;
   }

   private static boolean supportsCatalogs(DbConnectorResult dbConnectorResult)
   {
      boolean supportsCatalogs = false;
      try
      {
         supportsCatalogs = dbConnectorResult.getSQLConnection().supportsCatalogs();
      }
      catch (Exception ex)
      {
      }
      return supportsCatalogs;
   }

   private static boolean supportsProcerdures(DbConnectorResult dbConnectorResult)
   {
      boolean supportsProcedures = false;
      try
      {
         supportsProcedures = dbConnectorResult.getSQLConnection().supportsStoredProcedures();
      }
      catch (Exception ex)
      {
      }
      return supportsProcedures;
   }


   private static boolean appendTypes(TreeItem<ObjectTreeNode> parent, DbConnectorResult dbConnectorResult, String catalog, String schema)
   {
      boolean addedChildren = false;

      ArrayList<String> types = dbConnectorResult.getSQLConnection().getTableTypes();

      for (String type : types)
      {
         parent.getChildren().add(ObjectTreeItemFactory.createTableType(type, catalog, schema));
         addedChildren = true;
      }

      if (supportsProcerdures(dbConnectorResult))
      {
         parent.getChildren().add(ObjectTreeItemFactory.createProcedureType(catalog, schema));
      }

      parent.getChildren().add(ObjectTreeItemFactory.createUDTType(catalog, schema));

      return addedChildren;
   }

   private static boolean appendSchemas(TreeItem<ObjectTreeNode> parent, DbConnectorResult dbConnectorResult, String catalogName)
   {
      boolean addedChildren = false;
      ArrayList<DBSchema> schemas = dbConnectorResult.getSQLConnection().getSchemas();

      for (DBSchema schema : schemas)
      {
         if (Utils.compareRespectEmpty(catalogName, schema.getCatalog()))
         {
            parent.getChildren().add(ObjectTreeItemFactory.createSchema(schema));
            addedChildren = true;
         }
      }
      return addedChildren;
   }

   private static boolean appendCatalogs(TreeItem<ObjectTreeNode> parent, DbConnectorResult dbConnectorResult)
   {
      boolean addedChildren = false;
      ArrayList<String> catalogs = dbConnectorResult.getSQLConnection().getCatalogs();

      for (String catalog : catalogs)
      {
         parent.getChildren().add(ObjectTreeItemFactory.createCatalog(catalog));
         addedChildren = true;
      }
      return addedChildren;
   }
}
