package net.sourceforge.squirrel_sql.fw.timeoutproxy;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class StaticTimeOutThreadPool
{
   private static ExecutorService s_executorService;

   static <T> Future<T> submit(Callable<T> task)
   {
      if (null == s_executorService)
      {
         s_executorService = Executors.newCachedThreadPool();
      }

      return s_executorService.submit(task);
   }

}
