package org.squirrelsql.aliases.dbconnector;

import org.squirrelsql.aliases.Alias;
import org.squirrelsql.aliases.dbconnector.ConnectFailureDecisionListener;

import java.sql.SQLException;

public class DbConnectorResult
{
   private SQLException _connectException;
   private Alias _alias;

   private String _user; // Should only be null if login was canceled.

   private boolean _canceled;
   private boolean _editAliasRequested;
   private boolean _loginCanceled;

   public DbConnectorResult(Alias alias, String user)
   {
      _alias = alias;
      _user = user;
   }

   public boolean isConnected()
   {
      return null == _connectException && false == _canceled && false == _loginCanceled;
   }

   public void setConnectException(SQLException connectException)
   {
      _connectException = connectException;
   }

   public SQLException getConnectException()
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
}
