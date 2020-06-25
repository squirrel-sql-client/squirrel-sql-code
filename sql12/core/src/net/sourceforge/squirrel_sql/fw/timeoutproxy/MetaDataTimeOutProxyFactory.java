package net.sourceforge.squirrel_sql.fw.timeoutproxy;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;

import java.lang.reflect.Proxy;
import java.sql.DatabaseMetaData;

public class MetaDataTimeOutProxyFactory
{
   public static DatabaseMetaData wrap(DatabaseMetaData metaData)
   {
      return wrap(metaData, null);
   }

   public static DatabaseMetaData wrap(DatabaseMetaData metaData, ISession session)
   {
      long metaDataLoadingTimeOut;

      if (null == session)
      {
         metaDataLoadingTimeOut = Main.getApplication().getSquirrelPreferences().getSessionProperties().getMetaDataLoadingTimeOut();

         if(false == Main.getApplication().getSessionManager().isInCreateSession())
         {
            metaDataLoadingTimeOut = Main.getApplication().getSessionManager().getActiveSession().getProperties().getMetaDataLoadingTimeOut();
         }
      }
      else
      {
         metaDataLoadingTimeOut = session.getProperties().getMetaDataLoadingTimeOut();
      }

      if(0 < metaDataLoadingTimeOut)
      {
         return (DatabaseMetaData) Proxy.newProxyInstance(MetaDataTimeOutProxyFactory.class.getClassLoader(), new Class[]{DatabaseMetaData.class}, new MetaDataTimeOutInvocationHandler(metaData, metaDataLoadingTimeOut));
      }

      return metaData;
   }

}
