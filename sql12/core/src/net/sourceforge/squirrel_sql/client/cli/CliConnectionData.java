package net.sourceforge.squirrel_sql.client.cli;

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

import java.sql.SQLException;

public class CliConnectionData
{
   private ISQLAlias _alias;
   private CliSession _cliSession;

   public ISQLAlias getAlias()
   {
      return _alias;
   }

   public void setAlias(ISQLAlias alias)
   {
      _alias = alias;

      try
      {
         closeCliSession();
      }
      catch (Exception e)
      {
         //
      }
   }

   public CliSession getCliSession()
   {
      return _cliSession;
   }

   public void createCliSession()
   {
      if(null == _alias)
      {
         System.err.println("ERROR: No database connection has been opened. Call connect(...) to open a connection.");
         return;
      }

      _cliSession = new CliSession(_alias);
   }

   public boolean closeCliSession() throws SQLException
   {
      if(null == _cliSession)
      {
         return false;
      }

      try
      {
         _cliSession.close();
      }
      finally
      {
         _cliSession = null;
      }


      return true;
   }

   public void ensureCliSessionCreated()
   {
      if(null == _cliSession)
      {
         createCliSession();
      }
   }
}
