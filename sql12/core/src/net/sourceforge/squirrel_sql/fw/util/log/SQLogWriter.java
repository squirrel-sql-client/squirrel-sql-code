package net.sourceforge.squirrel_sql.fw.util.log;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SQLogWriter
{
   private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
   private SQLogLevel _minimumLogLevel;
   private File _executionLogFile;
   private ExecutorService _executorService;

   private long logCount;

   public SQLogWriter()
   {
      _minimumLogLevel = SQLogLevel.getMatchingLevel(ApplicationArguments.getInstance().getLogLevel());
      _executionLogFile = new ApplicationFiles().getExecutionLogFile();
      _executorService = Executors.newSingleThreadExecutor();
   }

   public void printLog(Class clazz, SQLogLevel level, Object message, Throwable th)
   {
      if(false == _minimumLogLevel.higherOrEqual(level))
      {
         return;
      }

      _executorService.submit(() -> writeLogEntry(clazz, level, message, th));
   }

   private void writeLogEntry(Class clazz, SQLogLevel level, Object message, Throwable th)
   {
      String logEntry = String.format("%s [%s] %s  %s  - %s\n%s",
                                            SIMPLE_DATE_FORMAT.format(new Date()),
                                            Thread.currentThread().getName(),
                                            level.name(),
                                            getClassName(clazz),
                                            getMessage(message),
                                            Utilities.getStackTrace(th));

      // Just to see all logs are written by the same thread.
      // logEntry = Thread.currentThread().getId() + "  " + Thread.currentThread().getName() + " #### " + logEntry;


      try
      {
         ++logCount;
         if(0 == logCount % 100 && 1048576L <= _executionLogFile.length())
         {
            Files.move(_executionLogFile.toPath(), Paths.get(_executionLogFile.getAbsolutePath() + "_old"), StandardCopyOption.REPLACE_EXISTING);
         }

         Files.write(
               _executionLogFile.toPath(),
               logEntry.getBytes(),
               StandardOpenOption.APPEND,
               StandardOpenOption.CREATE);
      }
      catch (Exception e)
      {
         System.out.println("Error writing log message: "  + logEntry);
         // About the only place where this is Ok.
         e.printStackTrace();
      }
   }

   private String getClassName(Class clazz)
   {
      if(null == clazz)
      {
         return "<classIsNull>";
      }
      return clazz.getName();
   }

   private String getMessage(Object message)
   {
      String ret = "<messageIsNull>";
      if(null != message)
      {
         try
         {
            ret = message.toString();
         }
         catch (Throwable t)
         {
            ret = "<toString throws " + t + ">";
         }
      }

      return ret;
   }
}
