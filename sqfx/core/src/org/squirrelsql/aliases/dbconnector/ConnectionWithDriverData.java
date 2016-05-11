package org.squirrelsql.aliases.dbconnector;

import java.sql.Connection;

public class ConnectionWithDriverData
{
   private final Connection _jdbcConn;
   private final boolean _jdbcCompliant;

   public ConnectionWithDriverData(Connection jdbcConn, boolean jdbcCompliant)
   {
      _jdbcConn = jdbcConn;
      _jdbcCompliant = jdbcCompliant;
   }

   public Connection getConnection()
   {
      return _jdbcConn;
   }

   public boolean jdbcCompliant()
   {
      return _jdbcCompliant;
   }
}
