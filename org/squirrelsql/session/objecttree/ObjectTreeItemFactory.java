package org.squirrelsql.session.objecttree;

import javafx.scene.control.TreeItem;
import org.squirrelsql.Props;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.DBSchema;
import org.squirrelsql.session.ProcedureInfo;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.UDTInfo;

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

   public static TreeItem<ObjectTreeNode> createTableType(String tableType, String catalog, String schema)
   {
      ObjectTreeNode objectTreeNode = new ObjectTreeNode(ObjectTreeNodeTypeKey.TABLE_TYPE_TYPE_KEY, _i18n.t("session.objecttree.tableType", tableType), catalog, schema, _props.getImageView("tableType.png"));
      objectTreeNode.setTableType(tableType);

      return new TreeItem(objectTreeNode);
   }

   public static TreeItem<ObjectTreeNode> createProcedureType(String catalog, String schema)
   {
      return new TreeItem(new ObjectTreeNode(ObjectTreeNodeTypeKey.PROCEDURE_TYPE_KEY, _i18n.t("session.objecttree.procedureType"), catalog, schema, _props.getImageView("procedureType.png")));
   }

   public static TreeItem<ObjectTreeNode> createUDTType(String catalog, String schema)
   {
      return new TreeItem(new ObjectTreeNode(ObjectTreeNodeTypeKey.UDT_TYPE_KEY, _i18n.t("session.objecttree.udtType"), catalog, schema, _props.getImageView("udtType.png")));
   }

   public static TreeItem<ObjectTreeNode> createTable(TableInfo tableInfo)
   {
      ObjectTreeNode objectTreeNode = new ObjectTreeNode(ObjectTreeNodeTypeKey.TABLE_TYPE_KEY, tableInfo.getName(), tableInfo.getCatalog(), tableInfo.getSchema(), _props.getImageView("table.png"));
      objectTreeNode.setTableInfo(tableInfo);

      return new TreeItem(objectTreeNode);
   }


   public static TreeItem<ObjectTreeNode> createProcedure(ProcedureInfo procedureInfo)
   {
      ObjectTreeNode objectTreeNode = new ObjectTreeNode(ObjectTreeNodeTypeKey.PROCEDURE_TYPE_KEY, procedureInfo.getName(), procedureInfo.getCatalog(), procedureInfo.getSchema(), _props.getImageView("procedure.png"));
      objectTreeNode.setProcedureInfo(procedureInfo);

      return new TreeItem(objectTreeNode);
   }

   public static TreeItem<ObjectTreeNode> createUDT(UDTInfo udtInfo)
   {
      ObjectTreeNode objectTreeNode = new ObjectTreeNode(ObjectTreeNodeTypeKey.UDT_TYPE_KEY, udtInfo.getName(), udtInfo.getCatalog(), udtInfo.getSchema(), _props.getImageView("udt.png"));
      objectTreeNode.setUDTInfo(udtInfo);

      return new TreeItem(objectTreeNode);
   }
}
