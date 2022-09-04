package net.sourceforge.squirrel_sql.fw.util.log;


public class SQLoggerKernel
{
   private static SQLLogCounts s_sqlLogCounts = new SQLLogCounts();

   private static SQLogWriter s_sqLogWriterSingleton;

   public static void info(Class clazz, Object message)
   {
      printLog(clazz, SQLogLevel.INFO, message);
      s_sqlLogCounts.incInfo();
   }

   public static void info(Class clazz, Object message, Throwable th)
   {
      s_sqLogWriterSingleton.printLog(clazz, SQLogLevel.INFO, message, th);
      s_sqlLogCounts.incInfo();
   }

   public static void warn(Class clazz, Object message)
   {
      printLog(clazz, SQLogLevel.WARN, message);
      s_sqlLogCounts.incWarning();
   }

   public static void warn(Class clazz, Object message, Throwable th)
   {
      s_sqLogWriterSingleton.printLog(clazz, SQLogLevel.WARN, message, th);
      s_sqlLogCounts.incWarning();
   }

   public static void error(Class clazz, Object message)
   {
      printLog(clazz, SQLogLevel.ERROR, message);
      s_sqlLogCounts.incError();
   }

   public static void error(Class clazz, Object message, Throwable th)
   {
      s_sqLogWriterSingleton.printLog(clazz, SQLogLevel.ERROR, message, th);
      s_sqlLogCounts.incError();
   }

   public static void debug(Class clazz, Object message)
   {
      printLog(clazz, SQLogLevel.DEBUG, message);
   }

   public static void debug(Class clazz, Object message, Throwable th)
   {
      s_sqLogWriterSingleton.printLog(clazz, SQLogLevel.DEBUG, message, th);
   }

   public static boolean isDebugEnabled()
   {
      return s_sqLogWriterSingleton.isDebugEnabled();
   }

   public static boolean isInfoEnabled()
   {
      return s_sqLogWriterSingleton.isInfoEnabled();
   }

   private static void printLog(Class clazz, SQLogLevel level, Object message)
   {
      s_sqLogWriterSingleton.printLog(clazz, level, message, null);
   }

   public static void init()
   {
      s_sqLogWriterSingleton = new SQLogWriter();
   }

   public static SQLLogCounts getLogCounts()
   {
      return s_sqlLogCounts;
   }
}
