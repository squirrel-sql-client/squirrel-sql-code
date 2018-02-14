package net.sourceforge.squirrel_sql.client.edtwatcher;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Timer;
import java.util.TimerTask;


public class EventQueueWorkingCheck implements Runnable
{

   public static final int MAX_ACCEPTED_EDT_DELAY_TIME = 2000;

   private static ILogger s_log = LoggerController.createLogger(EventQueueWorkingCheck.class);

   private final Timer m_timer;

   public EventQueueWorkingCheck()
   {
      m_timer = new Timer(true);

      TimerTask task = new TimerTask()
      {
         @Override
         public void run()
         {
            onTimerTaskReached();
         }
      };

      m_timer.schedule(task, MAX_ACCEPTED_EDT_DELAY_TIME);
   }

   private void onTimerTaskReached()
   {
      writeLog();
   }

   @Override
   public void run()
   {
      m_timer.cancel();
   }

   public void writeLog()
   {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      try
      {
         ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
         ThreadInfo[] threadInfo = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 1000);

         pw.println("----------------------------------------------------------------------------------------------------------------");
         pw.println("-- Detected Swing-EDT event running for longer than " + MAX_ACCEPTED_EDT_DELAY_TIME + " millis. Writing Stack dump:");
         pw.println("-- STACK DUMP BEGIN");


         for( ThreadInfo info : threadInfo )
         {
            if(null == info)
            {
               continue;
            }

            pw.println("Threadname: " + info.getThreadName());
            pw.println("ThreadId: " + info.getThreadId());
            pw.println("Threadstate: " + info.getThreadState());

            for( StackTraceElement stackTraceElement : info.getStackTrace() )
            {
               pw.println("    at " +  stackTraceElement);
            }

            pw.println();
            pw.println();
         }

         pw.println("-- STACK DUMP END");
         pw.println("----------------------------------------------------------------------------------------------------------------");

      }
      finally
      {
         pw.flush();
         pw.close();
      }

      s_log.warn(sw.toString());
   }

}
