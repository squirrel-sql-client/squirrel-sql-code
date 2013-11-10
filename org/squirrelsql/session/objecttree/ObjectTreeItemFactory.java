package org.squirrelsql.session.objecttree;

import javafx.scene.control.TreeItem;
import org.squirrelsql.Props;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.DBSchema;

public class ObjectTreeItemFactory
{
   private static I18n _i18n = new I18n(ObjectTreeItemFactory.class);
   private static Props _props = new Props(ObjectTreeItemFactory.class);


   public static TreeItem createAlias(DbConnectorResult dbConnectorResult)
   {
      return new TreeItem(new ObjectTreeNode(ObjectTreeNodeTypeKey.ALIAS_TYPE_KEY, _i18n.t("session.objecttree.alias", dbConnectorResult.getAlias().getName()), null, null, _props.getImageView("database.png")));
   }

   static TreeItem createCatalog(String catalog)
   {
      return new TreeItem(new ObjectTreeNode(ObjectTreeNodeTypeKey.CATALOG_TYPE_KEY, _i18n.t("session.objecttree.catalog", catalog), catalog, null, _props.getImageView("catalog.png")));
   }

   public static TreeItem createSchema(DBSchema schema)
   {
      return new TreeItem(new ObjectTreeNode(ObjectTreeNodeTypeKey.SCHEMA_TYPE_KEY, _i18n.t("session.objecttree.schema", schema.getSchema()), schema.getCatalog(), schema.getSchema(), _props.getImageView("schema.png")));
   }

   public static TreeItem<ObjectTreeNode> createTableType(String type)
   {
      return new TreeItem(new ObjectTreeNode(ObjectTreeNodeTypeKey.TABLE_TYPE_KEY, _i18n.t("session.objecttree.tableType", type), null, null, _props.getImageView("tableType.png")));
   }
}
