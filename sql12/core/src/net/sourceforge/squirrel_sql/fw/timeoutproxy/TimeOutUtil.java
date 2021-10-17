package net.sourceforge.squirrel_sql.fw.timeoutproxy;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TimeOutUtil
{
   private static final ILogger s_log = LoggerController.createLogger(TimeOutUtil.class);


   public static long getMetaDataLoadingTimeOutOfActiveSession()
   {
      long metaDataLoadingTimeOut = Main.getApplication().getSquirrelPreferences().getSessionProperties().getMetaDataLoadingTimeOut();

      if(   false == Main.getApplication().getSessionManager().isInCreateSession()
            && null != Main.getApplication().getSessionManager().getActiveSession()) // Happens when testing or loading Schema table for a new Alias that has not yet been saved.
      {
         metaDataLoadingTimeOut = Main.getApplication().getSessionManager().getActiveSession().getProperties().getMetaDataLoadingTimeOut();
      }
      return metaDataLoadingTimeOut;
   }

   public static void invokeWithTimeout(TimeOutableInvoker timeoutableInvoker)
   {
      try
      {
         final Future future = StaticTimeOutThreadPool.submit((Callable) () -> onSubmit(timeoutableInvoker));
         future.get(getMetaDataLoadingTimeOutOfActiveSession(), TimeUnit.MILLISECONDS);
      }
      catch (TimeoutException e)
      {
         final String msg = "Timeout as configured in menu File --> New Session Properties --> tab SQL --> section \"Meta data loading\" occured.";
         s_log.error(msg);
         throw new RuntimeException(msg, e);
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   public static <T> T callWithTimeout(TimeOutableCaller<T> timeoutableCaller)
   {
      try
      {
         final Future<T> future = StaticTimeOutThreadPool.submit(() -> timeoutableCaller.call());
         return future.get(getMetaDataLoadingTimeOutOfActiveSession(), TimeUnit.MILLISECONDS);
      }
      catch (TimeoutException e)
      {
         final String msg = "Timeout as configured in menu File --> New Session Properties --> tab SQL --> section \"Meta data loading\" occured.";
         s_log.error(msg);
         throw new RuntimeException(msg, e);
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private static Object onSubmit(TimeOutableInvoker timeoutableInvoker)
   {
      try
      {
         timeoutableInvoker.invoke();
         return null;
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
