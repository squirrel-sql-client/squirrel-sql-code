package org.squirrelsql.session;

import org.squirrelsql.aliases.dbconnector.DbConnectorResult;

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

   public ArrayList<TableInfo> getTableInfos(String catalog, String schema, String tableType)
   {
      return _dbConnectorResult.getSQLConnection().getTableInfos(catalog, schema, tableType);
   }

   public ArrayList<ProcedureInfo> getProcedureInfos(String catalog, String schema)
   {
      return _dbConnectorResult.getSQLConnection().getProcedureInfos(catalog, schema);
   }

   public ArrayList<UDTInfo> getUDTInfos(String catalog, String schema)
   {
      return _dbConnectorResult.getSQLConnection().getUDTInfos(catalog, schema);
   }
}
