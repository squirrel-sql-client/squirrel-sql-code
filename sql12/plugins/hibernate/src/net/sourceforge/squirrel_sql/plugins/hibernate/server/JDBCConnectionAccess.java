package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

public class JDBCConnectionAccess
{
   static HibernateSqlConnectionData getHibernateSqlConnectionData(ClassLoader cl, ReflectionCaller rcHibernateSession)
   {
      try
      {
         Class<?> returningWorkIf = cl.loadClass("org.hibernate.jdbc.ReturningWork");

         Object proxy
               = Proxy.newProxyInstance(cl, new Class[]{returningWorkIf}, new InvocationHandler()
                  {
                     @Override
                     public Object invoke(Object proxy, Method method, Object[] args)
                     {
                        return (Connection) args[0];
                     }
                  });

         RCParam param = new RCParam();
         param.add(proxy, returningWorkIf);

         Connection con = (Connection) rcHibernateSession.callMethod("doReturningWork", param).getCallee();

         DatabaseMetaData md = con.getMetaData();
         return new HibernateSqlConnectionData(md.getURL(), md.getUserName(), md.getDriverName(), md.getDriverVersion());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
