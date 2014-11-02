package org.squirrelsql;

import java.io.File;

public class LogFileWrapper
{
   private File _logFile;

   public LogFileWrapper(File logFile)
   {
      _logFile = logFile;
   }

   @Override
   public String toString()
   {
      return _logFile.getName();
   }

   public File getLogFile()
   {
      return _logFile;
   }

   public static LogFileWrapper[] wrap(File[] logFiles)
   {
      LogFileWrapper[] ret = new LogFileWrapper[logFiles.length];

      for (int i = 0; i < logFiles.length; i++)
      {
         File logFile = logFiles[i];

         ret[i] = new LogFileWrapper(logFile);
      }

      return ret;
   }
}
