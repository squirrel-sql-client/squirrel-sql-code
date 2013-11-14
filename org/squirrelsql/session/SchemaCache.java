package org.squirrelsql.session;

import javafx.scene.control.TreeItem;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.session.objecttree.ObjectTreeNode;

import java.util.ArrayList;

public class SchemaCache
{
   private DbConnectorResult _dbConnectorResult;

   public SchemaCache(DbConnectorResult dbConnectorResult)
   {
      _dbConnectorResult = dbConnectorResult;
   }

   public boolean shouldLoadSchema(DBSchema schema)
   {
      return true;
   }

   public ArrayList<Table> getTables(String catalog, String schema, String tableType)
   {
      return _dbConnectorResult.getSQLConnection().getTables(catalog, schema, tableType);
   }
}
