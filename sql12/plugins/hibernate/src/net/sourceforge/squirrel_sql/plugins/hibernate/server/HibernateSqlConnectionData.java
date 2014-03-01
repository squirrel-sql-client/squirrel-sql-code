package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.Serializable;

public class HibernateSqlConnectionData implements Serializable
{
   private String _url;
   private String _userName;
   private String _driverName;
   private String _driverVersion;

   public HibernateSqlConnectionData(String url, String userName, String driverName, String driverVersion)
   {
      _url = url;
      _userName = userName;
      _driverName = driverName;
      _driverVersion = driverVersion;
   }

   public String getURL()
   {
      return _url;
   }

   public String getUserName()
   {
      return _userName;
   }

   public String getDriverName()
   {
      return _driverName;
   }

   public String getDriverVersion()
   {
      return _driverVersion;
   }
}
