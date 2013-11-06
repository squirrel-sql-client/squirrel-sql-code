package org.squirrelsql.session.objecttree;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.apache.commons.lang3.SerializationUtils;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.DBSchema;

import java.util.ArrayList;

public class AliasCatalogsSchemasAndTypesCreator
{
   public static void createNodes(TreeView<ObjectTreeNode> objectsTree, DbConnectorResult dbConnectorResult)
   {
      TreeItem aliasRoot = ObjectTreeItemFactory.createAlias(dbConnectorResult);
      objectsTree.setRoot(aliasRoot);

      ArrayList<String> catalogs = new ArrayList<>();

      boolean supportsCatalogs = dbConnectorResult.getSQLConnection().supportsCatalogs();

      if (supportsCatalogs)
      {
         catalogs = dbConnectorResult.getSQLConnection().getCatalogs();
      }

      for (String catalog : catalogs)
      {
         aliasRoot.getChildren().add(ObjectTreeItemFactory.createCatalog(catalog));
      }

      ArrayList<DBSchema> schemas = dbConnectorResult.getSQLConnection().getSchemas();

      ArrayList<DBSchema> schemasInCatalogs = SerializationUtils.clone(schemas);

      for (Object o : aliasRoot.getChildren())
      {
         TreeItem<ObjectTreeNode> catalogItem = (TreeItem<ObjectTreeNode>) o;

         for (DBSchema schema : schemas)
         {
            if(Utils.compareRespectEmpty(catalogItem.getValue().getNodeName(), schema.getCatalog()))
            {
               catalogItem.getChildren().add(ObjectTreeItemFactory.createSchema(schema));
               schemasInCatalogs.add(schema);
            }
         }
      }

      ArrayList<DBSchema> schemasNotInCatalogs = (ArrayList<DBSchema>) schemas.clone();
      schemasNotInCatalogs.removeAll(schemasInCatalogs);


      TreeItem<ObjectTreeNode> parentForNonCatalogSchemas = getParentForNonCatalogSchemas(aliasRoot, catalogs, supportsCatalogs, schemasNotInCatalogs);

      for (DBSchema schemasNotInCatalog : schemasNotInCatalogs)
      {
         parentForNonCatalogSchemas.getChildren().add(ObjectTreeItemFactory.createSchema(schemasNotInCatalog));
      }
   }

   private static TreeItem<ObjectTreeNode> getParentForNonCatalogSchemas(TreeItem aliasRoot, ArrayList<String> catalogs, boolean supportsCatalogs, ArrayList<DBSchema> schemasNotInCatalogs)
   {
      TreeItem<ObjectTreeNode> ret = aliasRoot;

      if(supportsCatalogs && 0 < catalogs.size() && 0 < schemasNotInCatalogs.size())
      {
         ret = ObjectTreeItemFactory.createCatalog("<NO CATALOG>");
         aliasRoot.getChildren().add(ret);
      }
      return ret;
   }
}
