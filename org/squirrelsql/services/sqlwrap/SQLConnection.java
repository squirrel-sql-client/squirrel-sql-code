package org.squirrelsql.services.sqlwrap;

import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;

import java.sql.Connection;

public class SQLConnection
{
   private MessageHandler _mhLog = new MessageHandler(this.getClass(), MessageHandlerDestination.MESSAGE_LOG);

   private Connection _con;

   public SQLConnection(Connection con)
   {
      _con = con;
   }

   public void close()
   {
      try
      {
         _con.close();
      }
      catch (Throwable e)
      {
         _mhLog.warning("Error closing connection" , e);
      }
   }
}
