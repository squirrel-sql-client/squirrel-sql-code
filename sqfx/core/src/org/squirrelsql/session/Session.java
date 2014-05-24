package org.squirrelsql.session;

import org.squirrelsql.aliases.Alias;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.sqlwrap.SQLConnection;
import org.squirrelsql.session.schemainfo.SchemaCache;

public class Session
{
   private DbConnectorResult _dbConnectorResult;
   private SessionTabContext _mainTabContext;

   public Session(DbConnectorResult dbConnectorResult)
   {
      _dbConnectorResult = dbConnectorResult;
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
      return _dbConnectorResult.getSchemaCache();
   }

   public SessionProperties getSessionProperties()
   {
      return new SessionProperties();
   }

   public void setMainTabContext(SessionTabContext mainTabContext)
   {
      _mainTabContext = mainTabContext;
   }

   public SessionTabContext getMainTabContext()
   {
      return _mainTabContext;
   }

   public SQLConnection getSQLConnection()
   {
      return _dbConnectorResult.getSQLConnection();
   }
}
