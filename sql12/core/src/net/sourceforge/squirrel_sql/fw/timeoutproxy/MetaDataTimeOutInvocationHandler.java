package net.sourceforge.squirrel_sql.fw.timeoutproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class MetaDataTimeOutInvocationHandler implements InvocationHandler
{
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
      return future.get(_timeOut, TimeUnit.MILLISECONDS);
   }
}
