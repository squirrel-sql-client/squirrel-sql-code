package net.sourceforge.squirrel_sql.fw.timeoutproxy;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class TimeOutUtil
{
   private static final ILogger s_log = LoggerController.createLogger(TimeOutUtil.class);

   public static final int STD_INVOKE_WITH_TIMEOUT_MILLIS = 300;


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

   /**
    * If a user defined meta data loading timeout exists this is used
    * else {@link #STD_INVOKE_WITH_TIMEOUT_MILLIS} is used.
    */
   public static void invokeWithTimeout(TimeOutableInvoker timeoutableInvoker)
   {
      boolean usedMetaDataLoadingTimeout = false;
      try
      {
         final Future future = StaticTimeOutThreadPool.submit((Callable) () -> runnableToCallableWrapperFunction(timeoutableInvoker));
         final long metaDataLoadingTimeOutOfActiveSession = getMetaDataLoadingTimeOutOfActiveSession();

         if(0 == metaDataLoadingTimeOutOfActiveSession)
         {
            future.get(STD_INVOKE_WITH_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
         }
         else
         {
            future.get(metaDataLoadingTimeOutOfActiveSession, TimeUnit.MILLISECONDS);
         }
      }
      catch (TimeoutException e)
      {
         if(usedMetaDataLoadingTimeout)
         {
            final String msg = "Timeout as configured in menu File --> New Session Properties --> tab SQL --> section \"Meta data loading\" occured.";
            s_log.error(msg);
            throw new RuntimeException(msg, e);
         }
         else
         {
            throw Utilities.wrapRuntime(e);
         }
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   /**
    * If a user defined meta data loading timeout exists this is used
    * else {@link #STD_INVOKE_WITH_TIMEOUT_MILLIS} is used.
    */
   public static <T> T callWithTimeout(TimeOutableCaller<T> timeoutableCaller)
   {
      return callWithTimeout(timeoutableCaller, STD_INVOKE_WITH_TIMEOUT_MILLIS);
   }

   public static <T> T callWithTimeout(TimeOutableCaller<T> timeoutableCaller, int timeOutMillis)
   {
      boolean usedMetaDataLoadingTimeout = false;
      try
      {
         final Future<T> future = StaticTimeOutThreadPool.submit(() -> timeoutableCaller.call());
         final long metaDataLoadingTimeOutOfActiveSession = getMetaDataLoadingTimeOutOfActiveSession();

         if(0 == metaDataLoadingTimeOutOfActiveSession)
         {
            return future.get(timeOutMillis, TimeUnit.MILLISECONDS);
         }
         else
         {
            usedMetaDataLoadingTimeout = true;
            return future.get(metaDataLoadingTimeOutOfActiveSession, TimeUnit.MILLISECONDS);
         }
      }
      catch (TimeoutException e)
      {
         if(usedMetaDataLoadingTimeout)
         {
            final String msg = "Timeout as configured in menu File --> New Session Properties --> tab SQL --> section \"Meta data loading\" occured.";
            s_log.error(msg);
            throw new RuntimeException(msg, e);
         }
         else
         {
            throw Utilities.wrapRuntime(e);
         }
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private static Object runnableToCallableWrapperFunction(TimeOutableInvoker timeoutableInvoker)
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

   public static long getDefaultOrConfiguredTimeoutMillis()
   {
      final long metaDataLoadingTimeOutOfActiveSession = getMetaDataLoadingTimeOutOfActiveSession();

      if(0 == metaDataLoadingTimeOutOfActiveSession)
      {
         return STD_INVOKE_WITH_TIMEOUT_MILLIS;
      }

      return metaDataLoadingTimeOutOfActiveSession;
   }
}
