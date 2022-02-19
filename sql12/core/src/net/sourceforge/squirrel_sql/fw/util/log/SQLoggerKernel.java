package net.sourceforge.squirrel_sql.fw.util.log;


public class SQLoggerKernel
{
   private static SQLogWriter s_sqLogWriterSingleton;

   public static void info(Class clazz, Object message)
   {
      printLog(clazz, SQLogLevel.INFO, message);
   }

   public static void info(Class clazz, Object message, Throwable th)
   {
      s_sqLogWriterSingleton.printLog(clazz, SQLogLevel.INFO, message, th);
   }

   public static void warn(Class clazz, Object message)
   {
      printLog(clazz, SQLogLevel.WARN, message);
   }

   public static void warn(Class clazz, Object message, Throwable th)
   {
      s_sqLogWriterSingleton.printLog(clazz, SQLogLevel.WARN, message, th);
   }

   public static void error(Class clazz, Object message)
   {
      printLog(clazz, SQLogLevel.ERROR, message);
   }

   public static void error(Class clazz, Object message, Throwable th)
   {
      s_sqLogWriterSingleton.printLog(clazz, SQLogLevel.ERROR, message, th);
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
      // TODO
      return false;
   }

   public static boolean isInfoEnabled()
   {
      // TODO
      return true;
   }

   private static void printLog(Class clazz, SQLogLevel level, Object message)
   {
      s_sqLogWriterSingleton.printLog(clazz, level, message, null);
   }

   public static void init()
   {
      s_sqLogWriterSingleton = new SQLogWriter();
   }
}
