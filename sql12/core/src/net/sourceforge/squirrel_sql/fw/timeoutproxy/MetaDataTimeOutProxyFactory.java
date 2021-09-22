package net.sourceforge.squirrel_sql.fw.timeoutproxy;

import net.sourceforge.squirrel_sql.client.cli.CliInitializer;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.lang.reflect.Proxy;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class MetaDataTimeOutProxyFactory
{
   public static DatabaseMetaData wrap(DatabaseMetaDataProvider metaDataProvider)
   {
      return wrap(metaDataProvider, null);
   }

   public static DatabaseMetaData wrap(DatabaseMetaDataProvider metaDataProvider, ISession session)
   {
      try
      {
         if(CliInitializer.isInShellMode())
         {
            return metaDataProvider.getDataBaseMetaData();
         }

         long metaDataLoadingTimeOut;

         if (null == session)
         {
            metaDataLoadingTimeOut= TimeOutUtil.getMetaDataLoadingTimeOutOfActiveSession();
         }
         else
         {
            metaDataLoadingTimeOut = session.getProperties().getMetaDataLoadingTimeOut();
         }

         if(0 < metaDataLoadingTimeOut)
         {
            return (DatabaseMetaData) Proxy.newProxyInstance(MetaDataTimeOutProxyFactory.class.getClassLoader(), new Class[]{DatabaseMetaData.class}, new MetaDataTimeOutInvocationHandler(metaDataProvider, metaDataLoadingTimeOut));
         }

         return metaDataProvider.getDataBaseMetaData();
      }
      catch (SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
