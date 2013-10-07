package org.squirrelsql.session;

import org.squirrelsql.aliases.Alias;
import org.squirrelsql.aliases.dbconnector.ConnectFailureDecisionListener;

import java.sql.SQLException;

public class DbConnectorResult
{
   private SQLException _connectException;
   private Alias _alias;
   private boolean _canceled;
   private boolean _editAliasRequested;

   public DbConnectorResult(Alias alias)
   {
      _alias = alias;
   }

   public boolean isConnected()
   {
      return null == _connectException && false == _canceled;
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


}
