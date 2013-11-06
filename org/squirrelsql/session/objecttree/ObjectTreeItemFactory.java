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
      return new TreeItem(new ObjectTreeNode(_i18n.t("session.objecttree.alias", dbConnectorResult.getAlias().getName()), _props.getImageView("database.png")));
   }

   static TreeItem createCatalog(String catalog)
   {
      return new TreeItem(new ObjectTreeNode(_i18n.t("session.objecttree.catalog", catalog), _props.getImageView("catalog.png")));
   }

   public static TreeItem createSchema(DBSchema schema)
   {
      return new TreeItem(new ObjectTreeNode(_i18n.t("session.objecttree.schema", schema.getSchema()), _props.getImageView("schema.png")));
   }
}
