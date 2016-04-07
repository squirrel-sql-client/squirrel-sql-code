package org.squirrelsql.session;

import org.squirrelsql.aliases.Alias;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.sqlwrap.SQLConnection;
import org.squirrelsql.session.schemainfo.SchemaCacheProperty;

public class Session
{
   private DbConnectorResult _dbConnectorResult;
   private SessionTabContext _mainTabContext;
   private SessionCtrl _sessionCtrl;

   public Session(DbConnectorResult dbConnectorResult)
   {
      _dbConnectorResult = dbConnectorResult;
   }

   public Alias getAlias()
   {
      return _dbConnectorResult.getAliasDecorator().getAlias();
   }

   public void close()
   {
      _dbConnectorResult.close();
   }

   public DbConnectorResult getDbConnectorResult()
   {
      return _dbConnectorResult;
   }

   public SchemaCacheProperty getSchemaCacheValue()
   {
      return _dbConnectorResult.getSchemaCacheProperty();
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

   public void setSessionCtrl(SessionCtrl sessionCtrl)
   {
      _sessionCtrl = sessionCtrl;
   }

   public SessionCtrl getSessionCtrl()
   {
      return _sessionCtrl;
   }
}
