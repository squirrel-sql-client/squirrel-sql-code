package org.apache.logging.log4j;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * This class was introduced to be able to use version 5.2.2 of
 * "Apache POI - the Java API for Microsoft Documents".
 * See <a href="https://poi.apache.org/">https://poi.apache.org/</a>.
 * SQuirreL needs Apache POI to import and export MS-Excel files.
 *
 * Apache POI 5.2.2 depends on Log4j2.
 * SQuirreL does not use any Log4j libraries.
 * The part of Log4j's interface used by POI 5.2.2 when SQuirreL imports
 * and exports MS-Excel files is implemented mainly by this class.
 *
 * POI's Log4j2 calls are redirected to SQuirreL's own logging framework,
 * see  {@link #onInvoke(Method, Object[])}
 */
public class LogManager
{
   private static int log4jTraceDebugInfoCallsCount = 0;

   /**
    * This methode is called by Apache POI to receive an implementation
    * of the Log4J's Logger interface
    */
   public static Logger getLogger(final Class<?> clazz)
   {
      return (Logger) Proxy.newProxyInstance(LogManager.class.getClassLoader(),
                                             new Class[]{Logger.class},
                                             (proxy, method, args) -> onInvoke(method, args));
   }

   private static Object onInvoke(Method method, Object[] args)
   {
      if(   method.getName().toLowerCase().contains("trace")
         || method.getName().toLowerCase().contains("debug")
         || method.getName().toLowerCase().contains("info"))
      {
         ++log4jTraceDebugInfoCallsCount;
         final int maxLog4jTraceDebugInfoCallsCount = 10;

         if(log4jTraceDebugInfoCallsCount <= maxLog4jTraceDebugInfoCallsCount)
         {
            SQuirreLsDummyEmptyLog4jStub.s_log.info("SQuirreL's DUMMY/EMPTY Log4j stub was accessed: Logger." + method + "; Parameters: " + args);
         }

         if(log4jTraceDebugInfoCallsCount == maxLog4jTraceDebugInfoCallsCount)
         {
            final String msg = "SQuirreL's DUMMY/EMPTY Log4j stub was called for trace/debug/info methods " +
                               "" + maxLog4jTraceDebugInfoCallsCount + " times and will quit re-logging these methods now";
            SQuirreLsDummyEmptyLog4jStub.s_log.info(msg);
         }
      }
      else
      {
         SQuirreLsDummyEmptyLog4jStub.s_log.warn("SQuirreL's DUMMY/EMPTY Log4j stub was accessed: Logger." + method + "; Parameters: " + args);
      }


      if(LogBuilder.class.isAssignableFrom(method.getReturnType()))
      {
         return LogBuilder.NOOP;
      }
      else if("exit".equals(method.getName()) && 1 == method.getParameterTypes().length )
      {
         return args[0];
      }
      else if("void".equals(method.getReturnType().getName()))
      {
         SQuirreLsDummyEmptyLog4jStub.s_log.warn("SQuirreL's DUMMY/EMPTY Log4j stub returned null for the non void Logger method: " + method);
      }

      return null;
   }

}
