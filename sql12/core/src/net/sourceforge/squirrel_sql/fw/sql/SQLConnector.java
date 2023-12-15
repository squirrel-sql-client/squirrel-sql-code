package net.sourceforge.squirrel_sql.fw.sql;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.session.action.reconnect.ReconnectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

public class SQLConnector
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLConnector.class);

   public static SQLConnection getSqlConnection(ISQLDriver sqlDriver, SQLAlias alias, String user, String pw, SQLDriverPropertyCollection props, ReconnectInfo reconnectInfo, Driver driver)
   {
      try
      {
         Properties myProps = new Properties();
         if (props != null)
         {
            props.applyTo(myProps);
         }

         if(null != reconnectInfo && null != reconnectInfo.getUser())
         {
            myProps.put("user", reconnectInfo.getUser());
         }
         else if (user != null)
         {
            myProps.put("user", user);
         }

         if(null != reconnectInfo && null != reconnectInfo.getPassword())
         {
            myProps.put("password", reconnectInfo.getPassword());
         }
         else if (pw != null)
         {
            myProps.put("password", pw);
         }

         if (driver == null)
         {
            ClassLoader loader = new SQLDriverClassLoader(sqlDriver);
            driver = (Driver) (Class.forName(sqlDriver.getDriverClassName(), false, loader).getDeclaredConstructor().newInstance());
         }

         String url = alias.getUrl();

         if(null != reconnectInfo && null != reconnectInfo.getUrl())
         {
            url = reconnectInfo.getUrl();
         }

         Connection jdbcConn = driver.connect(url, myProps);

         if (jdbcConn == null)
         {
            throw new SQLException(s_stringMgr.getString("SQLDriverManager.error.noconnection"));
         }
         return new SQLConnection(jdbcConn, props, sqlDriver);
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
