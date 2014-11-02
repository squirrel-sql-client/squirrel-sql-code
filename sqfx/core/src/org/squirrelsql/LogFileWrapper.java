package org.squirrelsql;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

   public static List<LogFileWrapper> wrap(File[] logFiles)
   {
      ArrayList<LogFileWrapper> ret = new ArrayList<>();

      for (File logFile : logFiles)
      {
         ret.add(new LogFileWrapper(logFile));
      }

      return ret;
   }
}
