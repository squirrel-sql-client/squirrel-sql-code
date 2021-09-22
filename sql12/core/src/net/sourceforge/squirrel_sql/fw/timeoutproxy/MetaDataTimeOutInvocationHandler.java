package net.sourceforge.squirrel_sql.fw.timeoutproxy;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MetaDataTimeOutInvocationHandler implements InvocationHandler
{
   private static final ILogger s_log = LoggerController.createLogger(MetaDataTimeOutInvocationHandler.class);

   private BufferingDatabaseMetaDataProvider _metaDataProvider;
   private long _timeOut;

   public MetaDataTimeOutInvocationHandler(DatabaseMetaDataProvider metaDataProvider, long timeOut)
   {
      _metaDataProvider = new BufferingDatabaseMetaDataProvider(metaDataProvider);
      _timeOut = timeOut;
   }

   @Override
   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
   {
      final Future<Object> future = StaticTimeOutThreadPool.submit(() -> method.invoke(_metaDataProvider.getDataBaseMetaData(), args));
      try
      {
         return future.get(_timeOut, TimeUnit.MILLISECONDS);
      }
      catch (TimeoutException e)
      {
         final String msg = "Timeout as configured in menu File --> New Session Properties --> tab SQL --> section \"Meta data loading\" occured.";
         s_log.error(msg);

         throw new RuntimeException(msg, e);
      }
   }
}
