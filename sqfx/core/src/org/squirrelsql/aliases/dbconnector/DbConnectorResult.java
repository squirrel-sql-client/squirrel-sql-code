package org.squirrelsql.aliases.dbconnector;

import org.squirrelsql.aliases.Alias;
import org.squirrelsql.aliases.AliasDecorator;
import org.squirrelsql.services.sqlwrap.SQLConnection;
import org.squirrelsql.session.schemainfo.SchemaCache;
import org.squirrelsql.session.schemainfo.SchemaCacheProperty;

public class DbConnectorResult
{
   private Throwable _connectException;
   private AliasDecorator _alias;

   private String _user; // Should only be null if login was canceled.
   private String _password;

   private boolean _canceled;
   private boolean _editAliasRequested;
   private boolean _loginCanceled;
   private SQLConnection _sqlConnection;
   private SchemaCacheProperty _schemaCacheProperty = new SchemaCacheProperty();

   public DbConnectorResult(AliasDecorator alias, String user, String password)
   {
      _alias = alias;
      _user = user;
      _password = password;
   }

   public boolean isConnected()
   {
      return null != _sqlConnection;
   }

   public void setConnectException(Throwable connectException)
   {
      _connectException = connectException;
   }

   public Throwable getConnectException()
   {
      return _connectException;
   }

   public void setCanceled(boolean canceled)
   {
      _canceled = canceled;
   }

   public boolean isCanceled()
   {
      return _canceled;
   }

   public void setEditAliasRequested(boolean editAliasRequested)
   {
      _editAliasRequested = editAliasRequested;
   }

   public boolean isEditAliasRequested()
   {
      return _editAliasRequested;
   }


   public void setLoginCanceled(boolean loginCanceled)
   {
      _loginCanceled = loginCanceled;
   }

   public boolean isLoginCanceled()
   {
      return _loginCanceled;
   }

   public void setSQLConnection(SQLConnection sqlConnection)
   {
      _sqlConnection = sqlConnection;
   }

   public SQLConnection getSQLConnection()
   {
      return _sqlConnection;
   }

   public AliasDecorator getAliasDecorator()
   {
      return _alias;
   }

   public String getUser()
   {
      return _user;
   }

   public void setSchemaCache(SchemaCache schemaCache)
   {
      _schemaCacheProperty.set(schemaCache);
   }

   public SchemaCacheProperty getSchemaCacheValue()
   {
      return _schemaCacheProperty;
   }

   public void fireCacheUpdate()
   {
      _schemaCacheProperty.fireChanged();
   }

   public String getPassword()
   {
      return _password;
   }
}
