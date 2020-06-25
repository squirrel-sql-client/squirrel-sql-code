package net.sourceforge.squirrel_sql.fw.timeoutproxy;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MetaDataTimeOutInvocationHandler implements InvocationHandler
{
   private static final ILogger s_log = LoggerController.createLogger(MetaDataTimeOutInvocationHandler.class);


   private final ExecutorService _threadPool;
   private DatabaseMetaData _metaData;
   private long _timeOut;

   public MetaDataTimeOutInvocationHandler(DatabaseMetaData metaData, long timeOut)
   {
      _threadPool = Executors.newCachedThreadPool();
      _metaData = metaData;
      _timeOut = timeOut;
   }

   @Override
   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
   {
      final Future<Object> future = _threadPool.submit(() -> method.invoke(_metaData, args));
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
