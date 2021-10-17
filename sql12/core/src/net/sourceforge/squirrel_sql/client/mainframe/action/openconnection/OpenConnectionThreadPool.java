package net.sourceforge.squirrel_sql.client.mainframe.action.openconnection;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class OpenConnectionThreadPool
{
   private static ExecutorService s_executorService;

   static Future submit(Runnable runnable)
   {
      // A bit thread unclean. May result in duplicate ExecutorService creation.
      if (null == s_executorService)
      {
         s_executorService = Executors.newCachedThreadPool();
      }

      return s_executorService.submit(runnable);
   }
}
