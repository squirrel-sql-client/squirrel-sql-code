package net.sourceforge.squirrel_sql.fw.util.log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class SystemOutToLog extends OutputStream
{
   private static ILogger s_log = LoggerController.createLogger(SystemOutToLog.class);

   StringBuffer _line = new StringBuffer();
   private PrintStream _originalOut;
   private boolean _inWrite;

   public SystemOutToLog(PrintStream originalOut)
   {
      _originalOut = originalOut;
   }

   @Override
   public void write(int b) throws IOException
   {
      if(_inWrite)
      {
         return;
      }

      try
      {
         _inWrite = true;
         
         char c = (char) b;

         if('\n' == c)
         {
            _originalOut.println(_line);
            s_log.info(_line);
            _line.setLength(0);
         }
         else
         {
            _line.append(c);
         }
      }
      finally
      {
         _inWrite = false;
      }
   }
}
