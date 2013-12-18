package org.squirrelsql.session.objecttree;

import org.squirrelsql.session.Session;
import org.squirrelsql.table.TableLoader;
import org.squirrelsql.table.TableLoaderFactory;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TableDetailsReader
{
   private static interface MetaDataFunction
   {
      ResultSet apply(String catalog, String schema, String tableName) throws SQLException;
   }


   public static TableLoader readContent(Session session, ObjectTreeNode objectTreeNode)
   {
      try
      {
         String sql = "SELECT * FROM " + objectTreeNode.getTableInfo().getQualifiedName();

         Statement stat = session.getDbConnectorResult().getSQLConnection().getConnection().createStatement();

         stat.setMaxRows(session.getSessionProperties().getRowsLimit());
         ResultSet res = stat.executeQuery(sql);

         TableLoader tableLoader = TableLoaderFactory.loadDataFromResultSet(res);

         res.close();
         stat.close();

         return tableLoader;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static TableLoader readColumns(Session session, ObjectTreeNode objectTreeNode)
   {
      return loadTableMetaData(objectTreeNode, (catalog,schema,tableName) -> session.getDbConnectorResult().getSQLConnection().getDatabaseMetaData().getColumns(catalog, schema, tableName, null));
   }

   public static TableLoader readPrimaryKey(Session session, ObjectTreeNode objectTreeNode)
   {
      return loadTableMetaData(objectTreeNode, (catalog,schema,tableName) -> session.getDbConnectorResult().getSQLConnection().getDatabaseMetaData().getPrimaryKeys(catalog, schema, tableName));
   }

   public static TableLoader readExportedKeys(Session session, ObjectTreeNode objectTreeNode)
   {
      return loadTableMetaData(objectTreeNode, (catalog,schema,tableName) -> session.getDbConnectorResult().getSQLConnection().getDatabaseMetaData().getExportedKeys(catalog, schema, tableName));
   }

   public static TableLoader readImportedKeys(Session session, ObjectTreeNode objectTreeNode)
   {
      return loadTableMetaData(objectTreeNode, (catalog,schema,tableName) -> session.getDbConnectorResult().getSQLConnection().getDatabaseMetaData().getImportedKeys(catalog, schema, tableName));
   }

   public static TableLoader readIndexes(Session session, ObjectTreeNode objectTreeNode)
   {
      return loadTableMetaData(objectTreeNode, (catalog,schema,tableName) -> session.getDbConnectorResult().getSQLConnection().getDatabaseMetaData().getIndexInfo(catalog, schema, tableName, false, false));
   }

   public static TableLoader readTablePrivileges(Session session, ObjectTreeNode objectTreeNode)
   {
      return loadTableMetaData(objectTreeNode, (catalog,schema,tableName) -> session.getDbConnectorResult().getSQLConnection().getDatabaseMetaData().getTablePrivileges(catalog, schema, tableName));
   }

   public static TableLoader readColumnPrivileges(Session session, ObjectTreeNode objectTreeNode)
   {
      return loadTableMetaData(objectTreeNode, (catalog,schema,tableName) -> session.getDbConnectorResult().getSQLConnection().getDatabaseMetaData().getColumnPrivileges(catalog, schema, tableName, null));
   }

   public static TableLoader readBestRowIdentifier(Session session, ObjectTreeNode objectTreeNode)
   {
      return loadTableMetaData(objectTreeNode, (catalog,schema,tableName) -> session.getDbConnectorResult().getSQLConnection().getDatabaseMetaData().getBestRowIdentifier(catalog, schema, tableName, DatabaseMetaData.bestRowTransaction, true));
   }

   public static TableLoader readVersionColumns(Session session, ObjectTreeNode objectTreeNode)
   {
      return loadTableMetaData(objectTreeNode, (catalog,schema,tableName) -> session.getDbConnectorResult().getSQLConnection().getDatabaseMetaData().getVersionColumns(catalog, schema, tableName));
   }

   private static TableLoader loadTableMetaData(ObjectTreeNode objectTreeNode, MetaDataFunction metaDataFunction)
   {
      try
      {
         String catalog = objectTreeNode.getTableInfo().getCatalog();
         String schema = objectTreeNode.getTableInfo().getSchema();
         String name = objectTreeNode.getTableInfo().getName();

         ResultSet resultSet = metaDataFunction.apply(catalog, schema, name);

         TableLoader tableLoader = TableLoaderFactory.loadDataFromResultSet(resultSet, "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME");

         resultSet.close();
         return tableLoader;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }
}
