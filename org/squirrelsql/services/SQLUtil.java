package org.squirrelsql.services;

import java.sql.Connection;
import java.sql.ResultSet;

public class SQLUtil
{
   public static void close(Connection con)
   {
      try
      {
         con.close();
      }
      catch (Throwable e)
      {
         new MessageHandler(SQLUtil.class, MessageHandlerDestination.MESSAGE_LOG).warning("Error closing connection", e);
      }
   }

   public static void close(ResultSet res)
   {
      try
      {
         res.close();
      }
      catch (Throwable e)
      {
         new MessageHandler(SQLUtil.class, MessageHandlerDestination.MESSAGE_LOG).warning("Error closing ResultSet", e);
      }
   }
}
