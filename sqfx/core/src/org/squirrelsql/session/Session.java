package org.squirrelsql.session;

import javafx.scene.control.Tab;
import org.squirrelsql.aliases.Alias;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.session.action.ActionManager;
import org.squirrelsql.session.schemainfo.SchemaCache;

public class Session
{
   private DbConnectorResult _dbConnectorResult;
   private ActionManager _actionManager = new ActionManager();

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

   public ActionManager getActionManager()
   {
      return _actionManager;
   }

}
