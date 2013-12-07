package org.squirrelsql.session;

import org.squirrelsql.aliases.Alias;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.session.schemainfo.SchemaCache;

public class Session
{
   private SchemaCache _schemaCache;
   private DbConnectorResult _dbConnectorResult;

   public Session(DbConnectorResult dbConnectorResult)
   {
      _dbConnectorResult = dbConnectorResult;
      _schemaCache = new SchemaCache(dbConnectorResult);
   }

   public Alias getAlias()
   {
      return _dbConnectorResult.getAlias();
   }

   public void close()
   {
      _dbConnectorResult.getSQLConnection().close();
   }

   public DbConnectorResult getDbConnectorResult()
   {
      return _dbConnectorResult;
   }

   public SchemaCache getSchemaCache()
   {
      return _schemaCache;
   }
}
