package org.squirrelsql.services;

import org.squirrelsql.aliases.Alias;
import org.squirrelsql.aliases.dbconnector.ConnectionWithDriverData;
import org.squirrelsql.drivers.DriversUtil;
import org.squirrelsql.drivers.SQLDriver;
import org.squirrelsql.services.sqlwrap.SQLDriverClassLoader;

import java.sql.Connection;
import java.sql.Driver;
import java.util.Properties;

public class JDBCUtil
{
   public static ConnectionWithDriverData createJDBCConnection(Alias alias, String user, String password)
   {
      try
      {
         SQLDriver sqlDriver = DriversUtil.findDriver(alias.getDriverId());

         SQLDriverClassLoader driverClassLoader = DriversUtil.createDriverClassLoader(sqlDriver.getJarFileNamesList());

         Driver driver = (Driver)(Class.forName(sqlDriver.getDriverClassName(), false, driverClassLoader).newInstance());

         Properties myProps = new Properties();
         if (user != null)
         {
            myProps.put("user", user);
         }
         if (password != null)
         {
            myProps.put("password", password);
         }

         Connection jdbcConn = driver.connect(alias.getUrl(), myProps);

         if(null == jdbcConn)
         {
            // See Api doc of java.sql.Driver.connect();
            String msg =
                  "Wrong driver class \"" + sqlDriver.getDriverClassName() +
                  "\" to connect to URL \"" + alias.getUrl() + "\"" +
                  "\nDid you choose the wrong driver in your Alias definition?";

            throw new IllegalStateException(msg);
         }



         return new ConnectionWithDriverData(jdbcConn, driver.jdbcCompliant());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
