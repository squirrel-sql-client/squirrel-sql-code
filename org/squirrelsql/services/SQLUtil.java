package org.squirrelsql.services;

import java.sql.Connection;
import java.sql.ResultSet;

public class SQLUtil
{
   public static void close(Connection con)
   {
      if(null == con)
      {
         return;
      }

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
      if(null == res)
      {
         return;
      }

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
