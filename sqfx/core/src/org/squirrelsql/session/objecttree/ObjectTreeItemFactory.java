package org.squirrelsql.session.objecttree;

import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import org.squirrelsql.Props;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.globalicons.GlobalIconNames;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.DBSchema;
import org.squirrelsql.session.ProcedureInfo;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.UDTInfo;

public class ObjectTreeItemFactory
{
   private static I18n _i18n = new I18n(ObjectTreeItemFactory.class);
   private static Props _props = new Props(ObjectTreeItemFactory.class);

   private static final Image _databaseImage = _props.getImage(GlobalIconNames.DATABASE);
   private static final Image _catalogImage = _props.getImage(GlobalIconNames.CATALOG);
   private static final Image _schemaImage = _props.getImage(GlobalIconNames.SCHEMA);
   private static final Image _tableTypeImage = _props.getImage(GlobalIconNames.TABLE_TYPE);
   private static final Image _procedureTypeImage = _props.getImage(GlobalIconNames.PROCEDURE_TYPE);
   private static final Image _udtTypeImage = _props.getImage(GlobalIconNames.UDT_TYPE);
   private static final Image _tableImage = _props.getImage(GlobalIconNames.TABLE);
   private static final Image _procedureImage = _props.getImage(GlobalIconNames.PROCEDURE);
   private static final Image _udtImage = _props.getImage(GlobalIconNames.UDT);


   public static TreeItem<ObjectTreeNode> createAlias(DbConnectorResult dbConnectorResult)
   {
      ObjectTreeNode objectTreeNode = new ObjectTreeNode(ObjectTreeNodeTypeKey.ALIAS_TYPE_KEY, _i18n.t("session.objecttree.alias", dbConnectorResult.getAliasDecorator().getName()), null, null, _props.getImageView(_databaseImage));
      objectTreeNode.setDBConnectorResult(dbConnectorResult);

      return new TreeItem(objectTreeNode);
   }

   static TreeItem<ObjectTreeNode> createCatalog(String catalog)
   {
      return new TreeItem(new ObjectTreeNode(ObjectTreeNodeTypeKey.CATALOG_TYPE_KEY, _i18n.t("session.objecttree.catalog", catalog), catalog, null, _props.getImageView(_catalogImage)));
   }

   public static TreeItem<ObjectTreeNode> createSchema(DBSchema schema)
   {
      return new TreeItem(new ObjectTreeNode(ObjectTreeNodeTypeKey.SCHEMA_TYPE_KEY, _i18n.t("session.objecttree.schema", schema.getSchema()), schema.getCatalog(), schema.getSchema(), _props.getImageView(_schemaImage)));
   }

   public static TreeItem<ObjectTreeNode> createTableType(String tableType, String catalog, String schema)
   {
      ObjectTreeNode objectTreeNode = new ObjectTreeNode(ObjectTreeNodeTypeKey.TABLE_TYPE_TYPE_KEY, _i18n.t("session.objecttree.tableType", tableType), catalog, schema, _props.getImageView(_tableTypeImage));
      objectTreeNode.setTableType(tableType);

      return new TreeItem(objectTreeNode);
   }

   public static TreeItem<ObjectTreeNode> createProcedureType(String catalog, String schema)
   {
      return new TreeItem(new ObjectTreeNode(ObjectTreeNodeTypeKey.PROCEDURE_TYPE_KEY, _i18n.t("session.objecttree.procedureType"), catalog, schema, _props.getImageView(_procedureTypeImage)));
   }

   public static TreeItem<ObjectTreeNode> createUDTType(String catalog, String schema)
   {
      return new TreeItem(new ObjectTreeNode(ObjectTreeNodeTypeKey.UDT_TYPE_KEY, _i18n.t("session.objecttree.udtType"), catalog, schema, _props.getImageView(_udtTypeImage)));
   }

   public static TreeItem<ObjectTreeNode> createTable(TableInfo tableInfo)
   {
      ObjectTreeNode objectTreeNode = new ObjectTreeNode(ObjectTreeNodeTypeKey.TABLE_TYPE_KEY, tableInfo.getName(), tableInfo.getCatalog(), tableInfo.getSchema(), _props.getImageView(_tableImage));
      objectTreeNode.setTableInfo(tableInfo);

      return new TreeItem(objectTreeNode);
   }


   public static TreeItem<ObjectTreeNode> createProcedure(ProcedureInfo procedureInfo)
   {
      ObjectTreeNode objectTreeNode = new ObjectTreeNode(ObjectTreeNodeTypeKey.PROCEDURE_TYPE_KEY, procedureInfo.getName(), procedureInfo.getCatalog(), procedureInfo.getSchema(), _props.getImageView(_procedureImage));
      objectTreeNode.setProcedureInfo(procedureInfo);

      return new TreeItem(objectTreeNode);
   }

   public static TreeItem<ObjectTreeNode> createUDT(UDTInfo udtInfo)
   {
      ObjectTreeNode objectTreeNode = new ObjectTreeNode(ObjectTreeNodeTypeKey.UDT_TYPE_KEY, udtInfo.getName(), udtInfo.getCatalog(), udtInfo.getSchema(), _props.getImageView(_udtImage));
      objectTreeNode.setUDTInfo(udtInfo);

      return new TreeItem(objectTreeNode);
   }
}
