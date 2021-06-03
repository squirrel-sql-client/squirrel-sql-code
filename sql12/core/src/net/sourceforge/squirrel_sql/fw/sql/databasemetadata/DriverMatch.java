package net.sourceforge.squirrel_sql.fw.sql.databasemetadata;

import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;

public class DriverMatch
{
   private static final String COM_HTTX_DRIVER_PREFIX = "com.hxtt.sql.";

   public static boolean isComHttxDriver(ISQLConnection con)
   {
      if (null == con)
      {
         return false;
      }
      return con.getSQLDriver().getDriverClassName().startsWith(COM_HTTX_DRIVER_PREFIX);
   }
}
